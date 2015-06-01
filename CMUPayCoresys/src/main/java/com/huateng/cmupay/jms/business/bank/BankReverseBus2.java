package com.huateng.cmupay.jms.business.bank;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.service.system.IUpayCsysBatCutCtlService;
import com.huateng.cmupay.controller.service.system.IUpayCsysSeqMapInfoService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendBankJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankReverseMsgReqVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;

/**
 * @author cmt
 * @version 创建时间：2013-2-28 上午1:45:24 类说明
 */
@Service
public class BankReverseBus2 extends
		AbsCommonBus<BankMsgVo, BankMsgVo, Map<String, String>> {

	// 重发次数
//	private @Value("${retry.num}")
//	Long retryNum;

	private SendBankJmsMessageImpl sendBankJmsMessage;
	
	@Autowired
	protected IUpayCsysTxnLogService uPayCsysTxnLogService;

	@Autowired
	protected IUpayCsysSeqMapInfoService upayCsysSeqMapInfoService;
	
	@Autowired
	protected	IUpayCsysBatCutCtlService uPayCsysBatCutCtlService;
	
	@Autowired
	public void setSendBankJmsMessage(
			@Qualifier("sendBankJmsMessage") SendBankJmsMessageImpl sendBankJmsMessage) {
		this.sendBankJmsMessage = sendBankJmsMessage;
	}

	@Override
	public BankMsgVo execute(BankMsgVo msgVo, Map<String, String> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo) throws Exception {

		logger.info("BankReverseBus2 execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start,内部交易流水号{}.", txnLog.getIntTxnSeq());
		UpayCsysTxnLog txnLog1 = new UpayCsysTxnLog();
		
		// 生成预处理订单
		Long seqValue = upayCsysSeqMapInfoService.selectSeqValue(ExcConstant.TXN_LOG_SEQ);
//		String intTxnDate = uPayCsysBatCutCtlService
//				.findCutOffDate(ExcConstant.CUT_OFF_DATE);
		String intTxnSeq = Serial.genSerialNo(CommonConstant.Sequence.IntSeq.toString());
//		String intTxnTime = DateUtil.getDateyyyyMMddHHmmss();
//		txnLog1.setSeqId(seqValue);
//		txnLog1.setIntTxnSeq(intTxnSeq);
////		txnLog1.setIntTransCode("12300020");
//		txnLog1.setIntTxnDate(msgVo.getReqDate());
//		txnLog1.setIntTxnTime(msgVo.getReqDateTime());
////		txnLog1.setIntMqSeq("");
////		txnLog1.setBussType("02");
////		txnLog1.setBussChl("01");
////		txnLog1.setPayMode("01");
//		txnLog1.setMainFlag(CommonConstant.SpeSymbol.SPACE.toString());
//		txnLog1.setRcvActivityCode(CommonConstant.BankTrans.Bank06.toString());
////		txnLog1.setReqDomain("0001");
//		txnLog1.setReqTransId(intTxnSeq);
//		txnLog1.setRcvDomain(params.get("bankId"));
////		txnLog1.setRcvRouteType("01");
//		txnLog1.setRcvRouteVal(txnLog.getIdValue());
//		txnLog1.setRcvSessionId(intTxnSeq);
//		txnLog1.setRcvTransId(intTxnSeq);
//		txnLog1.setRcvTransDt(msgVo.getReqDate());
//		txnLog1.setRcvTransTm(msgVo.getReqDateTime());
////		txnLog1.setRcvOprId(intTxnSeq);
////		txnLog1.setRcvOprDt(intTxnDate);
////		txnLog1.setRcvOprTm(intTxnTime+"000");
//		txnLog1.setRcvCnlType(" ");
//		txnLog1.setIdValue(txnLog.getIdValue());
//		txnLog1.setPayAmt(Long.valueOf(txnLog.getPayAmt()));
//		txnLog1.setBankId(params.get("bankId"));
//		txnLog1.setBankAccId("");
//		txnLog1.setBankAcctType("");
//		txnLog1.setPayedType(" ");
//		
//		txnLog1.setOriOprTransId(params.get("reverseTransId"));
//		txnLog1.setOriOrgId(params.get("reverseOriReqSys"));
//		txnLog1.setOriReqDate(params.get("reverseTransDt"));
//		
//		txnLog1.setBackFlag(CommonConstant.YesOrNo.No.toString());
//		txnLog1.setRefundFlag(CommonConstant.YesOrNo.No.toString());
//		txnLog1.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());
//		txnLog1.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
//		txnLog1.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
//		uPayCsysTxnLogService.add(txnLog1);
//		
		BankMsgVo msgVoRevert = new BankMsgVo();
		BankMsgVo msgVoRevertRtn = new BankMsgVo();
		msgVoRevert.setActivityCode(CommonConstant.BankTrans.Bank06.toString());
		msgVoRevert.setReqSys(ExcConstant.BANK_REQ_SYS);
		msgVoRevert.setReqChannel(null == msgVo.getReqChannel() ? params
				.get("cnlTyp") : msgVo.getReqChannel());
		
//		String bankTransID = Serial
//				.genSerialNo(CommonConstant.Sequence.BankTransID.toString());
		msgVoRevert.setReqTransID(intTxnSeq);
		msgVoRevert.setReqDate(msgVo.getReqDate());
		msgVoRevert.setReqDateTime(msgVo.getReqDateTime());
		msgVoRevert.setActionCode(CommonConstant.ActionCode.Requset.toString());
		msgVoRevert.setRcvSys(null == msgVo.getRcvSys() ? params.get("bankId")
				: msgVo.getRcvSys());

		BankReverseMsgReqVo revertReqVo = new BankReverseMsgReqVo();
		revertReqVo.setOriReqDate(params.get("reverseTransDt"));
		revertReqVo.setOriReqSys(params.get("reverseOriReqSys"));
		revertReqVo.setOriReqTransID(params.get("reverseTransId"));
		msgVoRevert.setBody(revertReqVo);
//		retryNum=1L;
//		for (int i = 0; i < retryNum.intValue(); i++) {
			try {
//				List<String> resCode = Arrays.asList(ExcConstant.BANK_REVERT_RES);

				// 超时进行冲正处理
				msgVoRevertRtn = sendBankJmsMessage.sendMsg(msgVoRevert);
				txnLog1.setRcvTranshId(msgVoRevertRtn.getRcvTransID());
				txnLog1.setRcvTranshDt(msgVoRevertRtn.getRcvDate());
				txnLog1.setRcvTranshTm(msgVoRevertRtn.getRcvDateTime());
				txnLog1.setRcvRspCode(msgVoRevertRtn.getRspCode());
				txnLog1.setRcvRspDesc(msgVoRevertRtn.getRspDesc());
				txnLog1.setRcvSubRspCode(msgVoRevertRtn.getRspCode());
				txnLog1.setRcvSubRspDesc(msgVoRevertRtn.getRspDesc());
				txnLog1.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
//				if (resCode.contains(msgVoRevertRtn.getRspCode())) {
//					break;
//				}

			} catch (Exception e) {
				logger.error("冲正异常", e);
				txnLog1.setReverseFlag(CommonConstant.YesOrNo.No.toString());
				txnLog1.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				if(i == retryNum){
					uPayCsysTxnLogService.modify(txnLog1);
					throw e;
//				}
			}
//		}
		if (!RspCodeConstant.Bank.BANK_020A00.getValue().equals(
				msgVoRevertRtn.getRspCode())) {
			txnLog1.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			txnLog1.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			uPayCsysTxnLogService.modify(txnLog1);
		} else {
			//txnLog1.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
			txnLog1.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());
			uPayCsysTxnLogService.modify(txnLog1);
		}

		logger.info("BankReverseBus2 execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end,内部交易流水号{}.", txnLog.getIntTxnSeq());

		return msgVoRevertRtn;
	}

}
