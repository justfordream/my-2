 package com.huateng.cmupay.jms.business.crm;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;

/**
 * 
 * @author cmt
 *  冲正接口  T1000159
 *  银行或省公司发起的交易冲正，隔日冲正，或人工退费处理。
 */
@Service
public class CrmReverseBus extends
AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>>{
	

	@Autowired
	private IUpayCsysTxnLogService uPayCsysTxnLogService;
	
	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	@Autowired
	public void setSendCrmJmsMessage(@Qualifier("sendCrmJmsMessage")SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}
	
	public void setuPayCsysTxnLogService(IUpayCsysTxnLogService uPayCsysTxnLogService) {
		this.uPayCsysTxnLogService = uPayCsysTxnLogService;
	}

	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params, UpayCsysTxnLog txnLog,UpayCsysBindInfo bindInfo) throws Exception {
		
		logger.debug("CrmReverseBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start");
		
		CrmMsgVo msgVoRtn = new CrmMsgVo();
		
		try {
			// crm 充值请求
			msgVoRtn = sendCrmJmsMessage.sendMsg(msgVo);

		
		} catch (Exception e){
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			uPayCsysTxnLogService.modify(txnLog);
			throw e;
		}
		if (!RspCodeConstant.Wzw.WZW_0000.getValue().equals(msgVoRtn.getRspCode())) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setRcvRspType(msgVoRtn.getRspType());
			txnLog.setRcvRspCode(msgVoRtn.getRspCode());
			txnLog.setRcvRspDesc(msgVoRtn.getRspDesc());
			txnLog.setRcvSubRspCode(msgVoRtn.getRspCode());
			uPayCsysTxnLogService.modify(txnLog);
			/*throw new AppBizException(bankMsgVoRtn.getRspCode(),
					MessageHandler.getErrorMsgByCode(bankMsgVoRtn.getRspCode()));*/
		}
		logger.debug("CrmReverseBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		return msgVoRtn;
	}
}
