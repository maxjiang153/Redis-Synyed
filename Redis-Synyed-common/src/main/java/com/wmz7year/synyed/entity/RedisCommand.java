package com.wmz7year.synyed.entity;

import com.wmz7year.synyed.constant.RedisCommandSymbol;

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
	 * key
	 */
	private byte[] key;
	/**
	 * value
	 */
	private byte[] value;

	public RedisCommand(String command) {
		this.command = command;
	}

	public RedisCommand(String command, byte[] key) {
		this.command = command;
		this.key = key;
	}

	/**
	 * 设置key的方法
	 * 
	 * @param key
	 *            需要设置的字符串类型的key
	 */
	public void setKey(String key) {
		setKey(key.getBytes());
	}

	/**
	 * 需要设置的key
	 * 
	 * @param key
	 *            需要设置的key
	 */
	public void setKey(byte[] key) {
		this.key = key;
	}

	/**
	 * 添加值的方法<br>
	 * 第一次添加值直接设置<br>
	 * 接下来添加值的时候先添加一个空格 再添加值
	 * 
	 * @param value
	 *            需要添加的值
	 */
	public void addValue(byte[] value) {
		if (this.value == null) {
			this.value = value;
		} else {
			int length = this.value.length + value.length + 1;
			byte[] newValue = new byte[length];
			System.arraycopy(this.value, 0, newValue, 0, this.value.length);
			newValue[this.value.length] = RedisCommandSymbol.BLANK;
			System.arraycopy(value, 0, newValue, this.value.length + 1, value.length);
			this.value = newValue;
		}
	}

	/**
	 * 命令对象转换为byte数组的方法
	 * 
	 * @return
	 */
	public byte[] getBytes() {
		byte[] commandData = this.command.getBytes();
		int length = commandData.length;
		if (key != null) {
			length += key.length + 1;
		}
		if (value != null) {
			length += value.length + 1;
		}
		byte[] result = new byte[length];
		System.arraycopy(commandData, 0, result, 0, commandData.length);
		if (key != null) {
			result[commandData.length] = RedisCommandSymbol.BLANK;
			System.arraycopy(key, 0, result, commandData.length + 1, key.length);
		}
		if (value != null) {
			result[commandData.length + key.length + 1] = RedisCommandSymbol.BLANK;
			System.arraycopy(value, 0, result, commandData.length + key.length + 2, value.length);
		}
		return result;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisCommand [command=" + command + ", key=" + (key != null ? new String(key) : "null") + ", value="
				+ (value != null ? new String(value) : "null") + "]";
	}

}
