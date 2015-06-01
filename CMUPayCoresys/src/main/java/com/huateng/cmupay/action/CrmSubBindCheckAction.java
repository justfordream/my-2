package com.huateng.cmupay.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmSubBindCheckBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysDictCode;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubBindCheckReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSubBindCheckResVo;
import com.huateng.cmupay.utils.UUIDGenerator;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;

/**
 * @author ning.z 副号码签约校验 1．对省中心发送的报文进行格式校验 2． 记录交易流水号。 3．
 *         校验移动机构状态，同时校验移动省中心的交易权限。 4． 查询签约关系判断副号码是否可以签约 5．
 *         根据手机号查询到手机归属地，并更具归属地找到副号码所在地的路由信息。 6． 将副号码签约校验请求转发给移动省中心 7．
 *         接收移动省中心副号码签约校验应答 8． 更新交易流水 9． 返回应答给发起方
 */
@Controller("crmSubBindCheckAction")
@Scope("prototype")
public class CrmSubBindCheckAction extends AbsBaseAction<CrmMsgVo, CrmMsgVo> {
	@Autowired
	private CrmSubBindCheckBus crmSubBindCheckBus;

	// private @Value("${sign.slave.count}")
	// Integer signSlaveCount;
	@Override
	public CrmMsgVo execute(CrmMsgVo paramData) throws AppBizException {
		logger.debug("CrmSubBindCheckAction execute(Object) - start");
		CrmMsgVo crmMsgVo = (CrmMsgVo) paramData;
		CrmSubBindCheckReqVo subBindCheckReqVo = new CrmSubBindCheckReqVo();
		MsgHandle.unmarshaller(subBindCheckReqVo, (String) crmMsgVo.getBody());
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		CrmMsgVo msgVoRtn = crmMsgVo;
		String transIDH = crmMsgVo.getTxnSeq();// 落地方交易流水号
		String transIDHTime = null;// 落地方处理时间
		String intTxnDate = null;// TODO 从数据库获取
		Long seqId = null;
		UpayCsysTransCode transCode = crmMsgVo.getTransCode(); // 获取交易代码
		try {
			logger.debug("副号码签约校验,重复订单检查");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqDomain", crmMsgVo.getMsgSender());
			param.put("reqTransId", crmMsgVo.getTransIDO());
			UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService
					.findObj(param);
			if (upayCsysTxnLog != null) {
				log.info("副号码签约校验,订单重复,内部交易流水号:{},业务发起方:{}", new Object[] { transIDH,
						crmMsgVo.getMsgSender() });
				logger.info("副号码签约校验,订单重复,内部交易流水号:{},业务发起方:{},ReqOprId:{}",
						new Object[] { transIDH, crmMsgVo.getMsgSender(),
								crmMsgVo.getTransIDO() });
				
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc());
				msgVoRtn.setBody(res);
				logger.debug("CrmSubBindCheckAction execute(Object) - end  ");
				return msgVoRtn;
				
				/*
				 * 20131224 注释 weiyi 重复的订单直接返回到前置并提示订单重复
				intTxnDate = upayCsysTxnLog.getIntTxnDate();
				transIDHTime = upayCsysTxnLog.getIntTxnTime();
				seqId = upayCsysTxnLog.getSeqId();
				transIDH = upayCsysTxnLog.getIntTxnSeq();// 落地方交易流水号
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				txnLog = upayCsysTxnLog;
				*/
				
			} else {
				seqId = crmMsgVo.getSeqId();
				transIDHTime = crmMsgVo.getTxnTime();// 落地方处理时间
				intTxnDate = crmMsgVo.getTxnDate();// TODO 从数据库获取
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setTransIDH(transIDH);// 落地方交易流水号
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				/* 交易流水 */
				logger.debug("intTxnSeq:'{}'记录交易流水--start ", transIDH);
				txnLog.setSeqId(seqId);
				txnLog.setIntTxnDate(intTxnDate);// 内部交易日期
				txnLog.setIntTxnSeq(transIDH);
				txnLog.setIntTransCode(transCode.getTransCode());
				txnLog.setIntTxnTime(transIDHTime);
				txnLog.setPayMode(transCode.getPayMode());
				txnLog.setBussType(transCode.getBussType());
				txnLog.setBussChl(transCode.getBussChl());
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
				txnLog.setReqTransDt(StrUtil.subString(
						crmMsgVo.getTransIDOTime(), 0, 8));
				txnLog.setReqOprDt(txnLog.getReqTransDt());
//				txnLog.setReqOprId(UUIDGenerator.generateUUID());
				txnLog.setReqOprId("");
				txnLog.setReqOprTm(crmMsgVo.getTransIDOTime());
				txnLog.setReqTransTm(crmMsgVo.getTransIDOTime());
				txnLog.setReqTranshId(transIDH);
				txnLog.setReqTranshDt(intTxnDate);
				txnLog.setReqTranshTm(transIDHTime);
				txnLog.setMainIdType(subBindCheckReqVo.getMainIDType());
				txnLog.setMainIdValue(subBindCheckReqVo.getMainIDValue());
				txnLog.setMainFlag(CommonConstant.Mainflag.Slave.getValue());
				txnLog.setIdType(subBindCheckReqVo.getSubIDType());
				txnLog.setIdValue(subBindCheckReqVo.getSubIDValue());
				txnLog.setOriOrgId(null);
				txnLog.setOriOprTransId(null);
				txnLog.setOriReqDate(null);
				txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No
						.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				txnLog.setSettleDate(DateUtil.getDateyyyyMMdd());
				upayCsysTxnLogService.add(txnLog);
			}
			// 报文体内容check
			logger.debug("副号码签约校验,intTxnSeq:'{}',报文体check ---start ", transIDH);
			String validateMsg = this.validateModel(subBindCheckReqVo);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.info("副号码签约校验,内部交易流水号:{},body体校验成功", new Object[] { transIDH });
			} else {
				logger.warn("副号码签约校验,内部交易流水号:{},报文体校验失败:{},业务发起方:{}", new Object[] {
						transIDH, validateMsg, paramData.getMsgSender() });
				log.warn("副号码签约校验,内部交易流水号:{},报文体校验失败:{},业务发起方:{}", new Object[] {
						transIDH, validateMsg, paramData.getMsgSender() });
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_4A99.getDesc()
						+ validateMsg);
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A99.getDesc()
						+ validateMsg);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("CrmSubBindCheckAction execute(Object) - end");
				return msgVoRtn;
			}
			// 副号码的归属省查询
			// String subProvince = upayCsysImsiLdCdService
			// .findProvinceByMobileNumber(subBindCheckReqVo
			// .getSubIDValue());
			// String subProvince =
			// ProvAreaCache.getProvAreaByPrimary(subBindCheckReqVo
			// .getSubIDValue());
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(subBindCheckReqVo
					.getSubIDValue());
			String subProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
			if (subProvince == null) {
				logger.warn("副号码签约校验,副号码不正确:{},内部交易流水号:{},业务发起方:{}",
						new Object[] { subBindCheckReqVo.getSubIDValue(),
								transIDH, paramData.getMsgSender() });
				log.warn("副号码签约校验,副号码不正确:{},内部交易流水号:{},业务发起方:{}",
						new Object[] { subBindCheckReqVo.getSubIDValue(),
								transIDH, paramData.getMsgSender() });
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_2A22.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_2A22.getDesc());
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A22.getDesc());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A22.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("CrmSubBindCheckAction execute(Object) - end");
				return msgVoRtn;
			}
			txnLog.setIdProvince(subProvince);
			// 主号码归属省查询
			// String mainProvince = upayCsysImsiLdCdService
			// .findProvinceByMobileNumber(subBindCheckReqVo
			// .getMainIDValue());
			// String mainProvince =
			// ProvAreaCache.getProvAreaByPrimary(subBindCheckReqVo.getMainIDValue());
			ProvincePhoneNum mainProvincePhoneNum = findProvinceByMobileNumber(subBindCheckReqVo
					.getMainIDValue());
			String mainProvince = mainProvincePhoneNum == null ? null : mainProvincePhoneNum.getProvinceCode();
			if (mainProvince == null) {
				logger.warn("副号码签约校验,主号码不正确:{},内部交易流水号:{},业务发起方:{}",
						new Object[] { subBindCheckReqVo.getMainIDValue(),
								transIDH, paramData.getMsgSender() });
				log.warn("副号码签约校验,主号码不正确:{},内部交易流水号:{},业务发起方:{}",
						new Object[] { subBindCheckReqVo.getMainIDValue(),
								transIDH, paramData.getMsgSender() });
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_2A22.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_2A22.getDesc());
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A22.getDesc());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A22.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("CrmSubBindCheckAction execute(Object) - end");
				return msgVoRtn;
			}
			// 副号码机构号
			String subOrgId = SysMapCache.getProvCd(subProvince).getSysCd();
			/* 主号码签约信息 */
			logger.debug("intTxnSeq:'{}',查询主号签约信息", transIDH);
			Map<String, Object> mainParams = new HashMap<String, Object>();
			mainParams.put("idValue", subBindCheckReqVo.getMainIDValue());
			mainParams.put("idType", subBindCheckReqVo.getMainIDType());
			 mainParams.put("mainFlag",CommonConstant.Mainflag.Master.getValue());
			 mainParams.put("Status",
			 CommonConstant.BindStatus.Bind.toString());
			UpayCsysBindInfo mainBindInfo = upayCsysBindInfoService
					.findObj(mainParams);
			// 根据银行校验副号码归属地是否有权限
			if (null == mainBindInfo) {
				logger.warn(
						"副号码签约校验,内部交易流水号:{},主号:{}签约关系不存在,业务发起方:{}",
						new Object[] { transIDH,
								subBindCheckReqVo.getMainIDValue(),
								paramData.getMsgSender() });
				log.warn(
						"副号码签约校验,内部交易流水号:{},主号:{}签约关系不存在,业务发起方:{}",
						new Object[] { transIDH,
								subBindCheckReqVo.getMainIDValue(),
								paramData.getMsgSender() });
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_2A13.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_2A13.getDesc());
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A13.getDesc());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A13.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("CrmSubBindCheckAction execute(Object) - end");
				return msgVoRtn;
			} 
