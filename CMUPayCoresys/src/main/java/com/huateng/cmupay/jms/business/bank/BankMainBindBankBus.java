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
import com.huateng.toolbox.utils.DateUtil;

/**
 * @author fan_kui
 * 移动发起主号签约
 */
@Service
public class BankMainBindBankBus extends
		AbsCommonBus<BankMsgVo, BankMsgVo, Map<String, Object>> {

	@Autowired
	private SendBankJmsMessageImpl sendBankJmsMessage;

	@Autowired
	public void setSendBankJmsMessage(
			@Qualifier("sendBankJmsMessage") SendBankJmsMessageImpl sendBankJmsMessage) {
		this.sendBankJmsMessage = sendBankJmsMessage;
	}

	@Override
	public BankMsgVo execute(BankMsgVo msgVo, Map<String, Object> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)
			throws Exception {
		logger.debug("BankMainBindBank execute(Object,Map,Object,Object) -start,内部交易流水号{}.", txnLog.getIntTxnSeq());
		BankMsgVo bankMsgVoRtn = new BankMsgVo();
		try {
			txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			bankMsgVoRtn = sendBankJmsMessage.sendMsg(msgVo);
			txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		} catch (Exception e) {
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			throw e;
		}
		logger.debug("BankMainBindBank execute(Object,Map,Object,Object) -end,内部交易流水号{}.", txnLog.getIntTxnSeq());
		return bankMsgVoRtn;
	}
}
