package com.wmz7year.synyed.net;

import com.wmz7year.synyed.packet.redis.RedisPacket;

/**
 * redis响应监听器<br>
 * 
 * @Title: RedisResponseListener.java
 * @Package com.wmz7year.synyed.net
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月11日 下午12:49:05
 * @version V1.0
 */
public interface RedisResponseListener {

	/**
	 * 接受响应的方法
	 * 
	 * @param redisPacket
	 *            redis响应数据包
	 */
	public void receive(RedisPacket redisPacket);
}
