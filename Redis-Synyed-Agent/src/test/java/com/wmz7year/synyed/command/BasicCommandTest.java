package com.wmz7year.synyed.command;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmz7year.synyed.Booter;
import com.wmz7year.synyed.entity.RedisServer;
import com.wmz7year.synyed.module.ProtocolManager;

/**
 * 基础的命令测试类<br>
 * 具体在测试Redis命令的时候都需要继承该类
 * 
 * @author jiangwei (ydswcy513@gmail.com)
 * @since 2015年12月29日 上午10:29:31
 * @version V1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Booter.class)
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext
public abstract class BasicCommandTest {
	private static Logger logger = LoggerFactory.getLogger(BasicCommandTest.class);

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
	 * 同步管道管理模块
	 */
	@Autowired
	private ProtocolManager protocolManager;

	/**
	 * 源服务器信息
	 */
	protected RedisServer srcServer;

	/**
	 * 目标服务器信息
	 */
	protected RedisServer descServer;

	/**
	 * 设置测试类的方法<br>
	 * 创建源服务器与目标服务器的连接<br>
	 * 并且启动同步通道
	 */
	@Before
	public void setupTest() throws Exception {
		System.out.println("this:" + this);
		srcServer = new RedisServer(srcHost, srcPort, srcAuth);
		descServer = new RedisServer(descHost, descPort, descAuth);
		logger.info("等待同步RDB文件处理完成");
		while (!protocolManager.isRDBFileProcessed()) {
			TimeUnit.MILLISECONDS.sleep(1);
		}
		logger.info("RDB文件处理完成 开始测试");
	}

	/**
	 * 执行测试的方法<br>
	 * 1、在源Redis服务器执行命令<br>
	 * 2、获取目标Redis服务器的执行命令结果<br>
	 * 3、比较数据是否正确
	 */
	@Test
	public void doCommandSynCheckTest() throws Exception {
		logger.info("开始执行同步任务测试");
		// 在源服务器上执行写数据命令
		executeCommandOnSrcServer();
		// 在目标服务器上获取命令执行结果
		executeGetSynResultOnDescServer();
		// 检查命令执行结果
		checkSynResult();
	}

	/**
	 * 在源服务器上执行写数据命令
	 */
	public abstract void executeCommandOnSrcServer() throws Exception;

	/**
	 * 在目标服务器上获取命令执行结果
	 */
	public abstract void executeGetSynResultOnDescServer() throws Exception;

	/**
	 * 检查命令执行结果
	 */
	public abstract void checkSynResult() throws Exception;
}
