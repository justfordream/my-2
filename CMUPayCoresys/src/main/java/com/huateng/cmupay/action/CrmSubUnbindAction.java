package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.huateng.cmupay.jms.business.crm.CrmSubUnbindBus;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubBindMsgUpdateResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubUnbindReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubUnbindResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 移动渠道副号码解约
 * 
 * @author zeng.j 1 查询内部交易代码 2 记录交易流水-请求 3 验证报文格式 4 查询签约关系，验证签约状态 5
 *         发起方、主号码、副号码归属省判断 6 转发请求 6.1 更新交易流水-请求 6.2 验证接收方机构交易权限 6.3 发起交易 6.4
 *         接受应答，更新交易流水-应答 7 更新签约关系 8 更新交易流水-应答
 *   
 */
@Controller("crmSubUnbindAction")
@Scope("prototype")
public class CrmSubUnbindAction extends AbsBaseAction<CrmMsgVo, CrmMsgVo> {
	private static final Logger logger = LoggerFactory
			.getLogger(CrmSubUnbindAction.class);

	@Autowired
	private CrmSubUnbindBus crmSubUnbindBus;

	@Override
	public CrmMsgVo execute(CrmMsgVo paramData) throws AppBizException {
		logger.debug("start 副号码解约交易");
		/* 请求报文 */
		CrmMsgVo reqMsg = paramData;
		/* 交易流水 */
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		CrmSubUnbindReqVo reqBody = new CrmSubUnbindReqVo();
		/* 应答报文 */
		CrmMsgVo resMsg = reqMsg;
		CrmSubUnbindResVo resBody = new CrmSubUnbindResVo();
		String transIDH = reqMsg.getTxnSeq();
		String transIDHTime = null;
		String intTxnDate = null;
		Long seqId = null;
		UpayCsysBindInfo info = null;

		try {
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			resBody.setActionDate(reqBody.getActionDate());
			resBody.setSubID(reqBody.getSubID());
			resBody.setTransactionID(reqBody.getTransactionID());
			logger.debug("移动渠道副号码解约,info: main_id_value:{} , id_value:{} ",
					reqBody.getMainIDValue(), reqBody.getIDValue());
			reqMsg.setBody(reqBody);
			/** 交易代码 */
			UpayCsysTransCode transCode = reqMsg.getTransCode();
			boolean notLog=false;
			if(!StringUtils.isBlank(reqBody.getTransactionID())){
				Map<String, Object> param = new HashMap<String, Object>();
				
				// 防止字段超过指定长度从而跳过数据库订单重复校验的bug
				String reqDomain = reqMsg.getMsgSender();
				String reqOprId = reqBody.getTransactionID();
				if(reqDomain != null && reqDomain.length() > 4) {
					log.warn("移动渠道副号码解约接口:MsgSender代码{}超过4位，在订单重复查询时，只取前4位，,发起方:{},操作流水:{},内部流水:{}", new Object[] {reqDomain,
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					logger.warn("移动渠道副号码解约接口:MsgSender代码{}超过4位，在订单重复查询时，只取前4位，,发起方:{},操作流水:{},内部流水:{}", new Object[] {reqDomain,
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					reqDomain = reqDomain.substring(0, 4);
				}
				if(reqOprId != null && reqOprId.length() > 32) {
					log.warn("移动渠道副号码解约接口:TransactionID代码{}超过32位，在订单重复查询时，只取前32位，,发起方:{},操作流水:{},内部流水:{}", new Object[] {reqOprId,
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					logger.warn("移动渠道副号码解约接口:TransactionID代码{}超过32位，在订单重复查询时，只取前32位，,发起方:{},操作流水:{},内部流水:{}", new Object[] {reqOprId,
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
					logger.debug("移动渠道副号码解约,订单重复,发起方:{},发起方操作流水:{},内部流水:{}", new Object[] {
							reqMsg.getMsgSender(), reqBody.getTransactionID(),
							transIDH });
					intTxnDate = upayCsysTxnLog.getIntTxnDate();
					transIDHTime = upayCsysTxnLog.getIntTxnTime();
					seqId = upayCsysTxnLog.getSeqId();
					transIDH = upayCsysTxnLog.getIntTxnSeq();// 落地方交易流水号
					txnLog = upayCsysTxnLog;
					notLog=true;
				}
			}
			if(!notLog){
				transIDHTime = paramData.getTxnTime();
				intTxnDate = reqMsg.getTxnDate();
				seqId = reqMsg.getSeqId();
				txnLog.setSeqId(seqId);
				txnLog.setIntTxnSeq(transIDH);
				txnLog.setIntTxnDate(intTxnDate);
				txnLog.setIntTxnTime(transIDHTime);
				txnLog.setIntMqSeq(reqMsg.getMqSeq());
//				txnLog.setTxnCat(transCode.getTxnCat());
				txnLog.setBussType(transCode.getBussType());
				txnLog.setBussChl(transCode.getBussChl());
				txnLog.setIntTransCode(transCode.getTransCode());
				txnLog.setPayMode(transCode.getPayMode());
				txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
				txnLog.setReqVersion(reqMsg.getVersion());
				txnLog.setReqBipCode(reqMsg.getBIPCode());
				txnLog.setReqActivityCode(reqMsg.getActivityCode());
				txnLog.setReqDomain(reqMsg.getMsgSender());
				txnLog.setReqRouteType(reqMsg.getRouteType());
				txnLog.setReqRouteVal(reqMsg.getRouteValue());
				txnLog.setReqSessionId(reqMsg.getSessionID());
				txnLog.setReqCnlType(reqBody.getCnlTyp());
				txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No
						.toString());
				txnLog.setSettleDate(StrUtil.subString(
						reqMsg.getTransIDOTime(), 0, 8));

				txnLog.setReqTransId(reqMsg.getTransIDO());
				txnLog.setReqTransTm(reqMsg.getTransIDOTime());
				txnLog.setReqTransDt(StrUtil.subString(
						reqMsg.getTransIDOTime(), 0, 8));
				txnLog.setReqTranshDt(intTxnDate);
//				txnLog.setReqTranshId(resMsg.getTransIDH());
				txnLog.setReqTranshId(transIDH);
				txnLog.setReqTranshTm(transIDHTime);

				txnLog.setReqOprDt(reqBody.getActionDate());
				txnLog.setReqOprId(reqBody.getTransactionID());
				txnLog.setReqOprTm(reqMsg.getTransIDOTime());

				txnLog.setMainIdType(reqBody.getMainIDType());
				txnLog.setMainIdValue(reqBody.getMainIDValue());
				txnLog.setMainFlag(CommonConstant.Mainflag.Slave.getValue());
				txnLog.setIdType(reqBody.getIDType());
				txnLog.setIdValue(reqBody.getIDValue());
				txnLog.setSubId(reqBody.getSubID());
				txnLog.setSubTime(reqBody.getSubTime());
				txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				// if (isRepeatTrans(reqBody.getTransactionID(),
				// reqMsg.getMsgSender())) {
				// logger.info("该交易为重复交易 req_domain:{} , req_opr_id: {}",
				// reqMsg.getMsgSender(), reqBody.getTransactionID());
				// resBody.setRspCode(MessageHandler.getCrmErrCode("3A17"));
				// resBody.setRspInfo(MessageHandler.getCrmErrMsg("3A17"));
				// resMsg.setRspCode(MessageHandler.getWzwErrCode("2998"));
				// resMsg.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
				// resMsg.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				// resMsg.setBody(resBody);
				// return resMsg;
				// }
				txnLog.setSettleDate(DateUtil.getDateyyyyMMdd());
				logger.debug("移动渠道副号码解约,begin 添加副号码解约交易流水");
				upayCsysTxnLogService.add(txnLog);
				logger.debug("移动渠道副号码解约,end 添加副号码解约交易流水");
			} else {
				// 重复订单处理
				resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				resMsg.setRouteType(CommonConstant.RouteType.RouteProvince
						.getValue());
				resMsg.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				resMsg.setRouteValue(reqMsg.getRouteValue());
				resMsg.setTransIDH(transIDH);
				resMsg.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				
				CrmSubUnbindResVo res = new CrmSubUnbindResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc());
				res.setTransactionID(reqBody.getTransactionID());
				res.setActionDate(reqBody.getActionDate());
				res.setSubID(reqBody.getSubID());
				resMsg.setBody(res);
				
				return resMsg;
			}
			
			
			resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			resMsg.setRouteType(CommonConstant.RouteType.RouteProvince
					.getValue());
			resMsg.setRouteValue(reqMsg.getRouteValue());
			resMsg.setTransIDH(transIDH);
			resMsg.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
			/* 验证消息 */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				log.warn("移动渠道副号码解约,报文格式错误:{},内部流水:{},发起方:{}", new Object[] { checkrtn,
						transIDH, reqMsg.getMsgSender() });
				logger.warn("移动渠道副号码解约,报文格式错误:{},内部流水:{},发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getMsgSender() });

				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A99.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				resMsg.setRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
				resMsg.setRspDesc(RspCodeConstant.Crm.CRM_2998.getDesc());
				resMsg.setRspType(txnLog.getChlRspType());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A99.getDesc() + checkrtn);
				resMsg.setBody(resBody);

				logger.debug("end 副号码解约交易");
				return resMsg;
			}

			/* 副号码签约信息 */
			Map<String, Object> subParams = new HashMap<String, Object>();
			if (null != reqBody.getMainIDType()
					&& !"".equals(reqBody.getMainIDType())) {
				subParams.put("mainIdType", reqBody.getMainIDType());
			}
			if (null != reqBody.getMainIDValue()
					&& !"".equals(reqBody.getMainIDValue())) {
				subParams.put("mainIdValue", reqBody.getMainIDValue());
			}
			subParams.put("idType", reqBody.getIDType());
			subParams.put("idValue", reqBody.getIDValue());
			subParams.put("mainFlag", CommonConstant.Mainflag.Slave.getValue());
			info = upayCsysBindInfoService.findObj(subParams);
			if (null == info
					|| !CommonConstant.BindStatus.Bind.getValue().equals(
							info.getStatus())) {
				log.warn("移动渠道副号码解约,副号:{}签约信息不存在,内部流水:{},发起方:{}",
						new Object[] { reqBody.getIDValue(),
						transIDH, reqMsg.getMsgSender() });
				logger.warn("移动渠道副号码解约,副号:{}签约信息不存在,内部流水:{},发起方:{}",
						new Object[] { reqBody.getIDValue(),
						transIDH, reqMsg.getMsgSender() });
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
				resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
				resMsg.setRspType(txnLog.getChlRspType());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
				resMsg.setBody(resBody);
				
				logger.debug("end 副号码解约交易");
				return resMsg;
			}
			txnLog.setIdProvince(info.getIdProvince());
			logger.debug(
					"query success ,sub bind info , id_value:{} , main_id_value :{}",
					info.getIdValue(), info.getMainIdValue());
			/* 主号码签约信息 */
			Map<String, Object> mainParams = new HashMap<String, Object>();
			if (null != reqBody.getMainIDType()
					&& !"".equals(reqBody.getMainIDType())) {
				mainParams.put("idType", reqBody.getMainIDType());
			}
			if (null != reqBody.getMainIDValue()
					&& !"".equals(reqBody.getMainIDValue())) {
				mainParams.put("idVlaue", reqBody.getMainIDValue());
			}
			if (null != info && null != info.getMainIdValue()
					&& !"".equals(info.getMainIdValue())) {
				mainParams.put("idValue", info.getMainIdValue());
			}
			mainParams.put("mainFlag",
					CommonConstant.Mainflag.Master.getValue());
			mainParams.put("subId", reqBody.getSubID());
			UpayCsysBindInfo mainInfo = upayCsysBindInfoService
					.findObj(mainParams);
			logger.debug("query success ,main bind info , main_id_value{}",
					info.getMainIdValue());

			txnLog.setMainIdProvince(info.getMainIdProvince());
			txnLog.setBankId(info.getBankId());
//			txnLog.setBankAcctType(info.getBankAcctType());
//			txnLog.setBankAccId(info.getBankAccId());
			// txnLog.setPayedType(info.getPayType());
			txnLog.setRechAmount(info.getRechAmount());
			txnLog.setRechThreshold(info.getRechThreshold());
			txnLog.setMaxRechAmount(info.getMaxRechAmount());
			txnLog.setMaxRechThreshold(info.getMaxRechThreshold());
			txnLog.setUserId(info.getUserId());
			txnLog.setUserName(info.getUserName());
			txnLog.setUserType(info.getUserType());
			txnLog.setBankAccId(info.getBankAccId());
			txnLog.setBankAcctType(info.getBankAcctType());
			// txnLog.setSignStatus(info.getStatus());
			txnLog.setMainFlag(info.getMainFlag());

			/** 归属省判断 */
			/** 发起方归属省 */
			String reqProvince = SysMapCache.getSysCd(reqMsg.getMsgSender())
					.getAreaCd();
			/** 主号码归属省 */
			String mainIdProvince = info.getMainIdProvince().trim();
			/** 副号码归属省 */
			String subIdProvince = info.getIdProvince().trim();
			String subOrgId = SysMapCache.getProvCd(subIdProvince).getSysCd();
			String mainOrgId = SysMapCache.getProvCd(mainIdProvince).getSysCd();
			if (!reqProvince.equals(mainIdProvince)
					&& !reqProvince.equals(subIdProvince)) {
				log.warn("移动渠道副号码解约,发起方:{},主号码归属地:{},副号码归属地:{}都不相同,内部流水:{}",
						new Object[] { reqProvince, mainIdProvince,
								subIdProvince, transIDH });
				logger.warn("移动渠道副号码解约,发起方:{},主号码归属地:{},副号码归属地:{}都不相同,内部流水:{}",
						new Object[] { reqProvince, mainIdProvince,
								subIdProvince, transIDH });

				txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A05.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A05.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				resMsg.setRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
				resMsg.setRspDesc(RspCodeConstant.Crm.CRM_2998.getDesc());
				resMsg.setRspType(txnLog.getChlRspType());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_2A05.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_2A05.getDesc());
				resMsg.setBody(resBody);
				
				logger.debug("end 副号码解约交易");
				return resMsg;
			}
			if (reqProvince.equals(mainIdProvince)
					&& reqProvince.equals(subIdProvince)) {
				log.info("移动渠道副号码解约,发起方:{},主号码归属地:{},副号码归属地:{}都相同,内部流水:{}",
						new Object[] { reqProvince, mainIdProvince,
								subIdProvince, transIDH });
				logger.info("移动渠道副号码解约,发起方:{},主号码归属地:{},副号码归属地:{}都相同,内部流水:{}",
						new Object[] { reqProvince, mainIdProvince,
								subIdProvince,transIDH });
				txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());

				info.setClTxnSeq(reqMsg.getTransIDO());
				info.setClTxnDate(DateUtil.getDateyyyyMMdd());
				info.setClTxnTime(DateUtil.getDateyyyyMMddHHmmss());
				info.setClSubTime(DateUtil.getDateyyyyMMddHHmmss());
				info.setClOrgId(reqMsg.getMsgSender());
				info.setClCnlType(reqBody.getCnlTyp());
				info.setSettleDate(reqBody.getActionDate());
				info.setStatus(CommonConstant.BindStatus.UnBind.getValue());
				 upayCsysBindInfoService
				 .modifyLogAndBinfInfo(null, info, txnLog);
//				upayCsysBindInfoService
//						.modifyTxnLogAndDelBindInfo(info, txnLog);
				log.succ("移动渠道副号码解约,解约副号:{}成功,内部流水:{},发起方:{}",
						new Object[] { info.getIdValue(), transIDH,
								reqMsg.getMsgSender() });
				logger.info("移动渠道副号码解约,解约副号:{}成功,内部流水:{},发起方:{}",
						new Object[] { info.getIdValue(), transIDH,
								reqMsg.getMsgSender() });

				Map<String, Object> noPrams = new HashMap<String, Object>();
				noPrams.put("mainFlag",
						CommonConstant.Mainflag.Slave.getValue());
				noPrams.put("mainIdValue", reqBody.getMainIDValue());
				noPrams.put("status", CommonConstant.BindStatus.Bind.getValue());
				noPrams.put("subId", reqBody.getSubID());
				int subNum = upayCsysBindInfoService
						.findSubCountByParams(noPrams);
				if (null != mainInfo) {
					mainInfo.setSubNum(subNum);
					upayCsysBindInfoService.modify(mainInfo);
					log.succ(
							"移动渠道副号码解约,修改主号:{}签约副号数成功,内部流水:{},发起方:{}",
							new Object[] { mainInfo.getIdValue(),
									transIDH, reqMsg.getMsgSender() });
					logger.info(
							"移动渠道副号码解约,修改主号:{}签约副号数成功,内部流水:{},发起方:{}",
							new Object[] { mainInfo.getIdValue(),
									transIDH, reqMsg.getMsgSender() });
				}
				
				logger.debug("end 副号码解约交易");
				return succ(resMsg, resBody);
			}
			/** 转发交易 */
			/** 报文头 */
//			String sessionId = Serial
//					.genSerialNo(CommonConstant.Sequence.CrmSessionId
//							.getValue());
			CrmMsgVo forwardMsg = new CrmMsgVo();
			forwardMsg.setTransCode(transCode);
			forwardMsg.setVersion(reqMsg.getVersion());
			forwardMsg.setTestFlag(reqMsg.getTestFlag());
			forwardMsg.setBIPCode(CommonConstant.Bip.Bis09.getValue());
			forwardMsg
					.setActivityCode(CommonConstant.CrmTrans.Crm03.getValue());
			forwardMsg.setActionCode(CommonConstant.ActionCode.Requset
					.getValue());
			forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone
					.getValue());
			forwardMsg.setRouteValue(info.getIdValue());
			forwardMsg.setSessionID(transIDH);
//			forwardMsg.setSessionID(sessionId);
			forwardMsg.setTransIDO(transIDH);
			forwardMsg.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
			forwardMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());

			/** 报文体 */
			CrmSubUnbindReqVo forwardBody = new CrmSubUnbindReqVo();
//			String subTransactionId = Serial.genSerialNos(CommonConstant.Sequence.OprId.toString());
			//TransactionID设置成32位
			String subTransactionId = Serial.genSerialNum(CommonConstant.Sequence.OprId.toString());

			forwardBody.setDealType(CommonConstant.BindDealType.Unbind
					.getValue());
			forwardBody.setSubID(info.getSubId());
			forwardBody.setMainIDType(info.getMainIdType());
			forwardBody.setMainIDValue(info.getMainIdValue());
			forwardBody.setIDType(info.getIdType());
			forwardBody.setIDValue(info.getIdValue());
			forwardBody.setTransactionID(subTransactionId);
			forwardBody.setSubTime(info.getSignTxnTime());
			forwardBody.setActionDate(DateUtil.getDateyyyyMMdd());
			forwardBody.setCnlTyp(reqBody.getCnlTyp());

			boolean checkFlag = true;// 转发机构交易权限标识
			String forwardOrg = null;// 转发方机构代码

			if (reqProvince.equals(mainIdProvince)
					&& !reqProvince.equals(subIdProvince)) {
				logger.info(
						"移动渠道副号码解约,发起方归属地:{} 与主号码归属地:{}相同，与副号码归属地:{} 不相同,内部流水:{}",
						new Object[] { reqProvince, mainIdProvince,
								subIdProvince, transIDH });
				forwardOrg = subOrgId;
				forwardMsg.setRouteValue(info.getIdValue());
			}

			if (reqProvince.equals(subIdProvince)
					&& !reqProvince.equals(mainIdProvince)) {
				logger.info("移动渠道副号码解约,发起方归属地:{}与副号码归属地相同:{},与主号码归属地:{}不相同,内部流水:{}",
						new Object[] { reqProvince, subIdProvince,
								mainIdProvince,transIDH });
				forwardOrg = mainOrgId;
				forwardMsg.setRouteValue(reqBody.getMainIDValue());
			}
			logger.info("移动渠道副号码解约,转发副号码解约交易,接收方:{},内部流水:{},发起方:{}", new Object[] {
					forwardOrg,transIDH, reqMsg.getMsgSender() });
			forwardMsg.setMsgReceiver(forwardOrg);

			checkFlag = orgStatusCheck(forwardOrg);
			/* 解约处理 */
			info.setClCnlType(reqBody.getCnlTyp());
			info.setClOrgId(reqMsg.getMsgSender());//
			info.setClTxnDate(DateUtil.getDateyyyyMMddHHmmss());
			info.setClTxnSeq(reqMsg.getTransIDO());
			info.setClTxnTime(reqMsg.getTransIDOTime());
			info.setStatus(CommonConstant.BindStatus.UnBind.getValue());
			info.setSettleDate(reqBody.getActionDate());
			info.setClSubTime(DateUtil.getDateyyyyMMddHHmmss());

			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			 upayCsysBindInfoService.modifyLogAndBinfInfo(null, info, txnLog);
//			upayCsysBindInfoService.modifyTxnLogAndDelBindInfo(info, txnLog);
			log.succ("移动渠道副号码解约,解约副号:{}成功,内部流水:{},发起方:{}",
					new Object[] { info.getIdValue(), transIDH,
							reqMsg.getMsgSender() });
			logger.info("移动渠道副号码解约,解约副号:{}成功,内部流水:{},发起方:{}",
					new Object[] { info.getIdValue(), transIDH,
							reqMsg.getMsgSender() });

			Map<String, Object> noPrams = new HashMap<String, Object>();
			noPrams.put("mainFlag", CommonConstant.Mainflag.Slave.getValue());
			noPrams.put("mainIdValue", reqBody.getMainIDValue());
//			noPrams.put("mainIdValue", reqBody.getiDValue());//
			noPrams.put("status", CommonConstant.BindStatus.Bind.getValue());
			noPrams.put("subId", reqBody.getSubID());
			int subNum = upayCsysBindInfoService.findSubCountByParams(noPrams);
			if (null != mainInfo) {
				mainInfo.setSubNum(subNum);
				upayCsysBindInfoService.modify(mainInfo);
				log.succ(
						"移动渠道副号码解约,修改主号:{}签约副号数成功,内部流水:{},发起方:{}",
						new Object[] { mainInfo.getIdValue(),
								transIDH, reqMsg.getMsgSender() });
				logger.info(
						"移动渠道副号码解约,修改主号:{}签约副号数成功,内部流水:{},发起方:{}",
						new Object[] { mainInfo.getIdValue(),
								transIDH, reqMsg.getMsgSender() });
			}

			if (checkFlag) {
				txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
				txnLog.setRcvActivityCode(forwardMsg.getActivityCode());
				txnLog.setRcvBipCode(forwardMsg.getBIPCode());
				txnLog.setRcvCnlType(forwardBody.getCnlTyp());
				txnLog.setRcvDomain(forwardMsg.getMsgReceiver());
				txnLog.setRcvRouteType(forwardMsg.getRouteType());
				txnLog.setRcvRouteVal(forwardMsg.getRouteValue());
				txnLog.setRcvSessionId(forwardMsg.getSessionID());

				txnLog.setRcvTransId(forwardMsg.getTransIDO());
				txnLog.setRcvTransTm(forwardMsg.getTransIDOTime());
				txnLog.setRcvTransDt(StrUtil.subString(
						forwardMsg.getTransIDOTime(), 0, 8));
				txnLog.setRcvOprId(forwardBody.getTransactionID());
				txnLog.setRcvOprDt(forwardBody.getActionDate());

				forwardMsg.setBody(forwardBody);
				log.info("移动渠道副号码解约,转发副号解约,发起方:{},主号归属省:{},副号归属省:{},接收方:{},内部流水:{}",
						new Object[] { reqProvince, mainIdProvince,
								subIdProvince, forwardOrg, transIDH });
				logger.info("移动渠道副号码解约,转发副号解约,发起方:{},主号归属省:{},副号归属省:{},接收方:{},内部流水:{}",
						new Object[] { reqProvince, mainIdProvince,
								subIdProvince, forwardOrg, transIDH });
				forwardMsg = crmSubUnbindBus.execute(forwardMsg,
						new HashMap<String, Object>(), txnLog, info);
				logger.debug("return 转发副号码解约交易");
				CrmSubUnbindResVo forwardRtBody = new CrmSubUnbindResVo();
				MsgHandle.unmarshaller(forwardRtBody, forwardMsg.getBody()
						.toString());

				txnLog.setRcvSubRspCode(forwardRtBody.getRspCode());
				txnLog.setRcvSubRspDesc(forwardRtBody.getRspInfo());
				txnLog.setRcvTranshId(forwardMsg.getTransIDH());
				txnLog.setRcvTranshTm(forwardMsg.getTransIDHTime());
				txnLog.setRcvTranshDt(StrUtil.subString(
						forwardMsg.getTransIDHTime(), 0, 8));

				upayCsysTxnLogService.modify(txnLog);

				if (RspCodeConstant.Crm.CRM_0000.getValue().equals(
						forwardMsg.getRspCode())
						&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
								forwardRtBody.getRspCode())) {
					log.succ("移动渠道副号码解约,省:{}返回副号:{}解约成功,内部流水:{},发起方:{}", new Object[] {
							forwardOrg, info.getIdValue(), transIDH,
							reqMsg.getMsgSender() });
					logger.info("移动渠道副号码解约,省:{}返回副号:{}解约成功,内部流水:{},发起方:{}", new Object[] {
							forwardOrg, info.getIdValue(), transIDH,
							reqMsg.getMsgSender() });
				} else if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
						forwardMsg.getRspCode())) {
					log.warn(
							"移动渠道副号码解约,副号:{}解约超时,内部流水:{},发起方:{}",
							new Object[] { info.getIdValue(),
									transIDH, reqMsg.getMsgSender() });
					logger.warn(
							"移动渠道副号码解约,副号:{}解约超时,内部流水:{},发起方:{}",
							new Object[] { info.getIdValue(),
									transIDH, reqMsg.getMsgSender() });
				} else {
					log.warn(
							"移动渠道副号码解约,省:{}返回副号:{}解约失败,返回码:{}描述:{},内部流水:{},发起方:{}",
							new Object[] { forwardOrg, info.getIdValue(),
									forwardRtBody.getRspCode(),
									forwardRtBody.getRspInfo(),
									transIDH, reqMsg.getMsgSender() });
					logger.warn(
							"移动渠道副号码解约,省:{}返回副号:{}解约失败,返回码:{}描述:{},内部流水:{},发起方:{}",
							new Object[] { forwardOrg, info.getIdValue(),
									forwardRtBody.getRspCode(),
									forwardRtBody.getRspInfo(),
									transIDH, reqMsg.getMsgSender() });
				}

			} else {
				log.warn("移动渠道副号码解约,接收:{}方交易权限关闭,内部流水:{},发起方:{}", new Object[] {
						forwardOrg, transIDH, reqMsg.getMsgSender() });
				logger.warn("移动渠道副号码解约,接收:{}方交易权限关闭,内部流水:{},发起方:{}", new Object[] {
						forwardOrg, transIDH, reqMsg.getMsgSender() });
			}
			
