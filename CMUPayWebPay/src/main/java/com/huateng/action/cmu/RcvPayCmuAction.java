package com.huateng.action.cmu;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.CmuData;
import com.huateng.core.common.RemoteMsg;
import com.huateng.core.util.IpUtil;
import com.huateng.core.util.RequestUtil;
import com.huateng.filter.HttpStringFilter;
import com.huateng.log.MessageLogger;
import com.huateng.service.RcvPayService;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.vo.MsgData;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 接收省级"支付"请求信息
 * 
 * @author Gary
 * 
 */
public class RcvPayCmuAction extends ActionSupport {

	private static final long serialVersionUID = -6903465722416869669L;

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private MessageLogger upayLog = MessageLogger.getLogger(RcvPayCmuAction.class);
	@Autowired
	private RcvPayService rcvPayService;
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
			CmuData cmuData = new CmuData();
			logger.debug("开始进入支付请求处理");
			upayLog.info("开始进入支付请求处理");
			Map<String, String> requestMaps = HttpStringFilter.filterRequestParams(request,fileterStr);
			
			//请求来源信息
			RemoteMsg remoteMsg=IpUtil.getRemoteMsg(request);
			
			logger.info("请求来源IP[{}]，请求来源URL[{}]",remoteMsg.getIp(),remoteMsg.getRequestURL());
			upayLog.info("请求来源IP[{}]，请求来源URL[{}]",remoteMsg.getIp(),remoteMsg.getRequestURL());
			
			/*
			 * 获取参数
			 */
			this.assemlyCmuData(requestMaps, cmuData);
			/*
			 * 转发信息至指定银行
			 */
			MsgData msgData = rcvPayService.sendPayRequest(cmuData);

			logger.info("......向银行发送支付请求......[{}]", msgData.getRedirectHtml());
			upayLog.info("......向银行发送支付请求......[{}]", msgData.getRedirectHtml());
			writer = SessionRequestUtil.getResponse().getWriter();

			writer.write(msgData.getRedirectHtml());

			writer.flush();
		} catch (Exception e) {
			logger.error("网厅缴费异常",e.getMessage());
			upayLog.error("网厅缴费异常",e.getMessage());
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
	private void assemlyCmuData(Map<String, String> requestMaps, CmuData cmuData) {
		/* 获取请求信息 */
		String MerID = requestMaps.get("MerID");
		String OrderID = requestMaps.get("OrderID");
		String OrderTime = requestMaps.get("OrderTime");
		String Payed = requestMaps.get("Payed");
		String CurType = requestMaps.get("CurType");
		String IDValue = requestMaps.get("IDValue");
		String IDType = requestMaps.get("IDType");
		String MerVAR = requestMaps.get("MerVAR");
		String Lang = requestMaps.get("Lang");
		String BankID = requestMaps.get("BankID");
		String CLIENTIP = requestMaps.get("CLIENTIP");
		String MCODE = requestMaps.get("MCODE");

		String Sig = RequestUtil.paseDecode(requestMaps.get("Sig"));
		String BackURL = RequestUtil.paseDecode(requestMaps.get("BackURL"));
		String MerURL = RequestUtil.paseDecode(requestMaps.get("MerURL"));

		upayLog.info("......接收[({})]网厅支付请求,请求参数......[MerID:{}],[OrderID:{}],[OrderTime:{}],"
						+ "[Payed:{}],[CurType:{}],[IDValue:{}],[IDType:{}],[MerURL:{}],[MerVAR:{}],[BackURL:{}],[Lang:{}],"
						+ "[Sig:{}],[BankID:{}],[CLIENTIP:{}],[MCODE:{}]",
				new Object[] { MerID, MerID, OrderID, OrderTime, Payed,
						CurType, IDValue, IDType, MerURL, MerVAR, BackURL,
						Lang, Sig, BankID, CLIENTIP, MCODE });
		
		logger.info("......接收[({})]网厅支付请求,请求参数......[MerID:{}],[OrderID:{}],[OrderTime:{}],"
				+ "[Payed:{}],[CurType:{}],[IDValue:{}],[IDType:{}],[MerURL:{}],[MerVAR:{}],[BackURL:{}],[Lang:{}],"
				+ "[Sig:{}],[BankID:{}],[CLIENTIP:{}],[MCODE:{}]",
		new Object[] { MerID, MerID, OrderID, OrderTime, Payed,
				CurType, IDValue, IDType, MerURL, MerVAR, BackURL,
				Lang, Sig, BankID, CLIENTIP, MCODE });
		/* 组装payReq bankID 和 cmuId 为必填 */
		cmuData.setMerID(MerID);
		cmuData.setOrderID(OrderID);
		cmuData.setOrderTime(OrderTime);
		cmuData.setPayed(Payed);
		cmuData.setCurType(CurType);
		cmuData.setIDValue(IDValue);
		cmuData.setIDType(IDType);
		cmuData.setServerURL(MerURL);
		cmuData.setMerVAR(MerVAR);
		cmuData.setBackURL(BackURL);
		cmuData.setMerURL(MerURL);
		cmuData.setLang(Lang);
		cmuData.setSig(Sig);
		cmuData.setBankID(BankID);
		cmuData.setCLIENTIP(CLIENTIP);
		cmuData.setMCODE(MCODE);
		cmuData.setCmuID(MerID);
	}
}
