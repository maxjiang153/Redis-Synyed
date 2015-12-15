package com.wmz7year.synyed.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Redis数据传输解析过程中出现问题会抛出该异常
 * 
 * @Title: RedisProtocolException.java
 * @Package com.wmz7year.synyed.exception
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午3:05:20
 * @version V1.0
 */
public class RedisProtocolException extends Exception {
	private static final long serialVersionUID = -8089839086790389424L;
	private Throwable nestedThrowable = null;

	public RedisProtocolException() {
		super();
	}

	public RedisProtocolException(String msg) {
		super(msg);
	}

	public RedisProtocolException(Throwable nestedThrowable) {
		super(nestedThrowable);
		this.nestedThrowable = nestedThrowable;
	}

	public RedisProtocolException(String msg, Throwable nestedThrowable) {
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
