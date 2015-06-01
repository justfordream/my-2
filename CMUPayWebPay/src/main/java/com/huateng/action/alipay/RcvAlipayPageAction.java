package com.huateng.action.alipay;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.AliData;
import com.huateng.bean.TenData;
import com.huateng.core.common.CoreConstant;
import com.huateng.filter.HttpStringFilter;
import com.huateng.log.LogHandle;
import com.huateng.log.MessageLogger;
import com.huateng.service.AliPayService;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.vo.MsgData;
import com.opensymphony.xwork2.ActionSupport;

public class RcvAlipayPageAction extends ActionSupport {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(RcvAlipayPageAction.class);

	@Autowired
	private AliPayService aliPayService;

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
		AliData aliData = this.assemlyAliData(requestMaps);
		try {
			MsgData msgData = aliPayService.shopAliPayNotice(true, aliData);
			logger.info(".向移动商城发送支付请求.[{}]", msgData.getRedirectHtml());
			writer = response.getWriter();
			writer.write(msgData.getRedirectHtml());
			writer.flush();
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					CoreConstant.TransCode.ALI_SHOP_PAY_NOTICE.getCode(),
					CoreConstant.TransCode.ALI_SHOP_PAY_NOTICE.getDesc(),
					CoreConstant.BankCode.ALI.getCode(),
					CoreConstant.BankCode.ALI.getCode(), ".接收到支付宝支付页面跳转结果通知.");
		} catch (Exception e) {
			logger.error("处理支付宝支付页面跳转结果通知错误", e.getMessage());
			this.logHandle.info(false,
					CoreConstant.ErrorCode.CODE_015A06.getCode(),
					CoreConstant.TransCode.ALI_SHOP_PAY_NOTICE.getCode(),
					CoreConstant.TransCode.ALI_SHOP_PAY_NOTICE.getDesc(),
					CoreConstant.BankCode.ALI.getCode(),
					CoreConstant.BankCode.ALI.getCode(), e.getMessage());
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
	private AliData assemlyAliData(Map<String, String> requestMaps) {
		AliData data = new AliData();

		data.setIs_success(requestMaps.get("is_success"));
		data.setSign_type(requestMaps.get("sign_type"));
		data.setSign(requestMaps.get("sign"));
		data.setOut_trade_no(requestMaps.get("out_trade_no"));
		data.setSubject(requestMaps.get("subject"));
		data.setPayment_type(requestMaps.get("payment_type"));
		data.setExterface(requestMaps.get("exterface"));
		data.setTrade_no(requestMaps.get("trade_no"));
		data.setTrade_status(requestMaps.get("trade_status"));
		data.setNotify_id(requestMaps.get("notify_id"));
		data.setNotify_time(requestMaps.get("notify_time"));
		data.setNotify_type(requestMaps.get("notify_type"));
		data.setSeller_email(requestMaps.get("seller_email"));
		data.setBuyer_email(requestMaps.get("buyer_email"));
		data.setSeller_id(requestMaps.get("seller_id"));
		data.setBuyer_id(requestMaps.get("buyer_id"));
		data.setTotal_fee(requestMaps.get("total_fee"));
		data.setBody(requestMaps.get("body"));
		data.setExtra_common_param(requestMaps.get("extra_common_param"));
		data.setAgent_user_id(requestMaps.get("agent_user_id"));

		return data;

	}

}
