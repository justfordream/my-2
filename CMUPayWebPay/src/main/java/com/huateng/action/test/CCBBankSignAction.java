package com.huateng.action.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huateng.utils.SessionRequestUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 模拟建设银行端
 * 
 * @author Gary
 * 
 */
public class CCBBankSignAction extends ActionSupport {

	private static final long serialVersionUID = 2724114468115857655L;

	public String receive() {
		String aa = "lNUdPsEhsk/qUMubHS6tP/WMLaJXNexxyG+zEsQscL6p9tM+DN5urqRHAXh26nSpCMIoJzZ8h3Qu5nDN7TT+b5NrktQfxlrPrB71bVbhfnta1kuNvEs6v90DJEDNc2iCbtwhQUxfMgc+ZYBdqYBMxAzSi1I9L0eW/2ZKYvzPU=";
		String str;
		HttpServletResponse response=SessionRequestUtil.getResponse();
		HttpServletRequest request=SessionRequestUtil.getRequest();
		try {
			str = URLEncoder.encode(aa,"utf-8");
			request.setAttribute("key", str);
			response.sendRedirect("ErrorMsg.jsp?key=" + str);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
}
