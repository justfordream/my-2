package com.huateng.action.bank;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.CCBData;
import com.huateng.bean.CCBPayData;
import com.huateng.bean.CCBSignData;
import com.huateng.core.common.CoreConstant;
import com.huateng.filter.HttpStringFilter;
import com.huateng.log.LogHandle;
import com.huateng.log.MessageLogger;
import com.huateng.service.CCBService;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.vo.MsgData;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 建行前台页面通知处理器
 * 
 * @author Gary
 */
public class RcvCCBPageAction extends ActionSupport {

	private static final long serialVersionUID = 2436972785311935604L;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
    private MessageLogger upayLog=MessageLogger.getLogger(RcvCCBPageAction.class);
    
	@Autowired
	private CCBService ccbService;
	
	@Autowired
	private LogHandle logHandle;
    

	@Value("${SPECIAL_FILETER_LIST}")
	private String fileterStr;
	/**
	 * 统一入口
	 * 
	 * @return
	 */
	public String receive() {
		logger.debug(".开始进入银行参数返回处理.");
		HttpServletRequest request = SessionRequestUtil.getRequest();
		String txType = HttpStringFilter.paseStringFilter(request.getParameter("TXTYPE"),fileterStr);
		logger.info(".接收到银行页面跳转结果通知.{}", txType);
		upayLog.debug("接收到银行页面跳转结果通知{}", txType);
		if (CoreConstant.OPERATE_SIGN.equals(txType)) {
			this.processSign();
		} else if (CoreConstant.OPERATE_PAY.equals(txType)) {
			this.processPay();
		}
		return NONE;
	}

	/**
	 * 处理签约
	 */
	public String processSign() {
		PrintWriter writer = null;
		HttpServletRequest request = SessionRequestUtil.getRequest();
		HttpServletResponse response = SessionRequestUtil.getResponse();
		this.settingResponse(response);
		logger.debug("......接收到银行签约页面跳转结果通知......");
		Map<String, String> requestMaps = HttpStringFilter
				.filterRequestParams(request,fileterStr);
		CCBData ccbData = this.assemlyCCBData(requestMaps);
		try {
			MsgData msgData = ccbService.signNotice(true, ccbData);
			logger.info("......向网厅发送签约请求......[{}]", msgData.getRedirectHtml());
			writer = response.getWriter();
			writer.write(msgData.getRedirectHtml());
			writer.flush();
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					CoreConstant.TransCode.CRM_SIGN.getCode(),
					CoreConstant.TransCode.CRM_SIGN.getDesc(),
					CoreConstant.BankCode.CCB.getCode(),
					CoreConstant.BankCode.CCB.getCode(), "接收到银行签约页面跳转结果通知");
		} catch (Exception e) {
			e.printStackTrace();
			this.logHandle.info(false,
					CoreConstant.ErrorCode.CODE_015A06.getCode(),
					CoreConstant.TransCode.CRM_SIGN.getCode(),
					CoreConstant.TransCode.CRM_SIGN.getDesc(),
					CoreConstant.BankCode.CCB.getCode(),
					CoreConstant.BankCode.CCB.getCode(), e.getMessage());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return NONE;
	}

	/**
	 * 处理支付
	 */
	public String processPay() {
		PrintWriter writer = null;
		HttpServletRequest request = SessionRequestUtil.getRequest();
		HttpServletResponse response = SessionRequestUtil.getResponse();
		this.settingResponse(response);
		logger.debug(".接收到银行支付页面跳转结果通知.");
		Map<String, String> requestMaps = HttpStringFilter
				.filterRequestParams(request,fileterStr);
		CCBData ccbData = this.assemlyCCBData(requestMaps);
		try {
			MsgData msgData = ccbService.payNotice(true, ccbData);
			logger.info(".向网厅发送支付请求.[{}]", msgData.getRedirectHtml());
			writer = response.getWriter();
			writer.write(msgData.getRedirectHtml());
			writer.flush();
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					CoreConstant.TransCode.CRM_PAY.getCode(),
					CoreConstant.TransCode.CRM_PAY.getDesc(),
					CoreConstant.BankCode.CCB.getCode(),
					CoreConstant.BankCode.CCB.getCode(), "接收到银行支付页面跳转结果通知");
		} catch (Exception e) {
			logger.error("处理银行的支付结果错误",e.getMessage());
			this.logHandle.info(false,
					CoreConstant.ErrorCode.CODE_015A06.getCode(),
					CoreConstant.TransCode.CRM_PAY.getCode(),
					CoreConstant.TransCode.CRM_PAY.getDesc(),
					CoreConstant.BankCode.CCB.getCode(),
					CoreConstant.BankCode.CCB.getCode(), e.getMessage());
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return NONE;
	}

