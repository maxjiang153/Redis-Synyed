package com.wmz7year.synyed.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.job.SynJob;

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
	
	/**
	 * 同步任务对象
	 */
	private SynJob synJob;

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
		RedisServer srcServer = new RedisServer(srcHost, srcPort, srcAuth);
		RedisServer descServer = new RedisServer(descHost, descPort, descAuth);
		logger.info("源Redis服务器：" + srcServer);
		logger.info("目标Redis服务器：" + descServer);
		// 创建同步任务
		 synJob = new SynJob(srcServer, descServer);
		Thread synJobThread = new Thread(synJob);
		synJobThread.setName("SynJob");
		synJobThread.setDaemon(true);
		synJobThread.start();
	}

	/*
	 * @see com.wmz7year.synyed.module.BasicModule#destroyModule()
	 */
	@Override
	public void destroyModule() throws Exception {
		if(synJob != null){
			synJob.shutdown();
		}
	}

}
