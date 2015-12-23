package com.wmz7year.synyed.entity;

/**
 * 封装Redis命令数据的实体类<br>
 * 如key数据、value数据等
 * 
 * @Title: RedisCommandData.java
 * @Package com.wmz7year.synyed.entity
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月23日 下午5:33:26
 * @version V1.0
 */
public class RedisCommandData {
	/**
	 * 原始数据
	 */
	private byte[] data;
	/**
	 * 转换为字符串后的数据
	 */
	private String content;

	public RedisCommandData(byte[] data) {
		super();
		this.data = data;
		this.content = new String(data);
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisCommandData [dataLength=" + data.length + ", content=" + content + "]";
	}

}