	/**
	 * 设置响应公共信息
	 * 
	 * @param response
	 */
	public void settingResponse(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/html;charset=utf-8");
	}

	public CCBData assemlyCCBData(Map<String, String> requestMaps) {
		CCBData ccbData = new CCBData();
		String posId = requestMaps.get("POSID");
		String branchId = requestMaps.get("BRANCHID");
		String orderId = requestMaps.get("ORDERID");
		String subId = requestMaps.get("SUBID");
		String payment = requestMaps.get("PAYMENT");
		String curCode = requestMaps.get("CURCODE");
		String remark1 = requestMaps.get("REMARK1");
		String remark2 = requestMaps.get("REMARK2");
		String success = requestMaps.get("SUCCESS");
		String beggingNo = requestMaps.get("BEGGINGNO");
		String txType = requestMaps.get("TXTYPE");
		String accNo = requestMaps.get("ACCNO");
		String referer = requestMaps.get("REFERER");
		String clientIp = requestMaps.get("CLIENTIP");
		String sign = requestMaps.get("SIGN");
		logger.info("......接收建行结果通知，请求参数......[POSID:{},BRANCHID:{},ORDERID:{},SUBID:{},"
						+ "PAYMENT:{},CURCODE:{},REMARK1:{},REMARK2:{},SUCCESS:{},BEGGINGNO:{},TXTYPE:{},"
						+ "ACCNO:{},REFERER:{},CLIENTIP:{},SIGN:{}]",
				new Object[] { posId, branchId, orderId, subId, payment,
						curCode, remark1, remark2, success, beggingNo, txType,
						accNo, referer, clientIp, sign });
		ccbData.setPosId(posId);
		ccbData.setBranchId(branchId);
		ccbData.setOrderId(orderId);
		ccbData.setSubId(subId);
		ccbData.setPayment(payment);
		ccbData.setCurCode(curCode);
		ccbData.setRemark1(remark1);
		ccbData.setRemark2(remark2);
		ccbData.setSuccess(success);
		ccbData.setBeggingNo(beggingNo);
		ccbData.setTxType(txType);
		ccbData.setAccNo(accNo);
		ccbData.setReferer(referer);
		ccbData.setCLIENTIP(clientIp);
		ccbData.setSign(sign);

		return ccbData;
	}

	/**
	 * 转换请求信息为对象
	 * 
	 * @param request
	 * @param signRequestReq
	 */
	public CCBPayData assemlyPayData(HttpServletRequest request) {

		CCBPayData reqData = new CCBPayData();
		String OrderID = request.getParameter("OrderID");
		String RspCode = request.getParameter("RspCode");
		String RspInfo = request.getParameter("RspInfo");
		String MerVAR = request.getParameter("MerVAR");
		String Sig = request.getParameter("Sig");

		String backURL = request.getParameter("BackURL");
		String serverURL = request.getParameter("ServerURL");
		String homeProv = request.getParameter("HomeProv");

		reqData.setOrderID(OrderID);
		reqData.setRspCode(RspCode);
		reqData.setRspInfo(RspInfo);
		reqData.setMerVAR(MerVAR);
		reqData.setSig(Sig);
		reqData.setBackURL(backURL);
		reqData.setServerURL(serverURL);
		reqData.setHomeProv(homeProv);
		return reqData;
	}

	/**
	 * 转换请求信息为对象
	 * 
	 * @param request
	 * @param signRequestReq
	 */
	public CCBSignData assemlySignData(HttpServletRequest request) {

		CCBSignData reqData = new CCBSignData();

		String sessionID = request.getParameter("SessionID");
		String rspCode = request.getParameter("RspCode");
		String rspInfo = request.getParameter("RspInfo");
		String subID = request.getParameter("SubID");
		String subTime = request.getParameter("SubTime");
		String bankAcctID = request.getParameter("BankAcctID");
		String bankAcctType = request.getParameter("BankAcctType");
		String homeProv = request.getParameter("HomeProv");
		String sig = request.getParameter("Sig");
		String backURL = request.getParameter("BackURL");
		String serverURL = request.getParameter("ServerURL");

		reqData.setSessionID(sessionID);
		reqData.setRspCode(rspCode);
		reqData.setRspInfo(rspInfo);
		reqData.setSubID(subID);
		reqData.setSubTime(subTime);
		reqData.setBankAcctID(bankAcctID);
		reqData.setBankAcctType(bankAcctType);
		reqData.setHomeProv(homeProv);
		reqData.setSig(sig);
		reqData.setBackURL(backURL);
		reqData.setServerURL(serverURL);
		return reqData;
	}
}
