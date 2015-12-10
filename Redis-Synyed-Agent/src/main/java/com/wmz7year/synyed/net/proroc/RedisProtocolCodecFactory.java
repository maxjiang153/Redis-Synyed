package com.wmz7year.synyed.net.proroc;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Redis数据包解析器工厂类
 * 
 * @Title: RedisProtocolCodecFactory.java
 * @Package com.wmz7year.synyed.net.proroc
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午4:36:51
 * @version V1.0
 */
public class RedisProtocolCodecFactory implements ProtocolCodecFactory {

	/**
	 * redis协议解码器
	 */
	private RedisProtocolDecoder redisProtocolDecoder;

	/**
	 * redis协议编码器
	 */
	private RedisProtocolEncoder redisProtocolEncoder;

	public RedisProtocolCodecFactory() {
		this.redisProtocolDecoder = new RedisProtocolDecoder();
		this.redisProtocolEncoder = new RedisProtocolEncoder();
	}

	/*
	 * @see
	 * org.apache.mina.filter.codec.ProtocolCodecFactory#getEncoder(org.apache.
	 * mina.core.session.IoSession)
	 */
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return redisProtocolEncoder;
	}

	/*
	 * @see
	 * org.apache.mina.filter.codec.ProtocolCodecFactory#getDecoder(org.apache.
	 * mina.core.session.IoSession)
	 */
	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return redisProtocolDecoder;
	}

}
