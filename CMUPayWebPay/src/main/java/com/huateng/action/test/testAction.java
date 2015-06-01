package com.huateng.action.test;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huateng.utils.SessionRequestUtil;
import com.opensymphony.xwork2.ActionSupport;

public class testAction  extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String receive() {
		HttpServletRequest request = SessionRequestUtil.getRequest();
		HttpServletResponse reponse=SessionRequestUtil.getResponse();
		System.out.println(" === " + request.getParameter("MerID"));
		System.out.println(" === " + request.getParameter("OrderID"));
		System.out.println(" === " + request.getParameter("RspCode"));
		System.out.println(" === " + request.getParameter("RspInfo"));
		System.out.println(" === " + request.getParameter("MerVAR"));
		System.out.println(" === " + request.getParameter("OrderTime"));
		System.out.println(" === " + request.getParameter("Payed"));
		System.out.println(" === " + request.getParameter("CurType"));
		System.out.println(" === " + request.getParameter("MCODE"));
		System.out.println(" === " + request.getParameter("Sig"));
		System.out.println(" === " + request.getParameter("ServerURL"));
		System.out.println(" === " + request.getParameter("BackURL"));
		try {
			reponse.getWriter().write("OK");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NONE;
	}
	
}
