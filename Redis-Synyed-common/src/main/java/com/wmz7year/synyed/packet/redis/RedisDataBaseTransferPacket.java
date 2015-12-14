package com.wmz7year.synyed.packet.redis;

/**
 * 处理Redis数据文件传输的数据包对象<br>
 * TODO 针对数据包内容的解析
 * 
 * @Title: RedisDataBaseTransferPacket.java
 * @Package com.wmz7year.synyed.packet.redis
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月11日 下午6:30:46
 * @version V1.0
 */
public class RedisDataBaseTransferPacket extends RedisPacket {

	/**
	 * redis数据信息缓冲区
	 */
	private byte[] packetData;

	public RedisDataBaseTransferPacket(String command) {
		super(command);
	}

	public void setData(byte[] packetData) {
		this.packetData = packetData;
	}

	public byte[] getPacketData() {
		return packetData;
	}

	public void setPacketData(byte[] packetData) {
		this.packetData = packetData;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisDataBaseTransferPacket [packetDataLength=" + packetData.length + "]";
	}

}
