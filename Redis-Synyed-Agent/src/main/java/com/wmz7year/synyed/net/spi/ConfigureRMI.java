package com.wmz7year.synyed.net.spi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

/**
 * 配置RMI支持的bean
 * 
 * @Title: ConfigureRMI.java
 * @Package com.laimiya.push.spi
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月8日 上午9:58:16
 * @version V1.0
 */
@Configuration
public class ConfigureRMI {
	/**
	 * RMI服务地址
	 */
	@Value("${server.rmi.host}")
	private String rmiHost;

	/**
	 * rmi监听端口
	 */
	@Value("${server.rmi.port}")
	private Integer rmiPort;

	@Bean
	public RmiRegistryFactoryBean rmiRegistry() {
		// 设置jmx需要的系统参数
		setJMXProperties();

		final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
		rmiRegistryFactoryBean.setPort(rmiPort);
		rmiRegistryFactoryBean.setAlwaysCreate(true);
		return rmiRegistryFactoryBean;
	}

	private void setJMXProperties() {
		System.setProperty("java.rmi.server.hostname", rmiHost);
		System.setProperty("com.sun.management.jmxremote.port", String.valueOf(rmiHost));
		System.setProperty("com.sun.management.jmxremote.ssl.need.client.auth", "false");
		System.setProperty("com.sun.management.jmxremote.ssl", "false");
		System.setProperty("com.sun.management.jmxremote.authenticate", "false");
	}

	@Bean
	@DependsOn("rmiRegistry")
	public ConnectorServerFactoryBean connectorServerFactoryBean() throws Exception {
		final ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
		connectorServerFactoryBean.setObjectName("connector:name=rmi");
		connectorServerFactoryBean.setServiceUrl(
				String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", rmiHost, rmiPort, rmiHost, rmiPort));
		return connectorServerFactoryBean;
	}
}
