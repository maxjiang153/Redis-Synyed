package com.wmz7year.synyed.net;

import java.io.Closeable;

import com.wmz7year.synyed.exception.RedisProtocolException;

/**
 * 封装Redis连接对象的接口<br>
 * 
 * @Title: RedisConnection.java
 * @Package com.wmz7year.synyed.net
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午3:20:31
 * @version V1.0
 */
public interface RedisConnection extends Closeable {

	/**
	 * 连接到Redis的方法<br>
	 * 
	 * @param address
	 *            redis服务器地址
	 * @param port
	 *            redis端口
	 * @param timeout
	 *            连接超时时间
	 * @return true为连接成功 false为连接失败
	 * @throws RedisProtocolException
	 *             当连接发生问题时抛出该异常
	 */
	public boolean connect(String address, int port, long timeout) throws RedisProtocolException;

	/**
	 * 连接到Redis的方法<br>
	 * 
	 * @param address
	 *            redis服务器地址
	 * @param port
	 *            redis端口
	 * @param password
	 *            redis密码
	 * @param timeout
	 *            连接超时时间
	 * @return true为连接成功 false为连接失败
	 * @throws RedisProtocolException
	 *             当连接发生问题时抛出该异常
	 */
	public boolean connect(String address, int port, String password, long timeout) throws RedisProtocolException;

	/**
	 * 判断是否连接上的标识位
	 * 
	 * @return true为连接上 fasle为未连接
	 */
	public boolean isConnected();

	
	public Object sendCommand(String command) throws RedisProtocolException;
}
