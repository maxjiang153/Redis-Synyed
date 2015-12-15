package com.wmz7year.synyed.parser;

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
}
