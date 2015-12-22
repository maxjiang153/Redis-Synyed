package com.wmz7year.synyed.constant;

/**
 * 封装Redis命令的枚举类<br>
 * 该枚举类中的redis命令为会导致master向slave发起数据同步的命令<br>
 * 
 * 进度：BITOP
 * 
 * @Title: RedisCommand.java
 * @Package com.wmz7year.synyed.constant
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月9日 下午6:02:54
 * @version V1.0
 */
public enum RedisCommand {

	/**
	 * 向一个key中追加值<br>
	 * 
	 * http://redis.io/commands/append<br>
	 * 
	 * <pre>
	 * APPEND key value
	 * </pre>
	 */
	APPEND,

	/**
	 * 对N个key做AND/OR/XOR/NOT操作并且复制到第一个KEY上<br>
	 * 
	 * http://redis.io/commands/bitop<br>
	 * 
	 * <pre>
	 * 		BITOP AND destkey srckey1 srckey2 srckey3 ... srckeyN
	 * 		BITOP OR destkey srckey1 srckey2 srckey3 ... srckeyN
	 * 		BITOP XOR destkey srckey1 srckey2 srckey3 ... srckeyN
	 * 		BITOP NOT destkey srckey
	 * </pre>
	 */
	BITOP,

	/**
	 * 阻塞版本的LPOP<br>
	 * 
	 * http://redis.io/commands/blpop<br>
	 * 
	 * <pre>
	 * 		RPUSH list1 a b c
	 * 		BLPOP list1 list2 0
	 * </pre>
	 */
	BLPOP,

	/**
	 * 阻塞版本的RPOP<br>
	 * 
	 * http://redis.io/commands/brpop<br>
	 * 
	 * <pre>
	 * 		RPUSH list1 a b c
	 * 		BRPOP list1 list2 0
	 * </pre>
	 */
	BRPOP,

	/**
	 * 阻塞版本的RPOPLPUSH<br>
	 * 
	 * http://redis.io/commands/brpoplpush<br>
	 * 
	 * <pre>
	 * 		 BRPOPLPUSH msg reciver 500
	 * </pre>
	 */
	BRPOPLPUSH,

	/**
	 * 针对一个key自减的操作<br>
	 * 
	 * http://redis.io/commands/decr<br>
	 * 
	 * <pre>
	 * 		DERC key
	 * </pre>
	 */
	DECR,

	/**
	 * 删除一个key<br>
	 * 
	 * http://redis.io/commands/del<br>
	 * 
	 * <pre>
	 * 		DEL key
	 * </pre>
	 */
	DEL,

	/**
	 * 向一个list中添加值<br>
	 * 
	 * http://redis.io/commands/lpush<br>
	 * 
	 * <pre>
	 * 		LPUSH key value1 value2 value3...
	 * </pre>
	 */
	LPUSH,

	/**
	 * 向指定KEY设置值<br>
	 * 
	 * http://redis.io/commands/set<br>
	 * 
	 * <pre>
	 * 		SET key value
	 * </pre>
	 */
	SET,

	/**
	 * 向指定的key设置 k v值的方法<br>
	 * 
	 * http://redis.io/commands/hset<br>
	 * 
	 * <pre>
	 * 		HSET key field value
	 * </pre>
	 */
	HSET,

	/**
	 * 向指定key插入sort set值的方法<br>
	 * http://redis.io/commands/zadd<br>
	 * 
	 * <pre>
	 * 		ZADD key field value
	 * </pre>
	 */
	ZADD,

	/**
	 * 选择数据库的命令<br>
	 * http://redis.io/commands/SELECT<br>
	 * 
	 * <pre>
	 * 		SELECT dbnum
	 * </pre>
	 */
	SELECT,

	/**
	 * 心跳检查包<br>
	 * http://redis.io/commands/ping<br>
	 * 
	 * <pre>
	 * 		PING
	 * </pre>
	 */
	PING,

	/**
	 * 执行同步的命令
	 * 
	 * http://redis.io/commands/sync<br>
	 * 
	 * <pre>
	 * 		SYNC
	 * </pre>
	 */
	SYNC
}
