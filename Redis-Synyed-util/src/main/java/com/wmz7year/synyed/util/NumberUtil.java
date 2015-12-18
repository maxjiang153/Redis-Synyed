package com.wmz7year.synyed.util;

import java.util.Arrays;

/**
 * 数字转换工具类<br>
 * 主要用于大小端法byte与基本数据类型、有无符号类型数据转换
 * 
 * @Title: NumberUtil.java
 * @Package org.Redis.Synyed.util
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月18日 上午10:17:09
 * @version V1.0
 */
public class NumberUtil {
	/**
	 * byte数组转换为int值的方法<br>
	 * 
	 * 
	 * @param buffer
	 *            需要转换的byte数组
	 * @return 转换后的int值
	 */
	public static int byte2Int(byte[] buffer) {
		if (buffer == null) {
			throw new NullPointerException();
		}
		if (buffer.length != 4) {
			throw new IllegalArgumentException(
					"Error buffer length can not cover bytes to int:" + Arrays.toString(buffer));
		}
		return (((buffer[3]) << 24) | ((buffer[2] & 0xff) << 16) | ((buffer[1] & 0xff) << 8) | ((buffer[0] & 0xff)));
	}

	/**
	 * byte数组转换为16bit int值的方法<br>
	 * 
	 * 
	 * @param buffer
	 *            需要转换的byte数组
	 * @return 转换后的int值
	 */
	public static int byte216bitInt(byte[] buffer) {
		if (buffer == null) {
			throw new NullPointerException();
		}
		if (buffer.length != 2) {
			throw new IllegalArgumentException(
					"Error buffer length can not cover bytes to int:" + Arrays.toString(buffer));
		}
		return (((buffer[1] & 0xff) << 8) | ((buffer[0] & 0xff)));
	}

	/**
	 * byte数组转换为24bit int值的方法<br>
	 * 
	 * 
	 * @param buffer
	 *            需要转换的byte数组
	 * @return 转换后的int值
	 */
	public static int byte224bitInt(byte[] buffer) {
		if (buffer == null) {
			throw new NullPointerException();
		}
		if (buffer.length != 3) {
			throw new IllegalArgumentException(
					"Error buffer length can not cover bytes to int:" + Arrays.toString(buffer));
		}
		return (((buffer[2] & 0xff) << 16) | ((buffer[1] & 0xff) << 8) | ((buffer[0] & 0xff)));
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
	public static long byte2Long(byte[] buffer) {
		if (buffer == null) {
			throw new NullPointerException();
		}
		if (buffer.length != 8) {
			throw new IllegalArgumentException(
					"Error buffer length can not cover bytes to int:" + Arrays.toString(buffer));
		}
		return ((((long) buffer[7]) << 56) | (((long) buffer[6] & 0xff) << 48) | (((long) buffer[5] & 0xff) << 40)
				| (((long) buffer[4] & 0xff) << 32) | (((long) buffer[3] & 0xff) << 24)
				| (((long) buffer[2] & 0xff) << 16) | (((long) buffer[1] & 0xff) << 8) | (((long) buffer[0] & 0xff)));
	}
}
