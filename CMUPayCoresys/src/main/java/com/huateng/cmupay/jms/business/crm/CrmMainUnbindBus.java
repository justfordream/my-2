package com.huateng.cmupay.jms.business.crm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;

/**
 * crm 主号码解约
 * 
 * @author zeng.j
 * 
 */
@Service("crmMainUnbindBus")
public class CrmMainUnbindBus extends
		AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>> {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)
			throws Exception {
		logger.debug("CrmMainUnbindBus execute(Object , Map ,Object , Object ) - start");
		msgVo = sendCrmJmsMessage.sendMsg(msgVo);
		logger.debug("CrmMainUnbindBus execute(Object , Map ,Object , Object ) - end");
		return msgVo;
	}

	public SendCrmJmsMessageImpl getSendCrmJmsMessage() {
		return sendCrmJmsMessage;
	}

	public void setSendCrmJmsMessage(SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}

}
