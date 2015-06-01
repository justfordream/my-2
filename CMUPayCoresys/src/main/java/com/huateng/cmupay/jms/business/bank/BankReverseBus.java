package com.huateng.cmupay.jms.business.bank;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendBankJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankReverseMsgReqVo;
import com.huateng.toolbox.utils.DateUtil;

/**
 * @author cmt
 * @version 创建时间：2013-2-28 上午1:45:24 类说明
 */
@Service
public class BankReverseBus extends
		AbsCommonBus<BankMsgVo, BankMsgVo, Map<String, String>> {

	// 重发次数
//	private @Value("${retry.num}")
//	Long retryNum;

	private SendBankJmsMessageImpl sendBankJmsMessage;
	
	@Autowired
	protected IUpayCsysTxnLogService upayCsysTxnLogService;

	@Autowired
	public void setSendBankJmsMessage(
			@Qualifier("sendBankJmsMessage") SendBankJmsMessageImpl sendBankJmsMessage) {
		this.sendBankJmsMessage = sendBankJmsMessage;
	}

	@Override
	public BankMsgVo execute(BankMsgVo msgVo, Map<String, String> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo) throws Exception {

		logger.info("BankReverseBus execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start,内部交易流水号{}.", txnLog.getIntTxnSeq());

		BankMsgVo msgVoRevert = new BankMsgVo();
		BankMsgVo msgVoRevertRtn = new BankMsgVo();
		msgVoRevert.setActivityCode(CommonConstant.BankTrans.Bank06.toString());
		msgVoRevert.setReqSys(ExcConstant.BANK_REQ_SYS);
		msgVoRevert.setReqChannel(null == msgVo.getReqChannel() ? params
				.get("cnlTyp") : msgVo.getReqChannel());
		msgVoRevert.setReqDate(msgVo.getReqDate());
		String bankTransID =msgVo.getReqTransID();
		msgVoRevert.setReqTransID(bankTransID);
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
				txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				msgVoRevertRtn = sendBankJmsMessage.sendMsg(msgVoRevert);
				txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
//				if (resCode.contains(msgVoRevertRtn.getRspCode())) {
//					break;
//				}

			} catch (Exception e) {
				logger.error("冲正异常", e);
				txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				if(i == retryNum){
					throw e;
//				}
			}
//		}
		if (!RspCodeConstant.Bank.BANK_020A00.getValue().equals(
				msgVoRevertRtn.getRspCode())) {
			txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());

		} else {
//			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
//					.toString());
			txnLog.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());
		}

		logger.info("BankReverseBus execute(BankMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end,内部交易流水号{}.", txnLog.getIntTxnSeq());

		return msgVoRevertRtn;
	}

}
