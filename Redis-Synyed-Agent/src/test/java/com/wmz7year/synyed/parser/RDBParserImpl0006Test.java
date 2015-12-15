package com.wmz7year.synyed.parser;

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
	private static final byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -2, 0, 0, -64, 55, -64, 23,
			0, 3, 115, 100, 115, 4, 0, 0, 0, 0, 0, 1, 66, -64, -1, 0, 3, 115, 97, 100, 5, 97, 115, 100, 97, 115, 10, 7,
			114, 101, 99, 105, 118, 101, 114, 15, 15, 0, 0, 0, 12, 0, 0, 0, 2, 0, 0, -14, 2, -14, -1, 10, 5, 108, 105,
			115, 116, 49, 17, 17, 0, 0, 0, 13, 0, 0, 0, 2, 0, 0, 1, 97, 3, 1, 98, -1, 0, 5, 108, 105, 115, 116, 51, 1,
			99, 0, 5, 97, 115, 100, 97, 115, 7, 61, 113, 119, 101, 113, 119, 101, 0, 5, 97, 97, 97, 97, 97, 2, 98, 98,
			0, 4, 97, 97, 97, 97, -64, 100, 0, 1, 100, 4, 78, 85, 76, 76, 0, 8, 115, 98, 107, 97, 105, 102, 97, 110, 8,
			115, 98, 107, 97, 105, 102, 97, 110, 0, 1, 99, -64, 1, 0, 4, 116, 101, 115, 116, 2, 119, 101, 0, 1, 97, 1,
			98, 0, 5, 119, 113, 101, 113, 119, 3, 113, 119, 101, -1, 22, -25, 49, 125, 118, -66, -45, 14 };

	public void testParser() {

	}
}
