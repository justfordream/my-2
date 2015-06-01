package com.huateng.vo;

import java.util.HashMap;
import java.util.Map;

public class TPayShopOpenData {

	// 01 版本号 version
	private String version;

	// 02 证书ID certId
	private String certId;

	// 03 签名 signature
	private String signature;

	// 04 编码方式 encoding
	private String encoding;

	// 05 交易类型 txnType
	private String txnType;

	// 06 交易子类 txnSubType
	private String txnSubType;

	// 07 产品类型 bizType
	private String bizType;

	// 08 接入类型 accessType
	private String accessType;

	// 09 商户代码 merId
	private String merId;

	// 10 帐号 accNo
	private String accNo;

	// 11 请求方保留域 reqReserved
	private String reqReserved;

	// 12 保留域 reserved
	private String reserved;

	// 13 应答码 respCode
	private String respCode;

	// 14 应答信息 respMsg
	private String respMsg;

	// 15 开通状态 activateStatus
	private String activateStatus;

	// 16 支付卡类型 payCardType
	private String payCardType;

	// 17 银行卡验证信息及身份信息 customerInfo
	private String customerInfo;

	// 18 小额临时支付信息域 temporaryPayInfo
	private String temporaryPayInfo;

	private String serverURL;

	private String backURL;

	private String status;

	private String errorMsg;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getTxnSubType() {
		return txnSubType;
	}

	public void setTxnSubType(String txnSubType) {
		this.txnSubType = txnSubType;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getReqReserved() {
		return reqReserved;
	}

	public void setReqReserved(String reqReserved) {
		this.reqReserved = reqReserved;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public String getActivateStatus() {
		return activateStatus;
	}

	public void setActivateStatus(String activateStatus) {
		this.activateStatus = activateStatus;
	}

	public String getPayCardType() {
		return payCardType;
	}

	public void setPayCardType(String payCardType) {
		this.payCardType = payCardType;
	}

	public String getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(String customerInfo) {
		this.customerInfo = customerInfo;
	}

	public String getTemporaryPayInfo() {
		return temporaryPayInfo;
	}

	public void setTemporaryPayInfo(String temporaryPayInfo) {
		this.temporaryPayInfo = temporaryPayInfo;
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

	@Override
	public String toString() {
		return "TPayShopOpenData [version=" + version + ", certId=" + certId
				+ ", signature=" + signature + ", encoding=" + encoding
				+ ", txnType=" + txnType + ", txnSubType=" + txnSubType
				+ ", bizType=" + bizType + ", accessType=" + accessType
				+ ", merId=" + merId + ", accNo=" + accNo + ", reqReserved="
				+ reqReserved + ", reserved=" + reserved + ", respCode="
				+ respCode + ", respMsg=" + respMsg + ", activateStatus="
				+ activateStatus + ", payCardType=" + payCardType
				+ ", customerInfo=" + customerInfo + ", temporaryPayInfo="
				+ temporaryPayInfo + "]";
	}

	/**
	 * 验签Map
	 * 
	 * @return
	 */
	public Map<String, String> assemlyPayPlainMap() {
		Map<String, String> data = new HashMap<String, String>();

		// version
		data.put("version", version);

		// certId
		data.put("certId", certId);

		// signature
		data.put("signature", signature);

		// encoding
		data.put("encoding", encoding);

		// txnType
		data.put("txnType", txnType);

		// txnSubType
		data.put("txnSubType", txnSubType);

		// bizType
		data.put("bizType", bizType);

		// accessType
		data.put("accessType", accessType);

		// merId
		data.put("merId", merId);

		// accNo
		data.put("accNo", accNo);

		// reqReserved
		data.put("reqReserved", reqReserved);

		// reserved
		data.put("reserved", reserved);

		// respCode
		data.put("respCode", respCode);

		// respMsg
		data.put("respMsg", respMsg);

		// activateStatus
		data.put("activateStatus", activateStatus);

		// payCardType
		data.put("payCardType", payCardType);

		// customerInfo
		data.put("customerInfo", customerInfo);

		// temporaryPayInfo
		data.put("temporaryPayInfo", temporaryPayInfo);

		return data;
	}

}
