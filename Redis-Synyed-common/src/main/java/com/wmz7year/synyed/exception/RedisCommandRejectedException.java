package com.wmz7year.synyed.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Redis命令拦截异常<br>
 * 该异常用于Redis命令拦截过滤器<br>
 * 当抛出该异常时说明对应的命令不应该发送到目标Redis服务器上
 * 
 * @Title: RedisCommandRejectedException.java
 * @Package com.wmz7year.synyed.exception
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 下午2:26:40
 * @version V1.0
 */
public class RedisCommandRejectedException extends Exception {
	private static final long serialVersionUID = -8089839086790389424L;
	private Throwable nestedThrowable = null;

	public RedisCommandRejectedException() {
		super();
	}

	public RedisCommandRejectedException(String msg) {
		super(msg);
	}

	public RedisCommandRejectedException(Throwable nestedThrowable) {
		super(nestedThrowable);
		this.nestedThrowable = nestedThrowable;
	}

	public RedisCommandRejectedException(String msg, Throwable nestedThrowable) {
		super(msg, nestedThrowable);
		this.nestedThrowable = nestedThrowable;
	}

	/*
	 * @see java.lang.Throwable#printStackTrace()
	 */
	@Override
	public void printStackTrace() {
		super.printStackTrace();
		if (nestedThrowable != null) {
			nestedThrowable.printStackTrace();
		}
	}

	/*
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	@Override
	public void printStackTrace(PrintStream ps) {
		super.printStackTrace(ps);
		if (nestedThrowable != null) {
			nestedThrowable.printStackTrace(ps);
		}
	}

	/*
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	@Override
	public void printStackTrace(PrintWriter pw) {
		super.printStackTrace(pw);
		if (nestedThrowable != null) {
			nestedThrowable.printStackTrace(pw);
		}
	}
}
