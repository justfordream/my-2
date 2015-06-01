//package com.huateng.cmupay.jms.business.crm;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import com.huateng.cmupay.constant.CommonConstant;
//import com.huateng.cmupay.controller.service.system.UpayCsysTxnLogService;
//import com.huateng.cmupay.exception.AppBizException;
//import com.huateng.cmupay.jms.business.common.SendBankJmsMessageImpl;
//import com.huateng.cmupay.jms.business.common.SendCrmJmsMessageImpl;
//import com.huateng.cmupay.models.UpayCsysBindInfo;
//import com.huateng.cmupay.models.UpayCsysRouteInfo;
//import com.huateng.cmupay.models.UpayCsysTxnLog;
//
//import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmConsumeReqVo;
//import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
//
//import com.huateng.cmupay.utils.Serial;
//import com.huateng.cmupay.utils.DateUtil;
//
///**
// * @author ning.z
// * @version 银行手机充值超时冲正处理
// */
//public class CrmConsumeReverseBus {
//
//	// 重发次数
//	private @Value("${retry.num}")
//	Long retryNum;
//
//	@Autowired
//	private UpayCsysTxnLogService uPayCsysTxnLogService;
//	@Autowired
//	private SendCrmJmsMessageImpl sendCrmJmsMessage;
//
//	
//	
//	public CrmMsgVo execute(CrmMsgVo msgVo,UpayCsysRouteInfo routeInfo,UpayCsysTxnLog txnLog,UpayCsysBindInfo bindInfo)
//			throws AppBizException {
//
//		CrmMsgVo crmMsgVoRevert = new CrmMsgVo();
//
//		// bankMsgVo.setReqSys(CommonConstant.BankOrgCode.CMCC.toString());//TODO//此处需要确认
//		   //组装报文头信息
////				msgVo.setVersion("");
//				msgVo.setTestFlag("");
//				msgVo.setbIPCode("");
//				msgVo.setActivityCode(CommonConstant.CrmTrans.Crm07.toString());
//				msgVo.setActionCode(CommonConstant.ActionCode.Requset.toString());
//				msgVo.setOrigDomain("");
//				msgVo.setRouteType("");
//				msgVo.setHomeDomain("");
//				msgVo.setRouteValue("");
////				msgVo.setSessionID("");
//				msgVo.setTransIDO(Serial.genSerialNo(CommonConstant.PlatformCd.CrmSys.toString()));
////				msgVo.setTransIDOTime("");
//				msgVo.setTransIDH("");
////				msgVo.setTransIDHTime("");
//				msgVo.setTransIDC("");
//				msgVo.setConvID("");
//				msgVo.setCutOffDay("");
////				msgVo.setoSNTime)("");
//				msgVo.setoSNDUNS("");
//				msgVo.sethSNDUNS("");
//				msgVo.setMsgSender("");
//				msgVo.setMsgReceiver("");
////				msgVo.setPriority("");
////				msgVo.setServiceLevel("");
//				msgVo.setSvcContVer("");
//				//报文体
//				CrmConsumeReqVo crmConsumeReq = (CrmConsumeReqVo)msgVo.getBody();
//				//IDTYPE 主类已set值
//				crmConsumeReq.setIdType("");
//				crmConsumeReq.setIdValue("");
//				crmConsumeReq.setTransactionId("");
//				crmConsumeReq.setActionDate(DateUtil.getDateyyyyMMdd());
//				crmConsumeReq.setActionTime(DateUtil.getDateyyyyMMddHHmmss());
//				crmConsumeReq.setPayed("");
//				crmConsumeReq.setBankId("");
//				crmConsumeReq.setCnlType("");
//				crmConsumeReq.setPayedType("01");
//				msgVo.setBody(crmConsumeReq);
//
//		for (int i = 0; i < retryNum.intValue(); i++) {
//			try {
//
////				// 超时进行冲正处理
////				crmMsgVoRevert = sendCrmJmsMessage.sendMsg(routeInfo,
////						CommonConstant.CrmTrans.Crm07.toString(), "",
////						msgVo);
////
////			} catch (AppBizException e) {
////
////				txnLog.setStatus(CommonConstant.TxnStatus.RevertFail.toString());
////				uPayCsysTxnLogService.modify(txnLog);
////				throw e;
//
//			} catch (RuntimeException e) {
//				txnLog.setStatus(CommonConstant.TxnStatus.CrmRevertFail.toString());
//				uPayCsysTxnLogService.modify(txnLog);
//				throw e;
//			}
//			if (!"000000".equals(crmMsgVoRevert.getRspCode())) {
//				txnLog.setStatus(CommonConstant.TxnStatus.CrmRevertFail.toString());
//				uPayCsysTxnLogService.modify(txnLog);
//			} else {
//				txnLog.setStatus(CommonConstant.TxnStatus.CrmRevertSuccess
//						.toString());
//				txnLog.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());
//				uPayCsysTxnLogService.modify(txnLog);
//			}
//
//		}
//
//		return crmMsgVoRevert;
//	}
//
//}
