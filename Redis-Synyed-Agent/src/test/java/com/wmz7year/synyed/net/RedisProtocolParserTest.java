package com.wmz7year.synyed.net;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmz7year.synyed.Booter;
import com.wmz7year.synyed.constant.RedisCommandSymbol;
import com.wmz7year.synyed.exception.RedisProtocolException;
import com.wmz7year.synyed.net.proroc.RedisProtocolParser;
import com.wmz7year.synyed.packet.redis.RedisArraysPacket;
import com.wmz7year.synyed.packet.redis.RedisBulkStringPacket;
import com.wmz7year.synyed.packet.redis.RedisDataBaseTransferPacket;
import com.wmz7year.synyed.packet.redis.RedisErrorPacket;
import com.wmz7year.synyed.packet.redis.RedisIntegerPacket;
import com.wmz7year.synyed.packet.redis.RedisPacket;

/**
 * redis响应解析器相关的测试
 * 
 * @Title: RedisProtocolParserTest.java
 * @Package com.wmz7year.synyed.net
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月11日 下午7:18:18
 * @version V1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Booter.class)
public class RedisProtocolParserTest {
	private static final Logger logger = LoggerFactory.getLogger(RedisProtocolParserTest.class);

	private static final String CRLF = "\r\n";

	private static final RedisProtocolParser parser = new RedisProtocolParser();

	/**
	 * 测试普通的字符串解析
	 */
	@Test
	public void testSimpleStringPacket() throws Exception {
		String str = "helloworld";
		StringBuilder redisData = new StringBuilder();
		redisData.append('+');
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();

		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		String command = packet.getCommand();
		assertEquals(command, str);
	}

	/**
	 * 测试普通的长字符串解析
	 */
	@Test
	public void testLongSimpleStringPacket() throws Exception {
		String str = "helloworld";
		for (int i = 0; i < 10; i++) {
			str += str;
		}

		StringBuilder redisData = new StringBuilder();
		redisData.append('+');
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		logger.info("testLongSimpleStringPacket   str length:" + data.length);
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();

		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		String command = packet.getCommand();
		assertEquals(command, str);
	}

	/**
	 * 测试错误类型的字符串解析
	 */
	@Test
	public void testErrorSimpleStringPacket() throws Exception {
		String str = "helloworld";
		StringBuilder redisData = new StringBuilder();
		redisData.append('(');
		redisData.append(str);
		redisData.append(CRLF);

		RedisProtocolParser localParser = new RedisProtocolParser();
		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		try {
			localParser.read(buf);
		} catch (Exception e) {
			assertTrue(e instanceof RedisProtocolException);
		}

		RedisPacket[] packets = parser.getPackets();

		assertNull(packets);
	}

	/**
	 * 测试错误类型数据包解析
	 */
	@Test
	public void testErrorPacket() throws Exception {
		String str = "helloworld";
		StringBuilder redisData = new StringBuilder();
		redisData.append('-');
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();

		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		String command = packet.getCommand();
		assertEquals(command, RedisCommandSymbol.ERR);
		if (packet instanceof RedisErrorPacket) {
			RedisErrorPacket errorPacket = (RedisErrorPacket) packet;
			assertEquals(errorPacket.getErrorMessage(), str);
		} else {
			assertFalse("数据包类型解析错误", false);
		}

	}

	/**
	 * 测试错误类型长数据包解析
	 */
	@Test
	public void testLongErrorPacket() throws Exception {
		String str = "helloworld";
		for (int i = 0; i < 10; i++) {
			str += str;
		}
		StringBuilder redisData = new StringBuilder();
		redisData.append('-');
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		logger.info("testLongErrorPacket   str length:" + data.length);
		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();

		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		String command = packet.getCommand();
		assertEquals(command, RedisCommandSymbol.ERR);
		if (packet instanceof RedisErrorPacket) {
			RedisErrorPacket errorPacket = (RedisErrorPacket) packet;
			assertEquals(errorPacket.getErrorMessage(), str);
		} else {
			assertFalse("数据包类型解析错误", false);
		}

	}

	/**
	 * 测试整数类型数据
	 */
	@Test
	public void testIntegerPacket() throws Exception {
		long num = 10000;
		StringBuilder redisData = new StringBuilder();
		redisData.append(':');
		redisData.append(num);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();

		assertTrue(packets.length == 1);

		RedisPacket packet = packets[0];
		String command = packet.getCommand();
		assertEquals(command, RedisCommandSymbol.INTEGER);

		if (packet instanceof RedisIntegerPacket) {
			RedisIntegerPacket integerPacket = (RedisIntegerPacket) packet;
			assertEquals(integerPacket.getNum(), num);
		} else {
			assertFalse("数据包类型解析错误", false);
		}
	}

	/**
	 * 测试负数整数类型数据
	 */
	@Test
	public void testNegIntegerPacket() throws Exception {
		long num = -10001;
		StringBuilder redisData = new StringBuilder();
		redisData.append(':');
		redisData.append(num);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();

		assertTrue(packets.length == 1);

		RedisPacket packet = packets[0];
		String command = packet.getCommand();
		assertEquals(command, RedisCommandSymbol.INTEGER);

		if (packet instanceof RedisIntegerPacket) {
			RedisIntegerPacket integerPacket = (RedisIntegerPacket) packet;
			assertEquals(integerPacket.getNum(), num);
		} else {
			assertFalse("数据包类型解析错误", false);
		}
	}

	/**
	 * 测试错误整数类型数据
	 */
	@Test
	public void testErrorIntegerPacket() throws Exception {
		double num = -1000.1;
		StringBuilder redisData = new StringBuilder();
		redisData.append(':');
		redisData.append(num);
		redisData.append(CRLF);

		RedisProtocolParser localParser = new RedisProtocolParser();
		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();
		try {
			localParser.read(buf);
		} catch (Exception e) {
			assertTrue(e instanceof RedisProtocolException);
		}

		RedisPacket[] packets = parser.getPackets();

		assertNull(packets);

	}

	/**
	 * 测试数据库传输包
	 */
	@Test
	public void testDatabaseTransferPacket() throws Exception {
		byte[] data = { 36, 51, 49, 13, 10, 82, 69, 68, 73, 83, 48, 48, 48, 54, -2, 0, 0, 5, 109, 121, 107, 101, 121, 3,
				0, -1, -16, -1, -1, -100, -79, 119, 2, -41, -36, -30 };
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		assertEquals(packet.getCommand(), RedisCommandSymbol.DATABASETRANSFER);
		assertTrue(packet instanceof RedisDataBaseTransferPacket);

	}

	/**
	 * 测试复合字符串的包
	 */
	@Test
	public void testBulkPacket() throws Exception {
		String str = "helloworld";
		StringBuilder redisData = new StringBuilder();
		redisData.append('$');
		redisData.append(str.length());
		redisData.append(CRLF);
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		assertEquals(packet.getCommand(), RedisCommandSymbol.BULKSTRING);
		assertTrue(packet instanceof RedisBulkStringPacket);
		RedisBulkStringPacket bulkPacket = (RedisBulkStringPacket) packet;
		assertEquals(str, new String(bulkPacket.getData()));
	}

	/**
	 * 测试长复合字符串的包
	 */
	@Test
	public void testLongBulkPacket() throws Exception {
		String str = "helloworld";
		for (int i = 0; i < 10; i++) {
			str += str;
		}
		StringBuilder redisData = new StringBuilder();
		redisData.append('$');
		redisData.append(str.length());
		redisData.append(CRLF);
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();
		logger.info("testLongBulkPacket str length:" + data.length);
		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		assertEquals(packet.getCommand(), RedisCommandSymbol.BULKSTRING);
		assertTrue(packet instanceof RedisBulkStringPacket);
		RedisBulkStringPacket bulkPacket = (RedisBulkStringPacket) packet;
		assertEquals(str, new String(bulkPacket.getData()));
	}

	/**
	 * 测试复合字符串的包
	 */
	@Test
	public void testEmptyBulkPacket() throws Exception {
		String str = "";
		StringBuilder redisData = new StringBuilder();
		redisData.append('$');
		redisData.append(str.length());
		redisData.append(CRLF);
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		assertEquals(packet.getCommand(), RedisCommandSymbol.BULKSTRING);
		assertTrue(packet instanceof RedisBulkStringPacket);
		RedisBulkStringPacket bulkPacket = (RedisBulkStringPacket) packet;
		assertEquals(str, new String(bulkPacket.getData()));
	}

	/**
	 * 测试空的数组
	 */
	@Test
	public void testEmptyArrayPacket() throws Exception {
		StringBuilder redisData = new StringBuilder();
		redisData.append('*');
		redisData.append(0);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
	}

	/**
	 * 测试1个整数元素的数组
	 */
	@Test
	public void testOneIntegerDataArrayPacket() throws Exception {
		StringBuilder redisData = new StringBuilder();
		redisData.append('*');
		redisData.append(1);
		redisData.append(CRLF);
		redisData.append(':');
		redisData.append(1);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		assertEquals(packet.getCommand(), RedisCommandSymbol.ARRAY);

		RedisArraysPacket arrayPacket = (RedisArraysPacket) packet;
		assertTrue(arrayPacket.getPackets().size() == 1);
		RedisPacket elementPacket = arrayPacket.getPackets().get(0);
		assertEquals(elementPacket.getCommand(), RedisCommandSymbol.INTEGER);
	}

	/**
	 * 测试1个整数元素的数组
	 */
	@Test
	public void testOneIntegerDataOneStringDataArrayPacket() throws Exception {
		StringBuilder redisData = new StringBuilder();
		redisData.append('*');
		redisData.append(2);
		redisData.append(CRLF);
		redisData.append(':');
		redisData.append(1);
		redisData.append(CRLF);
		redisData.append('+');
		redisData.append("helloworld");
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data).flip();

		parser.read(buf);

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		assertEquals(packet.getCommand(), RedisCommandSymbol.ARRAY);

		RedisArraysPacket arrayPacket = (RedisArraysPacket) packet;
		assertTrue(arrayPacket.getPackets().size() == 2);
		RedisPacket elementPacket = arrayPacket.getPackets().get(0);
		assertEquals(elementPacket.getCommand(), RedisCommandSymbol.INTEGER);
		RedisPacket elementPacket1 = arrayPacket.getPackets().get(1);
		assertEquals(elementPacket1.getCommand(), "helloworld");
	}

	/**
	 * 测试分包
	 */
	@Test
	public void testSubSimpleStringPacket() throws Exception {
		String str = "helloworld";
		StringBuilder redisData = new StringBuilder();
		redisData.append('+');
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		for (int i = 0; i < data.length; i++) {
			ByteBuffer buf = ByteBuffer.allocate(1);
			buf.put(data[i]).flip();

			parser.read(buf);
		}

		RedisPacket[] packets = parser.getPackets();

		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		String command = packet.getCommand();
		assertEquals(command, str);

	}

	/**
	 * 测试错误类型分包
	 */
	@Test
	public void testSubErrorStringPacket() throws Exception {
		String str = "helloworld";
		StringBuilder redisData = new StringBuilder();
		redisData.append('-');
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		for (int i = 0; i < data.length; i++) {
			ByteBuffer buf = ByteBuffer.allocate(1);
			buf.put(data[i]).flip();

			parser.read(buf);
		}

		RedisPacket[] packets = parser.getPackets();

		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		String command = packet.getCommand();
		assertEquals(command, RedisCommandSymbol.ERR);
		if (packet instanceof RedisErrorPacket) {
			RedisErrorPacket errorPacket = (RedisErrorPacket) packet;
			assertEquals(errorPacket.getErrorMessage(), str);
		} else {
			assertFalse("数据包类型解析错误", false);
		}

	}

	/**
	 * 测试整数类型分包
	 */
	@Test
	public void testSubIntegerStringPacket() throws Exception {
		long num = 10000;
		StringBuilder redisData = new StringBuilder();
		redisData.append(':');
		redisData.append(num);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		for (int i = 0; i < data.length; i++) {
			ByteBuffer buf = ByteBuffer.allocate(1);
			buf.put(data[i]).flip();

			parser.read(buf);
		}

		RedisPacket[] packets = parser.getPackets();

		assertTrue(packets.length == 1);

		RedisPacket packet = packets[0];
		String command = packet.getCommand();
		assertEquals(command, RedisCommandSymbol.INTEGER);

		if (packet instanceof RedisIntegerPacket) {
			RedisIntegerPacket integerPacket = (RedisIntegerPacket) packet;
			assertEquals(integerPacket.getNum(), num);
		} else {
			assertFalse("数据包类型解析错误", false);
		}
	}

	/**
	 * 测试分段数据库传输包
	 */
	@Test
	public void testSubDatabaseTransferPacket() throws Exception {
		byte[] data = { 36, 51, 49, 13, 10, 82, 69, 68, 73, 83, 48, 48, 48, 54, -2, 0, 0, 5, 109, 121, 107, 101, 121, 3,
				0, -1, -16, -1, -1, -100, -79, 119, 2, -41, -36, -30 };

		for (int i = 0; i < data.length; i++) {
			ByteBuffer buf = ByteBuffer.allocate(1);
			buf.put(data[i]).flip();

			parser.read(buf);
		}

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		assertEquals(packet.getCommand(), RedisCommandSymbol.DATABASETRANSFER);
		assertTrue(packet instanceof RedisDataBaseTransferPacket);

	}

	/**
	 * 测试复合字符串的包
	 */
	@Test
	public void testSubBulkPacket() throws Exception {
		String str = "121321213213211321";
		StringBuilder redisData = new StringBuilder();
		redisData.append('$');
		redisData.append(str.length());
		redisData.append(CRLF);
		redisData.append(str);
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		for (int i = 0; i < data.length; i++) {
			ByteBuffer buf = ByteBuffer.allocate(1);
			buf.put(data[i]).flip();

			parser.read(buf);
		}

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		assertEquals(packet.getCommand(), RedisCommandSymbol.BULKSTRING);
		assertTrue(packet instanceof RedisBulkStringPacket);
		RedisBulkStringPacket bulkPacket = (RedisBulkStringPacket) packet;
		assertEquals(str, new String(bulkPacket.getData()));
	}

	/**
	 * 测试1个整数元素的数组
	 */
	@Test
	public void testSubOneIntegerDataOneStringDataArrayPacket() throws Exception {
		StringBuilder redisData = new StringBuilder();
		redisData.append('*');
		redisData.append(2);
		redisData.append(CRLF);
		redisData.append(':');
		redisData.append(1);
		redisData.append(CRLF);
		redisData.append('+');
		redisData.append("helloworld");
		redisData.append(CRLF);

		byte[] data = redisData.toString().getBytes();
		for (int i = 0; i < data.length; i++) {
			ByteBuffer buf = ByteBuffer.allocate(1);
			buf.put(data[i]).flip();

			parser.read(buf);
		}

		RedisPacket[] packets = parser.getPackets();
		assertNotNull(packets);
		assertTrue(packets.length == 1);
		RedisPacket packet = packets[0];
		assertEquals(packet.getCommand(), RedisCommandSymbol.ARRAY);

		RedisArraysPacket arrayPacket = (RedisArraysPacket) packet;
		assertTrue(arrayPacket.getPackets().size() == 2);
		RedisPacket elementPacket = arrayPacket.getPackets().get(0);
		assertEquals(elementPacket.getCommand(), RedisCommandSymbol.INTEGER);
		RedisPacket elementPacket1 = arrayPacket.getPackets().get(1);
		assertEquals(elementPacket1.getCommand(), "helloworld");
	}
}
