package com.huateng.bean;

/**
 * 中国移动统一支付系统（Web子系统）->银行网银系统的“支付”交易<br>
 * 支付结果通知请求（HTTP POST）
 * 
 * @author Gary
 * 
 */
public class BankPayResultRequest {
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
	private String BankID;
	// TODO ADD
	private String MerID;
	// TODO ADD
	private String ServerURL;
	// TODO ADD
	private String backURL;

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

	public String getBankID() {
		return BankID;
	}

	public void setBankID(String bankID) {
		BankID = bankID;
	}

	public String getMerID() {
		return MerID;
	}

	public void setMerID(String merID) {
		MerID = merID;
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
	 * 签名字符串
	 * 
	 * @return
	 */
	public String assemlyPlainText() {
		StringBuffer sb = new StringBuffer();
//		sb.append("POSID=" + this.posId + "&");
//		sb.append("BRANCHID=" + this.branchId + "&");
//		sb.append("ORDERID=" + this.orderId + "&");
//		sb.append("PAYMENT=" + this.payment + "&");
//		sb.append("CURCODE=" + this.curCode + "&");
//		sb.append("REMARK1=" + this.remark1 + "&");
//		sb.append("REMARK2=" + this.remark2 + "&");
//		sb.append("ACC_TYPE=" + this.accType + "&");
//		sb.append("SUCCESS=" + this.success + "&");
//		sb.append("TYPE=" + this.type + "&");
//		sb.append("REFERER=" + this.referer + "&");
//		sb.append("CLIENTIP=" + this.clientIp + "&");
//		sb.append("ACCDATE=" + this.accDate + "&");
//		sb.append("USRMSG=" + this.usrMsg);
		return sb.toString();
	}
}
