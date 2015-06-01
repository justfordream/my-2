package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmSubConnectBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubBindMsgUpdateReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubBindMsgUpdateResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubBindReqVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;

/**
 * @author ning.z 副号码签约关系同步信息 1． 对省中心发送的报文进行格式校验 2． 记录交易流水号。 3．
 *         校验移动机构状态，同时校验移动省中心的交易权限。 4． 主号码签约关系检查、副号码签约关系检查。 5．
 *         根据手机号查询到手机归属地，并判断主号码归属地和副号码归属地是否相同，
 *         如果相同则建立关联关系，如果不是同一归属地则将关联交易转发给归属地所在系统。 6． 接收关联交易应答 7． 建立关联关系 8．
 *         更新交易流水 9． 返回应答给发起方
 */

@Controller("crmSubConnectAction")
@Scope("prototype")
public class CrmSubConnectAction extends AbsBaseAction<CrmMsgVo, CrmMsgVo> {
	@Autowired
	private CrmSubConnectBus crmSubConnectBus;

	// private @Value("${sign.slave.count}")
	// Integer signSlaveCount;

	@Override
	public CrmMsgVo execute(CrmMsgVo paramData) throws AppBizException {
		logger.debug("CrmSubConnectAction execute(Object) - start");
		CrmMsgVo crmMsgVo = (CrmMsgVo) paramData;
		CrmMsgVo msgVoRtn = crmMsgVo;
		CrmMsgVo msgVoRsn = crmMsgVo;
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		CrmSubBindReqVo subBindMsgUpdateReqVo = new CrmSubBindReqVo();
		MsgHandle.unmarshaller(subBindMsgUpdateReqVo,
				(String) crmMsgVo.getBody());
		String transIDH = crmMsgVo.getTxnSeq();// 落地方交易流水号
		String transIDHTime = paramData.getTxnTime();// 落地方处理时间
		String intTxnDate = crmMsgVo.getTxnDate();
		Long seqId = crmMsgVo.getSeqId(); // 交易流水seq
		Long bainSeqId = upayCsysSeqMapInfoService
				.selectSeqValue(ExcConstant.BIND_INFO_SEQ); // 签约信息seq
		try {
			// 报文体内容check
			String validateMsg = this.validateModel(subBindMsgUpdateReqVo);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.info("副号码签约关系同步信息,内部交易流水号:{},body体校验成功",
						new Object[] { transIDH });
			} else {
				logger.warn(
						"副号码签约关系同步信息,内部交易流水号:{},报文体校验失败:{},业务发起方:{}",
						new Object[] { transIDH, validateMsg,
								paramData.getMsgSender() });
				log.warn(
						"副号码签约关系同步信息,内部交易流水号:{},报文体校验失败:{},业务发起方:{}",
						new Object[] { transIDH, validateMsg,
								paramData.getMsgSender() });
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_4A99.getDesc()
						+ validateMsg);
				res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
				res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
				res.setSubID(subBindMsgUpdateReqVo.getSubID());
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A99.getDesc()
						+ validateMsg);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);

				logger.debug("CrmSubConnectAction execute(Object) - end");
				return msgVoRtn;
			}

			UpayCsysTransCode transCode = crmMsgVo.getTransCode();
			boolean notLog = false;
			logger.debug("副号码签约关系同步信息,重复订单检查");
			if (!StringUtils.isBlank(subBindMsgUpdateReqVo.getTransactionID())) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("reqDomain", crmMsgVo.getMsgSender());
				param.put("reqOprId", subBindMsgUpdateReqVo.getTransactionID());
				UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService
						.findObj(param);
				if (upayCsysTxnLog != null) {
					log.info("副号码签约关系同步信息,订单重复,内部交易流水号:{},业务发起方:{}",
							new Object[] { transIDH, crmMsgVo.getMsgSender() });
					logger.info(
							"副号码签约关系同步信息,订单重复,内部交易流水号:{},业务发起方:{},ReqOprId:{}",
							new Object[] { transIDH, crmMsgVo.getMsgSender(),
									subBindMsgUpdateReqVo.getTransactionID() });
					intTxnDate = upayCsysTxnLog.getIntTxnDate();
					transIDHTime = upayCsysTxnLog.getIntTxnTime();
					seqId = upayCsysTxnLog.getSeqId();
					transIDH = upayCsysTxnLog.getIntTxnSeq();// 落地方交易流水号
					// String sessionId = transIDH;
					txnLog = upayCsysTxnLog;
					notLog = true;
				}
			}
			if (!notLog) {
				// 记录交易流水
				transIDHTime = crmMsgVo.getTxnTime();// 落地方处理时间
				intTxnDate = crmMsgVo.getTxnDate();
				seqId = crmMsgVo.getSeqId(); // 交易流水seq
				logger.debug("副号码签约关系同步信息,add交易流水");
				txnLog.setSeqId(seqId);
				txnLog.setIntTxnDate(intTxnDate);// 内部交易日期
				txnLog.setIntTxnSeq(transIDH);
				txnLog.setIntTransCode(CommonConstant.TransCode.SubSignSyn
						.getValue());
				txnLog.setIntTxnTime(transIDHTime);
				txnLog.setPayMode(transCode.getPayMode());
				txnLog.setBussType(transCode.getBussType());
				txnLog.setBussChl(transCode.getBussChl());
				txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No
						.toString());
				// txnLog.setTxnCat(transCode.getTxnCat());
				txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
				txnLog.setReqVersion(crmMsgVo.getVersion());
				txnLog.setReqBipCode(crmMsgVo.getBIPCode());
				txnLog.setReqActivityCode(crmMsgVo.getActivityCode());
				txnLog.setReqDomain(crmMsgVo.getMsgSender());
				txnLog.setReqRouteType(crmMsgVo.getRouteType());
				txnLog.setReqRouteVal(crmMsgVo.getRouteValue());
				txnLog.setReqSessionId(crmMsgVo.getSessionID());// 发起方业务流水号
				txnLog.setReqTransId(crmMsgVo.getTransIDO());
				txnLog.setSettleDate(subBindMsgUpdateReqVo.getActionDate());
				txnLog.setReqTransDt(StrUtil.subString(
						crmMsgVo.getTransIDOTime(), 0, 8));
				txnLog.setReqTransTm(crmMsgVo.getTransIDOTime());
				txnLog.setReqTranshId(transIDH);
				txnLog.setReqTranshDt(intTxnDate);
				txnLog.setReqTranshTm(transIDHTime);
				txnLog.setReqOprId(subBindMsgUpdateReqVo.getTransactionID());// 发起方操作
				txnLog.setReqOprDt(subBindMsgUpdateReqVo.getActionDate());
				txnLog.setReqOprTm(crmMsgVo.getTransIDOTime());
				txnLog.setReqCnlType(subBindMsgUpdateReqVo.getCnlTyp());// 发起方渠道标识
				txnLog.setMainFlag(CommonConstant.Mainflag.Slave.getValue());
				txnLog.setMainIdType(subBindMsgUpdateReqVo.getMainIDType());
				txnLog.setMainIdValue(subBindMsgUpdateReqVo.getMainIDValue());
				txnLog.setIdType(subBindMsgUpdateReqVo.getIDType());
				txnLog.setIdValue(subBindMsgUpdateReqVo.getIDValue());
				txnLog.setOriOrgId(null);
				txnLog.setOriOprTransId(null);
				txnLog.setOriReqDate(null);
				txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				txnLog.setSettleDate(DateUtil.getDateyyyyMMdd());
				upayCsysTxnLogService.add(txnLog);
			} else {
				// 重复交易
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc());
				res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
				res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
				res.setSubID(subBindMsgUpdateReqVo.getSubID());
				msgVoRtn.setBody(res);
