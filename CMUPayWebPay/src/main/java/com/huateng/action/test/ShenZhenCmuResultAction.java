package com.huateng.action.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.huateng.core.common.CoreConstant;
import com.huateng.utils.SessionRequestUtil;
import com.opensymphony.xwork2.ActionSupport;

public class ShenZhenCmuResultAction extends ActionSupport {

	private static final long serialVersionUID = -4504156281944709167L;
	private final static Logger logger = LoggerFactory
			.getLogger(ShenZhenCmuResultAction.class);

	public String receive() {
		HttpServletRequest request = SessionRequestUtil.getRequest();
		String operate = request.getParameter("operate");
		if (CoreConstant.OPERATE_SIGN.equals(operate)) {
			this.receiveSign();
		} else if (CoreConstant.OPERATE_PAY.equals(operate)) {
			this.receivePay();
		}
		PrintWriter writer;
		try {
			writer = SessionRequestUtil.getResponse().getWriter();
			writer.write("##############CRM SUCCESS##########");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return NONE;
	}

	public void receiveSign() {
		HttpServletRequest request = SessionRequestUtil.getRequest();
		System.out.println("###################OK###################");
		logger.debug("###########模拟省份后台获取签约参数############"
				+ request.getParameter("RspCode") + ":"
				+ request.getParameter("RspInfo"));
	}

	public void receivePay() {
		HttpServletRequest request = SessionRequestUtil.getRequest();
		System.out.println("###################OK###################");
		logger.debug("###########模拟省份后台获取缴费参数############"
				+ request.getParameter("RspCode") + ":"
				+ request.getParameter("RspInfo"));
	}
}
