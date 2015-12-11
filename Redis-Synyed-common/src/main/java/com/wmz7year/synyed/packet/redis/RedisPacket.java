package com.wmz7year.synyed.packet.redis;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Redis数据包<br>
 * 该数据包为最原始的对象
 * 
 * @Title: RedisPacket.java
 * @Package com.wmz7year.synyed.packet.redis
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午3:11:43
 * @version V1.0
 */
public abstract class RedisPacket implements Externalizable {
	/**
	 * redis命令
	 */
	protected String command;

	public RedisPacket(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	/*
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub

	}

	/*
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

	}

}
