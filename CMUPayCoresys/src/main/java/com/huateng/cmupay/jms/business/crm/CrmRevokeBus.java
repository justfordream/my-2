package com.huateng.cmupay.jms.business.crm;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;



/**
 * 银行向Crm发起的撤单请求
 * 银行运维人员发起移动用户某笔缴费交易的撤单操作流程，前提条件是原交易必须存在，且已对账完成。
 * @author hdm
 *
 */

@Service("crmRevokeBus")
public class CrmRevokeBus extends AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>>{

	@Autowired
	private IUpayCsysTxnLogService upayCsysTxnLogService;
	
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	
	public IUpayCsysTxnLogService getUpayCsysTxnLogService() {
		return upayCsysTxnLogService;
	}
	public void setUpayCsysTxnLogService(IUpayCsysTxnLogService upayCsysTxnLogService) {
		this.upayCsysTxnLogService = upayCsysTxnLogService;
	}

	public SendCrmJmsMessageImpl getSendCrmJmsMessage() {
		return sendCrmJmsMessage;
	}

	public void setSendCrmJmsMessage(@Qualifier("sendCrmJmsMessage")SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}

	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params, UpayCsysTxnLog txnLog,UpayCsysBindInfo bindInfo) throws AppBizException {
		
		logger.info("CrmRevokeBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start");
		
		CrmMsgVo msgVoRtn = new CrmMsgVo();
		
		try {
			// 向Crm撤单请求
			msgVoRtn = sendCrmJmsMessage.sendMsg(msgVo);

		} catch (AppBizException e) {
			
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			upayCsysTxnLogService.modify(txnLog);
			throw e;

		} catch (RuntimeException e){
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			upayCsysTxnLogService.modify(txnLog);
			throw e;
		}
		if (!RspCodeConstant.Wzw.WZW_0000.getValue().equals(msgVoRtn.getRspCode())) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setRcvRspType(msgVoRtn.getRspType());
			txnLog.setRcvRspCode(msgVoRtn.getRspCode());
			txnLog.setRcvRspDesc(msgVoRtn.getRspDesc());
			txnLog.setRcvSubRspCode(msgVoRtn.getRspCode());
			
			upayCsysTxnLogService.modify(txnLog);
		}
		
		logger.info("CrmRevokeBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		
		return msgVoRtn;
	}
}
