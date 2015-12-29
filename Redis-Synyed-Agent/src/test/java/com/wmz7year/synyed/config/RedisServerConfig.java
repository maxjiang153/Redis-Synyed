package com.wmz7year.synyed.config;

/**
 * 用于配置单元测试使用的Redis的类<br>
 * 配置源服务器地址与目标服务器地址
 * 
 * @author jiangwei (ydswcy513@gmail.com)
 * @since 2015年12月29日 上午10:15:08
 * @version V1.0
 */
public class RedisServerConfig {
	public static final String REDIS_SRC_SERVER_HOST = "127.0.0.1";
	public static final int REDIS_SRC_SERVER_PORT = 6380;
	public static final String REDIS_SRC_SERVER_PASSWORD = "password";
	public static final int REDIS_SRC_SERVER_TIMEOUT = 5000;

	public static final String REDIS_DESC_SERVER_HOST = "127.0.0.1";
	public static final int REDIS_DESC_SERVER_PORT = 6380;
	public static final String REDIS_DESC_SERVER_PASSWORD = "password";
	public static final int REDIS_DESC_SERVER_TIMEOUT = 5000;
}
