package com.wmz7year.synyed.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis命令对象<br>
 * 包含一个需要执行的命令 command 以及1个key 多个value
 * 
 * @Title: RedisCommand.java
 * @Package com.wmz7year.synyed.entity
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月23日 下午2:59:10
 * @version V1.0
 */
public class RedisCommand {
	/**
	 * 命令
	 */
	private String command;
	/**
	 * value
	 */
	private List<RedisCommandData> values = new ArrayList<RedisCommandData>();

	public RedisCommand(String command) {
		this.command = command;
	}

	/**
	 * 添加值的方法<br>
	 * 
	 * @param value
	 *            需要添加的值
	 */
	public void addValue(byte[] value) {
		this.values.add(new RedisCommandData(value));
	}

	public void addValue(String value) {
		addValue(value.getBytes());
	}

	/**
	 * 获取命令所占的空间大小的方法<br>
	 * 空间大小为命令+key+values的长度
	 * 
	 * @return 所占的空间大小
	 */
	public int getSize() {
		int result = 0;
		result += command.length();
		for (RedisCommandData value : values) {
			result += value.getData().length;
		}
		return result;
	}

	public String getCommand() {
		return this.command;
	}

	public List<RedisCommandData> getValues() {
		return this.values;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisCommand [command=" + command + ",values=" + values + "]";
	}

}
