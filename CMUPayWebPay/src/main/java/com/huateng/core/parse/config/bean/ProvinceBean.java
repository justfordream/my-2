package com.huateng.core.parse.config.bean;

/**
 * 省编码信息
 * 
 * @author Gary
 * 
 */
public class ProvinceBean {
	/**
	 * 省名称
	 */
	private String name;
	/**
	 * 省代码
	 */
	private String code;
	/**
	 * 发起方URL
	 */
	private String url;
	/**
	 * 签约BackURL
	 */
	private String signUrl;
	/**
	 * 支付BackURL
	 */
	private String payUrl;
	/**
	 * 后台URL
	 */
	private String serverUrl;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSignUrl() {
		return signUrl;
	}

	public void setSignUrl(String signUrl) {
		this.signUrl = signUrl;
	}

	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

}
