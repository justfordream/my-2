package com.huateng.action.bank;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.BankRequest;
import com.huateng.filter.HttpStringFilter;
import com.huateng.utils.SessionRequestUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 建行支付签约结果通知
 * 
 * @author yansen_huang
 * 
 */
public class RcvCCBPayAction extends ActionSupport {

	private static final long serialVersionUID = -5931485455826678640L;

	private final static Logger logger = Logger.getLogger(RcvCCBPayAction.class);
	

	@Value("${SPECIAL_FILETER_LIST}")
	private String fileterStr;
	
	public String recieve() {
		PrintWriter writer = null;
		try {
			logger.info("模拟银行开始接收参数============");
			HttpServletRequest request = SessionRequestUtil.getRequest();
			String txCode = HttpStringFilter.paseStringFilter(request.getParameter("TXCODE"),fileterStr);
			BankRequest reqData = new BankRequest();
			/** 520280 签约 ; 520290 支付 */
			if (StringUtils.equals(txCode, "520280")) {
				/** 封装获取的参数 */
				reqData = this.bankPayData(request);
				reqData.setBussType("0");
			} else if (StringUtils.equals(txCode, "520290")) {
				/** 封装获取的参数 */
				reqData = this.bankSginData(request);
				reqData.setBussType("1");
			}
			request.setAttribute("bankRequest", reqData);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return "success";
	}

	
	/**
	 * 转换支付请求信息为对象
	 * 
	 * @param request
	 * @param signRequestReq
	 */
	public BankRequest bankPayData(HttpServletRequest request) {
		BankRequest payReqData = new BankRequest();
		String merChantId = request.getParameter("MERCHANTID");
		String posId = request.getParameter("POSID");
		String branchId = request.getParameter("BRANCHID");
		String orderId = request.getParameter("ORDERID");
		String payment = request.getParameter("PAYMENT");
		String beggingNO = request.getParameter("BEGGINGNO");
		String area = request.getParameter("AREA");
		String payAcc = request.getParameter("PAYACC");
		String custType = request.getParameter("CUSTTYPE");
		String payType = request.getParameter("PAYTYPE");
		String limVal = request.getParameter("LIMVAL");
		String payAmt = request.getParameter("PAYAMT");
		String curCode = request.getParameter("CURCODE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String pub32 = request.getParameter("PUB32");
		String txCode = request.getParameter("TXCODE");
		String clientIp = request.getParameter("CLIENTIP");
		String gateWay = request.getParameter("GATEWAY");
		String mac = request.getParameter("MAC");
		String backUrl = request.getParameter("BACKURL");
		logger.debug("......建行支付接收的参数......[merChantId:" + merChantId
				+ ",posId:" + posId + ",branchId:" + branchId + ",orderId:"
				+ orderId + ",payment:" + payment + ",beggingNO:" + beggingNO
				+ ",area:" + area + "," + "payAcc:" + payAcc + ",custType:"
				+ custType + ",payType:"+payType+",limVal:"+limVal+",payAmt:"+payAmt+"," +
				"curCode:" + curCode + ",REMARK1:" + remark1
				+ ",REMARK2:" + remark2 + ",pub32:" + pub32 + ",txCode:"
				+ txCode + ",clientIp:" + clientIp + ",gateWay:" + gateWay
				+ ",mac:" + mac + ",backUrl:" + backUrl + "]");
		payReqData.setMerChantId(merChantId);
		payReqData.setPosId(posId);
		payReqData.setBranChId(branchId);
		payReqData.setOrderId(orderId);
		payReqData.setPayMent(payment);
		payReqData.setBeggingNo(beggingNO);
		payReqData.setArea(area);
		payReqData.setPayAcc(payAcc);
		payReqData.setCustType(custType);
		payReqData.setPayType(payType);
		payReqData.setLiMval(limVal);
		payReqData.setPayAmt(payAmt);
		payReqData.setCurCode(curCode);
		payReqData.setRemark1(remark1);
		payReqData.setRemark2(remark2);
		payReqData.setPub32(pub32);
		payReqData.setTxCode(txCode);
		payReqData.setClientiIp(clientIp);
		payReqData.setGateWay(gateWay);
		payReqData.setMac(mac);
		payReqData.setBackUrl(backUrl);
		return payReqData;
	}

	/**
	 * 转换签约请求信息为对象
	 * 
	 * @param request
	 * @param signRequestReq
	 */
	public BankRequest bankSginData(HttpServletRequest request) {
		BankRequest payReqData = new BankRequest();
		String merChantId = request.getParameter("MERCHANTID");
		String posId = request.getParameter("POSID");
		String branchId = request.getParameter("BRANCHID");
		String orderId = request.getParameter("ORDERID");
		String payment = request.getParameter("PAYMENT");
		String beggingNO = request.getParameter("BEGGINGNO");
		String area = request.getParameter("AREA");
		String payAcc = request.getParameter("PAYACC");
		String custType = request.getParameter("CUSTTYPE");

		String payType = request.getParameter("PAYTYPE");
		String liMval = request.getParameter("LIMVAL");
		String payAmt = request.getParameter("PAYAMT");

		String curCode = request.getParameter("CURCODE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String pub32 = request.getParameter("PUB32");
		String txCode = request.getParameter("TXCODE");
		String clientIp = request.getParameter("CLIENTIP");
		String gateWay = request.getParameter("GATEWAY");
		String mac = request.getParameter("MAC");
		String backUrl = request.getParameter("BACKURL");
		logger.debug("......建行签约接收的参数......[merChantId:" + merChantId
				+ ",posId:" + posId + ",branchId:" + branchId + ",orderId:"
				+ orderId + ",payment:" + payment + ",beggingNO:" + beggingNO
				+ ",area:" + area + "," + " payAcc:" + payAcc + ",custType:"
				+ custType + ",curCode:" + curCode + ",payType:" + payType + ""
				+ ",liMval:" + liMval + ",payAmt:" + payAmt + ",REMARK1:"
				+ remark1 + ",REMARK2:" + remark2 + ",pub32:" + pub32
				+ ",txCode:" + txCode + ",clientIp:" + clientIp + ",gateWay:"
				+ gateWay + ",mac:" + mac + ",backUrl:" + backUrl + "]");
		payReqData.setMerChantId(merChantId);
		payReqData.setPosId(posId);
		payReqData.setBranChId(branchId);
		payReqData.setOrderId(orderId);
		payReqData.setPayMent(payment);
		payReqData.setBeggingNo(beggingNO);
		payReqData.setArea(area);
		payReqData.setPayAcc(payAcc);
		payReqData.setCustType(custType);
		payReqData.setCurCode(curCode);
		payReqData.setRemark1(remark1);
		payReqData.setRemark2(remark2);
		payReqData.setPub32(pub32);
		payReqData.setTxCode(txCode);
		payReqData.setClientiIp(clientIp);
		payReqData.setGateWay(gateWay);
		payReqData.setMac(mac);
		payReqData.setBackUrl(backUrl);
		return payReqData;
	}
}
