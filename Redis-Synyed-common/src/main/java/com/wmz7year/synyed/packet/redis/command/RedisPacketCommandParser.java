package com.wmz7year.synyed.packet.redis.command;

import static com.wmz7year.synyed.constant.RedisCommandSymbol.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmz7year.synyed.entity.RedisCommand;
import com.wmz7year.synyed.packet.redis.RedisArraysPacket;
import com.wmz7year.synyed.packet.redis.RedisBulkStringPacket;
import com.wmz7year.synyed.packet.redis.RedisDataBaseTransferPacket;
import com.wmz7year.synyed.packet.redis.RedisErrorPacket;
import com.wmz7year.synyed.packet.redis.RedisIntegerPacket;
import com.wmz7year.synyed.packet.redis.RedisPacket;
import com.wmz7year.synyed.packet.redis.RedisSimpleStringPacket;
import com.wmz7year.synyed.parser.entry.RedisDB;

/**
 * redis数据包语法生成Redis命令的解析器<br>
 * 
 * @Title: RedisPacketCommandParser.java
 * @Package com.wmz7year.synyed.packet.redis.command
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 上午10:24:58
 * @version V1.0
 */
public class RedisPacketCommandParser {
	private static Logger logger = LoggerFactory.getLogger(RedisPacketCommandParser.class);

	/**
	 * Redis数据包解析为Redis命令操作的方法
	 * 
	 * @param redisPacket
	 *            需要解析的Redis数据包对象
	 * @return 解析出的Redis命令
	 */
	public List<RedisCommand> parseRedisPacket(RedisPacket redisPacket) {
		List<RedisCommand> commands = new ArrayList<RedisCommand>();

		if (logger.isDebugEnabled()) {
			logger.debug("Parse RedisPacket:" + redisPacket);
		}

		if (redisPacket instanceof RedisArraysPacket) { // 数组类型数据包
			List<RedisCommand> result = parserRedisArraysPacket(redisPacket);
			commands.addAll(result);
		} else if (redisPacket instanceof RedisBulkStringPacket) { // 复合类型字符串数据包
			// TODO
			System.out.println("RedisBulkStringPacket");
		} else if (redisPacket instanceof RedisDataBaseTransferPacket) { // 数据库传输包
			List<RedisCommand> result = parserRedisDataBaseTransferPacket(redisPacket);
			commands.addAll(result);
		} else if (redisPacket instanceof RedisErrorPacket) { // 错误信息类型数据包
			logger.warn("收到错误类型数据包   不进行解析操作：" + redisPacket);
		} else if (redisPacket instanceof RedisIntegerPacket) { // 整数类型数据包
			// TODO
			System.out.println("RedisIntegerPacket");
		} else if (redisPacket instanceof RedisSimpleStringPacket) { // 简单字符串类型数据包
			// TODO
			System.out.println("RedisSimpleStringPacket");
		} else {
			logger.error("不支持的数据包类型：" + redisPacket.getClass().getName());
			throw new IllegalArgumentException("不支持的数据包类型：" + redisPacket.getClass().getName());
		}
		return commands;
	}

	/**
	 * 解析数组内容包到命令的方法<br>
	 * 
	 * @param redisPacket
	 *            数组类型数据包
	 * @return
	 */
	private List<RedisCommand> parserRedisArraysPacket(RedisPacket redisPacket) {
		List<RedisCommand> result = new ArrayList<RedisCommand>();
		RedisArraysPacket packet = (RedisArraysPacket) redisPacket;

		// 判断是否是ping包
		if (packet.getArrayLength() == 1) {
			RedisPacket elementPacket = packet.getPackets().get(0);
			if (elementPacket instanceof RedisBulkStringPacket) {
				RedisBulkStringPacket bulkPacket = (RedisBulkStringPacket) elementPacket;
				String command = new String(bulkPacket.getData());
				if (PING.toString().equals(command)) {
					if (logger.isDebugEnabled()) {
						logger.debug("收到了ping包");
					}
					return Collections.emptyList();
				} else {
					result.add(new RedisCommand(command));
				}
			}
		} else {
			List<RedisPacket> packets = packet.getPackets();
			RedisCommand redisCommand = new RedisCommand(packets.get(0).getCommand());
			boolean isKeySeted = false;
			for (int i = 1; i < packets.size(); i++) {
				if (!isKeySeted) {
					redisCommand.setKey(packets.get(i).getData());
					isKeySeted = true;
				} else {
					redisCommand.addValue(packets.get(i).getData());
				}
			}
			result.add(redisCommand);
		}
		return result;
	}

	/**
	 * 解析数据库传输包到命令的方法<br>
	 * 
	 * @param redisPacket
	 *            数据库传输包
	 * @return 命令列表
	 */
	private List<RedisCommand> parserRedisDataBaseTransferPacket(RedisPacket redisPacket) {
		List<RedisCommand> result = new ArrayList<RedisCommand>();
		RedisDataBaseTransferPacket packet = (RedisDataBaseTransferPacket) redisPacket;
		Collection<RedisDB> redisDBs = packet.getRedisDbs();
		for (RedisDB redisDB : redisDBs) {
			// 添加选择数据库命令
			RedisCommand selectDBCommand = new RedisCommand(SELECT);
			selectDBCommand.setKey(String.valueOf(redisDB.getNum()));
			result.add(selectDBCommand);

			// 添加rdb中解析出的命令列表
			List<RedisCommand> commands = redisDB.getCommands();
			result.addAll(commands);
		}
		return result;
	}

}
