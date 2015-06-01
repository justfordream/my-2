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
import com.huateng.third.common.IpUtil;
import com.huateng.third.logFormat.MessageLogger;
import com.huateng.third.service.ALIPayService;
import com.huateng.third.service.TUPayService;

public class RcvALIPayAction extends BaseAction {
	
	private static final long serialVersionUID = 8805570499616212830L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger alipayLog = MessageLogger.getLogger(getClass());


	public String receive() {
		HttpServletRequest request = this.getHttpRequest();
		HttpServletResponse response = this.getHttpResponse();
		settingResponse(response);
		
		try {
			request.setCharacterEncoding(request.getParameter(CoreConstant.ALIPayMsg.ENCODING.getValue()) == null ?
					CoreConstant.MSG_ENCODING : request.getParameter(CoreConstant.ALIPayMsg.ENCODING.getValue()));
			
			//请求来源信息
			RemoteMsg remoteMsg = IpUtil.getRemoteMsg(request);
			
			logger.info("接收支付宝的支付缴费应答请求：IP[{}]，请求来源URL[{}]", remoteMsg.getIp(), remoteMsg.getRequestURL());
			alipayLog.info("接收支付宝的支付缴费应答请求：IP[{}]，请求来源URL[{}]", remoteMsg.getIp(), remoteMsg.getRequestURL());
			
			Map<String, String> respParam = getAllRequestParam(request);
			logger.info("接收支付宝的支付缴费应答请求：[{}]", respParam);
			alipayLog.info("接收支付宝的支付缴费应答请求：[{}]", respParam);
			
			ALIPayService alipayService = (ALIPayService) ServiceFactory.getInstance().findService("alipayService");
			
			/**
			 * 验签
			 */
			alipayService.checkSign(CoreConstant.ALI_PAY_ORG_ID, respParam);
			
			
			/**
			 * 发送报文信息给核心平台
			 */
			logger.info("开始将支付宝结果通知报文发往核心：[{}]", respParam);
			alipayLog.info("开始将支付宝结果通知报文发往核心：[{}]", respParam);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put(CoreConstant.THIRD_PAY_ORG_ID, CoreConstant.ALI_PAY_ORG_ID);
			alipayService.sendMsg(CoreConstant.ALI_PAY_DEST_ID, (Serializable) respParam, paramMap);
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return NONE; 
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
