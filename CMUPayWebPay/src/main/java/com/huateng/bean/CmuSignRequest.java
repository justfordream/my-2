package com.huateng.bean;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 省中心网厅->中国移动统一支付系统的“签约”交易 <br>
 * 签约请求
 * 
 * @author Gary
 */
public class CmuSignRequest {
	/**
	 * 交易码（区分签约和支付，网关和核心之间使用）
	 */
	@JsonProperty
	private String TransCode;
	/**
	 * 等待会话标识，省公司自定义，统一支付系统在签约结果通知请求中应该携带此参数
	 */
	@JsonProperty
	private String SessionID;

	/**
	 * 中国移动用户标识类型
	 */
	@JsonProperty
	private String IDType;

	/**
	 * 中国移动用户ID
	 */
	@JsonProperty
	private String IDValue;

	/**
	 * 后台结果回传URL
	 */
	@JsonProperty
	private String ServerURL;

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
	 * 签约请求生成时间 格式为YYYYMMDDHH24MISS
	 */
	@JsonProperty
	private String SubTime;
	/**
	 * 操作流水号
	 */
	@JsonProperty
	private String TransactionID;
	/**
	 * 发起方应用域编码
	 */
	@JsonProperty
	private String OrigDomain;
	/**
	 * 客户端IP
	 */
	@JsonProperty
	private String CLIENTIP;
	/**
	 * UPAY00001 签约请求时填写
	 */
	@JsonProperty
	private String MCODE;
	/**
	 * 归属省
	 */
	@JsonProperty
	private String HomeProv;
	/**
	 * 状态
	 */
	@JsonIgnore
	private String status;
	/**
	 * 错误描述
	 */
	@JsonIgnore
	private String error;

	@JsonIgnore
	public String getTransCode() {
		return TransCode;
	}

	@JsonIgnore
	public void setTransCode(String transCode) {
		TransCode = transCode;
	}

	@JsonIgnore
	public String getSessionID() {
		return SessionID;
	}

	@JsonIgnore
	public void setSessionID(String sessionID) {
		SessionID = sessionID;
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
	public String getHomeProv() {
		return HomeProv;
	}

	@JsonIgnore
	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}

	/**
	 * @return the status
	 */
	@JsonIgnore
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	@JsonIgnore
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * 获取签名字符串
	 * 
	 * @return
	 */
	public String assemlyPlainText() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(this.SessionID)) {
			sb.append("SESSIONID=" + this.SessionID + "|");
		}
		if (StringUtils.isNotBlank(this.IDType)) {
			sb.append("IDTYPE=" + this.IDType + "&");
		}
		if (StringUtils.isNotBlank(this.IDValue)) {
			sb.append("RIDVALUE=" + this.IDValue + "|");
		}
		if (StringUtils.isNotBlank(this.ServerURL)) {
			sb.append("SERVERURL=" + this.ServerURL + "|");
		}
		if (StringUtils.isNotBlank(this.BackURL)) {
			sb.append("BACKURL=" + this.BackURL + "|");
		}
		if (StringUtils.isNotBlank(this.BankID)) {
			sb.append("BANKID=" + this.BankID + "|");
		}
		if (StringUtils.isNotBlank(this.SubTime)) {
			sb.append("SUBTIME=" + this.SubTime + "|");
		}
		if (StringUtils.isNotBlank(this.TransactionID)) {
			sb.append("TRANSACTIONID=" + this.TransactionID + "|");
		}
		if (StringUtils.isNotBlank(this.OrigDomain)) {
			sb.append("ORIGDOMAIN=" + this.OrigDomain + "|");
		}
		if (StringUtils.isNotBlank(this.CLIENTIP)) {
			sb.append("CLIENTIP=" + this.CLIENTIP + "|");
		}
		if (StringUtils.isNotBlank(this.MCODE)) {
			sb.append("MCODE=" + this.MCODE);
		}
		return sb.toString();
	}
}
