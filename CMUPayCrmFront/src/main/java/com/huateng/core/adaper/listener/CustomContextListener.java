package com.huateng.core.adaper.listener;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

public class CustomContextListener extends ContextLoaderListener {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		String realPath = event.getServletContext().getRealPath("/");
		System.setProperty("web.root", realPath);

		// 获取ApplicationContext并设置ServiceFactory
		WebApplicationContext context = super.getCurrentWebApplicationContext();
		ServiceFactory.getInstance().setApplicationContext(context);
		logger.debug("系统初始化成功......");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		System.clearProperty("web.root");
		super.contextDestroyed(event);
	}
}
