package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ENCODING_INTSET;
import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_SET;
import static com.wmz7year.synyed.util.NumberUtil.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import com.wmz7year.synyed.exception.RedisRDBException;

/**
 * redis intset类型结构对象
 * 
 * @Title: RedisSetIntSet.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月18日 下午2:54:35
 * @version V1.0
 */
public class RedisSetIntSet extends RedisObject {

	/**
	 * intset数据
	 */
	private byte[] buffer;

	private ByteArrayInputStream bis = null;

	/**
	 * 每个元素的长度
	 */
	private int elementByteLength = 0;
	/**
	 * 元素数量
	 */
	private int elementCount = 0;

	/**
	 * 元素列表
	 */
	private List<Long> elements = new ArrayList<Long>();

	public RedisSetIntSet(byte[] buffer) throws RedisRDBException {
		this.buffer = buffer;
		this.bis = new ByteArrayInputStream(buffer);

		// 读取每个元素的长度
		this.elementByteLength = readElementByteLength();
		// 读取元素数量
		this.elementCount = readElementCount();

		// 读取元素
		readElements();
	}

	/**
	 * 读取元素列表的方法
	 * 
	 * @throws RedisRDBException
	 *             元素列表
	 */
	private void readElements() throws RedisRDBException {
		for (int i = 0; i < elementCount; i++) {
			byte[] buffer = new byte[elementByteLength];
			if (!readBytes(buffer, 0, elementByteLength)) {
				throw new RedisRDBException("解析错误");
			}
			// 转换为元素
			if (elementByteLength == 1) { // 8位数据
				elements.add((long) buffer[1]);
			} else if (elementByteLength == 2) { // 16位数据
				elements.add((long) byte216bitInt(buffer));
			} else if (elementByteLength == 4) { // 32位数据
				elements.add((long) byte2Int(buffer));
			} else if (elementByteLength == 8) { // 64数据
				elements.add(byte2Long(buffer));
			} else {
				throw new RedisRDBException("不识别的元素长度：" + elementByteLength);
			}
		}
	}

	/**
	 * 读取每个元素占位长度符号的方法
	 * 
	 * @return 每个元素的占位长度符号
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private int readElementByteLength() throws RedisRDBException {
		byte[] buffer = new byte[4];
		if (!readBytes(buffer, 0, 4)) {
			throw new RedisRDBException("解析错误");
		}
		return byte2Int(buffer);
	}

	/**
	 * 读取元素数量的方法
	 * 
	 * @return 元素数量
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private int readElementCount() throws RedisRDBException {
		byte[] buffer = new byte[4];
		if (!readBytes(buffer, 0, 4)) {
			throw new RedisRDBException("解析错误");
		}
		return byte2Int(buffer);
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getType()
	 */
	@Override
	public byte getType() {
		return REDIS_SET;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getEncoding()
	 */
	@Override
	public byte getEncoding() {
		return REDIS_ENCODING_INTSET;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#toCommand()
	 */
	@Override
	public String toCommand() {
		StringBuilder result = new StringBuilder();
		for (Long element : elements) {
			result.append(element).append(' ');
		}
		if (result.length() > 0 && result.charAt(result.length() - 1) == ' ') {
			return result.substring(0, result.length() - 1);
		}
		return result.toString();
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getBuffer()
	 */
	@Override
	public byte[] getBuffer() {
		return this.buffer;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisSetIntSet [buffer length=" + buffer.length + ",command=" + toCommand() + "]";
	}

	/**
	 * 读取一个byte数组的方法
	 * 
	 * @param buf
	 *            需要读取的byte数组
	 * @param start
	 *            读取的起始位
	 * @param num
	 *            读取数量
	 * @return true为读取成功 false为读取失败
	 * @throws RedisRDBException
	 *             读取过程中可能出现的异常
	 */
	private boolean readBytes(byte[] buf, int start, int num) throws RedisRDBException {
		if (num < 0) {
			throw new RedisRDBException("Num must bigger than zero");
		}
		if (start < 0) {
			throw new RedisRDBException("Start must bigger than zero");
		}
		if (num > buf.length) {
			throw new RedisRDBException("Num must less than buf length");
		}
		int flag = bis.read(buf, start, num);
		return flag == num;
	}

	public int getElementCount() {
		return this.elementCount;
	}
}
