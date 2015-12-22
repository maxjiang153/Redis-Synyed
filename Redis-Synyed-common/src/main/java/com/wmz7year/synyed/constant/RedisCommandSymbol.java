package com.wmz7year.synyed.constant;

/**
 * 封装Redis命令符号常量类
 * 
 * FIXME：常量按字母顺序排序
 * 
 * @Title: RedisCommandSymbol.java
 * @Package com.wmz7year.synyed.constant
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月11日 上午11:10:51
 * @version V1.0
 */
public class RedisCommandSymbol {
	/**
	 * 空格
	 */
	public static final char BLANK = ' ';
	/**
	 * 验证身份命令
	 */
	public static final String AUTH = "AUTH";
	/**
	 * redis错误符号
	 */
	public static final String ERR = "ERR";
	/**
	 * redis成功符号
	 */
	public static final String OK = "OK";
	/**
	 * ping命令
	 */
	public static final String PING = "PING";
	/**
	 * redis 数据传输符号
	 */
	public static final String DATABASETRANSFER = "DATABASETRANSFER";
	/**
	 * 整数类型数据包
	 */
	public static final String INTEGER = "INTEGER";
	/**
	 * 复合类型的字符串
	 */
	public static final String BULKSTRING = "BULKSTRING";
	/**
	 * 数组类型数据包
	 */
	public static final String ARRAY = "ARRAY";
}