//			else if (!(mainBindInfo.getMainFlag().equals(
//					CommonConstant.Mainflag.Master.getValue()) && mainBindInfo
//					.getStatus().equals(
//							CommonConstant.BindStatus.Bind.getValue()))) {
//				logger.info(
//						"内部交易流水号:{},主号:{}签约关系不存在,业务发起方:{}",
//						new Object[] { transIDH,
//								subBindCheckReqVo.getMainIDValue(),
//								paramData.getMsgSender() });
//				log.info(
//						"内部交易流水号:{},主号:{}签约关系不存在,业务发起方:{}",
//						new Object[] { transIDH,
//								subBindCheckReqVo.getMainIDValue(),
//								paramData.getMsgSender() });
//				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				msgVoRtn.setRspType(parseRspType(RspCodeConstant.Crm.CRM_2A13.getValue()));
//				CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
//				res.setRspCode(RspCodeConstant.Crm.CRM_2A13.getValue());
//				res.setRspInfo(RspCodeConstant.Crm.CRM_2A13.getDesc());
//				msgVoRtn.setBody(res);
//				txnLog.setChlRspType(parseRspType(RspCodeConstant.Crm.CRM_2A13.getValue()));
//				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A13.getDesc());
//				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				txnLog.setChlRspType(msgVoRtn.getRspType());
//				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A13.getValue());
//				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//				upayCsysTxnLogService.modify(txnLog);
//				return msgVoRtn;
//			}
			// Map<String, Object> bankParams = new HashMap<String, Object>();
			// bankParams.put("codeValue1", mainBindInfo.getBankId());
			// UpayCsysDictCode dictCode =
			// upayCsysDictCodeService.findObj(bankParams);
			// String bankTOrgs[] =
			// (dictCode.getCodeValue2()).split(CommonConstant.SpeSymbol.COMMA_MARK.getValue());
			
			
			// 通过主号的号码和副号的号码类型去字典表中查
			UpayCsysDictCode upayCsysDictCode = DictCodeCache.getBankTOrg(mainBindInfo.getBankId(), provincePhoneNum.getPhoneNumFlag());
			String bankTOrgs[] = upayCsysDictCode == null ? null : upayCsysDictCode.getCodeValue2().split(CommonConstant.SpeSymbol.COMMA_MARK.getValue());
			List<String> bankIdOrgs = Arrays.asList(bankTOrgs);
			if (bankTOrgs == null || !bankIdOrgs.contains(subOrgId)) {
				logger.warn("副号码签约校验,内部交易流水号:{},副号归属省:{}不在试点范围,业务发起方:{}", new Object[] {
						transIDH, subOrgId, paramData.getMsgSender() });
				log.warn("副号码签约校验,内部交易流水号:{},副号归属省:{}不在试点范围,业务发起方:{}", new Object[] {
						transIDH, subOrgId, paramData.getMsgSender() });
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_2A21.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_2A21.getDesc());
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A21.getDesc());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A21.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("CrmSubBindCheckAction execute(Object) - end");
				return msgVoRtn;
			}
			
			
			
			// else if (mainBindInfo.getBankId().equals(
			// CommonConstant.BankOrgCode.SPDB.getValue())&&!orgSPDBId.contains(subOrgId)){
			// logger.info("内部交易流水号:{},副号归属省:{}不在试点范围,业务发起方:{}",
			// new Object[]{transIDH,subOrgId,paramData.getMsgSender()});
			// log.info("内部交易流水号:{},副号归属省:{}不在试点范围,业务发起方:{}",
			// new Object[]{transIDH,subOrgId,paramData.getMsgSender()});
			// msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			// msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			// msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			// CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
			// res.setRspCode(RspCodeConstant.Crm.CRM_2A21.getValue());
			// res.setRspInfo(RspCodeConstant.Crm.CRM_2A21.getDesc());
			// msgVoRtn.setBody(res);
			// txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			// .toString());
			// txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A21.getDesc());
			// txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			// txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			// txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			// .toString());
			// txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A21.getValue());
			// txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			// txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			// upayCsysTxnLogService.modify(txnLog);
			// return msgVoRtn;
			// }
			// else if (mainBindInfo.getBankId().equals(
			// CommonConstant.BankOrgCode.CCB.getValue())
			// && !orgCCBId.contains(subOrgId)) {
			// logger.info("内部交易流水号:{},副号归属省:{}不在试点范围,业务发起方:{}",
			// new Object[]{transIDH,subOrgId,paramData.getMsgSender()});
			// log.info("内部交易流水号:{},副号归属省:{}不在试点范围,业务发起方:{}",
			// new Object[]{transIDH,subOrgId,paramData.getMsgSender()});
			// msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			// msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			// msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			// CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
			// res.setRspCode(RspCodeConstant.Crm.CRM_2A21.getValue());
			// res.setRspInfo(RspCodeConstant.Crm.CRM_2A21.getDesc());
			// msgVoRtn.setBody(res);
			// txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			// .toString());
			// txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A21.getDesc());
			// txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			// txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			// txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			// .toString());
			// txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A21.getValue());
			// txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			// txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			// upayCsysTxnLogService.modify(txnLog);
			// return msgVoRtn;
			// }else if(mainBindInfo.getBankId().equals("0009")&&
			// !testBankId.contains(subOrgId)){
			// logger.info("内部交易流水号:{},副号归属省:{}不在试点范围,业务发起方:{}",
			// new Object[]{transIDH,subOrgId,paramData.getMsgSender()});
			// log.info("内部交易流水号:{},副号归属省:{}不在试点范围,业务发起方:{}",
			// new Object[]{transIDH,subOrgId,paramData.getMsgSender()});
			// msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			// msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			// msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			// CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
			// res.setRspCode(RspCodeConstant.Crm.CRM_2A21.getValue());
			// res.setRspInfo(RspCodeConstant.Crm.CRM_2A21.getDesc());
			// msgVoRtn.setBody(res);
			// txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			// .toString());
			// txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A21.getDesc());
			// txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			// txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			// txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			// .toString());
			// txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A21.getValue());
			// txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			// txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			// upayCsysTxnLogService.modify(txnLog);
			// return msgVoRtn;
			// }
