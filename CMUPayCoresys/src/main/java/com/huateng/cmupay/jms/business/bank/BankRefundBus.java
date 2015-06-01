package com.huateng.cmupay.jms.business.bank;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendBankJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;

/**
 * 
 * @author hdm
 *
 */
@Service
public class BankRefundBus extends AbsCommonBus<BankMsgVo, BankMsgVo, Map<String, Object>>{
	

//	@Autowired
//	private IUpayCsysTxnLogService upayCsysTxnLogService;
	
	private SendBankJmsMessageImpl sendBankJmsMessage;

	@Autowired
	public void setSendCrmJmsMessage(@Qualifier("sendBankJmsMessage")SendBankJmsMessageImpl sendBankJmsMessage) {
		this.sendBankJmsMessage = sendBankJmsMessage;
	}
	
//	public void setUpayCsysTxnLogService(IUpayCsysTxnLogService upayCsysTxnLogService) {
//		this.upayCsysTxnLogService = upayCsysTxnLogService;
//	}

	@Override
	public BankMsgVo execute(BankMsgVo msgVo, Map<String, Object> params, UpayCsysTxnLog txnLog,UpayCsysBindInfo bindInfo) throws Exception  {
		
		logger.info("CrmReverseBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start,内部交易流水号{}.", txnLog.getIntTxnSeq());
		
		BankMsgVo msgVoRtn = new BankMsgVo();

		try {
			//发往银行进行退费
			msgVoRtn = sendBankJmsMessage.sendMsg(msgVo);
		} catch (Exception e) {
			throw e;
		}
		logger.info("CrmReverseBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) success - end,内部交易流水号{}.", txnLog.getIntTxnSeq());
		return msgVoRtn;
	}
}

