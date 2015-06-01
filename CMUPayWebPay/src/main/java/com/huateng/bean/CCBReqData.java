package com.huateng.bean;

/**
 * 建设银行请求数据
 * 
 * @author Gary
 * 
 */
public class CCBReqData {
	/**
	 * 交易码
	 */
	private String txCode;
	/**
	 * 商户代码
	 */
	private String merchantId;
	/**
	 * 柜台代码
	 */
	private String posId;
	/**
	 * 分行代码
	 */
	private String branchId;
	/**
	 * 定单号
	 */
	private String orderId;
	/**
	 * 付款金额
	 */
	private String payment;
	/**
	 * 交易编号
	 */
	private String beggingNo;
	/**
	 * 归属地编码
	 */
	private String area;
	/**
	 * 支付\签约账号
	 */
	private String payAcc;
	/**
	 * 用户类型
	 */
	private String custType;
	/**
	 * 缴费方式
	 */
	private String payType;
	/**
	 * 币种
	 */
	private String curCode;
	/**
	 * 客户端IP
	 */
	private String clientIp;
	/**
	 * 备注1
	 */
	private String remark1;
	/**
	 * 备注2
	 */
	private String remark2;
	/**
	 * 密钥后30位
	 */
	private String pub32;
	/**
	 * 网关类型
	 */
	private String gateway;

	/**
	 * 信息状态
	 */
	private String status;

	/**
	 * 错误描述
	 */
	private String error;
	// TODO ADD
	private String ServerURL;
	// TODO ADD
	private String backURL;

	public String getTxCode() {
		return txCode;
	}

	public void setTxCode(String txCode) {
		this.txCode = txCode;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

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

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getBeggingNo() {
		return beggingNo;
	}

	public void setBeggingNo(String beggingNo) {
		this.beggingNo = beggingNo;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPayAcc() {
		return payAcc;
	}

	public void setPayAcc(String payAcc) {
		this.payAcc = payAcc;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getCurCode() {
		return curCode;
	}

	public void setCurCode(String curCode) {
		this.curCode = curCode;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
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

	public String getPub32() {
		return pub32;
	}

	public void setPub32(String pub32) {
		this.pub32 = pub32;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
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
	 * 组织MD5字符串
	 * 
	 * @return
	 */
	public String make() {
		StringBuffer sb = new StringBuffer();
		sb.append("TXCODE=" + this.txCode + "&");
		sb.append("MERCHANTID=" + this.merchantId + "&");
		sb.append("POSID=" + this.posId + "&");
		sb.append("BRANCHID=" + this.branchId + "&");
		sb.append("ORDERID=" + this.orderId + "&");
		sb.append("PAYMENT=" + this.payment + "&");
		sb.append("BEGGINGNO=" + this.beggingNo + "&");
		sb.append("AREA=" + this.area + "&");
		sb.append("PAYACC=" + this.payAcc + "&");
		sb.append("PAYTYPE=" + this.payType + "&");
		sb.append("CURCODE=" + this.curCode + "&");
		sb.append("CLIENTIP=" + this.clientIp + "&");
		sb.append("REMARK1=" + this.remark1 + "&");
		sb.append("REMARK2=" + this.remark2 + "&");
		sb.append("GATEWAY=" + this.gateway + "&");
		sb.append("CUSTTYPE=" + this.custType + "&");
		return sb.toString();
	}
}
