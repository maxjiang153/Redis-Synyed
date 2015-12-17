package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ENCODING_LINKEDLIST;
import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_LIST;

import java.util.ArrayList;
import java.util.List;

/**
 * redis list类型数据结构对象
 * 
 * @Title: RedisListObject.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月16日 下午1:39:53
 * @version V1.0
 */
public class RedisListObject extends RedisObject {

	/**
	 * list中的元素
	 */
	private List<RedisStringObject> elements = new ArrayList<RedisStringObject>();

	public RedisListObject() {

	}

	/**
	 * 添加list中的元素的方法
	 * 
	 * @param redisObject
	 *            redis
	 */
	public void addElement(RedisObject redisObject) {
		if (!(redisObject instanceof RedisStringObject)) {
			throw new IllegalStateException("元素类型错误 必须为RedisStringObject类型");
		}
		this.elements.add((RedisStringObject) redisObject);
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getType()
	 */
	@Override
	public byte getType() {
		return REDIS_LIST;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getEncoding()
	 */
	@Override
	public byte getEncoding() {
		return REDIS_ENCODING_LINKEDLIST;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#toCommand()
	 */
	@Override
	public String toCommand() {
		StringBuilder result = new StringBuilder();
		for (RedisStringObject redisStringObject : elements) {
			result.append(redisStringObject.toCommand()).append(' ');
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
		return "RedisListObject [elements=" + elements + "]";
	}

}
