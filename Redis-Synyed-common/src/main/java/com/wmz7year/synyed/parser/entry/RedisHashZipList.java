package com.wmz7year.synyed.parser.entry;

import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ENCODING_ZIPLIST;
import static com.wmz7year.synyed.constant.RedisRDBConstant.REDIS_ZSET;
import static com.wmz7year.synyed.util.NumberUtil.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import com.wmz7year.synyed.entity.RedisCommandData;
import com.wmz7year.synyed.exception.RedisRDBException;

/**
 * redis hash ziplist编码类型数据结构对象
 * 
 * @Title: RedisHashZipList.java
 * @Package com.wmz7year.synyed.parser.entry
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月18日 下午3:34:53
 * @version V1.0
 */
public class RedisHashZipList extends RedisObject {
	/**
	 * 代表zlist结尾
	 */
	private static final byte ZLEND = (byte) 0xFF;

	private byte[] buffer;

	private ByteArrayInputStream bis = null;
	/**
	 * ziplist中元素的数量
	 */
	private int entryCount;

	/**
	 * ziplist解析出的元素
	 */
	private List<RedisCommandData> elements = new ArrayList<RedisCommandData>();
	/**
	 * 当前元素读取的字节数
	 */
	private int elementReadLength;

	public RedisHashZipList(byte[] buffer) throws RedisRDBException {
		this.buffer = buffer;

		bis = new ByteArrayInputStream(buffer);

		// 检查ziplist的数据完整性
		checkZLBytes();

		// 获取zltail 目前没发现用处
		getZipListZLTail();

		// 获取元素的数量
		this.entryCount = getEntryCount();

		// 处理读取元素内容
		processReadEntries();

		// 校验是否读完了
		if (readByte() != ZLEND) {
			throw new RedisRDBException("ziplist解析错误");
		}
	}

	/**
	 * 校验ziplist数据完整性的方法<br>
	 * 读取4个字节小端法转换为1个int 然后比对数据的长度是否一样
	 * 
	 * @throws RedisRDBException
	 *             当发生问题时抛出该异常
	 */
	private void checkZLBytes() throws RedisRDBException {
		byte[] buf = new byte[4];
		if (!readBytes(buf, 0, 4)) {
			throw new RedisRDBException("解析错误");
		}
		int zlbytes = byte2Int(buf);
		if (zlbytes != buffer.length) {
			throw new RedisRDBException("错误长度的ziplist数据");
		}
	}

	/**
	 * 获取ziplist元素开始位置id方法
	 * 
	 * @return ziplist元素开始位置
	 * @throws RedisRDBException
	 *             出现问题时抛出该异常
	 */
	private int getZipListZLTail() throws RedisRDBException {
		byte[] buf = new byte[4];
		if (!readBytes(buf, 0, 4)) {
			throw new RedisRDBException("解析错误");
		}
		int zlTail = byte2Int(buf);
		return zlTail;
	}

	/**
	 * 获取ziplist中元素数量的方法
	 * 
	 * @return ziplist中元素数量
	 * @throws RedisRDBException
	 *             出现问题时抛出该异常
	 */
	private int getEntryCount() throws RedisRDBException {
		byte[] buf = new byte[4];
		// 只读取两个字节
		if (!readBytes(buf, 0, 2)) {
			throw new RedisRDBException("解析错误");
		}
		int entryCount = byte2Int(buf);
		return entryCount;
	}

