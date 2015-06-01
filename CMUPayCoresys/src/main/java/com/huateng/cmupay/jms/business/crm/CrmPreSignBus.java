package com.huateng.cmupay.jms.business.crm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSignMsgRespVo;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;

/**
 * 银行预签约
 * 
 * @author ning.z
 * 
 */
@Service("crmPreSignBus")
public class CrmPreSignBus extends
		AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>> {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	private @Value("${retry.num}") Integer retryNum;
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;


	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo) throws Exception {
		logger.debug("CrmPreSignBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start"); 
		CrmMsgVo msgVoRtn = new CrmMsgVo();
		CrmSignMsgRespVo bodySignVoRtn =  new CrmSignMsgRespVo();
		try {
			txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			Long retryNum=Long.parseLong(DictCodeCache.getDictCode(DictConst.DictId.RetryTimes.getValue(), 
					DictConst.CodeId.RetryTimes.getValue()).getCodeValue2());
			for (int i = 1; i <= retryNum.intValue(); i++) {				
				msgVoRtn = sendCrmJmsMessage.sendMsg(msgVo);
				txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				MsgHandle.unmarshaller(bodySignVoRtn,
						(String) msgVoRtn.getBody());
				String rtBodyStr = (String) msgVoRtn.getBody();
				msgVoRtn.setBody(null==rtBodyStr || "".equals(rtBodyStr)?null:bodySignVoRtn);
				List<String> crmTimeoutCode = Arrays
						.asList(ExcConstant.CRM_TIMEOUT_CODE);
				if(msgVoRtn.getBody() == null||"".equals(msgVoRtn.getBody())  ){
					msgVoRtn.setBody(null);
				}else {
					msgVoRtn.setBody(bodySignVoRtn);
				}
				if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(msgVoRtn.getRspCode())
						|| crmTimeoutCode.contains(bodySignVoRtn.getRspCode())){
					if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(msgVoRtn.getRspCode())) {
						txnLog.setRcvTranshId(msgVoRtn.getTransIDH());
						txnLog.setRcvTranshDt(StrUtil.subString(
								msgVoRtn.getTransIDHTime(), 0, 8));
						txnLog.setRcvTranshTm(msgVoRtn.getTransIDHTime());
						txnLog.setRcvOprDt(StrUtil.subString(
								msgVoRtn.getTransIDHTime(), 0, 8));
						//txnLog.setRcvOprId(msgVoRtn.getTransIDH());
						txnLog.setRcvOprTm(msgVoRtn.getTransIDHTime());
						txnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr.getValue());
						txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setRcvSubRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
						txnLog.setRcvSubRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
						txnLog.setLastUpdTime(DateUtil
								.getDateyyyyMMddHHmmssSSS());
					} else {
						txnLog.setRcvTranshId(msgVoRtn.getTransIDH());
						txnLog.setRcvTranshDt(StrUtil.subString(
								msgVoRtn.getTransIDHTime(), 0, 8));
						txnLog.setRcvTranshTm(msgVoRtn.getTransIDHTime());
						txnLog.setRcvOprDt(StrUtil.subString(
								msgVoRtn.getTransIDHTime(), 0, 8));
						//txnLog.setRcvOprId(msgVoRtn.getTransIDH());
						txnLog.setRcvOprTm(msgVoRtn.getTransIDHTime());
						txnLog.setRcvRspType(msgVoRtn.getRspType());
						txnLog.setRcvRspCode(msgVoRtn.getRspCode());
						txnLog.setRcvRspDesc(msgVoRtn.getRspDesc());
						txnLog.setRcvSubRspCode(bodySignVoRtn.getRspCode());
						txnLog.setRcvSubRspDesc(bodySignVoRtn.getRspInfo());
						txnLog.setLastUpdTime(DateUtil
								.getDateyyyyMMddHHmmssSSS());
					}
					continue;
				} else {

					txnLog.setRcvTranshId(msgVoRtn.getTransIDH());
					txnLog.setRcvTranshDt(StrUtil.subString(
							msgVoRtn.getTransIDHTime(), 0, 8));
					txnLog.setRcvTranshTm(msgVoRtn.getTransIDHTime());
					txnLog.setRcvOprDt(StrUtil.subString(
							msgVoRtn.getTransIDHTime(), 0, 8));
					//txnLog.setRcvOprId(msgVoRtn.getTransIDH());
					txnLog.setRcvOprTm(msgVoRtn.getTransIDHTime());
					txnLog.setRcvRspType(msgVoRtn.getRspType());
					txnLog.setRcvRspCode(msgVoRtn.getRspCode());
					txnLog.setRcvRspDesc(msgVoRtn.getRspDesc());
					txnLog.setRcvSubRspCode(bodySignVoRtn.getRspCode());
					txnLog.setRcvSubRspDesc(bodySignVoRtn.getRspInfo());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					break;
				}

			}
			
		} catch (Exception e) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			throw e;
		}
		if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(msgVoRtn.getRspCode())&&RspCodeConstant.Crm.CRM_0000.getValue().equals(bodySignVoRtn.getRspCode())) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());// 最后一步//TODO
		} else {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
		}
		logger.debug("CrmPreSignBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		uPayCsysTxnLogService.modify(txnLog);
		return msgVoRtn;
	}

	public SendCrmJmsMessageImpl getSendCrmJmsMessage() {
		return sendCrmJmsMessage;
	}

	public void setSendCrmJmsMessage(SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}



}