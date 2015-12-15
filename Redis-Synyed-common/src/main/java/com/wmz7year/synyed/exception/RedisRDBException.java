package com.wmz7year.synyed.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 针对redis rdb文件解析处理过程中出现的问题抛出该异常信息
 * 
 * @Title: RedisRDBException.java
 * @Package com.wmz7year.synyed.exception
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月15日 上午10:55:32
 * @version V1.0
 */
public class RedisRDBException extends Exception {
	private static final long serialVersionUID = -8089839086790389424L;
	private Throwable nestedThrowable = null;

	public RedisRDBException() {
		super();
	}

	public RedisRDBException(String msg) {
		super(msg);
	}

	public RedisRDBException(Throwable nestedThrowable) {
		super(nestedThrowable);
		this.nestedThrowable = nestedThrowable;
	}

	public RedisRDBException(String msg, Throwable nestedThrowable) {
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