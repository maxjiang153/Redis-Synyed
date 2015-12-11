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

	public RedisIntegerPacket(String command) {
		super(command);
	}

	public void setNum(long num) {
		this.num = num;
	}

	public long getNum() {
		return this.num;
	}

}
