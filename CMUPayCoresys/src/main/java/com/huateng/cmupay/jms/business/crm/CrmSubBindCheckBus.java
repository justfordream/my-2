package com.huateng.cmupay.jms.business.crm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;

/**
 * 副号码签约校验
 * @author ning.z
 * 
 */
@Service("crmSubBindCheckBus")
public class CrmSubBindCheckBus extends
AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>> {
	protected final  Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	
//	private @Value("${retry.num}") Integer retryNum;
	
	@Autowired
	public void setSendCrmJmsMessage(
			@Qualifier("sendCrmJmsMessage") SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}



	
	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params, UpayCsysTxnLog txnLog,
			UpayCsysBindInfo bindInfo) throws Exception {
		logger.debug("CrmSubBindCheckBus execute(Object,Map,Object,Object) - start,内部交易流水号{}.", txnLog.getIntTxnSeq());
		
		try { 
			Long retryNum=Long.parseLong(DictCodeCache.getDictCode(DictConst.DictId.RetryTimes.getValue(), 
					DictConst.CodeId.RetryTimes.getValue()).getCodeValue2());
			for(int i=1;i<=retryNum;i++){
				msgVo = sendCrmJmsMessage.sendMsg(msgVo);
				if(RspCodeConstant.Upay.UPAY_U99998.getValue()
						.equals(msgVo.getRspCode())){
					txnLog.setRcvTranshId(msgVo.getTransIDH());
					txnLog.setRcvTranshDt(StrUtil.subString(msgVo.getTransIDOTime(), 0, 8));
					txnLog.setRcvTranshTm(msgVo.getTransIDOTime());
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
		logger.debug("CrmSubBindCheckBus execute(Object,Map,Object,Object) - end,内部交易流水号{}.", txnLog.getIntTxnSeq() );
		return msgVo;
	}

	
	
	
}