			logger.debug("end 副号码解约交易");
			return succ(resMsg, resBody);
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			log.error("移动渠道副号码解约,运行期内部异常!内部流水:{},发起方:{}", new Object[] {
					transIDH, reqMsg.getMsgSender() });
			logger.error(
					"移动渠道副号码解约,运行期内部异常:{},内部流水:{},发起方:{}",
					new Object[] { errCode, transIDH, reqMsg.getMsgSender() });
			logger.error("运行异常:",e);
			if (isUnbindSucc(info)) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			} else {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			}

			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			
			logger.debug("end 副号码解约交易");
			return succ(resMsg, resBody);
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			log.error(
					"移动渠道副号码解约,业务异常!内部流水:{},发起方:{}",
					new Object[] {transIDH,
							reqMsg.getMsgSender() });
			logger.error("移动渠道副号码解约,业务异常:{},内部流水:{},发起方:{}", new Object[] { errCode,
					transIDH, reqMsg.getMsgSender() });
			logger.error("移动渠道副号码解约,业务异常:",e);
			if (isUnbindSucc(info)) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			} else {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			}
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			logger.debug("end 副号码解约交易");
			return succ(resMsg, resBody);
		} catch (Exception e) {
			log.error("移动渠道副号码解约,系统异常!内部流水:{},发起方:{}", new Object[] { transIDH,
					reqMsg.getMsgSender() });
			logger.error(
					"移动渠道副号码解约,系统异常,内部流水:{},发起方:{}",
					new Object[] {transIDH, reqMsg.getMsgSender() });
			logger.error("移动渠道副号码解约,系统异常:",e);
			if (isUnbindSucc(info)) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			} else {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			}
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			logger.debug("end 副号码解约交易");
			return succ(resMsg, resBody);
		}
	}

	/**
	 * 返回解约结果
	 * 
	 * @param resMsg
	 * @param resBody
	 * @return
	 */
	private CrmMsgVo succ(CrmMsgVo resMsg, CrmSubUnbindResVo resBody) {
		resBody.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		resBody.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
		resMsg.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		resMsg.setRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		resMsg.setRspType(CommonConstant.CrmRspType.Success.getValue());
		resMsg.setBody(resBody);
		return resMsg;
	}

	/**
	 * 副号是否解约成功
	 * 
	 * @param info
	 * @return
	 */
	private boolean isUnbindSucc(UpayCsysBindInfo info) {
		return null != info
				&& CommonConstant.BindStatus.UnBind.getValue().equals(
						info.getStatus());
	}

	public CrmSubUnbindBus getCrmSubUnbindBus() {
		return crmSubUnbindBus;
	}

	public void setCrmSubUnbindBus(CrmSubUnbindBus crmSubUnbindBus) {
		this.crmSubUnbindBus = crmSubUnbindBus;
	}

}
