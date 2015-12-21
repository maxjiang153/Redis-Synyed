package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ENCODING_SKIPLIST;
import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ZSET;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	private List<ZSetValue> elements = null;

	public RedisZSetObject(int zsetlen) {
		this.elements = new ArrayList<ZSetValue>(zsetlen);
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
		elements.add(new ZSetValue(redisObject, score));
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

		// 对结果集进行排序
		Collections.sort(elements, new Comparator<ZSetValue>() {

			/*
			 * @see java.util.Comparator#compare(java.lang.Object,
			 * java.lang.Object)
			 */
			@Override
			public int compare(ZSetValue o1, ZSetValue o2) {
				return o1.getScore() > o2.getScore() ? 1 : 0;
			}

		});
		
		for (ZSetValue zsetValue : elements) {
			result.append(zsetValue.getRedisObject().toCommand()).append(' ');
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

	/**
	 * 
	 * @Title: ZSetValue.java
	 * @Package com.wmz7year.synyed.parser.entry
	 * @author jiangwei (ydswcy513@gmail.com)
	 * @date 2015年12月21日 下午3:30:13
	 * @version V1.0
	 */
	private class ZSetValue {
		private RedisObject redisObject;
		private double score;

		public ZSetValue(RedisObject redisObject, double score) {
			this.redisObject = redisObject;
			this.score = score;
		}

		public RedisObject getRedisObject() {
			return redisObject;
		}

		public double getScore() {
			return score;
		}

	}
}
