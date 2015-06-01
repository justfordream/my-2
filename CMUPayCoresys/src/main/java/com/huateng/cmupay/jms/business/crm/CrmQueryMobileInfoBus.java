package com.huateng.cmupay.jms.business.crm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 *用户应缴费查询交易
 * @author ning.z
 * 
 */
@Service("crmQueryMobileInfoBus")
public class CrmQueryMobileInfoBus extends
AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>> {
	protected final  Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	@Autowired
	private IUpayCsysTxnLogService upayCsysTxnLogService;
	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params, UpayCsysTxnLog txnLog,
			UpayCsysBindInfo bindInfo) throws AppBizException {
		logger.debug("CrmQueryMobileInfoBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start");
		msgVo = sendCrmJmsMessage.sendMsg(msgVo);	
		
		if(msgVo.getRspCode().equals(RspCodeConstant.Wzw.WZW_0000.getValue())){
			logger.info("用户应缴费交易查询成功" );		
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
		}else{
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());	
		}
		logger.debug("CrmQueryMobileInfoBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		return msgVo;
	}
	public SendCrmJmsMessageImpl getSendCrmJmsMessage() {
		return sendCrmJmsMessage;
	}
	public void setSendCrmJmsMessage(SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}
	public IUpayCsysTxnLogService getUpayCsysTxnLogService() {
		return upayCsysTxnLogService;
	}
	public void setUpayCsysTxnLogService(IUpayCsysTxnLogService upayCsysTxnLogService) {
		this.upayCsysTxnLogService = upayCsysTxnLogService;
	}
	
	
	
}