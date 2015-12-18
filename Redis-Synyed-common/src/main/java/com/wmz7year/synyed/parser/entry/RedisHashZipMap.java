package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ENCODING_ZIPLIST;
import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_HASH;
import static com.wmz7year.synyed.util.NumberUtil.byte2Int;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.wmz7year.synyed.exception.RedisRDBException;

/**
 * redis hash zipmap类型数据结构对象
 * 
 * @Title: RedisHashZipMap.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月18日 上午11:30:08
 * @version V1.0
 */
public class RedisHashZipMap extends RedisObject {
	/**
	 * 空格占位符
	 */
	private static final byte FREE = 0x00;

	/**
	 * hash zipmap数据
	 */
	private byte[] buffer;

	private ByteArrayInputStream bis = null;

	/**
	 * zip map中的元素长度
	 */
	private int zmLen = 0;
	/**
	 * 当前所包含的元素数量
	 */
	private int entryCount = 0;

	/**
	 * 解析出的元素列表
	 */
	private Map<String, String> elements = new HashMap<String, String>();

	public RedisHashZipMap(byte[] buffer) throws RedisRDBException {
		this.buffer = buffer;
		this.bis = new ByteArrayInputStream(buffer);

		// 读取第1个字节 如果该字节为 0xFE说明该元素长度是不准确的
		// 需要遍历所有元素长度才可以得到准确的长度
		this.zmLen = readByte();

		// 读取元素数量
		this.entryCount = readEntryLength();

		// 读取元素
		readEntries();

		// 校验末尾符号
		byte lastCheckFlag = readByte();
		if (lastCheckFlag != -1) {
			throw new RedisRDBException("末尾校验符不匹配");
		}
	}

	/**
	 * 读取元素列表的方法
	 * 
	 * @throws RedisRDBException
	 *             读取过程中出现问题则抛出该异常
	 */
	private void readEntries() throws RedisRDBException {
		for (int i = 0; i < entryCount; i++) {
			int keyLength = readLength();
			String key = readKey(keyLength);
			int valueLength = readLength();
			byte checkFlag = readByte();
			if (checkFlag != FREE) {
				throw new RedisRDBException("检查符不匹配");
			}
			String value = readValue(valueLength);
			elements.put(key, value);
		}
	}

	/**
	 * 根据指定长度读取key的方法
	 * 
	 * @param keyLength
	 *            key的长度
	 * @return 读取到的key
	 * @throws RedisRDBException
	 *             当读取发生问题时抛出该异常
	 */
	private String readKey(int keyLength) throws RedisRDBException {
		byte[] buffer = new byte[keyLength];
		if (!readBytes(buffer, 0, keyLength)) {
			throw new RedisRDBException("解析错误");
		}
		return new String(buffer);
	}

	/**
	 * 根据指定长度读取value的方法
	 * 
	 * @param valueLength
	 *            value的长度
	 * @return 读取到的key
	 * @throws RedisRDBException
	 *             当读取发生问题时抛出该异常
	 */
	private String readValue(int valueLength) throws RedisRDBException {
		byte[] buffer = new byte[valueLength];
		if (!readBytes(buffer, 0, valueLength)) {
			throw new RedisRDBException("解析错误");
		}
		return new String(buffer);
	}

	/**
	 * 读取元素数量的方法
	 * 
	 * @return 包含的元素数量
	 * @throws RedisRDBException
	 *             当解析发生问题时抛出该异常
	 */
	private int readEntryLength() throws RedisRDBException {
		return readLength();
	}

	/**
	 * 读取长度的方法<br>
	 * 先读取1个字节 如果该字节内容为0xFD 则接着向后读取4个字节转换为int作为长度
	 * 
	 * @return 读取到的长度
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private int readLength() throws RedisRDBException {
		byte b = readByte();
		if (b == 0xFD) {
			// 接着读取4个字节转换为int
			byte[] buffer = new byte[4];
			if (!readBytes(buffer, 0, 4)) {
				throw new RedisRDBException("解析错误");
			}
			return byte2Int(buffer);
		} else {
			return b;
		}
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getType()
	 */
	@Override
	public byte getType() {
		return REDIS_HASH;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getEncoding()
	 */
	@Override
	public byte getEncoding() {
		return REDIS_ENCODING_ZIPLIST;
	}

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#toCommand()
	 */
	@Override
	public String toCommand() {
		StringBuilder result = new StringBuilder();
		Iterator<Entry<String, String>> iterator = this.elements.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			result.append(entry.getKey()).append(' ').append(entry.getValue()).append(' ');
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
		return "RedisHashZipMap [buffer length=" + buffer.length + ",command=" + toCommand() + "]";
	}

	/**
	 * 读取1个字节的方法
	 * 
	 * @return 读取到的字节数据
	 * @throws RedisRDBException
	 *             当读取到结尾或者发生错误时抛出该异常
	 */
	private byte readByte() throws RedisRDBException {
		int data = bis.read();
		// 强转成byte
		byte b = (byte) data;
		return b;
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

	public int getZmLen() {
		return zmLen;
	}

	public int getElementCount() {
		return this.entryCount;
	}
}
