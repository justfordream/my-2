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
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.cache.TransCodeCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.bank.BankUnbindBus;
import com.huateng.cmupay.jms.business.crm.CrmSubUnbindBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankUnbindNoticeReq;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMainMobileUnBindReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMainMobileUnBindResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubUnbindReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubUnbindResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 移动渠道主号码解约
 * 
 * @author zeng.j
 * 
 */
@Controller("crmMainUnbindAction")
@Scope("prototype")
public class CrmMainUnbindAction extends AbsBaseAction<CrmMsgVo, CrmMsgVo> {

	@Autowired
	private CrmSubUnbindBus crmSubUnbindBus;
	@Autowired
	private BankUnbindBus bankUnBindBus;

	@Override
	public CrmMsgVo execute(CrmMsgVo paramData) throws AppBizException {
		logger.debug("CrmMainUnbindAction execute(Object) - start  ");
		/** 请求报文 */
		CrmMsgVo reqMsg = (CrmMsgVo) paramData;
		CrmMainMobileUnBindReqVo reqBody = new CrmMainMobileUnBindReqVo();
		/** 应答报文 */
		CrmMsgVo resMsg = new CrmMsgVo();
		CrmMainMobileUnBindResVo resBody = new CrmMainMobileUnBindResVo();
		/** 交易流水  */
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		String reqTransactionId = reqBody.getTransactionID();
		String transIDH = null;
		String transIDHTime = null;
		String intTxnDate = null;
		UpayCsysBindInfo mainBindInfo = null;
		Long seqId = null;
		transIDH = reqMsg.getTxnSeq();
		transIDHTime = paramData.getTxnTime();
		intTxnDate = reqMsg.getTxnDate();
		txnLog.setSettleDate(intTxnDate);
		seqId = reqMsg.getSeqId();
		resMsg=reqMsg;
		resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
		resMsg.setTransIDH(transIDH);
		resMsg.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
		resMsg.setTestFlag(reqMsg.getTestFlag());
		resMsg.setVersion(ExcConstant.CRM_VERSION);
		resMsg.setBIPCode(reqMsg.getBIPCode());
		resMsg.setActivityCode(reqMsg.getActivityCode());
		resMsg.setOrigDomain(reqMsg.getOrigDomain());
		resMsg.setHomeDomain(reqMsg.getHomeDomain());
		resMsg.setSessionID(reqMsg.getSessionID());
		resMsg.setTransIDO(reqMsg.getTransIDO());
		resMsg.setTransIDOTime(reqMsg.getTransIDOTime());
		resMsg.setRouteType(reqMsg.getRouteType());
		resMsg.setRouteValue(reqMsg.getRouteValue());
		/** 交易代码 */
		UpayCsysTransCode transCode = reqMsg.getTransCode();
		try {
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			resBody.setActionDate(reqBody.getActionDate());
			resBody.setSubId(reqBody.getSubID());
			resBody.setTransactionId(reqBody.getTransactionID());
			// resBody.setSubTime(reqBody.getSubTime());
			reqMsg.setBody(reqBody);
			if (!StringUtils.isBlank(reqBody.getTransactionID())) {
				Map<String, Object> param = new HashMap<String, Object>();
				
				// 防止字段超过指定长度从而跳过数据库订单重复校验的bug
				String reqDomain = reqMsg.getMsgSender();
				String reqOprId = reqBody.getTransactionID();
				if(reqDomain != null && reqDomain.length() > 4) {
					log.warn("移动渠道主号码解约接口:MsgSender代码{}超过4位，在订单重复查询时，只取前4位，,发起方:{},操作流水:{},内部流水:{}", new Object[] {reqDomain,
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					logger.warn("移动渠道主号码解约接口:MsgSender代码{}超过4位，在订单重复查询时，只取前4位，,发起方:{},操作流水:{},内部流水:{}", new Object[] {reqDomain,
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					reqDomain = reqDomain.substring(0, 4);
				}
				if(reqOprId != null && reqOprId.length() > 32) {
					log.warn("移动渠道主号码解约接口:TransactionID代码{}超过32位，在订单重复查询时，只取前32位，,发起方:{},操作流水:{},内部流水:{}", new Object[] {reqOprId,
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					logger.warn("移动渠道主号码解约接口:TransactionID代码{}超过32位，在订单重复查询时，只取前32位，,发起方:{},操作流水:{},内部流水:{}", new Object[] {reqOprId,
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					reqOprId = reqOprId.substring(0, 32);
				}
				param.put("reqDomain", reqDomain);
				param.put("reqOprId", reqOprId);
//				param.put("reqDomain", reqMsg.getMsgSender());
//				param.put("reqOprId", reqBody.getTransactionID());
				UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService
						.findObj(param);
				if (upayCsysTxnLog != null) {
					log.warn("移动渠道主号码解约接口!订单重复,发起方:{},操作流水:{},内部流水:{}", new Object[] {
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					logger.warn("移动渠道主号码解约接口!订单重复,发起方:{},操作流水:{},内部流水:{}", new Object[] {
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					resMsg.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					resBody.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc());
					resMsg.setBody(resBody);
					logger.debug("CrmMainUnbindAction execute(Object) - end  ");
					return resMsg;
				}
			}
			reqTransactionId = reqBody.getTransactionID();
			/** 交易流水 */
			txnLog.setSeqId(seqId);
			txnLog.setIntTxnSeq(transIDH);
			txnLog.setIntTxnDate(intTxnDate);
			txnLog.setIntTxnTime(transIDHTime);
			txnLog.setIntMqSeq(reqMsg.getMqSeq());
			// txnLog.setTxnCat(CommonConstant.LogTransType.Unbind.getValue());
			txnLog.setBussType(transCode.getBussType());
			txnLog.setBussChl(transCode.getBussChl());
			txnLog.setIntTransCode(transCode.getTransCode());
			txnLog.setPayMode(transCode.getPayMode());
			txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
			txnLog.setReqVersion(reqMsg.getVersion());
			txnLog.setReqBipCode(reqMsg.getBIPCode());
			txnLog.setReqActivityCode(reqMsg.getActivityCode());
			txnLog.setReqDomain(reqMsg.getMsgSender());
			txnLog.setReqRouteType(reqMsg.getRouteType());
			txnLog.setReqRouteVal(reqMsg.getRouteValue());
			txnLog.setReqSessionId(reqMsg.getSessionID());
			txnLog.setReqCnlType(reqBody.getCnlTyp());

			txnLog.setReqTransId(reqMsg.getTransIDO());
			txnLog.setReqTransDt(StrUtil.subString(reqMsg.getTransIDOTime(), 0,
					8));
			txnLog.setReqTransTm(reqMsg.getTransIDOTime());
			txnLog.setReqOprId(reqBody.getTransactionID());
			txnLog.setReqOprDt(reqBody.getActionDate());
			txnLog.setReqOprTm(reqMsg.getTransIDOTime());
			txnLog.setReqTranshId(resMsg.getTransIDH());
			txnLog.setReqTranshDt(StrUtil.subString(resMsg.getTransIDHTime(),
					0, 8));
			txnLog.setReqTranshTm(resMsg.getTransIDHTime());
			txnLog.setSettleDate(StrUtil.subString(reqMsg.getTransIDOTime(), 0,
					8));

			txnLog.setIdType(reqBody.getIDType());
			txnLog.setIdValue(reqBody.getIDValue());
			txnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
			txnLog.setSubId(reqBody.getSubID());
			txnLog.setSubTime(reqBody.getSubTime());
			txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			txnLog.setSettleDate(DateUtil.getDateyyyyMMdd());
			
			logger.debug("start 添加移动渠道主号码解约交易流水 ");
			upayCsysTxnLogService.add(txnLog);
			logger.debug("end 添加移动渠道主号码解约交易流水 ");
			/** 验证消息 */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				log.warn("移动渠道主号码解约接口!报文格式错误:{},内部流水:{},发起方:{}", new Object[] { checkrtn,
						transIDH, reqMsg.getMsgSender() });
				logger.warn("移动渠道主号码解约接口!报文格式错误:{},内部流水:{},发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getMsgSender() });

				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A99.getDesc()
						+ checkrtn);
				upayCsysTxnLogService.modify(txnLog);
				resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				resMsg.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A99.getDesc() + checkrtn);
				resMsg.setBody(resBody);
				logger.debug("CrmMainUnbindAction execute(Object) - end  ");
				return resMsg;
			}

			/** 主号码签约信息 */
			Map<String, Object> mainParams = new HashMap<String, Object>();
			mainParams.put("idValue", reqBody.getIDValue());
			mainParams.put("mainFlag",
					CommonConstant.Mainflag.Master.getValue());
			mainParams.put("subId", reqBody.getSubID().trim());
			mainBindInfo = upayCsysBindInfoService.findObj(mainParams);
			if (null == mainBindInfo
					|| !CommonConstant.BindStatus.Bind.getValue().equals(
							mainBindInfo.getStatus())) {
				log.warn(
						"主号码:{}无签约记录,内部流水:{},发起方:{}",
						new Object[] { reqBody.getIDValue(), transIDH,
								reqMsg.getMsgSender() });
				logger.warn("移动渠道主号码解约接口!主号码:{}无签约记录,内部流水:{},发起方:{}", new Object[] {
						reqBody.getIDValue(), transIDH, reqMsg.getMsgSender() });
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());

				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A09.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A09.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				upayCsysTxnLogService.modify(txnLog);

				logger.debug(
						"主号码:{}无签约记录,内部流水:{},发起方:{},返回码:{}描述:{},二级返回码:{}描述:{}",
						new Object[] { reqBody.getIDValue(), transIDH,
								reqMsg.getMsgSender(), resMsg.getRspCode(),
								resMsg.getRspDesc(), resBody.getRspCode(),
								resBody.getRspInfo() });
				logger.debug("CrmMainUnbindAction execute(Object) - end  ");
				return succ(resMsg, resBody);
			}

			// route value
//			resMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
//			resMsg.setRouteValue(mainBindInfo.getIdValue());

			String reqOrg = reqMsg.getMsgSender();
			String bindOrg = SysMapCache
					.getProvCd(mainBindInfo.getIdProvince()).getSysCd();
			txnLog.setIdProvince(mainBindInfo.getIdProvince());

			if (!reqOrg.equals(bindOrg)) {
				log.warn("移动渠道主号码解约接口!发起方:{}不允许发起主号码:{}(归属省:{})解约,内部流水:{}", new Object[] {
						reqMsg.getMsgSender(), reqBody.getIDValue(),
						mainBindInfo.getIdProvince(), transIDH });
				logger.warn(
						"发起方:{}不允许发起主号码:{}(归属省:{})解约,内部流水:{}",
						new Object[] { reqMsg.getMsgSender(),
								reqBody.getIDValue(),
								mainBindInfo.getIdProvince(), transIDH });
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());

				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A05.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A05.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				upayCsysTxnLogService.modify(txnLog);

				resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				resMsg.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_2A05.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_2A05.getDesc());
				resMsg.setBody(resBody);
				logger.debug("CrmMainUnbindAction execute(Object) - end  ");
				return resMsg;
			}

			/** 主号码的副号码签约信息列表 */
			Map<String, Object> subParams = new HashMap<String, Object>();
			subParams.put("subId", reqBody.getSubID().trim());
			subParams.put("mainIdValue", reqBody.getIDValue());
			subParams.put("mainFlag", CommonConstant.Mainflag.Slave.getValue());
			subParams.put("status", CommonConstant.BindStatus.Bind.getValue());
			List<UpayCsysBindInfo> subBindInfos = upayCsysBindInfoService
					.findList(subParams, null);
			logger.debug(" intTxnSeq:{},主号码:{}已签约的副号码：{}", new Object[] {
					transIDH, mainBindInfo.getIdValue(), subBindInfos });
			/** 副号码处理 */
			if (subBindInfos != null && subBindInfos.size() > 0) {
				logger.debug("intTxnSeq:{},start 解约副号码:{} ", transIDH,
						subBindInfos);
				/** 主号码归属省 */
				String mainIdProvince = mainBindInfo.getIdProvince().trim();
				for (UpayCsysBindInfo info : subBindInfos) {

					try {
						String subIdProvince = info.getIdProvince();
						String subOrgId = SysMapCache.getProvCd(subIdProvince)
								.getSysCd();

						/** 交易代码 */
//						UpayCsysTransCode subTransCode = TransCodeCache
//								.getTransCode(CommonConstant.Bip.Bis09
//										.getValue(),
//										CommonConstant.CrmTrans.Crm03
//												.getValue());
						// UpayCsysTransCode subTransCode =
						// reqMsg.getTransCode();
						Long subSeqId = upayCsysSeqMapInfoService
								.selectSeqValue(ExcConstant.TXN_LOG_SEQ);// log
																			// key
						String subIntTxnDate = upayCsysBatCutCtlService
								.findCutOffDate(ExcConstant.CUT_OFF_DATE);// 数据库时间
						String subTransIDO = Serial
								.genSerialNo(CommonConstant.Sequence.IntSeq
										.getValue());//
						String subTransIDOTime = DateUtil
								.getDateyyyyMMddHHmmss();

						String subTransactionId = Serial
								.genSerialNos(CommonConstant.Sequence.OprId
										.toString());
						UpayCsysTxnLog subLog = new UpayCsysTxnLog();
						subLog.setMainFlag(CommonConstant.Mainflag.Slave
								.getValue());
						subLog.setlSeqId(String.valueOf(txnLog.getSeqId()));// 关联主号解约流水
						subLog.setSettleDate(StrUtil.subString(
								reqMsg.getTransIDOTime(), 0, 8));
						subLog.setBussChl(reqMsg.getTransCode().getBussChl());
						subLog.setSeqId(subSeqId);
						subLog.setIntTxnSeq(subTransIDO);
						subLog.setIntTxnDate(subIntTxnDate);
						subLog.setIntTxnTime(DateUtil.getDateyyyyMMddHHmmss());
						// subLog.setTxnCat(CommonConstant.LogTransType.Unbind
						// .getValue());
						subLog.setBussType(reqMsg.getTransCode().getBussType());
						subLog.setIntTransCode(CommonConstant.TransCode.SubUnsign.getValue());
						subLog.setPayMode(reqMsg.getTransCode().getPayMode());
						subLog.setStatus(CommonConstant.TxnStatus.InitStatus
								.getValue());
						subLog.setReqRouteType(reqMsg.getRouteType());
						subLog.setReqRouteVal(reqMsg.getRouteValue());
						subLog.setReqTransId(subTransIDO);

						subLog.setMainIdType(info.getMainIdType());
						subLog.setMainIdValue(info.getMainIdValue());
						subLog.setMainIdProvince(info.getMainIdProvince());
						subLog.setIdType(info.getIdType());
						subLog.setIdValue(info.getIdValue());
						subLog.setIdProvince(info.getIdProvince());
						subLog.setSubId(mainBindInfo.getSubId());

						subLog.setReqDomain(reqMsg.getMsgSender());
						subLog.setRcvDomain(reqMsg.getMsgSender());

						subLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
						subLog.setRefundFlag(CommonConstant.YesOrNo.No
								.toString());
						subLog.setReverseFlag(CommonConstant.YesOrNo.No
								.toString());
						subLog.setReconciliationFlag(CommonConstant.YesOrNo.No
								.toString());
						subLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
								.getDateyyyyMMddHHmmssSSS());
						upayCsysTxnLogService.add(subLog);

						if (mainIdProvince.equals(subIdProvince)) {
							log.info(
									"主号码归属地:{}副号码归属地:{}相同,内部流水:{},发起方:{}",
									new Object[] { mainIdProvince,
											subIdProvince, subTransIDO,
											reqMsg.getMsgSender() });
							logger.info(
									"主号码归属地:{}副号码归属地:{}相同,内部流水:{},发起方:{}",
									new Object[] { mainIdProvince,
											subIdProvince, subTransIDO,
											reqMsg.getMsgSender() });
							info.setClCnlType(reqBody.getCnlTyp());
							info.setClOrgId(reqMsg.getMsgSender());
							info.setClTxnDate(subIntTxnDate);
//							info.setClTxnSeq(String.valueOf(subTransactionId));
							info.setClTxnSeq(String.valueOf(subTransIDO));
							info.setClTxnTime(subTransIDOTime);
							info.setStatus(CommonConstant.BindStatus.UnBind
									.getValue());
							info.setClSubTime(subTransIDOTime);
							info.setSettleDate(reqBody.getActionDate());//

							subLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000
									.getValue());
							subLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000
									.getDesc());
							subLog.setChlRspType(CommonConstant.CrmRspType.Success
									.getValue());
							subLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000
									.getValue());
							subLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000
									.getDesc());
							subLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
									.getValue());
							subLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
									.getDateyyyyMMddHHmmssSSS());
							logger.info("intTxnSeq:{},更新副号码:{}签约信息，seq_id: {}",
									new Object[] { transIDH, info.getIdValue(),
											info.getSeqId() });
							upayCsysBindInfoService.modifyLogAndBinfInfo(
									mainBindInfo, info, subLog);
							// upayCsysBindInfoService.modifyTxnLogAndDelBindInfo(
							// info, subLog);
							logger.info("intTxnSeq:{},解约副号码:{} 成功", transIDH,
									info.getIdValue());
							// Map<String, Object> noPrams = new HashMap<String,
							// Object>();
							// noPrams.put("mainFlag",
							// CommonConstant.Mainflag.Slave.getValue());
							// noPrams.put("mainIdValue",
							// mainBindInfo.getIdValue());
							// noPrams.put("status",
							// CommonConstant.BindStatus.Bind.getValue());
							// noPrams.put("subId", mainBindInfo.getSubId());
							// int subNum = upayCsysBindInfoService
							// .findSubCountByParams(noPrams);
							// mainBindInfo.setSubNum(subNum);
							// upayCsysBindInfoService.modify(mainBindInfo);
//							logger.info("intTxnSeq:{},修改主号码:{}签约副号码数量 成功",
//									transIDH, mainBindInfo.getIdValue());
							continue;
						} else {
							/** 转发交易 */
							/** 报文头 */
							CrmMsgVo forwardMsg = new CrmMsgVo();
//							forwardMsg.setTransCode(subTransCode);
							forwardMsg.setVersion(reqMsg.getVersion());
							forwardMsg.setTestFlag(reqMsg.getTestFlag());
							forwardMsg.setBIPCode(CommonConstant.Bip.Bis09
									.getValue());
							forwardMsg
									.setActivityCode(CommonConstant.CrmTrans.Crm03
											.getValue());
							forwardMsg
									.setActionCode(CommonConstant.ActionCode.Requset
											.getValue());
							forwardMsg
									.setOrigDomain(CommonConstant.OrgDomain.UPSS
											.getValue());
							forwardMsg
									.setHomeDomain(CommonConstant.OrgDomain.BOSS
											.getValue());
							forwardMsg
									.setRouteType(CommonConstant.RouteType.RoutePhone
											.getValue());
							forwardMsg
									.setSessionID(Serial
											.genSerialNo(CommonConstant.Sequence.CrmSessionId
													.getValue()));
							forwardMsg.setTransIDO(subTransIDO);
							forwardMsg.setTransIDOTime(subTransIDOTime);
							forwardMsg
									.setMsgSender(CommonConstant.BankOrgCode.CMCC
											.getValue());

							/** 报文体 */
							CrmSubUnbindReqVo forwardBody = new CrmSubUnbindReqVo();
							forwardBody
									.setDealType(CommonConstant.BindDealType.Unbind
											.getValue());
							forwardBody.setSubID(info.getSubId());
							forwardBody.setMainIDType(info.getMainIdType());
							forwardBody.setMainIDValue(info.getMainIdValue());
							forwardBody.setIDType(info.getIdType());
							forwardBody.setIDValue(info.getIdValue());
							forwardBody.setTransactionID(subTransactionId);
							forwardBody.setSubTime(info.getSignTxnTime());
							forwardBody.setActionDate(DateUtil
									.getDateyyyyMMdd());
							forwardBody.setCnlTyp(reqBody.getCnlTyp());

							subLog = initForwardLog(reqMsg, reqBody,
									forwardMsg, forwardBody, info, subSeqId,
									subIntTxnDate, subTransIDOTime,
									subTransIDO,subLog);

							forwardMsg.setMsgReceiver(subOrgId);
							forwardMsg.setRouteValue(info.getIdValue());

							subLog.setReqTransTm(reqMsg.getTransIDHTime());
							subLog.setReqTransDt(StrUtil.subString(
									reqMsg.getTransIDOTime(), 0, 8));
							// subLog.setReqActivityCode(forwardMsg
							// .getActivityCode());
							// subLog.setReqBipCode(forwardMsg.getBIPCode());
							// subLog.setReqCnlType(reqBody.getCnlTyp());
							// subLog.setReqDomain(reqMsg.getMsgSender());
							subLog.setRcvDomain(forwardMsg.getMsgReceiver());
							subLog.setRcvSessionId(Serial
									.genSerialNo(CommonConstant.Sequence.CrmSessionId
											.getValue()));
							subLog.setRcvTransTm(forwardMsg.getTransIDOTime());
							subLog.setRcvCnlType(reqBody.getCnlTyp());

							subLog.setReqOprDt(forwardBody.getActionDate());
							// subLog.setReqOprId(forwardBody.getTransactionID());
							subLog.setReqOprTm(reqMsg.getTransIDOTime());

							subLog.setReqRouteType(forwardMsg.getRouteType());
							subLog.setReqRouteVal(forwardMsg.getRouteValue());
							subLog.setReqTransDt(StrUtil.subString(
									forwardMsg.getTransIDOTime(), 0, 8));
							subLog.setReqTranshTm(StrUtil.subString(
									forwardMsg.getTransIDOTime(), 0, 14));
							subLog.setReqTranshId(forwardBody
									.getTransactionID());
							subLog.setReqTranshDt(StrUtil.subString(
									forwardMsg.getTransIDOTime(), 0, 8));
//							subLog.setReqTransId(forwardBody.getTransactionID());
							subLog.setReqTransId(forwardMsg.getTransIDO());
							// subLog.setReqVersion(forwardMsg.getVersion());

							forwardMsg.setBody(forwardBody);

							/* 解约处理 */
							info.setStatus(CommonConstant.BindStatus.UnBind
									.getValue());
//							info.setClSubTime(forwardMsg.getTransIDHTime()); 这个字段应该放到省成功解约副号后再填写
							info.setClCnlType(reqBody.getCnlTyp());
							info.setClOrgId(reqMsg.getMsgSender());
							info.setClTxnDate(subIntTxnDate);
//							info.setClTxnSeq(String.valueOf(subTransactionId));
							info.setClTxnSeq(String.valueOf(subTransIDO));
							info.setClTxnTime(subTransIDOTime);
							info.setSettleDate(forwardBody.getActionDate());//
							subLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
									.getValue());
							upayCsysBindInfoService.modifyLogAndBinfInfo(
									mainBindInfo, info, subLog);
							// upayCsysBindInfoService.modifyTxnLogAndDelBindInfo(
							// info, subLog);
							log.succ("解约副号码:{}成功,内部流水:{},发起方:{}",
									new Object[] { info.getIdValue(), transIDH,
											reqMsg.getMsgSender() });
							logger.info("解约副号码:{}成功,内部流水:{},发起方:{}",
									new Object[] { info.getIdValue(), transIDH,
											reqMsg.getMsgSender() });

							// Map<String, Object> noPrams = new HashMap<String,
							// Object>();
							// noPrams.put("mainFlag",
							// CommonConstant.Mainflag.Slave.getValue());
							// noPrams.put("mainIdValue",
							// mainBindInfo.getIdValue());
							// noPrams.put("status",
							// CommonConstant.BindStatus.Bind.getValue());
							// noPrams.put("subId", mainBindInfo.getSubId());
							// int subNum = upayCsysBindInfoService
							// .findSubCountByParams(noPrams);
							// mainBindInfo.setSubNum(subNum);
							// upayCsysBindInfoService.modify(mainBindInfo);
							// log.succ("修改主号码:{}签约副号数成功,内部流水:{},发起方:{}",
							// new Object[] { mainBindInfo.getIdValue(),
							// transIDH, reqMsg.getMsgSender() });
							// logger.info("修改主号码:{}签约副号数成功,内部流水:{},发起方:{}",
							// new Object[] { mainBindInfo.getIdValue(),
							// transIDH, reqMsg.getMsgSender() });

							logger.debug("begin 发送副号码解约请求");
							forwardMsg = crmSubUnbindBus.execute(forwardMsg,
									null, subLog, null);
							logger.debug("return 副号码解约请求返回成功");

							subLog.setChlSubRspCode(forwardMsg.getRspCode());
							CrmSubUnbindResVo forwardRtBody = new CrmSubUnbindResVo();
							MsgHandle.unmarshaller(forwardRtBody, forwardMsg
									.getBody().toString());

							subLog.setRcvActivityCode(forwardMsg
									.getActivityCode());
							subLog.setRcvBipCode(forwardMsg.getBIPCode());

							subLog.setRcvOprDt(forwardRtBody.getActionDate());
							subLog.setRcvOprId(forwardRtBody.getTransactionID());
							subLog.setRcvSubRspCode(forwardRtBody.getRspCode());
							subLog.setRcvSubRspDesc(forwardRtBody.getRspInfo());

							subLog.setRcvRouteType(forwardMsg.getRouteType());
							subLog.setRcvRouteVal(forwardMsg.getRouteValue());
							subLog.setRcvTransDt(StrUtil.subString(
									forwardMsg.getTransIDHTime(), 0, 8));
							subLog.setRcvTranshTm(forwardMsg.getTransIDHTime());
							
							
							subLog.setRcvTransId(forwardRtBody
									.getTransactionID());
							subLog.setRcvTranshId(forwardMsg.getTransIDH());
							subLog.setRcvVersion(forwardMsg.getVersion());

							if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
									forwardMsg.getRspCode())
									&& RspCodeConstant.Crm.CRM_0000.getValue()
											.equals(forwardRtBody.getRspCode())) {
								log.succ("副号:{}解约成功,内部流水:{},发起方:{}",
										new Object[] { info.getIdValue(),
												forwardMsg.getTransIDO(),
												reqMsg.getMsgSender() });
								logger.info("副号:{}解约成功,内部流水:{},发起方:{}",
										new Object[] { info.getIdValue(),
												forwardMsg.getTransIDO(),
												reqMsg.getMsgSender() });
								
								info.setClSubTime(forwardMsg.getTransIDHTime());
								upayCsysBindInfoService.modifySelective(info);
								
							} else {
								log.warn(
										"副号:{}解约失败,返回码:{}描述:{},内部流水:{},发起方:{}",
										new Object[] { info.getIdValue(),
												forwardRtBody.getRspCode(),
												forwardRtBody.getRspInfo(),
												forwardMsg.getTransIDO(),
												reqMsg.getMsgSender() });
								logger.warn(
										"副号:{}解约失败,返回码:{}描述:{},内部流水:{},发起方:{}",
										new Object[] { info.getIdValue(),
												forwardRtBody.getRspCode(),
												forwardRtBody.getRspInfo(),
												forwardMsg.getTransIDO(),
												reqMsg.getMsgSender() });
							}

							subLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000
									.getValue());
							subLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000
									.getDesc());
							subLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000
									.getValue());
							subLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000
									.getDesc());
							subLog.setChlRspType(CommonConstant.CrmRspType.Success
									.getValue());
							subLog.setLastUpdTime(DateUtil
									.getDateyyyyMMddHHmmss());
							subLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
									.getValue());
							subLog.setSubTime(info.getSignSubTime());
							upayCsysTxnLogService.modify(subLog);
						}
					} catch (Exception e) {
						log.error("移动渠道主号码解约接口!解约副号码异常:{},内部流水:{},发起方:{}", new Object[] {
								e, transIDH, reqMsg.getMsgSender() });
						logger.error("移动渠道主号码解约接口!解约副号码异常:{},内部流水:{},发起方:{}", new Object[] {
								e, transIDH, reqMsg.getMsgSender() });
						logger.error("移动渠道主号码解约接口!解约副号码异常:", e);
						continue;
					}
				}
				modifySubNum(mainBindInfo);// 修改主号码的签约副号码个数
				log.succ("修改主号码:{}签约副号数成功,内部流水:{},发起方:{}",
						new Object[] { mainBindInfo.getIdValue(), transIDH,
								reqMsg.getMsgSender() });
				logger.info("修改主号码:{}签约副号数成功,内部流水:{},发起方:{}",
						new Object[] { mainBindInfo.getIdValue(), transIDH,
								reqMsg.getMsgSender() });
			}
			String forwadDate = upayCsysBatCutCtlService
					.findCutOffDate(ExcConstant.CUT_OFF_DATE);
			BankMsgVo bankReqMsg = new BankMsgVo();
			/** 到银行的主号解约请求报文 */
			// 接口报文头信息
			bankReqMsg.setActivityCode(CommonConstant.BankTrans.Bank07
					.getValue());
			bankReqMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
			bankReqMsg.setReqChannel(reqBody.getCnlTyp());
			bankReqMsg.setReqDate(forwadDate);
			bankReqMsg.setReqTransID(transIDH);
			bankReqMsg.setReqDateTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			bankReqMsg.setActionCode(CommonConstant.ActionCode.Requset
					.getValue());
			bankReqMsg.setRcvSys(mainBindInfo.getBankId());
			// 返回内容请求中不填
			// 签名内容
			// bankReqMsg.setSignFlag(null);
			// bankReqMsg.setCerID(null);
			// bankReqMsg.setSignValue(null);

			/* 报文体内容 */
			BankUnbindNoticeReq unbindReq = new BankUnbindNoticeReq();
			unbindReq.setiDType(mainBindInfo.getIdType());
			unbindReq.setiDValue(mainBindInfo.getIdValue());
			unbindReq.setSubID(mainBindInfo.getSubId());
			bankReqMsg.setTransCode(transCode);
			bankReqMsg.setBody(unbindReq);
			/* 主号码解约处理 */
			mainBindInfo.setStatus(CommonConstant.BindStatus.UnBind.getValue());
