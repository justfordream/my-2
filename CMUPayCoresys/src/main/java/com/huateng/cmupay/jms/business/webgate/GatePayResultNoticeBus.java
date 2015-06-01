package com.huateng.cmupay.jms.business.webgate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.message.SendGateJmsMessageImpl;

@Service("gatePayResultNoticeBus")
public class GatePayResultNoticeBus {
	
	
	
	@Autowired
	private SendGateJmsMessageImpl sendGateJmsMessage;
	
	
	public void setSendGateJmsMessage(@Qualifier SendGateJmsMessageImpl sendGateJmsMessage) {
		this.sendGateJmsMessage = sendGateJmsMessage;
	}

	public Object execute(Object msgVo)
			throws AppBizException {
		sendGateJmsMessage.sendMsg(msgVo);
		return null;
	}
}
