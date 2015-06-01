package com.huateng.cmupay.parseMsg.webgate.vo.crm;

import java.util.HashMap;
import java.util.Map;

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

	@JsonProperty
	private Map<String, String> properties = new HashMap<String, String>(); // 扩展属性

	public String getTransCode() {
		return TransCode;
	}

	public void setTransCode(String transCode) {
		this.TransCode = transCode;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		this.OrderID = orderID;
	}

	public String getRspCode() {
		return RspCode;
	}

	public void setRspCode(String rspCode) {
		this.RspCode = rspCode;
	}

	public String getRspInfo() {
		return RspInfo;
	}

	public void setRspInfo(String rspInfo) {
		this.RspInfo = rspInfo;
	}

	public String getMerVAR() {
		return MerVAR;
	}

	public void setMerVAR(String merVAR) {
		this.MerVAR = merVAR;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

}
