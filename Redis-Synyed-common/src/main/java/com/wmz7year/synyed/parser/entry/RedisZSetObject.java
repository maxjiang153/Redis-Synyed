package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ENCODING_SKIPLIST;
import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ZSET;

import java.util.ArrayList;
import java.util.List;

/**
 * redis zset类型数据结构对象
 * 
 * @Title: RedisSetObject.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月16日 下午3:56:00
 * @version V1.0
 */
public class RedisZSetObject extends RedisObject {

	private List<RedisStringObject> elements = null;

	public RedisZSetObject(int zsetlen) {
		this.elements = new ArrayList<RedisStringObject>(zsetlen);
	}

	/**
	 * 向指定位置插入元素的方法
	 * 
	 * @param redisObject
	 *            需要插入的元素
	 * @param score
	 *            指定的位置
	 */
	public void addElement(RedisObject redisObject, double score) {
		if (!(redisObject instanceof RedisStringObject)) {
			throw new IllegalStateException("元素类型错误 必须为RedisStringObject类型");
		}
		elements.add((int) score, (RedisStringObject) redisObject);
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getType()
	 */
	@Override
	public byte getType() {
		return REDIS_ZSET;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getEncoding()
	 */
	@Override
	public byte getEncoding() {
		return REDIS_ENCODING_SKIPLIST;
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
	 */
	@Override
	public String toString() {
		return "RedisZSetObject [elements=" + elements + "]";
	}

}
