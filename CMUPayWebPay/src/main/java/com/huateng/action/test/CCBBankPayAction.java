package com.huateng.action.test;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 模拟浦发银行端
 * 
 * @author Gary
 * 
 */
public class CCBBankPayAction extends ActionSupport {

	private static final long serialVersionUID = 8968933240656783107L;

	public String receive() {
		System.out.println("........................CCBBank........................");
		return SUCCESS;
	}
}
