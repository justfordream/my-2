package com.huateng.cmupay.jms.business.bank;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendBankJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;

/**
 * 银行解约
 * 
 * @author zeng.j
 * 
 */
@Service("bankUnbindBus")
public class BankUnbindBus extends
		AbsCommonBus<BankMsgVo, BankMsgVo, Map<String, Object>> {
	@Autowired
	private SendBankJmsMessageImpl sendBankJmsMessage;

	@Override
	public BankMsgVo execute(BankMsgVo msgVo, Map<String, Object> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo) throws Exception {
		logger.debug("BankUnbindBus execute(Ojbect,Map,Object,Object) - start,内部交易流水号{}.", txnLog.getIntTxnSeq());
		// 发往银行进解约请求
		msgVo = sendBankJmsMessage.sendMsg(msgVo);
		logger.debug("BankUnbindBus execute(Ojbect,Map,Object,Object) - end,内部交易流水号{}.", txnLog.getIntTxnSeq());
		return msgVo;
	}

	public SendBankJmsMessageImpl getSendBankJmsMessage() {
		return sendBankJmsMessage;
	}

	public void setSendBankJmsMessage(SendBankJmsMessageImpl sendBankJmsMessage) {
		this.sendBankJmsMessage = sendBankJmsMessage;
	}

}
