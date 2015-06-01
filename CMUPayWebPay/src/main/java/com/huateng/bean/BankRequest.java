package com.huateng.bean;

public class BankRequest {
	/**
	 * 商户代码
	 */
	private String merChantId;
	/**
	 * 商户柜台代码
	 */
	private String posId;
	/**
	 * 分行代码
	 */
	private String branChId;
	/**
	 * 订单号
	 */
	private String orderId;
	/**
	 * 付款金额
	 */
	private String payMent;
	/**
	 * 交易编号
	 */
	private String beggingNo;
	/**
	 * 归属地编码
	 */
	private String area;
	/**
	 * 指定支付帐号
	 */
	private String payAcc;
	/**
	 * 用户类型
	 */
	private String custType;
	/**
	 * 缴费方式(移动用)
	 */
	private String payType;
	/**
	 * 充值阈值
	 */
	private String liMval;
	/**
	 * 充值额度
	 */
	private String payAmt;
	/**
	 * 币种
	 */
	private String curCode;
	/**
	 * 备注一
	 */
	private String remark1;
	/**
	 * 备注二
	 */
	private String remark2;
	/**
	 * 密钥
	 */
	private String pub32;
	/**
	 * 交易码
	 */
	private String txCode;
	/**
	 * 客户端IP
	 */
	private String clientiIp;
	/**
	 * 网关类型
	 */
	private String gateWay;
	/**
	 * MAC 校验域
	 */
	private String mac;
	/**
	 * 返回的URL
	 */
	private String backUrl;
	/**
	 * 0-签约类；1-支付类
	 */
	private String bussType;
	
	
	public String getMerChantId() {
		return merChantId;
	}

	public void setMerChantId(String merChantId) {
		this.merChantId = merChantId;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public String getBranChId() {
		return branChId;
	}

	public void setBranChId(String branChId) {
		this.branChId = branChId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPayMent() {
		return payMent;
	}

	public void setPayMent(String payMent) {
		this.payMent = payMent;
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

	public String getLiMval() {
		return liMval;
	}

	public void setLiMval(String liMval) {
		this.liMval = liMval;
	}

	public String getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(String payAmt) {
		this.payAmt = payAmt;
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

	public String getPub32() {
		return pub32;
	}

	public void setPub32(String pub32) {
		this.pub32 = pub32;
	}

	public String getTxCode() {
		return txCode;
	}

	public void setTxCode(String txCode) {
		this.txCode = txCode;
	}

	public String getClientiIp() {
		return clientiIp;
	}

	public void setClientiIp(String clientiIp) {
		this.clientiIp = clientiIp;
	}

	public String getGateWay() {
		return gateWay;
	}

	public void setGateWay(String gateWay) {
		this.gateWay = gateWay;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getBussType() {
		return bussType;
	}

	public void setBussType(String bussType) {
		this.bussType = bussType;
	}

}
