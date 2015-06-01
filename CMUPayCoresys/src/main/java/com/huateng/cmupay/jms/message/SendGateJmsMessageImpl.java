package com.huateng.cmupay.jms.message;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.stereotype.Component;

import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.common.AbsSendJmsMessage;
import com.huateng.cmupay.jms.common.JmsMsgHeader;


@Component("sendGateJmsMessage")
public class SendGateJmsMessageImpl  extends AbsSendJmsMessage<Object, Object>{

	
	
//	private final String SIGN_TYPE = "1";
//	private final String PAY_TYPE = "2";
//	@Autowired
//	private GateRemoting gateRemoting;

	public Object sendMsg(Object msgVo) throws AppBizException {
		logger.debug("start 发送报文到支付网关");
//		String headerString = "";
//		String jsonMsg = "";
//		if (msgVo instanceof CorePayResultNoticeReq) {
//			jsonMsg = JacksonUtils.bean2Json((CorePayResultNoticeReq) msgVo);
//			headerString = PAY_TYPE;
//		} else if (msgVo instanceof CoreResultSignRes) {
//			jsonMsg = JacksonUtils.bean2Json((CoreResultSignRes) msgVo);
//			headerString = SIGN_TYPE;
//		}
//		try {
//			logger.info("{}->GATE: {}",new Object[]{headerString,jsonMsg} );
//			String msg = gateRemoting.sendMsg(headerString, jsonMsg);
//			logger.info("{}->GATE: 网关接受成功。{}",new Object[]{headerString,msg});
//		} catch (RemoteProxyFailureException e) {
//			logger.error("GATE Hessian RemoteProxyFailureException", e);
//			AppBizException e1 = new AppBizException(RspCodeConstant.Upay.UPAY_U99997.getValue(), e);
//			throw e1;
//		} catch (RemoteConnectFailureException e) {
//			logger.error("GATE Hessian RemoteConnectFailureException", e);
//			AppBizException e1 = new AppBizException(RspCodeConstant.Upay.UPAY_U99997.getValue(), e);
//			throw e1;
//		} catch (HessianConnectionException e) {
//			logger.error("GATE Hessian  HessianConnectionException", e);
//			AppBizException e1 = new AppBizException(RspCodeConstant.Upay.UPAY_U99997.getValue(), e);
//			throw e1;
//		} catch (AppRTException e) {
//			logger.error("GATE hessian AppRTException", e);
//			AppBizException e1 = new AppBizException(RspCodeConstant.Upay.UPAY_U99999.getValue(), e);
//			throw e1;
//		} catch (Exception e) {
//			logger.error("GATE Hessian Exception", e);
//			AppBizException e1 = new AppBizException(RspCodeConstant.Upay.UPAY_U99999.getValue(), e);
//			throw e1;
//		}
		logger.debug("end 发送报文到支付网关");
		return null;
	}



	@Override
	protected Message putMessageProperties(Message msg, JmsMsgHeader header,
			String context) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

}
