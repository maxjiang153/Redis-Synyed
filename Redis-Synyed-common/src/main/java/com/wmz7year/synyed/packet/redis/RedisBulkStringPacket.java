package com.wmz7year.synyed.packet.redis;

/**
 * redis复合类型字符串传输的数据包
 * 
 * @Title: RedisBulkStringPacket.java
 * @Package com.wmz7year.synyed.packet.redis
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月11日 下午8:59:27
 * @version V1.0
 */
public class RedisBulkStringPacket extends RedisPacket {
	/**
	 * 字符串内容
	 */
	private String data;

	public RedisBulkStringPacket(String command) {
		super(command);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisBulkStringPacket [data=" + data + "]";
	}

}
