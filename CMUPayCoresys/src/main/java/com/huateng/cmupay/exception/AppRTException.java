package com.huateng.cmupay.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cmt
 * @descriptioin 应用运行期异常
 */
public class AppRTException extends RuntimeException {
	public static final long serialVersionUID = 0L;
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String code;
	private Object[] args;
	private String textMessage;



	public AppRTException(String code, String msg) {
		super(code + ": " + msg);
		this.code = code;
	}

	public AppRTException(String code, String msg, Throwable cause) {
		super(code + ": " + msg, cause);
		this.code = code;
	}

	
	 public AppRTException(final String code, final Object[] args, final Throwable cause) {
		    super(code + ": ",cause);
	        this.code = code;
	        this.args = args;
	        logger.error(code, args, cause);
	    }

	

	public AppRTException(Throwable cause) {
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