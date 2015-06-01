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
import com.huateng.cmupay.jms.business.crm.CrmChargeBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankConsumeReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankConsumeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 银行缴费
 * 
 * @author zeng.j
 * 
 */
@Controller("bankPayAction")
@Scope("prototype")
public class BankPayAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {

	@Autowired
	private CrmChargeBus crmChargeBus;

	@Override
	public BankMsgVo execute(BankMsgVo msgVo) throws AppBizException {
		logger.debug("银行缴费,BankPayAction execute(Object) - start");
		// 请求报文
		BankMsgVo reqMsg = msgVo;
		BankConsumeReqVo reqBody = new BankConsumeReqVo();
		BankConsumeResVo resBody = new BankConsumeResVo();
		BankMsgVo resMsg = reqMsg;
		UpayCsysTxnLog txnLog = null;
		String transIDH = msgVo.getTxnSeq();
		try {
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			reqMsg.setBody(reqBody);
			// 响应报文
			/* 作为银行的接收方交易流水，同时作为upss向crm发起交易的发起方交易流水 */
			
			/* 作为银行的接收方交易时间，同时作为upss向crm发起交易的发起方交易时间 */
			String transIDHTime = msgVo.getTxnTime();
			String intTxnDate = msgVo.getTxnDate();
			Long seqId = msgVo.getSeqId();
			resMsg.setRcvDate(transIDHTime.substring(0, 8));
			resMsg.setRcvDateTime(transIDHTime);
			resMsg.setRcvTransID(transIDH);
			resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			String transSeq=Serial.genSerialNo(CommonConstant.Sequence.IntSeq.getValue());
			/** 交易代码 */
			UpayCsysTransCode transCode = msgVo.getTransCode();
			//检查重复订单
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("reqDomain", reqMsg.getReqSys());
			params1.put("reqTransId", reqMsg.getReqTransID());
			txnLog = upayCsysTxnLogService.findObj(params1);
			if (txnLog != null) {
				log.warn("银行缴费,订单重复,内部交易流水号:{},业务发起方:{},银行交易流水号:{}" , 
						new Object[] {transIDH, reqMsg.getReqSys(),msgVo.getReqTransID()});
				logger.warn("银行缴费,订单重复,内部交易流水号:{},业务发起方:{},银行交易流水号:{}" , 
						new Object[] {transIDH, reqMsg.getReqSys(),msgVo.getReqTransID()});
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				logger.debug("BankPayAction execute(Object) - end");
				return resMsg;
			}else{
				txnLog = new UpayCsysTxnLog();
			}
			/** 交易流水 */
			initLog(txnLog, reqMsg, resMsg, reqBody, seqId, transIDH,
					intTxnDate, transIDHTime);
			txnLog.setSettleDate(reqMsg.getReqDate());
			upayCsysTxnLogService.add(txnLog);
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(reqBody.getIDValue());
			String idProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
			txnLog.setIdProvince(idProvince);
			/* 验证消息 */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				log.warn("银行缴费,报文体校验失败:{},内部交易流水:{},发起方:{},银行交易流水号:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys(),msgVo.getReqTransID() });
				logger.warn("银行缴费,报文体校验失败:{},内部交易流水:{},发起方:{},银行交易流水号:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys(),msgVo.getReqTransID() });
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_014A04
						.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc()
						+ checkrtn);
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A04
						.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A04
						.getDesc() + checkrtn);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				resMsg.setRspCode(RspCodeConstant.Bank.BANK_014A04.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc());
				resMsg.setBody(resBody);
				logger.debug("BankPayAction execute(Object) - end");
				return resMsg;
			}
           if (null == idProvince) {
				log.warn("银行缴费,手机号码不正确:{},内部交易流水:{},发起方:{},银行交易流水号:{}",
						new Object[] { transIDH, reqBody.getIDValue(),
								reqMsg.getReqSys(),msgVo.getReqTransID() });
				logger.warn("银行缴费,手机号码不正确:{},内部交易流水:{},发起方:{},银行交易流水号:{}",
						new Object[] { transIDH, reqBody.getIDValue(),
								reqMsg.getReqSys(),msgVo.getReqTransID() });
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A17
						.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A17
						.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A17
						.getDesc());
//				txnLog.setChlRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
				resMsg.setBody(resBody);
				logger.debug("BankPayAction execute(Object) - end");
				return resMsg;
			}
			txnLog.setIdProvince(idProvince);
			String forwardOrg = SysMapCache.getProvCd(idProvince).getSysCd();// 转发方机构代码
//			boolean checkFlag = false;// 接收方机构状态正常标识

