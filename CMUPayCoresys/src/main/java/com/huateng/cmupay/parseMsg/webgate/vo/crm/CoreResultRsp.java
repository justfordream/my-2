package com.huateng.cmupay.parseMsg.webgate.vo.crm;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 核心返回结果
 * 
 * @author Gary
 * 
 */
public class CoreResultRsp {
	/**
	 * 后台通知URL
	 */
	@JsonProperty
	private String serverURL;
	/**
	 * 前台通知URL
	 */
	@JsonProperty
	private String backURL;
	/**
	 * 错误码
	 */
	@JsonProperty
	private String rspCode;
	/**
	 * 错误信息
	 */
	@JsonProperty
	private String rspInfo;

	/**
	 * 返回值携带扩展属性
	 */
	@JsonProperty
	private Map<String, String> properties = new HashMap<String, String>();

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

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public String getRspInfo() {
		return rspInfo;
	}

	public void setRspInfo(String rspInfo) {
		this.rspInfo = rspInfo;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

}
