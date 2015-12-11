package com.wmz7year.synyed.net.spi;

import static com.wmz7year.synyed.constant.RedisProtocolConstant.REDIS_PROTOCOL_PARSER;
import static com.wmz7year.synyed.constant.RedisCommandSymbol.AUTH;
import static com.wmz7year.synyed.constant.RedisCommandSymbol.ERR;
import static com.wmz7year.synyed.constant.RedisCommandSymbol.OK;
import static com.wmz7year.synyed.constant.RedisCommandSymbol.PING;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmz7year.synyed.exception.RedisProtocolException;
import com.wmz7year.synyed.net.RedisResponseListener;
import com.wmz7year.synyed.net.RedisConnection;
import com.wmz7year.synyed.net.proroc.RedisProtocolCodecFactory;
import com.wmz7year.synyed.net.proroc.RedisProtocolParser;
import com.wmz7year.synyed.packet.redis.RedisErrorPacket;
import com.wmz7year.synyed.packet.redis.RedisPacket;

/**
 * 默认的Redis连接实现类<br>
 * 使用mina框架与Redis服务器保持连接
 * 
 * @Title: DefaultRedisConnection.java
 * @Package com.wmz7year.synyed.net.spi
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午3:33:22
 * @version V1.0
 */
public class DefaultRedisConnection extends IoHandlerAdapter implements RedisConnection {
	private static final Logger logger = LoggerFactory.getLogger(DefaultRedisConnection.class);

	/**
	 * 与redis服务器之间的socket对象
	 */
	private SocketConnector connector;

	/**
	 * 与redis服务器的会话对象
	 */
	private IoSession ioSession;

	/**
	 * 是否连接的表识位
	 */
	private AtomicBoolean isConnected = new AtomicBoolean(false);

	/**
	 * 服务器地址
	 */
	private String address;
	/**
	 * 服务器端口
	 */
	private int port;
	/**
	 * 登录redis的密码
	 */
	private String password;
	/**
	 * 连接超时时间
	 */
	private long connectionTimeOut;

	/**
	 * Redis命令执行锁<br>
	 * 该锁的目的是保证同一时刻只有一个Redis命令在执行
	 */
	private Lock lock = new ReentrantLock();

	/**
	 * redis包响应对象<br>
	 */
	private RedisPacket response;

	/**
	 * redis响应监听器
	 */
	private RedisResponseListener listener;

	public DefaultRedisConnection() {

	}

	/*
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(org.apache.
	 * mina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		this.ioSession = session;
		this.isConnected.set(true);
		// 向会话中绑定redis协议解析器
		this.ioSession.setAttribute(REDIS_PROTOCOL_PARSER, new RedisProtocolParser());

		logger.info("连接到Redis服务器" + address + "成功");
	}

	/*
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#sessionClosed(org.apache.
	 * mina.core.session.IoSession)
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		isConnected.set(false);
		super.sessionClosed(session);
	}

	/*
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#sessionIdle(org.apache.mina
	 * .core.session.IoSession, org.apache.mina.core.session.IdleStatus)
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// 发送ping命令
		sendCommand(PING);
	}

	/*
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(org.apache.
	 * mina.core.session.IoSession, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("Redis连接：" + this.address + " 端口：" + this.port + "出现异常", cause);
	}

	/*
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.
	 * mina.core.session.IoSession, java.lang.Object)
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (!(message instanceof RedisPacket)) {
			logger.error("收到错误解析的数据包：" + message);
			return;
		}
		RedisPacket redisPacket = (RedisPacket) message;
		if (listener != null) {
			listener.receive(redisPacket);
		} else {
			this.response = redisPacket;
		}
	}

	/*
	 * @see com.wmz7year.synyed.net.RedisConnection#connect(java.lang.String,
	 * int, long)
	 */
	@Override
	public boolean connect(String address, int port, long timeout) throws RedisProtocolException {
		return connect(address, port, null, timeout);
	}

