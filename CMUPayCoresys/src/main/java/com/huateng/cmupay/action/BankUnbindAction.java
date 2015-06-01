package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.cache.TransCodeCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmMainUnbindBus;
import com.huateng.cmupay.jms.business.crm.CrmSubUnbindBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankUnbindReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankUnbindResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMainMobileUnBindReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMainMobileUnBindResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubUnbindReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubUnbindResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 银行渠道主号码解约
 * 
 * @author zeng.j
 * 
 */
@Controller("bankUnbindAction")
@Scope("prototype")
public class BankUnbindAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {

	@Autowired
	private CrmSubUnbindBus crmSubUnbindBus;

	@Autowired
	private CrmMainUnbindBus crmMainUnbindBus;

	@Override
	public BankMsgVo execute(BankMsgVo paramData) throws AppBizException {
		logger.debug("BankUnbindAction execute(Object) - start");
		// 请求报文
		BankMsgVo reqMsg = paramData;
		BankUnbindReqVo reqBody = new BankUnbindReqVo();
		BankUnbindResVo resBody = new BankUnbindResVo();
		BankMsgVo resMsg = new BankMsgVo();
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		UpayCsysBindInfo mainBindInfo = null;
		String transIDH = reqMsg.getTxnSeq();
		String transIDHTime = null;
		String intTxnDate = null;
		Long seqId = null;
		String lSeqId = null;

		try {
			resMsg.setActionCode(CommonConstant.ActionCode.Respone.toString());
			resMsg.setReqSys(reqMsg.getReqSys());
			resMsg.setReqChannel(reqMsg.getReqChannel());
			resMsg.setReqDate(reqMsg.getReqDate());
			resMsg.setReqTransID(reqMsg.getReqTransID());
			resMsg.setReqDateTime(reqMsg.getReqDateTime());
			resMsg.setActivityCode(reqMsg.getActivityCode());
			resMsg.setRcvSys(reqMsg.getRcvSys());
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			// lSeqId = reqMsg.getReqTransID();
			reqMsg.setBody(reqBody);
			/** 交易代码 */
			UpayCsysTransCode transCode = reqMsg.getTransCode();
			logger.debug("检查订单状态,intTxnSeq:{}", new Object[] { transIDH });
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("reqDomain", reqMsg.getReqSys());
			params1.put("reqTransId", reqMsg.getReqTransID());
			txnLog = upayCsysTxnLogService.findObj(params1);
			if (txnLog != null) {
				intTxnDate = txnLog.getIntTxnDate();
				transIDHTime = txnLog.getIntTxnTime();
				seqId = txnLog.getSeqId();
				transIDH = txnLog.getIntTxnSeq();// 落地方交易流水号
				resMsg.setRcvDate(intTxnDate);
				resMsg.setRcvDateTime(transIDHTime);
				resMsg.setRcvTransID(transIDH);
				resMsg.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());

			} else {
				// 响应报文
				transIDHTime = paramData.getTxnTime();
				intTxnDate = reqMsg.getTxnDate();
				seqId = reqMsg.getSeqId();
				resMsg.setRcvDate(intTxnDate);
				resMsg.setRcvDateTime(transIDHTime);
				resMsg.setRcvTransID(transIDH);
				resMsg.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());

				/** 交易流水 */
				txnLog = new UpayCsysTxnLog();
				txnLog.setSeqId(seqId);
				txnLog.setIntTxnSeq(transIDH);
				txnLog.setIntTxnDate(intTxnDate);
				txnLog.setIntTxnTime(transIDHTime);
				txnLog.setIntMqSeq(reqMsg.getMqSeq());
				// txnLog.setTxnCat(transCode.getTxnCat());
				txnLog.setBussType(transCode.getBussType());
				txnLog.setIntTransCode(transCode.getTransCode());
				txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No
						.toString());
				txnLog.setPayMode(transCode.getPayMode());
				txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
				txnLog.setReqActivityCode(reqMsg.getActivityCode());
				txnLog.setReqCnlType(reqMsg.getReqChannel());
				txnLog.setReqDomain(reqMsg.getReqSys());
				txnLog.setBussChl(transCode.getBussChl());
				txnLog.setSettleDate(reqMsg.getReqDate());
				txnLog.setReqTransId(reqMsg.getReqTransID());
				txnLog.setReqTransDt(reqMsg.getReqDate());
				txnLog.setReqTransTm(reqMsg.getReqDateTime());
				txnLog.setReqTranshDt(intTxnDate);
				txnLog.setReqTranshId(transIDH);
				txnLog.setReqTranshTm(transIDHTime);
				txnLog.setReqOprDt(reqMsg.getReqDate());
				txnLog.setReqOprId(reqMsg.getReqTransID());
				txnLog.setReqOprTm(reqMsg.getReqDateTime());
				txnLog.setBankId(reqMsg.getReqSys());
				txnLog.setIdType(reqBody.getIDType());
				txnLog.setIdValue(reqBody.getIDValue());
				txnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
				txnLog.setSubId(reqBody.getSubID());
				txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				txnLog.setSettleDate(reqMsg.getReqDate());
				logger.debug("intTxnSeq:{},begin 添加银行渠道主号码解约交易流水", transIDH);
				upayCsysTxnLogService.add(txnLog);
				logger.debug("intTxnSeq:{},end 添加银行渠道主号码解约交易流水", transIDH);
			}
			lSeqId = String.valueOf(txnLog.getSeqId());// 用于副号码解约流水中关联主号码交易流水
			/* 验证消息 */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
