package com.wmz7year.synyed.net.spi;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.wmz7year.synyed.worker.ProtocolSyncWorker;

/**
 * 动态创建运行时Spring代理bean的工厂类<br>
 * 如ProtocolSyncWorker等
 * 
 * @Title: RuntimeBeanManager.java
 * @Package com.wmz7year.synyed.net.spi
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 下午1:24:17
 * @version V1.0
 */
@Configuration
public class RuntimeBeanManager {

	/**
	 * 运行时创建Spring代理bean工厂类
	 */
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RuntimeBeanFactory runtimeBeanFactory() {
		return new RuntimeBeanFactory() {

			/*
			 * @see com.wmz7year.synyed.net.spi.RuntimeBeanFactory#
			 * createRuntimeProtocolSyncWorker()
			 */
			@Override
			public ProtocolSyncWorker createRuntimeProtocolSyncWorker() {
				return createProtocolSyncWorker();
			}
		};
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ProtocolSyncWorker createProtocolSyncWorker() {
		return new ProtocolSyncWorker();
	}
}
