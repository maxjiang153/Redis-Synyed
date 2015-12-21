package com.wmz7year.synyed.entity;

/**
 * redis同步管道概念对象<br>
 * 每一个管道对应一个数据源与目标概念<br>
 * 数据源为数据产生着，目标为数据接受者<br>
 * 
 * @Title: Protocol.java
 * @Package com.wmz7year.synyed.entity
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月21日 下午9:37:03
 * @version V1.0
 */
public class Protocol {
	/**
	 * 源redis
	 */
	private RedisServer srcRedis;
	/**
	 * 目标redis
	 */
	private RedisServer descRedis;

	public Protocol() {
		super();
	}

	public Protocol(RedisServer srcRedis, RedisServer descRedis) {
		super();
		this.srcRedis = srcRedis;
		this.descRedis = descRedis;
	}

	public RedisServer getSrcRedis() {
		return srcRedis;
	}

	public void setSrcRedis(RedisServer srcRedis) {
		this.srcRedis = srcRedis;
	}

	public RedisServer getDescRedis() {
		return descRedis;
	}

	public void setDescRedis(RedisServer descRedis) {
		this.descRedis = descRedis;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descRedis == null) ? 0 : descRedis.hashCode());
		result = prime * result + ((srcRedis == null) ? 0 : srcRedis.hashCode());
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Protocol other = (Protocol) obj;
		if (descRedis == null) {
			if (other.descRedis != null)
				return false;
		} else if (!descRedis.equals(other.descRedis))
			return false;
		if (srcRedis == null) {
			if (other.srcRedis != null)
				return false;
		} else if (!srcRedis.equals(other.srcRedis))
			return false;
		return true;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Protocol [srcRedis=" + srcRedis + ", descRedis=" + descRedis + "]";
	}

}
