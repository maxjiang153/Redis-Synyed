package com.wmz7year.synyed.job;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.exception.RedisProtocolException;
import com.wmz7year.synyed.net.RedisConnection;
import com.wmz7year.synyed.net.RedisResponseListener;
import com.wmz7year.synyed.net.spi.DefaultRedisConnection;
import com.wmz7year.synyed.packet.redis.RedisDataBaseTransferPacket;
import com.wmz7year.synyed.packet.redis.RedisPacket;
import com.wmz7year.synyed.parser.entry.RedisDB;

/**
 * 同步任务类<br>
 * 负责执行具体的同步任务操作
 * 
 * @Title: SynJob.java
 * @Package com.wmz7year.synyed.job
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 上午9:53:15
 * @version V1.0
 */
public class SynJob implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SynJob.class);
	/**
	 * 源服务器信息
	 */
	private RedisServer srcServer;

	/**
	 * 目标服务器信息
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

	public SynJob(RedisServer srcServer, RedisServer descServer) {
		this.srcServer = srcServer;
		this.descServer = descServer;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		srcConnection = new DefaultRedisConnection();
		descConnection = new DefaultRedisConnection();

		// 分别连接到服务器
		try {
			srcConnection.connect(srcServer.getHost(), srcServer.getPort(), srcServer.getAuthPassword(), 5000);
		} catch (RedisProtocolException e) {
			logger.error("无法创建Redis连接 " + srcServer, e);
			return;
		}

		try {
			descConnection.connect(descServer.getHost(), descServer.getPort(), descServer.getAuthPassword(), 5000);
		} catch (RedisProtocolException e) {
			logger.error("无法创建Redis连接" + descServer, e);
			return;
		}

		// 同步源服务器
		try {
			srcConnection.sendCommand("SYNC", new RedisResponseListener() {

				/*
				 * @see
				 * com.wmz7year.synyed.net.RedisResponseListener#receive(com.
				 * wmz7year.synyed.packet.redis.RedisPacket)
				 */
				@Override
				public void receive(RedisPacket redisPacket) {
					if (redisPacket instanceof RedisDataBaseTransferPacket) {
						RedisDataBaseTransferPacket packet = (RedisDataBaseTransferPacket) redisPacket;
						Collection<RedisDB> redisDBs = packet.getRedisDbs();
						for (RedisDB redisDB : redisDBs) {
							List<String> commands = redisDB.getCommands();
							for (String command : commands) {
								try {
									logger.info("同步命令:" + command);
									descConnection.sendCommand(command);
								} catch (RedisProtocolException e) {
									logger.error("发送命令到目标服务器出现问题", e);
								}
							}
						}
					} else {
						System.out.println("redisPacket:" + redisPacket);
					}
				}
			});
			logger.info("开始执行同步任务 源服务器：" + srcServer + "  目标服务器：" + descServer);
		} catch (RedisProtocolException e) {
			logger.error("发送同步命令出现问题");
		}
	}

	/**
	 * 停止同步任务的方法
	 */
	public void shutdown() {
		try {
			srcConnection.close();
			descConnection.close();
		} catch (IOException e) {
			logger.error("关闭Redis连接出现问题", e);
		}
	}
}
