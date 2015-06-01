package com.huateng.cmupay.jms.business.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmPayNoticeReqVo;
@Service
public class CrmPayNoticeBus extends
AbsCommonBus<CrmMsgVo, CrmMsgVo, CrmPayNoticeReqVo>{
	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	@Autowired
	public void setSendCrmJmsMessage(
			@Qualifier("sendCrmJmsMessage") SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}
	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo,CrmPayNoticeReqVo reqVo,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)throws Exception{
		CrmMsgVo msgVoRtn = new CrmMsgVo();
		try {
			msgVoRtn = sendCrmJmsMessage.sendMsg(msgVo);
		}catch (Exception e) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			throw e;
		} 		
		return msgVoRtn;
	}

}
