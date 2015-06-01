package com.huateng.cmupay.parseMsg.webgate.vo.crm;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 网关支付支付结果通知
 * 
 * @author zeng.j
 * 
 */
public class CorePayResultNoticeReq {
	@JsonProperty
	private String MerID;
	@JsonProperty
	private String OrderID;
	@JsonProperty
	private String RspCode;
	@JsonProperty
	private String RspInfo;
	@JsonProperty
	private String MerVAR;
	@JsonProperty
	private String OrderTime;
	@JsonProperty
	private String Payed;
	@JsonProperty
	private String CurType;
	@JsonProperty
	private String MCODE;
	@JsonProperty
	private String Sig;
	@JsonProperty
	private String ServerURL;
	@JsonProperty
	private String BackURL;

	
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
	public String getRspCode() {
		return RspCode;
	}

	@JsonIgnore
	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}

	@JsonIgnore
	public String getRspInfo() {
		return RspInfo;
	}

	@JsonIgnore
	public void setRspInfo(String rspInfo) {
		RspInfo = rspInfo;
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
	public String getMCODE() {
		return MCODE;
	}

	@JsonIgnore
	public void setMCODE(String mCODE) {
		MCODE = mCODE;
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
	public String getServerURL() {
		return ServerURL;
	}

	@JsonIgnore
	public void setServerURL(String serverUrl) {
		ServerURL = serverUrl;
	}

	@JsonIgnore
	public String getBackURL() {
		return BackURL;
	}

	@JsonIgnore
	public void setBackURL(String backUrl) {
		BackURL = backUrl;
	}

}
