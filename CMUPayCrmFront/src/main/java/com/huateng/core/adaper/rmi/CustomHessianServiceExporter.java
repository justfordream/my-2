package com.huateng.core.adaper.rmi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.caucho.HessianServiceExporter;

public class CustomHessianServiceExporter extends HessianServiceExporter {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		try {
			RemoteMsg msg = this.getRemoteMsg(request);
			logger.debug("......接收到[" + msg.getRemoteIp() + "]的请求......");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return;
		}
		super.handleRequest(request, response);
	}

	/**
	 * 获取远程信息
	 * 
	 * @param request
	 * @return
	 */
	public RemoteMsg getRemoteMsg(HttpServletRequest request) {
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
		remote.setRemoteIp(ip);
		remote.setRemotePort(request.getRemotePort());
		remote.setRemoteUrl(request.getRequestURL().toString());
		remote.setRemoteHost(request.getRemoteHost());
		remote.setRemoteUser(request.getRemoteUser());
		return remote;
	}
}