	/**
	 * 处理读取ziplist元素内容的方法
	 * 
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private void processReadEntries() throws RedisRDBException {
		// 循环获取每一个元素
		for (int i = 0; i < entryCount; i++) {
			// 上一个元素的长度
			int perEntryLength = readPerEntryLength();
			if (perEntryLength != 0) { // 如果长度为0则说明是第一个元素
				// -1是为了减去刚刚读取的1位长度
				if ((elementReadLength - 1) != perEntryLength) {
					throw new RedisRDBException("redis元素解析错误");
				} else {
					elementReadLength = 1;
				}
			}
			// 读取符号
			byte entrySpecialFlag = readEntrySpecialFlag();
			// 读取元素长度
			RedisCommandData entry = readEntry(entrySpecialFlag);
			elements.add(entry);
		}
	}

	/**
	 * 读取元素内容的方法<br>
	 * 
	 * <pre>
	 * 	Special flag : This flag indicates whether the entry is a string or an integer. 
	 *  It also indicates the length of the string, or the size of the integer. 
	 *  The various encodings of this flag are shown below :
	 *  |00pppppp| – 1 byte : String value with length less than or equal to 63 bytes (6 bits).
	 *  |01pppppp|qqqqqqqq| – 2 bytes : String value with length less than or equal to 16383 bytes (14 bits).
	 *  |10______|qqqqqqqq|rrrrrrrr|ssssssss|tttttttt| – 5 bytes : String value with length greater than or equal to 16384 bytes.
	 *  |1100____| – Read next 2 bytes as a 16 bit signed integer
	 *  |1101____| – Read next 4 bytes as a 32 bit signed integer
	 *  |1110____| – Read next 8 bytes as a 64 bit signed integer
	 *  |11110000| – Read next 3 bytes as a 24 bit signed integer
	 *  |11111110| – Read next byte as an 8 bit signed integer
	 *  |1111xxxx| – (with xxxx between 0000 and 1101) immediate 4 bit integer. 
	 *  Unsigned integer from 0 to 12. The encoded value is actually from 1 to 13 because 0000 
	 *  and 1111 can not be used, so 1 should be subtracted from the encoded 4 bit value to obtain the right value.
	 * 
	 * </pre>
	 * 
	 * @param readEntrySpecialFlag
	 *            元素长度符号标识位
	 * @return 元素的长度
	 * @throws RedisRDBException
	 *             读取发生错误时抛出该异常
	 */
	private RedisCommandData readEntry(byte readEntrySpecialFlag) throws RedisRDBException {
		byte bit7 = (byte) ((readEntrySpecialFlag >> 7) & 0x1);
		byte bit6 = (byte) ((readEntrySpecialFlag >> 6) & 0x1);
		byte bit5 = (byte) ((readEntrySpecialFlag >> 5) & 0x1);
		byte bit4 = (byte) ((readEntrySpecialFlag >> 4) & 0x1);
		byte bit3 = (byte) ((readEntrySpecialFlag >> 3) & 0x1);
		byte bit2 = (byte) ((readEntrySpecialFlag >> 2) & 0x1);
		byte bit1 = (byte) ((readEntrySpecialFlag >> 1) & 0x1);
		byte bit0 = (byte) ((readEntrySpecialFlag >> 0) & 0x1);

		if (bit7 == 0 && bit6 == 0) { // 6bit字符串
			int length = (byte) ((bit5 << 5) + (bit4 << 4) + (bit3 << 3) + (bit2 << 2) + (bit1 << 1) + (bit0 << 0));
			byte[] buffer = new byte[length];
			if (!readBytes(buffer, 0, length)) {
				throw new RedisRDBException("解析错误");
			}
			elementReadLength += length;
			return new RedisCommandData(buffer);
		} else if (bit7 == 0 && bit6 == 1) { // 2bytes 14bit字符串
			int length = ((((bit3 << 3) + (bit2 << 2) + (bit1 << 1) + (bit0 << 0) - 1)) << 8) | readByte() & 0xFF;
			elementReadLength++;
			byte[] buffer = new byte[length];
			if (!readBytes(buffer, 0, length)) {
				throw new RedisRDBException("解析错误");
			}
			elementReadLength += length;
			return new RedisCommandData(buffer);
		} else if (bit7 == 1 && bit6 == 0) { // 5bytes 字符串长度大于16384
			byte[] buffer = new byte[4];
			if (!readBytes(buffer, 0, 4)) {
				throw new RedisRDBException("解析错误");
			}
			elementReadLength += 4;
			int len = byte2Int(buffer);
			buffer = new byte[len];
			if (!readBytes(buffer, 0, len)) {
				throw new RedisRDBException("解析错误");
			}
			return new RedisCommandData(buffer);
		} else if (bit7 == 1 && bit6 == 1 && bit5 == 0 && bit4 == 0) { // 16bit整数
			byte[] buffer = new byte[2];
			if (!readBytes(buffer, 0, 2)) {
				throw new RedisRDBException("解析错误");
			}
			elementReadLength += 2;
			int result = byte216bitInt(buffer);
			return new RedisCommandData(String.valueOf(result).getBytes());
		} else if (bit7 == 1 && bit6 == 1 && bit5 == 0 && bit4 == 1) { // 32bit整数
			byte[] buffer = new byte[4];
			if (!readBytes(buffer, 0, 4)) {
				throw new RedisRDBException("解析错误");
			}
			elementReadLength += 4;
			int result = byte2Int(buffer);
			return new RedisCommandData(String.valueOf(result).getBytes());
		} else if (bit7 == 1 && bit6 == 1 && bit5 == 1 && bit4 == 0) { // 64bit整数
			byte[] buffer = new byte[8];
			if (!readBytes(buffer, 0, 8)) {
				throw new RedisRDBException("解析错误");
			}
			elementReadLength += 8;
			long result = byte2Long(buffer);
			return new RedisCommandData(String.valueOf(result).getBytes());
		} else if (bit7 == 1 && bit6 == 1 && bit5 == 1 && bit4 == 1 && bit3 == 0 && bit2 == 0 && bit1 == 0
				&& bit0 == 0) { // 24bit整数
			byte[] buffer = new byte[3];
			if (!readBytes(buffer, 0, 3)) {
				throw new RedisRDBException("解析错误");
			}
			elementReadLength += 3;
			int result = byte224bitInt(buffer);
			return new RedisCommandData(String.valueOf(result).getBytes());
		} else if (bit7 == 1 && bit6 == 1 && bit5 == 1 && bit4 == 1 && bit3 == 1 && bit2 == 1 && bit1 == 1
				&& bit0 == 0) { // 8bit整数
			byte result = readByte();
			elementReadLength++;
			return new RedisCommandData(String.valueOf(result).getBytes());
		} else if (bit7 == 1 && bit6 == 1 && bit5 == 1 && bit4 == 1) { // 4bit整数数据
			byte result = (byte) ((bit3 << 3) + (bit2 << 2) + (bit1 << 1) + (bit0 << 0) - 1);
			return new RedisCommandData(String.valueOf(result).getBytes());
		} else {
			throw new RedisRDBException("不支持的entry special符号");
		}
	}

	/**
	 * 读取entry special符号
	 * 
	 * @return special符号
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private byte readEntrySpecialFlag() throws RedisRDBException {
		elementReadLength++;
		return readByte();
	}

	/**
	 * 读取上一个元素的长度的方法
	 * 
	 * @return 上一个元素的长度 如果返回值为0则说明是第一个元素
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private int readPerEntryLength() throws RedisRDBException {
		elementReadLength++;
		return readByte();
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

	/*
	 * @see com.wmz7year.synyed.parser.entry.RedisObject#getType()
	 */
	@Override
	public byte getType() {
		return REDIS_ZSET;
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
		for (RedisCommandData element : elements) {
			result.append(element.getContent()).append(' ');
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

	/**
	 * 获取list中元素数量的方法
	 * 
	 * @return list中元素数量
	 */
	public int getElementCount() {
		return this.entryCount;
	}

	/**
	 * 获取元素列表的方法
	 * 
	 * @return 元素列表集合
	 */
	public List<RedisCommandData> getElements() {
		return this.elements;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisHashZipList [buffer length=" + buffer.length + ",command=" + toCommand() + "]";
	}
}