//			mainBindInfo.setClTxnSeq(reqTransactionId);
			mainBindInfo.setClTxnSeq(reqMsg.getTransIDO());
			mainBindInfo.setClTxnDate(DateUtil.getDateyyyyMMdd());
			mainBindInfo.setClTxnTime(DateUtil.getDateyyyyMMddHHmmss());
			mainBindInfo.setClOrgId(reqMsg.getMsgSender());
//			mainBindInfo.setClSubTime(bankReqMsg.getRcvDateTime()); 在银行返回时再设值
			mainBindInfo.setClCnlType(reqBody.getCnlTyp());
			mainBindInfo.setSettleDate(forwadDate);

			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());

			upayCsysBindInfoService.modifySelectiveBindInfoAndLog(mainBindInfo,
					txnLog);
			// upayCsysBindInfoService.modifyTxnLogAndDelBindInfo(mainBindInfo,
			// txnLog);
			log.succ(
					"解约主号:{}成功,内部流水:{},发起方:{}",
					new Object[] { mainBindInfo.getIdValue(), transIDH,
							reqMsg.getMsgSender() });
			logger.info(
					"解约主号:{}成功,内部流水:{},发起方:{}",
					new Object[] { mainBindInfo.getIdValue(), transIDH,
							reqMsg.getMsgSender() });
			
			// 查询该交易的号码段属于移动还是联通电信的。
			ProvincePhoneNum provincePhoneNum = null;
			// 如果请求报文没有填写主号号码，则去签约关系表中查找该主号号码
			if(reqBody.getIDValue() == null) {
				provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(mainBindInfo.getIdValue());
			} else {
				provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(reqBody.getIDValue());
			}
			String  orgCheck = offOrgTrans(paramData.getMsgSender(),
					mainBindInfo.getBankId(), paramData.getTransCode()
							.getTransCode(), provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			boolean orgCheck = isO2OTransOn(paramData.getMsgSender(),
//					mainBindInfo.getBankId(), paramData.getTransCode()
//							.getTransCode());
			
			if(txnLog.getIdProvince() == null && provincePhoneNum != null) {
				txnLog.setIdProvince(provincePhoneNum.getProvinceCode());
			}
			
			if (orgCheck == null) {
				txnLog.setRcvTransDt(bankReqMsg.getReqDate());
				txnLog.setRcvTransId(bankReqMsg.getReqTransID());
				txnLog.setRcvTransTm(bankReqMsg.getReqDateTime());
				// 向银行发送主号码解约请求
				logger.debug("begin 发送银行主号码解约");
				txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				bankReqMsg = bankUnBindBus
						.execute(bankReqMsg, null, txnLog, null);
				txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setSettleDate(bankReqMsg.getRcvDate());
				
				mainBindInfo.setClSubTime(bankReqMsg.getRcvDateTime());
				upayCsysBindInfoService.modifySelective(mainBindInfo);
				
			} else {
				log.error("移动渠道主号码解约接口!主号解约接收方机构:{}状态异常,内部流水:{},发起方:{}",
						new Object[] { mainBindInfo.getBankId(), transIDH,
								reqMsg.getMsgSender() });
				logger.warn("主号解约接收方机构:{}状态异常,内部流水:{},发起方:{}",
						new Object[] { mainBindInfo.getBankId(), transIDH,
								reqMsg.getMsgSender() });
			}

			logger.debug("return 发送银行主号码解约");
			txnLog.setRcvActivityCode(bankReqMsg.getActivityCode());
			txnLog.setRcvCnlType(txnLog.getReqCnlType());
			txnLog.setRcvDomain(bankReqMsg.getRcvSys());//
			txnLog.setRcvRouteType(resMsg.getRouteType());
			txnLog.setRcvRouteVal(resMsg.getRouteValue());
			txnLog.setRcvOprId(bankReqMsg.getRcvTransID());
			txnLog.setRcvTranshDt(bankReqMsg.getRcvDate());
			txnLog.setRcvTranshId(bankReqMsg.getRcvTransID());
			txnLog.setRcvTranshTm(bankReqMsg.getRcvDateTime());

			txnLog.setUserId(StringFormat.formatCodeString(mainBindInfo
					.getUserId()));
			txnLog.setUserName(StringFormat.formatNameString(mainBindInfo
					.getUserName()));
			txnLog.setUserType(mainBindInfo.getUserType());
			txnLog.setBankId(mainBindInfo.getBankId());
			txnLog.setBankAcctType(mainBindInfo.getBankAcctType());
			txnLog.setBankAccId(StringFormat.formatCodeString(mainBindInfo
					.getBankAccId()));

			txnLog.setRechAmount(mainBindInfo.getRechAmount());
			txnLog.setMaxRechAmount(mainBindInfo.getMaxRechAmount());
			txnLog.setRechThreshold(mainBindInfo.getRechThreshold());
			txnLog.setMaxRechThreshold(mainBindInfo.getMaxRechThreshold());

//			txnLog.setChlSubRspCode(bankReqMsg.getRspCode());

			txnLog.setRcvRspCode(bankReqMsg.getRspCode());
			txnLog.setRcvRspDesc(bankReqMsg.getRspDesc());
			txnLog.setRcvSubRspCode(bankReqMsg.getRspCode());
			txnLog.setRcvSubRspDesc(bankReqMsg.getRspDesc());
			upayCsysTxnLogService.modify(txnLog);
			if (RspCodeConstant.Bank.BANK_020A00.getValue().equals(
					bankReqMsg.getRspCode())) {
				log.succ("银行:{}返回主号:{}解约成功,内部流水:[],发起方:{}", new Object[] {
						mainBindInfo.getBankId(), reqBody.getIDValue(),
						transIDH, reqMsg.getMsgSender() });
				logger.info("银行:{}返回主号:{}解约成功,内部流水:[],发起方:{}", new Object[] {
						mainBindInfo.getBankId(), reqBody.getIDValue(),
						transIDH, reqMsg.getMsgSender() });
			} else {
				log.warn(
						"银行:{}返回主号:{}解约失败,返回码:{}描述:{},内部流水:[],发起方:{}",
						new Object[] { mainBindInfo.getBankId(),
								reqBody.getIDValue(), bankReqMsg.getRspCode(),
								bankReqMsg.getRspDesc(), transIDH,
								reqMsg.getMsgSender() });
				logger.warn(
						"银行:{}返回主号:{}解约失败,返回码:{}描述:{},内部流水:[],发起方:{}",
						new Object[] { mainBindInfo.getBankId(),
								reqBody.getIDValue(), bankReqMsg.getRspCode(),
								bankReqMsg.getRspDesc(), transIDH,
								reqMsg.getMsgSender() });
			}
			/* 解约以发起方为准，无论银行端是否解约成功，都返回给省成功 */
			logger.debug("CrmMainUnbindAction execute(Object) - end  ");
			return succ(resMsg, resBody);

		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			log.error("移动渠道主号码解约接口!运行期内部异常!内部流水:{},发起方:{}",
					new Object[] { transIDH, reqMsg.getMsgSender() });
			logger.error("移动渠道主号码解约接口!运行期内部异常!,内部流水:{},发起方:{}", new Object[] { transIDH,
					reqMsg.getMsgSender() });
			logger.error("移动渠道主号码解约接口!运行异常:", e);
			if (null != txnLog) {
				if (isUnbindSuccess(mainBindInfo)) {
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.getValue());
				} else {
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
				}
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.getValue());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm
						.getDescByValue(errCode));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				upayCsysTxnLogService.modify(txnLog);
			}
			logger.debug("CrmMainUnbindAction execute(Object) - end  ");
			return succ(resMsg, resBody);
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			log.error("移动渠道主号码解约接口!业务异常!内部流水:{},发起方:{}",
					new Object[] { transIDH, reqMsg.getMsgSender() });
			logger.error("移动渠道主号码解约接口!业务异常!内部流水:{},发起方:{}",
					new Object[] { transIDH, reqMsg.getMsgSender() });
			logger.error("移动渠道主号码解约接口!业务异常:", e);
			if (null != txnLog) {
				if (isUnbindSuccess(mainBindInfo)) {
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.getValue());
				} else {
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
				}
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.getValue());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm
						.getDescByValue(errCode));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				upayCsysTxnLogService.modify(txnLog);
			}
			logger.debug("CrmMainUnbindAction execute(Object) - end  ");
			return succ(resMsg, resBody);
		} catch (Exception e) {
			log.error("移动渠道主号码解约接口!系统异常,内部流水:{},发起方:{}",
					new Object[] { transIDH, reqMsg.getMsgSender() });
			logger.error("移动渠道主号码解约接口!系统异常!内部流水:{},发起方:{}",
					new Object[] { transIDH, reqMsg.getMsgSender() });
			logger.error("移动渠道主号码解约接口!未知异常:", e);
			String errCode = "5A06";
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			if (null != txnLog) {
				if (isUnbindSuccess(mainBindInfo)) {
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.getValue());
				} else {
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
				}
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.getValue());
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm
						.getDescByValue(errCode)+":"+e.getMessage());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				upayCsysTxnLogService.modify(txnLog);
			}
			logger.debug("CrmMainUnbindAction execute(Object) - end  ");
			return succ(resMsg, resBody);
		}
	}

	/**
	 * 初始化符号解约交易流水
	 * 
	 * @param forwadMsg
	 * @param forwardBody
	 * @param info
	 * @param seqId
	 * @param intTxnDate
	 * @param transIDOTime
	 * @param transIDO
	 * @param transCode
	 * @param txnLog
	 * @return
	 */
	private UpayCsysTxnLog initForwardLog(CrmMsgVo reqMsg,
			CrmMainMobileUnBindReqVo reqBody, CrmMsgVo forwadMsg,
			CrmSubUnbindReqVo forwardBody, UpayCsysBindInfo info, Long seqId,
			String intTxnDate, String transIDOTime, String transIDO,UpayCsysTxnLog txnLog) {
		txnLog.setReqTransTm(reqMsg.getTransIDHTime());
//		txnLog.setReqTransId(forwardBody.getTransactionID());
		//改为报文头交易流水号TransIDO
		txnLog.setReqTransId(forwadMsg.getTransIDO());
		txnLog.setReqVersion(forwadMsg.getVersion());
		txnLog.setReqBipCode(forwadMsg.getBIPCode());
		txnLog.setReqActivityCode(forwadMsg.getActivityCode());
		txnLog.setReqDomain(CommonConstant.OrgDomain.UPSS.getValue());
		txnLog.setReqSessionId(forwadMsg.getSessionID());
		txnLog.setReqCnlType(forwardBody.getCnlTyp());
		txnLog.setReqOprId(forwardBody.getTransactionID());
		txnLog.setReqTransDt(StrUtil.subString(reqMsg.getTransIDOTime(), 0, 8));
		txnLog.setRcvCnlType(reqBody.getCnlTyp());
		txnLog.setReqOprDt(forwardBody.getActionDate());
		txnLog.setReqOprTm(reqMsg.getTransIDOTime());
		txnLog.setReqRouteType(forwadMsg.getRouteType());
		txnLog.setReqRouteVal(forwadMsg.getRouteValue());
		txnLog.setReqTranshTm(StrUtil.subString(forwadMsg.getTransIDOTime(), 0,
				14));
		txnLog.setReqTranshId(forwardBody.getTransactionID());
		txnLog.setReqTranshDt(StrUtil.subString(forwadMsg.getTransIDOTime(), 0,
				8));

		txnLog.setRcvDomain(forwadMsg.getMsgReceiver());
		txnLog.setRcvSessionId(Serial
				.genSerialNo(CommonConstant.Sequence.CrmSessionId.getValue()));
		txnLog.setRcvTransTm(forwadMsg.getTransIDOTime());

		txnLog.setSubTime(forwardBody.getSubTime());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
		return txnLog;
	}

	/**
	 * 返回解约结果
	 * 
	 * @param resMsg
	 * @param resBody
	 * @return
	 */
	private CrmMsgVo succ(CrmMsgVo resMsg, CrmMainMobileUnBindResVo resBody) {
		resBody.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		resBody.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
		resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
		resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
		resMsg.setRspType(CommonConstant.CrmRspType.Success.getValue());
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
	 * 修改主号码的签约副号码个数
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
