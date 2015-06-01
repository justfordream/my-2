package com.huateng.cmupay.jms.business.crm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.toolbox.utils.DateUtil;

/**
 * 副号码解约处理
 * @author zeng.j
 * 
 */
@Service("crmSubUnbindBus")
public class CrmSubUnbindBus extends
AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>> {
	protected final  Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	@Autowired
	private IUpayCsysTxnLogService upayCsysTxnLogService;
	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params, UpayCsysTxnLog txnLog,
			UpayCsysBindInfo bindInfo) throws Exception {
		logger.debug("CrmSubUnbindBus execute(Object,Map,Object,Object) - start");
		txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		msgVo = sendCrmJmsMessage.sendMsg(msgVo);	
		txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		if(RspCodeConstant.Wzw.WZW_0000.getValue().equals(msgVo.getRspCode())){
			logger.info("解约返回成功，respcode:0000" );		
		}else{
			logger.info("解约返回失败，respcode:{} , resp_desc{}",msgVo.getRspCode(),msgVo.getRspDesc() );		
		}	
		/**更新交易流水*/	
		txnLog.setRcvRspCode(msgVo.getRspCode());
		txnLog.setRcvRspDesc(msgVo.getRspDesc());
		txnLog.setRcvRspType(msgVo.getRspType());
		logger.debug("CrmSubUnbindBus execute(Object,Map,Object,Object) - end");
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
