package com.huateng.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Request工具类
 * 
 * @author Gary
 * 
 */
public class RequestUtil {

	private RequestUtil() {

	}

	/**
	 * Class<T> beanClass可以接受任何类型的javaBean,使用泛型调用者不用进行强转
	 * 
	 * @param request
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T requestToBean(HttpServletRequest request, Class<T> cls) {
		try {
			T bean = cls.newInstance();
			Map<String, String> map = request.getParameterMap();
			BeanUtils.populate(bean, map);
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 特殊字符转换
	 * 
	 * @param str
	 * @return
	 */
	public static String paseDecode(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}
		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 特殊字符转换
	 * 
	 * @param str
	 * @return
	 */
	public static String paseEncode(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
}
