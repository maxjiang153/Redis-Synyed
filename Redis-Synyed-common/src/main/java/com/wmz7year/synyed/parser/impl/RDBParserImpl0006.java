package com.wmz7year.synyed.parser.impl;

import static com.wmz7year.synyed.constant.RedisRDBConstant.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.Redis.Synyed.util.LZFDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmz7year.synyed.exception.RedisRDBException;
import com.wmz7year.synyed.parser.RDBParser;
import com.wmz7year.synyed.parser.entry.RedisDB;
import com.wmz7year.synyed.parser.entry.RedisHashObject;
import com.wmz7year.synyed.parser.entry.RedisListObject;
import com.wmz7year.synyed.parser.entry.RedisObject;
import com.wmz7year.synyed.parser.entry.RedisRDBCommand;
import com.wmz7year.synyed.parser.entry.RedisSetObject;
import com.wmz7year.synyed.parser.entry.RedisStringObject;
import com.wmz7year.synyed.parser.entry.RedisZSetObject;
import com.wmz7year.synyed.parser.entry.RedisZipListObject;

/**
 * 针对0006版本的redis rdb数据文件解析器
 * 
 * @Title: RDBParserImpl0006.java
 * @Package com.wmz7year.synyed.parser.impl
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月14日 下午2:26:50
 * @version V1.0
 */
public class RDBParserImpl0006 implements RDBParser {
	private static Logger logger = LoggerFactory.getLogger(RDBParserImpl0006.class);
	/**
	 * rdb文件版本号
	 */
	private static final String VERSION = "0006";
	/**
	 * 数据文件数据流对象
	 */
	private ByteArrayInputStream bis;

	/**
	 * rdb文件中的 redis数据库集合列表<br>
	 * key为redis 数据库的编号 value为redis数据库对象实体类
	 */
	private Map<Integer, RedisDB> redisDBs = new HashMap<Integer, RedisDB>();

	/*
	 * @see com.wmz7year.synyed.parser.RDBParser#gerVersion()
	 */
	@Override
	public String gerVersion() {
		return VERSION;
	}

	/*
	 * @see com.wmz7year.synyed.parser.RDBParser#parse(byte[])
	 */
	@Override
	public void parse(final byte[] rdbContent) throws RedisRDBException {
		if (rdbContent == null) {
			throw new NullPointerException();
		}
		logger.info("rdb文件长度:" + rdbContent.length);
		// byte
		bis = new ByteArrayInputStream(rdbContent);
		// 校验版本是否正确
		checkVersion();

		// 开始解析rdb文件内容
		parseRDBContent();
	}

	/**
	 * 校验版本是否正确的方法<br>
	 * 
	 * @throws RedisRDBException
	 *             如果版本不正确则抛出该异常
	 */
	private void checkVersion() throws RedisRDBException {
		byte[] rdbHeader = new byte[9];
		try {
			int flag = bis.read(rdbHeader);
			if (flag != 9) {
				throw new RedisRDBException("rdb头信息错误");
			}
			String redisRDBHeader = new String(rdbHeader);
			if (!redisRDBHeader.startsWith("REDIS")) {
				throw new RedisRDBException("rdb头标识符必须为 REDIS " + Arrays.toString(rdbHeader));
			}
			String version = redisRDBHeader.substring(5, 9);
			if (!VERSION.equals(version)) {
				throw new RedisRDBException("不支持的RDB版本：" + version);
			}
		} catch (IOException e) {
			throw new RedisRDBException("头信息错误", e);
		}
	}

