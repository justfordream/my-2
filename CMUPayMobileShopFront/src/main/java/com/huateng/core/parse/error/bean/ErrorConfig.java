package com.huateng.core.parse.error.bean;

import java.util.List;

/**
 * 错误
 * 
 * @author Gary
 * 
 */
public class ErrorConfig {

	private List<ErrorBean> bankErrorList;
	private List<ErrorBean> cmuErrorList;

	public List<ErrorBean> getBankErrorList() {
		return bankErrorList;
	}

	public void setBankErrorList(List<ErrorBean> bankErrorList) {
		this.bankErrorList = bankErrorList;
	}

	public List<ErrorBean> getCmuErrorList() {
		return cmuErrorList;
	}

	public void setCmuErrorList(List<ErrorBean> cmuErrorList) {
		this.cmuErrorList = cmuErrorList;
	}
	
}
