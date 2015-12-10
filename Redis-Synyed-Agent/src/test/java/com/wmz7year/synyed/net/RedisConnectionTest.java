package com.wmz7year.synyed.net;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmz7year.synyed.Booter;
import com.wmz7year.synyed.net.spi.DefaultRedisConnection;

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
	private static final Logger logger = LoggerFactory.getLogger(RedisConnectionTest.class);

	/**
	 * redis地址
	 */
	private String host = "127.0.0.1";

	/**
	 * redis端口
	 */
	private int port = 6380;

	/**
	 * 测试连接到redis
	 */
	@Test
	public void testConnectRedis() throws Exception {
		RedisConnection connection = new DefaultRedisConnection();
		connection.connect(host, port, 1000);
		connection.close();
		assertTrue(true);
	}

	/**
	 * 测试发送ping命令到redis并且拿到响应
	 */
	@Test
	public void testSendCommandToRedis() throws Exception {
		RedisConnection connection = new DefaultRedisConnection();
		connection.connect(host, port, 1000);
		Object sendCommand = connection.sendCommand("PING");
		connection.close();
		assertTrue(true);
	}
}
