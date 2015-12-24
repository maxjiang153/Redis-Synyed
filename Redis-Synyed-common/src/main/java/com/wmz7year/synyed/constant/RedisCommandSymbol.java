package com.wmz7year.synyed.constant;

/**
 * 封装Redis命令符号常量类
 * 
 * @Title: RedisCommandSymbol.java
 * @Package com.wmz7year.synyed.constant
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月11日 上午11:10:51
 * @version V1.0
 */
public class RedisCommandSymbol {
	/**
	 * 数组类型数据包
	 */
	public static final String ARRAY = "ARRAY";
	/**
	 * 验证身份命令
	 */
	public static final String AUTH = "AUTH";
	/**
	 * 空格
	 */
	public static final byte BLANK = ' ';
	/**
	 * 复合类型的字符串
	 */
	public static final String BULKSTRING = "BULKSTRING";
	/**
	 * redis 数据传输符号
	 */
	public static final String DATABASETRANSFER = "DATABASETRANSFER";
	/**
	 * redis错误符号
	 */
	public static final String ERR = "ERR";

	/**
	 * 设置一个key在指定时间过期<br>
	 * http://redis.io/commands/expireat<br>
	 * 
	 * <pre>
	 * 		 EXPIREAT mykey 1293840000
	 * </pre>
	 */
	public static final String EXPIREAT = "EXPIREAT";
	/**
	 * 整数类型数据包
	 */
	public static final String INTEGER = "INTEGER";

	/**
	 * 原始的方式获取值并且设置新的值<br>
	 * 必须在存在对应key的时候才能set 否则会出现异常<br>
	 * http://redis.io/commands/getset<br>
	 * 
	 * <pre>
	 *  set a b getset a c
	 * </pre>
	 */
	public static final String GETSET = "GETSET";

	/**
	 * 向指定的key设置 k v值的方法<br>
	 * 
	 * http://redis.io/commands/hset<br>
	 * 
	 * <pre>
	 * 		HSET key field value
	 * </pre>
	 */
	public static final String HSET = "HSET";
	/**
	 * 向一个list中添加值<br>
	 * 
	 * http://redis.io/commands/lpush<br>
	 * 
	 * <pre>
	 * 		LPUSH key value1 value2 value3...
	 * </pre>
	 */
	public static final String LPUSH = "LPUSH";
	/**
	 * redis成功符号
	 */
	public static final String OK = "OK";
	/**
	 * 心跳检查包<br>
	 * http://redis.io/commands/ping<br>
	 * 
	 * <pre>
	 * PING
	 * </pre>
	 */
	public static final String PING = "PING";
	/**
	 * 选择数据库的命令<br>
	 * http://redis.io/commands/SELECT<br>
	 * 
	 * <pre>
	 * 		SELECT dbnum
	 * </pre>
	 */
	public static final String SELECT = "SELECT";
	/**
	 * 向指定KEY设置值<br>
	 * 
	 * http://redis.io/commands/set<br>
	 * 
	 * <pre>
	 * 		SET key value
	 * </pre>
	 */
	public static final String SET = "SET";
	/**
	 * 执行同步的命令
	 * 
	 * http://redis.io/commands/sync<br>
	 * 
	 * <pre>
	 * SYNC
	 * </pre>
	 */
	public static final String SYNC = "SYNC";
	/**
	 * 向指定key插入sort set值的方法<br>
	 * http://redis.io/commands/zadd<br>
	 * 
	 * <pre>
	 * 		ZADD key field value
	 * </pre>
	 */
	public static final String ZADD = "ZADD";
}
