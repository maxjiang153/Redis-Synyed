package com.wmz7year.synyed.net.proroc;

import static com.wmz7year.synyed.constant.RedisProtocolConstant.REDIS_PROTOCOL_PARSER;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.wmz7year.synyed.packet.redis.RedisPacket;

/**
 * redis协议解析器<br>
 * 
 * @Title: RedisProtocolDecoder.java
 * @Package com.wmz7year.synyed.net.proroc
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午4:59:13
 * @version V1.0
 */
public class RedisProtocolDecoder extends CumulativeProtocolDecoder {

	/*
	 * @see org.apache.mina.filter.codec.CumulativeProtocolDecoder#doDecode(org.
	 * apache.mina.core.session.IoSession, org.apache.mina.core.buffer.IoBuffer,
	 * org.apache.mina.filter.codec.ProtocolDecoderOutput)
	 */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		RedisProtocolParser parser = (RedisProtocolParser) session.getAttribute(REDIS_PROTOCOL_PARSER);
		// 将接收到的数据通过解析器解析为Redis数据包对象
		parser.read(in.buf());

		// 获取解析器解析出的数据包
		RedisPacket[] redisPackets = parser.getPackets();
		if (redisPackets != null) {
			for (RedisPacket redisPacket : redisPackets) {
				out.write(redisPacket);
			}
		}

		// 以是否读取完数据为判断符
		return !in.hasRemaining();
	}

}
