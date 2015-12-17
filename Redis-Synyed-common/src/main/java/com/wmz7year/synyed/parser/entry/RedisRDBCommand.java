package com.wmz7year.synyed.parser.entry;

import java.util.List;

/**
 * rdb文件内容转换为redis命令的方法
 * 
 * @Title: RedisRDBCommand.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月17日 下午2:47:26
 * @version V1.0
 */
public class RedisRDBCommand {
	/**
	 * 命令的key
	 */
	private RedisObject key;
	/**
	 * 命令的value
	 */
	private RedisObject value;
	/**
	 * 命令的过期时间
	 */
	private long expiretime = -1;

	public RedisRDBCommand(RedisObject key, RedisObject value, long expiretime) {
		this.key = key;
		this.value = value;
		this.expiretime = expiretime;
	}

	public List<String> getCommands() {
		// TODO 分析key value 转换成redis命令
		return null;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisRDBCommand [key=" + key + ", value=" + value + ", expiretime=" + expiretime + "]";
	}

}
