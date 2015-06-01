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
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmBindReulstBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysSysMapInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankBindReulstReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmBindResultReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultSignRes;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;

/**
 * 
 * @author fan_kui 银行签约结果通知
 */
@Controller("bankBindResultsAction")
@Scope("prototype")
public class BankBindResultsAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {
	@Autowired
	private CrmBindReulstBus crmBindReulstBus;

	@Autowired
	private IUpayCsysTxnLogTmpService upayCsysTxnLogTmpService;

	@Override
	public BankMsgVo execute(BankMsgVo paramData) throws AppBizException {
		logger.debug("BankBindResultsAction execute(Object) - start");
		BankMsgVo bankMsgVo = new BankMsgVo();
		bankMsgVo = paramData;
		BankMsgVo bankRtnMsgVo = paramData;
		BankBindReulstReqVo reqVo = new BankBindReulstReqVo();
		UpayCsysTxnLog txnLog = null;
		String intTxnDate = null;
		String intTxnTime = null;
		Long seqId = null;
		String txnSeq = null;
		String idvalue = null;
		String crmCode="";
		try {
			logger.info("开始解析报文体");
			MsgHandle.unmarshaller(reqVo, (String) bankMsgVo.getBody());
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("reqDomain", bankMsgVo.getReqSys());
			params1.put("reqTransId", bankMsgVo.getReqTransID());
			txnLog = upayCsysTxnLogService.findObj(params1);
			
		
			if (txnLog != null) {
				txnSeq = txnLog.getIntTxnSeq();
				intTxnDate = txnLog.getIntTxnDate();
				intTxnTime = txnLog.getIntTxnTime();
				seqId = txnLog.getSeqId();
			
				
				txnLog.setBankId(bankMsgVo.getReqSys());
				txnLog.setIntTxnTime(intTxnTime);
				txnLog.setIntMqSeq(bankMsgVo.getMqSeq());
				txnLog.setIntTransCode((bankMsgVo.getTransCode()).getTransCode());
				txnLog.setIntTxnDate(intTxnDate);
				txnLog.setIntTxnSeq(txnSeq);
				txnLog.setPayMode(bankMsgVo.getTransCode().getPayMode());
				txnLog.setBussType(bankMsgVo.getTransCode().getBussType());
				txnLog.setBussChl(bankMsgVo.getTransCode().getBussChl());
//				txnLog.setTxnCat(bankMsgVo.getTransCode().getTxnCat());
				txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
				txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setReqOprId(bankMsgVo.getReqTransID());
				txnLog.setReqOprDt(bankMsgVo.getReqDate());
				txnLog.setReqOprTm(bankMsgVo.getReqDateTime());
				txnLog.setReqActivityCode(bankMsgVo.getActivityCode());
				txnLog.setReqDomain(bankMsgVo.getReqSys());
				txnLog.setReqCnlType(bankMsgVo.getReqChannel());
				txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReqTransDt(bankMsgVo.getReqDate());
				txnLog.setReqTransId(bankMsgVo.getReqTransID());
				txnLog.setReqTransTm(bankMsgVo.getReqDateTime());
				txnLog.setReqTranshDt(intTxnDate);
				txnLog.setReqTranshId(txnSeq);
				txnLog.setReqTranshTm(intTxnTime);
				txnLog.setReqSessionId(reqVo.getSessionID());
				txnLog.setBankAccId(StringFormat.formatCodeString(reqVo.getBankAcctID()));
				txnLog.setBankAcctType(reqVo.getBankAcctType());
				txnLog.setRechAmount(reqVo.getRechAmount()==null?null:StringFormat.paseLong(reqVo.getRechAmount()));
				txnLog.setRechThreshold(reqVo.getRechThreshold()==null?null:StringFormat.paseLong(reqVo.getRechThreshold()));
				txnLog.setPayType(reqVo.getPayType());
				txnLog.setSubId(reqVo.getSubID());
				txnLog.setSubTime(reqVo.getSubTime());
				txnLog.setSettleDate(bankMsgVo.getReqDate());
				txnLog.setMainFlag(CommonConstant.Mainflag.Master.toString());
				txnLog.setSettleDate(bankMsgVo.getReqDate());
				
			} else {
				txnLog = new UpayCsysTxnLog();
				txnSeq = bankMsgVo.getTxnSeq();
				intTxnDate = bankMsgVo.getTxnDate();
				intTxnTime = bankMsgVo.getTxnTime();
				seqId = bankMsgVo.getSeqId();

				
				logger.debug("intTxnSeq:() start add txnLog",txnSeq);

				if (StringUtils.isBlank(reqVo.getPayType())) {
					reqVo.setPayType(CommonConstant.PayWay.ManualConsume.toString());
				}

				// 生成预处理订单
				txnLog.setBankId(bankMsgVo.getReqSys());
				txnLog.setSeqId(seqId);
				txnLog.setIntTxnTime(intTxnTime);
				txnLog.setIntMqSeq(bankMsgVo.getMqSeq());
				txnLog.setIntTransCode((bankMsgVo.getTransCode()).getTransCode());
				txnLog.setIntTxnDate(intTxnDate);
				txnLog.setIntTxnSeq(txnSeq);
				txnLog.setPayMode(bankMsgVo.getTransCode().getPayMode());
				txnLog.setBussType(bankMsgVo.getTransCode().getBussType());
				txnLog.setBussChl(bankMsgVo.getTransCode().getBussChl());
//				txnLog.setTxnCat(bankMsgVo.getTransCode().getTxnCat());
				txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
				txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setReqOprId(bankMsgVo.getReqTransID());
				txnLog.setReqOprDt(bankMsgVo.getReqDate());
				txnLog.setReqOprTm(bankMsgVo.getReqDateTime());
				txnLog.setReqActivityCode(bankMsgVo.getActivityCode());
				txnLog.setReqDomain(bankMsgVo.getReqSys());
				txnLog.setReqCnlType(bankMsgVo.getReqChannel());
				txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
				txnLog.setReqTransDt(bankMsgVo.getReqDate());
				txnLog.setReqTransId(bankMsgVo.getReqTransID());
				txnLog.setReqTransTm(bankMsgVo.getReqDateTime());
				txnLog.setReqTranshDt(intTxnDate);
				txnLog.setReqTranshId(txnSeq);
				txnLog.setReqTranshTm(intTxnTime);
				txnLog.setReqSessionId(reqVo.getSessionID());
				txnLog.setBankAccId(StringFormat.formatCodeString(reqVo.getBankAcctID()));
				txnLog.setBankAcctType(reqVo.getBankAcctType());
				txnLog.setRechAmount(reqVo.getRechAmount()==null?null:StringFormat.paseLong(reqVo.getRechAmount()));
				txnLog.setRechThreshold(reqVo.getRechThreshold()==null?null:StringFormat.paseLong(reqVo.getRechThreshold()));
				txnLog.setPayType(reqVo.getPayType());
				txnLog.setSubId(reqVo.getSubID());
				txnLog.setSubTime(reqVo.getSubTime());
				txnLog.setSettleDate(bankMsgVo.getReqDate());
				txnLog.setMainFlag(CommonConstant.Mainflag.Master.toString());
				txnLog.setSettleDate(bankMsgVo.getReqDate());
				upayCsysTxnLogService.add(txnLog);
				logger.debug("intTxnSeq:{}add txnLog is success!!!",txnSeq);
			}
			
			bankRtnMsgVo.setRcvDateTime(intTxnTime);
			bankRtnMsgVo.setRcvDate(intTxnDate);
			bankRtnMsgVo.setRcvTransID(txnSeq);
			bankRtnMsgVo.setActionCode(CommonConstant.ActionCode.Respone.toString());
			
			txnLog.setSettleDate(bankMsgVo.getReqDate());//重复交易更新settleDate
			// 报文体格式校验
			String validateMsg = validateModel(reqVo);

			if (StringUtils.isNotBlank(validateMsg)) {
				String code = validateMsg.split(":")[0];
				log.warn("格式校验失败!银行流水:{},平台流水:{},银行机构:{},错误码:{},错误描述:{}",
						new Object[] { paramData.getReqTransID(),txnSeq, paramData.getReqSys(),
								code, RspCodeConstant.Bank.getDescByValue(code) });
				logger.warn("intTxnSeq:{} check is falied:{}",txnSeq, validateMsg);
				bankRtnMsgVo.setRspCode(code);
				bankRtnMsgVo.setRspDesc(validateMsg);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(code);
				txnLog.setChlRspDesc(validateMsg);
				txnLog.setChlSubRspCode(code);
				txnLog.setChlSubRspDesc(validateMsg);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);
				bankRtnMsgVo.setBody(null);
				return bankRtnMsgVo;
			}

			logger.info("intTxnSeq:{},报文体检验成功", new Object[]{txnSeq});
			// 查询签约关系
			Map<String, Object> paramBind = new HashMap<String, Object>();
			paramBind.put("reserved2", reqVo.getSessionID());
			paramBind.put("status",CommonConstant.TxnStatus.TxnSuccess.toString());
//			UpayCsysTxnLog preTxnLog = upayCsysTxnLogService.findObj(paramBind);
			List<UpayCsysTxnLog> preTxnLogs = upayCsysTxnLogService.findList(paramBind);
			UpayCsysTxnLog preTxnLog = null!=preTxnLogs&&preTxnLogs.size()>0?preTxnLogs.get(0):null;			
			UpayCsysTxnLogTmp preLogTmp=null;
			// 签约记录可能来自于网关
			if(preTxnLog==null){
				 preLogTmp = upayCsysTxnLogTmpService.findObj(paramBind);
			}
			if (preTxnLog == null && preLogTmp == null) {
				log.warn("无预签约记录!银行流水:{},平台流水:{},银行机构:{},预签约会话标识:{}",
						new Object[] { paramData.getReqTransID(),
						txnSeq, paramData.getReqSys(),reqVo.getSessionID() });
				logger.warn("intTxnSeq:{} 无此预签约记录，签约失败",txnSeq);
				bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
				bankRtnMsgVo.setBody(null);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
				upayCsysTxnLogService.modify(txnLog);
				crmCode=RspCodeConstant.Crm.CRM_4A05.getValue();
				return bankRtnMsgVo;
			}
		
			Map<String, Object> paramBinda = new HashMap<String, Object>();
			String idType = null==preLogTmp?preTxnLog.getIdType():preLogTmp.getIdType();
			String idValue = null==preLogTmp?preTxnLog.getIdValue():preLogTmp.getIdValue();
//			
			txnLog.setIdValue(idValue);
			txnLog.setIdType(idType);
			txnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
//			paramBinda.put("mainFlag", CommonConstant.Mainflag.Master.getValue());
//			paramBinda.put("subId", reqVo.getSubID());
//			paramBinda.put("status",CommonConstant.TxnStatus.TxnSuccess.toString());
			paramBinda.put("idType", idType);
			paramBinda.put("idValue", idValue);
			UpayCsysBindInfo mainBind = upayCsysBindInfoService.findObj(paramBinda);
			boolean bindFlag=false;
			if(null!=mainBind){
				//通知类的交易我们不要做拦截，直接发到省份
//				if(CommonConstant.BindStatus.Bind.getValue().equals(mainBind.getStatus())
//						||reqVo.getSubID().equals(mainBind.getSubId())){
//					log.warn("该用户已签约!银行流水:{},平台流水:{},银行机构:{}",
//							new Object[] { paramData.getReqTransID(),txnSeq, paramData.getReqSys()});
//					logger.info("intTxnSeq:{} 该用户已签约，签约失败",txnSeq);
//					bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_012A01.getValue());
//					bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_012A01.getDesc());
//					bankRtnMsgVo.setBody(null);
//					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A01.getValue());
//					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A01.getDesc());
//					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A01.getValue());
//					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A01.getDesc());
//					upayCsysTxnLogService.modify(txnLog);
//					crmCode=RspCodeConstant.Crm.CRM_2A01.getValue();
//					crmDesc=RspCodeConstant.Crm.CRM_2A01.getDesc();
//					return bankRtnMsgVo;
//				}
				bindFlag=true;
			}
			UpayCsysBindInfo bindInfo = new UpayCsysBindInfo();
			bindInfo.setStatus(CommonConstant.BindStatus.Bind.toString());
			bindInfo.setSubId(reqVo.getSubID());
			bindInfo.setSignSubTime(reqVo.getSubTime());
			bindInfo.setSignTxnSeq(bankMsgVo.getReqTransID());
			bindInfo.setSignTxnTime(bankMsgVo.getReqDateTime());
			bindInfo.setSignTxnDate(DateUtil.getDateyyyyMMddHHmmss().substring(0, 8));
			bindInfo.setBankAccId(StringFormat.formatCodeString(reqVo.getBankAcctID()));
			bindInfo.setBankAcctType(reqVo.getBankAcctType());
			bindInfo.setBankId(bankMsgVo.getReqSys());
			bindInfo.setPayType(reqVo.getPayType());
			bindInfo.setRechAmount(StringFormat.paseLong(reqVo.getRechAmount()));
			bindInfo.setRechThreshold(StringFormat.paseLong(reqVo.getRechThreshold()));
			bindInfo.setSignCnlType(bankMsgVo.getReqChannel());
			bindInfo.setSettleDate(bankMsgVo.getReqDate());
			bindInfo.setMainFlag(CommonConstant.Mainflag.Master.toString());
			bindInfo.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
			bindInfo.setReserved2(reqVo.getSessionID());
			bindInfo.setSignOrgId(bankMsgVo.getReqSys());
			if (preTxnLog != null) {
				bindInfo.setUserCat(preTxnLog.getUserCat());
				idvalue = preTxnLog.getIdValue();
			}
			if (StringUtils.isBlank(idvalue) && preLogTmp != null) {
				idvalue = preLogTmp.getIdValue();
			}
			
//			String idProvince = upayCsysImsiLdCdService.findProvinceByMobileNumber(idvalue);
//			String idProvince = ProvAreaCache.getProvAreaByPrimary(idvalue);
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(idvalue);
			String idProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
			txnLog.setIdProvince(idProvince);
			bindInfo.setIdProvince(StringUtils.isBlank(idProvince) ? "": idProvince);
			if(bindFlag){
				bindInfo.setIdValue(mainBind.getIdValue());
				bindInfo.setIdType(mainBind.getIdType());
				bindInfo.setSeqId(mainBind.getSeqId());
//				upayCsysBindInfoService.modify(bindInfo);
				upayCsysBindInfoService.modifyClean(bindInfo);
			}else{
				bindInfo.setIdValue(idvalue);
				bindInfo.setIdType(CommonConstant.UserSignType.Phone.getValue());
				bindInfo.setSeqId(paramData.getSeqId());
				upayCsysBindInfoService.add(bindInfo);
			}
			logger.info("intTxnSeq:{} 修改签约关系表成功",txnSeq);
			UpayCsysSysMapInfo upayCsysSysMapInfo = SysMapCache
					.getProvCd(bindInfo.getIdProvince());
			if (upayCsysSysMapInfo == null) {
				log.warn("查无手机号段信息!银行流水:{},平台流水:{},银行机构:{},省代码:{}",
						new Object[] { paramData.getReqTransID(),
						txnSeq, paramData.getReqSys(),bindInfo.getIdProvince() });
				logger.warn("intTxnSeq:{} 未找到对应省Crm机构编码,检查手机号码是否正确",txnSeq);

				bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_015A03.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A03.getDesc() + ":未找到对应省Crm机构编码,检查手机号码是否正确");
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A03.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A03.getDesc()+ ":未找到对应省Crm机构编码,检查手机号码是否正确");
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A03.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A03.getDesc() + ":未找到对应省Crm机构编码,检查手机号码是否正确");
				upayCsysTxnLogService.modify(txnLog);
				bankRtnMsgVo.setBody(null);
				crmCode=RspCodeConstant.Crm.CRM_5A03.getValue();
				return bankRtnMsgVo;
			}

			String orgFlag = offOrgTrans(bankMsgVo.getReqSys(), upayCsysSysMapInfo.getSysCd(), paramData.getTransCode().getTransCode(), 
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			boolean orgFlag = isO2OTransOn(bankMsgVo.getReqSys(), upayCsysSysMapInfo.getSysCd(), paramData.getTransCode().getTransCode());
//			boolean orgFlag = orgStatusCheck(upayCsysSysMapInfo.getSysCd());
			if (orgFlag != null) {
				log.info("CRM无权限!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},交易代码:{}",
						new Object[] { paramData.getReqTransID(),txnSeq, paramData.getReqSys(),
								upayCsysSysMapInfo.getSysCd(),paramData.getTransCode().getTransCode() });
				logger.warn("intTxnSeq:{}落地方机构:{}服务的权限关闭",txnSeq,upayCsysSysMapInfo.getSysCd());
				bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				bankRtnMsgVo.setBody(null);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc()+orgFlag);
				upayCsysTxnLogService.modify(txnLog);
				crmCode=RspCodeConstant.Crm.CRM_2A05.getValue();
				return bankRtnMsgVo;
			}
			if (reqVo.getPayType() == null) {
				reqVo.setPayType(CommonConstant.PayWay.ManualConsume.toString());
			}
			// 组装发送移动的签约结果通知报文
			CrmBindResultReqVo crmBindResultReqVo = new CrmBindResultReqVo();
			crmBindResultReqVo.setActionDate(intTxnDate);
			crmBindResultReqVo.setBankAcctType(reqVo.getBankAcctType());
			crmBindResultReqVo.setBankAcctID(reqVo.getBankAcctID());
			crmBindResultReqVo.setIDType(bindInfo.getIdType());
			crmBindResultReqVo.setIDValue(bindInfo.getIdValue());
			crmBindResultReqVo.setPayType(reqVo.getPayType());
			crmBindResultReqVo.setRechAmount(StringFormat.paseLong(reqVo.getRechAmount()));
			crmBindResultReqVo.setRechThreshold(StringFormat.paseLong(reqVo.getRechThreshold()));
			crmBindResultReqVo.setSessionID(reqVo.getSessionID());
			crmBindResultReqVo.setSubID(reqVo.getSubID());
			crmBindResultReqVo.setSubTime(reqVo.getSubTime());
			
