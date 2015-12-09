package com.wmz7year.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * RESP协议测试<br>
 * 
 * @Title: RESPTest.java
 * @Package com.wmz7year.test
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月9日 下午3:51:03
 * @version V1.0
 */
public class RESPTest extends TestCase {
	/**
	 * 服务器IP
	 */
	private String serverHost = "127.0.0.1";
	/**
	 * 服务器端口
	 */
	private int port = 6380;

	public RESPTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(RESPTest.class);
	}

	public void testResp() {
		try {
			Socket socket = new Socket(serverHost, port);
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			os.write("sync\r\n".getBytes());
			os.flush();
			int flag = 0;
			byte[] buffer = new byte[1024];
			while ((flag = is.read(buffer, 0, buffer.length)) != -1) {
				byte[] data = new byte[flag];
				System.arraycopy(buffer, 0, data, 0, flag);
				System.out.println("data:" + new String(data));
				System.out.println("array:" + Arrays.toString(data));
			}
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertTrue(true);
	}
}
