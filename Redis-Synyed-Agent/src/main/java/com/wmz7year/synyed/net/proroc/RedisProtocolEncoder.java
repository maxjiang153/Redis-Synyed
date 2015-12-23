package com.wmz7year.synyed.net.proroc;

import static com.wmz7year.synyed.constant.RedisProtocolConstant.REDIS_PROTOCOL_CR;
import static com.wmz7year.synyed.constant.RedisProtocolConstant.REDIS_PROTOCOL_LF;
import static com.wmz7year.synyed.constant.RedisProtocolConstant.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmz7year.synyed.entity.RedisCommand;
import com.wmz7year.synyed.entity.RedisCommandData;

/**
 * Redis协议编码器<br>
 * 编码方式为String类型的Redis命令结尾添加\r\n结束符<br>
 * 然后转换为mina的IoBuffer对象进入到接下来的处理链
 * 
 * @Title: RedisProtocolEncoder.java
 * @Package com.wmz7year.synyed.net.proroc
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午4:42:09
 * @version V1.0
 */
public class RedisProtocolEncoder extends ProtocolEncoderAdapter {
	private static final Logger logger = LoggerFactory.getLogger(RedisProtocolEncoder.class);

	/*
	 * @see
	 * org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core.
	 * session.IoSession, java.lang.Object,
	 * org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		if (message instanceof RedisCommand) {
			RedisCommand command = (RedisCommand) message;
			List<RedisCommandData> values = command.getValues();
			String redisCommand = command.getCommand();

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(REDIS_PROTOCOL_ARRAY);
			writeIntCrLf(values.size() + 1, bos);
			bos.write(REDIS_PROTOCOL_BULK_STRINGS);
			writeIntCrLf(redisCommand.length(), bos);
			bos.write(redisCommand.getBytes());
			writeCrLf(bos);
			for (RedisCommandData value : values) {
				bos.write(REDIS_PROTOCOL_BULK_STRINGS);
				writeIntCrLf(value.getData().length, bos);
				bos.write(value.getData());
				writeCrLf(bos);
			}
			// 生成最终的数据
			byte[] data = bos.toByteArray();

			IoBuffer buffer = IoBuffer.allocate(data.length);
			buffer.put(data);
			buffer.flip();

			if (logger.isDebugEnabled()) {
				logger.debug("send command:" + redisCommand + " hex:" + Arrays.toString(buffer.array()));
			}
			out.write(buffer);
		} else {
			logger.warn("未知类型的数据包 无法编码：" + message);
		}
	}

	/**
	 * 写入int值的方法
	 * 
	 * @param value
	 *            需要写入的int值
	 * @param bos
	 *            byte数组缓冲区流对象
	 * @throws IOException
	 *             当写入出现问题则抛出该异常
	 */
	private void writeIntCrLf(int value, ByteArrayOutputStream bos) throws IOException {
		bos.write(String.valueOf(value).getBytes());

		writeCrLf(bos);
	}

	/**
	 * 写入crlf结束分隔符的方法
	 * 
	 * @param bos
	 *            需要写入的数据输出流
	 * @throws IOException
	 *             当写入出现问题则抛出该异常
	 */
	private void writeCrLf(ByteArrayOutputStream bos) throws IOException {
		bos.write(REDIS_PROTOCOL_CR);
		bos.write(REDIS_PROTOCOL_LF);
	}

}
