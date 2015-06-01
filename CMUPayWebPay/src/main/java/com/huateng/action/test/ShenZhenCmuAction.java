package com.huateng.action.test;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.huateng.core.common.CoreConstant;
import com.huateng.utils.SessionRequestUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 网厅接收页面跳转action
 * 
 * @author Gary
 * 
 */
public class ShenZhenCmuAction extends ActionSupport {

	private static final long serialVersionUID = 5917451018030864118L;
	private final static Logger logger = Logger.getLogger(ShenZhenCmuAction.class);
	public String receive() {
		HttpServletRequest request = SessionRequestUtil.getRequest();
		String operate = request.getParameter("operate");
		String redirectPath = "";
		if (CoreConstant.OPERATE_SIGN.equals(operate)) {
			redirectPath = this.receiveSign();
		} else if (CoreConstant.OPERATE_PAY.equals(operate)) {
			redirectPath = this.receivePay();
		}
		return redirectPath;
	}

	public String receiveSign() {
		HttpServletRequest request = SessionRequestUtil.getRequest();
		String rspCode = request.getParameter("RspCode");
		String rspInfo = request.getParameter("RspInfo");
		request.setAttribute("RspCode", rspCode);
		request.setAttribute("RspInfo", rspInfo);
		logger.debug("响应码:["+rspCode+"],响应信息:["+rspInfo+"]");
		return SUCCESS;
	}

	public String receivePay() {
		HttpServletRequest request = SessionRequestUtil.getRequest();
		String rspCode = request.getParameter("RspCode");
		String rspInfo = request.getParameter("RspInfo");
		request.setAttribute("RspCode", rspCode);
		request.setAttribute("RspInfo", rspInfo);
		logger.debug("响应码:["+rspCode+"],响应信息:["+rspInfo+"]");
		return SUCCESS;
	}
}
