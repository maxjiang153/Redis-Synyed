package com.wmz7year.synyed.constant;

/**
 * rdb解析用的常量封装类
 * 
 * @Title: RedisRDBConstant.java
 * @Package com.wmz7year.synyed.constant
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月15日 上午11:25:15
 * @version V1.0
 */
public class RedisRDBConstant {
	public static final double R_Zero = 0.0, R_PosInf = 1.0 / R_Zero, R_NegInf = -1.0 / R_Zero, R_Nan = R_Zero / R_Zero;

	/**
	 * redis key 过期时间 秒
	 */
	public static final byte REDIS_RDB_OPCODE_EXPIRETIME = (byte) 0xFD;
	/**
	 * redis key 过期时间 毫秒
	 */
	public static final byte REDIS_RDB_OPCODE_EXPIRETIME_MS = (byte) 0xFC;
	/**
	 * rdb文件中选择数据库的符号<br>
	 * 该编号后的下一个字节为数据库编号
	 */
	public static final byte REDIS_RDB_OPCODE_SELECTDB = (byte) 0xFE;
	/**
	 * rdb结尾检查符号<br>
	 * 之后跟着8个字节进行CRC64校验
	 */
	public static final byte REDIS_RDB_OPCODE_EOF = (byte) 0xFF;

	/*
	 * Defines related to the dump file format. To store 32 bits lengths for
	 * short keys requires a lot of space, so we check the most significant 2
	 * bits of the first byte to interpreter the length:
	 *
	 * 00|000000 => if the two MSB are 00 the len is the 6 bits of this byte
	 * 01|000000 00000000 => 01, the len is 14 byes, 6 bits + 8 bits of next
	 * byte 10|000000 [32 bit integer] => if it's 01, a full 32 bit len will
	 * follow 11|000000 this means: specially encoded object will follow. The
	 * six bits number specify the kind of object that follows. See the
	 * REDIS_RDB_ENC_* defines.
	 *
	 * Lengths up to 63 are stored using a single byte, most DB keys, and may
	 * values, will fit inside.
	 */
	public static final int REDIS_RDB_6BITLEN = 0;
	public static final int REDIS_RDB_14BITLEN = 1;
	public static final int REDIS_RDB_32BITLEN = 2;
	public static final int REDIS_RDB_ENCVAL = 3;
	public static final int REDIS_RDB_LENERR = Integer.MAX_VALUE;

	/*
	 * When a length of a string object stored on disk has the first two bits
	 * set, the remaining two bits specify a special encoding for the object
	 * accordingly to the following defines:
	 */
	public static final int REDIS_RDB_ENC_INT8 = 0; /* 8 bit signed integer */
	public static final int REDIS_RDB_ENC_INT16 = 1; /* 16 bit signed integer */
	public static final int REDIS_RDB_ENC_INT32 = 2; /* 32 bit signed integer */
	public static final int REDIS_RDB_ENC_LZF = 3; /*
													 * string compressed with
													 * FASTLZ
													 */

	/*
	 * Objects encoding. Some kind of objects like Strings and Hashes can be
	 * internally represented in multiple ways. The 'encoding' field of the
	 * object is set to one of this fields for this object.
	 */
	public static final byte REDIS_ENCODING_RAW = 0; /* Raw representation */
	public static final byte REDIS_ENCODING_INT = 1; /* Encoded as integer */
	public static final byte REDIS_ENCODING_ZIPMAP = 2; /* Encoded as zipmap */
	public static final byte REDIS_ENCODING_HT = 3; /*
													 * Encoded as a hash table
													 */
	public static final byte REDIS_ENCODING_LINKEDLIST = 4; /*
															 * Encoded as
															 * regular linked
															 * list
															 */
	public static final byte REDIS_ENCODING_ZIPLIST = 5; /*
															 * Encoded as
															 * ziplist
															 */
	public static final byte REDIS_ENCODING_INTSET = 6; /* Encoded as intset */
	public static final byte REDIS_ENCODING_SKIPLIST = 7; /*
															 * Encoded as
															 * skiplist
															 */

	// redis数据对象类型
	public static final byte REDIS_STRING = 0;
	public static final byte REDIS_LIST = 1;
	public static final byte REDIS_SET = 2;
	public static final byte REDIS_ZSET = 3;
	public static final byte REDIS_HASH = 4;
	public static final byte REDIS_HASH_ZIPMAP = 9;
	public static final byte REDIS_LIST_ZIPLIST = 10;
	public static final byte REDIS_SET_INTSET = 11;
	public static final byte REDIS_ZSET_ZIPLIST = 12;
	public static final byte REDIS_HASH_ZIPLIST = 13;

	/*
	 * Dup object types to RDB object types. Only reason is readability (are we
	 * dealing with RDB types or with in-memory object types?).
	 */
	public static final byte REDIS_RDB_TYPE_STRING = 0;
	public static final byte REDIS_RDB_TYPE_LIST = 1;
	public static final byte REDIS_RDB_TYPE_SET = 2;
	public static final byte REDIS_RDB_TYPE_ZSET = 3;
	public static final byte REDIS_RDB_TYPE_HASH = 4;

	/* Object types for encoded objects. */
	public static final byte REDIS_RDB_TYPE_HASH_ZIPMAP = 9;
	public static final byte REDIS_RDB_TYPE_LIST_ZIPLIST = 10;
	public static final byte REDIS_RDB_TYPE_SET_INTSET = 11;
	public static final byte REDIS_RDB_TYPE_ZSET_ZIPLIST = 12;
	public static final byte REDIS_RDB_TYPE_HASH_ZIPLIST = 13;

}
