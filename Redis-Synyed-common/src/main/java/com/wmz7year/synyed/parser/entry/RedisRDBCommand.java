package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisCommand.*;

import java.util.ArrayList;
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

	/**
	 * 根据value值分析生成redis命令的方法<br>
	 * 
	 * @return redis命令集合
	 */
	public List<String> getCommands() {
		List<String> commands = new ArrayList<String>();
		// string 类型的value 生成redis命令 set key value
		if (value instanceof RedisStringObject) {
			StringBuilder builder = new StringBuilder();
			builder.append(SET).append(' ').append(key.toCommand()).append(' ').append(value.toCommand());
			commands.add(builder.toString());
		} else if (value instanceof RedisZipListObject) {
			StringBuilder builder = new StringBuilder();
			builder.append(LPUSH).append(' ').append(key.toCommand()).append(' ').append(value.toCommand());
			commands.add(builder.toString());
		}
		// TODO 过期时间
		return commands;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisRDBCommand [key=" + key + ", value=" + value + ", expiretime=" + expiretime + "]";
	}

}
