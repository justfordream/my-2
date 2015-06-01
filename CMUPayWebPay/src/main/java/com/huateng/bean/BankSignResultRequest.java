package com.huateng.bean;

/**
 * 中国移动统一支付系统（Web子系统）->银行网银系统的“签约”交易<br>
 * 签约结果通知请求
 * 
 * @author Gary
 * 
 */
public class BankSignResultRequest {
	/**
	 * 等待会话标识，中国移动自定义，银行系统在签约结果通知请求中应该携带此参数
	 */
	private String SessionID;
	/**
	 * 签约结果，标识签约是否成功以及失败的原因
	 */
	private String RspCode;
	/**
	 * 对于签约结果的描述信息
	 */
	private String RspInfo;
	/**
	 * 签约协议号
	 */
	private String SubID;
	/**
	 * 签约关系生成时间，格式为YYYYMMDDHHmmSS
	 */
	private String SubTime;
	/**
	 * 银行账号信息
	 */
	private String BankAcctID;
	/**
	 * 具体取值参考附录，区分是借记卡还是信用卡。
	 */
	private String BankAcctType;
	/**
	 * 银行签名
	 */
	private String Sig;
	// TODO ADD
	private String BankID;
	// TODO ADD
	private String MerID;
	// TODO ADD
	private String ServerURL;
	// TODO ADD
	private String backURL;

	public String getSessionID() {
		return SessionID;
	}

	public void setSessionID(String sessionID) {
		SessionID = sessionID;
	}

	public String getRspCode() {
		return RspCode;
	}

	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}

	public String getRspInfo() {
		return RspInfo;
	}

	public void setRspInfo(String rspInfo) {
		RspInfo = rspInfo;
	}

	public String getSubID() {
		return SubID;
	}

	public void setSubID(String subID) {
		SubID = subID;
	}

	public String getSubTime() {
		return SubTime;
	}

	public void setSubTime(String subTime) {
		SubTime = subTime;
	}

	public String getBankAcctID() {
		return BankAcctID;
	}

	public void setBankAcctID(String bankAcctID) {
		BankAcctID = bankAcctID;
	}

	public String getBankAcctType() {
		return BankAcctType;
	}

	public void setBankAcctType(String bankAcctType) {
		BankAcctType = bankAcctType;
	}

	public String getSig() {
		return Sig;
	}

	public void setSig(String sig) {
		Sig = sig;
	}

	public String getBankID() {
		return BankID;
	}

	public void setBankID(String bankID) {
		BankID = bankID;
	}

	public String getMerID() {
		return MerID;
	}

	public void setMerID(String merID) {
		MerID = merID;
	}

	public String getServerURL() {
		return ServerURL;
	}

	public void setServerURL(String serverURL) {
		ServerURL = serverURL;
	}

	public String getBackURL() {
		return backURL;
	}

	public void setBackURL(String backURL) {
		this.backURL = backURL;
	}

	public String assemlyPlainText() {
		return "";
	}
}
