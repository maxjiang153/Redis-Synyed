package com.wmz7year.synyed.parser;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.apache.commons.io.HexDump;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmz7year.synyed.Booter;
import com.wmz7year.synyed.parser.entry.RedisDB;

/**
 * redis rdb0006版本的解析器测试
 * 
 * @Title: RDBParserImpl0006Test.java
 * @Package com.wmz7year.synyed.parser
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月14日 下午2:37:44
 * @version V1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Booter.class)
public class RDBParserImpl0006Test {
	private static final Logger logger = LoggerFactory.getLogger(RDBParserImpl0006Test.class);
	private static final byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -2, 0, 0, 1, 97, 1, 98, 0, 1,
			99, 1, 100, 0, 1, 98, 1, 99, -1, 16, 79, 24, -53, 114, -99, 102, 122 };

	/**
	 * 显示RDB字节内容以及格式化
	 */
	@Test
	public void showRDBDumpData() throws Exception {
		HexDump.dump(rdbData, 0, System.out, 0);
	}

	/**
	 * 测试解析RDB文件内容
	 */
	@Test
	public void testParseRDBFile() throws Exception {
		byte[] rdbHeader = new byte[9];
		System.arraycopy(rdbData, 0, rdbHeader, 0, 9);
		RDBParser rdbParser = RDBParserFactory.createRDBParser(rdbHeader);
		rdbParser.parse(rdbData);

		Collection<RedisDB> redisDBs = rdbParser.getRedisDBs();
		for (RedisDB redisDB : redisDBs) {
			List<String> commands = redisDB.getCommands();
			for (String command : commands) {
				logger.info("redis command rdb:" + redisDB.getNum() + " command：" + command);
			}
		}
	}

	/**
	 * 测试正常校验crc64值
	 */
	@Test
	public void testCheckRDBCRCSum() throws Exception {
		byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -1, -36, -77, 67, -16, 90, -36, -14, 86 };
		byte[] rdbHeader = new byte[9];
		System.arraycopy(rdbData, 0, rdbHeader, 0, 9);
		RDBParser rdbParser = RDBParserFactory.createRDBParser(rdbHeader);
		rdbParser.parse(rdbData);
	}

	/**
	 * 测试正常校验crc64值
	 */
	@Test
	public void testCheckRDBCRCSumOnerror() throws Exception {
		byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -1, -36, -77, 67, -16, 90, -36, -14, 87 };
		byte[] rdbHeader = new byte[9];
		System.arraycopy(rdbData, 0, rdbHeader, 0, 9);
		try {
			RDBParser rdbParser = RDBParserFactory.createRDBParser(rdbHeader);
			rdbParser.parse(rdbData);
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	/**
	 * 测试解析各种类型元素的ziplist
	 */
	@Test
	public void testZipListObject() throws Exception {
		byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -2, 0, 10, 4, 116, 101, 115, 116, -61, 64,
				107, 64, 119, 4, 119, 0, 0, 0, 116, 32, 3, 7, 9, 0, 0, 58, 97, 97, 115, 100, 32, 2, 31, 113, 119, 100,
				99, 122, 120, 103, 100, 115, 103, 115, 114, 105, 103, 106, 97, 101, 121, 110, 54, 104, 102, 55, 98, 121,
				97, 98, 99, 101, 100, 102, 103, 20, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116,
				117, 118, 119, 120, 121, 122, 60, 18, -32, 9, 59, 28, 20, -48, -46, 2, -106, 73, 6, -48, 21, -51, 91, 7,
				6, -16, -121, -42, 18, 5, -64, 57, 48, 4, -2, 123, 3, -3, 2, -14, -1, -1, -23, 112, 17, 61, -79, 80,
				-10, 89 };
		byte[] rdbHeader = new byte[9];
		System.arraycopy(rdbData, 0, rdbHeader, 0, 9);
		RDBParser rdbParser = RDBParserFactory.createRDBParser(rdbHeader);
		rdbParser.parse(rdbData);
		RedisDB redisDB = rdbParser.getRedisDBs().iterator().next();
		List<String> commands = redisDB.getCommands();
		logger.info(commands.get(0));
	}
}
