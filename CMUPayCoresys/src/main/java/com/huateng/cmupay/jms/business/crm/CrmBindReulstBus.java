package com.huateng.cmupay.jms.business.crm;

import java.util.Map;
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
 * 
 * @author fan_kui
 * 
 */

@Service
public class CrmBindReulstBus extends
		AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>> {
	
//	private @Value("${retry.num}") Integer retryNum;
	
	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	@Autowired
	public void setSendCrmJmsMessage(
			@Qualifier("sendCrmJmsMessage") SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}

	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)
			throws Exception {

		logger.debug("CrmBindReulstBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start");

		CrmMsgVo msgVoRtn = new CrmMsgVo();
		try {
			txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			Long retryNum=Long.parseLong(DictCodeCache.getDictCode(DictConst.DictId.RetryTimes.getValue(), 
					DictConst.CodeId.RetryTimes.getValue()).getCodeValue2());
			for (int i = 1; i <= retryNum; i++) {
				msgVoRtn = sendCrmJmsMessage.sendMsg(msgVo);
				txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				//签约结果通知没有报文体，报文体超时不用判断
				if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(msgVoRtn.getRspCode())) {
						txnLog.setRcvTranshId(msgVoRtn.getTransIDH());
						txnLog.setRcvTranshDt(StrUtil.subString(msgVoRtn.getTransIDOTime(), 0, 8));
						txnLog.setRcvTranshTm(msgVoRtn.getTransIDOTime());
						txnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr.getValue());
						txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setRcvSubRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
						txnLog.setRcvSubRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
						txnLog.setRcvBipCode(msgVoRtn.getBIPCode());
						txnLog.setRcvActivityCode(msgVoRtn.getActivityCode());
						txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					continue;
				} else {
					txnLog.setRcvTranshId(msgVoRtn.getTransIDH());
					txnLog.setRcvTranshDt(StrUtil.subString(
							msgVoRtn.getTransIDOTime(), 0, 8));
					txnLog.setRcvTranshTm(msgVoRtn.getTransIDOTime());
					txnLog.setRcvRspType(msgVoRtn.getRspType());
					txnLog.setRcvRspCode(msgVoRtn.getRspCode());
					txnLog.setRcvRspDesc(msgVoRtn.getRspDesc());
					txnLog.setRcvBipCode(msgVoRtn.getBIPCode());
					txnLog.setRcvActivityCode(msgVoRtn.getActivityCode());
					//签约结果通知，没有报文体 报文头错误码填入报文体，为了对账统一
					txnLog.setRcvSubRspCode(msgVoRtn.getRspCode());
					txnLog.setRcvSubRspDesc(msgVoRtn.getRspDesc());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					break;
				}
			}
		} catch (Exception e) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			throw e;
		}
		//通知类交易没有报文体 只判断报文头返回码
		if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(msgVoRtn.getRspCode())) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());// 最后一步//TODO
		} else {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
		}
		logger.debug("CrmBindReulstBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		return msgVoRtn;
	}

	

}
