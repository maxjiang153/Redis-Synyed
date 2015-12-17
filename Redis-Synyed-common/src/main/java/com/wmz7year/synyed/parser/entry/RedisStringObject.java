package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ENCODING_RAW;
import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_STRING;

/**
 * Redis 字符串类型数据对象
 * 
 * @Title: RedisStringObject.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月16日 下午1:20:59
 * @version V1.0
 */
public class RedisStringObject extends RedisObject {
	/**
	 * string字符串数据
	 */
	private byte[] data;

	public RedisStringObject(byte[] data) {
		this.data = data;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getType()
	 */
	@Override
	public byte getType() {
		return REDIS_STRING;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getEncoding()
	 */
	@Override
	public byte getEncoding() {
		return REDIS_ENCODING_RAW;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#toCommand()
	 */
	@Override
	public String toCommand() {
		return new String(data);
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getBuffer()
	 */
	@Override
	public byte[] getBuffer() {
		return this.data;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisStringObject [data=" + new String(data) + "]";
	}

}
