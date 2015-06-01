package com.huateng.cmupay.parseMsg.webgate.vo.crm;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 与核心签约结果请求
 * 
 * @author Gary
 * 
 */
public class CoreResultSignReq {
	/**
	 * 交易码（区分签约和支付，网关和核心之间使用）
	 */
	@JsonProperty
	private String TransCode;
	/**
	 * 等待会话标识，中国移动自定义，银行系统在签约结果通知请求中应该携带此参数<br>
	 * 通过此获取网厅的serverURL和backURL
	 */
	@JsonProperty
	private String OrderID;
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
	 * 签约关系生成时间，格式为YYYYMMDDHHmmSS
	 */
	@JsonProperty
	private String SubTime;
	/**
	 * 银行账号信息
	 */
	@JsonProperty
	private String BankAcctID;
	/**
	 * 具体取值参考附录，区分是借记卡还是信用卡。
	 */
	@JsonProperty
	private String BankAcctType;
	public String getTransCode() {
		return TransCode;
	}
	public void setTransCode(String transCode) {
		TransCode = transCode;
	}
	public String getOrderID() {
		return OrderID;
	}
	public void setOrderID(String orderId) {
		OrderID = orderId;
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



}
