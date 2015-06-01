package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.huateng.cmupay.jms.business.crm.CrmQueryMobileInfoBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankUserInfoQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankUserInfoQueryResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmUseInfoQueryReq;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmUserInfoQueryRes;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.cmupay.utils.UUIDGenerator;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 银行发起的缴费查询交易
 * 
 * @author zhang_ning 1. 银行手机号码信息查询请求，发送至统一支付系统； 2. 统一支付系统将请求转发至手机号码归属省CRM系统； 3.
 *         省CRM系统查询结果返回给统一支付系统； 4. 统一支付系统把查询结果返回银行系统
 */
@Controller("bankQueryMobileInfoAction")
@Scope("prototype")
public class BankQueryMobileInfoAction extends
		AbsBaseAction<BankMsgVo, BankMsgVo> {
	public final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CrmQueryMobileInfoBus crmQueryMobileInfoBus;
	public BankMsgVo execute(BankMsgVo paramData) throws AppBizException {
		logger.debug("BankQueryMobileInfoAction execute(Object) - start");
		BankMsgVo msgVo = (BankMsgVo) paramData;
		BankMsgVo msgVoRtn = msgVo;
		BankUserInfoQueryReqVo bodyMsgVo = new BankUserInfoQueryReqVo();
		BankUserInfoQueryResVo bankQueryInfo = new BankUserInfoQueryResVo();
		CrmUserInfoQueryRes crmUserInfoRes = new CrmUserInfoQueryRes();
		MsgHandle.unmarshaller(bodyMsgVo, (String) msgVo.getBody());
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		CrmMsgVo subReq = new CrmMsgVo();
		String transIDH = msgVo.getTxnSeq();// 落地方交易流水号
		String transIDHTime = paramData.getTxnTime();// 落地方处理时间
		String intTxnDate = msgVo.getTxnDate();//从数据库获取
		Long seqId = msgVo.getSeqId(); // 交易流水
		msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
		msgVoRtn.setRcvTransID(transIDH);
		msgVoRtn.setRcvDateTime(transIDHTime);
		msgVoRtn.setRcvDate(intTxnDate);
		
		UpayCsysTransCode transCode = msgVo.getTransCode();// 获取交易代码
//		String mainProvince = upayCsysImsiLdCdService
//				.findProvinceByMobileNumber(bodyMsgVo.getIDValue());
//		String mainProvince = ProvAreaCache.getProvAreaByPrimary(bodyMsgVo.getIDValue());
//		String mainProvince = findProvinceByMobileNumber(bodyMsgVo.getIDValue());
		ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(bodyMsgVo.getIDValue());
		String mainProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();

		txnLog.setIdProvince(mainProvince);
		if (mainProvince == null) {
			logger.warn("手机号码不正确:{},UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
					new Object[] {bodyMsgVo.getIDValue(), transIDH, paramData.getReqSys(),paramData.getReqTransID()});
			log.warn("手机号码不正确:{},UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
					new Object[] {bodyMsgVo.getIDValue(), transIDH, paramData.getReqSys(),paramData.getReqTransID()});
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
			msgVoRtn.setBody(bankQueryInfo);
			return msgVoRtn;
		}
		try {
			logger.debug("intTxnSeq:{},检查订单状态",new Object[]{transIDH});
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqDomain", msgVo.getReqSys());
			param.put("reqTransId", msgVo.getReqTransID());
			UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService
					.findObj(param);
			if (upayCsysTxnLog != null) {
				log.warn("订单重复,UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
						new Object[] {transIDH, paramData.getReqSys(),paramData.getReqTransID()});
				logger.warn("订单重复,UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
						new Object[] {transIDH, paramData.getReqSys(),paramData.getReqTransID()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				msgVoRtn.setBody(bankQueryInfo);
				return msgVoRtn;
			}
			// 记录交易流水
			logger.debug("intTxnSeq:{},记录交易流水--start",new Object []{transIDH});
			txnLog.setSeqId(seqId);
			txnLog.setIntTxnDate(intTxnDate);// 内部交易日期
			txnLog.setIntTxnSeq(transIDH);// 内部交易流水号
			txnLog.setIntTransCode(transCode.getTransCode());
			txnLog.setIntTxnTime(transIDHTime);
			txnLog.setPayMode(transCode.getPayMode());
			txnLog.setBussType(transCode.getBussType());
			txnLog.setBussChl(transCode.getBussChl());
//			txnLog.setTxnCat(transCode.getTxnCat());
			txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
			txnLog.setReqActivityCode(msgVo.getActivityCode());
			txnLog.setReqTransId(msgVo.getReqTransID());
			txnLog.setReqTransDt(msgVo.getReqDate());
			txnLog.setBankId(msgVo.getReqSys());
			txnLog.setReqTransTm(msgVo.getReqDateTime());
			txnLog.setReqTranshDt(intTxnDate);
			txnLog.setReqTranshId(transIDH);
			txnLog.setReqTranshTm(transIDHTime);
			txnLog.setReqOprDt(msgVo.getReqDate());
			txnLog.setReqOprId(msgVo.getReqTransID());
			txnLog.setReqOprTm(msgVo.getReqDateTime());
			txnLog.setReqDomain(msgVo.getReqSys());
			txnLog.setReqCnlType(msgVo.getReqChannel());// 发起方渠道标识
			txnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
			txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setIdType(bodyMsgVo.getIDType());
			txnLog.setIdValue(bodyMsgVo.getIDValue());
//			txnLog.setOriOrgId(null);
//			txnLog.setOriOprTransId(null);// 原交易流水号
//			txnLog.setOriReqDate(null);//
			txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setSettleDate(msgVo.getReqDate());
			upayCsysTxnLogService.add(txnLog);
			// 报文体格式校验
			logger.debug("内部交易流水号:{},报文体内容校验--start",new Object[]{transIDH});
			String validateMsg = this.validateModel(bodyMsgVo);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.debug("UPAY流水号:{},body体校验成功",new Object[]{transIDH});
			} else {
				logger.warn("UPAY流水号:{},报文体校验失败:{},业务发起方:{},银行交易流水号:{}",new Object[]{transIDH,validateMsg,paramData.getReqSys(),paramData.getReqTransID()});
				log.warn("UPAY流水号:{},业务发起方:{},银行交易流水号:{}",new Object[]{transIDH,paramData.getReqSys(),paramData.getReqTransID()});
				String code = validateMsg.split(":")[0];
				msgVoRtn.setRspCode(code);
				msgVoRtn.setRspDesc(validateMsg);
				msgVoRtn.setBody(bankQueryInfo);
				txnLog.setChlRspCode(code);
				txnLog.setChlRspDesc(validateMsg);
				txnLog.setChlSubRspCode(code);
				txnLog.setChlSubRspDesc(validateMsg);
//				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			String subOrgId = SysMapCache.getProvCd(mainProvince).getSysCd();
			
			String orgTransFlag = offOrgTrans(msgVo.getReqSys(), subOrgId, paramData.getTransCode().getTransCode(),
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			boolean orgTransFlag = isO2OTransOn(msgVo.getReqSys(), subOrgId, paramData.getTransCode().getTransCode());
//			boolean orgTransFlag = orgStatusCheck(subOrgId);
			if (orgTransFlag != null) {
				logger.warn("接收方机构关闭:{},UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
						new Object[] {subOrgId, transIDH, paramData.getReqSys(),paramData.getReqTransID()});
				log.info("接收方机构关闭:{},UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
						new Object[] {subOrgId, transIDH, paramData.getReqSys(),paramData.getReqTransID()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				msgVoRtn.setBody(bankQueryInfo);
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc()+orgTransFlag);
//				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
//						.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			// 发往CRM请求
			UpayCsysRouteInfo routeInfo = new UpayCsysRouteInfo();
			subReq.setRouteInfo(routeInfo);
			subReq.setTransCode(transCode); // 交易代码
			subReq.setVersion(ExcConstant.CRM_VERSION);
			subReq.setTestFlag(testFlag);
			subReq.setBIPCode(CommonConstant.Bip.Bis15.getValue());
			subReq.setActivityCode(CommonConstant.CrmTrans.Crm16.getValue());
			subReq.setActionCode(CommonConstant.ActionCode.Requset.getValue());
			subReq.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			subReq.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			subReq.setRouteType(CommonConstant.RouteType.RoutePhone.toString());
			subReq.setSessionID(transIDH);
			subReq.setRouteValue(bodyMsgVo.getIDValue());
			subReq.setTransIDO(transIDH);
			subReq.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
			subReq.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
			subReq.setMsgReceiver(subOrgId);
			subReq.setPriority(null);
			subReq.setServiceLevel(null);
			CrmUseInfoQueryReq useInfo = new CrmUseInfoQueryReq();
			useInfo.setIdType(bodyMsgVo.getIDType());
			useInfo.setIdValue(bodyMsgVo.getIDValue());
			subReq.setBody(useInfo);
			txnLog.setRcvActivityCode(subReq.getActivityCode());
			txnLog.setRcvBipCode(subReq.getBIPCode());
			txnLog.setRcvDomain(subOrgId);
			txnLog.setRcvRouteType(subReq.getRouteType());
			txnLog.setRcvRouteVal(subReq.getRouteValue());
			txnLog.setRcvTransDt(intTxnDate);
			txnLog.setRcvTransId(transIDH);
			txnLog.setRcvSessionId(transIDH);
			txnLog.setRcvTransTm(transIDHTime);
			// 发往CRM的查询信息
			logger.debug("内部交易流水号:{},发往CRM请求--start",
					new Object[]{transIDH});
			txnLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			Map<String, Object> params = new HashMap<String, Object>();
			UpayCsysBindInfo subBindInfoa = new UpayCsysBindInfo();
			CrmMsgVo msgVoRsn = crmQueryMobileInfoBus.execute(subReq, params,txnLog, subBindInfoa);
			logger.debug("内部交易流水号:{},发往CRM请求--end",
					new Object[]{transIDH});
			txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
			txnLog.setIdProvince(mainProvince);
			txnLog.setRcvSessionId(msgVoRsn.getSessionID());
			txnLog.setRcvTransDt(intTxnDate);
			txnLog.setRcvTransId(transIDH);
			txnLog.setRcvTransTm(transIDHTime);
			txnLog.setRcvTranshId(msgVoRsn.getTransIDH());
			txnLog.setRcvTranshTm(msgVoRsn.getTransIDHTime());
			txnLog.setRcvTranshDt(StrUtil.subString(
					msgVoRsn.getTransIDHTime(), 0, 8));
			txnLog.setRcvOprDt(StrUtil.subString(
					msgVoRsn.getTransIDHTime(), 0, 8));
			
			//平台发给省的报文如果有TransactionID字段，那么RcvOprId值填TransactionID，
			//如果报文中没有TransactionID，那么RcvOprId不用填 UR单：UPAY_DT-243

			//txnLog.setRcvOprId(UUIDGenerator.generateUUID());
			
			txnLog.setRcvOprTm(msgVoRsn.getTransIDHTime());
			txnLog.setRcvRspCode(msgVoRsn.getRspCode());
			txnLog.setRcvRspDesc(msgVoRsn.getRspDesc());
			txnLog.setRcvRspType(msgVoRsn.getRspType());
			txnLog.setBankId(msgVo.getReqSys());
			if (msgVoRsn.getBody() == null||msgVoRsn.getBody().equals("")) {
				log.warn("返回报文体为空,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{}" , 
						new Object[] {transIDH,msgVoRsn.getTransIDH(),msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver()});
				logger.warn("返回报文体为空,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},网状网超时" , 
						new Object[] {transIDH,msgVoRsn.getTransIDH(),msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver()});
				String errCode = msgVoRsn.getRspCode();
				errCode = BankErrorCodeCache.getBankErrCode(errCode);
				msgVoRtn.setRspCode(errCode);
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				msgVoRtn.setBody(bankQueryInfo);
				// 更新交易流水
				txnLog.setChlRspCode(errCode);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));// 发起方应答描述
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				logger.info("请求成功更新交易流水，返回应答--start");
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			txnLog.setRcvEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
			// 解析CRM返回的应答报文体
			MsgHandle.unmarshaller(crmUserInfoRes, (String) msgVoRsn.getBody());
			if(RspCodeConstant.Upay.UPAY_U99998.getValue().equals(msgVoRsn.getRspCode())){			
				log.warn("网状网应答超时,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{}" , 
						new Object[] {transIDH,msgVoRsn.getTransIDH(),msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver()});
				logger.warn("网状网应答超时,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{}" , 
						new Object[] {transIDH,msgVoRsn.getTransIDH(),msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver()});			
				String errCode = msgVoRsn.getRspCode();
				errCode = BankErrorCodeCache.getBankErrCode(errCode);
				msgVoRtn.setRspCode(errCode);
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone
						.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvDateTime(transIDHTime );
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setBody(bankQueryInfo);
				// 更新交易流水
				txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
				txnLog.setIdProvince(mainProvince);
				txnLog.setRcvSessionId(msgVoRsn.getSessionID());
				txnLog.setRcvTranshId(msgVoRsn.getTransIDH());
				txnLog.setRcvTranshTm(msgVoRsn.getTransIDHTime());
				txnLog.setRcvTranshDt(StrUtil.subString(
						msgVoRsn.getTransIDHTime(), 0, 8));
				txnLog.setRcvRspCode(msgVoRsn.getRspCode());
				txnLog.setRcvRspDesc(msgVoRsn.getRspDesc());
				txnLog.setUserStatus(crmUserInfoRes.getUserStatus());
				txnLog.setUserCat(crmUserInfoRes.getUserCat());
				txnLog.setUserName(StringFormat.formatNameString(crmUserInfoRes.getUserName()));
				txnLog.setBalance(crmUserInfoRes.getBalance());
				txnLog.setBankId(msgVo.getReqSys());
				txnLog.setNeedPayAmt(crmUserInfoRes.getPayAmount());
				txnLog.setChlRspCode(errCode);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));// 发起方应答描述
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				logger.info("请求成功更新交易流水，返回应答--start");
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			txnLog.setUserStatus(crmUserInfoRes.getUserStatus());
			txnLog.setUserCat(crmUserInfoRes.getUserCat());
			txnLog.setSignStatus(crmUserInfoRes.getSignStatus());
			txnLog.setUserName(StringFormat.formatNameString(crmUserInfoRes.getUserName()));
			txnLog.setBalance(crmUserInfoRes.getBalance());
			txnLog.setNeedPayAmt(crmUserInfoRes.getPayAmount());
			if ((msgVoRsn.getRspCode()).equals(RspCodeConstant.Wzw.WZW_0000.getValue())) {
				if (crmUserInfoRes.getUserName() != null) {
					String um = crmUserInfoRes.getUserName();
					//String useName = um.replace(um.substring(0, 1), "*");
					bankQueryInfo.setUserName(StringFormat.formatNameString(um));
				}
				if ((CommonConstant.UserStatus.Normal.toString())
						.equals(crmUserInfoRes.getUserStatus())) {
					log.succ("CRM应答为成功,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH(),
									msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
					logger.info("CRM应答为成功,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH(),
							msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_010A00
							.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_010A00
							.getDesc());
					bankQueryInfo.setHomeProv(mainProvince);
					bankQueryInfo.setBalance(crmUserInfoRes.getBalance());
					bankQueryInfo.setPayAmount(crmUserInfoRes.getPayAmount());
					bankQueryInfo.setUserCat(crmUserInfoRes.getUserCat());
					msgVoRtn.setBody(bankQueryInfo);
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00
							.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00
							.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00
							.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00
							.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.toString());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					return msgVoRtn;

				}else if ((CommonConstant.UserStatus.OneWayDown.toString())
						.equals(crmUserInfoRes.getUserStatus()) || (CommonConstant.UserStatus.Down.toString())
						.equals(crmUserInfoRes.getUserStatus()) ) {
					log.succ("CRM应答为成功,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH(),
									msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
					logger.info("CRM应答为成功,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH(),
							msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_010A00
							.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_010A00
							.getDesc());
					bankQueryInfo.setHomeProv(mainProvince);
					bankQueryInfo.setBalance(crmUserInfoRes.getBalance());
					bankQueryInfo.setPayAmount(crmUserInfoRes.getPayAmount());
					bankQueryInfo.setUserCat(crmUserInfoRes.getUserCat());
					msgVoRtn.setBody(bankQueryInfo);
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00
							.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00
							.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00
							.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00
							.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.toString());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					return msgVoRtn;

				} 
				else if ((CommonConstant.UserStatus.PhoneNot.toString())
						.equals(crmUserInfoRes.getUserStatus())) {
					log.succ("CRM应答,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH(),
							msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
					logger.info("CRM应答,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH(),
							msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A11
							.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A11
							.getDesc());
					bankQueryInfo.setBalance(crmUserInfoRes.getBalance());
					bankQueryInfo.setHomeProv(crmUserInfoRes.getHomeProv());
					bankQueryInfo.setPayAmount(crmUserInfoRes.getPayAmount());
					bankQueryInfo.setUserCat(crmUserInfoRes.getUserCat());
					msgVoRtn.setBody(bankQueryInfo);
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A11
							.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A11
							.getDesc());// 发起方应答描述
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A11
							.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A11
							.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.toString());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					return msgVoRtn;

				} else {
					log.succ("CRM应答,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH(),
							msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
					logger.info("CRM应答,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
							new Object[] { transIDH, msgVoRsn.getTransIDH(),
							msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A08.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A08.getDesc());
					bankQueryInfo.setBalance(crmUserInfoRes.getBalance());
					bankQueryInfo.setHomeProv(crmUserInfoRes.getHomeProv());
					bankQueryInfo.setPayAmount(crmUserInfoRes.getPayAmount());
					bankQueryInfo.setUserCat(crmUserInfoRes.getUserCat());
					msgVoRtn.setBody(bankQueryInfo);
					// 更新交易流水
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A08
							.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A08
							.getDesc());// 发起方应答描述
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A08
							.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A08
							.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.toString());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					return msgVoRtn;
				}
				
			} else {
				log.warn("CRM应答为失败,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
						new Object[] { transIDH, msgVoRsn.getTransIDH(),
						msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
				logger.warn("CRM应答为失败,UPAY流水号:{},CRM应答流水:{},发往CRM流水:{},CRM机构号：{},应答码:{},用户状态:{}",
						new Object[] { transIDH, msgVoRsn.getTransIDH(),
						msgVoRsn.getTransIDO(),msgVoRsn.getMsgReceiver(),msgVoRsn.getRspCode(),crmUserInfoRes.getUserStatus()});
				String errCode = msgVoRsn.getRspCode();
				errCode = BankErrorCodeCache.getBankErrCode(errCode);
				msgVoRtn.setRspCode(errCode);
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				msgVoRtn.setBody(bankQueryInfo);
				txnLog.setChlRspCode(errCode);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));// 发起方应答描述
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("BankQueryMobileInfoAction execute(Object) - end");
				return msgVoRtn;
			} 
//			else {
//				log.error("CRM应答为失败,内部交易流水号:{},CRM应答流水:{},应答码:{},应答描述:{}" , 
//						new Object[] {transIDH,msgVoRsn.getTransIDH(),msgVoRsn.getRspCode(),msgVoRsn.getRspDesc()});
//				logger.info("CRM应答为失败,内部交易流水号:{},CRM应答流水:{},应答码:{},应答描述:{}" , 
//						new Object[] {transIDH,msgVoRsn.getTransIDH(),msgVoRsn.getRspCode(),msgVoRsn.getRspDesc()});
//				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A14.getValue());
//				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A14.getDesc());
//				msgVoRtn.setBody(bankQueryInfo);
//				txnLog.setIdProvince(mainProvince);
//				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A14.getValue());
//				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A14.getDesc());// 发起方应答描述
//				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A14.getValue());
//				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A14.getDesc());
//				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
////				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
//				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//				upayCsysTxnLogService.modify(txnLog);
//				logger.debug("BankQueryMobileInfoAction execute(Object) - end");
//				return msgVoRtn;
//			}
		} catch (AppRTException e) {
			log.error("内部异常,UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
					new Object[] {transIDH, paramData.getReqSys(),paramData.getReqTransID()});
			logger.error("内部异常,代码:{},UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
					new Object[] {e.getCode(),transIDH, paramData.getReqSys(),paramData.getReqTransID()});
			logger.error("内部异常,UPAY流水号:"+transIDH,e);
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setBody(bankQueryInfo);
			txnLog.setIdProvince(mainProvince);
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
//			txnLog.setChlRspType(CommonConstant.CrmRspType.SysErr.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		} catch (AppBizException e) {
			log.error("业务异常,UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
					new Object[] {transIDH, paramData.getReqSys(),paramData.getReqTransID()});
			logger.error("业务异常,代码:{},UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
					new Object[] {e.getCode(),transIDH, paramData.getReqSys(),paramData.getReqTransID()});
			logger.error("业务异常,UPAY流水号:"+transIDH,e);
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setBody(bankQueryInfo);
			txnLog.setIdProvince(mainProvince);
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
//			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		} catch (Exception e) {
			log.error("未知异常,UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
					new Object[] {transIDH, paramData.getReqSys(),paramData.getReqTransID()});
			logger.error("未知异常,UPAY流水号:{},业务发起方:{},银行交易流水号:{}" , 
					new Object[] {transIDH, paramData.getReqSys(),paramData.getReqTransID()});		
			logger.error("未知异常,UPAY流水号:"+transIDH,e);
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
			
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setBody(bankQueryInfo);
			txnLog.setIdProvince(mainProvince);
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			if (CommonConstant.TxnStatus.InitStatus.toString().equals(
					txnLog.getStatus())
					|| "".equals(StringUtil.toTrim(txnLog.getStatus()))) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			}
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
//			txnLog.setChlRspType(CommonConstant.CrmRspType.SysErr.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		}
	}

}