//			logger.debug("intTxnSeq:'{}',查询副号签约信息", transIDH);
//			Map<String, Object> subParams = new HashMap<String, Object>();
//			subParams.put("idValue", subBindCheckReqVo.getSubIDValue());
//			subParams.put("idType", subBindCheckReqVo.getSubIDType());
//			UpayCsysBindInfo subBindInfo = upayCsysBindInfoService
//					.findObj(subParams);
//			if (subBindInfo != null
//					&& subBindInfo.getMainFlag().equals(
//							CommonConstant.Mainflag.Slave.getValue())
//					&& (subBindInfo.getStatus())
//							.equals(CommonConstant.BindStatus.Bind.toString())) {
//				logger.info("内部交易流水号:{},业务发起方:{},副号:'{}'已签约",
//						new Object[] { transIDH, paramData.getMsgSender(),
//								subBindInfo.getIdValue() });
//				log.info("内部交易流水号:{},业务发起方:{},副号:'{}'已签约",
//						new Object[] { transIDH, paramData.getMsgSender(),
//								subBindInfo.getIdValue() });
//				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				msgVoRtn.setRspType(parseRspType(RspCodeConstant.Crm.CRM_2A15.getValue()));
//				CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
//				if (subBindCheckReqVo.getMainIDValue().equals(
//						subBindInfo.getMainIdValue())) {
//					res.setRspCode(RspCodeConstant.Crm.CRM_2A15.getValue());
//					res.setRspInfo(RspCodeConstant.Crm.CRM_2A15.getDesc());
//					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A15
//							.getDesc());
//					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A15
//							.getValue());
//				} else {
//					res.setRspCode(RspCodeConstant.Crm.CRM_2A16.getValue());
//					res.setRspInfo(RspCodeConstant.Crm.CRM_2A16.getDesc());
//					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A16
//							.getValue());
//					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A15
//							.getDesc());
//				}
//				msgVoRtn.setBody(res);
//				txnLog.setChlRspType(msgVoRtn.getRspType());
//				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//				upayCsysTxnLogService.modify(txnLog);
//				return msgVoRtn;
//			}
			UpayCsysRouteInfo route = null;
			// 校验接收方机构状态
			logger.debug("副号码签约校验,intTxnSeq:'{}',校验接收方机构", transIDH);
			boolean orgFlag = true;
			orgFlag = orgStatusCheck(subOrgId);
			if (orgFlag) {
				CrmMsgVo subReq = new CrmMsgVo();
				subReq.setRouteInfo(route);
				subReq.setTransCode(transCode);
				subReq.setVersion(crmMsgVo.getVersion());
				subReq.setTestFlag(crmMsgVo.getTestFlag());
				subReq.setBIPCode(CommonConstant.Bip.Bis08.getValue());
				subReq.setActivityCode(CommonConstant.CrmTrans.Crm14.getValue());
				subReq.setActionCode(CommonConstant.ActionCode.Requset
						.getValue());
				subReq.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
				subReq.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
				subReq.setRouteType(CommonConstant.RouteType.RoutePhone
						.toString());
				subReq.setRouteValue(subBindCheckReqVo.getSubIDValue());
				subReq.setTransIDO(transIDH);
				subReq.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
				subReq.setSessionID(transIDH);
				subReq.setMsgSender(crmMsgVo.getMsgReceiver());
				subReq.setMsgReceiver(subOrgId);
				subReq.setPriority(null);
				subReq.setServiceLevel(null);
				txnLog.setRcvTransId(transIDH);
				txnLog.setRcvTransTm(transIDHTime);
				txnLog.setRcvTransDt(intTxnDate);
				CrmSubBindCheckReqVo CheckReqVo2 = new CrmSubBindCheckReqVo();
				CheckReqVo2.setMainIDType(subBindCheckReqVo.getMainIDType());
				CheckReqVo2.setMainIDValue(subBindCheckReqVo.getMainIDValue());
				CheckReqVo2.setSubIDType(subBindCheckReqVo.getSubIDType());
				CheckReqVo2.setSubIDValue(subBindCheckReqVo.getSubIDValue());
				subReq.setBody(CheckReqVo2);
				logger.debug("副号码签约校验,发往省CRM请求--start");
				txnLog.setRcvStartTm(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				Map<String, Object> params = new HashMap<String, Object>();
				CrmMsgVo msgVaRtn = crmSubBindCheckBus.execute(subReq, params,
						txnLog,null);
				logger.debug("副号码签约校验,发往省CRM请求--end");
				txnLog.setRcvEndTm(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
//				txnLog.setRcvOprId(UUIDGenerator.generateUUID());
				txnLog.setRcvOprId("");
				txnLog.setRcvOprDt(DateUtil.getDateyyyyMMdd());
				txnLog.setRcvOprTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
						msgVaRtn.getRspCode())) {
					log.warn("副号码签约校验,网状网应答超时,内部交易流水号:{},CRM应答流水:{}", new Object[] {
							transIDH, msgVaRtn.getTransIDH() });
					logger.warn("副号码签约校验,网状网应答超时,内部交易流水号:{},CRM应答流水:{}", new Object[] {
							transIDH, msgVaRtn.getTransIDH() });
					CrmSubBindCheckResVo subCheckRes = new CrmSubBindCheckResVo();
					txnLog.setRcvVersion(msgVaRtn.getVersion());
					txnLog.setIdProvince(subProvince);
					txnLog.setMainIdProvince(mainProvince);
					txnLog.setRcvActivityCode(subReq.getActivityCode());
					txnLog.setRcvBipCode(subReq.getBIPCode());
					txnLog.setRcvDomain(subOrgId);
					txnLog.setRcvRouteType(subReq.getRouteType());
					txnLog.setRcvRouteVal(subReq.getRouteValue());
					txnLog.setRcvSessionId(msgVaRtn.getSessionID());
					txnLog.setRcvTranshId(msgVaRtn.getTransIDH());
					txnLog.setRcvTranshTm(msgVaRtn.getTransIDHTime());
					txnLog.setRcvTranshDt(StrUtil.subString(
							msgVaRtn.getTransIDHTime(), 0, 8));
					txnLog.setRcvRspCode(msgVaRtn.getRspCode());
					txnLog.setRcvRspDesc(msgVaRtn.getRspDesc());
					txnLog.setRcvRspType(msgVaRtn.getRspType());
					txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_5A07
							.getValue());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.toString());
					upayCsysTxnLogService.modify(txnLog);
					msgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());
					msgVoRtn.setRspType(msgVaRtn.getRspType());
					subCheckRes.setRspCode(RspCodeConstant.Crm.CRM_5A07
							.getValue());
					subCheckRes.setRspInfo(RspCodeConstant.Crm.CRM_5A07
							.getDesc());
					msgVoRtn.setBody(subCheckRes);
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					
					logger.debug("CrmSubBindCheckAction execute(Object) - end");
					return msgVoRtn;
				} else if (msgVaRtn.getBody() == null
						|| msgVaRtn.getBody().equals("")) {
					// else if (msgVaRtn.getBody() == null) {
					log.warn("副号码签约校验,网状网应答超时,内部交易流水号:{},CRM应答流水:{}", new Object[] {
							transIDH, msgVaRtn.getTransIDH() });
					logger.warn("副号码签约校验,网状网应答超时,内部交易流水号:{},CRM应答流水:{}", new Object[] {
							transIDH, msgVaRtn.getTransIDH() });
					CrmSubBindCheckResVo subCheckRes = new CrmSubBindCheckResVo();
					String errCode = msgVaRtn.getRspCode();
					errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
					txnLog.setRcvVersion(msgVaRtn.getVersion());
					txnLog.setIdProvince(subProvince);
					txnLog.setMainIdProvince(mainProvince);
					txnLog.setRcvActivityCode(subReq.getActivityCode());
					txnLog.setRcvBipCode(subReq.getBIPCode());
//					txnLog.setRcvDomain(subReq.getHomeDomain());
					txnLog.setRcvDomain(subOrgId);
					txnLog.setRcvRouteType(subReq.getRouteType());
					txnLog.setRcvRouteVal(subReq.getRouteValue());
					txnLog.setRcvSessionId(msgVaRtn.getSessionID());
					txnLog.setRcvTranshId(msgVaRtn.getTransIDH());
					txnLog.setRcvTranshTm(msgVaRtn.getTransIDHTime());
					txnLog.setRcvTranshDt(StrUtil.subString(
							msgVaRtn.getTransIDHTime(), 0, 8));
					txnLog.setRcvRspCode(msgVaRtn.getRspCode());
					txnLog.setRcvRspDesc(msgVaRtn.getRspDesc());
					txnLog.setRcvRspType(msgVaRtn.getRspType());// TODO
					txnLog.setChlRspCode(errCode);
					txnLog.setChlRspType(msgVoRtn.getRspType());
					txnLog.setChlRspDesc(RspCodeConstant.Crm
							.getDescByValue(errCode));
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.toString());
					upayCsysTxnLogService.modify(txnLog);
					msgVoRtn.setRspCode(errCode);
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.getDescByValue(errCode));
//					msgVoRtn.setRspCode(MessageHandler.getWzwErrCode(errCode));
//					msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg(errCode));
					msgVoRtn.setRspType(msgVaRtn.getRspType());
					subCheckRes.setRspCode(RspCodeConstant.Crm.CRM_5A07
							.getValue());
					subCheckRes.setRspInfo(RspCodeConstant.Crm.CRM_5A07
							.getDesc());
					msgVoRtn.setBody(subCheckRes);
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					
					logger.debug("CrmSubBindCheckAction execute(Object) - end");
					return msgVoRtn;
				}
				CrmSubBindCheckResVo subCheckRes = new CrmSubBindCheckResVo();
				MsgHandle
						.unmarshaller(subCheckRes, (String) msgVaRtn.getBody());
				String rtuCode = null;
				String rtuDesc = null;
				String subRtuCode = null;
				String subRtuDesc = null;
				//只为转换5999错误
				String subRspCode5999 = "5999";
				String rtnSubRspCode = null ; 
				
				if (subCheckRes.getRspCode().equals(
						RspCodeConstant.Crm.CRM_0000.getValue())
						&& (msgVaRtn.getRspCode())
								.equals(RspCodeConstant.Wzw.WZW_0000.getValue())) {
					logger.info("副号码签约校验,内部交易流水号:{},业务发起方:{},CRM应答码:{}",
							new Object[] { transIDH, paramData.getMsgSender(),
									subCheckRes.getRspCode() });
					log.succ("副号码签约校验,内部交易流水号:{},业务发起方:{},CRM应答码:{}",
							new Object[] { transIDH, paramData.getMsgSender(),
									subCheckRes.getRspCode() });
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.toString());
					rtuCode = msgVaRtn.getRspCode();
