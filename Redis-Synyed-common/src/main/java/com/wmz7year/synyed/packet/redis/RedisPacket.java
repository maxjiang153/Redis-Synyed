package com.wmz7year.synyed.packet.redis;

/**
 * Redis数据包<br>
 * 该数据包为最原始的对象
 * 
 * @Title: RedisPacket.java
 * @Package com.wmz7year.synyed.packet.redis
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午3:11:43
 * @version V1.0
 */
public abstract class RedisPacket {
	/**
	 * redis命令
	 */
	protected String command;

	public RedisPacket(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * 获取数据包原始数据的方法
	 * 
	 * @return 数据包原始数据
	 */
	public abstract byte[] getData();
}