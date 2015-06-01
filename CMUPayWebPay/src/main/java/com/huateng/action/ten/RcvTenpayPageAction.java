package com.huateng.action.ten;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.TenData;
import com.huateng.core.common.CoreConstant;
import com.huateng.filter.HttpStringFilter;
import com.huateng.log.LogHandle;
import com.huateng.log.MessageLogger;
import com.huateng.service.TenPayService;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.vo.MsgData;
import com.opensymphony.xwork2.ActionSupport;

public class RcvTenpayPageAction extends ActionSupport {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(RcvTenpayPageAction.class);

	@Autowired
	private TenPayService tenPayService;

	@Autowired
	private LogHandle logHandle;

	@Value("${SPECIAL_FILETER_LIST}")
	private String fileterStr;

	/**
	 * 处理支付
	 */
	public String receive() {
		PrintWriter writer = null;
		HttpServletRequest request = SessionRequestUtil.getRequest();
		HttpServletResponse response = SessionRequestUtil.getResponse();
		this.settingResponse(response);
		logger.debug(".接收到财付通支付页面跳转结果通知.");
		upayLog.debug(".接收到财付通支付页面跳转结果通知.");
		Map<String, String> requestMaps = HttpStringFilter.filterRequestParams(
				request, fileterStr);
		TenData tenData = this.assemlyCCBData(requestMaps);
		try {
			MsgData msgData = tenPayService.shopTenPayNotice(true, tenData);
			logger.info(".向移动商城发送支付请求.[{}]", msgData.getRedirectHtml());
			writer = response.getWriter();
			writer.write(msgData.getRedirectHtml());
			writer.flush();
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					CoreConstant.TransCode.TEN_SHOP_PAY_NOTICE.getCode(),
					CoreConstant.TransCode.TEN_SHOP_PAY_NOTICE.getDesc(),
					CoreConstant.BankCode.TEN.getCode(),
					CoreConstant.BankCode.TEN.getCode(), ".接收到财付通支付页面跳转结果通知.");
		} catch (Exception e) {
			logger.error("处理财付通支付页面跳转结果通知错误", e.getMessage());
			this.logHandle.info(false,
					CoreConstant.ErrorCode.CODE_015A06.getCode(),
					CoreConstant.TransCode.TEN_SHOP_PAY_NOTICE.getCode(),
					CoreConstant.TransCode.TEN_SHOP_PAY_NOTICE.getDesc(),
					CoreConstant.BankCode.TEN.getCode(),
					CoreConstant.BankCode.TEN.getCode(), e.getMessage());
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
	private void settingResponse(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/html;charset=utf-8");
	}

	/**
	 * 解析参数
	 * 
	 * @param requestMaps
	 * @return
	 */
	private TenData assemlyCCBData(Map<String, String> requestMaps) {
		TenData data = new TenData();

		data.setSign_type(requestMaps.get("sign_type"));
		data.setService_version(requestMaps.get("service_version"));
		data.setInput_charset(requestMaps.get("input_charset"));
		data.setSign(requestMaps.get("sign"));
		data.setSign_key_index(requestMaps.get("sign_key_index"));
		data.setTrade_mode(requestMaps.get("trade_mode"));
		data.setTrade_state(requestMaps.get("trade_state"));
		data.setPay_info(requestMaps.get("pay_info"));
		data.setPartner(requestMaps.get("partner"));
		data.setBank_type(requestMaps.get("bank_type"));
		data.setBank_billno(requestMaps.get("bank_billno"));
		data.setTotal_fee(requestMaps.get("total_fee"));
		data.setFee_type(requestMaps.get("fee_type"));
		data.setNotify_id(requestMaps.get("notify_id"));
		data.setTransaction_id(requestMaps.get("transaction_id"));
		data.setOut_trade_no(requestMaps.get("out_trade_no"));
		data.setAttach(requestMaps.get("attach"));
		data.setTime_end(requestMaps.get("time_end"));
		data.setTransport_fee(requestMaps.get("transport_fee"));
		data.setProduct_fee(requestMaps.get("product_fee"));
		data.setDiscount(requestMaps.get("discount"));
		data.setBuyer_alias(requestMaps.get("buyer_alias"));

		return data;

	}
}
