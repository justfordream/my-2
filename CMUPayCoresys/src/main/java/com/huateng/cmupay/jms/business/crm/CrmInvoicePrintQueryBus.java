package com.huateng.cmupay.jms.business.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmPrintInvoiceQueryReqVo;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;


@Service
public class CrmInvoicePrintQueryBus extends
AbsCommonBus<CrmMsgVo, CrmMsgVo, CrmPrintInvoiceQueryReqVo>{
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
//	private @Value("${retry.num}") Integer retryNum;
	@Autowired
	public void setSendCrmJmsMessage(
			@Qualifier("sendCrmJmsMessage") SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}
	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo,CrmPrintInvoiceQueryReqVo reqVo,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)throws Exception{
		logger.debug("CrmInvoicePrintQueryBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start");
		CrmMsgVo msgVoRtn = new CrmMsgVo();
		try {
			txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			Long retryNum=Long.parseLong(DictCodeCache.getDictCode(DictConst.DictId.RetryTimes.getValue(), 
					DictConst.CodeId.RetryTimes.getValue()).getCodeValue2());
			for(int i=1;i<=retryNum.intValue();i++){
				msgVoRtn = sendCrmJmsMessage.sendMsg(msgVo);
				txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				if(RspCodeConstant.Upay.UPAY_U99998.getValue()
						.equals(msgVoRtn.getRspCode())){
					txnLog.setRcvTranshId(msgVoRtn.getTransIDH());
					txnLog.setRcvTranshDt(StrUtil.subString(msgVoRtn.getTransIDOTime(), 0, 8));
					txnLog.setRcvTranshTm(msgVoRtn.getTransIDOTime());
					txnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setRcvSubRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
					txnLog.setRcvSubRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				continue;
				}
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				break;
			}
		}catch (Exception e) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			throw e;
		} 	
		logger.debug("CrmInvoicePrintQueryBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		return msgVoRtn;
	}

}