	/**
	 * 解析RDB正文内容的方法<br>
	 * 
	 * @throws RedisRDBException
	 *             当解析过程中出现问题抛出该异常
	 */
	private void parseRDBContent() throws RedisRDBException {
		// 数据类型
		byte type = 0;
		// 过期时间
		long expiretime = 0l;
		// 当前时间
		long now = System.currentTimeMillis();
		// 当前读取的redis数据库
		RedisDB redisDB = null;

		while (true) {
			// redis的key value对象
			RedisObject key, value;
			expiretime = -1;
			// 读取类型
			type = rdbLoadType();
			if (type == REDIS_RDB_OPCODE_EXPIRETIME) {
				// 读取过期时间 单位秒
				expiretime = rdbLoadTime();
				// 获取一下数据类型
				type = rdbLoadType();
				// 过期时间秒转换为毫秒
				expiretime *= 1000;

			} else if (type == REDIS_RDB_OPCODE_EXPIRETIME_MS) {
				expiretime = rdbLoadMillisecondTime();

				// 获取一下数据类型
				type = rdbLoadType();
			}

			// 如果数据读取完了则退出循环
			if (type == REDIS_RDB_OPCODE_EOF) {
				break;
			}

			// 处理选择数据库符号
			if (type == REDIS_RDB_OPCODE_SELECTDB) {
				int dbID = rdbLoadLen().getLen();
				redisDB = redisDBs.get(dbID);
				if (redisDB == null) {
					redisDB = new RedisDB(dbID);
					redisDBs.put(dbID, redisDB);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("切换到数据库：" + dbID);
				}
				continue;
			}

			// 读取key
			key = rdbLoadStringObject();
			// 读取value
			value = rdbLoadObject(type);

			// 校验过期时间 如果数据过期则不处理
			if (expiretime != -1 && expiretime < now) {
				continue;
			}

			// 转换为rdb command对象
			RedisRDBCommand rdbCommand = new RedisRDBCommand(key, value, expiretime);

			// 添加到rdb中
			redisDB.addCommand(rdbCommand);
		}
		// 校验8字节的CRC
		byte[] crcBuf = new byte[8];
		if (!readBytes(crcBuf, 0, 8)) {
			throw new RedisRDBException("CRC校验值读取异常");
		}
		// TODO crc check
	}

	/**
	 * 读取redis指定类型对象的方法
	 * 
	 * @param type
	 *            对象类型
	 * @return redis对象
	 * @throws RedisRDBException
	 *             解析过程中发生错误则抛出该异常信息
	 */
	private RedisObject rdbLoadObject(byte type) throws RedisRDBException {
		RedisObject result = null;
		if (type == REDIS_RDB_TYPE_STRING) { // 读取string类型值
			result = rdbLoadStringValue();
		} else if (type == REDIS_RDB_TYPE_LIST) { // 读取list类型的值
			result = rdbLoadListValue();
		} else if (type == REDIS_RDB_TYPE_SET) { // 读取set类型的值
			result = rdbLoadSetValue();
		} else if (type == REDIS_RDB_TYPE_ZSET) { // 读取list/set类型的值
			result = rdbLoadZSetValue();
		} else if (type == REDIS_RDB_TYPE_HASH) { // 读取hash类型的值
			result = rdbLoadHashValue();
		} else if (type == REDIS_RDB_TYPE_HASH_ZIPMAP || type == REDIS_RDB_TYPE_LIST_ZIPLIST
				|| type == REDIS_RDB_TYPE_SET_INTSET || type == REDIS_RDB_TYPE_ZSET_ZIPLIST
				|| type == REDIS_RDB_TYPE_HASH_ZIPLIST) {
			RedisObject aux = rdbLoadStringObject();
			byte[] buffer = aux.getBuffer();
			switch (type) {
			case REDIS_RDB_TYPE_HASH_ZIPMAP:
				System.out.println("REDIS_RDB_TYPE_HASH_ZIPMAP");
				// TODO
				break;
			case REDIS_RDB_TYPE_LIST_ZIPLIST:
				result = new RedisZipListObject(buffer);
				break;
			case REDIS_RDB_TYPE_SET_INTSET:
				System.out.println("REDIS_RDB_TYPE_SET_INTSET");
				// TODO
				break;
			case REDIS_RDB_TYPE_ZSET_ZIPLIST:
				System.out.println("REDIS_RDB_TYPE_ZSET_ZIPLIST");
				// TODO
				break;
			case REDIS_RDB_TYPE_HASH_ZIPLIST:
				System.out.println("REDIS_RDB_TYPE_HASH_ZIPLIST");
				// TODO
				break;
			default:
				throw new RedisRDBException("未知的RDB数据类型：" + type);
			}
			System.out.println("集合对象");
		} else {
			throw new RedisRDBException("未知的类型：" + type);
		}
		return result;
	}

