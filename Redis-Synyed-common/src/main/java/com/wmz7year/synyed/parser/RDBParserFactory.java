package com.wmz7year.synyed.parser;

import java.util.Arrays;

import com.wmz7year.synyed.exception.RedisRDBException;
import com.wmz7year.synyed.parser.impl.RDBParserImpl0006;

/**
 * RDB文件解析器创建工厂类<br>
 * 通过解析rdb内容协议头9个字节REDIS****来判断具体使用哪个版本的解析器<br>
 * 
 * @Title: RDBParserFactory.java
 * @Package com.wmz7year.synyed.parser
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月15日 上午10:50:11
 * @version V1.0
 */
public class RDBParserFactory {

	private RDBParserFactory() {

	}

	/**
	 * 根据RDB文件头创建rdb文件解析器的方法
	 * 
	 * @param rdbHeader
	 *            rdb文件头
	 * @return 对应版本的rdb文件解析器对象
	 * @throws RedisRDBException
	 *             当版本号不能识别或者不支持时抛出该异常
	 */
	public static RDBParser createRDBParser(byte[] rdbHeader) throws RedisRDBException {
		if (rdbHeader == null) {
			throw new NullPointerException("rdb header can not be null!");
		}
		if (rdbHeader.length != 9) {
			throw new RedisRDBException("rdb头长度必须为9 " + Arrays.toString(rdbHeader));
		}
		String redisRDBHeader = new String(rdbHeader);
		if (!redisRDBHeader.startsWith("REDIS")) {
			throw new RedisRDBException("rdb头标识符必须为 REDIS " + Arrays.toString(rdbHeader));
		}
		try {
			String version = redisRDBHeader.substring(5, 9);
			if ("0006".equals(version)) {
				return new RDBParserImpl0006();
			} else {
				throw new RedisRDBException("不支持的RDB版本：" + version);
			}
		} catch (Exception e) {
			throw new RedisRDBException(e);
		}
	}
}
