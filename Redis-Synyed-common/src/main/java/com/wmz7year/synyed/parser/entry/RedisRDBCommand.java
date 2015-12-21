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
		if (value instanceof RedisHashObject) {
			// TODO
			System.out.println("RedisHashObject");
		} else if (value instanceof RedisHashZipList) {
			// hash set类型数据 生成hset命令
			commands.addAll(createHSETCommand());
		} else if (value instanceof RedisHashZipMap) {
			// TODO
			System.out.println("RedisHashZipMap");
		} else if (value instanceof RedisListObject) {
			// TODO
			System.out.println("RedisListObject");
		} else if (value instanceof RedisSetIntSet) {
			// TODO
			System.out.println("RedisSetIntSet");
		} else if (value instanceof RedisSetObject) {
			// TODO
			System.out.println("RedisSetObject");
		} else if (value instanceof RedisStringObject) {
			// string 类型的value 生成redis set命令
			commands.add(createSETCommand());
		} else if (value instanceof RedisZipListObject) {
			// list类型的value 生成lpush命令
			commands.add(createLPUSHCommand());
		} else if (value instanceof RedisZSetObject) {
			// TODO
			System.out.println("RedisZSetObject");
		} else if (value instanceof RedisZSetZipList) {
			// sorted set类型数据 生成zadd命令
			commands.add(createZADDCommand());
		} else {
			throw new IllegalStateException("不支持的数据类型：" + value.getClass().getName());
		}
		// TODO 过期时间
		return commands;
	}

	/**
	 * 根据key value 创建set命令的方法<br>
	 * 
	 * set key value
	 * 
	 * @return set命令
	 */
	private String createSETCommand() {
		StringBuilder builder = new StringBuilder();
		builder.append(SET).append(' ').append(key.toCommand()).append(' ').append(value.toCommand());
		return builder.toString();
	}

	/**
	 * 根据key value创建lpush命令的方法<br>
	 * 
	 * lpush key value value..
	 * 
	 * @return lpush命令
	 */
	private String createLPUSHCommand() {
		StringBuilder builder = new StringBuilder();
		builder.append(LPUSH).append(' ').append(key.toCommand()).append(' ').append(value.toCommand());
		return builder.toString();
	}

	/**
	 * 根据key value创建hset命令<br>
	 * 
	 * hset key flag value
	 * 
	 * @return hset命令
	 */
	private List<String> createHSETCommand() {
		List<String> commands = new ArrayList<String>();
		RedisHashZipList hashZipList = (RedisHashZipList) value;
		List<String> elements = hashZipList.getElements();
		for (int i = 0; i < hashZipList.getElementCount(); i += 2) {
			StringBuilder builder = new StringBuilder();
			builder.append(HSET).append(' ').append(key.toCommand()).append(' ').append(elements.get(i)).append(' ')
					.append(elements.get(i + 1));
			commands.add(builder.toString());
		}
		return commands;
	}

	/**
	 * 根据key value创建zadd命令<br>
	 * 
	 * zadd key field value
	 * 
	 * @return zadd命令
	 */
	private String createZADDCommand() {
		StringBuilder builder = new StringBuilder();
		builder.append(ZADD).append(' ').append(key.toCommand()).append(' ').append(value.toCommand());
		return builder.toString();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisRDBCommand [key=" + key + ", value=" + value + ", expiretime=" + expiretime + "]";
	}

}
