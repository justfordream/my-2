package com.huateng.bean;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class CmuData {
	@JsonProperty
	private String SessionID;
	@JsonProperty
	private String MerID;
	@JsonProperty
	private String OrderID;
	@JsonProperty
	private String OrderTime;
	@JsonProperty
	private String Payed;
	@JsonProperty
	private String CurType;
	@JsonProperty
	private String MerVAR;
	@JsonProperty
	private String MerURL;
	@JsonProperty
	private String IDType;
	@JsonProperty
	private String IDValue;
	@JsonProperty
	private String ServerURL;
	@JsonProperty
	private String BackURL;
	@JsonProperty
	private String Lang;
	@JsonProperty
	private String Sig;
	@JsonProperty
	private String CmuID;
	@JsonProperty
	private String BankID;
	@JsonProperty
	private String SubTime;
	@JsonProperty
	private String TransactionID;
	@JsonProperty
	private String OrigDomain;
	@JsonProperty
	private String CLIENTIP;
	@JsonProperty
	private String MCODE;
	@JsonProperty
	private String TransCode;
	@JsonProperty
	private String PayType;
	@JsonProperty
	private String RechThreshold="";
	@JsonProperty
	private String RechAmount="";
	/**
	 * 信息状态
	 */
	@JsonIgnore
	private String status;

	/**
	 * 错误描述
	 */
	@JsonIgnore
	private String errorMsg;

	@JsonIgnore
	public String getSessionID() {
		return SessionID;
	}

	@JsonIgnore
	public void setSessionID(String sessionID) {
		SessionID = sessionID;
	}

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
	public String getMerVAR() {
		return MerVAR;
	}

	@JsonIgnore
	public void setMerVAR(String merVAR) {
		MerVAR = merVAR;
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
	public String getIDValue() {
		return IDValue;
	}

	@JsonIgnore
	public void setIDValue(String iDValue) {
		IDValue = iDValue;
	}

	@JsonIgnore
	public String getServerURL() {
		return ServerURL;
	}

	@JsonIgnore
	public void setServerURL(String serverURL) {
		ServerURL = serverURL;
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
	public String getCmuID() {
		return CmuID;
	}

	@JsonIgnore
	public void setCmuID(String cmuID) {
		CmuID = cmuID;
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
	public String getSubTime() {
		return SubTime;
	}

	@JsonIgnore
	public void setSubTime(String subTime) {
		SubTime = subTime;
	}

	@JsonIgnore
	public String getTransactionID() {
		return TransactionID;
	}

	@JsonIgnore
	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}

	@JsonIgnore
	public String getOrigDomain() {
		return OrigDomain;
	}

	@JsonIgnore
	public void setOrigDomain(String origDomain) {
		OrigDomain = origDomain;
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
	public String getTransCode() {
		return TransCode;
	}

	@JsonIgnore
	public void setTransCode(String transCode) {
		TransCode = transCode;
	}

	@JsonIgnore
	public String getStatus() {
		return status;
	}

	@JsonIgnore
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonIgnore
	public String getErrorMsg() {
		return errorMsg;
	}

	@JsonIgnore
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@JsonIgnore
	public String getPayType() {
		return PayType;
	}

	@JsonIgnore
	public void setPayType(String payType) {
		PayType = payType;
	}

	@JsonIgnore
	public String getRechThreshold() {
		return RechThreshold;
	}

	@JsonIgnore
	public void setRechThreshold(String rechThreshold) {
		RechThreshold = rechThreshold;
	}

	@JsonIgnore
	public String getRechAmount() {
		return RechAmount;
	}

	@JsonIgnore
	public void setRechAmount(String rechAmount) {
		RechAmount = rechAmount;
	}

	public String getMerURL() {
		return MerURL;
	}

	public void setMerURL(String merURL) {
		MerURL = merURL;
	}

	/**
	 * 签约验签字符串
	 * 
	 * @return
	 */
	public String assemlySignPlainText() {
		StringBuffer sb = new StringBuffer();
		sb.append("SESSIONID=" + this.SessionID + "|");
		sb.append("IDTYPE=" + this.IDType + "|");
		sb.append("IDVALUE=" + this.IDValue + "|");
		sb.append("SERVERURL=" + this.ServerURL + "|");
		sb.append("BACKURL=" + this.BackURL + "|");
		sb.append("LANG=" + this.Lang + "|");
		sb.append("BANKID=" + this.BankID + "|");
		sb.append("SUBTIME=" + this.SubTime + "|");
		sb.append("TRANSACTIONID=" + this.TransactionID + "|");
		sb.append("ORIGDOMAIN=" + this.OrigDomain + "|");
		sb.append("CLIENTIP=" + this.CLIENTIP + "|");
		sb.append("MCODE=" + this.MCODE + "|");
		sb.append("PAYTYPE=" + this.PayType + "|");
		sb.append("RECHTHRESHOLD=" + this.RechThreshold + "|");
		sb.append("RECHAMOUNT=" + this.RechAmount);
		return sb.toString();
	}

	/**
	 * 支付验签字符串
	 * 
	 * @return
	 */
	public String assemlyPayPlainText() {
		StringBuffer sb = new StringBuffer();
		sb.append("MERID=" + this.MerID + "|");
		sb.append("ORDERID=" + this.OrderID + "|");
		sb.append("ORDERTIME=" + this.OrderTime + "|");
		sb.append("PAYED=" + this.Payed + "|");
		sb.append("CURTYPE=" + this.CurType + "|");
		sb.append("IDVALUE=" + this.IDValue + "|");
		sb.append("IDTYPE=" + this.IDType + "|");
		sb.append("MERURL=" + this.ServerURL + "|");
		sb.append("MERVAR=" + this.MerVAR + "|");
		sb.append("BACKURL=" + this.BackURL + "|");
		sb.append("LANG=" + this.Lang + "|");
		sb.append("BANKID=" + this.BankID + "|");
		sb.append("CLIENTIP=" + this.CLIENTIP + "|");
		sb.append("MCODE=" + this.MCODE);
		return sb.toString();
	}
}
