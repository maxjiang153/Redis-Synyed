package com.wmz7year.synyed.command.filter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.wmz7year.synyed.constant.RedisCommandSymbol;
import com.wmz7year.synyed.entity.RedisCommand;
import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.exception.RedisCommandRejectedException;

/**
 * GETSET命令校验拦截器<br>
 * GETSET命令必须在key存在的情况下才能正常执行<br>
 * 最终执行的还是SET key value<br>
 * 所以在该拦截器中转换命令GETSET为SET
 * 
 * @Title: RedisGETSETCommandCheckInterceptor.java
 * @Package com.wmz7year.synyed.command.filter
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月24日 下午12:34:41
 * @version V1.0
 */
@Component
public class RedisGETSETCommandCheckInterceptor implements RedisCommandInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(RedisGETSETCommandCheckInterceptor.class);

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
		if (RedisCommandSymbol.GETSET.equals(command.getCommand().toUpperCase())) {
			if (logger.isDebugEnabled()) {
				logger.debug("发现GETSET命令 转换为普通SET命令:" + command);
			}
			command.setCommand(RedisCommandSymbol.SET);
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
		return "GETSET command check interceptor";
	}

}
