package com.huateng.bean;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 与核心支付结果请求
 * 
 * @author Gary
 * 
 */
public class CoreResultPayReq {
	/**
	 * 交易码（区分签约和支付，网关和核心之间使用）
	 */
	@JsonProperty
	private String TransCode;
	/**
	 * 订单标识<br>
	 * 通过此获取网厅的serverURL和backURL
	 */
	@JsonProperty
	private String OrderID;
	/**
	 * 支付结果，标识支付是否成功以及失败的原因
	 */
	@JsonProperty
	private String RspCode;
	/**
	 * 对于支付结果的描述信息
	 */
	@JsonProperty
	private String RspInfo;
	/**
	 * 中国移动自定义，当银行通知支付结果时，携带此变量，中国移动可以用此变量维护session等
	 */
	@JsonProperty
	private String MerVAR;
	@JsonIgnore
	public String getTransCode() {
		return TransCode;
	}
	@JsonIgnore
	public void setTransCode(String transCode) {
		TransCode = transCode;
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

	

}
