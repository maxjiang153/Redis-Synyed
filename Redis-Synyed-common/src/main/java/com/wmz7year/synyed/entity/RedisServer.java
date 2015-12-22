package com.wmz7year.synyed.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * redis服务器信息对象<br>
 * 封装了redis服务器的地址信息与验证信息
 * 
 * @Title: RedisServer.java
 * @Package com.wmz7year.synyed.entity
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月21日 下午9:38:45
 * @version V1.0
 */
public class RedisServer {
	/**
	 * redis服务器地址
	 */
	private String host;
	/**
	 * redis服务器端口
	 */
	private int port;
	/**
	 * 验证密码
	 */
	private String authPassword;

	public RedisServer() {
		super();
	}

	public RedisServer(String host, int port, String authPassword) {
		super();
		this.host = host;
		this.port = port;
		if (!StringUtils.isEmpty(authPassword)) {
			this.authPassword = authPassword;
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		if (!StringUtils.isEmpty(authPassword)) {
			this.authPassword = authPassword;
		}
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authPassword == null) ? 0 : authPassword.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RedisServer other = (RedisServer) obj;
		if (authPassword == null) {
			if (other.authPassword != null)
				return false;
		} else if (!authPassword.equals(other.authPassword))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedisServer [host=" + host + ", port=" + port + ", useAuthPassword=" + (authPassword == null) + "]";
	}

}
