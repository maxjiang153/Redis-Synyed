package com.wmz7year.synyed.command.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.wmz7year.synyed.entity.RedisCommand;
import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.exception.RedisCommandRejectedException;

/**
 * 对同步命令进行日志记录的拦截器
 * 
 * @Title: RedisCommandLogInterceptor.java
 * @Package com.wmz7year.synyed.command.filter
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 下午2:42:49
 * @version V1.0
 */
@Component
public class RedisCommandLogInterceptor implements RedisCommandInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(RedisCommandLogInterceptor.class);

	/*
	 * @see com.wmz7year.synyed.command.filter.RedisCommandInterceptor#
	 * beforeSendCommand(com.wmz7year.synyed.entity.RedisCommand,
	 * com.wmz7year.synyed.entity.RedisServer,
	 * com.wmz7year.synyed.entity.RedisServer)
	 */
	@Override
	public void beforeSendCommand(RedisCommand command, RedisServer srcServer, RedisServer descServer)
			throws RedisCommandRejectedException {
		logger.info("准备同步命令:" + command);
	}

	/*
	 * @see com.wmz7year.synyed.command.filter.RedisCommandInterceptor#
	 * afterSendCommand(com.wmz7year.synyed.entity.RedisCommand, boolean,
	 * com.wmz7year.synyed.entity.RedisServer,
	 * com.wmz7year.synyed.entity.RedisServer)
	 */
	@Override
	public void afterSendCommand(RedisCommand command, boolean result, RedisServer srcServer, RedisServer descServer)
			throws RedisCommandRejectedException {
		logger.info("同步命令:" + command + " 结果：" + result);
	}

	/*
	 * @see com.wmz7year.synyed.command.filter.RedisCommandInterceptor#getName()
	 */
	@Override
	public String getName() {
		return "command logger filter";
	}

}
