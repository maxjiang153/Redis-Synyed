package com.wmz7year.synyed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Synyed Agent主程序启动类<br>
 * 
 * @Title: Booter.java
 * @Package com.wmz7year.synyed
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月10日 下午3:14:45
 * @version V1.0
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Booter {
	public static void main(String[] args) {
		SpringApplication.run(Booter.class, args);
	}
}
