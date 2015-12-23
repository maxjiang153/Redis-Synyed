package com.wmz7year.synyed.worker;

import static com.wmz7year.synyed.constant.RedisCommandSymbol.*;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wmz7year.synyed.constant.RedisCommand;
import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.exception.RedisCommandRejectedException;
import com.wmz7year.synyed.exception.RedisProtocolException;
import com.wmz7year.synyed.module.RedisCommandFilterManager;
import com.wmz7year.synyed.net.RedisConnection;
import com.wmz7year.synyed.net.RedisResponseListener;
import com.wmz7year.synyed.net.spi.DefaultRedisConnection;
import com.wmz7year.synyed.packet.redis.RedisPacket;
import com.wmz7year.synyed.packet.redis.RedisSimpleStringPacket;
import com.wmz7year.synyed.packet.redis.command.RedisPacketCommandParser;

/**
 * Redis同步管道对象<br>
 * 该管道为单个源Redis到单个目标Redis的通道<br>
 * 
 * @Title: ProtocolSyncWorker.java
 * @Package com.wmz7year.synyed.worker
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 上午11:38:32
 * @version V1.0
 */
public class ProtocolSyncWorker implements RedisResponseListener {
	private static final Logger logger = LoggerFactory.getLogger(ProtocolSyncWorker.class);

	/**
	 * Redis命令过滤处理模块
	 */
	@Autowired
	private RedisCommandFilterManager redisCommandFilterManager;

	/**
	 * redis源服务器
	 */
	private RedisServer srcServer;

	/**
	 * redis目标服务器
	 */
	private RedisServer descServer;

	/**
	 * 源服务器连接
	 */
	private RedisConnection srcConnection;
	/**
	 * 目标服务器连接
	 */
	private RedisConnection descConnection;

	/**
	 * redis数据包命令解析器
	 */
	private RedisPacketCommandParser packetCommandParser = new RedisPacketCommandParser();

	/**
	 * 设置管道同步源服务器信息的方法<br>
	 * 
	 * @param srcServer
	 *            源服务器信息
	 */
	public void setSrcRedis(RedisServer srcServer) {
		if (srcServer == null) {
			throw new NullPointerException();
		}
		this.srcServer = srcServer;
	}

	/**
	 * 设置管道同步目标服务器信息的方法<br>
	 * 
	 * @param descServer
	 *            目标服务器信息
	 */
	public void setDescRedis(RedisServer descServer) {
		if (descServer == null) {
			throw new NullPointerException();
		}
		this.descServer = descServer;
	}

	/**
	 * 启动同步管道的方法
	 */
	public void start() {
		logger.info("准备启动同步管道 源Redis：" + srcServer + "  目标Redis：" + descServer);

		// 创建源redis与目标redis的连接
		try {
			createConnections();
		} catch (RedisProtocolException e) {
			logger.error("无法创建Redis连接 " + srcServer, e);
			return;
		}

		// 开始同步Redis源服务器的方法
		startSyncSourceRedisServer();

		logger.info("启动同步管道成功 源Redis：" + srcServer + "  目标Redis：" + descServer);
	}

	/**
	 * 开始同步Redis源服务器的方法<br>
	 * 发送SYNC命令到源服务器
	 */
	private void startSyncSourceRedisServer() {
		// TODO PSYNC
		try {
			srcConnection.sendCommand(String.valueOf(RedisCommand.SYNC), this);
		} catch (RedisProtocolException e) {
			logger.error("发送同步命令失败", e);
		}
	}

	/**
	 * 创建源服务器与目标服务器连接的方法
	 * 
	 * @throws RedisProtocolException
	 *             当创建连接出现问题时抛出该异常
	 */
	private void createConnections() throws RedisProtocolException {
		srcConnection = new DefaultRedisConnection();
		descConnection = new DefaultRedisConnection();

		// 分别连接到服务器
		try {
			srcConnection.connect(srcServer.getHost(), srcServer.getPort(), srcServer.getAuthPassword(), 5000);
		} catch (RedisProtocolException e) {
			throw e;
		}

		try {
			descConnection.connect(descServer.getHost(), descServer.getPort(), descServer.getAuthPassword(), 5000);
		} catch (RedisProtocolException e) {
			throw e;
		}
	}

	/**
	 * 停止同步任务的方法
	 */
	public void shutdown() {
		try {
			if (srcConnection != null) {
				srcConnection.close();
			}
		} catch (IOException e) {
			logger.error("关闭源Redis连接出现问题", e);
		}
		try {
			if (descConnection != null) {
				descConnection.close();
			}
		} catch (IOException e) {
			logger.error("关闭目标Redis连接出现问题", e);
		}
	}

	/*
	 * @see
	 * com.wmz7year.synyed.net.RedisResponseListener#receive(com.wmz7year.synyed
	 * .packet.redis.RedisPacket)
	 */
	@Override
	public void receive(RedisPacket redisPacket) {
		// 解析出命令列表
		List<String> commands = packetCommandParser.parseRedisPacket(redisPacket);
		for (String command : commands) {
			// 处理解析出的命令
			processCommand(command);
		}
	}

	/**
	 * 处理需要执行的同步命令的方法<br>
	 * 首先经过拦截器进行命令过滤操作<br>
	 * 其次命令持久化到磁盘作为记录备份<br>
	 * 最后发送到目标服务器并且校验响应结果
	 * 
	 * @param command
	 *            需要处理的命令
	 */
	private void processCommand(String command) {
		if (logger.isDebugEnabled()) {
			logger.debug("开始处理同步命令：" + command);
		}

		try {
			// 在命令发送前进行过滤操作
			redisCommandFilterManager.beforeSendCommand(command, srcServer, descServer);

			// 发送命令到目标服务器
			RedisPacket responsePacket = sendCommandToTargetServer(command);
			// 处理响应
			boolean result = processResponsePacket(responsePacket);

			// 在命令发送后进行过滤操作
			redisCommandFilterManager.afterSendCommand(command, result, srcServer, descServer);
		} catch (RedisCommandRejectedException e) {
			logger.info("命令：" + command + " 被拦截器拦截");
		} catch (RedisProtocolException e) {
			logger.error("发送命令到目标服务器出现问题", e);
		}
	}

	/**
	 * 发送同步命令到目标服务器的方法
	 * 
	 * @param command
	 *            需要发送的同步命令
	 * @return 发送命令的响应结果
	 * @throws RedisProtocolException
	 *             当发送命令出现问题则抛出该异常
	 */
	private RedisPacket sendCommandToTargetServer(String command) throws RedisProtocolException {
		try {
			RedisPacket responsePacket = descConnection.sendCommand(command);
			if (logger.isDebugEnabled()) {
				logger.debug("同步命令:" + command + " 响应结果：" + responsePacket.toString());
			}
			return responsePacket;
		} catch (RedisProtocolException e) {
			logger.error("发送命令到目标服务器出现问题", e);
			throw e;
		}
	}

	/**
	 * 处理执行同步命令响应结果的方法
	 * 
	 * @param responsePacket
	 *            响应数据包对象
	 * @return true为命令执行成功 false为命令执行失败
	 */
	private boolean processResponsePacket(RedisPacket responsePacket) {
		if (responsePacket instanceof RedisSimpleStringPacket) {
			RedisSimpleStringPacket simpleStringPacket = (RedisSimpleStringPacket) responsePacket;
			if (OK.equals(simpleStringPacket.getCommand())) {
				return true;
			}
		} else {
			System.err.println("responsePacket:" + responsePacket);
		}
		return false;
	}
}
