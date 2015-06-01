/**
 * 银联消费类交易应答
 */
package com.huateng.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * 银联消费类交易应答
 * 
 * @author zhaojunnan
 * 
 */
public class TpayData {

	/**
	 * 01 版本号
	 */
	private String version;

	/**
	 * 02 编码方式
	 */
	private String encoding;

	/**
	 * 03 证书ID
	 */
	private String certId;

	/**
	 * 04 签名
	 */
	private String signature;

	/**
	 * 05 交易类型
	 */
	private String txnType;

	/**
	 * 06 交易子类
	 */
	private String txnSubType;

	/**
	 * 07 产品类型
	 */
	private String bizType;

	/**
	 * 08 接入类型
	 */
	private String accessType;

	/**
	 * 09 商户代码
	 */
	private String merId;

	/**
	 * 10 商户订单号
	 */
	private String orderId;

	/**
	 * 11 订单发送时间
	 */
	private String txnTime;

	/**
	 * 12 帐号
	 */
	private String accNo;

	/**
	 * 12 帐号
	 */
	private String txnAmt;

	/**
	 * 14 交易币种
	 */
	private String currencyCode;

	/**
	 * 15 请求方保留域
	 */
	private String reqReserved;

	/**
	 * 16 保留域
	 */
	private String reserved;

	/**
	 * 17 交易查询流水号
	 */
	private String queryId;

	/**
	 * 18 响应码
	 */
	private String respCode;

	/**
	 * 19 响应信息
	 */
	private String respMsg;

	/**
	 * 20 响应时间
	 */
	private String respTime;

	/**
	 * 21 清算金额
	 */
	private String settleAmt;

	/**
	 * 22 清算币种
	 */
	private String settleCurrencyCode;

	/**
	 * 23 清算日期
	 */
	private String settleDate;

	/**
	 * 24 系统跟踪号
	 */
	private String traceNo;

	/**
	 * 25 交易传输时间
	 */
	private String traceTime;

	/**
	 * 26 兑换日期
	 */
	private String exchangeDate;

	/**
	 * 26 兑换日期
	 */
	private String exchangeRate;

	/**
	 * 28 支付卡类型
	 */
	private String payCardType;

	/**
	 * 29 支付方式
	 */
	private String payType;

	/**
	 * 29 支付方式
	 */
	private String issuerIdentifyMode;

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

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTxnTime() {
		return txnTime;
	}

	public void setTxnTime(String txnTime) {
		this.txnTime = txnTime;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getTxnAmt() {
		return txnAmt;
	}

	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
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

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
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

	public String getRespTime() {
		return respTime;
	}

	public void setRespTime(String respTime) {
		this.respTime = respTime;
	}

	public String getSettleAmt() {
		return settleAmt;
	}

	public void setSettleAmt(String settleAmt) {
		this.settleAmt = settleAmt;
	}

	public String getSettleCurrencyCode() {
		return settleCurrencyCode;
	}

	public void setSettleCurrencyCode(String settleCurrencyCode) {
		this.settleCurrencyCode = settleCurrencyCode;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getTraceNo() {
		return traceNo;
	}

	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
	}

	public String getTraceTime() {
		return traceTime;
	}

	public void setTraceTime(String traceTime) {
		this.traceTime = traceTime;
	}

	public String getExchangeDate() {
		return exchangeDate;
	}

	public void setExchangeDate(String exchangeDate) {
		this.exchangeDate = exchangeDate;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getPayCardType() {
		return payCardType;
	}

	public void setPayCardType(String payCardType) {
		this.payCardType = payCardType;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getIssuerIdentifyMode() {
		return issuerIdentifyMode;
	}

	public void setIssuerIdentifyMode(String issuerIdentifyMode) {
		this.issuerIdentifyMode = issuerIdentifyMode;
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
		
		return "TpayData [version=" + version + ", encoding=" + encoding
				+ ", certId=" + certId + ", signature=" + signature
				+ ", txnType=" + txnType + ", txnSubType=" + txnSubType
				+ ", bizType=" + bizType + ", accessType=" + accessType
				+ ", merId=" + merId + ", orderId=" + orderId + ", txnTime="
				+ txnTime + ", accNo=" + accNo + ", txnAmt=" + txnAmt
				+ ", currencyCode=" + currencyCode + ", reqReserved="
				+ reqReserved + ", reserved=" + reserved + ", queryId="
				+ queryId + ", respCode=" + respCode + ", respMsg=" + respMsg
				+ ", respTime=" + respTime + ", settleAmt=" + settleAmt
				+ ", settleCurrencyCode=" + settleCurrencyCode
				+ ", settleDate=" + settleDate + ", traceNo=" + traceNo
				+ ", traceTime=" + traceTime + ", exchangeDate=" + exchangeDate
				+ ", exchangeRate=" + exchangeRate + ", payCardType="
				+ payCardType + ", payType=" + payType
				+ ", issuerIdentifyMode=" + issuerIdentifyMode + ", serverURL="
				+ serverURL + ", backURL=" + backURL + ", status=" + status
				+ ", errorMsg=" + errorMsg + "]";
	}

	/**
	 * 将对象放置在map集合中
	 * 
	 * @return
	 */
	public Map<String, String> assemlyPayPlainMap() {

		Map<String, String> data = new HashMap<String, String>();

		data.put("version", version);
		data.put("encoding", encoding);
		data.put("certId", certId);
		data.put("signature", signature);
		data.put("txnType", txnType);
		data.put("txnSubType", txnSubType);
		data.put("bizType", bizType);
		data.put("accessType", accessType);
		data.put("merId", merId);
		data.put("orderId", orderId);
		data.put("txnTime", txnTime);
		data.put("accNo", accNo);
		data.put("txnAmt", txnAmt);
		data.put("currencyCode", currencyCode);
		data.put("reqReserved", reqReserved);
		data.put("reserved", reserved);
		data.put("queryId", queryId);
		data.put("respCode", respCode);
		data.put("respMsg", respMsg);
		data.put("respTime", respTime);
		data.put("settleAmt", settleAmt);
		data.put("settleCurrencyCode", settleCurrencyCode);
		data.put("settleDate", settleDate);
		data.put("traceNo", traceNo);
		data.put("traceTime", traceTime);
		data.put("exchangeDate", exchangeDate);
		data.put("exchangeRate", exchangeRate);
		data.put("payCardType", payCardType);
		data.put("payType", payType);
		data.put("issuerIdentifyMode", issuerIdentifyMode);

		return data;
	}
}
