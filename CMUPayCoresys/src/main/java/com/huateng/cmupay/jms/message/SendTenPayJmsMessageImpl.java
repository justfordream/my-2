package com.huateng.cmupay.jms.message;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.caucho.hessian.client.HessianProxyFactory;
import com.huateng.cmupay.constant.CommonConstant.BankOrgCode;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.common.AbsSendJmsMessage;
import com.huateng.cmupay.jms.common.JmsMsgHeader;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.remoting.client.TUpayRemoting;
import com.huateng.cmupay.remoting.client.TenPayRemoting;
import com.huateng.toolbox.json.JacksonUtils;

/**
 * 给财付通发支付结果查询
 * @author Manzhizhen
 *
 */
@Component("sendTenPayJmsMessage")
public class SendTenPayJmsMessageImpl extends AbsSendJmsMessage<Map<String, String>, Object> {
	@Value(value="${tenpay.connectTimeout}")
	protected String tenpayConnectTimeout;
	@Value(value="${tenpay.readTimeout}")
	protected String tenpayReadTimeOut;
	
	@Override
	protected Message putMessageProperties(Message msg, JmsMsgHeader header,
			String context) throws JMSException {
		return null;
	}

	//发往财付通
	@Override
	public Object sendMsg(Map<String, String> paramMap) throws AppBizException {
		logger.debug("SendTenPayJmsMessageImpl sendMsg(Map<String, String> paramMap) - start");

		//获取路由信息
		Map<String, String> paramRoute = new HashMap<String, String>();
		paramRoute.put("orgId", BankOrgCode.TENPAY.getValue());

		UpayCsysRouteInfo routeInfo = findRouteInfo(paramRoute);
		
		String intTxnSeq = (String) paramMap.get("#intTxnSeq");	// 获得内部交易流水
		String orderId = (String) paramMap.get("#orderId");		// 获得订单号

		
		// 除去附加参数
		paramMap.remove("#intTxnSeq");
		paramMap.remove("#orderId");
		
		if (routeInfo == null) {
			logger.warn("财付通接收方交易路由关闭,内部交易流水号:{},订单号:{},接收方机构号:{}.",
					new Object[] { intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			log.warn("财付通接收方交易路由关闭,内部交易流水号:{},订单号:{},接收方机构号:{}.",
					new Object[] { intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			throw new AppBizException(
					RspCodeConstant.Upay.UPAY_U99997.getValue(),
					RspCodeConstant.Upay.UPAY_U99997.getDesc() + "接收方交易路由关闭。");
		}
		
		JmsMsgHeader header = new JmsMsgHeader();
		header.setProtocol(routeInfo.getProtocol());
		header.setReqIp(routeInfo.getReqIp());
		header.setReqPort(routeInfo.getReqPort());
		header.setReqPath(routeInfo.getReqPath());
		header.setRouteInfo(routeInfo.getRouteInfo());
//		header.setReqIp("127.0.0.1");
//		header.setReqPort("8080");
//		header.setReqPath("/CMUPayImitator/ReceiveMsgAutoResponServletForTenQuery.do");
		
		Map<String, String> reapMap = null;
		try {
			logger.info("开始发往财付通,内部交易流水号:{},订单号:{}, 接收方机构号:{}.",
					new Object[] { intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			log.info("开始发往财付通,内部交易流水号:{},订单号:{}, 接收方机构号:{}.",
					new Object[] { intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			
			String headerJson = JacksonUtils.bean2Json(header);
			
			HessianProxyFactory factory = new HessianProxyFactory();
			StringBuffer urlBuffer = new StringBuffer();
			urlBuffer.append(routeInfo.getHostProtocol()).append("://");
			urlBuffer.append(routeInfo.getHostIp()).append(":");
			urlBuffer.append(routeInfo.getHostPort());
			urlBuffer.append(routeInfo.getHostPath());
			String url = urlBuffer.toString();
			factory.setReadTimeout(Long.parseLong(tenpayConnectTimeout));
			factory.setConnectTimeout(Long.parseLong(tenpayReadTimeOut));
			TenPayRemoting remoting = (TenPayRemoting) factory.create(TUpayRemoting.class, url);
			
			logger.debug("核心调用支付前置地址:{}", url);
			
			reapMap = (Map<String, String>) remoting.sendMsg(headerJson, paramMap);
			
			logger.info("接收到财付通的应答,内部交易流水号:{},订单号:{}, 接收方机构号:{}.",
					new Object[] { intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			log.info("接收到财付通的应答,内部交易流水号:{},订单号:{}, 接收方机构号:{}.",
					new Object[] { intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			
			logger.debug("SendTenPayJmsMessageImpl sendMsg(TpayMsgVo) - end");
			
			return reapMap;
			
		} catch (AppRTException e){
			logger.error("财付通 Hessian连接异常:{},内部交易流水号:{},订单号:{}, 接收方机构号:{}.",
					new Object[] { e.getMessage(), intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			log.error("财付通 Hessian连接异常:{},内部交易流水号:{},订单号:{}, 接收方机构号:{}.",
					new Object[] { e.getMessage(), intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			
			throw e;
		}catch (Exception e) {
			logger.error("财付通 Hessian连接异常:{},内部交易流水号:{},订单号:{}, 接收方机构号:{}.",
					new Object[] { e.getMessage(), intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			log.error("财付通 Hessian连接异常:{},内部交易流水号:{},订单号:{}, 接收方机构号:{}.",
					new Object[] { e.getMessage(), intTxnSeq, orderId, BankOrgCode.TENPAY.getValue()});
			
			throw new AppBizException(RspCodeConstant.Upay.UPAY_U99998.getValue(), 
					RspCodeConstant.Upay.UPAY_U99998.getDesc() + " " + e.getMessage());
		}
	}
}
