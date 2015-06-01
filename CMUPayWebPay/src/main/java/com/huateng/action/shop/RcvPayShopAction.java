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
import com.huateng.service.RcvShopPayService;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.vo.MsgData;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 处理移动商城的缴费、支付请求
 * 
 * @author hys
 * 
 */
public class RcvPayShopAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog = MessageLogger
			.getLogger(RcvPayShopAction.class);

	@Autowired
	private RcvShopPayService rcvShopPayService;

	@Value("${SPECIAL_FILETER_LIST}")
	private String fileterStr;

	/**
	 * 接受省中心支付请求信息
	 */
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
			MsgData msgData = rcvShopPayService.sendPayRequest(cmuData);

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

	/**
	 * 转换请求信息为支付对象
	 * 
	 * @param request
	 *            请求对象
	 * @param cmuData
	 *            支付对象
	 */
	private void assemlyCmuData(Map<String, String> requestMaps,
			ShopData shopData) {
		/* 获取请求信息 */
		String MerID = requestMaps.get("MerID");
		String OrderID = requestMaps.get("OrderID");
		String OrderTime = requestMaps.get("OrderTime");
		String payMent = requestMaps.get("Payment");
		String CurType = requestMaps.get("CurType");
		String chargeMoney = requestMaps.get("ChargeMoney");
		String IDValue = requestMaps.get("IDValue");
		String IDType = requestMaps.get("IDType");
		String HomeProv = requestMaps.get("HomeProv");
		String ProdCnt = requestMaps.get("ProdCnt");
		String ProdID = requestMaps.get("ProdID");
		String Commision = requestMaps.get("Commision");

		String RebateFee = requestMaps.get("RebateFee");
		String ServiceFee = requestMaps.get("ServiceFee");
		String ActivityNO = requestMaps.get("ActivityNO");
		String clientIp = requestMaps.get("CLIENTIP");

		String CreditCardFee = requestMaps.get("CreditCardFee");
		String ProdDiscount = requestMaps.get("ProdDiscount");

		String MerURL = requestMaps.get("MerURL");
		String MerVAR = requestMaps.get("MerVAR");
		String BackURL = requestMaps.get("BackURL");
		String Lang = requestMaps.get("Lang");
		String BankID = requestMaps.get("BankID");
		String MCODE = requestMaps.get("MCODE");
		String Sig = requestMaps.get("Sig");

		String ProdShelfNO = requestMaps.get("ProdShelfNO");
		String OrderType = requestMaps.get("OrderType");
		String PayTimeoutTime = requestMaps.get("PayTimeoutTime");
		String ShopMerId = requestMaps.get("ShopMerId");

		String Reserve1 = requestMaps.get("Reserve1");
		String Reserve2 = requestMaps.get("Reserve2");
		String Reserve3 = requestMaps.get("Reserve3");
		String Reserve4 = requestMaps.get("Reserve4");

		logger.info(
				"接收[({})]网厅支付请求,请求参数MerID:{},OrderID:{},OrderTime:{},payMent:{},CurType:{},ChargeMoney:{},"
						+ "IDValue:{},IDType:{},HomeProv:{},ProdCnt:{},ProdID:{},Commision:{},RebateFee:{},CreditCardFee:{},ProdDiscount:{},ServiceFee:{},ActivityNO:{},"
						+ "MerURL:{},MerVAR:{},BackURL:{},Lang:{},BankID:{},CLIENTIP:{},MCODE:{},Sig:{},ProdShelfNO:{},OrderType:{},PayTimeoutTime:{},ShopMerId:{},Reserve1:{},Reserve2:{},Reserve3:{},Reserve4:{}",
				new Object[] { MerID, MerID, OrderID, OrderTime, payMent,
						CurType, chargeMoney, IDValue, IDType, HomeProv,
						ProdCnt, ProdID, Commision, RebateFee, CreditCardFee,
						ProdDiscount, ServiceFee, ActivityNO, MerURL, MerVAR,
						BackURL, Lang, BankID, clientIp, MCODE, Sig,
						ProdShelfNO, OrderType, PayTimeoutTime, ShopMerId,
						Reserve1, Reserve2, Reserve3, Reserve4 });

		upayLog.info(
				"接收[({})]网厅支付请求,请求参数MerID:{},OrderID:{},OrderTime:{},payMent:{},CurType:{},ChargeMoney:{},"
						+ "IDValue:{},IDType:{},HomeProv:{},ProdCnt:{},ProdID:{},Commision:{},RebateFee:{},CreditCardFee:{},ProdDiscount:{},ServiceFee:{},ActivityNO:{},"
						+ "MerURL:{},MerVAR:{},BackURL:{},Lang:{},BankID:{},CLIENTIP:{},MCODE:{},Sig:{},ProdShelfNO:{},OrderType:{},PayTimeoutTime:{},ShopMerId:{},Reserve1:{},Reserve2:{},Reserve3:{},Reserve4:{}",
				new Object[] { MerID, MerID, OrderID, OrderTime, payMent,
						CurType, chargeMoney, IDValue, IDType, HomeProv,
						ProdCnt, ProdID, Commision, RebateFee, CreditCardFee,
						ProdDiscount, ServiceFee, ActivityNO, MerURL, MerVAR,
						BackURL, Lang, BankID, clientIp, MCODE, Sig,
						ProdShelfNO, OrderType, PayTimeoutTime, ShopMerId,
						Reserve1, Reserve2, Reserve3, Reserve4 });
		shopData.setMerID(MerID);
		shopData.setOrderID(OrderID);
		shopData.setOrderTime(OrderTime);
		shopData.setPayment(payMent);
		shopData.setCurType(CurType);
		shopData.setChargeMoney(chargeMoney);
		shopData.setIDValue(IDValue);
		shopData.setIDType(IDType);
		shopData.setHomeProv(HomeProv);
		shopData.setProdCnt(ProdCnt);
		shopData.setProdId(ProdID);
		shopData.setCommision(Commision);
		shopData.setRebateFee(RebateFee);
		shopData.setCreditCardFee(CreditCardFee);
		shopData.setProdDiscount(ProdDiscount);
		shopData.setServiceFee(ServiceFee);
		shopData.setClientIp(clientIp);
		shopData.setActivityNo(ActivityNO);
		shopData.setServerURL(MerURL);
		shopData.setMerVar(MerVAR);
		shopData.setBackURL(BackURL);
		shopData.setLang(Lang);
		shopData.setBankID(BankID);
		shopData.setMcode(MCODE);
		shopData.setSig(Sig);
		shopData.setProdShelfNO(ProdShelfNO);
		shopData.setOrderType(OrderType);
		shopData.setPayTimeoutTime(PayTimeoutTime);
		shopData.setShopMerId(ShopMerId);
		shopData.setReserve1(Reserve1);
		shopData.setReserve2(Reserve2);
		shopData.setReserve3(Reserve3);
		shopData.setReserve4(Reserve4);
	}
}
