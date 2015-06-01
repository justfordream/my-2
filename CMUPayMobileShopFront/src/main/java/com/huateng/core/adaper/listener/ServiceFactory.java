package com.huateng.core.adaper.listener;

import org.springframework.context.ApplicationContext;

public class ServiceFactory {
	private final static ServiceFactory instance = new ServiceFactory();

	private ApplicationContext applicationContext;

	private ServiceFactory() {
	}

	public static ServiceFactory getInstance() {
		return instance;
	}

	public synchronized void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 从Spring容器中获取服务参数
	 * 
	 * 
	 * @param serviceName
	 *            服务名称
	 * 
	 * @return
	 */
	public Object findService(String serviceName) {
		Object service = applicationContext.getBean(serviceName);
		return service;
	}
}
