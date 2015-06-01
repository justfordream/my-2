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
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmPrintInvoinceReqVo;
import com.huateng.toolbox.utils.DateUtil;

/**
 * 
 * @author fan_kui
 *	发票打印接口发送移动
 */
@Service
public class CrmPrintInvoinceBus extends
AbsCommonBus<CrmMsgVo, CrmMsgVo, CrmPrintInvoinceReqVo>{
	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	@Autowired
	public void setSendCrmJmsMessage(
			@Qualifier("sendCrmJmsMessage") SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}
	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo,CrmPrintInvoinceReqVo reqVo,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)throws Exception{		
		logger.debug("CrmPrintInvoinceBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start");
		CrmMsgVo msgVoRtn = new CrmMsgVo();
		try {
			
			
			txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			msgVoRtn = sendCrmJmsMessage.sendMsg(msgVo);
			txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		}catch (Exception e) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());

			throw e;
		} 
		logger.debug("CrmPrintInvoinceBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		return msgVoRtn;
	}

}
