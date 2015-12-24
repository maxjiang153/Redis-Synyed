package com.wmz7year.synyed.command.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.wmz7year.synyed.constant.RedisCommandSymbol;
import com.wmz7year.synyed.entity.RedisCommand;
import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.exception.RedisCommandRejectedException;

/**
 * eval命令过滤类 不同步eval命令
 * 
 * @Title: RedisEvalCommandCheckInterceptor.java
 * @Package com.wmz7year.synyed.command.filter
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月24日 下午4:00:33
 * @version V1.0
 */
@Component
public class RedisEvalCommandCheckInterceptor implements RedisCommandInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(RedisEvalCommandCheckInterceptor.class);

	/*
	 * @see com.wmz7year.synyed.command.filter.RedisCommandInterceptor#
	 * beforeSendCommand(com.wmz7year.synyed.entity.RedisCommand,
	 * com.wmz7year.synyed.entity.RedisServer,
	 * com.wmz7year.synyed.entity.RedisServer)
	 */
	@Override
	public void beforeSendCommand(RedisCommand command, RedisServer srcServer, RedisServer descServer)
			throws RedisCommandRejectedException {
		// 判断是否是get set命令
		String redisCommand = command.getCommand().toUpperCase();
		if (RedisCommandSymbol.EVAL.equals(redisCommand) || RedisCommandSymbol.EVALSHA.equals(redisCommand)) {
			logger.info("收到eval命令  拦截命令");
			throw new RedisCommandRejectedException();
		}
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
		// ignore
	}

	/*
	 * @see com.wmz7year.synyed.command.filter.RedisCommandInterceptor#getName()
	 */
	@Override
	public String getName() {
		return "Eval command interceptor";
	}

}
