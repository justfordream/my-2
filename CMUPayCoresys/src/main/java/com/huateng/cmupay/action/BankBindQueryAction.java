package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankBindQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankBindQueryResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.utils.UUIDGenerator;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StringUtil;

/** 
 * 银行发起的签约关系查询交易
 *  
 * @author ning.z
 * 
 */
@Controller("bankBindQueryAction")
@Scope("prototype")
public class BankBindQueryAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {
	public final Logger logger = LoggerFactory.getLogger(this.getClass());

	public BankMsgVo execute(BankMsgVo paramData) throws AppBizException {
		logger.debug("BankBindQueryAction execute(Object) - start");
		BankMsgVo msgVo = (BankMsgVo) paramData;
		BankMsgVo msgVoRtn = msgVo;
		BankBindQueryReqVo bodyMsgReqVo = new BankBindQueryReqVo();
		BankBindQueryResVo bankResVo = new BankBindQueryResVo();
		MsgHandle.unmarshaller(bodyMsgReqVo, (String) msgVo.getBody());
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		String transIDH = msgVo.getTxnSeq();
		String intTxnDate = msgVo.getTxnDate();
		String transIDHTime = msgVo.getTxnTime();
		Long seqId = msgVo.getSeqId();
		msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
		msgVoRtn.setRcvTransID(transIDH);// 接收方交易流水号
		msgVoRtn.setRcvDateTime(transIDHTime);
		msgVoRtn.setRcvDate(intTxnDate);
		UpayCsysTransCode transCode = msgVo.getTransCode();// 获取交易代码
		try {
			logger.info("内部交易流水号:{},检查订单状态",new Object[]{transIDH});
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqDomain", msgVo.getReqSys());
			param.put("reqTransId", msgVo.getReqTransID());
			UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService
					.findObj(param);
			// TODO 重复订单处理需重新考虑
			if (upayCsysTxnLog != null) {
				log.info("订单重复,内部交易流水号:{},业务发起方:{}" , 
						new Object[] {transIDH, paramData.getReqSys()});
				logger.info("订单重复,内部交易流水号:{},业务发起方:{},TransIDO:{}" , 
						new Object[] {transIDH, paramData.getReqSys(),msgVo.getReqTransID()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				msgVoRtn.setBody(bankResVo);
				return msgVoRtn;
			}
			// 记录交易流水
			logger.debug("内部交易流水号:{},记录交易流水--start",new Object[]{transIDH});
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
			txnLog.setReqTransTm(msgVo.getReqDateTime());
			txnLog.setReqOprDt(msgVo.getReqDate());
			txnLog.setReqOprId(msgVo.getReqTransID());
			txnLog.setReqOprTm(msgVo.getReqDateTime());
			txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setReqTranshDt(intTxnDate);
			txnLog.setReqTranshId(transIDH);
			txnLog.setReqTranshTm(transIDHTime);
			txnLog.setRcvTransDt(intTxnDate);
			txnLog.setRcvTransId(transIDH);
			txnLog.setRcvTransTm(transIDHTime);
			//平台发给省的报文如果有TransactionID字段，那么RcvOprId值填TransactionID，
			//如果报文中没有TransactionID，那么RcvOprId不用填 UR单：UPAY_DT-243
//			txnLog.setRcvOprId(UUIDGenerator.generateUUID());
			txnLog.setReqDomain(msgVo.getReqSys());
			txnLog.setReqCnlType(msgVo.getReqChannel());// 发起方渠道标识
			txnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
			txnLog.setIdType(bodyMsgReqVo.getIDType());
			txnLog.setIdValue(bodyMsgReqVo.getIDValue());
			txnLog.setOriOrgId(null);
			txnLog.setOriOprTransId(null);// 原交易流水号
			txnLog.setOriReqDate(null);//
			txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			txnLog.setSettleDate(msgVo.getReqDate());
			upayCsysTxnLogService.add(txnLog);
			// 报文体格式校验
			logger.debug("内部交易流水号:{},报文体内容校验--start",new Object[]{transIDH});
			String validateMsg = this.validateModel(bodyMsgReqVo);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.info("内部交易流水号:{},body体校验成功",new Object[]{transIDH});
			} else {
				logger.warn("内部交易流水号:{},报文体校验失败:{},业务发起方:{}",new Object[]{transIDH,validateMsg,paramData.getReqSys()});
				log.warn("内部交易流水号:{},报文体校验失败:{},业务发起方:{}",new Object[]{transIDH,validateMsg,paramData.getReqSys()});
				String code = validateMsg.split(":")[0];
				msgVoRtn.setRspCode(code);
				msgVoRtn.setRspDesc(validateMsg);
				msgVoRtn.setBody(bankResVo);
				txnLog.setChlRspCode(code);
				txnLog.setChlRspDesc(validateMsg);
				txnLog.setChlSubRspCode(code);
				txnLog.setChlSubRspDesc(validateMsg);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
//			String mainProvince = upayCsysImsiLdCdService.findProvinceByMobileNumber(bodyMsgReqVo.getIDValue());
//			String mainProvince = ProvAreaCache.getProvAreaByPrimary(bodyMsgReqVo.getIDValue());
//			String mainProvince = findProvinceByMobileNumber(bodyMsgReqVo.getIDValue());
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(bodyMsgReqVo.getIDValue());
			String mainProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
			
			txnLog.setIdProvince(mainProvince);
			if (mainProvince == null) {
				logger.warn("手机号码不正确:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {bodyMsgReqVo.getIDValue(), transIDH, paramData.getReqSys()});
				log.warn("手机号码不正确:{},内部交易流水号:{},业务发起方:{}" , 
						new Object[] {bodyMsgReqVo.getIDValue(), transIDH, paramData.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
				msgVoRtn.setBody(bankResVo);
				return msgVoRtn;
			}
			logger.info("intTxnSeq:{},查询签约关系:{}",new Object[]{transIDH,bodyMsgReqVo.getIDValue()});
			Map<String, Object> bankBindParam = new HashMap<String, Object>();
			bankBindParam.put("idValue", bodyMsgReqVo.getIDValue());
			bankBindParam.put("idType",CommonConstant.UserSignType.Phone.getValue());
			bankBindParam.put("mainFlag",CommonConstant.Mainflag.Master.getValue());
			bankBindParam.put("status",CommonConstant.BindStatus.Bind.getValue());
			UpayCsysBindInfo bindInfo = upayCsysBindInfoService.findObj(bankBindParam);
			if (bindInfo == null) {
				logger.warn("内部交易流水号:{},签约关系为空:{},业务发起方:{}",
						new Object[]{transIDH,bodyMsgReqVo.getIDValue(),paramData.getReqSys()});
				log.warn("内部交易流水号:{},签约关系为空:{},业务发起方:{}",
						new Object[]{transIDH,bodyMsgReqVo.getIDValue(),paramData.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A09.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A09.getDesc());
				msgVoRtn.setBody(bankResVo);
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A09.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A09.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A09.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A09.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			} else if (!(msgVo.getReqSys().equals(bindInfo.getBankId()))) {
				logger.warn("内部交易流水号:{},发起方非签约银行:{},业务发起方:{}",
						new Object[]{transIDH,bindInfo.getBankId(),paramData.getReqSys()});
				log.warn("内部交易流水号:{},发起方非签约银行:{},业务发起方:{}",
						new Object[]{transIDH,bindInfo.getBankId(),paramData.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A23.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A23.getDesc());
				msgVoRtn.setBody(bankResVo);
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_013A23.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_013A23.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_013A23.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_013A23.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
//			// TODO confirm
//			if ((null != bindInfo.getUserCat() || !"".equals(bindInfo
//					.getUserCat()))
//					&& (null != bindInfo.getPayType() || !"".equals(bindInfo
//							.getPayType()))) {
//				if (CommonConstant.UpayFeeType.PreFee.getValue().equals(
//						bindInfo.getPayType())&&CommonConstant.PayWay.AutoConsume.getValue()
//						 .equals(bindInfo.getUserCat())){
//					bankResVo.setRechAmount(bindInfo.getRechAmount());
//					bankResVo.setRechThreshold(bindInfo.getRechThreshold());
//				}
//			}			
			if ((null != bindInfo.getPayType() || !"".equals(bindInfo
							.getPayType()))) {
				if (CommonConstant.PayWay.AutoConsume.getValue().equals(
						bindInfo.getPayType())){
					bankResVo.setRechAmount(bindInfo.getRechAmount());
					bankResVo.setRechThreshold(bindInfo.getRechThreshold());
				}
			}
			bankResVo.setBankAcctID(bindInfo.getBankAccId());
			bankResVo.setBankAcctType(bindInfo.getBankAcctType());
			bankResVo.setPayType(bindInfo.getPayType());
			bankResVo.setSubID(bindInfo.getSubId());
			bankResVo.setSubTime(bindInfo.getSignSubTime());
			bankResVo.setHomeProv(bindInfo.getIdProvince());
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			msgVoRtn.setBody(bankResVo);
			txnLog.setBankAccId(bindInfo.getBankAccId());
			txnLog.setBankAcctType(bindInfo.getBankAcctType());
			txnLog.setPayType(bindInfo.getPayType());
			txnLog.setSubId(bindInfo.getSubId());
			txnLog.setSubTime(bindInfo.getSignSubTime());
			txnLog.setBankId(bindInfo.getBankId());
			txnLog.setIdProvince(mainProvince);
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			logger.info("内部交易流水号:{},业务发起方:{},应答码:{}",
					new Object[]{transIDH,paramData.getReqSys(),msgVoRtn.getRspCode()});
			log.succ("内部交易流水号:{},业务发起方:{},应答码:{}",
					new Object[]{transIDH,paramData.getReqSys(),msgVoRtn.getRspCode()});
			return msgVoRtn;

		} catch (AppRTException e) {
			log.error("内部异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {transIDH, paramData.getReqSys()});
			logger.error("内部异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {transIDH, paramData.getReqSys()});
			logger.error("内部异常:",e);
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setBody(bankResVo);
//			txnLog.setIdProvince(mainProvince);
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
//			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr .getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			logger.debug("BankBindQueryAction execute(Object) - end");
			return msgVoRtn;
		} catch (AppBizException e) {
			log.error("业务异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {transIDH, paramData.getReqSys()});
			logger.error("业务异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {transIDH, paramData.getReqSys()});
			logger.error("业务异常",e);
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setBody(bankResVo);
//			txnLog.setIdProvince(mainProvince);
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
//			txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		} catch (Exception e) {
			log.error("未知异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {transIDH, paramData.getReqSys()});
			logger.error("未知异常!内部交易流水号:{},业务发起方:{}" , 
					new Object[] {transIDH, paramData.getReqSys()});	
			logger.error("未知异常:",e);
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
			msgVoRtn.setBody(bankResVo);
//			txnLog.setIdProvince(mainProvince);
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			if (CommonConstant.TxnStatus.InitStatus.toString().equals(
					txnLog.getStatus())
					|| "".equals(StringUtil.toTrim(txnLog.getStatus()))) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			}
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
//			txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		}
	}
}
