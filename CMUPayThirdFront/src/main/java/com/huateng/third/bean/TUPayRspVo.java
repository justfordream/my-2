package com.huateng.third.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 银联的缴费和支付的应答实体
 * 由于需要通过ActiveMQ传送，所以必须序列化
 * @author Manzhizhen
 * 
 */
public class TUPayRspVo implements Serializable {
	private static final long serialVersionUID = -670413572121932814L;
	
	@JsonProperty
	private String version; // 版本号
	@JsonProperty
	private String encoding; // 编码方式
	@JsonProperty
	private String certId; // 证书ID
	@JsonProperty
	private String signature; // 签名
	@JsonProperty
	private String txnType; // 交易类型
	@JsonProperty
	private String txnSubType; // 交易子类
	@JsonProperty
	private String bizType; // 产品类型
	@JsonProperty
	private String accessType; // 接入类型
	@JsonProperty
	private String merId; // 商户代码
	@JsonProperty
	private String orderId; // 商户订单号
	@JsonProperty
	private String txnTime; // 订单发送时间
	@JsonProperty
	private String accNo; // 帐号
	@JsonProperty
	private String txnAmt; // 交易金额
	@JsonProperty
	private String currencyCode; // 交易币种
	@JsonProperty
	private String reqReserved; // 请求方保留域
	@JsonProperty
	private String reserved; // 保留域
	@JsonProperty
	private String queryId; // 交易查询流水号
	@JsonProperty
	private String respCode; // 响应码
	@JsonProperty
	private String respMsg; // 响应信息
	@JsonProperty
	private String respTime; // 响应时间
	@JsonProperty
	private String settleAmt; // 清算金额
	@JsonProperty
	private String settleCurrencyCode; // 清算币种
	@JsonProperty
	private String settleDate; // 清算日期
	@JsonProperty
	private String traceNo; // 系统跟踪号
	@JsonProperty
	private String traceTime; // 交易传输时间
	@JsonProperty
	private String exchangeDate; // 兑换日期
	@JsonProperty
	private String exchangeRate; // 汇率
	@JsonProperty
	private String payCardType; // 支付卡类型
	@JsonProperty
	private String payType; // 支付方式
	@JsonProperty
	private String issuerIdentifyMode; // 发卡机构识别模式

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

}
