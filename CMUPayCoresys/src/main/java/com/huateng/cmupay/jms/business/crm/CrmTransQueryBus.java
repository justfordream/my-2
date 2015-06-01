package com.huateng.cmupay.jms.business.crm;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;

/**
 * crm 交易结果查询
 * 
 * @author zeng.j
 * 
 */
@Service("crmTransQueryBus")
public class CrmTransQueryBus extends
		AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>> {


	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)
			throws AppRTException, AppBizException, Exception {
		logger.debug("CrmTransQueryBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - start");
		/** 报文头 */
		CrmMsgVo reqMsg = new CrmMsgVo();
		CrmMsgVo resMsg = new CrmMsgVo();
		String idProvince = (String) params.get("idProvince");
		String forwardOrg = SysMapCache.getProvCd(idProvince).getSysCd();
		String sessionId = Serial
				.genSerialNo(CommonConstant.Sequence.CrmSessionId.getValue());
		String transId = Serial.genSerialNo(CommonConstant.Sequence.IntSeq
				.getValue());
		String actionDate = (String) params.get("actionDate");
		String oriReqSys = (String) params.get("oriReqSys");
		String oprId = (String) params.get("oprId");
		String activityCode = (String) params.get("activityCode");
		String idValue = (String) params.get("pidValue");
		reqMsg.setVersion(ExcConstant.CRM_VERSION);
		reqMsg.setTestFlag(testFlag);
		reqMsg.setBIPCode(CommonConstant.Bip.Bis18.getValue());
		reqMsg.setActivityCode(CommonConstant.CrmTrans.Crm09.getValue());
		reqMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
		reqMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
		reqMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
		//修改为按号码路由 20131212 modify by weiyi
		reqMsg.setRouteType(CommonConstant.RouteType.RoutePhone
				.getValue());
//		reqMsg.setRouteValue(forwardOrg);
		reqMsg.setRouteValue(idValue);
		
		reqMsg.setSessionID(sessionId); // 待确认
		reqMsg.setTransIDO(transId);
		reqMsg.setTransIDOTime(DateUtil.getDateyyyyMMddHHmmss());
		reqMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
		reqMsg.setMsgReceiver(forwardOrg);//

		/** 报文体 */
		CrmTransQueryReqVo reqBody = new CrmTransQueryReqVo();
		reqBody.setOriActionDate(actionDate);
		reqBody.setOriReqSys(oriReqSys);
		reqBody.setOriTransactionID(oprId);
		reqBody.setOriActivityCode(activityCode);
		reqMsg.setBody(reqBody);
		logger.info(" start crm交易结果查询 ");
		resMsg = sendCrmJmsMessage.sendMsg(reqMsg);
		logger.info(" return crm交易结果查询");
		CrmTransQueryResVo resBody = new CrmTransQueryResVo();
		MsgHandle.unmarshaller(resBody, resMsg.getBody()
				.toString());
		logger.info(" success 转换交易结果查询返回报文体");
		resMsg.setBody(resBody);
		logger.debug("CrmTransQueryBus execute(CrmMsgVo, UpayCsysTxnLog, UpayCsysBindInfo) - end");
		return resMsg;
	}

}
