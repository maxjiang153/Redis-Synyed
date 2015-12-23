package com.wmz7year.synyed.packet.redis;

/**
 * Redis错误类型的响应包对象<br>
 * 包含了错误的响应信息
 * 
 * @Title: RedisErrorPacket.java
 * @Package com.wmz7year.synyed.packet.redis
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月11日 上午11:20:55
 * @version V1.0
 */
public class RedisErrorPacket extends RedisPacket {
	/**
	 * 错误信息的提示语
	 */
	private String errorMessage;
	/**
	 * 数据内容
	 */
	private byte[] data;

	public RedisErrorPacket(String command, byte[] data) {
		super(command);
		this.data = data;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisErrorPacket [errorMessage=" + errorMessage + ", command=" + command + "]";
	}

	/*
	 * @see com.wmz7year.synyed.packet.redis.RedisPacket#getData()
	 */
	@Override
	public byte[] getData() {
		return data;
	}

}
