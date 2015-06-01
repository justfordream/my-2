package com.huateng.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 过滤特殊字符串
 * 
 * @author yansen.huang
 * 
 */
public class StringFilter implements Filter {

	private static ResourceBundle bundle = ResourceBundle
			.getBundle("property/jms");

	private static Logger logger = (Logger) LoggerFactory
			.getLogger(StringFilter.class);
	private static List<String> specialList = new ArrayList<String>();

	// 非法词、敏感词、特殊字符、配置在初始化参数中
	public static List<String> getSpecialList() {
		String specailString = bundle.getString("SPECIAL_FILETER_LIST");
		logger.info("######需要过滤的字符串########" + specailString);
		String[] specail = specailString.split(",");
		for (String spec : specail) {
			specialList.add(spec);
		}
		return specialList;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		getSpecialList();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		System.out.println("##############过滤请求的字符串#############");
//		//转换成实例的请求和响应对象
//        HttpServletRequest req = (HttpServletRequest)request;
//		String param = "";
//		String paramValue = "";
//		java.util.Enumeration params = req.getParameterNames();
//		// 循环读取参数
//		while (params.hasMoreElements()) {
//			param = (String) params.nextElement(); // 获取请求中的参数
//			String[] values = req.getParameterValues(param);// 获得每个参数对应的值
//			for (int i = 0; i < values.length; i++) {
//				System.out.println("转换前的参数" + values[i]);
//				for (String key : specialList) {
//					values[i] = values[i].replace(key, "");
//				}
//				paramValue = values[i];
//			}
//			// 把转义后的参数重新放回request中
//			System.out.println("转换后的值" + param + "====" + paramValue);
//			req.setAttribute(param, paramValue);			  
//		}
        //继续执行
        chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}
}
