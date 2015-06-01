package com.huateng.bean;

public class CCBData {

	private String posId;
	private String branchId;
	private String orderId;
	private String subId;
	private String payment;
	private String curCode;
	private String remark1;
	private String remark2;
	private String success;
	private String beggingNo;
	private String txType;// 0-签约类；1-支付类
	private String accNo;
	private String referer;
	private String CLIENTIP;
	private String sign;

	private String serverURL;
	private String backURL;
	private String status;
	private String errorMsg;

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSubId() {
		return subId;
	}

	public void setSubId(String subId) {
		this.subId = subId;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getCurCode() {
		return curCode;
	}

	public void setCurCode(String curCode) {
		this.curCode = curCode;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getBeggingNo() {
		return beggingNo;
	}

	public void setBeggingNo(String beggingNo) {
		this.beggingNo = beggingNo;
	}

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getCLIENTIP() {
		return CLIENTIP;
	}

	public void setCLIENTIP(String cLIENTIP) {
		CLIENTIP = cLIENTIP;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public String getBackURL() {
		return backURL;
	}

	public void setBackURL(String backURL) {
		this.backURL = backURL;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String assemlyPlainText() {
		StringBuffer sb = new StringBuffer();
		sb.append("POSID=" + this.posId + "&");
		sb.append("BRANCHID=" + this.branchId + "&");
		sb.append("ORDERID=" + this.orderId + "&");
		sb.append("SUBID="+this.subId+"&");
		sb.append("PAYMENT=" + this.payment + "&");
		sb.append("CURCODE=" + this.curCode + "&");
		sb.append("REMARK1=" + this.remark1 + "&");
		sb.append("REMARK2=" + this.remark2 + "&");
		sb.append("SUCCESS=" + this.success + "&");
		sb.append("BEGGINGNO=" + this.beggingNo + "&");
		sb.append("TXTYPE=" + this.txType + "&");
		sb.append("ACCNO=" + this.accNo + "&");
		sb.append("REFERER=" + this.referer + "&");
		sb.append("CLIENTIP=" + this.CLIENTIP + "");
		return sb.toString();
	}
}
