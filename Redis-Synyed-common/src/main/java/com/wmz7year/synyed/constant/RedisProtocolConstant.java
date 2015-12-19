package com.wmz7year.synyed.constant;

/**
 * 封装Redis协议常量的类<br>
 * 如协议结束符\r\n等常量
 *
 * @Title: RedisProtocolConstant.java
 * @Package com.wmz7year.synyed.constant
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月9日 下午5:18:45
 * @version V1.0
 */
public class RedisProtocolConstant {
	/**
	 * redis数据包解析器
	 */
	public static final String REDIS_PROTOCOL_PARSER = "redis_protpcol_parser";
	/**
	 * redis命令结束分隔符
	 */
	public static final String COMMAND_END_SUFFIX = "\r\n";

	/**
	 * \r byte类型 换行符常量
	 */
	public static final byte REDIS_PROTOCOL_CR = '\r';
	/**
	 * \n byte类型 回车符号常量
	 */
	public static final byte REDIS_PROTOCOL_LF = '\n';
	/**
	 * redis字符串类型
	 */
	public static final byte REDIS_PROTOCOL_SIMPLE_STRING = '+';
	/**
	 * redis错误类型
	 */
	public static final byte REDIS_PROTOCOL_ERRORS = '-';
	/**
	 * redis整数类型
	 */
	public static final byte REDIS_PROTOCOL_INTEGERS = ':';
	/**
	 * redis 复合字符串类型
	 */
	public static final byte REDIS_PROTOCOL_BULK_STRINGS = '$';
	/**
	 * redis 数组类型
	 */
	public static final byte REDIS_PROTOCOL_ARRAY = '*';

}
