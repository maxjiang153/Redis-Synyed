package com.wmz7year.synyed.net;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmz7year.synyed.Booter;
import com.wmz7year.synyed.constant.RedisCommandSymbol;
import com.wmz7year.synyed.entity.RedisCommand;
import com.wmz7year.synyed.net.spi.DefaultRedisConnection;
import com.wmz7year.synyed.packet.redis.RedisPacket;

/**
 * redis连接相关的测试
 * 
 * @Title: RedisConnectionTest.java
 * @Package com.wmz7year.synyed.net
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午3:39:45
 * @version V1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Booter.class)
public class RedisConnectionTest {

	/**
	 * 源服务器地址
	 */
	@Value("${protocol.src.host}")
	private String srcHost;

	/**
	 * 源服务器端口
	 */
	@Value("${protocol.src.port}")
	private int srcPort;

	/**
	 * 源服务器验证密码
	 */
	@Value("${protocol.src.auth}")
	private String srcAuth;

	/**
	 * 测试连接到redis 使用密码
	 */
	@Test
	public void testConnectUsingPasswordRedis() throws Exception {
		RedisConnection connection = new DefaultRedisConnection();
		connection.connect(srcHost, srcPort, srcAuth, 5000);
		connection.close();
		assertTrue(true);
	}

	/**
	 * 测试发送ping命令到redis并且拿到响应
	 */
	@Test
	public void testSendCommandToRedis() throws Exception {
		RedisConnection connection = new DefaultRedisConnection();
		connection.connect(srcHost, srcPort, srcAuth, 5000);
		RedisPacket response = connection.sendCommand(new RedisCommand(RedisCommandSymbol.PING));
		assertEquals(response.getCommand(), "PONG");
		connection.close();
	}

	/**
	 * 测试发送命令使用redis连接监听器接受响应
	 */
	@Test
	public void testSendCommandToRedisWithResponseListener() throws Exception {
		RedisResponseListener listener = new RedisResponseListener() {

			/*
			 * @see com.wmz7year.synyed.net.RedisResponseListener#receive(com.
			 * wmz7year.synyed.packet.redis.RedisPacket)
			 */
			@Override
			public void receive(RedisPacket response) {
				assertEquals(response.getCommand(), "PONG");
			}
		};
		RedisConnection connection = new DefaultRedisConnection();
		connection.connect(srcHost, srcPort, srcAuth, 5000);
		connection.sendCommand(new RedisCommand(RedisCommandSymbol.PING), listener);
		connection.close();
	}
}
