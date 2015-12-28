package com.wmz7year.synyed.net.spi;

import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.exception.RedisProtocolException;
import com.wmz7year.synyed.net.RedisConnection;

/**
 * Redis连接创建工厂类<br>
 * 通过指定的{@code com.wmz7year.synyed.entity.RedisServer}<br>
 * Redis服务器信息创建对应连接对象的方法
 * 
 * @author jiangwei (ydswcy513@gmail.com)
 * @since 2015年12月28日 下午3:40:48
 * @version V1.0
 */
public class RedisConnectionFactory {

	private RedisConnectionFactory() {

	}

	/**
	 * 创建默认实现类Redis连接对象的方法<br>
	 * 
	 * 
	 * @param redisServer
	 *            Redis服务器信息
	 * @param timeOut
	 *            连接超时实际
	 * @return Redis连接对象
	 * @throws RedisProtocolException
	 *             当创建连接失败时抛出该异常
	 */
	public static RedisConnection createDefaultRedisConnection(RedisServer redisServer, int timeOut)
			throws RedisProtocolException {
		if (redisServer == null) {
			throw new NullPointerException();
		}
		try {
			RedisConnection connection = new DefaultRedisConnection();
			connection.connect(redisServer.getHost(), redisServer.getPort(), redisServer.getAuthPassword(), timeOut);
			return connection;
		} catch (RedisProtocolException e) {
			throw e;
		}
	}
}
