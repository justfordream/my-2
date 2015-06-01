package com.huateng.cmupay.jms.business.bank;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendBankJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankUserCousumeQueryReqVo;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:11:29 
 * 类说明 
 */
@Service
public class BankUserCousumeQueryBus extends AbsCommonBus<BankMsgVo,BankMsgVo,Map<String,String>>{
	
	
	
	private SendBankJmsMessageImpl sendBankJmsMessage;
	
	
	@Override
	public BankMsgVo execute(BankMsgVo msgVo,Map<String,String> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)
			throws AppBizException {
	
		logger.info("BankUserCousumeQueryBus execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start,内部交易流水号{}.", txnLog.getIntTxnSeq()); 
		
		
		msgVo.setReqChannel(params.get("cnlTyp"));
		msgVo.setActivityCode(CommonConstant.BankTrans.Bank05.toString());
		msgVo.setReqSys(ExcConstant.BANK_REQ_SYS);
		msgVo.setRcvSys(params.get("bankId"));
		msgVo.setActionCode(CommonConstant.ActionCode.Requset.toString());
//		msgVo.setReqDate(msgVo.getTxnDate());
//		msgVo.setReqDateTime(msgVo.getTxnTime());
		String  bankTransID =	msgVo.getReqTransID();
		msgVo.setReqTransID(bankTransID);
		

		BankUserCousumeQueryReqVo  bodyReqVo= new BankUserCousumeQueryReqVo();
		bodyReqVo.setActionDate(msgVo.getReqDate());
		bodyReqVo.setOriReqSys(msgVo.getReqSys());
		bodyReqVo.setTransactionID(msgVo.getReqTransID());
		msgVo.setBody(bodyReqVo);
		
	
		BankMsgVo bankMsgVoRtn = new BankMsgVo();
		try {
			// 发往银行进行扣款请求
			bankMsgVoRtn = sendBankJmsMessage.sendMsg(msgVo);

		} catch (AppBizException e) {
			throw e;

		} catch (RuntimeException e){
			throw e;
		}
		if (!"020A00".equals(bankMsgVoRtn.getRspCode())) {
			
		}

		
		logger.info("BankUserCousumeQueryBus execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end,内部交易流水号{}.", txnLog.getIntTxnSeq()); 
		
		return bankMsgVoRtn;
	

	}





	public SendBankJmsMessageImpl getSendBankJmsMessage() {
		return sendBankJmsMessage;
	}


	public void setSendBankJmsMessage(SendBankJmsMessageImpl sendBankJmsMessage) {
		this.sendBankJmsMessage = sendBankJmsMessage;
	}
	
	
	
	
	
	
}


