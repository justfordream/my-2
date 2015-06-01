package com.huateng.bean;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 建行支付数据
 * 
 * @author Gary
 * 
 */
public class CCBPayData {
	/**
	 * 交易码（区分签约和支付，网关和核心之间使用）
	 */
	@JsonProperty
	private String TransCode;
	/**
	 * 订单标识
	 */
	private String OrderID;
	/**
	 * 支付结果，标识支付是否成功以及失败的原因
	 */
	private String RspCode;
	/**
	 * 对于支付结果的描述信息
	 */
	private String RspInfo;
	/**
	 * 中国移动自定义，当银行通知支付结果时，携带此变量，中国移动可以用此变量维护session等
	 */
	private String MerVAR;
	/**
	 * 银行签名
	 */
	private String Sig;

	// TODO ADD
	private String HomeProv;
	// TODO ADD
	private String ServerURL;
	// TODO ADD
	private String backURL;
	private String status;

	private String error;

	public String getTransCode() {
		return TransCode;
	}

	public void setTransCode(String transCode) {
		TransCode = transCode;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getRspCode() {
		return RspCode;
	}

	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}

	public String getRspInfo() {
		return RspInfo;
	}

	public void setRspInfo(String rspInfo) {
		RspInfo = rspInfo;
	}

	public String getMerVAR() {
		return MerVAR;
	}

	public void setMerVAR(String merVAR) {
		MerVAR = merVAR;
	}

	public String getSig() {
		return Sig;
	}

	public void setSig(String sig) {
		Sig = sig;
	}

	public String getHomeProv() {
		return HomeProv;
	}

	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}

	public String getServerURL() {
		return ServerURL;
	}

	public void setServerURL(String serverURL) {
		ServerURL = serverURL;
	}

	public String getBackURL() {
		return backURL;
	}

	public void setBackURL(String backURL) {
		this.backURL = backURL;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * 签名字符串
	 * 
	 * @return
	 */
	public String assemlyPlainText() {
		StringBuffer sb = new StringBuffer();
		// sb.append("POSID=" + this.posId + "&");
		// sb.append("BRANCHID=" + this.branchId + "&");
		// sb.append("ORDERID=" + this.orderId + "&");
		// sb.append("PAYMENT=" + this.payment + "&");
		// sb.append("CURCODE=" + this.curCode + "&");
		// sb.append("REMARK1=" + this.remark1 + "&");
		// sb.append("REMARK2=" + this.remark2 + "&");
		// sb.append("ACC_TYPE=" + this.accType + "&");
		// sb.append("SUCCESS=" + this.success + "&");
		// sb.append("TYPE=" + this.type + "&");
		// sb.append("REFERER=" + this.referer + "&");
		// sb.append("CLIENTIP=" + this.clientIp + "&");
		// sb.append("ACCDATE=" + this.accDate + "&");
		// sb.append("USRMSG=" + this.usrMsg);
		return sb.toString();
	}
}
