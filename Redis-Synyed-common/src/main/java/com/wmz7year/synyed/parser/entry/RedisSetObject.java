package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ENCODING_HT;
import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_RDB_TYPE_SET;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wmz7year.synyed.entity.RedisCommandData;

/**
 * redis set类型数据结构对象
 * 
 * @Title: RedisSetObject.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月16日 下午3:56:00
 * @version V1.0
 */
public class RedisSetObject extends RedisObject {

	/**
	 * set中的元素
	 */
	private Set<RedisStringObject> elements = new HashSet<RedisStringObject>();

	public RedisSetObject() {

	}

	/**
	 * 添加set中的元素的方法
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
		return REDIS_RDB_TYPE_SET;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getEncoding()
	 */
	@Override
	public byte getEncoding() {
		return REDIS_ENCODING_HT;
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
		return "RedisSetObject [elements=" + elements + "]";
	}

	public List<RedisCommandData> getElements() {
		List<RedisCommandData> result = new ArrayList<RedisCommandData>();
		for (RedisStringObject element : elements) {
			result.add(new RedisCommandData(element.getBuffer()));
		}
		return result;
	}

}
