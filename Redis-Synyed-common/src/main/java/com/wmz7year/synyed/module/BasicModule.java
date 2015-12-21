package com.wmz7year.synyed.module;

/**
 * 基础的模块对象
 * 
 * @Title: BasicModule.java
 * @Package com.wmz7year.synyed.module
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月21日 下午5:51:45
 * @version V1.0
 */
public abstract class BasicModule implements Module {
	/*
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		initialize();
	}

	/*
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		destroyModule();
	}

	/**
	 * 执行初始化的方法<br>
	 * 当spring初始化模块后会调用该方法
	 * 
	 * @throws Exception
	 *             当初始化出现问题时抛出该异常
	 */
	public abstract void initialize() throws Exception;

	/**
	 * 销毁模块的方法
	 * 
	 * @throws Exception
	 *             当销毁过程出现问题时抛出该异常
	 */
	public abstract void destroyModule() throws Exception;

}