//				txnLog.setChlRspType(msgVoRtn.getRspType());
//				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				txnLog.setChlRspType(msgVoRtn.getRspType());
//				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
//				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A17.getDesc());
//				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				upayCsysTxnLogService.modify(txnLog);

				logger.debug("CrmSubConnectAction execute(Object) - end");
				return msgVoRtn;
			}

			// 根据subID查找主号码
			logger.debug("副号码签约关系同步信息,开始SubId查询主号");
			Map<String, Object> mainPhone = new HashMap<String, Object>();
			if (subBindMsgUpdateReqVo.getMainIDValue() == null
					|| "".equals(subBindMsgUpdateReqVo.getMainIDValue())) {
				logger.info("副号码签约关系同步信息,MainIDValue为空");
			} else {
				mainPhone
						.put("idValue", subBindMsgUpdateReqVo.getMainIDValue());
			}
			mainPhone.put("subId", subBindMsgUpdateReqVo.getSubID());
			mainPhone
					.put("mainFlag", CommonConstant.Mainflag.Master.getValue());
			mainPhone.put("status", CommonConstant.BindStatus.Bind.toString());
			List<UpayCsysBindInfo> mainSubInfo = upayCsysBindInfoService
					.findList(mainPhone, null);
			if (mainSubInfo.size() == 0 || mainSubInfo.isEmpty()) {
				logger.warn(
						"副号码签约关系同步信息,内部交易流水号:{},签约协议号:{}不存在,业务发起方:{}",
						new Object[] { transIDH,
								subBindMsgUpdateReqVo.getSubID(),
								paramData.getMsgSender() });
				log.warn(
						"副号码签约关系同步信息,内部交易流水号:{},签约协议号:'{}不存在,业务发起方:{}",
						new Object[] { transIDH,
								subBindMsgUpdateReqVo.getSubID(),
								paramData.getMsgSender() });
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_2A13.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_2A13.getDesc());
				res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
				res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
				res.setSubID(subBindMsgUpdateReqVo.getSubID());
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A13.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A13.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);

				logger.debug("CrmSubConnectAction execute(Object) - end");
				return msgVoRtn;
			} else if (mainSubInfo.size() > 1) {
				logger.warn(
						"副号码签约关系同步信息,内部交易流水号:{},签约协议号:{}填写错误,业务发起方:{}",
						new Object[] { transIDH,
								subBindMsgUpdateReqVo.getSubID(),
								paramData.getMsgSender() });
				log.warn(
						"副号码签约关系同步信息,内部交易流水号:{},签约协议号:'{}填写错误,业务发起方:{}",
						new Object[] { transIDH,
								subBindMsgUpdateReqVo.getSubID(),
								paramData.getMsgSender() });
				msgVoRtn = crmMsgVo;
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_3A30.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_3A30.getDesc());
				res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
				res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
				res.setSubID(subBindMsgUpdateReqVo.getSubID());
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspCode());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A30.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A30.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);

				logger.debug("CrmSubConnectAction execute(Object) - end");
				return msgVoRtn;
			}
			UpayCsysBindInfo mainPhoneInfo = mainSubInfo.get(0);
			// 查询副号码的签约信息
			logger.debug("副号码签约关系同步信息,开始查询副号码签约信息");
			Map<String, Object> paramBind = new HashMap<String, Object>();
			paramBind.put("idValue", subBindMsgUpdateReqVo.getIDValue());
			UpayCsysBindInfo subBindInfo = upayCsysBindInfoService
					.findObj(paramBind);
			if (subBindInfo == null) {
				logger.info("intTxnSeq:{},副号:{}签约信息为空,add one", transIDH,
						subBindMsgUpdateReqVo.getIDValue());
				subBindInfo = new UpayCsysBindInfo();
				subBindInfo.setSeqId(bainSeqId);
				subBindInfo.setSignCnlType(subBindMsgUpdateReqVo.getCnlTyp());// TODO
				subBindInfo.setSignOrgId(crmMsgVo.getMsgSender());
				subBindInfo.setMainFlag(CommonConstant.Mainflag.Slave
						.getValue());
				subBindInfo.setPayType("");
				subBindInfo.setIdProvince("");
				subBindInfo.setSubId(subBindMsgUpdateReqVo.getSubID());// add
				subBindInfo.setIdType(subBindMsgUpdateReqVo.getIDType());
				subBindInfo.setIdValue(subBindMsgUpdateReqVo.getIDValue());
				subBindInfo.setStatus(CommonConstant.BindStatus.UnBind
						.getValue());
				subBindInfo.setLastUpdTime(transIDHTime);
				upayCsysBindInfoService.add(subBindInfo);
			} else if ((subBindInfo.getStatus())
					.equals(CommonConstant.BindStatus.Bind.toString())) {
				logger.warn("副号码签约关系同步信息,内部交易流水号:{},副号码:{}已签约,业务发起方:{}",
						new Object[] { transIDH, subBindInfo.getIdValue(),
								paramData.getMsgSender() });
				log.warn("副号码签约关系同步信息,内部交易流水号:{},副号码:{}已签约,业务发起方:{}",
						new Object[] { transIDH, subBindInfo.getIdValue(),
								paramData.getMsgSender() });
				msgVoRtn = crmMsgVo;
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
				if (subBindMsgUpdateReqVo.getMainIDValue().equals(
						subBindInfo.getMainIdValue())) {
					res.setRspCode(RspCodeConstant.Crm.CRM_2A15.getValue());
					res.setRspInfo(RspCodeConstant.Crm.CRM_2A15.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A15
							.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A15
							.getDesc());
				} else {
					res.setRspCode(RspCodeConstant.Crm.CRM_2A16.getValue());
					res.setRspInfo(RspCodeConstant.Crm.CRM_2A16.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A16
							.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A16
							.getDesc());
				}
				res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
				res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
				res.setSubID(subBindMsgUpdateReqVo.getSubID());
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);

				logger.debug("CrmSubConnectAction execute(Object) - end");
				return msgVoRtn;
			}
			// 副号码的归属省查询
			// String subProvince = upayCsysImsiLdCdService
			// .findProvinceByMobileNumber(subBindMsgUpdateReqVo
			// .getiDValue());
			// String subProvince =
			// ProvAreaCache.getProvAreaByPrimary(subBindMsgUpdateReqVo.getiDValue());
			ProvincePhoneNum subProvincePhoneNum = findProvinceByMobileNumber(subBindMsgUpdateReqVo.getIDValue());
			String subProvince = subProvincePhoneNum == null ? null : subProvincePhoneNum.getProvinceCode();
			if (subProvince == null) {
				logger.warn("副号码签约关系同步信息,内部交易流水号:{},副号码:{}填写错误,业务发起方:{}",
						new Object[] { transIDH, subBindInfo.getIdValue(),
								paramData.getMsgSender() });
				log.warn("副号码签约关系同步信息,内部交易流水号:{},副号码:{}填写错误,业务发起方:{}",
						new Object[] { transIDH, subBindInfo.getIdValue(),
								paramData.getMsgSender() });
				msgVoRtn = crmMsgVo;
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_2A22.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_2A22.getDesc());
				res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
				res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
				res.setSubID(subBindMsgUpdateReqVo.getSubID());
				msgVoRtn.setBody(res);
				txnLog.setRcvDomain(crmMsgVo.getMsgSender());
				txnLog.setRcvOprId(subBindMsgUpdateReqVo.getTransactionID());
				txnLog.setRcvOprDt(subBindMsgUpdateReqVo.getActionDate());
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A22.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A22.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);

				logger.debug("CrmSubConnectAction execute(Object) - end");
				return msgVoRtn;
			}
			txnLog.setIdProvince(subProvince);
			// 副号码机构号
			String subOrgId = SysMapCache.getProvCd(subProvince).getSysCd();
			// 主号码归属省
			if(mainPhoneInfo != null && mainPhoneInfo.getIdValue() == null) {
				log.debug("副号码签约关系同步信息,在主号签约关系信息表中找到的主号号码为空！内部交易流水号:{},CRM应答流水:{}",
						new Object[] { transIDH, msgVoRsn.getTransIDH() });
				logger.debug("副号码签约关系同步信息,在主号签约关系信息表中找到的主号号码为空！内部交易流水号:{},CRM应答流水:{}",
						new Object[] { transIDH, msgVoRsn.getTransIDH() });
			}
			ProvincePhoneNum mainProvincePhoneNum = findProvinceByMobileNumber(mainPhoneInfo.getIdValue());
			String mainIdProvince = mainProvincePhoneNum == null ? null : mainProvincePhoneNum.getProvinceCode();
			// 主号码归属地、副号码归属地都相同，生成签约关系
			if (subProvince.equals(mainIdProvince)) {
				UpayCsysBindInfo subBindInfos = new UpayCsysBindInfo();
				subBindInfos.setSeqId(subBindInfo.getSeqId());
				subBindInfos.setSignCnlType(subBindMsgUpdateReqVo.getCnlTyp());
				subBindInfos.setSignOrgId(crmMsgVo.getMsgSender());
				subBindInfos.setMainFlag(CommonConstant.Mainflag.Slave
						.toString());
				subBindInfos.setSignSubTime(subBindMsgUpdateReqVo.getSubTime());// CRM应答端时间
				subBindInfos.setSignTxnDate(StrUtil.subString(
						crmMsgVo.getTransIDOTime(), 0, 8));
				subBindInfos.setSignTxnSeq(crmMsgVo.getTransIDO());// 签约交易流水号//TODO
				subBindInfos.setSignTxnTime(crmMsgVo.getTransIDOTime());
				subBindInfos.setSubId(subBindMsgUpdateReqVo.getSubID());
				subBindInfos.setMainIdProvince(mainIdProvince);
				subBindInfos.setSettleDate(subBindMsgUpdateReqVo
						.getActionDate());
				subBindInfos.setMainIdType(subBindMsgUpdateReqVo
						.getMainIDType());
				subBindInfos.setMainIdValue(subBindMsgUpdateReqVo
						.getMainIDValue());
				subBindInfos.setIdProvince(subProvince);
				subBindInfos.setIdType(subBindMsgUpdateReqVo.getIDType());
				subBindInfos.setIdValue(subBindMsgUpdateReqVo.getIDValue());
				subBindInfos.setPayType(" ");
				subBindInfos.setStatus(CommonConstant.BindStatus.Bind
						.getValue());
				subBindInfos.setLastUpdTime(transIDHTime);
				// 主号签约关系
				// 交易流水
				txnLog.setSubId(subBindMsgUpdateReqVo.getSubID());
				// txnLog.setSubTime(transIDHTime);
				txnLog.setSubTime(subBindMsgUpdateReqVo.getSubTime());

				txnLog.setIdProvince(subProvince);
				txnLog.setMainIdProvince(mainIdProvince);
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.toString());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
				txnLog.setSignStatus(CommonConstant.BindStatus.Bind.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
				upayCsysBindInfoService.modifyLogAndBinfInfo(subBindInfos,
						null, txnLog);
				Map<String, Object> mainPhoness = new HashMap<String, Object>();
				mainPhoness.put("subId", subBindMsgUpdateReqVo.getSubID());
				mainPhoness.put("mainIdValue",
						subBindMsgUpdateReqVo.getMainIDValue());
				mainPhoness.put("status",
						CommonConstant.BindStatus.Bind.getValue());
				mainPhoness.put("mainFlag",
						CommonConstant.Mainflag.Slave.toString());
				List<UpayCsysBindInfo> mainSubInfoss = upayCsysBindInfoService
						.findList(mainPhoness, null);
				mainPhoneInfo.setSubNum(mainSubInfoss.size());
				upayCsysBindInfoService.modify(mainPhoneInfo);
				// 返回应答给发起方
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.Success
						.toString());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
				res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
				res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
				res.setSubID(subBindMsgUpdateReqVo.getSubID());
				msgVoRtn.setBody(res);
				logger.info(
						"副号码签约关系同步信息,内部交易流水号:{},返回给发起方流水:{},业务发起方:{},省返回二级应答码:{},签约号码:{}",
						new Object[] { transIDH, msgVoRtn.getTransIDH(),
								paramData.getMsgSender(), res.getRspCode(),
								subBindInfo.getIdValue() });
				log.succ(
						"副号码签约关系同步信息,内部交易流水号:{},返回给发起方流水:{},业务发起方:{},省返回二级应答码:{},签约号码:{}",
						new Object[] { transIDH, msgVoRtn.getTransIDH(),
								paramData.getMsgSender(), res.getRspCode(),
								subBindInfo.getIdValue() });

				logger.debug("CrmSubConnectAction execute(Object) - end");
				return msgVoRtn;
			}
			txnLog.setRcvTransId(transIDH);
			txnLog.setRcvTransTm(transIDHTime);
			txnLog.setRcvTransDt(intTxnDate);
			logger.debug("开始校验机构权限");
			boolean orgFlag = true;
			orgFlag = orgStatusCheck(subOrgId);
			if (orgFlag) {
				UpayCsysRouteInfo routeInfo = new UpayCsysRouteInfo();
				CrmMsgVo subReq = new CrmMsgVo();
				subReq.setRouteInfo(routeInfo);
				subReq.setTransCode(transCode); // 交易代码
				subReq.setVersion(crmMsgVo.getVersion());
				subReq.setTestFlag(crmMsgVo.getTestFlag());
				subReq.setBIPCode(CommonConstant.Bip.Bis08.getValue());
				subReq.setActivityCode(CommonConstant.CrmTrans.Crm03.getValue());
				subReq.setActionCode(CommonConstant.ActionCode.Requset
						.getValue());
				subReq.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
				subReq.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
				subReq.setRouteType(CommonConstant.RouteType.RoutePhone
						.toString());
				//注释掉按省份路由
//				subReq.setRouteType(CommonConstant.RouteType.RouteProvince
//						.toString());
				subReq.setRouteValue(subBindMsgUpdateReqVo.getIDValue());
				subReq.setTransIDO(transIDH);
				subReq.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
				subReq.setSessionID(transIDH);
				subReq.setMsgSender(crmMsgVo.getMsgReceiver());
				subReq.setMsgReceiver(subOrgId);
				subReq.setPriority(null);
				subReq.setServiceLevel(null);
				// 请求报文体
				CrmSubBindMsgUpdateReqVo subBindMsgUpdateReqVo2 = new CrmSubBindMsgUpdateReqVo();
				subBindMsgUpdateReqVo2.setDealType(subBindMsgUpdateReqVo
						.getDealType());
				subBindMsgUpdateReqVo2.setSubID(subBindMsgUpdateReqVo
						.getSubID());
				subBindMsgUpdateReqVo2.setMainIDType(subBindMsgUpdateReqVo
						.getMainIDType());
				subBindMsgUpdateReqVo2.setMainIDValue(subBindMsgUpdateReqVo
						.getMainIDValue());
				subBindMsgUpdateReqVo2.setIDType(subBindMsgUpdateReqVo
						.getIDType());
				subBindMsgUpdateReqVo2.setIDValue(subBindMsgUpdateReqVo
						.getIDValue());
				
//				subBindMsgUpdateReqVo2.setTransactionID(Serial.genSerialNos(CommonConstant.Sequence.OprId.toString()));// 平台生成
				//TransactionID设置成32位
				subBindMsgUpdateReqVo2.setTransactionID(Serial.genSerialNum(CommonConstant.Sequence.OprId.toString()));// 平台生成
				subBindMsgUpdateReqVo2.setSubTime(subBindMsgUpdateReqVo
						.getSubTime());// 从内部取
				subBindMsgUpdateReqVo2.setActionDate(intTxnDate);
				subBindMsgUpdateReqVo2.setCnlTyp(subBindMsgUpdateReqVo
						.getCnlTyp());
				subReq.setBody(subBindMsgUpdateReqVo2);
				txnLog.setRcvRouteType(subReq.getRouteType());
				txnLog.setRcvRouteVal(subReq.getRouteValue());
				txnLog.setRcvDomain(subReq.getMsgReceiver());
				// 发往CRM的签约信息
				logger.info("副号码签约关系同步信息,intTxnSeq:{},开始发往crm 签约请求", transIDH);
				txnLog.setRcvStartTm(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				Map<String, Object> params = new HashMap<String, Object>();
				msgVoRsn = crmSubConnectBus.execute(subReq, params, txnLog,
						subBindInfo);
				txnLog.setRcvEndTm(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				logger.info("intTxnSeq:{},发往crm 签约请求结束，更新交易流水", transIDH);
				if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
						msgVoRsn.getRspCode())) {
					log.warn("副号码签约关系同步信息,网状网应答超时,内部交易流水号:{},CRM应答流水:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH() });
					logger.warn("副号码签约关系同步信息,网状网应答超时,内部交易流水号:{},CRM应答流水:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH() });
					CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
					UpayCsysBindInfo subBindInfos = new UpayCsysBindInfo();
					subBindInfos.setSignSubTime(subBindMsgUpdateReqVo
							.getSubTime());
					subBindInfos.setSignOrgId(crmMsgVo.getMsgSender());
					subBindInfos.setSeqId(subBindInfo.getSeqId());
					subBindInfos.setSignCnlType(subBindMsgUpdateReqVo.getCnlTyp());
					subBindInfos.setSignTxnDate(StrUtil.subString(
							crmMsgVo.getTransIDOTime(), 0, 8));
					subBindInfos.setSignTxnSeq(crmMsgVo.getTransIDO());// 签约交易流水号
					subBindInfos.setSignTxnTime(crmMsgVo.getTransIDOTime());
					subBindInfos.setSettleDate(subBindMsgUpdateReqVo
							.getActionDate());
					subBindInfos.setSubId(subBindMsgUpdateReqVo.getSubID());
					subBindInfos.setMainFlag(CommonConstant.Mainflag.Slave
							.getValue());
					subBindInfos.setStatus(CommonConstant.BindStatus.UnBind
							.getValue());
					subBindInfos.setMainIdProvince(mainIdProvince);
					subBindInfos.setMainIdType(subBindMsgUpdateReqVo
							.getMainIDType());
					subBindInfos.setMainIdValue(subBindMsgUpdateReqVo
							.getMainIDValue());
					subBindInfos.setPayType(" ");
					subBindInfos.setIdProvince(subProvince);
					subBindInfos.setIdType(subBindMsgUpdateReqVo.getIDType());
					subBindInfos.setIdValue(subBindMsgUpdateReqVo.getIDValue());
					subBindInfos.setLastUpdTime(transIDHTime);
					// 更新交易流水
					txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
					txnLog.setIdProvince(subProvince);
					txnLog.setMainIdProvince(mainIdProvince);
					txnLog.setRcvActivityCode(subReq.getActivityCode());
					txnLog.setRcvBipCode(subReq.getBIPCode());
					txnLog.setRcvCnlType(subBindMsgUpdateReqVo.getCnlTyp());
					txnLog.setRcvOprId(subBindMsgUpdateReqVo.getTransactionID());
					txnLog.setRcvOprDt(subBindMsgUpdateReqVo.getActionDate());
					txnLog.setRcvSessionId(msgVoRsn.getSessionID());
					txnLog.setRcvTranshId(msgVoRsn.getTransIDH());
					txnLog.setRcvTranshTm(msgVoRsn.getTransIDHTime());
					txnLog.setRcvTranshDt(StrUtil.subString(
							msgVoRsn.getTransIDHTime(), 0, 8));
					txnLog.setRcvRspCode(msgVoRsn.getRspCode());
					txnLog.setRcvRspDesc(msgVoRsn.getRspDesc());
					txnLog.setRcvRspType(msgVoRsn.getRspType());
					txnLog.setRcvSubRspCode(msgVoRsn.getRspCode());
					txnLog.setRcvSubRspDesc(msgVoRsn.getRspDesc());
					txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_5A07
							.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());// 发起方应答描述
					txnLog.setSubId(mainPhoneInfo.getSubId());
					txnLog.setSubTime(subBindMsgUpdateReqVo.getSubTime());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
					upayCsysBindInfoService.modifyLogAndBinfInfo(subBindInfos,
							mainPhoneInfo, txnLog);
					logger.debug("副号码签约关系同步信息,返回应答给发起方");
					msgVoRtn.setRspType(msgVoRsn.getRspType());
					msgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());
					msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
							.getValue());
					msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
					msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0,
							14));
					msgVoRtn.setBody(res);

					logger.debug("CrmSubConnectAction execute(Object) - end");
					return msgVoRtn;
				} else if (msgVoRsn.getBody() == null
						|| msgVoRsn.getBody().equals("")) {
					// else if (msgVoRsn.getBody() == null) {
					log.warn("副号码签约关系同步信息,网状网应答超时,内部交易流水号:{},CRM应答流水:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH() });
					logger.warn("副号码签约关系同步信息,网状网应答超时,内部交易流水号:{},CRM应答流水:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH() });
					String errCode = msgVoRsn.getRspCode();
					errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
					CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
					UpayCsysBindInfo subBindInfos = new UpayCsysBindInfo();
					subBindInfos.setSignSubTime(subBindMsgUpdateReqVo
							.getSubTime());
					subBindInfos.setSignOrgId(crmMsgVo.getMsgSender());
					subBindInfos.setSeqId(subBindInfo.getSeqId());
					subBindInfos.setSignCnlType(subBindMsgUpdateReqVo.getCnlTyp());
					subBindInfos.setSignTxnDate(StrUtil.subString(
							crmMsgVo.getTransIDOTime(), 0, 8));
					subBindInfos.setSettleDate(subBindMsgUpdateReqVo
							.getActionDate());
					subBindInfos.setSignTxnSeq(crmMsgVo.getTransIDO());// 签约交易流水号
					subBindInfos.setSignTxnTime(crmMsgVo.getTransIDOTime());
					subBindInfos.setSubId(subBindMsgUpdateReqVo.getSubID());
					subBindInfos.setMainFlag(CommonConstant.Mainflag.Slave
							.getValue());
					subBindInfos.setStatus(CommonConstant.BindStatus.UnBind
							.getValue());
					subBindInfos.setMainIdProvince(mainIdProvince);
					subBindInfos.setMainIdType(subBindMsgUpdateReqVo
							.getMainIDType());
					subBindInfos.setMainIdValue(subBindMsgUpdateReqVo
							.getMainIDValue());
					subBindInfos.setPayType(" ");
					subBindInfos.setIdProvince(subProvince);
					subBindInfos.setIdType(subBindMsgUpdateReqVo.getIDType());
					subBindInfos.setIdValue(subBindMsgUpdateReqVo.getIDValue());
					subBindInfos.setLastUpdTime(transIDHTime);
					// 更新交易流水
					txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
					txnLog.setIdProvince(subProvince);
					txnLog.setMainIdProvince(mainIdProvince);
					txnLog.setRcvActivityCode(subReq.getActivityCode());
					txnLog.setRcvBipCode(subReq.getBIPCode());
					txnLog.setRcvCnlType(subBindMsgUpdateReqVo.getCnlTyp());
					txnLog.setRcvOprId(subBindMsgUpdateReqVo.getTransactionID());
					txnLog.setRcvOprDt(subBindMsgUpdateReqVo.getActionDate());
					txnLog.setRcvSessionId(msgVoRsn.getSessionID());
					txnLog.setRcvTranshId(msgVoRsn.getTransIDH());
					txnLog.setRcvTranshTm(msgVoRsn.getTransIDHTime());
					txnLog.setRcvTranshDt(StrUtil.subString(
							msgVoRsn.getTransIDHTime(), 0, 8));
					txnLog.setRcvRspCode(msgVoRsn.getRspCode());
					txnLog.setRcvRspDesc(msgVoRsn.getRspDesc());
					txnLog.setRcvRspType(msgVoRsn.getRspType());
					txnLog.setRcvSubRspCode(msgVoRsn.getRspCode());
					// txnLog.setChlRspCode(MessageHandler.getWzwErrCode(errCode));
					// txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg(errCode));//
					// 发起方应答描述
					txnLog.setChlRspCode(errCode);
					txnLog.setChlRspDesc(RspCodeConstant.Wzw
							.getDescByValue(errCode));// 发起方应答描述
					txnLog.setSubId(mainPhoneInfo.getSubId());
					txnLog.setSubTime(subBindMsgUpdateReqVo.getSubTime());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
					upayCsysBindInfoService.modifyLogAndBinfInfo(subBindInfos,
							mainPhoneInfo, txnLog);
					logger.debug("副号码签约关系同步信息,返回应答给发起方");
					msgVoRtn.setRspType(msgVoRsn.getRspType());
					// msgVoRtn.setRspCode(MessageHandler.getWzwErrCode(errCode));
					// msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg(errCode));
					msgVoRtn.setRspCode(errCode);
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw
							.getDescByValue(errCode));
					msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
							.getValue());
					msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
					msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0,
							14));
					msgVoRtn.setBody(res);

					logger.debug("CrmSubConnectAction execute(Object) - end");
					return msgVoRtn;
				}
				CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
				MsgHandle.unmarshaller(res, (String) msgVoRsn.getBody());
				// 更新交易流水，签约关系
				if (msgVoRsn.getRspCode().equals(
						RspCodeConstant.Wzw.WZW_0000.getValue())
						&& (res.getRspCode())
								.equals(RspCodeConstant.Crm.CRM_0000.getValue())) {
					subBindInfo.setStatus(CommonConstant.BindStatus.Bind
							.getValue());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.getValue());
				} else {
					subBindInfo.setStatus(CommonConstant.BindStatus.UnBind
							.getValue());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
				}
				UpayCsysBindInfo subBindInfos = new UpayCsysBindInfo();
				subBindInfos.setSignSubTime(subBindMsgUpdateReqVo.getSubTime());
				subBindInfos.setSignOrgId(crmMsgVo.getMsgSender());
				subBindInfos.setSeqId(subBindInfo.getSeqId());
				subBindInfos.setSignCnlType(subBindMsgUpdateReqVo.getCnlTyp());
				subBindInfos.setSignTxnDate(StrUtil.subString(
						crmMsgVo.getTransIDOTime(), 0, 8));
				subBindInfos.setSettleDate(subBindMsgUpdateReqVo
						.getActionDate());
				subBindInfos.setSignTxnSeq(crmMsgVo.getTransIDO());// 签约交易流水号
				subBindInfos.setSignTxnTime(crmMsgVo.getTransIDOTime());
				subBindInfos.setSubId(subBindMsgUpdateReqVo.getSubID());
				subBindInfos.setMainFlag(CommonConstant.Mainflag.Slave
						.getValue());
				subBindInfos.setStatus(subBindInfo.getStatus());
				subBindInfos.setMainIdProvince(mainIdProvince);
				subBindInfos.setMainIdType(subBindMsgUpdateReqVo
						.getMainIDType());
				subBindInfos.setMainIdValue(subBindMsgUpdateReqVo
						.getMainIDValue());
				subBindInfos.setPayType(" ");
				subBindInfos.setIdProvince(subProvince);
				subBindInfos.setIdType(subBindMsgUpdateReqVo.getIDType());
				subBindInfos.setIdValue(subBindMsgUpdateReqVo.getIDValue());
				subBindInfos.setLastUpdTime(transIDHTime);
				// 更新交易流水
				txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
				txnLog.setIdProvince(subProvince);
				txnLog.setMainIdProvince(mainIdProvince);
				txnLog.setRcvActivityCode(subReq.getActivityCode());
				txnLog.setRcvBipCode(subReq.getBIPCode());
				txnLog.setRcvCnlType(subBindMsgUpdateReqVo.getCnlTyp());
				txnLog.setRcvOprId(subBindMsgUpdateReqVo2.getTransactionID());
				txnLog.setRcvOprDt(subBindMsgUpdateReqVo2.getActionDate());
				txnLog.setRcvSessionId(msgVoRsn.getSessionID());
				txnLog.setRcvTranshId(msgVoRsn.getTransIDH());
				txnLog.setRcvTranshTm(msgVoRsn.getTransIDHTime());
				txnLog.setRcvTranshDt(StrUtil.subString(
						msgVoRsn.getTransIDHTime(), 0, 8));
				txnLog.setRcvRspCode(msgVoRsn.getRspCode());
				txnLog.setRcvRspDesc(msgVoRsn.getRspDesc());
				txnLog.setRcvRspType(msgVoRsn.getRspType());
				txnLog.setRcvSubRspCode(msgVoRsn.getRspCode());
				txnLog.setChlRspCode(msgVoRsn.getRspCode());
				txnLog.setChlRspType(msgVoRsn.getRspType());
				txnLog.setChlRspDesc(msgVoRsn.getRspDesc());// 发起方应答描述
				//转换省返回码
