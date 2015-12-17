package com.wmz7year.synyed.parser;

import org.apache.commons.io.HexDump;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmz7year.synyed.Booter;

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
	private static final byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -2, 0, 10, 8, 116, 101, 115,
			116, 108, 105, 115, 116, 23, 23, 0, 0, 0, 20, 0, 0, 0, 6, 0, 0, -10, 2, -11, 2, -12, 2, -13, 2, -14, 2, -15,
			-1, 0, 1, 97, -61, 21, 65, 44, 10, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, -32, -1, 9, -31, 14, 13, 1,
			56, 57, 1, 7, 108, 105, 115, 116, 107, 101, 121, 9, -61, 24, 66, 98, 10, 48, 49, 50, 51, 52, 53, 54, 55, 56,
			57, 48, -32, -1, 9, -31, -1, 13, -30, 60, 17, 1, 56, 57, -61, 21, 65, 64, 10, 48, 49, 50, 51, 52, 53, 54,
			55, 56, 57, 48, -32, -1, 9, -31, 34, 13, 1, 56, 57, -61, 18, 65, 4, 10, 48, 49, 50, 51, 52, 53, 54, 55, 56,
			57, 48, -32, -18, 9, 1, 56, 57, -61, 18, 64, -6, 10, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, -32, -28,
			9, 1, 56, 57, -61, 18, 64, -16, 10, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, -32, -38, 9, 1, 56, 57, -61,
			18, 64, 100, 10, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, -32, 78, 9, 1, 56, 57, 20, 48, 49, 50, 51, 52,
			53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 10, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, -64,
			1, 12, 7, 115, 111, 114, 116, 75, 101, 121, 21, 21, 0, 0, 0, 18, 0, 0, 0, 2, 0, 0, 6, 118, 97, 108, 117,
			101, 50, 8, -13, -1, 0, 1, 98, 1, 98, 11, 6, 115, 101, 116, 75, 101, 121, 10, 2, 0, 0, 0, 1, 0, 0, 0, 1, 0,
			-1, 17, -89, 90, -116, 65, 70, 6, 55 };

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
	}

}
