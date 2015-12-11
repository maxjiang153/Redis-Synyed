package com.wmz7year.synyed.net.proroc;

import static com.wmz7year.synyed.constant.RedisProtocolConstant.*;
import static com.wmz7year.synyed.constant.RedisCommandSymbol.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.wmz7year.synyed.exception.RedisProtocolException;
import com.wmz7year.synyed.packet.redis.RedisErrorPacket;
import com.wmz7year.synyed.packet.redis.RedisPacket;
import com.wmz7year.synyed.packet.redis.RedisSimpleStringPacket;

/**
 * Redis数据管道解析器<br>
 * 将socket中读取的byte数据流进行初步处理<br>
 * 如断包、粘包等<br>
 * 每个redis命令都以\r\n结尾 也就是0x0D 0x0A<br>
 * 该解析器为全局唯一的对象
 * 
 * 
 * @Title: RedisProtocolParser.java
 * @Package com.wmz7year.synyed.parser
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午2:57:26
 * @version V1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RedisProtocolParser {
	/**
	 * 缓冲区大小 1M
	 */
	private int BUFFERSIZE = 1024 * 1024;
	/**
	 * 缓冲区
	 */
	private byte[] buffer = new byte[BUFFERSIZE];
	/**
	 * 缓冲区中写入的位置
	 */
	private int limit = 0;
	/**
	 * 缓冲区读取过的位置
	 */
	private int readFlag = 0;
	/**
	 * 缓冲区最大长度
	 */
	private int maxLength = buffer.length;

	/**
	 * 判断缓冲区是否写满的标志位
	 */
	private boolean isFull = false;
	/**
	 * 当前解析中的数据包
	 */
	private byte[] currentPacket;
	/**
	 * 读取数据包的自动扩容数量
	 */
	private int readInc = 128;
	/**
	 * 解析出的数据包列表
	 */
	private List<RedisPacket> packets = new ArrayList<RedisPacket>();

	/**
	 * 解析Redis数据包的方法<br>
	 * 
	 * @param byteBuffer
	 *            需要解析的数据包内容
	 * @throws RedisProtocolException
	 *             当解析过程中出现问题则抛出该异常
	 */
	public void read(ByteBuffer byteBuffer) throws RedisProtocolException {
		// 获取数据包内容的长度
		int dataLength = byteBuffer.limit();
		// 创建对应大小的缓冲区
		byte[] dataBuffer = new byte[dataLength];
		// 读取数据
		byteBuffer.get(dataBuffer);

		// 获取当前缓冲区剩余可用空间
		int currentCapacity = 0;
		if (limit > readFlag) {
			currentCapacity = (maxLength - limit) + readFlag;
		} else if (limit == readFlag) {
			currentCapacity = maxLength;
		} else {
			currentCapacity = maxLength - ((maxLength - readFlag) + limit);
		}

		// 判断缓冲区容量是否可以装得下此次来的数据 如果不能则扩充数组长度
		if (currentCapacity < dataLength) {
			byte[] tempBuffer = new byte[maxLength + (dataLength - currentCapacity)];
			if (limit > readFlag) { // 判断是否是连续拷贝
				System.arraycopy(buffer, readFlag, tempBuffer, 0, (limit - currentCapacity));
				limit = limit - readFlag; // 重置写入标识位
				maxLength = buffer.length;
			} else if (limit == readFlag) { // 读取与写入一样 重置缓冲区
				limit = 0; // 重置写入标识位
				maxLength = buffer.length;
			} else { // 分开拷贝
				System.arraycopy(buffer, readFlag, tempBuffer, 0, (maxLength - readFlag));
				System.arraycopy(buffer, 0, tempBuffer, (maxLength - readFlag), limit);
				maxLength = buffer.length;
			}
			buffer = null;
			buffer = tempBuffer;
			tempBuffer = null;
			readFlag = 0; // 复位读取的地方
		}

		// 判断是否需要中断复制
		if ((maxLength - limit) < dataLength) {
			// 分段的长度
			int flag = (maxLength - limit);
			// 第一次复制 从可以写入位置一直复制到数组结束
			System.arraycopy(dataBuffer, 0, buffer, limit, flag);
			// 第二次复制 从第一个元素开始写直到写完
			System.arraycopy(dataBuffer, flag, buffer, 0, dataLength - flag);
			limit = dataLength - flag;
			isFull = (limit == readFlag); // 判断是否为写满
		} else {
			System.arraycopy(dataBuffer, 0, buffer, limit, dataLength);
			// 更新limit位置
			limit = dataLength + limit;
			isFull = (limit == readFlag); // 判断是否为写满
		}
		dataBuffer = null;

		// 开始解析数据包
		while (true) {
			// 当前处理中的数据包写入位置
			int tempFlag = 0;
			// 判断是否有解析中的包 如果没有则初始化一个新的当前包缓冲区
			if (currentPacket == null) {
				currentPacket = new byte[readInc];
			}
			// 冲缓冲池中读取数据
			for (int i = readFlag; i < maxLength; i++) {
				byte b = buffer[i];
				// 判断结束位
				if (b == REDIS_PROTOCOL_R) {
					if ((i + 1) != maxLength) {
						if (buffer[i + 1] == REDIS_PROTOCOL_N) {
							// 说明这是一个完整的数据包 需要解析成对象
							byte[] packet = new byte[tempFlag];
							System.arraycopy(currentPacket, 0, packet, 0, tempFlag);

							// 将byte数据解析成数据包对象
							RedisPacket redisPacket = parsePacket(packet);
							this.packets.add(redisPacket);

							// 释放资源 生成新的包
							tempFlag = 0;
							currentPacket = null;
							currentPacket = new byte[readInc];
							continue;
						}
					}
				}
				// 判断是否需要扩容
				if (tempFlag + 1 == currentPacket.length) {
					byte[] temp = new byte[currentPacket.length + readInc];
					System.arraycopy(currentPacket, 0, temp, 0, currentPacket.length);
					currentPacket = temp;
				}
				// 追加数据
				currentPacket[tempFlag++] = buffer[i];
			}
			break;
		}
	}

	/**
	 * 解析byte数据为数据包对象的方法
	 * 
	 * @param buffer
	 *            需要解析的缓冲区数据
	 * @throws RedisProtocolException
	 *             当解析出现问题时抛出该异常
	 */
	private RedisPacket parsePacket(byte[] buffer) throws RedisProtocolException {
		int bufferLength = buffer.length;
		for (int i = 0; i < bufferLength; i++) {
			// 读取第一个字节 该字节为redis协议类型字段
			byte type = buffer[i];
			byte[] data = null;
			switch (type) {
			case REDIS_PROTOCOL_SIMPLE_STRING:
				data = new byte[bufferLength - 1];
				System.arraycopy(buffer, 1, data, 0, bufferLength - 1);
				RedisSimpleStringPacket simpleStringPacket = new RedisSimpleStringPacket(new String(data));
				return simpleStringPacket;
			case REDIS_PROTOCOL_ERRORS:
				data = new byte[bufferLength - 6];
				// -ERR message
				System.arraycopy(buffer, 5, data, 0, bufferLength - 6);
				RedisErrorPacket errorPacket = new RedisErrorPacket(ERR);
				errorPacket.setErrorMessage(new String(data));
				return errorPacket;
			case REDIS_PROTOCOL_INTEGERS:
				// TODO read integer
				break;
			case REDIS_PROTOCOL_BULK_STRINGS:
				// TODO read bulk strings
				break;
			case REDIS_PROTOCOL_ARRAY:
				// TODO read array;
				break;
			default:
				throw new RedisProtocolException("未知的数据类型：" + type);
			}
		}
		// TODO
		return null;
	}

	/**
	 * 获取解析到的消息内容列表的方法
	 * 
	 * @return 消息内容列表
	 */
	public RedisPacket[] getPackets() {
		// 如果没解析出消息啧返回空数组
		if (packets.size() == 0) {
			return null;
		}
		RedisPacket[] redisPackets = packets.toArray(new RedisPacket[packets.size()]);
		// 执行内存回收操作
		packets.clear();
		gc();

		return redisPackets;
	}

	/**
	 * 当缓冲区超过默认大小时进行内存回收<br>
	 * 同时对缓冲区内的数据进行整理
	 */
	private void gc() {
		// 判断缓冲区大小是否为默认大小
		if (maxLength == BUFFERSIZE) {
			return;
		}
		if (readFlag == maxLength && !isFull) {
			buffer = null; // 释放缓冲区内存
			buffer = new byte[BUFFERSIZE];
			limit = 0;
			readFlag = 0;
			maxLength = BUFFERSIZE;
		}
	}
}
