package com.wmz7year.synyed.packet.redis;

/**
 * Redis整数类型数据包<br>
 * 
 * @Title: RedisIntegerPacket.java
 * @Package com.wmz7year.synyed.packet.redis
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月11日 下午7:47:37
 * @version V1.0
 */
public class RedisIntegerPacket extends RedisPacket {
	/**
	 * 数据对应的值
	 */
	private long num;
	/**
	 * 数据内容
	 */
	private byte[] data;

	public RedisIntegerPacket(String command, byte[] data) {
		super(command);
		this.data = data;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public long getNum() {
		return this.num;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisIntegerPacket [num=" + num + "]";
	}

	/*
	 * @see com.wmz7year.synyed.packet.redis.RedisPacket#getData()
	 */
	@Override
	public byte[] getData() {
		return data;
	}

}
