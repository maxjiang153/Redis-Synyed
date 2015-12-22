package com.wmz7year.synyed.module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.net.spi.RuntimeBeanFactory;
import com.wmz7year.synyed.worker.ProtocolSyncWorker;

/**
 * 同步管道管理模块<br>
 * 
 * @Title: ProtocolManager.java
 * @Package com.wmz7year.synyed.module
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月22日 上午9:17:30
 * @version V1.0
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProtocolManager extends BasicModule {
	private static final Logger logger = LoggerFactory.getLogger(ProtocolManager.class);

	/**
	 * 动态创建bean的工厂类
	 */
	@Autowired
	private RuntimeBeanFactory runtimeBeanFactory;

	/**
	 * 管道同步工作线程池对象
	 */
	private ExecutorService protocolSyncWorkerThreadPool;

	/**
	 * 管道同步工作线程池大小
	 */
	@Value("${server.pool.protocol.syncworker.size}")
	private int syncWorkerSize;

	/**
	 * 源服务器地址
	 */
	@Value("${protocol.src.host}")
	private String srcHost;

	/**
	 * 源服务器端口
	 */
	@Value("${protocol.src.port}")
	private int srcPort;

	/**
	 * 源服务器验证密码
	 */
	@Value("${protocol.src.auth}")
	private String srcAuth;

	/**
	 * 目标服务器地址
	 */
	@Value("${protocol.desc.host}")
	private String descHost;

	/**
	 * 目标服务器端口
	 */
	@Value("${protocol.desc.port}")
	private int descPort;

	/**
	 * 目标服务验证密码
	 */
	@Value("${protocol.desc.auth}")
	private String descAuth;

	/*
	 * @see com.wmz7year.synyed.module.Module#getName()
	 */
	@Override
	public String getName() {
		return "ProtocolManager";
	}

	/*
	 * @see com.wmz7year.synyed.module.BasicModule#initialize()
	 */
	@Override
	public void initialize() throws Exception {
		// 初始化同步工作线程线程池
		logger.info("创建管道同步工作线程线程池  大小：" + syncWorkerSize);
		protocolSyncWorkerThreadPool = Executors.newFixedThreadPool(syncWorkerSize, new ThreadFactory() {
			private AtomicInteger counter = new AtomicInteger(0);

			/*
			 * @see
			 * java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
			 */
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName("Protocol-Sync-Worker-Thread" + counter.getAndIncrement());
				return thread;
			}
		});

		// TODO 以后会去掉这部分代码

		RedisServer srcServer = new RedisServer(srcHost, srcPort, srcAuth);
		RedisServer descServer = new RedisServer(descHost, descPort, descAuth);

		ProtocolSyncWorker syncWorker = runtimeBeanFactory.createRuntimeProtocolSyncWorker();
		syncWorker.setSrcRedis(srcServer);
		syncWorker.setDescRedis(descServer);
		syncWorker.start();
	}

	/*
	 * @see com.wmz7year.synyed.module.BasicModule#destroyModule()
	 */
	@Override
	public void destroyModule() throws Exception {
		// 关闭同步工作线程线程池
		if (protocolSyncWorkerThreadPool != null) {
			protocolSyncWorkerThreadPool.shutdown();
		}
	}

}
