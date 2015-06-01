package com.huateng.cmupay.parseMsg.webgate.vo.crm;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 中国移动统一支付系统（Web子系统）->银行网银系统的“签约”交易<br>
 * 将用户重定向回中国移动网上营业厅（HTTP响应）
 * 
 * @author Gary
 * 
 */
public class CoreResultSignRes {
	/**
	 * 等待会话标识，省公司自定义，统一支付系统在签约结果通知请求中应该携带此参数
	 */
	@JsonProperty
	private String SessionID;
	/**
	 * 签约结果，标识签约是否成功以及失败的原因
	 */
	@JsonProperty
	private String RspCode;
	/**
	 * 对于签约结果的描述信息
	 */
	@JsonProperty
	private String RspInfo;
	/**
	 * 签约协议号
	 */
	@JsonProperty
	private String SubID;

	/**
	 * 签约关系生成时间，格式为YYYYMMDDHH24MISS
	 */
	@JsonProperty
	private String SubTime;

	/**
	 * 银行账号信息
	 */
	@JsonProperty
	private String BankAcctID;
	/**
	 * 见附录：银行卡类型，区分是借记卡还是信用卡
	 */
	@JsonProperty
	private String BankAcctType;

	/**
	 * 见附录：银行账期
	 */
	@JsonProperty
	private String SettleDate;

	
	
	/**
	 * 银行编码
	 */
	@JsonProperty
	private String BankID;

	/**
	 * 银行系统签名数据
	 */
	@JsonProperty
	private String Sig;
	/**
	 * 操作流水号(与请求流水号相同)
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
	 * 操作请求日期 YYYYMMDD
	 */
	@JsonProperty
	private String ActionDate;
	/**
	 * 证件类型
	 */
	@JsonProperty
	private String UserIDType;
	/**
	 * 证件编码
	 */
	@JsonProperty
	private String UserID;
	/**
	 * 用户姓名
	 */
	@JsonProperty
	private String UserName;
	/**
	 * 缴费方式
	 */
	@JsonProperty
	private String PayType;
	/**
	 * 充值金额 （分）
	 */
	@JsonProperty
	private String RechAmount;
	/**
	 * 阈值 （分）
	 */
	@JsonProperty
	private String RechThreshold;
	/**
	 * 1-预付费2-后付费
	 */
	@JsonProperty
	private String UserCat;
	/**
	 * UPAY00002 将用户重定向回中国移动省公司网上营业厅时填写
	 */
	@JsonProperty
	private String MCODE;
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
	
	
	@JsonIgnore
	public String getSessionID() {
		return SessionID;
	}

	@JsonIgnore
	public void setSessionID(String sessionID) {
		SessionID = sessionID;
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
	public String getSubID() {
		return SubID;
	}

	@JsonIgnore
	public void setSubID(String subID) {
		SubID = subID;
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
	public String getBankAcctID() {
		return BankAcctID;
	}

	@JsonIgnore
	public void setBankAcctID(String bankAcctID) {
		BankAcctID = bankAcctID;
	}

	@JsonIgnore
	public String getBankAcctType() {
		return BankAcctType;
	}

	@JsonIgnore
	public void setBankAcctType(String bankAcctType) {
		BankAcctType = bankAcctType;
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
	public String getSettleDate() {
		return SettleDate;
	}
	@JsonIgnore
	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
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
	public String getActionDate() {
		return ActionDate;
	}

	@JsonIgnore
	public void setActionDate(String actionDate) {
		ActionDate = actionDate;
	}

	@JsonIgnore
	public String getUserIDType() {
		return UserIDType;
	}

	@JsonIgnore
	public void setUserIDType(String userIDType) {
		UserIDType = userIDType;
	}

	@JsonIgnore
	public String getUserID() {
		return UserID;
	}

	@JsonIgnore
	public void setUserID(String userID) {
		UserID = userID;
	}

	@JsonIgnore
	public String getUserName() {
		return UserName;
	}

	@JsonIgnore
	public void setUserName(String userName) {
		UserName = userName;
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
	public String getRechAmount() {
		return RechAmount;
	}

	@JsonIgnore
	public void setRechAmount(String rechAmount) {
		RechAmount = rechAmount;
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
	public String getUserCat() {
		return UserCat;
	}
	@JsonIgnore
	public void setUserCat(String userCat) {
		UserCat = userCat;
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
		if (StringUtils.isNotBlank(this.RspCode)) {
			sb.append("RSPCODE=" + this.RspCode + "|");
		}
		if (StringUtils.isNotBlank(this.SubID)) {
			sb.append("SUBID=" + this.SubID + "|");
		}
		if (StringUtils.isNotBlank(this.SubTime)) {
			sb.append("SUBTIME=" + this.SubTime + "|");
		}
		if (StringUtils.isNotBlank(this.BankAcctID)) {
			sb.append("BANKACCTID=" + this.BankAcctID + "|");
		}
		if (StringUtils.isNotBlank(this.BankAcctType)) {
			sb.append("BANKACCTTYPE=" + this.BankAcctType + "|");
		}
		if (StringUtils.isNotBlank(this.BankID)) {
			sb.append("BANKID=" + this.BankID + "|");
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
		if (StringUtils.isNotBlank(this.ActionDate)) {
			sb.append("ACTIONDATE=" + this.ActionDate + "|");
		}
		if (StringUtils.isNotBlank(this.UserIDType)) {
			sb.append("USERIDTYPE=" + this.UserIDType + "|");
		}
		if (StringUtils.isNotBlank(this.UserID)) {
			sb.append("USERID=" + this.UserID + "|");
		}
		if (StringUtils.isNotBlank(this.PayType)) {
			sb.append("PAYTYPE=" + this.PayType + "|");
		}
		if (StringUtils.isNotBlank(this.RechAmount)) {
			sb.append("RECHAMOUNT=" + this.RechAmount + "|");
		}
		if (StringUtils.isNotBlank(this.RechThreshold)) {
			sb.append("RECHTHRESHOLD=" + this.RechThreshold + "|");
		}
		if (StringUtils.isNotBlank(this.UserCat)) {
			sb.append("USERCAT=" + this.UserCat + "|");
		}
		if (StringUtils.isNotBlank(this.MCODE)) {
			sb.append("MCODE=" + this.MCODE + "|");
		}
		return sb.toString();
	}

}
