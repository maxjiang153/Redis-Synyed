package com.wmz7year.synyed.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.wmz7year.synyed.command.filter.RedisCommandInterceptor;
import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.exception.RedisCommandRejectedException;

/**
 * Redis命令拦截管理模块对象<br>
 * 所有需要同步的Redis命令都会先经过该模块<br>
 * 然后调用所有com.wmz7year.synyed.command.filter.RedisCommandInterceptor<br>
 * 接口的实现类进行过滤拦截处理
 * 
 * @Title: RedisCommandFilterManager.java
 * @Package com.wmz7year.synyed.module
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 下午2:22:57
 * @version V1.0
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RedisCommandFilterManager extends BasicModule {
	private static final Logger logger = LoggerFactory.getLogger(RedisCommandFilterManager.class);

	/**
	 * Redis命令过滤器集合
	 */
	@Autowired
	private List<RedisCommandInterceptor> interceptors = new ArrayList<RedisCommandInterceptor>();

	/*
	 * @see com.wmz7year.synyed.module.Module#getName()
	 */
	@Override
	public String getName() {
		return "RedisCommandFilterManager";
	}

	/*
	 * @see com.wmz7year.synyed.module.BasicModule#initialize()
	 */
	@Override
	public void initialize() throws Exception {
		logger.info("发现的拦截器数量：" + interceptors.size());
	}

	/*
	 * @see com.wmz7year.synyed.module.BasicModule#destroyModule()
	 */
	@Override
	public void destroyModule() throws Exception {
		interceptors.clear();
	}

	/**
	 * 在命令发送之前执行过滤的方法<br>
	 * 当抛出RedisCommandRejectedException异常时会中断处理连接<br>
	 * 对应的命令不会发送到目标服务器并且也不会执行后续的过滤器
	 * 
	 * @param command
	 *            需要过滤的命令
	 * @param srcServer
	 *            命令产生的源服务器
	 * @param descServer
	 *            命令准备同步到的目标服务器
	 * @throws RedisCommandRejectedException
	 *             当出现问题时中断同步该命令以及处理链
	 */
	public void beforeSendCommand(String command, RedisServer srcServer, RedisServer descServer)
			throws RedisCommandRejectedException {
		Iterator<RedisCommandInterceptor> iterator = interceptors.iterator();
		while (iterator.hasNext()) {
			RedisCommandInterceptor commandInterceptor = iterator.next();
			try {
				commandInterceptor.beforeSendCommand(command, srcServer, descServer);
			} catch (RedisCommandRejectedException e) {
				logger.info("命令：" + command + "在发送前被拦截器:" + commandInterceptor.getName() + "拦截");
				throw e;
			}
		}

	}

	/**
	 * 在命令发送之后执行过滤的方法<br>
	 * 当抛出RedisCommandRejectedException异常时会中断处理连接<br>
	 * 
	 * @param command
	 *            需要过滤的命令
	 * @param srcServer
	 *            命令产生的源服务器
	 * @param descServer
	 *            命令准备同步到的目标服务器
	 * @throws RedisCommandRejectedException
	 *             当出现问题时中断处理链
	 */
	public void afterSendCommand(String command, RedisServer srcServer, RedisServer descServer)
			throws RedisCommandRejectedException {
		Iterator<RedisCommandInterceptor> iterator = interceptors.iterator();
		while (iterator.hasNext()) {
			RedisCommandInterceptor commandInterceptor = iterator.next();
			try {
				commandInterceptor.afterSendCommand(command, srcServer, descServer);
			} catch (RedisCommandRejectedException e) {
				logger.info("命令：" + command + "在发送后被拦截器:" + commandInterceptor.getName() + "拦截");
				throw e;
			}
		}
	}

}