//			crmBindResultReqVo.setTransactionID(Serial.genSerialNos(CommonConstant.Sequence.OprId.getValue()));
			//流水号设置成32位
			crmBindResultReqVo.setTransactionID(Serial.genSerialNum(CommonConstant.Sequence.OprId.getValue()));
			crmBindResultReqVo.setBankID(bankMsgVo.getReqSys());
			crmBindResultReqVo.setSettleDate(bankMsgVo.getReqDate());
			CrmMsgVo crmMsgVo = new CrmMsgVo();
			crmMsgVo.setBody(crmBindResultReqVo);
			crmMsgVo.setVersion(ExcConstant.CRM_VERSION);
			crmMsgVo.setTestFlag(testFlag);
			if(null!=preLogTmp){
				crmMsgVo.setBIPCode(CommonConstant.Bip.Bis02.toString());
			}
			if(null!=preTxnLog){
				crmMsgVo.setBIPCode(CommonConstant.Bip.Bis03.toString());
			}
//			crmMsgVo.setBIPCode(CommonConstant.Bip.Bis03.toString());
			crmMsgVo.setActivityCode(CommonConstant.CrmTrans.Crm05.toString());
			crmMsgVo.setActionCode(CommonConstant.ActionCode.Requset.toString());
			crmMsgVo.setOrigDomain(CommonConstant.OrgDomain.UPSS.toString());
			crmMsgVo.setRouteType(CommonConstant.RouteType.RoutePhone.toString());
			crmMsgVo.setHomeDomain(CommonConstant.OrgDomain.BOSS.toString());
			crmMsgVo.setRouteValue(bindInfo.getIdValue());
			crmMsgVo.setSessionID(txnSeq);
			crmMsgVo.setTransIDO(txnSeq);
			crmMsgVo.setMqSeq(Serial.genSerialNo(CommonConstant.Sequence.SendCrmMqSeq.getValue()));
			crmMsgVo.setTransIDOTime(StrUtil.subString(intTxnTime, 0, 14));
			crmMsgVo.setMsgReceiver(upayCsysSysMapInfo.getSysCd());
			Map<String, Object> params = new HashMap<String, Object>();
			
			txnLog.setIdProvince(bindInfo.getIdProvince());
			txnLog.setRcvDomain(upayCsysSysMapInfo.getSysCd());
			txnLog.setIdType(bindInfo.getIdType());
			txnLog.setIdValue(bindInfo.getIdValue());
			txnLog.setRcvSessionId(crmMsgVo.getSessionID());
			txnLog.setRcvRouteType(crmMsgVo.getRouteType());
			txnLog.setRcvRouteVal(crmMsgVo.getRouteValue());
			txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
			txnLog.setRcvTransDt(intTxnDate);
			txnLog.setRcvTransId(txnSeq);
			txnLog.setRcvTransTm(intTxnTime);
			txnLog.setRcvOprDt(crmBindResultReqVo.getActionDate());
			txnLog.setRcvOprId(crmBindResultReqVo.getTransactionID());
			txnLog.setRcvOprTm(intTxnTime);
			txnLog.setRcvBipCode(crmMsgVo.getBIPCode());
			txnLog.setRcvActivityCode(CommonConstant.CrmTrans.Crm05.getValue());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			// 发送消息到省CRM,由省CRM发送消息到网状网，在由网状网通知省份
			logger.info("intTxnSeq:{} 签约完成，发送消息到移动 -start--crmBindReulstBus.execute",txnSeq);
			CrmMsgVo crmMsgVoRtn = crmBindReulstBus.execute(crmMsgVo, params,txnLog, bindInfo);
			logger.info("intTxnSeq:{} 移动返回签约结果:{} - end--crmBindReulstBus.execute",txnSeq,crmMsgVoRtn.getRspCode());
			String errCode = crmMsgVoRtn.getRspCode();
            String crmErrCode=crmMsgVoRtn.getRspCode();
            
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			bankRtnMsgVo.setRspCode(errCode);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			bankRtnMsgVo.setBody(null);
			upayCsysTxnLogService.modify(txnLog);
			if(txnLog.getChlSubRspCode().equals(RspCodeConstant.Bank.BANK_010A00.getValue())){
				crmCode=RspCodeConstant.Crm.CRM_0000.getValue();
			}else{
				crmErrCode=CrmErrorCodeCache.getCrmErrCode(crmErrCode);
				crmCode=crmErrCode;
				if(StringUtils.isBlank(crmCode)){
					crmCode=RspCodeConstant.Crm.CRM_2998.getValue();
				}
			}
