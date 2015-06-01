package com.huateng.core.base;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

/**
 * action 基类
 * 
 * @author Gary
 * 
 */
public abstract class BaseAction extends ActionSupport {

	private static final long serialVersionUID = -2589955759985124631L;

	public abstract String receive();

	/**
	 * 获取session
	 * 
	 * @return
	 */
	public HttpSession getSession() {

		HttpServletRequest request = ServletActionContext.getRequest();

		HttpSession session = request.getSession();

		return session;
	}

	/**
	 * 获取request
	 * 
	 * @return
	 */
	public HttpServletRequest getRequest() {

		HttpServletRequest request = ServletActionContext.getRequest();

		return request;
	}

	/**
	 * 获取request
	 * 
	 * @return
	 */
	public HttpServletResponse getResponse() {

		HttpServletResponse sesponse = ServletActionContext.getResponse();

		return sesponse;
	}

	/**
	 * Class<T> beanClass可以接受任何类型的javaBean,使用泛型调用者不用进行强转
	 * 
	 * @param request
	 * @param cls
	 * @return
	 */
	public <T> T requestToBean(HttpServletRequest request, Class<T> cls) {
		try {
			T bean = cls.newInstance();
			Map<?, ?> map = request.getParameterMap();
			BeanUtils.populate(bean, map);
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
