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
import com.huateng.vo.TPayShopOpenData;
import com.opensymphony.xwork2.ActionSupport;

public class RcvTUPayShopOpenPageAction extends ActionSupport {


	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(RcvTUPayShopOpenPageAction.class);

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

		logger.debug(".接收到银联开通认证支付页面跳转结果通知.");
		upayLog.debug(".接收到银联开通认证支付页面页面跳转结果通知.");

		Map<String, String> requestMaps = HttpStringFilter.filterRequestParams(
				request, fileterStr);
		TPayShopOpenData tPayShopOpenData = this.assemlyTpayData(requestMaps);

		try {

			MsgData msgData = tpayService.payShopOpenNotice(true,
					tPayShopOpenData);

			logger.info("......向移动商城返回开通认证支付页面消息通知......[{}]",
					msgData.getRedirectHtml());
			upayLog.info("......向移动商城返回开通认证支付页面消息通知......[{}]",
					msgData.getRedirectHtml());

			writer = response.getWriter();
			writer.write(msgData.getRedirectHtml());
			writer.flush();

			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					CoreConstant.TransCode.TSHOP_PAY_NOTICE.getCode(),
					CoreConstant.TransCode.TSHOP_PAY_NOTICE.getDesc(),
					CoreConstant.BankCode.TPAY.getCode(),
					CoreConstant.BankCode.TPAY.getCode(), "接收到银联开通认证支付页面页面跳转结果通知");

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
	public TPayShopOpenData assemlyTpayData(Map<String, String> temporaryPayInfo) {
		TPayShopOpenData data = new TPayShopOpenData();

		data.setVersion(temporaryPayInfo.get("version"));
		data.setCertId(temporaryPayInfo.get("certId"));
		data.setSignature(temporaryPayInfo.get("signature"));
		data.setEncoding(temporaryPayInfo.get("encoding"));
		data.setTxnType(temporaryPayInfo.get("txnType"));
		data.setTxnSubType(temporaryPayInfo.get("txnSubType"));
		data.setBizType(temporaryPayInfo.get("bizType"));
		data.setAccessType(temporaryPayInfo.get("accessType"));
		data.setMerId(temporaryPayInfo.get("merId"));
		data.setAccNo(temporaryPayInfo.get("accNo"));
		data.setReqReserved(temporaryPayInfo.get("reqReserved"));
		data.setReserved(temporaryPayInfo.get("reserved"));
		data.setRespCode(temporaryPayInfo.get("respCode"));
		data.setRespMsg(temporaryPayInfo.get("respMsg"));
		data.setActivateStatus(temporaryPayInfo.get("activateStatus"));
		data.setPayCardType(temporaryPayInfo.get("payCardType"));
		data.setCustomerInfo(temporaryPayInfo.get("customerInfo"));
		data.setTemporaryPayInfo(temporaryPayInfo.get("temporaryPayInfo"));

		return data;
	}
}