	/**
	 * 读取string类型值转换为redis数据对象的方法
	 * 
	 * @return redis对象
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private RedisObject rdbLoadStringValue() throws RedisRDBException {
		return rdbLoadEncodedStringObject();
	}

	/**
	 * 读取list类型值转换为redis数据对象的方法
	 * 
	 * @return redis对象
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private RedisObject rdbLoadListValue() throws RedisRDBException {
		// 读取list元素的长度
		int len = rdbLoadLen().getLen();

		RedisListObject redisListObject = new RedisListObject();

		// 依次解析每一个元素
		while (len-- != 0) {
			RedisObject ele = rdbLoadEncodedStringObject();
			// 添加到对象中
			redisListObject.addElement(ele);
		}
		return redisListObject;
	}

	/**
	 * 读取set类型值转换为redis数据对象的方法
	 * 
	 * @return redis对象
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private RedisObject rdbLoadSetValue() throws RedisRDBException {
		// 读取set元素的长度
		int len = rdbLoadLen().getLen();

		RedisSetObject redisSetObject = new RedisSetObject();
		// 读取set的每一个元素
		for (int i = 0; i < len; i++) {
			RedisObject ele = rdbLoadEncodedStringObject();
			redisSetObject.addElement(ele);
		}
		return redisSetObject;
	}

	/**
	 * 读取zset类型值转换为redis数据对象的方法
	 * 
	 * @return redis对象
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private RedisObject rdbLoadZSetValue() throws RedisRDBException {
		// zset长度
		int zsetlen = rdbLoadLen().getLen();
		RedisZSetObject redisZSetObject = new RedisZSetObject(zsetlen);

		// 依次解析每一个元素
		while (zsetlen-- != 0) {
			// 读取元素内容
			RedisObject ele = rdbLoadEncodedStringObject();
			double score = rdbLoadDoubleValue();
			redisZSetObject.addElement(ele, score);
		}
		return redisZSetObject;
	}

	/**
	 * 读取hash类型的值转换为redis数据对象的方法
	 * 
	 * @return redis数据对象
	 * @throws RedisRDBException
	 *             当读取发生错误时抛出该异常
	 */
	private RedisObject rdbLoadHashValue() throws RedisRDBException {
		int len = rdbLoadLen().getLen();

		RedisHashObject redisHashObject = new RedisHashObject();

		while (len > 0) {
			RedisObject field = null;
			RedisObject value = null;

			len--;

			// 读取field value
			field = rdbLoadStringObject();
			value = rdbLoadStringObject();
			redisHashObject.addElement(field, value);

		}
		return redisHashObject;
	}

	/**
	 * 读取double值的方法
	 * 
	 * @return double值
	 * @throws RedisRDBException
	 *             当读取过程中发生问题则抛出该异常
	 */
	private double rdbLoadDoubleValue() throws RedisRDBException {
		byte[] buf = new byte[256];
		int len = 0;
		double val = 0;
		// 读取第一个字节
		if (!readBytes(buf, 0, 1)) {
			throw new RedisRDBException("解析错误");
		}
		len = buf[0] & 0xFF;
		switch (len) {
		case 255:
			return R_NegInf;
		case 254:
			return R_PosInf;
		case 253:
			return R_Nan;
		default:
			if (!readBytes(buf, 0, len)) {
				throw new RedisRDBException("解析错误");
			}
			buf[len] = '\0';
			val = Double.parseDouble(new String(buf));
			return val;
		}
	}

	/**
	 * 读取redis普通string编码对象数据的方法
	 * 
	 * @return redis对象
	 * @throws RedisRDBException
	 *             当读取过程中发生错误则抛出该异常信息
	 */
	private RedisObject rdbLoadStringObject() throws RedisRDBException {
		return rdbGenericLoadStringObject(false);
	}

	/**
	 * 读取redis编码后字符串对象数据的方法
	 * 
	 * @return redis对象
	 * @throws RedisRDBException
	 *             当读取过程中发生错误则抛出该异常信息
	 */
	private RedisObject rdbLoadEncodedStringObject() throws RedisRDBException {
		return rdbGenericLoadStringObject(true);
	}

	/**
	 * 通用读取字符串编码对象的方法
	 * 
	 * @param isEncode
	 *            是否编码的表识位
	 * @return redis对象
	 * @throws RedisRDBException
	 *             当读取过程中发生错误则抛出该异常信息
	 */
	private RedisObject rdbGenericLoadStringObject(boolean isEncode) throws RedisRDBException {
		byte[] val = null;
		RdbLen len = rdbLoadLen();
		if (len.isEncoded()) {
			switch (len.getLen()) {
			case REDIS_RDB_ENC_INT8:
			case REDIS_RDB_ENC_INT16:
			case REDIS_RDB_ENC_INT32:
				return rdbLoadIntegerObject(len.getLen());
			case REDIS_RDB_ENC_LZF:
				return rdbLoadLzfStringObject();
			default:
				throw new RedisRDBException("Unknown RDB encoding type");
			}
		}
		if (len.getLen() == REDIS_RDB_LENERR) {
			throw new RedisRDBException("RDB parser error");
		}
		val = new byte[len.getLen()];
		if (!readBytes(val, 0, val.length)) {
			throw new RedisRDBException("RDB read value error");
		}
		return createRedisStringObject(val);
	}