//				log.warn("银行交易结果查询接口!报文格式错误:{},内部交易流水号:{},业务发起方:{}", new Object[] {
//						checkrtn, transIDH, reqMsg.getReqSys() });
//				logger.warn("银行交易结果查询接口!报文格式错误:{},内部交易流水号:{},业务发起方:{}", new Object[] {
//						checkrtn, transIDH, reqMsg.getReqSys() });
//				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A05
//						.getValue());
//				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A05.getDesc()
//						+ checkrtn);
//				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
//						.getValue());
//				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A05
//						.getValue());
//				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A05
//						.getDesc() + checkrtn);
//				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
//				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
//						.getDateyyyyMMddHHmmssSSS());
//				upayCsysTxnLogService.modify(txnLog);
//				logger.debug("BankUnbindAction execute(Object) - end");
//				return fail(resMsg, resBody,
//						RspCodeConstant.Bank.BANK_015A05.getValue(),
//						RspCodeConstant.Bank.BANK_015A05.getDesc() + checkrtn);
				
				String code = checkrtn.split(":")[0];
				log.warn("银行交易结果查询接口!报文格式错误:{},内部交易流水号:{},业务发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys() });
				logger.warn("银行交易结果查询接口!报文格式错误:{},内部交易流水号:{},业务发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys() });
				txnLog.setChlRspCode(code);
				txnLog.setChlRspDesc(checkrtn);
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.getValue());
				txnLog.setChlSubRspCode(code);
				txnLog.setChlSubRspDesc(checkrtn);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("BankUnbindAction execute(Object) - end");
				return fail(resMsg, resBody, code, checkrtn);
			}
			txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			/* 主号码签约信息 */
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idValue", reqBody.getIDValue().trim());
			params.put("mainFlag", CommonConstant.Mainflag.Master.getValue());
			params.put("subId", reqBody.getSubID().trim());
			mainBindInfo = upayCsysBindInfoService.findObj(params);
			if (null == mainBindInfo
					|| !CommonConstant.BindStatus.Bind.getValue().equals(
							mainBindInfo.getStatus())) {
				log.warn("银行交易结果查询接口!签约信息不存在,主号码:{},内部交易流水号:{},业务发起方:{}", new Object[] {
						reqBody.getIDValue(), transIDH, reqMsg.getReqSys() });
				logger.warn(
						"主号码签约信息不存在,主号码:{},内部交易流水号:{},业务发起方:{}",
						new Object[] { reqBody.getIDValue(), transIDH,
								reqMsg.getReqSys() });
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A09
						.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A09.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A09
						.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A09
						.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("BankUnbindAction execute(Object) - end");
				return fail(resMsg, resBody,
						RspCodeConstant.Bank.BANK_012A09.getValue(),
						RspCodeConstant.Bank.BANK_012A09.getDesc());
			}
			txnLog.setUserCat(mainBindInfo.getUserCat());
			txnLog.setBankAcctType(mainBindInfo.getBankAcctType());
			txnLog.setBankAccId(mainBindInfo.getBankAccId());
			txnLog.setUserType(mainBindInfo.getUserType());
			txnLog.setUserId(mainBindInfo.getUserId());
			txnLog.setUserName(mainBindInfo.getUserName());
			txnLog.setPayType(mainBindInfo.getPayType());
			txnLog.setRechAmount(mainBindInfo.getRechAmount());
			txnLog.setRechThreshold(mainBindInfo.getRechThreshold());
			txnLog.setIdProvince(mainBindInfo.getIdProvince());
			txnLog.setSubTime(mainBindInfo.getSignSubTime());
			/* 验证银行 */
			if (!mainBindInfo.getBankId().equals(reqMsg.getReqSys())) {
				log.warn("银行交易结果查询接口!主号码:{}签约信息为其它银行:{}账号关联,内部交易流水:{}", new Object[] {
						reqBody.getIDValue(), mainBindInfo.getBankId(),
						transIDH });
				logger.warn("银行交易结果查询接口!主号码:{}签约信息为其它银行:{}账号关联,内部交易流水:{}", new Object[] {
						reqBody.getIDValue(), mainBindInfo.getBankId(),
						transIDH });
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A00
						.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A00.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A00
						.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A00
						.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				resBody.setBankID(mainBindInfo.getBankId());
				logger.debug("BankUnbindAction execute(Object) - end");
				return fail(resMsg, resBody,
						RspCodeConstant.Bank.BANK_012A00.getValue(),
						RspCodeConstant.Bank.BANK_012A00.getDesc());
			}

			String mainIdProvince = mainBindInfo.getIdProvince();

			/** 主号码的副号码签约信息列表 */
			Map<String, Object> subParams = new HashMap<String, Object>();
			subParams.put("mainIdValue", reqBody.getIDValue());
			subParams.put("mainFlag", CommonConstant.Mainflag.Slave.toString());
			subParams.put("isHistory",
					CommonConstant.IsHistory.Normal.getValue());
			subParams.put("status",
					CommonConstant.TxnStatus.TxnSuccess.toString());
			List<UpayCsysBindInfo> subBindInfos = upayCsysBindInfoService
					.findList(subParams, null);
			/** 副号码处理 */
			if (subBindInfos != null && subBindInfos.size() > 0) {

				logger.debug("intTxnSeq:{} ,begin 解约副号码", transIDH);
				String reqVersion = ExcConstant.CRM_VERSION;
				for (UpayCsysBindInfo info : subBindInfos) {
					String subTransId2Sub = Serial
							.genSerialNos(CommonConstant.Sequence.OprId
									.getValue());
					String subIdProvince = info.getIdProvince();
					String subOrgId = SysMapCache.getProvCd(subIdProvince)
							.getSysCd();

					/** 交易代码 */
					UpayCsysTransCode subTransCode = TransCodeCache
							.getTransCode(CommonConstant.Bip.Bis09.getValue(),
									CommonConstant.CrmTrans.Crm03.getValue());
					// UpayCsysTransCode subTransCode = reqMsg.getTransCode();
					Long subSeqId2Sub = upayCsysSeqMapInfoService
							.selectSeqValue(ExcConstant.TXN_LOG_SEQ);// log key
					String subIntTxnDate = upayCsysBatCutCtlService
							.findCutOffDate(ExcConstant.CUT_OFF_DATE);// 数据库时间
					String subTransIDO2Sub = Serial
							.genSerialNo(CommonConstant.Sequence.IntSeq
									.getValue());//
					String subTransIDOTime = StrUtil.subString(transIDHTime, 0,
							14);

					info.setStatus(CommonConstant.BindStatus.UnBind.getValue());
					info.setClSubTime(subTransIDOTime);
					info.setClCnlType(reqMsg.getReqChannel());
					info.setClOrgId(reqMsg.getReqSys());
					info.setClTxnDate(subIntTxnDate);
					info.setClTxnSeq(String.valueOf(subSeqId2Sub));
					info.setClTxnTime(subTransIDOTime);
					info.setSettleDate(reqMsg.getReqDate());
					upayCsysBindInfoService.modify(info);
					// upayCsysBindInfoService.del(info);
					log.succ("解约副号码:{} 成功,内部交易流水:{},业务发起方:{}",
							new Object[] { info.getIdValue(), transIDH,
									paramData.getReqSys() });
					logger.info("解约副号码:{} 成功,内部交易流水:{},业务发起方:{}",
							new Object[] { info.getIdValue(), transIDH,
									paramData.getReqSys() });
					// Map<String, Object> noPrams = new HashMap<String,
					// Object>();
					// noPrams.put("mainFlag",
					// CommonConstant.Mainflag.Slave.getValue());
					// noPrams.put("mainIdValue", mainBindInfo.getIdValue());
					// noPrams.put("status",
					// CommonConstant.BindStatus.Bind.getValue());
					// noPrams.put("subId", mainBindInfo.getSubId());
					// int subNum = upayCsysBindInfoService
					// .findSubCountByParams(noPrams);
					// mainBindInfo.setSubNum(subNum);
					// upayCsysBindInfoService.modify(mainBindInfo);
					// log.succ("修改主号码:{}签约副号码数量成功,内部交易流水:{},业务发起方:{}",
					// new Object[] { mainBindInfo.getIdValue(), transIDH,
					// paramData.getReqSys() });
					// logger.info("修改主号码:{}签约副号码数量成功,内部交易流水:{},业务发起方:{}",
					// new Object[] { mainBindInfo.getIdValue(), transIDH,
					// paramData.getReqSys() });

					try {
						// 向副号码归属省发起副号码解约
						log.info("向副号码归属省:{}发起副号码:{}解约,内部交易流水号:{},发起方系统:{}",
								new Object[] { subIdProvince,
										info.getIdValue(), subTransId2Sub,
										paramData.getReqSys() });
						logger.info("向副号码归属省:{}发起副号码:{}解约,内部交易流水号:{},发起方系统:{}",
								new Object[] { subIdProvince,
										info.getIdValue(), subTransId2Sub,
										paramData.getReqSys() });
						sendCrmSubProvSubUnbind(subTransCode, reqVersion, info,
								subTransIDO2Sub, subTransIDOTime,
								subTransId2Sub, reqMsg, subOrgId, subSeqId2Sub,
								subIntTxnDate, paramData, lSeqId, transIDH);
					} catch (Exception e) {
						log.error(
								"向副号码归属省:{}发起副号码:{}解约异常,内部交易流水号:{},发起方系统:{}",
								new Object[] { subIdProvince,
										info.getIdValue(), transIDH,
										paramData.getReqSys() });
						logger.error(
								"向副号码归属省:{}发起副号码:{},内部交易流水号:{},发起方系统:{}",
								new Object[] { subIdProvince,
										info.getIdValue(), transIDH,
										paramData.getReqSys() });
						logger.error("银行交易结果查询接口!解约异常:", e);
					}

					if (!mainIdProvince.trim().equals(subIdProvince.trim())) {
						// 向主号码归属省发起副号码解约
						String subTransIDO2Main = Serial
								.genSerialNo(CommonConstant.Sequence.IntSeq
										.getValue());
						String subTransId2Main = Serial
								.genSerialNos(CommonConstant.Sequence.OprId
										.getValue());
						String mainOrgId = SysMapCache.getProvCd(
								mainBindInfo.getIdProvince()).getSysCd();
						Long subSeqId2Main = upayCsysSeqMapInfoService
								.selectSeqValue(ExcConstant.TXN_LOG_SEQ);
						try {
							log.info(
									"向主号码归属省:{}发起副号码:{}解约内部交易流水号:{},发起方系统:{}",
									new Object[] { mainIdProvince,
											info.getIdValue(), subTransId2Main,
											paramData.getReqSys() });
							logger.info(
									"向主号码归属省:{}发起副号码:{}解约内部交易流水号:{},发起方系统:{}",
									new Object[] { mainIdProvince,
											info.getIdValue(), subTransId2Main,
											paramData.getReqSys() });
							sendCrmMainProvSubUnbind(subTransCode, reqVersion,
									mainBindInfo, subTransIDO2Main,
									subTransIDOTime, info, subTransId2Main,
									reqMsg, mainOrgId, subSeqId2Main,
									subIntTxnDate, paramData, lSeqId, transIDH);
						} catch (Exception e) {
							log.error(
									"向主号码归属省:{}发起副号码:{}解约异常,内部交易流水号:{},发起方系统:{}",
									new Object[] { mainIdProvince,
											info.getIdValue(), transIDH,
											paramData.getReqSys() });
							logger.error(
									"向主号码归属省:{}发起副号码:{},内部交易流水号:{},发起方系统:{}",
									new Object[] { mainIdProvince,
											info.getIdValue(), transIDH,
											paramData.getReqSys() });
							logger.error("银行交易结果查询接口!解约异常:", e);
						}
					}
				}
				modifySubNum(mainBindInfo);
				log.succ("修改主号码:{}签约副号码数量成功,内部交易流水:{},业务发起方:{}",
						new Object[] { mainBindInfo.getIdValue(), transIDH,
								paramData.getReqSys() });
				logger.info("修改主号码:{}签约副号码数量成功,内部交易流水:{},业务发起方:{}",
						new Object[] { mainBindInfo.getIdValue(), transIDH,
								paramData.getReqSys() });
			}

			String mainUnbindVersion = ExcConstant.CRM_VERSION;
			mainBindInfo.setStatus(CommonConstant.BindStatus.UnBind.getValue());
			mainBindInfo.setClCnlType(reqMsg.getReqChannel());
			mainBindInfo.setClTxnSeq(reqMsg.getReqTransID());
			mainBindInfo.setClTxnDate(reqMsg.getReqDate());
			mainBindInfo.setClTxnTime(reqMsg.getReqDateTime());
			mainBindInfo.setClOrgId(reqMsg.getReqSys());
			mainBindInfo.setClSubTime(transIDHTime);
			mainBindInfo.setSettleDate(reqMsg.getReqDate());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());

			upayCsysBindInfoService.modifySelectiveBindInfoAndLog(mainBindInfo,
					txnLog);
			// upayCsysBindInfoService.modifyTxnLogAndDelBindInfo(mainBindInfo,
			// txnLog);
			log.succ("解约主号码:{} 成功,内部交易流水号:{},发起方系统:{}",
					new Object[] { mainBindInfo.getIdValue(), transIDH,
							paramData.getReqSys() });
			logger.info("解约主号码:{} 成功,内部交易流水号:{},发起方系统:{}",
					new Object[] { mainBindInfo.getIdValue(), transIDH,
							paramData.getReqSys() });

			/** 报文头 */
			String msgReceiver = SysMapCache.getProvCd(
					mainBindInfo.getIdProvince()).getSysCd();
			CrmMsgVo forwardMsg = new CrmMsgVo();
			forwardMsg.setTransCode(transCode);
			forwardMsg.setVersion(mainUnbindVersion);
			forwardMsg.setTestFlag(testFlag);
			forwardMsg.setBIPCode(CommonConstant.Bip.Bis06.getValue());
			forwardMsg
					.setActivityCode(CommonConstant.CrmTrans.Crm02.getValue());
			forwardMsg.setActionCode(CommonConstant.ActionCode.Requset
					.getValue());
			forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone
					.getValue());
			forwardMsg.setRouteValue(mainBindInfo.getIdValue());
			forwardMsg.setSessionID(transIDH);
			forwardMsg.setTransIDO(transIDH);
			forwardMsg.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));

			CrmMainMobileUnBindReqVo forwardBody = new CrmMainMobileUnBindReqVo();
			CrmMainMobileUnBindResVo forwardBodyRtn = new CrmMainMobileUnBindResVo();
			forwardBody.setDealType(CommonConstant.BindDealType.Unbind
					.getValue());
			forwardBody.setSubID(reqBody.getSubID());
			forwardBody.setIDType(reqBody.getIDType());
			forwardBody.setIDValue(reqBody.getIDValue());
			forwardBody.setBankID(mainBindInfo.getBankId());
			forwardBody.setBankAcctType(mainBindInfo.getBankAcctType());
			forwardBody.setBankAcctID(mainBindInfo.getBankAccId());
			String forwardOprId = Serial
					.genSerialNos(CommonConstant.Sequence.OprId.getValue());
			forwardBody.setTransactionID(forwardOprId);
			forwardBody.setSubTime(mainBindInfo.getSignSubTime());
			forwardBody.setActionDate(DateUtil.getDateyyyyMMdd());
			forwardBody.setUserIDType(mainBindInfo.getUserType());
			forwardBody.setUserID(mainBindInfo.getUserId());
			forwardBody.setUserName(mainBindInfo.getUserName());
			forwardBody.setCnlTyp(reqMsg.getReqChannel());
			forwardBody.setPayType(mainBindInfo.getPayType());
			forwardBody.setRechAmount(mainBindInfo.getRechAmount());
			forwardBody.setRechThreshold(mainBindInfo.getRechThreshold());

			forwardMsg.setBody(forwardBody);
			forwardMsg.setMsgReceiver(msgReceiver);
			txnLog.setUserCat(forwardBody.getUserCat());
			txnLog.setBankAcctType(forwardBody.getBankAcctType());
			txnLog.setBankAccId(forwardBody.getBankAcctID());
			txnLog.setUserType(forwardBody.getUserIDType());
			txnLog.setUserId(forwardBody.getUserID());
			txnLog.setUserName(forwardBody.getUserName());
			txnLog.setPayType(forwardBody.getPayType());
			txnLog.setRechAmount(forwardBody.getRechAmount());
			txnLog.setRechThreshold(forwardBody.getRechThreshold());
			txnLog.setRcvTransDt(StrUtil.subString(
					forwardMsg.getTransIDOTime(), 0, 8));
			txnLog.setRcvTransId(forwardMsg.getTransIDO());
			txnLog.setRcvSessionId(forwardMsg.getSessionID());
			txnLog.setRcvTransTm(forwardMsg.getTransIDOTime());
			txnLog.setRcvOprDt(forwardBody.getActionDate());
			txnLog.setRcvOprId(forwardBody.getTransactionID());
			txnLog.setRcvOprTm(forwardMsg.getTransIDOTime());
			txnLog.setRcvActivityCode(forwardMsg.getActivityCode());
			txnLog.setRcvDomain(forwardMsg.getMsgReceiver());
			txnLog.setRcvBipCode(forwardMsg.getBIPCode());
			txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
			txnLog.setRcvRouteType(forwardMsg.getRouteType());
			txnLog.setRcvRouteVal(forwardMsg.getRouteValue());
			txnLog.setRcvOprTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			if (orgStatusCheck(msgReceiver)) {
				forwardMsg = crmMainUnbindBus.execute(forwardMsg, null, null,
						null);
				MsgHandle.unmarshaller(forwardBodyRtn,
						(String) forwardMsg.getBody());
				txnLog.setRcvRspType(forwardMsg.getRspType());
				txnLog.setRcvRspCode(forwardMsg.getRspCode());
				txnLog.setRcvRspDesc(forwardMsg.getRspDesc());
			}
			logger.debug("intTxnSeq:{}, retrun 主号码解约请求", transIDH);
			txnLog.setRcvTranshId(forwardMsg.getTransIDH());
			txnLog.setRcvTranshTm(forwardMsg.getTransIDHTime());
			txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setRcvTranshDt(StrUtil.subString(
					forwardMsg.getTransIDHTime(), 0, 8));
			txnLog.setRcvRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setRcvRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setRcvCnlType(reqMsg.getReqChannel());
			if ((forwardBodyRtn != null && !"".equals(forwardBodyRtn))
					&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
							forwardBodyRtn.getRspCode())) {
				log.succ("银行返回主号码:{}解约成功,内部交易流水号:{},发起方系统:{}",
						new Object[] { mainBindInfo.getIdValue(), transIDH,
								paramData.getReqSys() });
				logger.info("银行返回主号码:{}解约成功,内部交易流水号:{},发起方系统:{}",
						new Object[] { mainBindInfo.getIdValue(), transIDH,
								paramData.getReqSys() });
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00
						.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00
						.getValue());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00
						.getDesc());

				txnLog.setRcvSubRspCode(forwardBodyRtn.getRspCode());
				txnLog.setRcvSubRspDesc(forwardBodyRtn.getRspInfo());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
			} else {
				log.warn(
						"省:{}返回主号码:{}解约失败,内部交易流水号:{},发起方系统:{},省响应码:{}",
						new Object[] { forwardMsg.getMsgReceiver(),
								mainBindInfo.getIdValue(), transIDH,
								paramData.getReqSys(),
								forwardBodyRtn.getRspCode() });
				logger.warn(
						"省:{}返回主号码:{}解约失败,内部交易流水号:{},发起方系统:{},省响应码:{}",
						new Object[] { forwardMsg.getMsgReceiver(),
								mainBindInfo.getIdValue(), transIDH,
								paramData.getReqSys(),
								forwardBodyRtn.getRspCode() });
			}
			upayCsysTxnLogService.modify(txnLog);
			/**
			 * 解约以发起方为准，无论省解约是否成功，都返回银行成功
			 */
			logger.debug("BankUnbindAction execute(Object) - end,intTxnSeq:{}",
					new Object[] { transIDH });
			logger.debug("BankUnbindAction execute(Object) - end");
			return succ(resMsg, resBody);
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.error("银行交易结果查询接口!系统异常!内部交易流水号:{},业务发起方:{}", new Object[] { transIDH,
					reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!系统异常,代码:{},内部交易流水号:{},业务发起方:{}", new Object[] {
					errCode, transIDH, reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!系统异常:", e);
			if (isUnbindSuccess(mainBindInfo)) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			} else {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			}
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());

			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank
					.getDescByValue(errCode));
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			logger.debug("BankUnbindAction execute(Object) - end");
			return succ(resMsg, resBody);
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			log.error("银行交易结果查询接口!系统异常!内部交易流水号:{},业务发起方:{}", new Object[] { transIDH,
					reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!系统异常,代码:{},内部交易流水号:{},业务发起方:{}", new Object[] {
					errCode, transIDH, reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!系统异常:", e);
			if (isUnbindSuccess(mainBindInfo)) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			} else {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			}
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank
					.getDescByValue(errCode));
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			logger.debug("BankUnbindAction execute(Object) - end");
			return succ(resMsg, resBody);
		} catch (Exception e) {
			log.error("银行交易结果查询接口!系统异常!内部交易流水号:{},业务发起方:{}", new Object[] { transIDH,
					reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!系统异常!内部交易流水号:{},业务发起方:{}", new Object[] { transIDH,
					reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!系统异常:", e);
			if (isUnbindSuccess(mainBindInfo)) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			} else {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			}
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			// String errCode = "015A06";
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			logger.debug("BankUnbindAction execute(Object) - end");
			return succ(resMsg, resBody);
		}

	}

	/**
	 * 向副号码省发起副号码解约
	 * 
	 * @param subTransCode
	 *            符号解约交易代码
	 * @param reqVersion
	 *            请求版本号
	 * @param info
	 *            副号码签约信息
	 * @param subTransIDO
	 *            发起方交易流水
	 * @param subTransIDOTime
	 *            发起方时间
	 * @param subTransId
	 *            发起方交易流水好
	 * @param reqMsg
	 *            银行请求信息
	 * @param subOrgId
	 *            副号码归属机构号
	 * @param subSeqId
	 *            副号码解约交易流水seqId
	 * @param subIntTxnDate
	 *            副号码解约交易流水时间
	 * @param paramData
	 *            银行请求信息
	 * @param lSeqId
	 *            TODO
	 * @throws AppBizException业务异常
	 */
	private void sendCrmSubProvSubUnbind(UpayCsysTransCode subTransCode,
			String reqVersion, UpayCsysBindInfo info, String subTransIDO,
			String subTransIDOTime, String subTransId, BankMsgVo reqMsg,
			String subOrgId, Long subSeqId, String subIntTxnDate,
			BankMsgVo paramData, String lSeqId, String transIDH)
			throws Exception {
		/** 转发交易 */
		/** 报文头 */
		CrmMsgVo forwardMsg = new CrmMsgVo();
		forwardMsg.setTestFlag(testFlag);
		forwardMsg.setTransCode(subTransCode);
		forwardMsg.setVersion(reqVersion);
		forwardMsg.setBIPCode(CommonConstant.Bip.Bis09.getValue());
		forwardMsg.setActivityCode(CommonConstant.CrmTrans.Crm03.getValue());
		forwardMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
		forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
		forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
		forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
		forwardMsg.setRouteValue(info.getIdValue());
		forwardMsg.setSessionID(subTransIDO);
		forwardMsg.setTransIDO(subTransIDO);
		forwardMsg.setTransIDOTime(subTransIDOTime);

		/** 报文体 */
		CrmSubUnbindReqVo forwardBody = new CrmSubUnbindReqVo();
		forwardBody.setDealType(CommonConstant.BindDealType.Unbind.getValue());
		forwardBody.setSubID(info.getSubId());
		forwardBody.setMainIDType(info.getMainIdType());
		forwardBody.setMainIDValue(info.getMainIdValue());
		forwardBody.setIDType(info.getIdType());
		forwardBody.setIDValue(info.getIdValue());
		forwardBody.setTransactionID(subTransId);
		forwardBody.setSubTime(info.getSignTxnTime());
		forwardBody.setActionDate(DateUtil.getDateyyyyMMdd());
		forwardBody.setCnlTyp(reqMsg.getReqChannel());

		// boolean checkFlag = false;
		String forwardOrg = subOrgId;

		ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(info.getIdValue());
		String checkFlag = offOrgTrans(reqMsg.getReqSys(), subOrgId, paramData
				.getTransCode().getTransCode(), provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
		// checkFlag = isO2OTransOn(reqMsg.getReqSys(), subOrgId,
		// paramData.getTransCode().getTransCode());
		// checkFlag = orgStatusCheck(subOrgId);

		UpayCsysTxnLog subLog = initSubLog2Sub(info, subSeqId, subIntTxnDate,
				subTransIDOTime, subTransIDO, subTransCode, forwardMsg,
				forwardBody, lSeqId);
		subLog.setSettleDate(reqMsg.getReqDate());
		logger.debug("begin 添加解约副号码交易流水,内部交易流水:{}", transIDH);
		subLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
		subLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
		subLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		subLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		subLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
		subLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
		subLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
				.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogService.add(subLog);
		logger.debug("success 添加解约副号码交易流水,内部交易流水:{}", transIDH);

		if (checkFlag != null) {
			logger.warn(
					"副号码机构:{}状态异常,内部交易流水号:{},发起方:{},接收方:{}",
					new Object[] { subOrgId, subTransIDO,
							forwardMsg.getTransIDO(), reqMsg.getReqSys(),
							subOrgId });
			return;
		}
		forwardMsg.setMsgReceiver(forwardOrg);
		forwardMsg.setBody(forwardBody);
		subLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		subLog.setRcvOprTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		subLog.setRcvDomain(forwardMsg.getMsgReceiver());
		forwardMsg = crmSubUnbindBus.execute(forwardMsg, null, subLog, null);
		subLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		subLog.setRcvVersion(forwardMsg.getVersion());
		subLog.setRcvOprId(forwardBody.getTransactionID());
		subLog.setRcvRouteType(forwardMsg.getRouteType());
		subLog.setRcvRouteVal(forwardMsg.getRouteValue());
		subLog.setRcvSessionId(forwardMsg.getSessionID());
		subLog.setRcvTransId(forwardMsg.getTransIDO());
		subLog.setRcvTransDt(forwardMsg.getTransIDOTime());
		subLog.setRcvActivityCode(forwardMsg.getActivityCode());
		subLog.setRcvBipCode(forwardMsg.getBIPCode());
		subLog.setRcvCnlType(forwardBody.getCnlTyp());
		subLog.setRcvRspCode(forwardMsg.getRspCode());
		subLog.setRcvRspDesc(forwardMsg.getRspDesc());
		subLog.setRcvTranshId(forwardMsg.getTransIDH());
		subLog.setRcvTransTm(forwardMsg.getTransIDHTime());
		subLog.setRcvOprDt(forwardBody.getActionDate());
		subLog.setRcvOprTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		CrmSubUnbindResVo forwardRtBody = new CrmSubUnbindResVo();
		String rtBodyStr = (String) forwardMsg.getBody();
		if (StringUtils.isBlank(rtBodyStr)) {
			subLog.setRcvRspType(CommonConstant.CrmRspType.BusErr .getValue());
			upayCsysTxnLogService.modify(subLog);
			return;
		} else {
			MsgHandle.unmarshaller(forwardRtBody, rtBodyStr);
			subLog.setRcvSubRspCode(forwardRtBody.getRspCode());
			subLog.setRcvSubRspDesc(forwardRtBody.getRspInfo());
			subLog.setRcvRspType(CommonConstant.CrmRspType.Success.getValue());
			if (forwardMsg.getRspCode().equals(
					RspCodeConstant.Wzw.WZW_0000.getValue())
					&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
							forwardRtBody.getRspCode())) {
				log.succ(
						"解约副号:{}成功,内部交易流水号:{},发起方:{},接收方:{}",
						new Object[] { info.getIdValue(),
								forwardMsg.getTransIDO(), reqMsg.getReqSys(),
								forwardOrg });
				logger.info(
						"解约副号:{}成功,内部交易流水号:{},发起方:{},接收方:{}",
						new Object[] { info.getIdValue(),
								forwardMsg.getTransIDO(), reqMsg.getReqSys(),
								forwardOrg });
			} else {
				log.error(
						"解约副号:{}失败,内部交易流水号:{},发起方:{},接收方:{},rspCode:{},rspDesc:{}",
						new Object[] { info.getIdValue(),
								forwardMsg.getTransIDO(), reqMsg.getReqSys(),
								forwardOrg, forwardRtBody.getRspCode(),
								forwardRtBody.getRspInfo() });
				logger.info(
						"解约副号:{}失败,内部交易流水号:{},发起方:{},接收方:{},rspCode:{},rspDesc:{}",
						new Object[] { info.getIdValue(),
								forwardMsg.getTransIDO(), reqMsg.getReqSys(),
								forwardOrg, forwardRtBody.getRspCode(),
								forwardRtBody.getRspInfo() });
			}
			upayCsysTxnLogService.modify(subLog);
		}

	}

	/**
	 * 向主号码省发起解约
	 * 
	 * @param subTransCode
	 *            交易代码
	 * @param reqVersion
	 *            报文版本
	 * @param mainBindInfo
	 *            主号码签约信息
	 * @param subTransIDO
	 *            发起流水
	 * @param subTransIDOTime
	 *            发起时间
	 * @param info
	 *            副号码签约信息
	 * @param subTransId
	 *            操作流水
	 * @param reqMsg
	 *            银行请求报文
	 * @param mainOrgId
	 *            主号码归属机构
	 * @param subSeqId
	 *            log key
	 * @param subIntTxnDate
	 *            log date
	 * @param paramData
	 *            银行请求报文
	 * @param lSeqId
	 *            TODO
	 * @throws AppBizException
	 *             业务异常
	 */
	private void sendCrmMainProvSubUnbind(UpayCsysTransCode subTransCode,
			String reqVersion, UpayCsysBindInfo mainBindInfo,
			String subTransIDO, String subTransIDOTime, UpayCsysBindInfo info,
			String subTransId, BankMsgVo reqMsg, String mainOrgId,
			Long subSeqId, String subIntTxnDate, BankMsgVo paramData,
			String lSeqId, String transIDH) throws Exception {
		/** 转发交易 */
		/** 报文头 */
		CrmMsgVo forwardMsg = new CrmMsgVo();
		forwardMsg.setTestFlag(testFlag);
		forwardMsg.setTransCode(subTransCode);
		forwardMsg.setVersion(reqVersion);
		forwardMsg.setBIPCode(CommonConstant.Bip.Bis09.getValue());
		forwardMsg.setActivityCode(CommonConstant.CrmTrans.Crm03.getValue());
		forwardMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
		forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
		forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
		forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
		forwardMsg.setRouteValue(mainBindInfo.getIdValue());
		forwardMsg.setSessionID(subTransIDO);
		forwardMsg.setTransIDO(subTransIDO);
		forwardMsg.setTransIDOTime(subTransIDOTime);

		/** 报文体 */
		CrmSubUnbindReqVo forwardBody = new CrmSubUnbindReqVo();
		forwardBody.setDealType(CommonConstant.BindDealType.Unbind.getValue());
		forwardBody.setSubID(info.getSubId());
		forwardBody.setMainIDType(info.getMainIdType());
		forwardBody.setMainIDValue(info.getMainIdValue());
		forwardBody.setIDType(info.getIdType());
		forwardBody.setIDValue(info.getIdValue());
		forwardBody.setTransactionID(subTransId);
		forwardBody.setSubTime(info.getSignTxnTime());
		forwardBody.setActionDate(DateUtil.getDateyyyyMMdd());
		forwardBody.setCnlTyp(reqMsg.getReqChannel());

		String forwardOrg = mainOrgId;
		ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(info.getIdValue());
		String checkFlag = offOrgTrans(reqMsg.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode(), 
				provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
		// checkFlag = orgStatusCheck(mainOrgId);

		UpayCsysTxnLog subLog = initSubLog2Main(mainBindInfo, info, subSeqId,
				subIntTxnDate, subTransIDOTime, subTransIDO, subTransCode,
				forwardMsg, forwardBody, reqMsg, subSeqId, lSeqId);
		subLog.setSettleDate(reqMsg.getReqDate());
		logger.debug("begin 添加解约副号码交易流水,内部交易流水号:{}", transIDH);
		subLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
		subLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
		subLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		subLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		subLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
		subLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
		subLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
				.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogService.add(subLog);
		logger.debug("succ 添加解约副号码交易流水,内部交易流水号:{}", transIDH);

		if (checkFlag !=null) {
			log.error(
					"主号机构:{}状态异常,内部交易流水号:{},业务发起方:{},接收方:{}",
					new Object[] { mainOrgId, subTransIDO,
							forwardMsg.getTransIDO(), reqMsg.getReqSys(),
							mainOrgId });
			logger.info(
					"主号机构:{}状态异常,内部交易流水号:{},业务发起方:{},接收方:{}",
					new Object[] { mainOrgId, subTransIDO,
							forwardMsg.getTransIDO(), reqMsg.getReqSys(),
							mainOrgId });
			return;
		}
		forwardMsg.setMsgReceiver(forwardOrg);
		forwardMsg.setBody(forwardBody);
		subLog.setRcvOprTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		subLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		subLog.setRcvDomain(forwardMsg.getMsgReceiver());
		forwardMsg = crmSubUnbindBus.execute(forwardMsg, null, subLog, null);
		subLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
		subLog.setRcvOprId(forwardBody.getTransactionID());
		subLog.setRcvRouteType(forwardMsg.getRouteType());
		subLog.setRcvRouteVal(forwardMsg.getRouteValue());
		subLog.setRcvSessionId(forwardMsg.getSessionID());
		subLog.setRcvTransId(forwardMsg.getTransIDO());
		subLog.setRcvActivityCode(forwardMsg.getActivityCode());
		subLog.setRcvBipCode(forwardMsg.getBIPCode());
		subLog.setRcvCnlType(forwardBody.getCnlTyp());
		subLog.setRcvVersion(forwardMsg.getVersion());
		subLog.setRcvRspCode(forwardMsg.getRspCode());
		subLog.setRcvRspDesc(forwardMsg.getRspDesc());
		subLog.setRcvTransTm(forwardMsg.getTransIDOTime());
		subLog.setRcvTranshId(forwardMsg.getTransIDH());
		subLog.setRcvTranshTm(forwardMsg.getTransIDHTime());
		CrmSubUnbindResVo forwardRtBody = new CrmSubUnbindResVo();
		String rtBodyStr = (String) forwardMsg.getBody();
		if (StringUtils.isBlank(rtBodyStr)) {
			subLog.setRcvRspType(CommonConstant.CrmRspType.BusErr .getValue());
			upayCsysTxnLogService.modify(subLog);
			return;
		} else {
			MsgHandle.unmarshaller(forwardRtBody, rtBodyStr);
			subLog.setRcvOprDt(forwardRtBody.getActionDate());
			subLog.setRcvOprTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			subLog.setRcvSubRspCode(forwardRtBody.getRspCode());
			subLog.setRcvSubRspDesc(forwardRtBody.getRspInfo());
			subLog.setRcvRspType(CommonConstant.CrmRspType.Success.getValue());
			if (forwardMsg.getRspCode().equals(
					RspCodeConstant.Wzw.WZW_0000.getValue())
					&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
							forwardRtBody.getRspCode())) {
				log.succ(
						"解约副号:{}成功,内部交易流水号:{},业务发起方:{},接收方:{}",
						new Object[] { info.getIdValue(),
								forwardMsg.getTransIDO(), reqMsg.getReqSys(),
								mainOrgId });
				logger.info(
						"解约副号:{}成功,内部交易流水号:{},业务发起方:{},接收方:{}",
						new Object[] { info.getIdValue(),
								forwardMsg.getTransIDO(), reqMsg.getReqSys(),
								mainOrgId });
			} else {
				log.error(
						"解约副号:{}失败,内部交易流水号:{},业务发起方:{},接收方:{},rspCode:{},rspDesc:{}",
						new Object[] { info.getIdValue(),
								forwardMsg.getTransIDO(), reqMsg.getReqSys(),
								mainOrgId, forwardRtBody.getRspCode(),
								forwardRtBody.getRspInfo() });
				logger.info(
						"解约副号:{}失败,内部交易流水号:{},业务发起方:{},接收方:{},rspCode:{},rspDesc:{}",
						new Object[] { info.getIdValue(),
								forwardMsg.getTransIDO(), reqMsg.getReqSys(),
								mainOrgId, forwardRtBody.getRspCode(),
								forwardRtBody.getRspInfo() });
			}
			upayCsysTxnLogService.modify(subLog);
		}
	}

	private UpayCsysTxnLog initSubLog2Main(UpayCsysBindInfo mainBindInfo,
			UpayCsysBindInfo info, Long subSeqId, String subIntTxnDate,
			String subTransIDOTime, String subTransIDO,
			UpayCsysTransCode subTransCode, CrmMsgVo forwardMsg,
			CrmSubUnbindReqVo forwardBody, BankMsgVo reqMsg, Long intTxnSeq,
			String lSeqId) {
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		txnLog.setlSeqId(lSeqId);
		txnLog.setSeqId(subSeqId);
		txnLog.setSettleDate(reqMsg.getReqDate());
		txnLog.setIntTxnSeq(forwardMsg.getTransIDO());
		txnLog.setIntTxnDate(subIntTxnDate);
		txnLog.setIntTxnTime(subTransIDOTime);
		txnLog.setBussChl(subTransCode.getBussChl());
		// txnLog.setTxnCat(CommonConstant.LogTransType.Unbind.getValue());
		txnLog.setBussType(subTransCode.getBussType());
		txnLog.setIntTransCode(subTransCode.getTransCode());
		txnLog.setPayMode(subTransCode.getPayMode());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
		txnLog.setReqActivityCode(forwardMsg.getActivityCode());
		txnLog.setReqDomain(CommonConstant.OrgDomain.UPSS.getValue());
		txnLog.setReqRouteType(CommonConstant.RouteType.RoutePhone.getValue());
		txnLog.setReqRouteVal(info.getIdValue());
		txnLog.setReqTransId(subTransIDO);
		txnLog.setReqTransTm(subTransIDOTime);
		txnLog.setMainFlag(CommonConstant.Mainflag.Slave.getValue());
		txnLog.setMainIdType(info.getMainIdType());
		txnLog.setMainIdValue(info.getMainIdValue());
		txnLog.setMainIdProvince(info.getMainIdProvince());
		txnLog.setIdType(forwardBody.getIDType());
		txnLog.setIdValue(forwardBody.getIDValue());
		txnLog.setIdProvince(info.getIdProvince());
		txnLog.setSubId(forwardBody.getSubID());
		txnLog.setBankId(info.getBankId());
		txnLog.setSubTime(forwardMsg.getTransIDOTime());
		txnLog.setReconciliationFlag(CommonConstant.ReconciliationFlag.reconciliationFl
				.getValue());
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
				.getDateyyyyMMddHHmmssSSS());
		return txnLog;
	}

	/**
	 * 向副号码归属省发送副号码解约的交易日志
	 * 
	 * @param info
	 * @param seqId
	 * @param intTxnDate
	 * @param transIDOTime
	 * @param transIDO
	 * @param transCode
	 * @param forwardMsg
	 * @param forwardBody
	 * @param lSeqId
	 *            TODO
	 * @return
	 */
	private UpayCsysTxnLog initSubLog2Sub(UpayCsysBindInfo info, Long seqId,
			String intTxnDate, String transIDOTime, String transIDO,
			UpayCsysTransCode transCode, CrmMsgVo forwardMsg,
			CrmSubUnbindReqVo forwardBody, String lSeqId) {
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		txnLog.setSeqId(seqId);
		txnLog.setlSeqId(lSeqId);
		txnLog.setIntTxnSeq(forwardMsg.getTransIDO());
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDOTime);
		txnLog.setBussChl(transCode.getBussChl());
		// txnLog.setTxnCat(CommonConstant.LogTransType.Unbind.getValue());
		txnLog.setBussType(transCode.getBussType());
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
		txnLog.setReqActivityCode(forwardMsg.getActivityCode());
		txnLog.setReqBipCode(forwardMsg.getBIPCode());
		txnLog.setReqSessionId(forwardMsg.getSessionID());
		txnLog.setReqOprId(forwardBody.getTransactionID());
		txnLog.setReqOprDt(forwardBody.getActionDate());
		txnLog.setReqDomain(CommonConstant.OrgDomain.UPSS.getValue());
		txnLog.setReqRouteType(CommonConstant.RouteType.RoutePhone.getValue());
		txnLog.setReqRouteVal(info.getIdValue());
		txnLog.setReqTransId(transIDO);
		txnLog.setReqTransTm(transIDOTime);
		txnLog.setReqVersion(forwardMsg.getVersion());
		txnLog.setReqCnlType(forwardBody.getCnlTyp());
		txnLog.setMainIdType(info.getMainIdType());
		txnLog.setMainIdValue(info.getMainIdValue());
		txnLog.setMainIdProvince(info.getMainIdProvince());
		txnLog.setIdType(forwardBody.getIDType());
		txnLog.setIdValue(forwardBody.getIDValue());
		txnLog.setIdProvince(info.getIdProvince());
		txnLog.setBankId(info.getBankId());
		txnLog.setSubId(forwardBody.getSubID());
		txnLog.setSubTime(forwardMsg.getTransIDOTime());
		txnLog.setMainFlag(CommonConstant.Mainflag.Slave.getValue());
		txnLog.setReconciliationFlag(CommonConstant.ReconciliationFlag.reconciliationFl
				.getValue());
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
				.getDateyyyyMMddHHmmssSSS());
		return txnLog;
	}

	/**
	 * 返回成功解约结果
	 * 
	 * @param resMsg
	 * @param resBody
	 * @return
	 */
	private BankMsgVo succ(BankMsgVo resMsg, BankUnbindResVo resBody) {
		resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
		resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
		resMsg.setBody(resBody);
		return resMsg;
	}

	/**
	 * 返回失败解约结果
	 * 
	 * @param resMsg
	 * @param resBody
	 * @param rspCode
	 * @param rspDesc
	 * @return
	 */
	private BankMsgVo fail(BankMsgVo resMsg, BankUnbindResVo resBody,
			String rspCode, String rspDesc) {
		resMsg.setRspCode(rspCode);
		resMsg.setRspDesc(rspDesc);
		resMsg.setBody(resBody);
		return resMsg;
	}

	/**
	 * 是否解约成功
	 * 
	 * @param info
	 *            签约关系
	 * @return
	 */
	private boolean isUnbindSuccess(UpayCsysBindInfo info) {
		return null != info
				&& CommonConstant.BindStatus.UnBind.getValue().equals(
						info.getStatus());
	}

	/**
	 * 修改主号码签约副号码数量
	 * 
	 * @param mainBindInfo
	 *            主号码签约信息
	 * @throws Exception
	 */
	private void modifySubNum(UpayCsysBindInfo mainBindInfo) throws Exception {
		Map<String, Object> noPrams = new HashMap<String, Object>();
		noPrams.put("mainFlag", CommonConstant.Mainflag.Slave.getValue());
		noPrams.put("mainIdValue", mainBindInfo.getIdValue());
		noPrams.put("status", CommonConstant.BindStatus.Bind.getValue());
		noPrams.put("subId", mainBindInfo.getSubId());
		int subNum = upayCsysBindInfoService.findSubCountByParams(noPrams);
		mainBindInfo.setSubNum(subNum);
		upayCsysBindInfoService.modify(mainBindInfo);
	}

}
