package com.huateng.bean;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class ShopData {
	@JsonProperty
	private String MerID; // 商户标识，移动商城标识
	@JsonProperty
	private String OrderID; // 订单标识
	@JsonProperty
	private String orderTime; // 交易日期时间
	@JsonProperty
	private String payment; // 订单总金额，单位为“分”
	@JsonProperty
	private String curType; // 币种，默认人民币，填“RMB” 01
	@JsonProperty
	private String chargeMoney; // 订单的充值金额，单位为“分”
	@JsonProperty
	private String IDValue; // 移动用户号码
	@JsonProperty
	private String IDType; // 移动用户标识的类型
	@JsonProperty
	private String homeProv; // 归属省
	@JsonProperty
	private String prodCnt; // 订单中的产品数量
	@JsonProperty
	private String prodId; // 充值产品编号
	@JsonProperty
	private String commision; // 佣金
	@JsonProperty
	private String rebateFee; // 积分返点费用
	@JsonProperty
	private String rodDision; // 产品折减金额
	@JsonProperty
	private String creditCardFee; // 信用卡费用
	@JsonProperty
	private String prodDiscount;
	@JsonProperty
	private String serviceFee; // 服务费用
	@JsonProperty
	private String activityNo; // 营销活动编号
	@JsonProperty
	private String ServerURL; // 统一支付系统向此URL发送充值结果通知
	@JsonProperty
	private String merVar; // 移动商城自定义，当统一支付系统通知支付结果时，携带此变量，移动商城可以用此变量维护session等
	@JsonProperty
	private String BackURL; // 用户界面返回URL，银行发送支付结果通知
	@JsonProperty
	private String lang; // 语言，默认为空
	@JsonProperty
	private String BankID; // 银行编码
	@JsonProperty
	private String mcode; // 填UPAY10001， 支付请求时填写
	@JsonProperty
	private String sig; // 移动商城的签名数据
	@JsonProperty
	private String prodShelfNO;// 商品上架编码
	@JsonProperty
	private String orderType;// 订单类型
	@JsonProperty
	private String payTimeoutTime;// 订单支付超时时间
	@JsonProperty
	private String shopMerId;// 移动商城的子商户号
	@JsonProperty
	private String reserve1; // 保留字段1
	@JsonProperty
	private String reserve2; // 保留字段2
	@JsonProperty
	private String reserve3; // 保留字段3
	@JsonProperty
	private String reserve4; // 保留字段4
	@JsonProperty
	private String status;

	private String statusDesc;// 状态描述
	@JsonProperty
	private String TransCode; // 交易代码
	@JsonProperty
	private String clientIp;

	@JsonProperty
	private Map<String, String> properties = new HashMap<String, String>(); // 扩展属性

	// 2014.5.19新增
	@JsonProperty
	private String bankAcctID;// 银行账号
	@JsonProperty
	private String customerInfo; // 银行卡验证信息及身份信息

	public String getMerID() {
		return MerID;
	}

	public void setMerID(String merID) {
		MerID = merID;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getCurType() {
		return curType;
	}

	public void setCurType(String curType) {
		this.curType = curType;
	}

	public String getChargeMoney() {
		return chargeMoney;
	}

	public void setChargeMoney(String chargeMoney) {
		this.chargeMoney = chargeMoney;
	}

	public String getIDValue() {
		return IDValue;
	}

	public void setIDValue(String iDValue) {
		IDValue = iDValue;
	}

	public String getIDType() {
		return IDType;
	}

	public void setIDType(String iDType) {
		IDType = iDType;
	}

	public String getHomeProv() {
		return homeProv;
	}

	public void setHomeProv(String homeProv) {
		this.homeProv = homeProv;
	}

	public String getProdCnt() {
		return prodCnt;
	}

	public void setProdCnt(String prodCnt) {
		this.prodCnt = prodCnt;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public String getCommision() {
		return commision;
	}

	public void setCommision(String commision) {
		this.commision = commision;
	}

	public String getRebateFee() {
		return rebateFee;
	}

	public void setRebateFee(String rebateFee) {
		this.rebateFee = rebateFee;
	}

	public String getRodDision() {
		return rodDision;
	}

	public void setRodDision(String rodDision) {
		this.rodDision = rodDision;
	}

	public String getCreditCardFee() {
		return creditCardFee;
	}

	public void setCreditCardFee(String creditCardFee) {
		this.creditCardFee = creditCardFee;
	}

	public String getProdDiscount() {
		return prodDiscount;
	}

	public void setProdDiscount(String prodDiscount) {
		this.prodDiscount = prodDiscount;
	}

	public String getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(String serviceFee) {
		this.serviceFee = serviceFee;
	}

	public String getActivityNo() {
		return activityNo;
	}

	public void setActivityNo(String activityNo) {
		this.activityNo = activityNo;
	}

	public String getServerURL() {
		return ServerURL;
	}

	public void setServerURL(String serverURL) {
		ServerURL = serverURL;
	}

	public String getMerVar() {
		return merVar;
	}

	public void setMerVar(String merVar) {
		this.merVar = merVar;
	}

	public String getBackURL() {
		return BackURL;
	}

	public void setBackURL(String backURL) {
		BackURL = backURL;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getBankID() {
		return BankID;
	}

	public void setBankID(String bankID) {
		BankID = bankID;
	}

	public String getMcode() {
		return mcode;
	}

	public void setMcode(String mcode) {
		this.mcode = mcode;
	}

	public String getSig() {
		return sig;
	}

	public void setSig(String sig) {
		this.sig = sig;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

	public String getProdShelfNO() {
		return prodShelfNO;
	}

	public void setProdShelfNO(String prodShelfNO) {
		this.prodShelfNO = prodShelfNO;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getPayTimeoutTime() {
		return payTimeoutTime;
	}

	public void setPayTimeoutTime(String payTimeoutTime) {
		this.payTimeoutTime = payTimeoutTime;
	}

	public String getShopMerId() {
		return shopMerId;
	}

	public void setShopMerId(String shopMerId) {
		this.shopMerId = shopMerId;
	}

	public String getReserve4() {
		return reserve4;
	}

	public void setReserve4(String reserve4) {
		this.reserve4 = reserve4;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransCode() {
		return TransCode;
	}

	public void setTransCode(String transCode) {
		TransCode = transCode;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getBankAcctID() {
		return bankAcctID;
	}

	public void setBankAcctID(String bankAcctID) {
		this.bankAcctID = bankAcctID;
	}

	public String getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(String customerInfo) {
		this.customerInfo = customerInfo;
	}

	/**
	 * 支付验签字符串
	 * 
	 * @return
	 */
	public String assemlyPayPlainText() {
		StringBuffer sb = new StringBuffer();
		sb.append("MerID=" + this.MerID + "|");
		sb.append("OrderID=" + this.OrderID + "|");
		sb.append("OrderTime=" + this.orderTime + "|");
		sb.append("Payment=" + this.payment + "|");
		sb.append("CurType=" + this.curType + "|");
		sb.append("ChargeMoney=" + this.chargeMoney + "|");
		sb.append("IDValue=" + this.IDValue + "|");
		sb.append("IDType=" + this.IDType + "|");
		sb.append("HomeProv=" + this.homeProv + "|");
		sb.append("ProdCnt=" + this.prodCnt + "|");
		sb.append("ProdID=" + this.prodId + "|");
		sb.append("Commision=" + this.commision + "|");
		sb.append("RebateFee=" + this.rebateFee + "|");
		sb.append("ProdDiscount=" + this.prodDiscount + "|");
		sb.append("CreditCardFee=" + this.creditCardFee + "|");
		sb.append("ServiceFee=" + this.serviceFee + "|");
		sb.append("ActivityNO=" + this.activityNo + "|");
		sb.append("ProdShelfNO=" + this.prodShelfNO + "|");
		sb.append("OrderType=" + this.orderType + "|");
		sb.append("PayTimeoutTime=" + this.payTimeoutTime + "|");
		sb.append("ShopMerId=" + this.shopMerId + "|");
		sb.append("Reserve1=" + this.reserve1 + "|");
		sb.append("Reserve2=" + this.reserve2 + "|");
		sb.append("Reserve3=" + this.reserve3 + "|");
		sb.append("Reserve4=" + this.reserve4 + "|");
		sb.append("MerURL=" + this.ServerURL + "|");
		sb.append("MerVAR=" + this.merVar + "|");
		sb.append("BackURL=" + this.BackURL + "|");
		sb.append("Lang=" + this.lang + "|");
		sb.append("BankID=" + this.BankID + "|");
		sb.append("CLIENTIP=" + this.clientIp + "|");
		sb.append("MCODE=" + this.mcode);
		return sb.toString();
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

}
