package com.huateng.cmupay.parseMsg.webgate.vo.crm;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 省公司网厅->中国移动统一支付系统的“支付”交易 支付请求
 * 
 * @author Gary
 */
public class CmuPayRequest {
	/**
	 * 内部交易代码
	 */
	@JsonProperty
	
	private String TransCode;

	/**
	 * 商户标识
	 */
	@JsonProperty
	private String MerID;
	/**
	 * 订单标识
	 */
	@JsonProperty
	private String OrderID;

	/**
	 * 交易日期时间
	 */
	@JsonProperty
	private String OrderTime;
	/**
	 * 订单金额，单位为“分”
	 */
	@JsonProperty
	private String Payed;
	/**
	 * 币种，默认人民币
	 */
	@JsonProperty
	private String CurType;
	/**
	 * 为该用户充值
	 */
	@JsonProperty
	private String IDValue;
	/**
	 * 中国移动充值用户的标识的类型
	 */
	@JsonProperty
	private String IDType;

	/**
	 * 统一支付系统向此URL发送支付结果通知
	 */
	@JsonProperty
	private String MerURL;

	/**
	 * 省公司自定义，当统一支付系统通知支付结果时，携带此变量，省公司可以用此变量维护session等
	 */
	@JsonProperty
	private String MerVAR;

	/**
	 * 用户界面返回URL
	 */
	@JsonProperty
	private String BackURL;

	/**
	 * 语言，默认为中文版
	 */
	@JsonProperty
	private String Lang;

	/**
	 * 省公司在银行侧的签名数据
	 */
	@JsonProperty
	private String Sig;

	/**
	 * 银行编码
	 */
	@JsonProperty
	private String BankID;
	/**
	 * 客户端IP
	 */
	@JsonProperty
	private String CLIENTIP;
	/**
	 * UPAY10001 支付请求时填写
	 */
	@JsonProperty
	private String MCODE;
	/**
	 * TODO 归属省
	 */
	@JsonProperty
	private String HomeProv;

	@JsonIgnore
	public String getMerID() {
		return MerID;
	}

	@JsonIgnore
	public void setMerID(String merID) {
		MerID = merID;
	}

	@JsonIgnore
	public String getOrderID() {
		return OrderID;
	}

	@JsonIgnore
	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	@JsonIgnore
	public String getOrderTime() {
		return OrderTime;
	}

	@JsonIgnore
	public void setOrderTime(String orderTime) {
		OrderTime = orderTime;
	}

	@JsonIgnore
	public String getPayed() {
		return Payed;
	}

	@JsonIgnore
	public void setPayed(String payed) {
		Payed = payed;
	}

	@JsonIgnore
	public String getCurType() {
		return CurType;
	}

	@JsonIgnore
	public void setCurType(String curType) {
		CurType = curType;
	}

	@JsonIgnore
	public String getIDValue() {
		return IDValue;
	}

	@JsonIgnore
	public void setIDValue(String iDValue) {
		IDValue = iDValue;
	}

	@JsonIgnore
	public String getIDType() {
		return IDType;
	}

	@JsonIgnore
	public void setIDType(String iDType) {
		IDType = iDType;
	}

	@JsonIgnore
	public String getMerURL() {
		return MerURL;
	}

	@JsonIgnore
	public void setMerURL(String merURL) {
		MerURL = merURL;
	}

	@JsonIgnore
	public String getMerVAR() {
		return MerVAR;
	}

	@JsonIgnore
	public void setMerVAR(String merVAR) {
		MerVAR = merVAR;
	}

	@JsonIgnore
	public String getBackURL() {
		return BackURL;
	}

	@JsonIgnore
	public void setBackURL(String backURL) {
		BackURL = backURL;
	}

	@JsonIgnore
	public String getLang() {
		return Lang;
	}

	@JsonIgnore
	public void setLang(String lang) {
		Lang = lang;
	}

	@JsonIgnore
	public String getSig() {
		return Sig;
	}

	@JsonIgnore
	public void setSig(String sig) {
		Sig = sig;
	}

	@JsonIgnore
	public String getBankID() {
		return BankID;
	}

	@JsonIgnore
	public void setBankID(String bankID) {
		BankID = bankID;
	}

	@JsonIgnore
	public String getCLIENTIP() {
		return CLIENTIP;
	}

	@JsonIgnore
	public void setCLIENTIP(String cLIENTIP) {
		CLIENTIP = cLIENTIP;
	}

	@JsonIgnore
	public String getMCODE() {
		return MCODE;
	}

	@JsonIgnore
	public void setMCODE(String mCODE) {
		MCODE = mCODE;
	}

	@JsonIgnore
	public String getHomeProv() {
		return HomeProv;
	}

	@JsonIgnore
	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}
	
	@JsonIgnore
	public String getTransCode() {
		return TransCode;
	}

	@JsonIgnore
	public void setTransCode(String transCode) {
		TransCode = transCode;
	}

}
