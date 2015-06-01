package com.huateng.cmupay.jms.message;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.stereotype.Component;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianProxyFactory;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.TUPayConstant;
import com.huateng.cmupay.constant.TUPayConstant.UnPayRspCode;
import com.huateng.cmupay.constant.TUPayConstant.UnionPayMsg;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.common.AbsSendJmsMessage;
import com.huateng.cmupay.jms.common.JmsMsgHeader;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.TpayMsgVo;
import com.huateng.cmupay.remoting.client.TUpayRemoting;
import com.huateng.toolbox.json.JacksonUtils;

@Component("sendtpayJmsMessage")
public class SendTpayJmsMessageImpl extends AbsSendJmsMessage<TpayMsgVo,TpayMsgVo> {

	@Override
	protected Message putMessageProperties(Message msg, JmsMsgHeader header,
			String context) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	//发往银联
	@Override
	public TpayMsgVo sendMsg(TpayMsgVo msgVo) throws AppBizException {
		logger.debug("SendTpayJmsMessageImpl sendMsg(TpayMsgVo) - start");

		//获取路由信息
		Map<String, String> paramRoute = new HashMap<String, String>();
		paramRoute.put("orgId", msgVo.getMsgReceiver());

		UpayCsysRouteInfo routeInfo = findRouteInfo( paramRoute);
		
		String transIDO = msgVo.getTransIDO();
		String orderId = msgVo.getTpayReqData().get(TUPayConstant.UnionPayMsg.ORDERID.getValue());
		String msgReveiver = msgVo.getMsgReceiver();
		
		if (routeInfo == null) {
			logger.warn("接收方交易路由关闭,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.",
					new Object[] { transIDO, orderId,msgReveiver });
			log.warn("接收方交易路由关闭,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.", 
					new Object[] {transIDO, orderId,msgReveiver });
			throw new AppBizException(
					RspCodeConstant.Upay.UPAY_U99997.getValue(),
					RspCodeConstant.Upay.UPAY_U99997.getDesc() + "接收方交易路由关闭。");
		}
		
		//https://127.0.0.1:8081/gateway/api/backTransRequest.do
		JmsMsgHeader header = new JmsMsgHeader();
		header.setProtocol(routeInfo.getProtocol());
		header.setReqIp(routeInfo.getReqIp());
		header.setReqPort(routeInfo.getReqPort());
		header.setReqPath(routeInfo.getReqPath() + msgVo.getReqPathAppend());
		header.setRouteInfo(routeInfo.getRouteInfo());
		/*header.setReqIp("127.0.0.1");
		header.setReqPort("8080");
		header.setReqPath("/CMUPayImitator/ReceiveMsgAutoResponServletForRefund.do");*/
		
		TpayMsgVo tpayMsgVoRtn = new TpayMsgVo();
		Map<String,String> tpayRspData = new HashMap<String, String>();
		
		try {
			logger.info("发往银联,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.",
					new Object[] { transIDO, orderId,msgReveiver });
			log.info("发往银联,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.", 
					new Object[] {transIDO, orderId,msgReveiver });
			
			String headerJson = JacksonUtils.bean2Json(header);
			
			HessianProxyFactory factory = new HessianProxyFactory();
			TUpayRemoting remoting = null;
			StringBuffer urlBuffer = new StringBuffer();
			urlBuffer.append(routeInfo.getHostProtocol()).append("://");
			urlBuffer.append(routeInfo.getHostIp()).append(":");
			urlBuffer.append(routeInfo.getHostPort());
			urlBuffer.append(routeInfo.getHostPath());
			String url = urlBuffer.toString();
			factory.setReadTimeout(Long.parseLong(this.readTimeOut));
			factory.setConnectTimeout(Long.parseLong(this.connectTimeout));
			//url = "http://localhost:8080/CMUPayThirdFront/remote/TUpayRemoting";
			remoting = (TUpayRemoting) factory.create(TUpayRemoting.class, url);
			
			logger.debug("核心调用支付前置地址:{}",url);
			
			tpayRspData = remoting.sendMsg(headerJson, msgVo.getTpayReqData());
			
			if(null == tpayRspData || tpayRspData.size() == 0){
				logger.error("银联交易超时,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.",
						new Object[] { transIDO, orderId,msgReveiver });
				log.error("银联交易超时,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.", 
						new Object[] {transIDO, orderId,msgReveiver });
				
				tpayRspData.put(UnionPayMsg.RESPCODE.getValue(), RspCodeConstant.Upay.UPAY_U99998.getValue());
				tpayRspData.put(UnionPayMsg.RESPMSG.getValue(), RspCodeConstant.Upay.UPAY_U99998.getDesc());
				tpayMsgVoRtn.setTpayRspData(tpayRspData);
				
				return tpayMsgVoRtn;
			}
			tpayMsgVoRtn.setTpayRspData(tpayRspData);
			
		} catch (RemoteProxyFailureException e){
			logger.error("银联Hessian 连接异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.",
					new Object[] { transIDO, orderId,msgReveiver });
			log.error("银联Hessian 连接异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.", 
					new Object[] {transIDO, orderId,msgReveiver });
			
			tpayRspData.put(UnionPayMsg.RESPCODE.getValue(), RspCodeConstant.Upay.UPAY_U99998.getValue());
			tpayRspData.put(UnionPayMsg.RESPMSG.getValue(), RspCodeConstant.Upay.UPAY_U99998.getDesc());
			tpayMsgVoRtn.setTpayRspData(tpayRspData);
			
			return tpayMsgVoRtn;
		} catch (RemoteConnectFailureException e){
			logger.error("银联Hessian 连接异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.",
					new Object[] { transIDO, orderId,msgReveiver });
			log.error("银联Hessian 连接异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.", 
					new Object[] {transIDO, orderId,msgReveiver });
			
			tpayRspData.put(UnionPayMsg.RESPCODE.getValue(), RspCodeConstant.Upay.UPAY_U99998.getValue());
			tpayRspData.put(UnionPayMsg.RESPMSG.getValue(), RspCodeConstant.Upay.UPAY_U99998.getDesc());
			tpayMsgVoRtn.setTpayRspData(tpayRspData);
			
			return tpayMsgVoRtn;
		} catch (HessianConnectionException e){
			logger.error("银联Hessian 连接异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.",
					new Object[] { transIDO, orderId,msgReveiver });
			log.error("银联Hessian 连接异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.", 
					new Object[] {transIDO, orderId,msgReveiver });
			
			tpayRspData.put(UnionPayMsg.RESPCODE.getValue(), RspCodeConstant.Upay.UPAY_U99998.getValue());
			tpayRspData.put(UnionPayMsg.RESPMSG.getValue(), RspCodeConstant.Upay.UPAY_U99998.getDesc());
			tpayMsgVoRtn.setTpayRspData(tpayRspData);
			
			return tpayMsgVoRtn;
		} catch (AppRTException e){
			logger.error("发往银联运行异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.",
					new Object[] { transIDO, orderId,msgReveiver });
			logger.error("发往银联运行异常",e);
			log.error("发往银联运行异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.", 
					new Object[] {transIDO, orderId,msgReveiver });
			throw e;
		}catch (Exception e) {
			logger.error("发往银联未知异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.",
					new Object[] { transIDO, orderId,msgReveiver });
			logger.error("发往银联未知异常",e);
			log.error("发往银联未知异常,发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{}.", 
					new Object[] {transIDO, orderId,msgReveiver });
			
			throw new AppBizException(RspCodeConstant.Upay.UPAY_U99999.getValue(), e);
		}

		//银联应答成功
		if (UnPayRspCode.UNPAY_00.getValue().equals(
				tpayMsgVoRtn.getTpayRspData().get(UnionPayMsg.RESPCODE.getValue()))) {
			log.succ("银联返回成功，发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{},应答码:{} ",
					new Object[] { transIDO, orderId,msgReveiver,
					tpayMsgVoRtn.getTpayRspData().get(UnionPayMsg.RESPCODE.getValue())});
		} else {
			log.error("银联返回失败，发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{},应答码:{} ",
					new Object[] { transIDO, orderId,msgReveiver,
					tpayMsgVoRtn.getTpayRspData().get(UnionPayMsg.RESPCODE.getValue())});
		}
		
		log.info("银联返回成功，发往银联流水号:{},商户订单号(OrderId):{},接收方机构号:{},应答码:{} ",
				new Object[] { transIDO, orderId,msgReveiver,
				tpayMsgVoRtn.getTpayRspData().get(UnionPayMsg.RESPCODE.getValue())});
		
		logger.debug("SendTpayJmsMessageImpl sendMsg(TpayMsgVo) - end");
		return tpayMsgVoRtn;
	}


}
