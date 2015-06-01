package com.huateng.core.exception;

/**
 * 异常处理
 * 
 * @author Gary
 * 
 */
public class AppException extends Exception {

	private static final long serialVersionUID = -1983226404550966502L;
	/**
	 * 错误码
	 */
	private String code;
	/**
	 * 错误信息
	 */
	private String msg;

	public AppException() {
		super();
	}

	public AppException(String code) {
		super(code);
		this.code = code;
	}

	public AppException(Throwable cause) {
		super(cause.getMessage());
	}

	public AppException(String code, String msg) {
		super(code + ":" + msg);
		this.code = code;
		this.msg = msg;
	}

	public AppException(String code, Throwable cause) {
		super(code + ":" + cause.getMessage());
		this.code = code;
		this.msg = cause.getMessage();
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