			/** 报文头 */
			CrmMsgVo forwardMsg = new CrmMsgVo();
			CrmMsgVo forwardRtMsg = new CrmMsgVo();
			forwardMsg.setTransCode(transCode);
			forwardMsg.setVersion(ExcConstant.CRM_VERSION);
			forwardMsg.setTestFlag(testFlag);
			forwardMsg.setBIPCode(CommonConstant.Bip.Bis15.getValue());
			forwardMsg
					.setActivityCode(CommonConstant.CrmTrans.Crm07.getValue());
			forwardMsg.setActionCode(CommonConstant.ActionCode.Requset
					.getValue());
			forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone
					.getValue());
			forwardMsg.setRouteValue(reqBody.getIDValue());
			forwardMsg.setSessionID(transSeq); //
			forwardMsg.setTransIDO(transSeq);
			forwardMsg.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
			forwardMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
			forwardMsg.setMsgReceiver(forwardOrg);//

			
			String checkFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode(), 
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());

			if (checkFlag == null) {
				Map<String, String> params = new HashMap<String, String>();
				
				params.put("idType", reqBody.getIDType());
				params.put("idValue", reqBody.getIDValue());
//				params.put("transactionID", Serial.genSerialNos(CommonConstant.Sequence.OprId.getValue()));
				//TransactionID设置成32位
				params.put("transactionID", Serial.genSerialNum(CommonConstant.Sequence.OprId.getValue()));
				params.put("actionDate", intTxnDate);
				params.put("actionTime", StrUtil.subString(transIDHTime, 0, 14));
			    
				//新充值改造的字段
				params.put("busiTransID", String.valueOf(reqMsg.getReqTransID()));
				
				params.put("chargeMoney", String.valueOf(txnLog.getPayAmt()));
				params.put("organID",String.valueOf(txnLog.getBankId()));
				params.put("cnlTyp", reqMsg.getReqChannel());
				params.put("payedType",CommonConstant.PayType.PayPre.getValue());
				params.put("settleDate", reqMsg.getReqDate());
				params.put("orderNo", txnLog.getOrderId());
				params.put("payment", String.valueOf(txnLog.getPayAmt()));
				params.put("activityNo",txnLog.getActivityNo());
				
//				params1.put("payTransID", "");
//				params1.put("productNo", "");
//				params1.put("orderCnt", "");
//				params1.put("commision", "");
//				params1.put("rebateFee", "");
//				params1.put("prodDiscount","");
//				params1.put("creditCardFee","");
//				params1.put("serviceFee", "");
//				params1.put("productShelfNo", "");
				
				logger.info(
						"银行缴费,手机:{}充值,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{},发往省交易流水号:{}",
						new Object[] { reqBody.getIDValue(), transIDH,
								reqMsg.getReqSys(), forwardMsg.getMsgReceiver(),msgVo.getReqTransID(),transSeq});
				forwardRtMsg = crmChargeBus.execute(forwardMsg, params, txnLog,
						null);
				logger.info(
						"银行缴费,手机:{}充值返回,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{},发往省交易流水号:{}",
						new Object[] { reqBody.getIDValue(), transIDH,
								reqMsg.getReqSys(), forwardMsg.getMsgReceiver(),msgVo.getReqTransID(), transSeq});
				txnLog.setRcvTransDt(StrUtil.subString(
						forwardMsg.getTransIDOTime(), 0, 8));
				txnLog.setRcvTransId(forwardMsg.getTransIDO());
				txnLog.setRcvTransTm(forwardMsg.getTransIDOTime());
				CrmChargeResVo rtBody = null;
				if ("".equals(forwardRtMsg.getBody())) {
					log.warn(
							"银行缴费,充值返回报文体为空,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{}",
							new Object[] { transIDH,
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),msgVo.getReqTransID() });
					logger.warn(
							"银行缴费,充值返回报文体为空,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{}",
							new Object[] { transIDH,
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),msgVo.getReqTransID() });

					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
							.getDateyyyyMMddHHmmssSSS());
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A07
							.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A07
							.getDesc()+":充值网状网超时"+checkFlag);
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A07
							.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A07
							.getDesc()+":充值网状网超时"+checkFlag);

