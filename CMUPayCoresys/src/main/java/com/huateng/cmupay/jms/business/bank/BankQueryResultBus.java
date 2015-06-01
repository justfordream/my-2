package com.huateng.cmupay.jms.business.bank;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendBankJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankTransQueryReqVo;

/**
 * @author cmt
 * @version 创建时间：2013-3-11 下午8:37:50 类说明 crm 交易结果查询
 */
@Service("bankQueryResultBus")
public class BankQueryResultBus extends
		AbsCommonBus<BankMsgVo, BankMsgVo, Map<String, String>> {

	// 重发次数
//	private @Value("${retry.num}")
//	Long retryNum;
	private SendBankJmsMessageImpl sendBankJmsMessage;

	@Autowired
	public void setSendBankJmsMessage(
			@Qualifier("sendBankJmsMessage") SendBankJmsMessageImpl sendBankJmsMessage) {
		this.sendBankJmsMessage = sendBankJmsMessage;
	}

	@Override
	public BankMsgVo execute(BankMsgVo msgVo, Map<String, String> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo) throws Exception {
		logger.info("BankQueryResultBus execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start,内部交易流水号{}.", txnLog.getIntTxnSeq());

		BankMsgVo msgVoRtn = new BankMsgVo();
		BankMsgVo msgVoParam = new BankMsgVo();
		BankTransQueryReqVo bodyMsgVo = new BankTransQueryReqVo();
		msgVoParam.setReqSys(ExcConstant.BANK_REQ_SYS);
		msgVoParam.setReqChannel(params.get("cnlTyp"));
		msgVoParam.setReqDate(msgVo.getReqDate());
		msgVoParam.setReqTransID(msgVo.getReqTransID());
		msgVoParam.setReqDateTime(msgVo.getReqDateTime());
		msgVoParam.setActivityCode(CommonConstant.BankTrans.Bank05.toString());
		msgVoParam.setActionCode(CommonConstant.ActionCode.Requset.toString());
		msgVoParam.setRcvSys(params.get("bankId"));
		
		bodyMsgVo.setOriReqSys(msgVoParam.getReqSys());
		bodyMsgVo.setOriReqDate(msgVoParam.getReqDate());
		bodyMsgVo.setOriReqTransID(msgVoParam.getReqTransID());
		msgVoParam.setBody(bodyMsgVo);
		Long retryNum=Long.parseLong(DictCodeCache.getDictCode(DictConst.DictId.RetryTimes.getValue(), 
				DictConst.CodeId.RetryTimes.getValue()).getCodeValue2());
		for (int i = 0; i < retryNum.intValue(); i++) {
			try {
				// bank 交易结果查询请求
				msgVoRtn = sendBankJmsMessage.sendMsg(msgVoParam);
//				MsgHandle.unmarshaller(bodyTransMsgRtn, (String)msgVoRtn.getBody());
//				txnLog.setOuterTranshId(msgVoRtn.getRcvTransID());
//				txnLog.setOuterTranshDt(msgVoRtn.getRcvDate());
//				txnLog.setOuterTranshTm(msgVoRtn.getRcvDateTime());
//				txnLog.setOuterRspCode(msgVoRtn.getRspCode());
//				txnLog.setOuterRspDesc(msgVoRtn.getRspDesc());
////				txnLog.setOuterSubRspCode(msgVoRtn.getRspCode());//此处填写报文体返回码
////				txnLog.setOuterSubRspDesc(msgVoRtn.getRspDesc());
//				txnLog.setOuterSubRspCode(bodyTransMsgRtn.getRspCode());//此处填写报文体返回码
//				txnLog.setOuterSubRspDesc(bodyTransMsgRtn.getRspInfo());
//				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				if (RspCodeConstant.Bank.BANK_020A00.getValue().equals(
						msgVoRtn.getRspCode())) {
					break;
				}

			} catch (Exception e) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				if(i == retryNum){
					throw e;
				}
				
			}
		}
		if (!RspCodeConstant.Bank.BANK_020A00.getValue().equals(
				msgVoRtn.getRspCode())) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
		} else {
			//txnLog.setStatus(CommonConstant.TxnStatus.PaySuccess.toString());
		}
		logger.info("BankQueryResultBus execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end,内部交易流水号{}.", txnLog.getIntTxnSeq());
		return msgVoRtn;
	}

}
