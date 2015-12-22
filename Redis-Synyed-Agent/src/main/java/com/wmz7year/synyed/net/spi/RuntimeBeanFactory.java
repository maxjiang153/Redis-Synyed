package com.wmz7year.synyed.net.spi;

import com.wmz7year.synyed.worker.ProtocolSyncWorker;

/**
 * 动态创建Spring bean的工厂类接口
 * 
 * @Title: RuntimeBeanFactory.java
 * @Package com.wmz7year.synyed.net.spi
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 下午1:25:31
 * @version V1.0
 */
public interface RuntimeBeanFactory {

	/**
	 * 创建同步管道执行工作类对象的方法<br>
	 * 
	 * @return 同步管道执行工作类对象
	 */
	public ProtocolSyncWorker createRuntimeProtocolSyncWorker();
}