//					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
//							.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
							.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A07
							.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A07
							.getDesc()+":网状网超时");
					resMsg.setBody(resBody);
					logger.debug("BankPayAction execute(Object) - end");
					return resMsg;
				}else{
					rtBody = (CrmChargeResVo) forwardRtMsg.getBody();
				}
				if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
						forwardRtMsg.getRspCode())
						&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
								rtBody.getRspCode())) {
					log.succ(
							"银行缴费,充值返回成功,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{}",
							new Object[] { transIDH,
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),msgVo.getReqTransID() });
					logger.info(
							"银行缴费,充值返回成功,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{}",
							new Object[] { transIDH,
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),msgVo.getReqTransID() });
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
							.getValue());
//					txnLog.setChlRspType(CommonConstant.CrmRspType.Success
//							.getValue());
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00
							.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00
							.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00
							.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00
							.getDesc());
					upayCsysTxnLogService.modify(txnLog);
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00
							.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00
							.getDesc());
					resMsg.setBody(resBody);
					logger.debug("BankPayAction execute(Object) - end");
					return resMsg;
				} else if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
						forwardRtMsg.getRspCode())) {
					log.warn(
							"银行缴费,充值返回超时,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{}",
							new Object[] { transIDH,
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),msgVo.getReqTransID() });
					logger.warn(
							"银行缴费,充值返回超时,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{}",
							new Object[] { transIDH,
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),msgVo.getReqTransID() });
//					txnLog.setChlRspType(CommonConstant.CrmRspType.SysErr
//							.getValue());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A07
							.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A07
							.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A07
							.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A07
							.getDesc());
					upayCsysTxnLogService.modify(txnLog);

					resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A07
							.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A07
							.getDesc());

					resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A07
							.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A07
							.getDesc());
					resMsg.setBody(resBody);
					logger.debug("BankPayAction execute(Object) - end");
					return resMsg;
				} else {
					log.warn(
							"银行缴费,充值返回失败,内部交易流水:{},发起方:{},接收方:{},返回码:{},银行交易流水号:{}",
							new Object[] { transIDH,
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),
									rtBody.getRspCode(),msgVo.getReqTransID() });
					logger.warn(
							"银行缴费,充值返回失败,内部交易流水:{},发起方:{},接收方:{},返回码:{},银行交易流水号:{}",
							new Object[] {transIDH,
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),
									rtBody.getRspCode(),msgVo.getReqTransID() });
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
					String errName = BankErrorCodeCache.getBankErrCode(rtBody
							.getRspCode());
					logger.debug(
							"银行缴费,内部交易流水:{}转换银行返回码crm 返回码：{} ，转化为银行的返回码： {}",
							new Object[] { transIDH,
									rtBody.getRspCode(), errName });
					txnLog.setChlRspCode(errName);
					txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errName));
					txnLog.setChlSubRspCode(errName);
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errName));
					upayCsysTxnLogService.modify(txnLog);
					resMsg.setRspCode(errName);
					resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errName));
					resMsg.setBody(resBody);
					logger.debug("BankPayAction execute(Object) - end");
					return resMsg;
				}
			} else {
				log.info("银行缴费,接收方机构状态异常,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{}",
						new Object[] { transIDH, reqMsg.getReqSys(),
								forwardMsg.getMsgReceiver(),msgVo.getReqTransID() });
				logger.warn(
						"银行缴费,接收方机构状态异常,内部交易流水:{},发起方:{},接收方:{},银行交易流水号:{}",
						 new Object[] { transIDH,
								reqMsg.getReqSys(), forwardMsg.getMsgReceiver(),msgVo.getReqTransID() });
//				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
//						.getValue());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18
						.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18
						.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18
						.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
						.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				resMsg.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());

				resMsg.setBody(resBody);
				logger.debug("BankPayAction execute(Object) - end");
				return resMsg;
			}
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.error(
					"银行缴费,运行异常!内部交易流水号:{},业务发起方:{},银行交易流水号:{}",
					new Object[] {
							RspCodeConstant.Bank.getDescByValue(errCode),
							transIDH, reqMsg.getReqSys() ,msgVo.getReqTransID()});
			logger.error(
					"银行缴费,运行异常!内部交易流水号:{},业务发起方:{},银行交易流水号:{}",
					new Object[] { 
							transIDH, reqMsg.getReqSys(),msgVo.getReqTransID() });
			logger.error("银行缴费,运行异常,内部交易流水号: " +  reqMsg.getTxnSeq(), e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
//			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());

			upayCsysTxnLogService.modify(txnLog);

			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setBody(resBody);
			logger.debug("BankPayAction execute(Object) - end");
			return resMsg;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.error(
					"银行缴费,业务异常!内部交易流水号:{},业务发起方:{},银行交易流水号:{}",
					new Object[] {transIDH, reqMsg.getReqSys(),msgVo.getReqTransID() });
			logger.error(
					"银行缴费,业务异常!内部交易流水号:{},业务发起方:{},银行交易流水号:{}",
					new Object[] {
							transIDH, reqMsg.getReqSys(),msgVo.getReqTransID() });
			logger.error("银行缴费,业务异常,内部交易流水号: " +  reqMsg.getTxnSeq(), e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());

			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
//			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());

			upayCsysTxnLogService.modify(txnLog);

			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setBody(resBody);
			logger.debug("BankPayAction execute(Object) - end");
			return resMsg;
		} catch (Exception e) {
			log.error("银行缴费,系统异常!内部交易流水号:{},业务发起方:{},银行交易流水号:{}",
					new Object[] { transIDH, reqMsg.getReqSys(),msgVo.getReqTransID() });
			logger.error("银行缴费,系统异常!内部交易流水号:{},业务发起方:{},银行交易流水号:{}",
					new Object[] {transIDH, reqMsg.getReqSys() ,msgVo.getReqTransID()});
			logger.error("银行缴费,系统异常,内部交易流水号: " +  reqMsg.getTxnSeq(), e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());

//			txnLog.setChlRspType(CommonConstant.CrmRspType.SysErr.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
			resMsg.setBody(resBody);
			logger.debug("BankPayAction execute(Object) - end");
			return resMsg;
		}
	}

	/**
	 * 初始化交易流水
	 * 
	 * @param txnLog
	 * @param reqMsg
	 * @param resMsg
	 * @param reqBody
	 * @param seqId
	 * @param transIDH
	 * @param intTxnDate
	 * @param transIDHTime
	 */
	private void initLog(UpayCsysTxnLog txnLog, BankMsgVo reqMsg,
			BankMsgVo resMsg, BankConsumeReqVo reqBody, Long seqId,
			String transIDH, String intTxnDate, String transIDHTime) {
		/** 交易代码 */
		UpayCsysTransCode transCode = reqMsg.getTransCode();
		/** 交易流水 */
		txnLog.setSeqId(seqId);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setIntMqSeq(reqMsg.getMqSeq());
//		txnLog.setTxnCat(transCode.getTxnCat());
		txnLog.setBussType(transCode.getBussType());
		txnLog.setBussChl(transCode.getBussChl());
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setSettleDate(reqMsg.getReqDate());
		txnLog.setReqDomain(reqMsg.getReqSys());
		txnLog.setReqCnlType(reqMsg.getReqChannel());
		txnLog.setReqActivityCode(reqMsg.getActivityCode());
		txnLog.setReqTransId(reqMsg.getReqTransID());
		txnLog.setReqTransDt(reqMsg.getReqDate());
		txnLog.setReqTransTm(reqMsg.getReqDateTime());
		txnLog.setReqTranshId(resMsg.getRcvTransID());
		txnLog.setReqTranshDt(resMsg.getRcvDate());
		txnLog.setReqTranshTm(resMsg.getRcvDateTime());
		txnLog.setIdType(reqBody.getIDType());
		txnLog.setIdValue(reqBody.getIDValue());
		txnLog.setPayAmt(StringFormat.paseLong(reqBody.getPayed()));
		txnLog.setBankId(reqMsg.getReqSys());
		txnLog.setReqOprId(reqMsg.getReqTransID());
		txnLog.setReqOprDt(reqMsg.getReqDate());
		txnLog.setReqOprTm(reqMsg.getReqDateTime());
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
				.getDateyyyyMMddHHmmssSSS());
		txnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
	}
}
