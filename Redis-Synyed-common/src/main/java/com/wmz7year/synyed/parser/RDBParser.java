package com.wmz7year.synyed.parser;

import com.wmz7year.synyed.exception.RedisRDBException;

/**
 * Redis rdb文件解析器<br>
 * 用于首次SYNC到redis时 redis把rdb文件内容发送过来解析成redis命令时使用<br>
 * 每个版本的格式都不同，实现类针对不同的版本进行
 * 
 * @Title: RDBParser.java
 * @Package com.wmz7year.synyed.parser
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月14日 下午1:54:22
 * @version V1.0
 */
public interface RDBParser {

	/**
	 * 获取rdb格式版本的方法<br>
	 * rdb的格式版本信息为头内容的9个字节数据REDIS****
	 * 
	 * @return rdb内容版本
	 */
	public String gerVersion();

	/**
	 * 执行解析的方法<br>
	 * 
	 * // TODO 解析为RDB对象 或者解析成命令
	 * 
	 * @param rdbContent
	 *            rdb文件内容
	 * @throws RedisRDBException
	 *             当解析过程中发生错误抛出该异常
	 */
	public void parse(byte[] rdbContent) throws RedisRDBException;
}
