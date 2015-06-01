package com.huateng.cmupay.jms.business.crm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTmallTxnLog;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;

/**
 * 第三方支付流程去CRM充值
 * @author Manzhizhen
 */
@Service
public class TUpayCrmChargeBus extends
		AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, String>> {

	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public CrmMsgVo execute(CrmMsgVo chargeMsgVo, Map<String, String> params,
			TpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo) throws Exception {
		logger.debug("TUpayCrmChargeBus execute(CrmMsgVo, TpayCsysTxnLog, UpayCsysBindInfo) - start,内部交易流水号{}.", txnLog.getIntTxnSeq());
		CrmMsgVo msgVo = new CrmMsgVo();

		CrmChargeResVo bodyChargeVoRtn = new CrmChargeResVo();
		CrmChargeReqVo bodyChargeVo = new CrmChargeReqVo();
		
		bodyChargeVo.setIDType(params.get("idType"));
		bodyChargeVo.setIDValue(params.get("idValue"));
		bodyChargeVo.setTransactionID(params.get("transactionID"));

		bodyChargeVo.setBusiTransID(params.get("busiTransID"));
		bodyChargeVo.setPayTransID(params.get("payTransID"));
		bodyChargeVo.setActionDate(params.get("actionDate"));
		bodyChargeVo.setActionTime(params.get("actionTime"));
		
		bodyChargeVo.setChargeMoney(params.get("chargeMoney"));
		bodyChargeVo.setOrganID(params.get("organID"));
		bodyChargeVo.setCnlTyp(params.get("cnlTyp"));
		bodyChargeVo.setPayedType(params.get("payedType"));
		bodyChargeVo.setSettleDate(params.get("settleDate"));
		
		bodyChargeVo.setOrderNo(params.get("orderNo"));
		bodyChargeVo.setProductNo(params.get("productNo"));
		bodyChargeVo.setPayment(params.get("payment"));
		bodyChargeVo.setOrderCnt(params.get("orderCnt"));
		bodyChargeVo.setCommision(params.get("commision"));
		bodyChargeVo.setRebateFee(params.get("rebateFee"));
		bodyChargeVo.setProdDiscount(params.get("prodDiscount"));
		bodyChargeVo.setCreditCardFee(params.get("creditCardFee"));
		bodyChargeVo.setServiceFee(params.get("serviceFee"));
		bodyChargeVo.setActivityNo(params.get("activityNo"));
		bodyChargeVo.setProductShelfNo(params.get("productShelfNo"));
		bodyChargeVo.setReserve1(params.get("reserve1"));
		bodyChargeVo.setReserve2(params.get("reserve2"));
		bodyChargeVo.setReserve3(params.get("reserve3"));
		bodyChargeVo.setReserve4(params.get("reserve4"));
	
		msgVo.setVersion(ExcConstant.CRM_VERSION);
		msgVo.setTestFlag(testFlag);
		msgVo.setBIPCode(chargeMsgVo.getBIPCode());
		msgVo.setActivityCode(CommonConstant.CrmTrans.Crm07.toString());
		msgVo.setActionCode(CommonConstant.ActionCode.Requset.toString());
		msgVo.setOrigDomain(ExcConstant.DOMAIN_UPSS);
		msgVo.setRouteType(CommonConstant.RouteType.RoutePhone.toString());
		msgVo.setHomeDomain(ExcConstant.DOMAIN_BOSS);
		msgVo.setRouteValue(chargeMsgVo.getRouteValue());
		msgVo.setSessionID(chargeMsgVo.getSessionID());
		msgVo.setTransIDO(chargeMsgVo.getTransIDO());
		msgVo.setTransIDOTime(StrUtil.subString(chargeMsgVo.getTransIDOTime(),
				0, 14));
		msgVo.setMsgReceiver(chargeMsgVo.getMsgReceiver());
		msgVo.setBody(bodyChargeVo);
		txnLog.setPayedType(bodyChargeVo.getPayedType());
//		txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
//		txnLog.setRcvBipCode(chargeMsgVo.getBIPCode());
//		txnLog.setRcvActivityCode(CommonConstant.CrmTrans.Crm07.toString());
		txnLog.setRcvDomain(chargeMsgVo.getMsgReceiver());
//		txnLog.setRcvRouteType(CommonConstant.RouteType.RoutePhone.toString());
//		txnLog.setRcvRouteVal(bodyChargeVo.getIDValue());
		txnLog.setRcvCnlType(txnLog.getReqCnlType());
		txnLog.setRcvSessionId(chargeMsgVo.getSessionID());
		txnLog.setRcvTransId(chargeMsgVo.getTransIDO());
		txnLog.setRcvTransDt(txnLog.getIntTxnDate());
		txnLog.setRcvTransTm(StrUtil.subString(chargeMsgVo.getTransIDOTime(),
				0, 14));
		txnLog.setRcvOprId(bodyChargeVo.getTransactionID());
		txnLog.setRcvOprDt(txnLog.getIntTxnDate());
		txnLog.setRcvOprTm(StrUtil.subString(chargeMsgVo.getTransIDOTime(), 0,
				14));
		CrmMsgVo msgVoRtn = null;
		// crm 充值请求
		txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		logger.info("手机号:{},发起方:{},接收方机构:{},发往省交易流水号:{},发往省操作流水号:{},内部交易流水号{},订单号：{}",
				new Object[] {bodyChargeVo.getIDValue(),bodyChargeVo.getOrganID(), 
				chargeMsgVo.getMsgReceiver(),msgVo.getTransIDO(), bodyChargeVo.getTransactionID(),
				txnLog.getIntTxnSeq(), bodyChargeVo.getOrderNo()});
		
		try {
			msgVoRtn = sendCrmJmsMessage.sendMsg(msgVo);
			logger.debug(
					"CrmChargeBus  sendCrmJmsMessage.sendMsg response:{}",
					new Object[] { msgVoRtn.getBody() });
			txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			String bodyMsg = (String) msgVoRtn.getBody();
			MsgHandle.unmarshaller(bodyChargeVoRtn, bodyMsg);
			
			if (bodyMsg == null || "null".equalsIgnoreCase(bodyMsg.trim())
					|| "".equals(bodyMsg.trim())
					/*|| (Arrays.asList(ExcConstant.WZW_BODY_MSG_ERROR)).contains(bodyMsg.trim())*/) {
				msgVoRtn.setBody("");
			} else {
				msgVoRtn.setBody(bodyChargeVoRtn);
			}
			List<String> crmTimeoutCode = Arrays.asList(ExcConstant.CRM_TIMEOUT_CODE);
			if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(msgVoRtn.getRspCode())
					|| crmTimeoutCode.contains(bodyChargeVoRtn.getRspCode())) {
				if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(msgVoRtn.getRspCode())) {
					txnLog.setRcvTranshId(msgVoRtn.getTransIDH());
					txnLog.setRcvTranshDt(StrUtil.subString(
							msgVoRtn.getTransIDHTime(), 0, 8));
					txnLog.setRcvTranshTm(msgVoRtn.getTransIDHTime());
					txnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr
							.toString());
					txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_2998
							.getValue());
					txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_2998
							.getDesc());
					txnLog.setRcvSubRspCode(RspCodeConstant.Upay.UPAY_U99998
							.getValue());
					txnLog.setRcvSubRspDesc(RspCodeConstant.Upay.UPAY_U99998
							.getDesc());
					txnLog.setLastUpdTime(DateUtil
							.getDateyyyyMMddHHmmssSSS());
				} else {
					txnLog.setRcvTranshId(msgVoRtn.getTransIDH());
					txnLog.setRcvTranshDt(StrUtil.subString(
							msgVoRtn.getTransIDHTime(), 0, 8));
					txnLog.setRcvTranshTm(msgVoRtn.getTransIDHTime());
					txnLog.setRcvRspType(msgVoRtn.getRspType());
					txnLog.setRcvRspCode(msgVoRtn.getRspCode());
					txnLog.setRcvRspDesc(msgVoRtn.getRspDesc());
					txnLog.setRcvSubRspCode(bodyChargeVoRtn.getRspCode());
					txnLog.setRcvSubRspDesc(bodyChargeVoRtn.getRspInfo());
					txnLog.setLastUpdTime(DateUtil
							.getDateyyyyMMddHHmmssSSS());
				}
			} else {
				txnLog.setRcvTranshId(msgVoRtn.getTransIDH());
				txnLog.setRcvTranshDt(StrUtil.subString(
						msgVoRtn.getTransIDHTime(), 0, 8));
				txnLog.setRcvTranshTm(msgVoRtn.getTransIDHTime());
				txnLog.setRcvRspType(msgVoRtn.getRspType());
				txnLog.setRcvRspCode(msgVoRtn.getRspCode());
				txnLog.setRcvRspDesc(msgVoRtn.getRspDesc());
//				txnLog.setUserCat(bodyChargeVoRtn.getUserCat());
				txnLog.setRcvSubRspCode(bodyChargeVoRtn.getRspCode());
				txnLog.setRcvSubRspDesc(bodyChargeVoRtn.getRspInfo());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			}
		} catch (Exception e) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				throw e;
		}

		if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
				msgVoRtn.getRspCode())
				&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
						bodyChargeVoRtn.getRspCode())) {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
		} else {
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
		}

		logger.debug("TUpayCrmChargeBus execute(CrmMsgVo, TpayCsysTxnLog, UpayCsysBindInfo) - end,内部交易流水号{}.", txnLog.getIntTxnSeq());
		return msgVoRtn;
	}

	// 这个方法没有用到
	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, String> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo bindInfo)
			throws AppRTException, AppBizException, Exception {
		return null;
	}
	
}
