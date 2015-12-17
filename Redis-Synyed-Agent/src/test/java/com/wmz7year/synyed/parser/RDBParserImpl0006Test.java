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
	private static final byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -1, -36, -77, 67, -16, 90, -36, -14, 86 };

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