	/*
	 * @see com.wmz7year.synyed.net.RedisConnection#connect(java.lang.String,
	 * int, java.lang.String, long)
	 */
	@Override
	public boolean connect(String address, int port, String password, long timeout) throws RedisProtocolException {
		logger.info("准备连接到Redis服务器:" + address + " 端口：" + port + "  是否使用密码验证：" + (password != null));
		this.address = address;
		this.port = port;
		this.password = password;
		this.connectionTimeOut = timeout;
		// 执行连接操作
		connect();

		// 等待mina进行连接处理
		long start = System.currentTimeMillis();
		while (ioSession == null) {
			long current = System.currentTimeMillis();
			if ((current - start) > this.connectionTimeOut) {
				throw new RedisProtocolException("创建Redis连接超时");
			} else {
				// 等待100毫秒
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}

		// 如果密码不为空则执行登录操作
		if (password != null) {
			RedisPacket response = sendCommand(AUTH, this.password);
			if (response.getCommand().equals(OK)) {
				return true;
			} else if (response.getCommand().equals(ERR)) {
				RedisErrorPacket errorResponse = (RedisErrorPacket) response;
				throw new RedisProtocolException(errorResponse.getErrorMessage());
			} else {
				throw new RedisProtocolException("发生未知错误");
			}
		}
		return true;
	}

	/**
	 * 执行连接操作的方法
	 * 
	 * @throws RedisProtocolException
	 *             当连接出现问题时抛出该异常
	 */
	private void connect() throws RedisProtocolException {
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(connectionTimeOut);
		connector.getFilterChain().addFirst("redis-protocol", new ProtocolCodecFilter(new RedisProtocolCodecFactory()));
		connector.setHandler(this);
		connector.connect(new InetSocketAddress(address, port));
	}

	/*
	 * @see com.wmz7year.synyed.net.RedisConnection#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return isConnected.get();
	}

	/*
	 * @see
	 * com.wmz7year.synyed.net.RedisConnection#sendCommand(java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	public RedisPacket sendCommand(String command, String... args) throws RedisProtocolException {
		if (!isConnected()) {
			throw new RedisProtocolException("未连接到服务器");
		}
		if (listener != null) {
			throw new RedisProtocolException("当前已经设置Redis响应数据包收集器 无法执行Redis命令");
		}
		lock.lock();
		try {
			// 清空上次响应
			response = null;
			// 发送命令到redis服务器
			this.ioSession.write(buildRequestCommand(command, args));
			// 等待响应
			long start = System.currentTimeMillis();
			while (response == null) {
				long current = System.currentTimeMillis();
				if ((current - start) > this.connectionTimeOut) {
					if (response != null) {
						return response;
					}
					throw new RedisProtocolException("redis响应超时");
				} else {
					// 等待100毫秒
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}
			return response;
		} finally {
			lock.unlock();
		}
	}

	/*
	 * @see
	 * com.wmz7year.synyed.net.RedisConnection#sendCommand(java.lang.String,
	 * com.wmz7year.synyed.net.RedisCommandResponseListener, java.lang.String[])
	 */
	@Override
	public void sendCommand(String command, RedisResponseListener listener, String... params)
			throws RedisProtocolException {
		if (!isConnected()) {
			throw new RedisProtocolException("未连接到服务器");
		}

		this.listener = listener;

		// 清空上次响应
		response = null;
		// 发送命令到redis服务器
		this.ioSession.write(buildRequestCommand(command, params));
	}

	/*
	 * @see com.wmz7year.synyed.net.RedisConnection#cancalResponseListener(com.
	 * wmz7year.synyed.net.RedisCommandResponseListener)
	 */
	@Override
	public void cancalResponseListener(RedisResponseListener listener) {
		if (this.listener == listener) {
			this.listener = null;
		} else {
			logger.warn("不匹配的监听器对象：" + listener);
		}
	}

	/**
	 * 把命令与附带的参数合并为一个完整请求命令的方法
	 * 
	 * @param command
	 *            redis命令
	 * @param args
	 *            请求参数列表
	 * @return 完整的请求命令
	 */
	private String buildRequestCommand(String command, String... args) {
		StringBuilder sb = new StringBuilder();
		sb.append(command).append(' ');
		for (String arg : args) {
			sb.append(arg).append(' ');
		}
		String result = sb.substring(0, sb.length() - 1);
		return result;
	}

	/*
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if (!isConnected()) {
			throw new IOException("未连接到Redis服务器  无法关闭连接");
		}
		logger.info("关闭Redis连接");
		ioSession.close(false);
	}

}
