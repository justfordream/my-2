package com.huateng.cmupay.jms.business.bank;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendBankJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.toolbox.utils.DateUtil;
/**
 * 
 * @author fan_kui\
 * 银行账号校验BUSSINESS
 *
 */
@Service
public class BankCheckBankBus extends
		AbsCommonBus<BankMsgVo, BankMsgVo, Map<String, Object>> {

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
		logger.debug("BankCheckBank execute(Object,Map,Object,Object) -start,内部交易流水号{}.", txnLog.getIntTxnSeq());
		
		BankMsgVo bankMsgVoRtn = new BankMsgVo();
		try {
			// 发往银行请求
			txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			bankMsgVoRtn = sendBankJmsMessage.sendMsg(msgVo);
			txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		} catch (Exception e) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			throw e;
		} 
		logger.debug("BankCheckBank execute(Object,Map,Object,Object) -end,内部交易流水号{}.", txnLog.getIntTxnSeq());
		return bankMsgVoRtn;
	}
}