//					rtuDesc = RspCodeConstant.Wzw.getDescByValue(rtuCode);
					rtuDesc = RspCodeConstant.Crm.getDescByValue(rtuCode);
					subRtuCode = subCheckRes.getRspCode();
//					subRtuDesc = RspCodeConstant.Wzw.getDescByValue(subRtuCode);
					subRtuDesc = RspCodeConstant.Crm.getDescByValue(subRtuCode);
					
					rtnSubRspCode = subCheckRes.getRspCode();
					
				} else {
					logger.warn("副号码签约校验,内部交易流水号:{},业务发起方:{},CRM应答码:{}",
							new Object[] { transIDH, paramData.getMsgSender(),
									subCheckRes.getRspCode() });
					log.warn("副号码签约校验,内部交易流水号:{},业务发起方:{},CRM应答码:{}",
							new Object[] { transIDH, paramData.getMsgSender(),
									subCheckRes.getRspCode() });
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.toString());
					rtuCode = CrmErrorCodeCache.getCrmErrCode(msgVaRtn.getRspCode());
//					rtuDesc = RspCodeConstant.Wzw.getDescByValue(rtuCode);
					rtuDesc = RspCodeConstant.Crm.getDescByValue(rtuCode);
					
					
					/*
					 *  20131217 add by weiyi 
					 * 断返回应答码是否为5999,是则改为2A99 暂时固定为此值,未将此值写入CommonConstant类
					
					if(subRspCode5999.equals(subCheckRes.getRspCode())){
						subRtuCode = RspCodeConstant.Crm.CRM_2A99.getValue();
						subRtuDesc = RspCodeConstant.Crm.CRM_2A99.getDesc();
						rtnSubRspCode = RspCodeConstant.Crm.CRM_2A99.getValue();
					}
					else{
						subRtuCode = CrmErrorCodeCache.getCrmErrCode(subCheckRes.getRspCode());
//						subRtuDesc = RspCodeConstant.Wzw.getDescByValue(subRtuCode);
						subRtuDesc = RspCodeConstant.Crm.getDescByValue(subRtuCode);
						rtnSubRspCode = subCheckRes.getRspCode();
					} */
					
					subRtuCode = RspCodeConstant.Crm.getCrmSecondValueByCode(subCheckRes.getRspCode());
