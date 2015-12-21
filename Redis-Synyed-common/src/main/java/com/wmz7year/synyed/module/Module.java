package com.wmz7year.synyed.module;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 模块对象接口<br>
 * 
 * @Title: Module.java
 * @Package com.wmz7year.synyed.module
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月21日 下午5:50:49
 * @version V1.0
 */
public interface Module extends InitializingBean, DisposableBean {

	/**
	 * 获取模块名称的方法<br>
	 * 
	 * @return 模块名称
	 */
	public String getName();

}
