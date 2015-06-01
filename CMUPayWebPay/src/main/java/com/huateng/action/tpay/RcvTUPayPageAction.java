/**
 * 银联跳转到统一支付接口
 */
package com.huateng.action.tpay;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.common.CoreConstant;
import com.huateng.filter.HttpStringFilter;
import com.huateng.log.LogHandle;
import com.huateng.log.MessageLogger;
import com.huateng.service.TpayService;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.vo.MsgData;
import com.huateng.vo.TpayData;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 银联跳转到统一支付接口
 * 
 * @author Administrator
 * 
 */
public class RcvTUPayPageAction extends ActionSupport {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog = MessageLogger
			.getLogger(RcvTUPayPageAction.class);

	@Autowired
	private TpayService tpayService;

	@Autowired
	private LogHandle logHandle;

	@Value("${SPECIAL_FILETER_LIST}")
	private String fileterStr;

	@Value("${TPAY.B2C.ENCODING}")
	private String defaultEncoding;

	public String receive() {

		PrintWriter writer = null;
		HttpServletRequest request = SessionRequestUtil.getRequest();
		try {
			String encoding = request.getParameter("encoding");
			if (null == encoding && "".equals(encoding)) {
				encoding = defaultEncoding;
			}
			request.setCharacterEncoding(request.getParameter("encoding"));
		} catch (UnsupportedEncodingException e1) {
			logger.debug(".请求数据编码格式转化错误.编码格式为：{}",
					request.getParameter("encoding"));
			upayLog.debug(".请求数据编码格式转化错误.编码格式为：{}",
					request.getParameter("encoding"));
		}

		HttpServletResponse response = SessionRequestUtil.getResponse();
		this.settingResponse(response);

		logger.debug(".接收到银联缴费页面跳转结果通知.");
		upayLog.debug(".接收到银联缴费页面跳转结果通知.");

		Map<String, String> requestMaps = HttpStringFilter.filterRequestParams(
				request, fileterStr);
		TpayData tpayData = this.assemlyTpayData(requestMaps);

		try {

			MsgData msgData = tpayService.shopTpayNotice(true, tpayData);

			logger.info("......向移动商城返回缴费消息通知......[{}]",
					msgData.getRedirectHtml());
			upayLog.info("......向移动商城返回缴费消息通知......[{}]",
					msgData.getRedirectHtml());

			writer = response.getWriter();
			writer.write(msgData.getRedirectHtml());
			writer.flush();

			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					CoreConstant.TransCode.TSHOP_PAY_NOTICE.getCode(),
					CoreConstant.TransCode.TSHOP_PAY_NOTICE.getDesc(),
					CoreConstant.BankCode.TPAY.getCode(),
					CoreConstant.BankCode.TPAY.getCode(), "接收到银联缴费页面跳转结果通知");

		} catch (Exception e) {

			e.printStackTrace();

			this.logHandle.info(false,
					CoreConstant.ErrorCode.CODE_015A06.getCode(),
					CoreConstant.TransCode.TSHOP_PAY_NOTICE.getCode(),
					CoreConstant.TransCode.TSHOP_PAY_NOTICE.getDesc(),
					CoreConstant.BankCode.TPAY.getCode(),
					CoreConstant.BankCode.TPAY.getCode(), e.getMessage());
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

	/**
	 * 将返回数据组装成TpayData
	 * 
	 * @param requestMaps
	 * @return
	 */
	public TpayData assemlyTpayData(Map<String, String> requestMaps) {
		TpayData tpayData = new TpayData();

		tpayData.setVersion(requestMaps.get("version"));
		tpayData.setEncoding(requestMaps.get("encoding"));
		tpayData.setCertId(requestMaps.get("certId"));
		tpayData.setSignature(requestMaps.get("signature"));
		tpayData.setTxnType(requestMaps.get("txnType"));
		tpayData.setTxnSubType(requestMaps.get("txnSubType"));
		tpayData.setBizType(requestMaps.get("bizType"));
		tpayData.setAccessType(requestMaps.get("accessType"));
		tpayData.setMerId(requestMaps.get("merId"));
		tpayData.setOrderId(requestMaps.get("orderId"));
		tpayData.setTxnTime(requestMaps.get("txnTime"));
		tpayData.setAccNo(requestMaps.get("accNo"));
		tpayData.setTxnAmt(requestMaps.get("txnAmt"));
		tpayData.setCurrencyCode(requestMaps.get("currencyCode"));
		tpayData.setReqReserved(requestMaps.get("reqReserved"));
		tpayData.setReserved(requestMaps.get("reserved"));
		tpayData.setQueryId(requestMaps.get("queryId"));
		tpayData.setRespCode(requestMaps.get("respCode"));
		tpayData.setRespMsg(requestMaps.get("respMsg"));
		tpayData.setRespTime(requestMaps.get("respTime"));
		tpayData.setSettleAmt(requestMaps.get("settleAmt"));
		tpayData.setSettleCurrencyCode(requestMaps.get("settleCurrencyCode"));
		tpayData.setSettleDate(requestMaps.get("settleDate"));
		tpayData.setTraceNo(requestMaps.get("traceNo"));
		tpayData.setTraceTime(requestMaps.get("traceTime"));
		tpayData.setExchangeDate(requestMaps.get("exchangeDate"));
		tpayData.setExchangeRate(requestMaps.get("exchangeRate"));
		tpayData.setPayCardType(requestMaps.get("payCardType"));
		tpayData.setPayType(requestMaps.get("payType"));
		tpayData.setIssuerIdentifyMode(requestMaps.get("issuerIdentifyMode"));

		return tpayData;
	}
}
