package com.huateng.third.filter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.huateng.core.common.CoreConstant;

public class HttpStringFilter {

	/**
	 * 过滤单个字符串
	 * 
	 * @param params
	 * @return
	 */
	public static String paseStringFilter(String params, String specailString) {
		List<String> specialList = new ArrayList<String>();
		for (int i = 0; i < specailString.length(); i++) {
			specialList.add(String.valueOf(specailString.charAt(i)));
		}
		for (String key : specialList) {
			params = params.replace(key, "");
		}
		return params;
	}

	/**
	 * 过滤特殊字符串
	 * 
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map<String, String> filterRequestParams(
			HttpServletRequest request, String specailString) {
		List<String> specialList = new ArrayList<String>();
		for (int i = 0; i < specailString.length(); i++) {
			specialList.add(String.valueOf(specailString.charAt(i)));
		}
		String param = "";
		String paramValue = "";
		java.util.Enumeration params = request.getParameterNames();
		Map<String, String> paramsMap = new HashMap<String, String>();
		// 循环读取参数
		while (params.hasMoreElements()) {
			param = (String) params.nextElement(); // 获取请求中的参数
			String[] values = request.getParameterValues(param);// 获得每个参数对应的值
			for (int i = 0; i < values.length; i++) {
				for (String key : specialList) {
					if (values[i].contains(key)) {
						values[i] = values[i].replace(key, "");
					}
				}
				paramValue = values[i];
			}
			// 把转义后的参数重新放回request中
			paramsMap.put(param, paramValue);
		}
		return paramsMap;
	}
	
	/**
	 * 过滤特殊字符串
	 * 并将报文通过特定编码组装成Map对象
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map<String, String> filterRequestParamsForEncoding(
			HttpServletRequest request, String specailString, String encoding) throws UnsupportedEncodingException {
		List<String> specialList = new ArrayList<String>();
		for (int i = 0; i < specailString.length(); i++) {
			specialList.add(String.valueOf(specailString.charAt(i)));
		}
		String param = "";
		String paramValue = "";
		java.util.Enumeration params = request.getParameterNames();
		Map<String, String> paramsMap = new HashMap<String, String>();
		// 循环读取参数
		while (params.hasMoreElements()) {
			param = (String) params.nextElement(); // 获取请求中的参数
			paramValue = request.getParameter(param);// 获得每个参数对应的值
			
			if(paramValue != null) {
				paramValue = new String(paramValue.getBytes(CoreConstant.MSG_ENCODING), encoding);
			}
			
			for (String key : specialList) {
				if (paramValue.contains(key)) {
					paramValue = paramValue.replace(key, "");
				}
			}
			
			// 把转义后的参数重新放回request中
			paramsMap.put(param, paramValue);
		}
		return paramsMap;
	}

}
