package com.wmz7year.synyed.parser.entry;

/**
 * 封装Redis数据对象的抽象类
 * 
 * @Title: RedisObject.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月16日 下午1:18:01
 * @version V1.0
 */
public abstract class RedisObject {

	/**
	 * 数据对象类型
	 * 
	 * @return 数据对象类型
	 */
	public abstract byte getType();

	/**
	 * 获取数据编码方式的方法
	 * 
	 * @return 数据编码方式
	 */
	public abstract byte getEncoding();

	/**
	 * 转换为Redis命令的方法
	 * 
	 * @return redis命令字符串
	 */
	public abstract String toCommand();

	/**
	 * 获取原始数据的方法
	 * 
	 * @return 原始数据
	 */
	public byte[] getBuffer() {
		throw new UnsupportedOperationException();
	}
}
