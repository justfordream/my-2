package com.huateng.cmupay.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cmt
 * @description 应用业务层异常
 */
public class AppBizException extends Exception {
	public static final long serialVersionUID = 1L;
	
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 异常码
	 */
	private String code;
	
	/**
	 * 参数数组
	 */
	private Object[] args;
	
	/**
	 * 文本信息
	 */
	private String textMessage;

	public AppBizException() {
	}

	

	public AppBizException(String code, String msg) {
		super(code + ": " + msg);
		this.code = code;
	}
	
	public AppBizException(String code, Throwable cause) {
		super(code + ": " + cause.getMessage());
		this.code = code;
	
	}


	public AppBizException(String code, String msg, Throwable cause) {
		super(code + ": " + msg, cause);
		this.code = code;
	}
	
	 public AppBizException(final String code, final Object[] args, final Throwable cause) {
		
	        super(code + ": ",cause);
	        this.code = code;
	        this.args = args;
	        logger.error(code, args, cause);
	    }

	

	public AppBizException(Throwable cause) {
		super(cause);
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String string) {
		this.code = string;
	}

	public Object[] getArgs() {
		return this.args;
	}

	public void setArgs(Object[] objects) {
		this.args = objects;
	}

	public String getTextMessage() {
		return this.textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}
}