//					subRtuDesc = RspCodeConstant.Wzw.getDescByValue(subRtuCode);
					subRtuDesc = RspCodeConstant.Crm.getCrmSecondDescByCode(subRtuCode);
					rtnSubRspCode = subRtuCode;

					
				}
				
				
				
				txnLog.setRcvVersion(msgVaRtn.getVersion());
				txnLog.setIdProvince(subProvince);
				txnLog.setMainIdProvince(mainProvince);
				txnLog.setRcvActivityCode(subReq.getActivityCode());
				txnLog.setRcvBipCode(subReq.getBIPCode());
//				txnLog.setRcvDomain(subReq.getHomeDomain());
				txnLog.setRcvDomain(subOrgId);
				txnLog.setRcvRouteType(subReq.getRouteType());
				txnLog.setRcvRouteVal(subReq.getRouteValue());
				txnLog.setRcvSessionId(msgVaRtn.getSessionID());// TODO
				txnLog.setRcvTranshId(msgVaRtn.getTransIDH());
				txnLog.setRcvTranshTm(msgVaRtn.getTransIDHTime());
				txnLog.setRcvTranshDt(StrUtil.subString(
						msgVaRtn.getTransIDHTime(), 0, 8));
				txnLog.setRcvRspCode(msgVaRtn.getRspCode());
				txnLog.setRcvRspDesc(msgVaRtn.getRspDesc());
				txnLog.setRcvRspType(msgVaRtn.getRspType());// TODO
