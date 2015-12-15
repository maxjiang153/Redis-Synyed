package com.wmz7year.synyed.parser.impl;

import com.wmz7year.synyed.parser.RDBParser;

/**
 * 针对0006版本的redis rdb数据文件解析器
 * 
 * @Title: RDBParserImpl0006.java
 * @Package com.wmz7year.synyed.parser.impl
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月14日 下午2:26:50
 * @version V1.0
 */
public class RDBParserImpl0006 implements RDBParser {
	/**
	 * rdb文件版本号
	 */
	private static final String VERSION = "0006";

	/*
	 * @see com.wmz7year.synyed.parser.RDBParser#gerVersion()
	 */
	@Override
	public String gerVersion() {
		return VERSION;
	}

}
