package com.huateng.cmupay.jms.business.crm;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.toolbox.utils.DateUtil;

@Service
public class CrmCompareUserInfoBus extends
		AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>> {
	

	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	@Autowired
	public void setSendCrmJmsMessage(
			@Qualifier("sendCrmJmsMessage") SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}

	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)
			throws Exception {
		logger.debug("CompareUserInfoBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start");
		CrmMsgVo msgVoRtn = new CrmMsgVo();
		try {
			
			txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			msgVoRtn = sendCrmJmsMessage.sendMsg(msgVo);
			txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		} catch (Exception e) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			throw e;
		}
		logger.debug("CompareUserInfoBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		return msgVoRtn;
	}

}