//				txnLog.setRcvSubRspCode(subCheckRes.getRspCode());
				
				//设置为转换后的返回码
				txnLog.setRcvSubRspCode(rtnSubRspCode);
				
				txnLog.setRcvSubRspDesc(subCheckRes.getRspInfo());
				txnLog.setChlRspCode(msgVaRtn.getRspCode());
				txnLog.setChlRspType(msgVaRtn.getRspType());
				txnLog.setChlRspDesc(msgVaRtn.getRspDesc());// 发起方应答描述
//				txnLog.setChlSubRspCode(subCheckRes.getRspCode());
				
				//设置为转换后的返回码
				txnLog.setChlSubRspCode(rtnSubRspCode);
				
				txnLog.setChlSubRspDesc(subCheckRes.getRspInfo());

				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog); // 更新交易流水
//				msgVoRtn.setRspCode(MessageHandler.getWzwErrCode(msgVaRtn.getRspCode()));
//				msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg(msgVaRtn.getRspCode()));
				
//				msgVoRtn.setRspCode(msgVaRtn.getRspCode());
//				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.getDescByValue(msgVaRtn.getRspCode()));
				msgVoRtn.setRspCode(rtuCode);
				msgVoRtn.setRspDesc(rtuDesc);
				
				msgVoRtn.setRspType(msgVaRtn.getRspType());
