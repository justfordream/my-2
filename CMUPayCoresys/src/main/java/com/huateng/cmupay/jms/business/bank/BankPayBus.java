package com.huateng.cmupay.jms.business.bank;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendBankJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankPayReqVo;
import com.huateng.toolbox.utils.DateUtil;

/**
 * @author cmt
 * @version 创建时间：2013-2-27 下午8:50:03 类说明
 */
@Service
public class BankPayBus extends
		AbsCommonBus<BankMsgVo, BankMsgVo, Map<String, String>> {
	

	private SendBankJmsMessageImpl sendBankJmsMessage;

	@Autowired
	public void setSendBankJmsMessage(
			@Qualifier("sendBankJmsMessage") SendBankJmsMessageImpl sendBankJmsMessage) {
		this.sendBankJmsMessage = sendBankJmsMessage;
	}

	@Override
	public BankMsgVo execute(BankMsgVo msgVo, Map<String, String> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)
			throws Exception {

		logger.debug("BankPayBus execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start,内部交易流水号{}.", txnLog.getIntTxnSeq()); 
		BankMsgVo bankMsgVo = new BankMsgVo();
		
		bankMsgVo.setTransCode(msgVo.getTransCode());
		bankMsgVo.setReqTransID(txnLog.getIntTxnSeq());
		bankMsgVo.setReqDate(txnLog.getIntTxnDate());
		bankMsgVo.setReqDateTime(txnLog.getIntTxnTime());
		bankMsgVo.setReqChannel(params.get("cnlTyp"));
		bankMsgVo.setActivityCode(CommonConstant.BankTrans.Bank03.toString());
		bankMsgVo.setReqSys(ExcConstant.BANK_REQ_SYS);
		bankMsgVo.setActionCode(CommonConstant.ActionCode.Requset.toString());
		bankMsgVo.setRcvSys(params.get("bankId"));
		BankPayReqVo bodyMsgVo = new BankPayReqVo();
		bodyMsgVo.setPayed(params.get("payed"));
		bodyMsgVo.setSubID(params.get("subId"));
		bodyMsgVo.setIDType(params.get("idType"));
		bodyMsgVo.setIDValue(params.get("idValue"));
		bodyMsgVo.setHomeProv(params.get("homeProv"));
		bankMsgVo.setBody(bodyMsgVo);
		txnLog.setOuterActivityCode(CommonConstant.BankTrans.Bank03.toString());
		txnLog.setOuterDomain(params.get("bankId"));
		txnLog.setOuterCnlType(txnLog.getReqCnlType());
		txnLog.setOuterTransId(txnLog.getIntTxnSeq());
		txnLog.setOuterTransDt(txnLog.getIntTxnDate());
		txnLog.setOuterTransTm(txnLog.getIntTxnTime());
//		txnLog.setOuterOprId(txnLog.getIntTxnSeq());
//		txnLog.setOuterOprDt(txnLog.getIntTxnDate());
//		txnLog.setOuterOprTm(txnLog.getIntTxnTime());
		BankMsgVo bankMsgVoRtn = null;
		try {
			// 发往银行进行扣款请求
			txnLog.setOuterStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			bankMsgVoRtn = sendBankJmsMessage.sendMsg(bankMsgVo);
			txnLog.setOuterEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			
			txnLog.setOuterOprId(bankMsgVoRtn.getRcvTransID());
			txnLog.setOuterOprDt(bankMsgVoRtn.getRcvDate());
			txnLog.setOuterOprTm(bankMsgVoRtn.getRcvDateTime());
			
			txnLog.setSettleDate(bankMsgVoRtn.getRcvDate());
			txnLog.setOuterTranshId(bankMsgVoRtn.getRcvTransID());
			txnLog.setOuterTranshDt(bankMsgVoRtn.getRcvDate());
			txnLog.setOuterTranshTm(bankMsgVoRtn.getRcvDateTime());
			txnLog.setOuterRspCode(bankMsgVoRtn.getRspCode());
			txnLog.setOuterRspDesc(bankMsgVoRtn.getRspDesc());
			txnLog.setOuterSubRspCode(bankMsgVoRtn.getRspCode());
			txnLog.setOuterSubRspDesc(bankMsgVoRtn.getRspDesc());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
			
		} catch (Exception e) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			throw e;
		}
		if (!RspCodeConstant.Bank.BANK_020A00.getValue().equals(bankMsgVoRtn.getRspCode())) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
		}else {
			//txnLog.setStatus(CommonConstant.TxnStatus.PaySuccess.toString());
		}
		
		logger.debug("BankPayBus execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end,内部交易流水号{}.", txnLog.getIntTxnSeq());

		return bankMsgVoRtn;
	}

}