	/**
	 * 读取整数类型数据编码对象的方法
	 * 
	 * @param enctype
	 *            数据类型
	 * @return Redis对象
	 * @throws RedisRDBException
	 *             当读取过程中发生错误则抛出该异常信息
	 */
	private RedisObject rdbLoadIntegerObject(int enctype) throws RedisRDBException {
		byte[] enc = new byte[4];
		long val;

		if (enctype == REDIS_RDB_ENC_INT8) {
			if (!readBytes(enc, 0, 1)) {
				throw new RedisRDBException("解析错误");
			}
			val = enc[0];
		} else if (enctype == REDIS_RDB_ENC_INT16) {
			int v = 0;
			if (!readBytes(enc, 0, 2)) {
				throw new RedisRDBException("解析错误");
			}
			v = (enc[0] & 0xFF) | (enc[1] << 8);
			val = v;
		} else if (enctype == REDIS_RDB_ENC_INT32) {
			int v = 0;
			if (!readBytes(enc, 0, 4)) {
				throw new RedisRDBException("解析错误");
			}
			v = (enc[0] & 0xFF) | (enc[1] << 8) | (enc[2] << 16) | (enc[3] << 24);
			val = v;
		} else {
			val = 0; /* anti-warning */
			throw new RedisRDBException("Unknown RDB integer encoding type");
		}
		return createRedisStringObject(String.valueOf(val).getBytes());
	}

	/**
	 * 读取LZF压缩格式的字符串的方法
	 * 
	 * @return redis数据对象
	 * @throws RedisRDBException
	 *             当解析出现问题时抛出该异常
	 */
	private RedisObject rdbLoadLzfStringObject() throws RedisRDBException {
		int len, clen;
		byte[] buf = null;
		byte[] val = null;
		clen = rdbLoadLen().getLen();
		len = rdbLoadLen().getLen();

		buf = new byte[clen];
		if (!readBytes(buf, 0, clen)) {
			throw new RedisRDBException("解析错误");
		}
		// lzf解压缩
		try {
			val = new byte[len];
			LZFDecoder.decode(buf, 0, clen, val, 0, len);
		} catch (Exception e) {
			throw new RedisRDBException("LZF解压数据失败", e);
		}

		return createRedisStringObject(val);
	}

	/**
	 * 创建RedisStringObject对象的方法
	 * 
	 * @param buffer
	 *            redis对象数据
	 * @return redis对象
	 */
	private RedisObject createRedisStringObject(byte[] buffer) {
		RedisObject obj = new RedisStringObject(buffer);
		return obj;
	}

	/**
	 * 读取数据类型的方法
	 * 
	 * @return 数据类型
	 * @throws RedisRDBException
	 *             当读取出现问题时抛出该异常
	 */
	private byte rdbLoadType() throws RedisRDBException {
		byte type = readByte();
		return type;
	}

	/**
	 * 加载时间的方法<br>
	 * 读取4个字节数据转换为int类型的值<br>
	 * 该时间单位为秒
	 * 
	 * @return 读取出的时间
	 * @throws RedisRDBException
	 *             读取过程中出现错误则抛出该异常
	 */
	private long rdbLoadTime() throws RedisRDBException {
		byte[] buffer = new byte[4];
		boolean result = readBytes(buffer, 0, buffer.length);
		if (!result) {
			throw new RedisRDBException("Load time error");
		}
		return byte2Int(buffer);
	}

	/**
	 * 加载时间的方法<br>
	 * 读取8个字节数据转换为long类型的值<br>
	 * 该时间单位为毫秒
	 * 
	 * @return 读取出的时间
	 * @throws RedisRDBException
	 *             读取过程中出现错误则抛出该异常
	 */
	private long rdbLoadMillisecondTime() throws RedisRDBException {
		byte[] buffer = new byte[8];
		boolean result = readBytes(buffer, 0, buffer.length);
		if (!result) {
			throw new RedisRDBException("Load millisecond time error");
		}
		return byte2Long(buffer);
	}