//				subCheckRes.setRspCode(subCheckRes.getRspCode());
//				subCheckRes.setRspInfo(RspCodeConstant.Crm.getDescByValue(subCheckRes.getRspCode()));
				subCheckRes.setRspCode(subRtuCode);
				subCheckRes.setRspInfo(subRtuDesc);
//				subCheckRes.setRspCode(MessageHandler.getCrmErrCode(subCheckRes.getRspCode()));
				msgVoRtn.setBody(subCheckRes);
				logger.debug("CrmSubBindCheckAction execute(Object) - end");
				return msgVoRtn;
			} else {
				logger.warn("副号码签约校验,接收方机构关闭:{},内部交易流水号:{},业务发起方:{}", new Object[] {
						subOrgId, transIDH, paramData.getMsgSender() });
				log.warn("副号码签约校验,接收方机构关闭:{},内部交易流水号:{},业务发起方:{}", new Object[] {
						subOrgId, transIDH, paramData.getMsgSender() });
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
				res.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
				res.setRspInfo(RspCodeConstant.Crm.CRM_3A35.getDesc());
				msgVoRtn.setBody(res);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A35.getDesc());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("CrmSubBindCheckAction execute(Object) - end");
				return msgVoRtn;
			}
		} catch (AppRTException e) {
			log.error("副号码签约校验,内部异常!内部交易流水号:{},业务发起方:{}", new Object[] { transIDH,
					paramData.getMsgSender() });
			logger.error(
					"副号码签约校验,内部异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { e.getCode(), transIDH,
							paramData.getMsgSender() });
			logger.error("副号码签约校验,内部异常:", e);
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);//
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
			res.setRspCode(errCode);
			res.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			msgVoRtn.setBody(res);
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			
			logger.debug("CrmSubBindCheckAction execute(Object) - end");
			return msgVoRtn;
		} catch (AppBizException e) {
			log.error("副号码签约校验,业务异常!内部交易流水号:{},业务发起方:{}", new Object[] { transIDH,
					paramData.getMsgSender() });
			logger.error(
					"副号码签约校验,业务异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { e.getCode(), transIDH,
							paramData.getMsgSender() });
			logger.error("副号码签约校验,业务异常:", e);
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
			res.setRspCode(errCode);
			res.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			msgVoRtn.setBody(res);
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlRspType(msgVoRtn.getRspType());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			
			logger.debug("CrmSubBindCheckAction execute(Object) - end");
			return msgVoRtn;
		} catch (Exception e) {
			log.error("副号码签约校验,未知异常!内部交易流水号:{},业务发起方:{}", new Object[] { transIDH,
					paramData.getMsgSender() });
			logger.error("副号码签约校验,未知异常!内部交易流水号:{},业务发起方:{}", new Object[] { transIDH,
					paramData.getMsgSender() });
			logger.error("副号码签约校验,未知异常:", e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A06.getDesc()+":"+e.getMessage());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			CrmSubBindCheckResVo res = new CrmSubBindCheckResVo();
			res.setRspCode(RspCodeConstant.Crm.CRM_5A06.getValue());

			//注释掉输出到应答报文的错误信息(该信息可能包含SQL异常) 20131213 modify by weiyi
//			String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_230?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_230);
//			res.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc()+":"+errDesc);
			
			res.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc());
			
			upayCsysTxnLogService.modify(txnLog);
			msgVoRtn.setBody(res);
			
			logger.debug("CrmSubBindCheckAction execute(Object) - end");
			return msgVoRtn;
		}

	}
}