//				txnLog.setChlSubRspCode(res.getRspCode());
//				txnLog.setChlSubRspDesc(res.getRspInfo());
				String subRtuCode = null;
				subRtuCode = RspCodeConstant.Crm.getCrmSecondValueByCode(res.getRspCode());
				txnLog.setChlSubRspCode(subRtuCode);
				txnLog.setChlSubRspDesc(res.getRspInfo());
				
				txnLog.setSubId(mainPhoneInfo.getSubId());
				txnLog.setSubTime(subBindMsgUpdateReqVo.getSubTime());
				upayCsysBindInfoService.modifyLogAndBinfInfo(subBindInfos,
						null, txnLog);
				Map<String, Object> mainPhoness = new HashMap<String, Object>();
				mainPhoness.put("subId", subBindMsgUpdateReqVo.getSubID());
				mainPhoness.put("mainIdValue",
						subBindMsgUpdateReqVo.getMainIDValue());
				mainPhoness.put("status",
						CommonConstant.BindStatus.Bind.getValue());
				mainPhoness.put("mainFlag",
						CommonConstant.Mainflag.Slave.toString());
				List<UpayCsysBindInfo> mainSubInfoss = upayCsysBindInfoService
						.findList(mainPhoness, null);
				mainPhoneInfo.setSubNum(mainSubInfoss.size());
				upayCsysBindInfoService.modify(mainPhoneInfo);
				logger.debug("副号码签约关系同步信息,返回应答给发起方");
				if (res.getRspCode().equals(
						RspCodeConstant.Crm.CRM_0000.getValue())) {
					logger.info(
							"副号码签约关系同步信息,内部交易流水号:{},业务发起方:{},副号省应答流水:{},CRM二级应答码:{},应答描述:{}",
							new Object[] { transIDH, paramData.getMsgSender(),
									msgVoRsn.getTransIDH(), res.getRspCode(),
									res.getRspInfo() });
					log.succ(
							"副号码签约关系同步信息,内部交易流水号:{},业务发起方:{},副号省应答流水:{},CRM二级应答码:{},应答描述:{}",
							new Object[] { transIDH, paramData.getMsgSender(),
									msgVoRsn.getTransIDH(), res.getRspCode(),
									res.getRspInfo() });
					msgVoRtn.setRspType(txnLog.getChlRspType());
					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
				} else {
//					logger.warn(
//							"副号码签约关系同步信息,内部交易流水号:{},业务发起方:{},副号省应答流水:{},CRM二级应答码:{},应答描述:{}",
//							new Object[] { transIDH, paramData.getMsgSender(),
//									msgVoRsn.getTransIDH(), res.getRspCode(),
//									res.getRspInfo() });
//					log.warn(
//							"副号码签约关系同步信息,内部交易流水号:{},业务发起方:{},副号省应答流水:{},CRM二级应答码:{},应答描述:{}",
//							new Object[] { transIDH, paramData.getMsgSender(),
//									msgVoRsn.getTransIDH(), res.getRspCode(),
//									res.getRspInfo() });
					logger.warn(
					"副号码签约关系同步信息,内部交易流水号:{},业务发起方:{},副号省应答流水:{},CRM二级应答码:{},应答描述:{}",
					new Object[] { transIDH, paramData.getMsgSender(),
							msgVoRsn.getTransIDH(), subRtuCode,
							res.getRspInfo() });
					log.warn(
					"副号码签约关系同步信息,内部交易流水号:{},业务发起方:{},副号省应答流水:{},CRM二级应答码:{},应答描述:{}",
					new Object[] { transIDH, paramData.getMsgSender(),
							msgVoRsn.getTransIDH(), subRtuCode,
							res.getRspInfo() });
					msgVoRtn.setRspType(txnLog.getChlRspType());
					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				}
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
				//转换省二级返回码
