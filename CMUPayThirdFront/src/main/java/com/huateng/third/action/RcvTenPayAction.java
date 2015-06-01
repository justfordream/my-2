/**
 * 
 */
package com.huateng.third.action;

import java.io.IOException;
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
import com.huateng.third.service.TenPayService;

/**
 * 接收财付通的支付结果通知请求
 * @author Manzhizhen
 *
 */
public class RcvTenPayAction extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger tenPayLog = MessageLogger.getLogger(getClass());
	private static final long serialVersionUID = 6501967158645741617L;
	private static Map<String, String> paramMap;
	
	static{
		paramMap = new HashMap<String, String>();
		paramMap.put(CoreConstant.THIRD_PAY_ORG_ID, CoreConstant.TEN_PAY_ORG_ID);
		paramMap.put(CoreConstant.TXN_CODE, CoreConstant.TenPayTxnCode.NOTICE_TXN.getValue());
	}
	

	@Override
	public String receive() {
		HttpServletRequest request = this.getHttpRequest();
		HttpServletResponse response = this.getHttpResponse();
		settingResponse(response);
		
		try {
			request.setCharacterEncoding(request.getParameter(CoreConstant.UnionPayMsg.ENCODING.getValue()) == null ?
					CoreConstant.MSG_ENCODING : request.getParameter(CoreConstant.UnionPayMsg.ENCODING.getValue()));
			
			//请求来源信息
			RemoteMsg remoteMsg = IpUtil.getRemoteMsg(request);
			
			logger.info("接收财付通端的支付缴费应答请求：IP[{}]，请求来源URL[{}]", remoteMsg.getIp(), remoteMsg.getRequestURL());
			tenPayLog.info("接收财付通端的支付缴费应答请求：IP[{}]，请求来源URL[{}]", remoteMsg.getIp(), remoteMsg.getRequestURL());
			
			// 将Request对象中的属性去掉特殊字符后转成Map
//			Map<String, String> requestMaps = HttpStringFilter.filterRequestParamsForEncoding(request, fileterStr, encoding);
			Map<String, String> respParam = getAllRequestParam(request);
			logger.info("接收财付通端的支付缴费应答请求：[{}]", respParam);
			tenPayLog.info("接收财付通端的支付缴费应答请求：[{}]", respParam);
			
			TenPayService tenPayService = (TenPayService) ServiceFactory.getInstance().findService("tenpayService");
			
			/**
			 * 验签
			 */
			tenPayService.checkSign(CoreConstant.UNION_PAY_ORG_ID, respParam);
			
			/**
			 * 发送报文信息给核心平台
			 */
			logger.info("开始将财付通结果通知报文发往核心：[{}]", respParam);
			tenPayLog.info("开始将财付通结果通知报文发往核心：[{}]", respParam);
			tenPayService.sendMsg("9997", (Serializable) respParam, paramMap);
			
			response.getWriter().write("success");
			
		} catch (Exception e) {
			if(response != null) {
				try {
					response.getWriter().write("fail");
				} catch (IOException e1) {
					logger.error("", e1);
				}
			}
			logger.error("", e);
		}
		
		return NONE; 
	}
	
	/**
	 * 设置响应公共信息
	 * 
	 * @param response
	 */
	public void settingResponse(HttpServletResponse response) {
		response.setCharacterEncoding(CoreConstant.MSG_ENCODING);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/html;charset=" + CoreConstant.MSG_ENCODING);
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
