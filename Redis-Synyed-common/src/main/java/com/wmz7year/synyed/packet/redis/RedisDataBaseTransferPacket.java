package com.wmz7year.synyed.packet.redis;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.wmz7year.synyed.exception.RedisRDBException;
import com.wmz7year.synyed.parser.RDBParser;
import com.wmz7year.synyed.parser.RDBParserFactory;
import com.wmz7year.synyed.parser.entry.RedisDB;

/**
 * 处理Redis数据文件传输的数据包对象<br>
 * 
 * @Title: RedisDataBaseTransferPacket.java
 * @Package com.wmz7year.synyed.packet.redis
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月11日 下午6:30:46
 * @version V1.0
 */
public class RedisDataBaseTransferPacket extends RedisPacket {
	/**
	 * rdb文件对象
	 */
	private File rdbFile;
	/**
	 * rdb文件解析器对象
	 */
	private RDBParser rdbParser;
	/**
	 * rdb文件内容
	 */
	byte[] rdbContent = null;

	public RedisDataBaseTransferPacket(String command, File rdbFile) throws RedisRDBException {
		super(command);
		this.rdbFile = rdbFile;

		try {
			rdbContent = FileUtils.readFileToByteArray(rdbFile);
		} catch (IOException e) {
			throw new RedisRDBException(e);
		}

		// 创建解析器
		createParser();
		// 读取正文内容
		parserContent();
	}

	/**
	 * 创建rdb文件解析器的方法
	 * 
	 * @throws RedisRDBException
	 *             当发生错误时抛出该异常
	 */
	private void createParser() throws RedisRDBException {
		byte[] rdbHeader = new byte[9];
		System.arraycopy(rdbContent, 0, rdbHeader, 0, 9);
		rdbParser = RDBParserFactory.createRDBParser(rdbHeader);
	}

	/**
	 * 读取正文内容的方法
	 * 
	 * @throws RedisRDBException
	 *             当发生错误时抛出该异常
	 */
	private void parserContent() throws RedisRDBException {
		rdbParser.parse(rdbContent);
	}

	/**
	 * 获取解析出的redis 数据库对象列表的方法
	 * 
	 * @return redis数据库对象列表集合
	 */
	public Collection<RedisDB> getRedisDbs() {
		return this.rdbParser.getRedisDBs();
	}

	public File getRdbFile() {
		return rdbFile;
	}

	public void setRdbFile(File rdbFile) {
		this.rdbFile = rdbFile;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisDataBaseTransferPacket [rdbFile=" + rdbFile + "]";
	}

}
