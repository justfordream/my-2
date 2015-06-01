package com.huateng.crm.bean.message.head;

/**
 * 应答节点
 * 
 * @author Gary
 * 
 */
public class Response {
	/**
	 * 应答/错误类型(参见应答/错误类型表)
	 */
	private String RspType;
	/**
	 * 应答/错误代码(参见应答/错误代码表)
	 */
	private String RspCode;
	/**
	 * 应答/错误描述(应答或错误描述)
	 */
	private String RspDesc;

	public String getRspType() {
		return RspType;
	}

	public void setRspType(String rspType) {
		RspType = rspType;
	}

	public String getRspCode() {
		return RspCode;
	}

	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}

	public String getRspDesc() {
		return RspDesc;
	}

	public void setRspDesc(String rspDesc) {
		RspDesc = rspDesc;
	}

}
