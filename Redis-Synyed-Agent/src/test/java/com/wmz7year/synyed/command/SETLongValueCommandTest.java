package com.wmz7year.synyed.command;

import static com.wmz7year.synyed.net.spi.RedisConnectionFactory.*;
import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmz7year.synyed.entity.RedisCommand;
import com.wmz7year.synyed.net.RedisConnection;
import com.wmz7year.synyed.packet.redis.RedisBulkStringPacket;
import com.wmz7year.synyed.packet.redis.RedisSimpleStringPacket;

/**
 * set 长value测试
 * @author jiangwei (ydswcy513@gmail.com)
 * @since 2015年12月29日 下午1:06:21
 * @version V1.0
 */
public class SETLongValueCommandTest extends BasicCommandTest {
	private static Logger logger = LoggerFactory.getLogger(SETLongValueCommandTest.class);
	/**
	 * 测试key
	 */
	private String key = "testSetLongKey";
	/**
	 * 测试value
	 */
	private String value = "a";

	/**
	 * 同步后的结果
	 */
	private String resultValue;

	public SETLongValueCommandTest() {
		for (int i = 0; i < 15; i++) {
			value += value;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wmz7year.synyed.command.BasicCommandTest#executeCommandOnSrcServer()
	 */
	@Override
	public void executeCommandOnSrcServer() throws Exception {
		logger.info("准备测试同步命令 SET 长Value情况  value长度：" + this.value.length());
		RedisConnection redisConnection = createDefaultRedisConnection(srcServer, 5000);
		RedisCommand redisCommand = new RedisCommand("SET");
		redisCommand.addValue(key);
		redisCommand.addValue(value);
		RedisSimpleStringPacket result = (RedisSimpleStringPacket) redisConnection.sendCommand(redisCommand);
		assertEquals(result.getCommand(), "OK");
		redisConnection.close();
	}

	/*
	 * @see com.wmz7year.synyed.command.BasicCommandTest#
	 * executeGetSynResultOnDescServer()
	 */
	@Override
	public void executeGetSynResultOnDescServer() throws Exception {
		RedisConnection redisConnection = createDefaultRedisConnection(descServer, 5000);
		RedisCommand redisCommand = new RedisCommand("GET");
		redisCommand.addValue(key);
		RedisBulkStringPacket result = (RedisBulkStringPacket) redisConnection.sendCommand(redisCommand);
		this.resultValue = new String(result.getData());
		redisConnection.close();
	}

	/*
	 * @see com.wmz7year.synyed.command.BasicCommandTest#checkSynResult()
	 */
	@Override
	public void checkSynResult() throws Exception {
		assertEquals(value, resultValue);
	}

}
