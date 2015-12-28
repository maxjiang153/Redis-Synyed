package com.wmz7year.synyed.command.filter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.wmz7year.synyed.entity.RedisCommand;
import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.exception.RedisCommandRejectedException;

/**
 * 同步命令状态统计拦截器<br>
 * 每秒钟打印一下统计信息
 * 
 * @author jiangwei (ydswcy513@gmail.com)
 * @since 2015年12月28日 上午11:39:06
 * @version V1.0
 */
@Component
public class RedisCommandSynStatusInterceptor implements RedisCommandInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(RedisCommandSynStatusInterceptor.class);

	// 命令统计器
	private AtomicInteger commandCounter = new AtomicInteger(0);
	// 当前分钟命令统计器
	private AtomicInteger currentMinuteCounter = new AtomicInteger(0);
	// 错误统计
	private AtomicInteger errorCounter = new AtomicInteger(0);

	public RedisCommandSynStatusInterceptor() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					logger.info("当前同步速度:" + currentMinuteCounter.getAndSet(0) + "/s 总同步命令" + commandCounter.get()
							+ "  同步失败命令：" + errorCounter.get());
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}

	/*
	 * @see com.wmz7year.synyed.command.filter.RedisCommandInterceptor#
	 * beforeSendCommand(com.wmz7year.synyed.entity.RedisCommand,
	 * com.wmz7year.synyed.entity.RedisServer,
	 * com.wmz7year.synyed.entity.RedisServer)
	 */
	@Override
	public void beforeSendCommand(RedisCommand command, RedisServer srcServer, RedisServer descServer)
			throws RedisCommandRejectedException {
		// ignore
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
		if (result) {
			currentMinuteCounter.incrementAndGet();
			commandCounter.incrementAndGet();
		} else {
			errorCounter.getAndIncrement();
			logger.error(command.toString());
		}
	}

	/*
	 * @see com.wmz7year.synyed.command.filter.RedisCommandInterceptor#getName()
	 */
	@Override
	public String getName() {
		return "Redis command syn status counter";
	}
}
