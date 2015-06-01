package com.huateng.file.listener;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.huateng.constant.Common;

import main.java.com.huateng.util.FileLoader;

/**
 * 将响应的报文装载在内存中
 * 
 * @author zeng.j
 * 
 */
public class RspFileLoadListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		String ctxpath = ctx.getContextPath();
		String sysPath = ctx.getRealPath("/");
		System.out.println("系统绝对路径" + sysPath);
		System.out.println("contextpath:" + ctxpath);
		// 将响应的报文装载在内存中
		FileLoader loader = new FileLoader();
		Map<String, String> map = loader.load(sysPath);

		ctx.setAttribute(Common.FKEY, map);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}

}
