package com.huateng.core.util;

import javax.servlet.http.HttpServletRequest;

import com.huateng.core.common.RemoteMsg;

public class IpUtil {

	
	public static RemoteMsg getRemoteMsg(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		RemoteMsg remote = new RemoteMsg();
		remote.setIp(ip);
		
		remote.setRequestURL(request.getRequestURL().toString());
		return remote;
	}
}