	/**
	 * 读取数据长度的方法<br>
	 * 
	 * @return 数据的长度对象
	 * @throws RedisRDBException
	 *             读取过程中可能出现的异常
	 */
	private RdbLen rdbLoadLen() throws RedisRDBException {
		RdbLen result = new RdbLen(false, 0);
		byte[] buf = new byte[2];
		int type = 0;

		if (!readBytes(buf, 0, 1)) {
			result.setLen(REDIS_RDB_LENERR);
			return result;
		}
		type = (buf[0] & 0xC0) >> 6;
		if (type == REDIS_RDB_ENCVAL) {
			/* Read a 6 bit encoding type. */
			result.setEncoded(true);
			result.setLen(buf[0] & 0x3F);
			return result;
		} else if (type == REDIS_RDB_6BITLEN) {
			/* Read a 6 bit len. */
			result.setLen(buf[0] & 0x3F);
			return result;
		} else if (type == REDIS_RDB_14BITLEN) {
			/* Read a 14 bit len. */
			if (!readBytes(buf, 1, 1)) {
				result.setLen(REDIS_RDB_LENERR);
				return result;
			}
			result.setLen(((buf[0] & 0x3F) << 8) | buf[1] & 0xFF);
			return result;
		} else {
			/* Read a 32 bit len. */
			byte[] intBuf = new byte[4];
			if (!readBytes(intBuf, 0, 4)) {
				result.setLen(REDIS_RDB_LENERR);
				return result;
			}
			result.setLen(byte2Int(intBuf));
			return result;
		}
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

	/**
	 * byte数组转换为int值的方法<br>
	 * 
	 * 
	 * @param buffer
	 *            需要转换的byte数组
	 * @return 转换后的int值
	 * @throws RedisRDBException
	 *             当参数不正确时会抛出该异常
	 */
	private int byte2Int(byte[] buffer) throws RedisRDBException {
		if (buffer == null) {
			throw new NullPointerException();
		}
		if (buffer.length != 4) {
			throw new RedisRDBException("Error buffer length can not cover bytes to int:" + Arrays.toString(buffer));
		}
		return (((buffer[3]) << 24) | ((buffer[2] & 0xff) << 16) | ((buffer[1] & 0xff) << 8) | ((buffer[0] & 0xff)));
	}

	/**
	 * byte数组转换为long值的方法
	 * 
	 * @param buffer
	 *            需要转换的byte数组
	 * @return 转换后的long类型值
	 * @throws RedisRDBException
	 *             当参数不正确时会抛出该异常
	 */
	private long byte2Long(byte[] buffer) throws RedisRDBException {
		if (buffer == null) {
			throw new NullPointerException();
		}
		if (buffer.length != 8) {
			throw new RedisRDBException("Error buffer length can not cover bytes to int:" + Arrays.toString(buffer));
		}
		return ((((long) buffer[7]) << 56) | (((long) buffer[6] & 0xff) << 48) | (((long) buffer[5] & 0xff) << 40)
				| (((long) buffer[4] & 0xff) << 32) | (((long) buffer[3] & 0xff) << 24)
				| (((long) buffer[2] & 0xff) << 16) | (((long) buffer[1] & 0xff) << 8) | (((long) buffer[0] & 0xff)));
	}

	/**
	 * 封装rdb数据长度已经是否编码的实体类
	 * 
	 * @Title: RDBParserImpl0006.java
	 * @Package com.wmz7year.synyed.parser.impl
	 * @author jiangwei (ydswcy513@gmail.com)
	 * @date 2015年12月15日 下午11:00:18
	 * @version V1.0
	 */
	private class RdbLen {
		/**
		 * 是否编码的表识别位
		 */
		private boolean isEncoded;
		/**
		 * 数据长度
		 */
		private int len;

		public RdbLen(boolean isEncoded, int len) {
			this.isEncoded = isEncoded;
			this.len = len;
		}

		public void setEncoded(boolean isEncoded) {
			this.isEncoded = isEncoded;
		}

		public boolean isEncoded() {
			return isEncoded;
		}

		public void setLen(int len) {
			this.len = len;
		}

		public int getLen() {
			return this.len;
		}
	}
}