//				res.setRspCode(res.getRspCode());
//				res.setRspInfo(RspCodeConstant.Crm.getDescByValue(res
//						.getRspCode()));
				res.setRspCode(subRtuCode);
				res.setRspInfo(RspCodeConstant.Crm.getCrmSecondDescByCode(subRtuCode));
				
				res.setSubID(res.getSubID());
				res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
				msgVoRtn.setBody(res);

				logger.debug("CrmSubConnectAction execute(Object) - end");
				return msgVoRtn;
			} else {
				logger.warn(
						"副号码签约关系同步信息,接收方机构关闭:{},内部交易流水号:{},业务发起方:{}",
						new Object[] { subOrgId, transIDH,
								paramData.getMsgSender() });
				log.warn(
						"副号码签约关系同步信息,接收方机构关闭:{},内部交易流水号:{},业务发起方:{}",
						new Object[] { subOrgId, transIDH,
								paramData.getMsgSender() });
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_3A35.getDesc());
				res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
				res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
				res.setSubID(subBindMsgUpdateReqVo.getSubID());
				msgVoRtn.setBody(res);
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A35.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);

				logger.debug("CrmSubConnectAction execute(Object) - end");
				return msgVoRtn;
			}
		} catch (AppRTException e) {
			log.error("副号码签约关系同步信息,内部异常!内部交易流水号:{},业务发起方:{}", new Object[] {
					transIDH, paramData.getMsgSender() });
			logger.error(
					"副号码签约关系同步信息,内部异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { e.getCode(), transIDH,
							paramData.getMsgSender() });
			logger.error("副号码签约关系同步信息,内部异常:", e);
			String errCode = e.getCode();
