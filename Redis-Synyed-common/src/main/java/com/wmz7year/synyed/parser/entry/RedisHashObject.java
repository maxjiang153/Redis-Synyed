package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ENCODING_ZIPLIST;
import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_HASH;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * redis hash类型的对象
 * 
 * @Title: RedisHashObject.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月16日 下午5:05:17
 * @version V1.0
 */
public class RedisHashObject extends RedisObject {

	private Map<RedisObject, RedisObject> elements = new HashMap<RedisObject, RedisObject>();

	public RedisHashObject() {

	}

	/**
	 * 添加元素的方法
	 * 
	 * @param field
	 *            field
	 * @param value
	 *            value
	 */
	public void addElement(RedisObject field, RedisObject value) {
		if (!(field instanceof RedisStringObject)) {
			throw new IllegalStateException("元素类型错误 必须为RedisStringObject类型");
		}
		if (!(value instanceof RedisStringObject)) {
			throw new IllegalStateException("元素类型错误 必须为RedisStringObject类型");
		}
		elements.put(field, value);
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getType()
	 */
	@Override
	public byte getType() {
		return REDIS_HASH;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getEncoding()
	 */
	@Override
	public byte getEncoding() {
		return REDIS_ENCODING_ZIPLIST;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#toCommand()
	 */
	@Override
	public String toCommand() {
		StringBuilder result = new StringBuilder();
		Iterator<Entry<RedisObject, RedisObject>> iterator = elements.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<RedisObject, RedisObject> next = iterator.next();
			result.append(next.getKey().toCommand()).append(' ').append(next.getValue().toCommand()).append(' ');
		}
		if (result.length() > 0 && result.charAt(result.length()) == ' ') {
			return result.substring(0, result.length() - 1);
		}
		return result.toString();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisHashObject [elements=" + elements + "]";
	}

}
