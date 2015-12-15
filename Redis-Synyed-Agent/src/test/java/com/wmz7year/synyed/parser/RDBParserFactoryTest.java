package com.wmz7year.synyed.parser;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmz7year.synyed.Booter;
import com.wmz7year.synyed.exception.RedisRDBException;

/**
 * rdb文件解析器创建工厂类相关的测试
 * 
 * @Title: RDBParserFactoryTest.java
 * @Package com.wmz7year.synyed.parser
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月15日 上午11:00:55
 * @version V1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Booter.class)
public class RDBParserFactoryTest {

	/**
	 * 测试rdb头长度错误
	 */
	@Test
	public void testErrorLengthRDBHeader() {
		byte[] rdbHeader = new byte[] { 0, 0, 0, 0, 0, 0 };
		try {
			RDBParserFactory.createRDBParser(rdbHeader);
			assertTrue(false);
		} catch (RedisRDBException e) {
			assertTrue(true);
		}
	}

	/**
	 * 测试rdb头内容错误
	 */
	@Test
	public void testErrorHeadRDBHeader() {
		// RRDIS0006
		byte[] rdbHeader = new byte[] { 82, 82, 68, 73, 83, 48, 48, 48, 54 };
		try {
			RDBParserFactory.createRDBParser(rdbHeader);
			assertTrue(false);
		} catch (RedisRDBException e) {
			assertTrue(true);
		}
	}

	/**
	 * 测试正确的REDIS头解析
	 */
	@Test
	public void testRDBHeader() {
		// RRDIS0006
		byte[] rdbHeader = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54 };
		try {
			RDBParser rdbParser = RDBParserFactory.createRDBParser(rdbHeader);
			assertTrue(rdbParser != null);
			assertEquals(rdbParser.gerVersion(), "0006");
		} catch (RedisRDBException e) {
			assertTrue(false);
		}
	}

	/**
	 * 测试错误的rdb版本
	 */
	@Test
	public void testErrorVersionRDBHeader() {
		// RRDIS0006
		byte[] rdbHeader = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 55 };
		try {
			RDBParserFactory.createRDBParser(rdbHeader);
			assertTrue(false);
		} catch (RedisRDBException e) {
			assertTrue(true);
		}
	}
}
