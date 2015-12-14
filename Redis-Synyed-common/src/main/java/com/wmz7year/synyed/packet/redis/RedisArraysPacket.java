package com.wmz7year.synyed.packet.redis;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis 数组类型数据包对象
 * 
 * @Title: RedisArraysPacket.java
 * @Package com.wmz7year.synyed.packet.redis
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月14日 上午9:23:08
 * @version V1.0
 */
public class RedisArraysPacket extends RedisPacket {
	/**
	 * 数组长度
	 */
	private long arrayLength;
	/**
	 * 数组内的数据包列表
	 */
	private List<RedisPacket> packets = new ArrayList<RedisPacket>();

	public RedisArraysPacket(String command) {
		super(command);
	}

	public long getArrayLength() {
		return arrayLength;
	}

	public void setArrayLength(long arrayLength) {
		this.arrayLength = arrayLength;
	}

	public List<RedisPacket> getPackets() {
		return packets;
	}

	public void setPackets(List<RedisPacket> packets) {
		this.packets = packets;
	}

	public void addPacket(RedisPacket packet) {
		this.packets.add(packet);
	}
}
