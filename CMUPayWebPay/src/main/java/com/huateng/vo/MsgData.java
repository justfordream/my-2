package com.huateng.vo;

import java.util.Map;


/**
 * 支付网关域信息
 * 
 * @author gary
 * 
 */
public class MsgData {

	private BankData bankData;

	private CmuData cmuData;

	private String resCode;
	/**
	 * 前台通知HTML
	 */
	private String redirectHtml;
	/**
	 * 后台通知html
	 */
	private String noticeBackHtml;

	private String errorInfo;
	
	private String url;
	
	private Map<String,String> params;

	public String getRedirectHtml() {
		return redirectHtml;
	}

	public void setRedirectHtml(String redirectHtml) {
		this.redirectHtml = redirectHtml;
	}

	public BankData getBankData() {
		return bankData;
	}

	public void setBankData(BankData bankData) {
		this.bankData = bankData;
	}

	public CmuData getCmuData() {
		return cmuData;
	}

	public void setCmuData(CmuData cmuData) {
		this.cmuData = cmuData;
	}

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getNoticeBackHtml() {
		return noticeBackHtml;
	}

	public void setNoticeBackHtml(String noticeBackHtml) {
		this.noticeBackHtml = noticeBackHtml;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

}
