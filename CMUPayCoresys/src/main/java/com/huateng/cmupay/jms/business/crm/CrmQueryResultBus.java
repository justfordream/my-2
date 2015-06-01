package com.huateng.cmupay.jms.business.crm;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryResVo;

/**
 * @author cmt
 * @version 创建时间：2013-3-11 下午8:37:50 类说明 crm 交易结果查询
 */
@Service("crmQueryResultBus")
public class CrmQueryResultBus extends
		AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, String>> {

	// 重发次数
//	private @Value("${retry.num}")
//	Long retryNum;

	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	@Autowired
	public void setSendCrmJmsMessage(
			@Qualifier("sendCrmJmsMessage") SendCrmJmsMessageImpl sendCrmJmsMessage) {
		this.sendCrmJmsMessage = sendCrmJmsMessage;
	}

	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, String> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo) throws Exception {
		logger.info("CrmQueryResultBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start");

		CrmMsgVo queryMsgVoRtn = new CrmMsgVo();
		CrmMsgVo queryMsgVo = new CrmMsgVo();
		queryMsgVo.setBIPCode(msgVo.getBIPCode());
		queryMsgVo.setOrigDomain(params.get("origDomain"));
		queryMsgVo.setHomeDomain(params.get("homeDomain"));
		queryMsgVo.setRouteType(CommonConstant.RouteType.RoutePhone.toString());
		queryMsgVo.setRouteValue(params.get("idValue"));
		queryMsgVo.setSessionID(msgVo.getTransIDO());
		queryMsgVo.setTransIDO(msgVo.getTransIDO());
		queryMsgVo.setTransIDOTime(txnLog.getIntTxnTime());
		queryMsgVo.setTestFlag(testFlag);
		queryMsgVo.setVersion(ExcConstant.CRM_VERSION);
		queryMsgVo.setActivityCode(CommonConstant.CrmTrans.Crm09.toString());
		queryMsgVo.setActionCode(CommonConstant.ActionCode.Requset.toString());

		CrmTransQueryReqVo queryBodyMsgVo = new CrmTransQueryReqVo();
		CrmTransQueryResVo queryBodyMsgRtnVo = new CrmTransQueryResVo();
		queryBodyMsgVo.setOriReqSys(params.get("msgSender"));
		queryBodyMsgVo.setOriActionDate(msgVo.getTransIDOTime().substring(0, 8));
		queryBodyMsgVo.setOriTransactionID(msgVo.getTransIDO());
		queryMsgVo.setBody(queryBodyMsgVo);
		Long retryNum=Long.parseLong(DictCodeCache.getDictCode(DictConst.DictId.RetryTimes.getValue(), 
					DictConst.CodeId.RetryTimes.getValue()).getCodeValue2());
		for (int i = 0; i < retryNum.intValue(); i++) {
			try {
				// crm 交易结果查询请求
				queryMsgVoRtn = sendCrmJmsMessage.sendMsg(queryMsgVo);

				txnLog.setRcvRspType(queryMsgVoRtn.getRspType());
				txnLog.setRcvRspCode(queryMsgVoRtn.getRspCode());
				txnLog.setRcvRspDesc(queryMsgVoRtn.getRspDesc());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());

				MsgHandle.unmarshaller(queryBodyMsgRtnVo,
						(String) queryMsgVoRtn.getBody());
				txnLog.setRcvSubRspCode(queryMsgVoRtn.getRspCode());
				txnLog.setRcvSubRspDesc(queryMsgVoRtn.getRspDesc());

			} catch (Exception e) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				// throw e;
			}

			if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
					queryMsgVoRtn.getRspCode())
					&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
							queryBodyMsgRtnVo.getRspCode())) {
				break;
			}
		}

		if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
				queryMsgVoRtn.getRspCode())
				&& RspCodeConstant.Wzw.WZW_0000.getValue().equals(
						queryBodyMsgRtnVo.getRspCode())) {
			// txnLog.setStatus(CommonConstant.TxnStatus.ChargeSuccess
			// .toString());
		} else {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
		}

		queryMsgVoRtn.setBody(queryBodyMsgRtnVo);
		logger.info("CrmQueryResultBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		return queryMsgVoRtn;
	}

}
