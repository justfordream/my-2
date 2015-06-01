package com.huateng.cmupay.hessian.message;

import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.controller.cache.RouteCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysRouteInfoService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.logFormat.MessageLogger;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.toolbox.utils.StringUtil;

/**
 * @author cmt
 * 
 * @param <T>
 * @param <R>
 */
public abstract class AbsSendHessianMessage<T, R> implements ISendHessianMessage {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected MessageLogger log = MessageLogger.getLogger(this.getClass());
//	private JmsTemplate template;
//	private Destination destination;
//	private Destination respDest;
//
//	protected Map<String, Destination> destinationMap;
//	protected Map<String, Destination> respDestMap;

	@Autowired
	protected IUpayCsysRouteInfoService upayCsysRouteInfoService;

	/**
	 * 查询路由信息
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	protected UpayCsysRouteInfo findRouteInfo(Map<String, String> param)
			throws AppBizException {

		logger.debug("AbsSendJmsMessage findRouteInfo(Map<String,String>) - Start");

		String orgId = param.get("orgId");

		if ("".equals(StringUtil.toTrim(orgId))) {

			return null;
		}
		UpayCsysRouteInfo routeInfo = RouteCache.getRouteInfo(orgId);
		if (routeInfo == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orgId", orgId);
			params.put("isHistory", CommonConstant.IsHistory.Normal.toString());
			params.put("status", CommonConstant.IsActive.True.toString());
			routeInfo = upayCsysRouteInfoService.findObj(params);
		}

		logger.debug("AbsSendJmsMessage findRouteInfo(Map<String,String>) - end");
		return routeInfo;

	}

//	@Override
//	public String sendMsg(HessianMsgHeader hessianMsgHeader, String jmsBody)
//			throws AppBizException {
//
//		logger.debug("AbsSendJmsMessage sendMsg(JmsMsgHeader, String) - start");
//
//		// destination = destinationMap.get(routeInfo.getReqMqId());
//		// respDest = respDestMap.get(routeInfo.getReqMqId());
//		if (destination == null) {
//			throw new AppBizException(MessageHandler.getUpayErrCode("U99999"), "发送队列名为 null!");
//		}
//		if (respDest == null) {
//			throw new AppBizException(MessageHandler.getUpayErrCode("U99999"), "返回队列名为 null!");
//		}
//		if (jmsBody == null) {
//			throw new AppBizException(MessageHandler.getUpayErrCode("U99999"), "发送请求的消息体为null!");
//		}
//
//		final String context = jmsBody;
//		//logger.info("发送前置报文是："+context);
//		final HessianMsgHeader header =  hessianMsgHeader;
//		
//		logger.info("=============成功向 {}发送了一条JMS消息,mqSeq_context:{}",
//				destination.toString(), header.getMqSeq() + "_" + context);
//		// 添加与虚拟账户发送消息流水
//
//		Message msg = template.receiveSelected(respDest,
//				"reqTxnSeq='" + header.getMqSeq() + "'");
//		if (msg != null) {
//			//logger.info("=============成功从{}收到了一条JMS消息,mqSeq_returnMsg:{}", respDest.toString(), header.getMqSeq() + "_" + msg);
//			// 把接收到的JMS消息更新到消息流水
//
//			String respStr = null;
//			try {
//				respStr = ((TextMessage) msg).getText();
//				logger.info("=============成功从{}收到了一条JMS消息,消息内容为:{}",
//						respDest.toString(), respStr);
//
//			} catch (JMSException e) {
//				logger.error("sendMsg(JmsMsgHeader, String)", e); //$NON-NLS-1$
//
//				throw new AppBizException(MessageHandler.getUpayErrCode("U99999"), e);
//			}
//
//			logger.debug("AbsSendJmsMessage sendMsg(JmsMsgHeader, String) - end");
//			return respStr;
//		} else {
//			
//			logger.error("=============接受消息超时了! mqSeq :{}", header.getMqSeq());
//			throw new AppBizException(MessageHandler.getUpayErrCode("U99998"), "接收消息超时!");
//		}
//	}

//	protected abstract Message putMessageProperties(Message msg,
//			HessianMsgHeader header, String context) throws JMSException;
	
	public abstract R sendMsg(T msgVo) throws AppBizException;

//	public JmsTemplate getTemplate() {
//		return template;
//	}
//
//	public void setTemplate(JmsTemplate template) {
//		this.template = template;
//	}
//
//	public Destination getDestination() {
//		return destination;
//	}
//
//	public void setDestination(Destination destination) {
//		this.destination = destination;
//	}
//
//	public Destination getRespDest() {
//		return respDest;
//	}
//
//	public Map<String, Destination> getDestinationMap() {
//		return destinationMap;
//	}
//
//	public void setDestinationMap(Map<String, Destination> destinationMap) {
//		this.destinationMap = destinationMap;
//	}
//
//	public Map<String, Destination> getRespDestMap() {
//		return respDestMap;
//	}
//
//	public void setRespDestMap(Map<String, Destination> respDestMap) {
//		this.respDestMap = respDestMap;
//	}
//
//	public void setRespDest(Destination respDest) {
//		this.respDest = respDest;
//	}

	public IUpayCsysRouteInfoService getUpayCsysRouteInfoService() {
		return upayCsysRouteInfoService;
	}

	public void setUpayCsysRouteInfoService(
			IUpayCsysRouteInfoService upayCsysRouteInfoService) {
		this.upayCsysRouteInfoService = upayCsysRouteInfoService;
	}

	

}
