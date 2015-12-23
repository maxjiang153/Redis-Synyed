package com.wmz7year.synyed.command.filter;

import com.wmz7year.synyed.entity.RedisCommand;
import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.exception.RedisCommandRejectedException;

/**
 * Redis命令拦截过滤器<br>
 * 每条命令在发送到目标redis之前会调用beforeSendCommand方法<br>
 * 在发送后会调用afterSendCommand方法<br>
 * 
 * @Title: RedisCommandInterceptor.java
 * @Package com.wmz7year.synyed.command.filter
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 下午2:25:17
 * @version V1.0
 */
public interface RedisCommandInterceptor {

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
	public void beforeSendCommand(RedisCommand command, RedisServer srcServer, RedisServer descServer)
			throws RedisCommandRejectedException;

	/**
	 * 在命令发送之后执行过滤的方法<br>
	 * 当抛出RedisCommandRejectedException异常时会中断处理连接<br>
	 * 
	 * @param command
	 *            需要过滤的命令
	 * @param result
	 *            true为命令同步成功 false为命令同步失败
	 * @param srcServer
	 *            命令产生的源服务器
	 * @param descServer
	 *            命令准备同步到的目标服务器
	 * @throws RedisCommandRejectedException
	 *             当出现问题时中断处理链
	 */
	public void afterSendCommand(RedisCommand command, boolean result, RedisServer srcServer, RedisServer descServer)
			throws RedisCommandRejectedException;

	/**
	 * 获取拦截器名称的方法
	 * 
	 * @return 拦截器名称
	 */
	public String getName();
}
