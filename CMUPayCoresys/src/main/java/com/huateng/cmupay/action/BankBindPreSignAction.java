package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmPreSignBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankSignReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankSignResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSignMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmSignMsgRespVo;
import com.huateng.cmupay.utils.UUIDGenerator;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 
 * @author ning.z
 * 
 * 
 *         银行签约预处理
 */
@Controller("bankBindPreSignAction")
@Scope("prototype")
public class BankBindPreSignAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {
	@Autowired
	private CrmPreSignBus crmPreSignBus;
	@Override
	public BankMsgVo execute(BankMsgVo paramData) throws AppBizException {
		logger.debug("BankBindPreSignAction execute(Object) - start");
		BankMsgVo bankMsgVo = (BankMsgVo) paramData;
		BankMsgVo msgVoRtn = bankMsgVo;
		BankSignReqVo bankSignReqVo = new BankSignReqVo();
		BankSignResVo bankSignResVo = new BankSignResVo();
		UpayCsysTxnLog txnLog = null;
		Long seqId = null;
		String intTxnDate = null;
		String txnSeq = null;
		String intTxnTime = null;
		try {
			MsgHandle.unmarshaller(bankSignReqVo, (String) bankMsgVo.getBody());
			UpayCsysTransCode transCode = bankMsgVo.getTransCode();
			logger.debug("检查订单状态");
			//检查重复订单
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("reqDomain", bankMsgVo.getReqSys());
			params1.put("reqTransId", bankMsgVo.getReqTransID());
			txnLog = upayCsysTxnLogService.findObj(params1);
			if (txnLog != null) {
				intTxnDate = txnLog.getIntTxnDate();
				intTxnTime = txnLog.getIntTxnTime();
				seqId = txnLog.getSeqId();
				txnSeq = txnLog.getIntTxnSeq();// 落地方交易流水号
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvDateTime(intTxnTime);
				msgVoRtn.setRcvTransID(txnSeq);
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.toString());
				logger.info("预签约交易重复:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {bankSignReqVo.getIDValue(), txnSeq, paramData.getReqSys()});
				log.info("预签约交易重复:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {bankSignReqVo.getIDValue(), txnSeq, paramData.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				msgVoRtn.setBody(null);
				return msgVoRtn;
				
			} else {
				txnLog = new UpayCsysTxnLog();
				txnSeq = bankMsgVo.getTxnSeq();
				intTxnDate = bankMsgVo.getTxnDate();
				intTxnTime = bankMsgVo.getTxnTime();
				seqId = bankMsgVo.getSeqId();
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvDateTime(intTxnTime);
				msgVoRtn.setRcvTransID(txnSeq);
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.toString());
				// 记录交易流水
				logger.debug("开始插入一条新的交易流水");
				txnLog.setSeqId(seqId);
				txnLog.setIntTxnDate(intTxnDate);// 内部交易日期
				txnLog.setIntTxnSeq(txnSeq);
				txnLog.setIntTransCode(CommonConstant.TransCode.PreSign.getValue());
				txnLog.setIntTxnTime(intTxnTime);
				txnLog.setPayMode(transCode.getPayMode());
				txnLog.setBussType(transCode.getBussType());
				txnLog.setBussChl(transCode.getBussChl());
				//txnLog.setTxnCat(transCode.getTxnCat());
				txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
				txnLog.setReqBipCode(transCode.getReqBipCode());
				txnLog.setReqActivityCode(bankMsgVo.getActivityCode());
				txnLog.setReqTransId(bankMsgVo.getReqTransID());
				txnLog.setReqTranshDt(intTxnDate);
				txnLog.setReqTranshId(txnSeq);
				txnLog.setReqTranshTm(intTxnTime);
				txnLog.setReqDomain(bankMsgVo.getReqSys());
				txnLog.setReqTransDt(bankMsgVo.getReqDate());
				txnLog.setReqTransTm(bankMsgVo.getReqDateTime());
				txnLog.setReqCnlType(bankMsgVo.getReqChannel());
				txnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
				txnLog.setIdType(bankSignReqVo.getIDType());
				txnLog.setIdValue(bankSignReqVo.getIDValue());
				txnLog.setReqOprDt(bankMsgVo.getReqDate());
				txnLog.setBankId(bankMsgVo.getReqSys());
				txnLog.setReqOprId(bankMsgVo.getReqTransID());
				txnLog.setReqOprTm(bankMsgVo.getReqDateTime());
				txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setSettleDate(bankMsgVo.getReqDate());
				upayCsysTxnLogService.add(txnLog);
			}
			// 报文体内容check
			String validateMsg = this.validateModel(bankSignReqVo);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.debug("body validate success");
			} else {
				String code = validateMsg.split(":")[0];
				logger.error("报文格式错误:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {validateMsg, txnSeq, paramData.getReqSys()});
				log.error("报文格式错误:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {validateMsg, txnSeq, paramData.getReqSys()});
				msgVoRtn.setRspCode(code);
				msgVoRtn.setRspDesc(validateMsg);
				msgVoRtn.setBody(null);
				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
//				txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			//查询号码归属省
//			String reqProvince = upayCsysImsiLdCdService.findProvinceByMobileNumber(bankSignReqVo.getIdValue());
//			String reqProvince = ProvAreaCache.getProvAreaByPrimary(bankSignReqVo.getIdValue());
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(bankSignReqVo.getIDValue());
			String reqProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();

			if (reqProvince == null) {
				logger.error("手机号码不正确:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {bankSignReqVo.getIDValue(), txnSeq, paramData.getReqSys()});
				log.error("手机号码不正确:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {bankSignReqVo.getIDValue(), txnSeq, paramData.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
				msgVoRtn.setBody(null);
				txnLog.setIdProvince(null);
				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
//				txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			txnLog.setIdProvince(reqProvince);
			String mainOrgId = SysMapCache.getProvCd(reqProvince).getSysCd();
			//TODO 没有权限就意味着不在试点范围内，移动要求返回012A16 org_code表还需要配置
			//校验接收方机交易状态
//			String orgTransFlag = offOrgTrans(bankMsgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode());
			//校验接收方机交易状态
			String orgTransFlag = offOrgTrans(bankMsgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode(), 
					provincePhoneNum == null ? CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType() : provincePhoneNum.getPhoneNumFlag());
//			boolean orgTransFlag = isO2OTransOn(bankMsgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode());
//			boolean orgTransFlag = orgStatusCheck(mainOrgId);
			if (orgTransFlag != null) {
				logger.warn("接收方机构关闭:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {mainOrgId, txnSeq, paramData.getReqSys()});
				log.info("接收方机构关闭:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {mainOrgId, txnSeq, paramData.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				msgVoRtn.setBody(null);
				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc()+orgTransFlag);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			logger.debug("内部交易流水号:{},拼装发往CRM报文",
					new Object[]{txnSeq});
			CrmMsgVo subReq = new CrmMsgVo();
			subReq.setVersion(ExcConstant.CRM_VERSION);
			subReq.setTestFlag(testFlag);
			subReq.setBIPCode(CommonConstant.Bip.Bis03.getValue());
			subReq.setActivityCode(CommonConstant.CrmTrans.Crm04.getValue());
			subReq.setActionCode(CommonConstant.ActionCode.Requset.getValue());
			subReq.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			subReq.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			subReq.setRouteType(CommonConstant.RouteType.RoutePhone.toString());
			subReq.setSessionID(txnSeq);
			subReq.setRouteValue(bankSignReqVo.getIDValue());
			subReq.setTransIDO(txnSeq);
			subReq.setTransIDOTime(StrUtil.subString(intTxnTime, 0, 14));
			subReq.setMsgReceiver(mainOrgId);
			CrmSignMsgReqVo signVo = new CrmSignMsgReqVo();
			signVo.setBankID(bankMsgVo.getReqSys());
			signVo.setIDType(bankSignReqVo.getIDType());
			signVo.setIDValue(bankSignReqVo.getIDValue());
			subReq.setBody(signVo);
			txnLog.setIdProvince(reqProvince);
			txnLog.setRcvActivityCode(subReq.getActivityCode());
			txnLog.setRcvBipCode(subReq.getBIPCode());
			txnLog.setRcvDomain(subReq.getMsgReceiver());
			txnLog.setRcvRouteType(subReq.getRouteType());
			txnLog.setRcvRouteVal(subReq.getRouteValue());
			txnLog.setRcvTransDt(intTxnDate);
			txnLog.setRcvTransId(txnSeq);
			txnLog.setRcvSessionId(txnSeq);
			//
			//txnLog.setRcvOprId(UUIDGenerator.generateUUID());
			txnLog.setRcvTransTm(StrUtil.subString(intTxnTime, 0, 14));
			Map<String, Object> params = new HashMap<String, Object>();
			UpayCsysBindInfo upayCsysBindInfo = new UpayCsysBindInfo();
			//发往省预签约请求
			CrmMsgVo crmMsgVoRtn = crmPreSignBus.execute(subReq, params,
					txnLog, upayCsysBindInfo);
			logger.debug("内部交易流水号:{},接收到移动返回消息",
					new Object[]{txnSeq});
			txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
			txnLog.setRcvSessionId(crmMsgVoRtn.getSessionID());
			if (crmMsgVoRtn.getBody() == null || crmMsgVoRtn.getBody().equals("")) {
				log.error("预签约返回报文体为空,内部交易流水号:{},业务应答方:{}" , 
						new Object[] {txnSeq,crmMsgVoRtn.getTransIDH()});
				logger.error("预签约返回报文体为空,内部交易流水号:{},业务应答方:{}" , 
						new Object[] {txnSeq,crmMsgVoRtn.getTransIDH()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());				
				msgVoRtn.setBody(null);
				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
				txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
//				txnLog.setRcvRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			//判断应答报文体是否为空
			CrmSignMsgRespVo crmSignMsgRespVo = crmMsgVoRtn.getBody() == null ? null
					: (CrmSignMsgRespVo) crmMsgVoRtn.getBody();
			if (!crmSignMsgRespVo.getRspCode().equals(RspCodeConstant.Crm.CRM_0000.getValue())) {
				log.error("CRM应答为失败,内部交易流水号:{},业务应答方:{},应答码:{},应答描述:{}" , 
						new Object[] {txnSeq,crmMsgVoRtn.getTransIDH(),crmSignMsgRespVo.getRspCode(),crmSignMsgRespVo.getRspInfo()});
				logger.error("CRM应答为失败,内部交易流水号:{},业务应答方:{},应答码:{},应答描述:{}" , 
						new Object[] {txnSeq,crmMsgVoRtn.getTransIDH(),crmSignMsgRespVo.getRspCode(),crmSignMsgRespVo.getRspInfo()});
				String errCode = BankErrorCodeCache.getBankErrCode(crmSignMsgRespVo.getRspCode());
				bankSignResVo.setHomeProv(reqProvince);
				msgVoRtn.setBody(bankSignResVo);
				msgVoRtn.setRspCode(errCode);
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
				txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
				txnLog.setRcvSubRspCode(crmSignMsgRespVo.getRspCode());
				txnLog.setRcvSubRspDesc(crmSignMsgRespVo.getRspInfo());
//				txnLog.setRcvRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			//交易成功,给银行应答
			log.succ("CRM应答为成功,内部交易流水号:{},业务应答方:{},应答码:{},应答描述:{}" , 
					new Object[] {txnSeq,crmMsgVoRtn.getTransIDH(),crmSignMsgRespVo.getRspCode(),crmSignMsgRespVo.getRspInfo()});
			logger.info("CRM应答为成功,内部交易流水号:{},业务应答方:{},应答码:{},应答描述:{}" , 
					new Object[] {txnSeq,crmMsgVoRtn.getTransIDH(),crmSignMsgRespVo.getRspCode(),crmSignMsgRespVo.getRspInfo()});
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			bankSignResVo.setSessionID(crmSignMsgRespVo.getSessionID());
			bankSignResVo.setUserCat(crmSignMsgRespVo.getUserCat());
			bankSignResVo.setAutoPayable(crmSignMsgRespVo.getAutoPayable());
			bankSignResVo.setDefRechThreshold(crmSignMsgRespVo.getDefRechThreshold());
			bankSignResVo.setMaxRechThreshold(crmSignMsgRespVo.getMaxRechThreshold());
			bankSignResVo.setDefRechAmount(crmSignMsgRespVo.getDefRechAmount());
			bankSignResVo.setMaxRechAmount(crmSignMsgRespVo.getMaxRechAmount());
			bankSignResVo.setHomeProv(reqProvince);
			msgVoRtn.setBody(bankSignResVo);
//			txnLog.setPayType(crmSignMsgRespVo.getAutoPayable());
			txnLog.setRechAmount(crmSignMsgRespVo.getDefRechAmount());
			txnLog.setRechThreshold(crmSignMsgRespVo.getDefRechThreshold());
			txnLog.setMaxRechAmount(crmSignMsgRespVo.getMaxRechAmount());
			txnLog.setMaxRechThreshold(crmSignMsgRespVo.getMaxRechThreshold());
			txnLog.setUserCat(crmSignMsgRespVo.getUserCat());
			txnLog.setIdProvince(reqProvince);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			txnLog.setReserved2(crmSignMsgRespVo.getSessionID());
			txnLog.setReserved3(crmSignMsgRespVo.getAutoPayable());
			txnLog.setChlRspCode(msgVoRtn.getRspCode());
			txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
			txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
			txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
			txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
			txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
			txnLog.setRcvRspType(CommonConstant.CrmRspType.Success.toString());
			txnLog.setRcvSubRspCode(crmSignMsgRespVo.getRspCode());
			txnLog.setRcvSubRspDesc(crmSignMsgRespVo.getRspInfo());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			logger.debug("BankBindPreSignAction execute(Object) - end");
			return msgVoRtn;
		} catch (AppRTException e) {
			log.error("内部异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {txnSeq, paramData.getReqSys()});
			logger.error("内部异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {txnSeq, paramData.getReqSys()});
			logger.error("内部异常:",e);
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setBody(bankSignResVo);
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
//			txnLog.setChlRspType(CommonConstant.CrmRspType.SysErr.toString());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		} catch (AppBizException e) {
			log.error("业务异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {txnSeq, paramData.getReqSys()});
			logger.error("业务异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {txnSeq, paramData.getReqSys()});
			logger.error("业务异常:",e);
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setBody(bankSignResVo);
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
//			txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		} catch (Exception e) {
			log.error("未知异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {txnSeq, paramData.getReqSys()});
			logger.error("未知异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {txnSeq, paramData.getReqSys()});		
			logger.error("未知异常:",e);
			
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
			
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setBody(bankSignResVo);
			if (CommonConstant.TxnStatus.InitStatus.toString().equals(txnLog.getStatus())
					|| "".equals(StringUtil.toTrim(txnLog.getStatus()))) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			}
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
//			txnLog.setChlRspType(CommonConstant.CrmRspType.SysErr.toString());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		}
	}
}
