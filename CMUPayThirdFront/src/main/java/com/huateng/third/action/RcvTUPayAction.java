package com.huateng.third.action;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.adaper.listener.ServiceFactory;
import com.huateng.core.base.BaseAction;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.RemoteMsg;
import com.huateng.third.bean.TUPayRspVo;
import com.huateng.third.common.IpUtil;
import com.huateng.third.logFormat.MessageLogger;
import com.huateng.third.service.TUPayService;


/**
 * 接收银联端的支付缴费应答http请求
 * 
 * @author leon
 * 
 */
public class RcvTUPayAction extends BaseAction {

	private static final long serialVersionUID = 8805570499616212830L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog = MessageLogger.getLogger(getClass());
	private static Map<String, String> paramMap;
	
	static{
		paramMap = new HashMap<String, String>();
		paramMap.put(CoreConstant.THIRD_PAY_ORG_ID, CoreConstant.UNION_PAY_ORG_ID);
	}

	public String receive() {
		HttpServletRequest request = this.getHttpRequest();
		HttpServletResponse response = this.getHttpResponse();
		settingResponse(response);
		
		try {
			request.setCharacterEncoding(request.getParameter(CoreConstant.UnionPayMsg.ENCODING.getValue()) == null ?
					CoreConstant.MSG_ENCODING : request.getParameter(CoreConstant.UnionPayMsg.ENCODING.getValue()));
			
			//请求来源信息
			RemoteMsg remoteMsg = IpUtil.getRemoteMsg(request);
			
			logger.info("接收银联端的支付缴费应答请求：IP[{}]，请求来源URL[{}]", remoteMsg.getIp(), remoteMsg.getRequestURL());
			upayLog.info("接收银联端的支付缴费应答请求：IP[{}]，请求来源URL[{}]", remoteMsg.getIp(), remoteMsg.getRequestURL());
			
			// 新建用于接收缴费支付应答报文的对象
//			TUPayRspVo tUPayRspVo = new TUPayRspVo();
			
			// 将Request对象中的属性去掉特殊字符后转成Map
//			Map<String, String> requestMaps = HttpStringFilter.filterRequestParamsForEncoding(request, fileterStr, encoding);
			Map<String, String> respParam = getAllRequestParam(request);
			logger.info("接收银联端的支付缴费应答请求：[{}]", respParam);
			upayLog.info("接收银联端的支付缴费应答请求：[{}]", respParam);
			
			TUPayService tupayService = (TUPayService) ServiceFactory.getInstance().findService("tupayService");
			
			/**
			 * 验签
			 */
			tupayService.checkSign(CoreConstant.UNION_PAY_ORG_ID, respParam);
			
			// 将应答报文组装到对象中
//			assemlyCmuData(respParam, tUPayRspVo);
			
			/**
			 * 发送报文信息给核心平台
			 */
			logger.info("开始将银联结果通知报文发往核心：[{}]", respParam);
			upayLog.info("开始将银联结果通知报文发往核心：[{}]", respParam);
			tupayService.sendMsg(null, (Serializable) respParam, paramMap);
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return NONE; 
	}

	/**
	 * 转换请求信息为支付缴费应答对象
	 * 
	 * @param request
	 *            请求对象
	 * @param cmuData
	 *            支付对象
	 */
	private void assemlyCmuData(Map<String, String> requestMaps, TUPayRspVo unionPayRspVo) {
		/* 获取请求信息 */
		String version = requestMaps.get("version");
		String encoding = requestMaps.get("encoding");
		String certId = requestMaps.get("certId");
		String signature = requestMaps.get("signature");
		String txnType = requestMaps.get("txnType");
		String txnSubType = requestMaps.get("txnSubType");
		String bizType = requestMaps.get("bizType");
		String accessType = requestMaps.get("accessType");
		String merId = requestMaps.get("merId");
		String orderId = requestMaps.get("orderId");
		String txnTime = requestMaps.get("txnTime");
		String accNo = requestMaps.get("accNo");
		String txnAmt = requestMaps.get("txnAmt");
		String currencyCode = requestMaps.get("currencyCode");
		String reqReserved = requestMaps.get("reqReserved");
		String reserved = requestMaps.get("reserved");
		String queryId= requestMaps.get("queryId");
		String respCode= requestMaps.get("respCode");
		String respMsg = requestMaps.get("respMsg");
		String respTime = requestMaps.get("respTime");
		String settleAmt = requestMaps.get("settleAmt");
		String settleCurrencyCode = requestMaps.get("settleCurrencyCode");
		String settleDate = requestMaps.get("settleDate");
		String traceNo = requestMaps.get("traceNo");
		String traceTime = requestMaps.get("traceTime");
		String exchangeDate = requestMaps.get("exchangeDate");
		String exchangeRate = requestMaps.get("exchangeRate");
		String payCardType = requestMaps.get("payCardType");
		String payType = requestMaps.get("payType");
		String issuerIdentifyMode = requestMaps.get("issuerIdentifyMode");
		
		logger.info("接收到银联支付缴费应答报文,应答参数version:{},encoding:{},certId:{},signature:{},txnType:{},txnSubType:{},bizType:{}" +
				",accessType:{},merId:{},orderId:{},txnTime:{},accNo:{},txnAmt:{},currencyCode:{},reqReserved:{},reserved:{}" +
				",queryId:{},respCode:{},respMsg:{},respTime:{},settleAmt:{},settleCurrencyCode:{},settleDate:{},traceNo:{},traceTime:{}" +
				",exchangeDate:{},exchangeRate:{},payCardType:{},payType:{},issuerIdentifyMode:{}",
		new Object[] { version, encoding, certId, signature, txnType, txnSubType, bizType, accessType, merId, orderId, txnTime, accNo, txnAmt,
				currencyCode, reqReserved, reserved, queryId, respCode, respMsg, respTime, settleAmt, settleCurrencyCode, 
				settleDate, traceNo, traceTime, exchangeDate, exchangeRate, payCardType, payType, issuerIdentifyMode});
		
		upayLog.info("接收到银联支付缴费应答报文,应答参数version:{},encoding:{},certId:{},signature:{},txnType:{},txnSubType:{},bizType:{}" +
				",accessType:{},merId:{},orderId:{},txnTime:{},accNo:{},txnAmt:{},currencyCode:{},reqReserved:{},reserved:{}" +
				",queryId:{},respCode:{},respMsg:{},respTime:{},settleAmt:{},settleCurrencyCode:{},settleDate:{},traceNo:{},traceTime:{}" +
				",exchangeDate:{},exchangeRate:{},payCardType:{},payType:{},issuerIdentifyMode:{}",
		new Object[] { version, encoding, certId, signature, txnType, txnSubType, bizType, accessType, merId, orderId, txnTime, accNo, txnAmt,
				currencyCode, reqReserved, reserved, queryId, respCode, respMsg, respTime, settleAmt, settleCurrencyCode, 
				settleDate, traceNo, traceTime, exchangeDate, exchangeRate, payCardType, payType, issuerIdentifyMode});
		
		unionPayRspVo.setVersion(version);
		unionPayRspVo.setEncoding(encoding);
		unionPayRspVo.setCertId(certId);
		unionPayRspVo.setSignature(signature);
		unionPayRspVo.setTxnType(txnType);
		unionPayRspVo.setTxnSubType(txnSubType);
		unionPayRspVo.setBizType(bizType);
		unionPayRspVo.setAccessType(accessType);
		unionPayRspVo.setMerId(merId);
		unionPayRspVo.setOrderId(orderId);
		unionPayRspVo.setTxnTime(txnTime);
		unionPayRspVo.setAccNo(accNo);
		unionPayRspVo.setTxnAmt(txnAmt);
		unionPayRspVo.setCurrencyCode(settleCurrencyCode);
		unionPayRspVo.setReqReserved(reqReserved);
		unionPayRspVo.setReserved(reserved);
		unionPayRspVo.setQueryId(queryId);
		unionPayRspVo.setRespCode(respCode);
		unionPayRspVo.setRespMsg(respMsg);
		unionPayRspVo.setRespTime(respTime);
		unionPayRspVo.setSettleAmt(settleAmt);
		unionPayRspVo.setSettleCurrencyCode(settleCurrencyCode);
		unionPayRspVo.setSettleDate(settleDate);
		unionPayRspVo.setTraceNo(traceNo);
		unionPayRspVo.setTraceTime(traceTime);
		unionPayRspVo.setExchangeDate(exchangeDate);
		unionPayRspVo.setExchangeRate(exchangeRate);
		unionPayRspVo.setPayCardType(payCardType);
		unionPayRspVo.setIssuerIdentifyMode(issuerIdentifyMode);
	}
	
	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
			}
		}
		return res;
	}
	
}