//			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			errCode = RspCodeConstant.Crm.getCrmSecondValueByCode(errCode);
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
			msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
			CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
			res.setRspCode(errCode);
//			res.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			res.setRspInfo(RspCodeConstant.Crm.getCrmSecondDescByCode(errCode));
			res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
			res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
			res.setSubID(subBindMsgUpdateReqVo.getSubID());
			msgVoRtn.setBody(res);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			upayCsysTxnLogService.modify(txnLog);

			logger.debug("CrmSubConnectAction execute(Object) - end");
			return msgVoRtn;
		} catch (AppBizException e) {
			log.error("副号码签约关系同步信息,业务异常!内部交易流水号:{},业务发起方:{}", new Object[] {
					transIDH, paramData.getMsgSender() });
			logger.error(
					"副号码签约关系同步信息,业务异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { e.getCode(), transIDH,
							paramData.getMsgSender() });
			logger.error("副号码签约关系同步信息,业务异常:", e);
			String errCode = e.getCode();
//			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			errCode = RspCodeConstant.Crm.getCrmSecondValueByCode(errCode);
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
			msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
			CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
			res.setRspCode(errCode);
//			res.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			res.setRspInfo(RspCodeConstant.Crm.getCrmSecondDescByCode(errCode));
			res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
			res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
			res.setSubID(subBindMsgUpdateReqVo.getSubID());
			msgVoRtn.setBody(res);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			upayCsysTxnLogService.modify(txnLog);

			logger.debug("CrmSubConnectAction execute(Object) - end");
			return msgVoRtn;
		} catch (Exception e) {
			log.error("副号码签约关系同步信息,未知异常!内部交易流水号:{},业务发起方:{}", new Object[] {
					transIDH, paramData.getMsgSender() });

			logger.error("副号码签约关系同步信息,未知异常!内部交易流水号:{},业务发起方:{}", new Object[] {
					transIDH, paramData.getMsgSender() });
			logger.error("副号码签约关系同步信息,未知异常:", e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A06.getDesc()
					+ ":" + e.getMessage());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			upayCsysTxnLogService.modify(txnLog);
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
			msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
			CrmSubBindMsgUpdateResVo res = new CrmSubBindMsgUpdateResVo();
			res.setRspCode(RspCodeConstant.Crm.CRM_5A06.getValue());
			
			//注释掉输出到应答报文的错误信息(该信息可能包含SQL异常) 20131213 modify by weiyi
//			String errDesc = e.getMessage().length() <= ExcConstant.MSG_LENGTH_230 ? e
//					.getMessage() : e.getMessage().substring(0,
//					ExcConstant.MSG_LENGTH_230);
//			res.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc() + ":"
//					+ errDesc);
			
			res.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc());
			
			res.setTransactionID(subBindMsgUpdateReqVo.getTransactionID());
			res.setActionDate(subBindMsgUpdateReqVo.getActionDate());
			res.setSubID(subBindMsgUpdateReqVo.getSubID());
			msgVoRtn.setBody(res);

			logger.debug("CrmSubConnectAction execute(Object) - end");
			return msgVoRtn;

		}
	}
}