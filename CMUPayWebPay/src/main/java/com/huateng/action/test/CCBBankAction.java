package com.huateng.action.test;

import javax.servlet.http.HttpServletRequest;

import com.huateng.utils.SessionRequestUtil;
import com.opensymphony.xwork2.ActionSupport;

public class CCBBankAction extends ActionSupport{
	private static final long serialVersionUID = 7278357416161692877L;

	public String receive() {
		System.out.println("........................CCBBank........................");
		HttpServletRequest request = SessionRequestUtil.getRequest();
		String txCode = request.getParameter("TXCODE");
		request.setAttribute("txCode", txCode);
		if ("520280".equals(txCode)) {
			return "sign";
		} else if ("520290".equals(txCode)) {
			return "pay";
		}
		return SUCCESS;
	}
}
