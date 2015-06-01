package com.huateng.core.base;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.RemoteMsg;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Action 类
 * 
 * @author Gary
 * 
 */
public abstract class BaseAction extends ActionSupport {
	private static final long serialVersionUID = -2589955759985124631L;

	public abstract String receive();

	/**
	 * 获取HTTP Request对象
	 * 
	 * @return
	 */
	public HttpServletRequest getHttpRequest() {
		return ServletActionContext.getRequest();
	}

	/**
	 * 获取HTTP Response响应对象
	 * 
	 * @return
	 */
	public HttpServletResponse getHttpResponse() {
		return ServletActionContext.getResponse();
	}

	/**
	 * 设置响应公共信息
	 * 
	 * @param response
	 */
	public void settingResponse(HttpServletResponse response) {
		response.setCharacterEncoding(CoreConstant.MSG_ENCODING);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/html;charset=" + CoreConstant.MSG_ENCODING);
	}

	/**
	 * 设置请求公共信息
	 * 
	 * @param request
	 * @throws UnsupportedEncodingException
	 */
	public void settingRequest(HttpServletRequest request) throws UnsupportedEncodingException {
		request.setCharacterEncoding(CoreConstant.MSG_ENCODING);
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
		remote.setIp(ip);
		remote.setPort(request.getRemotePort());
		remote.setRequestURL(request.getRequestURL().toString());
		return remote;
	}
}
