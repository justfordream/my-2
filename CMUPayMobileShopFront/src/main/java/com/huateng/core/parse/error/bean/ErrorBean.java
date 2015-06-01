package com.huateng.core.parse.error.bean;

/**
 * 错误码
 * 
 * @author Gary
 * 
 */
public class ErrorBean {
	/**
	 * 内部错误码
	 */
	private String innerCode;
	/**
	 * 外部错误码
	 */
	private String outerCode;
	/**
	 * 错误描述
	 */
	private String desc;

	public String getInnerCode() {
		return innerCode;
	}

	public void setInnerCode(String innerCode) {
		this.innerCode = innerCode;
	}

	public String getOuterCode() {
		return outerCode;
	}

	public void setOuterCode(String outerCode) {
		this.outerCode = outerCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
