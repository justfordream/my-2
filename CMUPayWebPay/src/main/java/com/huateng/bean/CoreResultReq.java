package com.huateng.bean;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class CoreResultReq {
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
	
	
	@JsonProperty
	private Map<String, String> properties = new HashMap<String, String>(); // 扩展属性

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

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	
}