//			// 根据交易的sessionId 到临时交易流水表里面查询省份是否有记录。如果有记录就是网关过来的签约，需要通知网关。
//			// 否则是银行端过来的不需要通知网关
			if (preLogTmp != null) {
				logger.info("intTxnSeq:{} noticeMsg end  支付网关签约流程完成,签约协议号:{}",txnSeq,txnLog.getSubId());
//				try {
//					logger.info("intTxnSeq:{} start 通知支付网关签约开始,签约协议号:{}",txnSeq,txnLog.getSubId());
//					CoreResultSignRes gateSign = gateSignRes(txnLog, preLogTmp,crmCode,crmDesc);
//					gatePayResultNoticeBus.execute(gateSign);
//					logger.info("intTxnSeq:{} noticeMsg end   通知支付网关签约成功,签约协议号:{}",txnSeq,txnLog.getSubId());
//				} catch (Exception e) {
//					logger.error("intTxnSeq:{} 支付网关签约通知值失败,签约协议号:{}",txnSeq,txnLog.getSubId());
//					logger.error("失败原因",e);
//					log.error("intTxnSeq:{} 支付网关签约通知值失败,签约协议号:{}",txnSeq,txnLog.getSubId());
//				}
			}
			logger.debug("BankBindResultsAction execute(Object) - end");
			log.succ("内部流水:{},接收方流水:{},响应码:{} " ,new Object[]{txnSeq,crmMsgVoRtn.getTransIDH(),errCode});
			return bankRtnMsgVo;
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.error("运行异常!银行流水:{},平台流水:{},银行机构:{}",
					new Object[] { paramData.getReqTransID(),txnSeq, paramData.getReqSys()});
			logger.error("intTxnSeq:{},运行期内部异常",new Object[]{txnSeq});
			logger.error("运行异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			bankRtnMsgVo.setRspCode(errCode);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			upayCsysTxnLogService.modify(txnLog);
			bankRtnMsgVo.setBody(null);
			return bankRtnMsgVo;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.error("业务异常!银行流水:{},平台流水:{},银行机构:{}",
					new Object[] { paramData.getReqTransID(),txnSeq, paramData.getReqSys()});
			logger.error("intTxnSeq:{},业务异常",new Object[]{txnSeq});
			logger.error("业务异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			bankRtnMsgVo.setRspCode(errCode);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			upayCsysTxnLogService.modify(txnLog);
			bankRtnMsgVo.setBody(null);
			return bankRtnMsgVo;
		} catch (Exception e) {
			log.error("未知异常!银行流水:{},平台流水:{},银行机构:{}",
					new Object[] { paramData.getReqTransID(),txnSeq, paramData.getReqSys()});
			logger.error("intTxnSeq:{} ,未知异常", txnSeq);
			logger.error("未知异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
			
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			upayCsysTxnLogService.modify(txnLog);
			bankRtnMsgVo.setBody(null);
			return bankRtnMsgVo;
		}
	}

//	/**
//	 * 封装返回给省份的参数
//	 * @param txnLog
//	 * @param txnTmpLog
//	 * @param crmCode
//	 * @param crmDesc
//	 * @return
//	 */
//	private CoreResultSignRes gateSignRes(UpayCsysTxnLog txnLog,
//			UpayCsysTxnLogTmp txnTmpLog,String crmCode,String crmDesc) {
//		logger.debug("交易流水{},临时交易流水{}",new Object[]{txnLog,txnTmpLog});
//		CoreResultSignRes resMsg = new CoreResultSignRes();
//		if(txnLog!=null){
//			resMsg.setActionDate(txnLog.getIntTxnDate());
//			resMsg.setBankAcctID(StringFormat.formatCodeString(txnLog.getBankAccId()));
//			resMsg.setBankAcctType(txnLog.getBankAcctType());
//			resMsg.setSessionID(txnLog.getReqSessionId());
//			resMsg.setSubID(txnLog.getSubId());
//			resMsg.setSubTime(txnLog.getSubTime());
//			resMsg.setTransactionID(txnLog.getReqTransId());
//			resMsg.setUserCat(txnLog.getUserCat());
//			resMsg.setUserID(StringFormat.formatCodeString(txnLog.getUserId()));
//			resMsg.setUserIDType(txnLog.getUserType());
//			resMsg.setUserName(StringFormat.formatNameString(txnLog.getUserName()));
//		}
//		if(txnTmpLog!=null){
//			resMsg.setPayType(txnTmpLog.getPayType());
//			resMsg.setRechAmount(String.valueOf(txnTmpLog.getRechAmount()));
//			resMsg.setRechThreshold(String.valueOf(txnTmpLog.getRechThreshold()));
//			resMsg.setBankID(txnTmpLog.getBankId());
//			resMsg.setCLIENTIP(txnTmpLog.getClientIp());
//			resMsg.setMCODE(RspCodeConstant.Crm.GATA_CODE_00002.getValue());
//			resMsg.setOrigDomain(txnTmpLog.getMerId());
//			resMsg.setBackURL(txnTmpLog.getBackUrl());
//			resMsg.setServerURL(txnTmpLog.getServerUrl());
//		}
//		resMsg.setRspCode(crmCode);
//		resMsg.setRspInfo(crmDesc);
//		return resMsg;
//	}
}
