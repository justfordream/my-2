package com.huateng.action.shop;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.ShopData;
import com.huateng.core.common.RemoteMsg;
import com.huateng.core.util.IpUtil;
import com.huateng.filter.HttpStringFilter;
import com.huateng.log.MessageLogger;
import com.huateng.service.RcvPayShopOpenService;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.vo.MsgData;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 接收移动商城的开通认证支付请求
 * 
 * @author zhaojunnan
 * 
 */
public class RcvPayShopOpenAction extends ActionSupport {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(RcvPayShopOpenAction.class);

	@Autowired
	private RcvPayShopOpenService rcvPayShopOpenService;

	@Value("${SPECIAL_FILETER_LIST}")
	private String fileterStr;

	public String recieve() {
		PrintWriter writer = null;
		try {
			HttpServletRequest request = SessionRequestUtil.getRequest();
			HttpServletResponse response = SessionRequestUtil.getResponse();
			this.settingResponse(response);
			ShopData cmuData = new ShopData();
			Map<String, String> requestMaps = HttpStringFilter
					.filterRequestParams(request, fileterStr);

			// 请求来源信息
			RemoteMsg remoteMsg = IpUtil.getRemoteMsg(request);
			logger.info("请求来源IP[{}]，请求来源URL[{}]", remoteMsg.getIp(),
					remoteMsg.getRequestURL());
			this.assemlyCmuData(requestMaps, cmuData);

			/*
			 * 转发信息至指定银行
			 */
			MsgData msgData = rcvPayShopOpenService
					.sendPayShopOpenRequest(cmuData);

			writer = SessionRequestUtil.getResponse().getWriter();
			writer.write(msgData.getRedirectHtml());
			writer.flush();
		} catch (Exception e) {
			logger.error("移动商城缴费异常,订单号:{}", e.getMessage());
			upayLog.error("移动商城缴费异常", e.getMessage());
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

	private void assemlyCmuData(Map<String, String> requestMaps,
			ShopData shopData) {

		/* 获取请求信息 */
		String MerID = requestMaps.get("MerID");
		String OrderID = requestMaps.get("OrderID");
		String OrderTime = requestMaps.get("OrderTime");
		String Payment = requestMaps.get("Payment");
		String BankAcctID = requestMaps.get("BankAcctID");
		String CustomerInfo = requestMaps.get("CustomerInfo");
		String MerURL = requestMaps.get("MerURL");
		String MerVAR = requestMaps.get("MerVAR");
		String BackURL = requestMaps.get("BackURL");
		String Lang = requestMaps.get("Lang");
		String CLIENTIP = requestMaps.get("CLIENTIP");
		String MCODE = requestMaps.get("MCODE");
		String Sig = requestMaps.get("Sig");

		logger.info(
				"接收[({})]网厅支付请求,请求参数MerID:{},OrderID:{},OrderTime:{},Payment:{},BankAcctID:{},CustomerInfo:{},"
						+ "MerURL:{},MerVAR:{},BackURL:{},Lang:{},CLIENTIP:{},MCODE:{},Sig:{}",
				new Object[] { MerID, MerID, OrderID, OrderTime, Payment,
						BankAcctID, CustomerInfo, MerURL, MerVAR, BackURL,
						Lang, CLIENTIP, MCODE, Sig });

		upayLog.info(
				"接收[({})]网厅支付请求,请求参数MerID:{},OrderID:{},OrderTime:{},Payment:{},BankAcctID:{},CustomerInfo:{},"
						+ "MerURL:{},MerVAR:{},BackURL:{},Lang:{},CLIENTIP:{},MCODE:{},Sig:{}",
				new Object[] { MerID, MerID, OrderID, OrderTime, Payment,
						BankAcctID, CustomerInfo, MerURL, MerVAR, BackURL,
						Lang, CLIENTIP, MCODE, Sig });

		shopData.setMerID(MerID);
		shopData.setOrderID(OrderID);
		shopData.setOrderTime(OrderTime);
		shopData.setPayment(Payment);
		shopData.setBankAcctID(BankAcctID);
		shopData.setCustomerInfo(CustomerInfo);
		shopData.setServerURL(MerURL);
		shopData.setMerVar(MerVAR);
		shopData.setBackURL(BackURL);
		shopData.setLang(Lang);
		shopData.setClientIp(CLIENTIP);
		shopData.setMcode(MCODE);
		shopData.setSig(Sig);
	}
}
