package com.wmz7year.synyed.parser.entry;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装Redis 数据库信息的实体类<br>
 * 
 * @Title: RedisDB.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月15日 下午3:17:53
 * @version V1.0
 */
public class RedisDB {
	/**
	 * 数据库编号
	 */
	private int num;
	/**
	 * rdb中的命令列表
	 */
	private List<String> command = new ArrayList<String>();

	public RedisDB(int num) {
		this.num = num;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	/**
	 * 添加redis命令的方法<br>
	 * 将命令对象添加到命令集合中
	 * 
	 * @param rdbCommand
	 *            redis命令对象
	 */
	public void addCommand(RedisRDBCommand rdbCommand) {
		command.addAll(rdbCommand.getCommands());
	}

	/**
	 * 获取rdb中所有需要同步的命令列表的方法
	 * 
	 * @return redis命令列表集合
	 */
	public List<String> getCommands() {
		return this.command;
	}

}
