package com.huateng.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务层异常处理类
 * 
 * @author Gary
 * 
 */
public class ServiceException extends Exception {
	private static final long serialVersionUID = 622540166703606667L;
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 错误码
	 */
	private String code;
	/**
	 * 错误描述
	 */
	private String msg;

	public ServiceException() {
		super();
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(String code, String msg) {
		super(code + ":" + msg);
		this.code = code;
		this.msg = msg;
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
