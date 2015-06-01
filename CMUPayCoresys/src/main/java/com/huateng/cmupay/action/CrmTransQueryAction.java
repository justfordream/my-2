package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogHisService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.message.SendBankJmsMessageImpl;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankTransQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankTransQueryResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryResVo;
import com.huateng.cmupay.utils.UUIDGenerator;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 移动渠道交易结果查询
 * 
 * @author zeng.j
 * 
 */
@Controller("crmTransQueryAction")
@Scope("prototype")
public class CrmTransQueryAction extends AbsBaseAction<CrmMsgVo, CrmMsgVo> {

	@Autowired
	private SendBankJmsMessageImpl sendBankJmsMessage;
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	
	@Autowired
	private IUpayCsysTxnLogHisService upayCsysTxnLogHisService;
	

	@Override
	public CrmMsgVo execute(CrmMsgVo msgVo) throws AppBizException {
		logger.debug("CrmTransQueryAction execute(Object) - start");
		/* 请求报文 */
		CrmMsgVo reqMsg = msgVo;
		CrmTransQueryReqVo reqBody = new CrmTransQueryReqVo();
		/* 应答报文 */
		CrmMsgVo resMsg = reqMsg;
		CrmTransQueryResVo resBody = new CrmTransQueryResVo();
		/* 交易流水 */
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		String transIDH = reqMsg.getTxnSeq();
		try {
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqDomain", reqMsg.getMsgSender());
			param.put("reqTransId", reqMsg.getTransIDO());
			UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService.findObj(param);
			if (upayCsysTxnLog != null) {
				logger.warn("移动渠道交易结果查询,操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},",
						new Object[]{reqMsg.getTransIDO(),reqBody.getOriTransactionID(),transIDH,
						reqMsg.getMsgSender()});
				log.warn("移动渠道交易结果查询,操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},",
						new Object[]{reqMsg.getTransIDO(),reqBody.getOriTransactionID(),transIDH,
						reqMsg.getMsgSender()});
				resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				resMsg.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resBody.setOriTransactionID(reqMsg.getTransIDO());
				resBody.setOriActionDate(reqBody.getOriActionDate());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc());
				resMsg.setBody(resBody);
				
				logger.debug("结束移动渠道交易结果查询交易");
				return resMsg;
			}
			
			reqMsg.setBody(reqBody);
			String transIDHTime = msgVo.getTxnTime();
			String intTxnDate = reqMsg.getTxnDate();
			txnLog.setSettleDate(intTxnDate);
			Long seqId = reqMsg.getSeqId();

			resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			resMsg.setRouteType(CommonConstant.RouteType.RouteProvince
					.getValue());
			resMsg.setRouteValue(reqMsg.getRouteValue());
			resMsg.setTransIDH(transIDH);
			resMsg.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));

			resBody.setOriActionDate(reqBody.getOriActionDate());
			resBody.setOriTransactionID(reqBody.getOriTransactionID());

			/** 交易代码 */
			UpayCsysTransCode transCode = reqMsg.getTransCode();
			initLog(txnLog, seqId, transIDH, intTxnDate, transIDHTime, reqMsg,
					transCode, resMsg, reqBody);
			txnLog.setSettleDate(DateUtil.getDateyyyyMMdd());
			upayCsysTxnLogService.add(txnLog);

			/* 验证消息 */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				logger.warn("移动渠道交易结果查询,内部交易流水号:{},报文体check失败：{}",
						transIDH, checkrtn);
				log.warn("移动渠道交易结果查询,格式校验失败!内部交易流水号:{},CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},错误字段:{}",
						new Object[]{transIDH,msgVo.getTransIDO(),msgVo.getTransIDO(),transIDH,
						msgVo.getMsgSender(),checkrtn});
				resBody.setRspCode(RspCodeConstant.Crm.CRM_4A04.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A04.getDesc()+","+checkrtn);

				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc()+":"+checkrtn);
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				resMsg.setRspType(txnLog.getChlRspType());
				resMsg.setBody(resBody);
				
				logger.debug("CrmTransQueryAction execute(Object) - end");
				return resMsg;
			}

			// 查询原交易，判断是否需要转发
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reqOprId", reqBody.getOriTransactionID());
//			params.put("reqDomain", reqBody.getOriReqSys());
			params.put("reqTransDt", reqBody.getOriActionDate());
			UpayCsysTxnLog transLog = upayCsysTxnLogService.findObj(params);
			UpayCsysTxnLogHis hisStlTransLog = null;
			if (null == transLog) {
				// 前线库查不到去后线库查
				hisStlTransLog = upayCsysTxnLogHisService.findHisStlObj(params);
				// 如果后线库也查不到，则报原交易不存在
				if(hisStlTransLog == null) {
					logger.warn("移动渠道交易结果查询,原交易不存在:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
							new Object[]{reqBody.getOriTransactionID(),reqMsg.getTransIDO(),transIDH,
							reqMsg.getMsgSender()});
					log.warn("移动渠道交易结果查询,原交易不存在:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
							new Object[]{reqBody.getOriTransactionID(),reqMsg.getTransIDO(),transIDH,
							reqMsg.getMsgSender()});
					resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					resMsg.setRspType(CommonConstant.CrmRspType.Success.getValue());
					resBody.setRspCode(RspCodeConstant.Crm.CRM_4A05.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A05.getDesc());
					txnLog.setChlRspCode(resMsg.getRspCode());
					txnLog.setChlRspDesc(resMsg.getRspDesc());
					txnLog.setChlRspType(resMsg.getRspType());
					txnLog.setChlSubRspCode(resBody.getRspCode());
					txnLog.setChlSubRspDesc(resBody.getRspInfo());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					resMsg.setBody(resBody);
					
					logger.debug("CrmTransQueryAction execute(Object) - end");
					return resMsg;
				}
			}
			
			txnLog.setOriOrgId(transLog != null ? transLog.getReqDomain() : hisStlTransLog.getReqDomain());
			txnLog.setRcvDomain(transLog != null ? transLog.getRcvDomain() : hisStlTransLog.getRcvDomain());
			
			String outerDomain = (transLog != null ? transLog.getOuterDomain() : hisStlTransLog.getOuterDomain());
			if (StringUtils.isBlank(outerDomain) && CommonConstant.TxnStatus.InitStatus.getValue().equals(transLog != null ? transLog.getStatus() : hisStlTransLog.getStatus())) {
				logger.warn("移动渠道交易结果查询,原缴费交易未发往银行扣款:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqBody.getOriTransactionID(),reqMsg.getTransIDO(),transIDH,
						reqMsg.getMsgSender()});
				log.warn("移动渠道交易结果查询,原缴费交易未发往银行扣款:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqBody.getOriTransactionID(),reqMsg.getTransIDO(),transIDH,
						reqMsg.getMsgSender()});
				resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
				resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_4A30.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A30.getDesc());
				resMsg.setRspType(CommonConstant.CrmRspType.Success.getValue());
				resMsg.setBody(resBody);
				
				txnLog.setChlRspCode(resMsg.getRspCode());
				txnLog.setChlRspDesc(resMsg.getRspDesc());
				txnLog.setChlRspType(resMsg.getRspType());
				txnLog.setChlSubRspCode(resBody.getRspCode());
				txnLog.setChlSubRspDesc(resBody.getRspInfo());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				upayCsysTxnLogService.modify(txnLog);
				
				
				logger.debug("CrmTransQueryAction execute(Object) - end");
				return resMsg;
			}else if (StringUtils.isBlank(outerDomain) && CommonConstant.TxnStatus.TxnFail.getValue().equals(transLog != null ? transLog.getStatus() : hisStlTransLog.getStatus())) {
				logger.warn("移动渠道交易结果查询,原缴费交易失败:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqBody.getOriTransactionID(),reqMsg.getTransIDO(),transIDH,
						reqMsg.getMsgSender()});
				log.warn("移动渠道交易结果查询,原缴费交易失败:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqBody.getOriTransactionID(),reqMsg.getTransIDO(),transIDH,
						reqMsg.getMsgSender()});
				resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
				resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
				resBody.setRspCode(transLog != null ? transLog.getChlSubRspCode() : hisStlTransLog.getChlSubRspCode());
				resBody.setRspInfo(transLog != null ? transLog.getChlSubRspDesc() : hisStlTransLog.getChlSubRspDesc());
				resMsg.setRspType(CommonConstant.CrmRspType.Success.getValue());
				resMsg.setBody(resBody);
				
				txnLog.setChlRspCode(resMsg.getRspCode());
				txnLog.setChlRspDesc(resMsg.getRspDesc());
				txnLog.setChlRspType(resMsg.getRspType());
				txnLog.setChlSubRspCode(resBody.getRspCode());
				txnLog.setChlSubRspDesc(resBody.getRspInfo());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("CrmTransQueryAction execute(Object) - end");
				return resMsg;
			}
			
			String bankID  = transLog != null ? transLog.getBankId() : hisStlTransLog.getBankId();
			String idValue = transLog != null ? transLog.getIdValue() : hisStlTransLog.getIdValue();
			
			if(StringUtils.isBlank(bankID)){
				{
					logger.warn("移动渠道交易结果查询,落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{bankID,reqMsg.getTransIDO(),transIDH,
							reqMsg.getMsgSender(),idValue});
					log.warn("移动渠道交易结果查询,落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{bankID,reqMsg.getTransIDO(),transIDH,
							reqMsg.getMsgSender(),idValue});
					
					resBody.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A35.getDesc() + "接收方交易权限关闭");

					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.Success
							.getValue());
					txnLog.setChlSubRspCode(resBody.getRspCode());
					txnLog.setChlSubRspDesc(resBody.getRspInfo());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					resMsg.setRspCode(txnLog.getChlRspCode());
					resMsg.setRspDesc(txnLog.getChlRspDesc());
					resMsg.setRspType(CommonConstant.CrmRspType.Success.getValue());
					resMsg.setBody(resBody);

					logger.debug("CrmTransQueryAction execute(Object) - end");
					return resMsg;
				}
			}
			
//			boolean checkFlag = isO2OTransOn(msgVo.getMsgSender(),
//					bankID,msgVo.getTransCode().getTransCode());
			// 查询该交易的号码段属于移动还是联通电信的。
			ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(idValue);
			String checkFlag = offOrgTrans(msgVo.getMsgSender(),
					bankID,msgVo.getTransCode().getTransCode(), provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
			
			if(provincePhoneNum != null) {
				txnLog.setIdProvince(provincePhoneNum.getProvinceCode());
			}
			
			if (checkFlag == null) {
				BankMsgVo bankQueryRtMsg = bankTransQuery(transIDH, bankID,
						transLog, hisStlTransLog, reqBody, txnLog, upayCsysTxnLogService);
				if (bankQueryRtMsg == null) {
					logger.warn("移动渠道交易结果查询,银行返回报文为空!CRM操作流水:{},UPAY流水:{},发起省:{}" ,
							new Object[]{reqMsg.getTransIDO(),transIDH,
							reqMsg.getMsgSender()});
					log.warn("移动渠道交易结果查询,银行返回报文为空!CRM操作流水:{},UPAY流水:{},发起省:{}" ,
							new Object[]{reqMsg.getTransIDO(),transIDH,
							reqMsg.getMsgSender()});
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.getValue());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc()+checkFlag);
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					resBody.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_5A07.getDesc());

					resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					resMsg.setRspType(txnLog.getChlRspType());
					resMsg.setBody(resBody);
					
					logger.debug("CrmTransQueryAction execute(Object) - end");
					return resMsg;
				}
				BankTransQueryResVo bankQueryRtBody = (BankTransQueryResVo) bankQueryRtMsg
						.getBody();
				if (RspCodeConstant.Bank.BANK_020A00.getValue().equals(
						bankQueryRtMsg.getRspCode())
						&& RspCodeConstant.Bank.BANK_020A00.getValue().equals(
								bankQueryRtBody.getOriRspCode())) {
					
					logger.info("移动渠道交易结果查询,Bank应答原交易缴费成功!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
							new Object[]{bankQueryRtMsg.getRcvTransID(),transIDH,
							bankID,idValue,bankQueryRtMsg.getRspCode(),bankQueryRtMsg.getRspDesc()});
					log.info("移动渠道交易结果查询,Bank应答原交易缴费成功!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
							new Object[]{bankQueryRtMsg.getRcvTransID(),transIDH,
							bankID,idValue,bankQueryRtMsg.getRspCode(),bankQueryRtMsg.getRspDesc()});
					
					String chargeOrg = transLog != null ? transLog.getRcvDomain() : hisStlTransLog.getRcvDomain();// 充值省
					if (StringUtils.isBlank(chargeOrg)) {
						logger.warn("移动渠道交易结果查询,原交易未发往CRM进行充值:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
								new Object[]{reqBody.getOriTransactionID(),reqMsg.getTransIDO(),transIDH,
								reqMsg.getMsgSender()});
						log.warn("移动渠道交易结果查询,原交易未发往CRM进行充值:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
								new Object[]{reqBody.getOriTransactionID(),reqMsg.getTransIDO(),transIDH,
								reqMsg.getMsgSender()});
						resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
						resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
						resBody.setRspCode(transLog != null ? transLog.getRcvSubRspCode() : hisStlTransLog.getRcvSubRspCode());
						resBody.setRspInfo(transLog != null ? transLog.getRcvSubRspDesc() : hisStlTransLog.getRcvSubRspDesc());
						resMsg.setRspType(CommonConstant.CrmRspType.Success
								.getValue());
						resMsg.setBody(resBody);
						
						txnLog.setChlRspCode(resMsg.getRspCode());
						txnLog.setChlRspDesc(resMsg.getRspDesc());
						txnLog.setChlRspType(resMsg.getRspType());
						txnLog.setChlSubRspCode(resBody.getRspCode());
						txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						upayCsysTxnLogService.modify(txnLog);
						
						
						logger.debug("CrmTransQueryAction execute(Object) - end");
						return resMsg;
					}

					// 向充值省发起交易结果查询
					CrmMsgVo crmQueryRtMsg = null;
					if (orgStatusCheck(chargeOrg)) {
						crmQueryRtMsg = crmTransQuery(transIDH, transIDHTime,
								chargeOrg, reqBody, txnLog, transLog, hisStlTransLog,
								upayCsysTxnLogService);
					} else {
						logger.warn("移动渠道交易结果查询,落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{chargeOrg,reqMsg.getTransIDO(),transIDH,
								reqMsg.getMsgSender(),idValue});
						log.warn("移动渠道交易结果查询,落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{chargeOrg,reqMsg.getTransIDO(),transIDH,
								reqMsg.getMsgSender(),idValue});
						
						resBody.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
						resBody.setRspCode(RspCodeConstant.Crm.CRM_3A35.getDesc());
						resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
						resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
						resMsg.setRspType(CommonConstant.CrmRspType.Success
								.getValue());
						resMsg.setBody(resBody);

						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
						txnLog.setChlRspType(CommonConstant.CrmRspType.Success
								.getValue());
						txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A35.getDesc());
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
								.getValue());
						txnLog.setLastUpdTime(DateUtil
								.getDateyyyyMMddHHmmssSSS());
						upayCsysTxnLogService.modify(txnLog);

						
						logger.debug("CrmTransQueryAction execute(Object) - end");
						return resMsg;
					}
					if (crmQueryRtMsg == null) {
						log.warn("移动渠道交易结果查询,CRM前置超时!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
								new Object[]{reqMsg.getTransIDO(),reqMsg.getTransIDO(),transIDH,
								reqMsg.getMsgSender(),idValue,bankID});
						logger.warn("移动渠道交易结果查询,CRM前置超时!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
								new Object[]{reqMsg.getTransIDO(),reqMsg.getTransIDO(),transIDH,
								reqMsg.getMsgSender(),idValue,bankID});
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
								.getValue());
						txnLog.setLastUpdTime(DateUtil
								.getDateyyyyMMddHHmmssSSS());
						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
						txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());
						txnLog.setLastUpdTime(DateUtil
								.getDateyyyyMMddHHmmssSSS());
						upayCsysTxnLogService.modify(txnLog);
						resBody.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
						resBody.setRspInfo(RspCodeConstant.Crm.CRM_5A07.getDesc());

						resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						resMsg.setRspType(txnLog.getChlRspType());
						resMsg.setBody(resBody);
						
						logger.debug("CrmTransQueryAction execute(Object) - end");
						return resMsg;
					}

					CrmTransQueryResVo queryRtBody = crmQueryRtMsg.getBody() == null ? null
							: (CrmTransQueryResVo) crmQueryRtMsg.getBody();
					if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
							crmQueryRtMsg.getRspCode())
							&&RspCodeConstant.Crm.CRM_0000.getValue().equals(
									queryRtBody.getRspCode())) {
						logger.info("移动渠道交易结果查询,CRM成功响应:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
								new Object[]{crmQueryRtMsg.getTransIDO(),reqMsg.getTransIDO(),transIDH,
								reqMsg.getMsgSender(),idValue,bankID});
						log.succ("移动渠道交易结果查询,CRM成功响应:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
								new Object[]{crmQueryRtMsg.getTransIDO(),reqMsg.getTransIDO(),transIDH,
								reqMsg.getMsgSender(),idValue,bankID});

							resBody.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
							resBody.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
							resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
							resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
							resMsg.setRspType(CommonConstant.CrmRspType.Success
									.getValue());
							resMsg.setBody(resBody);

							txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
							txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
							txnLog.setRcvSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
							txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
							txnLog.setChlRspCode(resMsg.getRspCode());
							txnLog.setChlRspDesc(resMsg.getRspDesc());
							txnLog.setChlRspType(resMsg.getRspType());
							txnLog.setChlSubRspCode(resBody.getRspCode());
							txnLog.setChlSubRspDesc(resBody.getRspInfo());
							txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
									.getValue());
							txnLog.setLastUpdTime(DateUtil
									.getDateyyyyMMddHHmmssSSS());
							upayCsysTxnLogService.modify(txnLog);

							logger.debug("CrmTransQueryAction execute(Object) - end");
							return resMsg;
						}else {
							String errCode = queryRtBody.getRspCode();
							//由于CRM端要求,当crm端返回充值失败的时候
							if(RspCodeConstant.Crm.CRM_5A02.getValue().equals(errCode) || RspCodeConstant.Crm.CRM_4A05.getValue().equals(errCode)){
								logger.warn("移动渠道交易结果查询,CRM响应充值失败:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
										new Object[]{crmQueryRtMsg.getTransIDO(),reqMsg.getTransIDO(),transIDH,
										reqMsg.getMsgSender(),idValue,bankID});
								log.warn("移动渠道交易结果查询,CRM响应充值失败:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
										new Object[]{crmQueryRtMsg.getTransIDO(),reqMsg.getTransIDO(),transIDH,
										reqMsg.getMsgSender(),idValue,bankID});
								resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
								resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
								resMsg.setRspType(CommonConstant.CrmRspType.Success.getValue());
								resBody.setRspCode(RspCodeConstant.Crm.CRM_3A26.getValue());
								resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A26.getDesc());
//								resBody.setRspCode(errCode);
//								resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
								resMsg.setBody(resBody);
								
								txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
								txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
								txnLog.setRcvSubRspCode(errCode);
								txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
								txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
								txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
								txnLog.setChlRspType(resMsg.getRspType());
								txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A26.getValue());
								txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A26.getDesc());
//								txnLog.setChlSubRspCode(errCode);
//								txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
								txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
								txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
								upayCsysTxnLogService.modify(txnLog);
								
								logger.debug("CrmTransQueryAction execute(Object) - end");
								return resMsg;
							}
								logger.warn("移动渠道交易结果查询,CRM响应失败:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
										new Object[]{crmQueryRtMsg.getTransIDO(),reqMsg.getTransIDO(),transIDH,
										reqMsg.getMsgSender(),idValue,bankID});
								log.warn("移动渠道交易结果查询,CRM响应失败:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
										new Object[]{crmQueryRtMsg.getTransIDO(),reqMsg.getTransIDO(),transIDH,
										reqMsg.getMsgSender(),idValue,bankID});
								resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
								resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
								resMsg.setRspType(CommonConstant.CrmRspType.Success.getValue());
								resBody.setRspCode(errCode);
								resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
								resMsg.setBody(resBody);
								
								txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
								txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
								txnLog.setRcvSubRspCode(errCode);
								txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
								txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
								txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
								txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
								txnLog.setChlSubRspCode(errCode);
								txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
								txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
//								txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
								txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
								upayCsysTxnLogService.modify(txnLog);
								
								logger.debug("CrmTransQueryAction execute(Object) - end");
								return resMsg;	
							}
				} else {
					logger.warn("移动渠道交易结果查询,Bank应答原交易支付失败!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
							new Object[]{bankQueryRtMsg.getRcvTransID(),transIDH,
							txnLog.getBankId(),txnLog.getIdValue(),bankQueryRtMsg.getRspCode(),bankQueryRtMsg.getRspDesc()});
					log.warn("移动渠道交易结果查询,原交易支付失败!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
							new Object[]{bankQueryRtMsg.getRcvTransID(),transIDH,
							bankID,idValue,bankQueryRtMsg.getRspCode(),bankQueryRtMsg.getRspDesc()});
					String rspc = bankQueryRtBody.getOriRspCode()==null?bankQueryRtMsg.getRspCode():bankQueryRtBody.getOriRspCode();
					String errName = CrmErrorCodeCache.getCrmErrCode(rspc); 
//					String errName = CrmErrorCodeCache.getCrmErrCode(bankQueryRtBody.getOriRspCode()) == null ? 
//							  CrmErrorCodeCache.getCrmErrCode(bankQueryRtMsg.getRspCode())
//							: CrmErrorCodeCache.getCrmErrCode(bankQueryRtBody.getOriRspCode());
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.Success
							.getValue());
					txnLog.setChlSubRspCode(errName);
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errName));
//					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					resBody.setRspCode(errName);
					resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errName));
					resMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
					resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
					resMsg.setRspType(CommonConstant.CrmRspType.Success
							.getValue());
					resMsg.setBody(resBody);
					
					logger.debug("CrmTransQueryAction execute(Object) - end");
					return resMsg;
				}
			} else {
				
				logger.warn("移动渠道交易结果查询,落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{bankID,reqMsg.getTransIDO(),transIDH,
						reqMsg.getMsgSender(),idValue});
				log.warn("移动渠道交易结果查询,落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{bankID,reqMsg.getTransIDO(),transIDH,
						reqMsg.getMsgSender(),idValue});
				
				resBody.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A35.getDesc() + "接收方"
						+ bankID + "交易权限关闭");

				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.Success
						.getValue());
				txnLog.setChlSubRspCode(resBody.getRspCode());
				txnLog.setChlSubRspDesc(resBody.getRspInfo()+checkFlag);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				resMsg.setRspCode(txnLog.getChlRspCode());
				resMsg.setRspDesc(txnLog.getChlRspDesc());
				resMsg.setRspType(CommonConstant.CrmRspType.Success.getValue());
				resMsg.setBody(resBody);

				logger.debug("CrmTransQueryAction execute(Object) - end");
				return resMsg;
			}

		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			logger.error("移动渠道交易结果查询,intTxnSeq:{},运行异常:{}" , new Object[]{transIDH,errCode});
			log.error("移动渠道交易结果查询,运行异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
					new Object[]{msgVo.getTransIDO(),msgVo.getTransIDO(),transIDH,
					msgVo.getMsgSender()});
			logger.error("移动渠道交易结果查询,运行异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			resBody.setRspCode(errCode);
			resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));

			resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			resMsg.setRspType(txnLog.getChlRspType());
			resMsg.setBody(resBody);
			logger.debug("CrmTransQueryAction execute(Object) - end");
			return resMsg;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			log.error("移动渠道交易结果查询,系统异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}",
					new Object[]{msgVo.getTransIDO(),msgVo.getTransIDO(),transIDH,
					msgVo.getMsgSender()});
			logger.error("移动渠道交易结果查询,intTxnSeq:{},业务异常:{}" , new Object[]{transIDH,errCode}); 
			logger.error("移动渠道交易结果查询,业务异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			resBody.setRspCode(errCode);
			resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));

			resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			resMsg.setRspType(txnLog.getChlRspCode());
			resMsg.setBody(resBody);
			logger.debug("CrmTransQueryAction execute(Object) - end");
			return resMsg;
		} catch (Exception e) {
			log.error("移动渠道交易结果查询,未知异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}",
					new Object[]{msgVo.getTransIDO(),msgVo.getTransIDO(),transIDH,
					msgVo.getMsgSender()});
			logger.error("移动渠道交易结果查询,intTxnSeq:{},未知异常!" , new Object[]{transIDH});
			logger.error("移动渠道交易结果查询,未知异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			String errCode = RspCodeConstant.Crm.CRM_5A06.getValue();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(e.getMessage());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			resBody.setRspCode(errCode);
			
			//注释掉输出到应答报文的错误信息(该信息可能包含SQL异常) 20131213 modify by weiyi
//			String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_230?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_230);
//			resBody.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc()+":"+errDesc);
			
			resBody.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc());

			resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			resMsg.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			resMsg.setRspType(txnLog.getChlRspCode());
			resMsg.setBody(resBody);
			logger.debug("CrmTransQueryAction execute(Object) - end");
			return resMsg;
		}

	}

	/**
	 * 向银行发起交易结果查询
	 * 
	 * @param transIDH
	 *            upay作为发起方时的流水号
	 * @param forwardOrg
	 *            银行机构代码
	 * @param transLog
	 *            原交易信息
	 * @param reqBody
	 *            请求信息报文体
	 * @param txnLog
	 *            当前交易流水信息
	 * @param upayCsysTxnLogService
	 *            log service
	 * @return
	 * @throws AppBizException
	 */
	private BankMsgVo bankTransQuery(String transIDH, String forwardOrg,
			UpayCsysTxnLog transLog, UpayCsysTxnLogHis hisStlTransLog, CrmTransQueryReqVo reqBody,
			UpayCsysTxnLog txnLog, IUpayCsysTxnLogService upayCsysTxnLogService)
			throws AppBizException {
		/** 查询银行支付结果 */
		BankMsgVo queryMsg = new BankMsgVo();
		BankMsgVo queryRtMsg = null;
		BankTransQueryResVo queryRtBody = new BankTransQueryResVo();
		queryMsg.setActivityCode(CommonConstant.BankTrans.Bank05.getValue());
		queryMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
		queryMsg.setReqDate(DateUtil.getDateyyyyMMdd());
		queryMsg.setReqTransID(transIDH);
		queryMsg.setReqDateTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		queryMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
		queryMsg.setRcvSys(forwardOrg);
		queryMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());// 请求中没有，银行是必填
		// 签名内容
//		queryMsg.setSignFlag(null);
//		queryMsg.setCerID(null);
//		queryMsg.setSignValue(null);
		BankTransQueryReqVo queryBody = new BankTransQueryReqVo();
		queryBody.setOriReqDate(transLog != null ? transLog.getIntTxnDate() : hisStlTransLog.getIntTxnDate());
		queryBody.setOriReqSys(ExcConstant.BANK_REQ_SYS);
		queryBody.setOriReqTransID(transLog != null ? transLog.getIntTxnSeq() : hisStlTransLog.getIntTxnSeq());
		queryBody.setOriActivityCode(transLog != null ? transLog.getOuterActivityCode() : hisStlTransLog.getOuterActivityCode());
		queryMsg.setBody(queryBody);
		logger.debug("移动渠道交易结果查询,start 银行交易结果查询");
		queryRtMsg = sendBankJmsMessage.sendMsg(queryMsg);
		txnLog.setSettleDate(queryRtMsg.getRcvDate());
		logger.debug("移动渠道交易结果查询,return 银行渠道交易结果查询");
		if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
				queryRtMsg.getRspCode())) {
			
			String bankId = transLog != null ? transLog.getBankId() : hisStlTransLog.getBankId();
			String idValue = transLog != null ? transLog.getIdValue() : hisStlTransLog.getIdValue();
			logger.warn("移动渠道交易结果查询,银行前置超时!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
					new Object[]{queryRtMsg.getRcvTransID(),transIDH,
					bankId,idValue,queryRtMsg.getRspCode(),queryRtMsg.getRspDesc()});
			log.warn("移动渠道交易结果查询,银行前置超时!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
					new Object[]{queryRtMsg.getRcvTransID(),transIDH,
					bankId,idValue,queryRtMsg.getRspCode(),queryRtMsg.getRspDesc()});
			
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			String errCode = queryRtMsg.getRspCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return null;
		}
		MsgHandle.unmarshaller(queryRtBody, (String) queryRtMsg.getBody());
		queryRtMsg.setBody(queryRtBody);

		txnLog.setOuterActivityCode(queryMsg.getActivityCode());

		txnLog.setOuterTransDt(queryMsg.getReqDate());
		txnLog.setOuterTransTm(queryMsg.getReqDateTime());
		txnLog.setOuterTransId(queryMsg.getReqTransID());
		txnLog.setOuterTranshDt(queryRtMsg.getRcvDate());
		txnLog.setOuterTranshTm(queryRtMsg.getRcvDateTime());
		txnLog.setOuterTranshId(queryRtMsg.getRcvTransID());
		txnLog.setOuterOprDt(queryMsg.getReqDate());
		txnLog.setOuterOprId(queryRtMsg.getRcvTransID());
		txnLog.setOuterRspCode(queryRtMsg.getRspCode());
		txnLog.setOuterRspDesc(queryRtMsg.getRspDesc());
		txnLog.setOuterSubRspCode(queryRtMsg.getRspCode());
		txnLog.setOuterSubRspDesc(queryRtMsg.getRspDesc());

		return queryRtMsg;
	}

	/**
	 * 查询crm充值结果
	 * 
	 * @param transIDH
	 *            平台作为发起方时的交易流水号
	 * @param transIDHTime
	 *            平台时间
	 * @param queryOrg
	 *            原交易充值省
	 * @param reqBody
	 *            请求报文体
	 * @param txnLog
	 *            当前交易记录
	 * @param transLog
	 *            原交易记录
	 * @param upayCsysTxnLogService
	 *            txnlog service
	 * @return
	 * @throws AppBizException
	 */
	private CrmMsgVo crmTransQuery(String transIDH, String transIDHTime,
			String queryOrg, CrmTransQueryReqVo reqBody, UpayCsysTxnLog txnLog,
			UpayCsysTxnLog transLog, UpayCsysTxnLogHis hisStlTransLog,
			IUpayCsysTxnLogService upayCsysTxnLogService)
			throws AppBizException {
		// 查询省充值结果
		/** 报文头 */
		CrmMsgVo queryMsg = new CrmMsgVo();
		CrmMsgVo queryRtMsg = new CrmMsgVo();
		/** 报文体 */
		CrmTransQueryReqVo queryBody = new CrmTransQueryReqVo();
		CrmTransQueryResVo queryRtBody = new CrmTransQueryResVo();
		queryMsg.setVersion(ExcConstant.CRM_VERSION);
		queryMsg.setTestFlag(testFlag);
		queryMsg.setBIPCode(CommonConstant.Bip.Bis16.getValue());
		queryMsg.setActivityCode(CommonConstant.CrmTrans.Crm09.getValue());
		queryMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
		queryMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
		queryMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
		queryMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
		queryMsg.setRouteValue(transLog != null ? transLog.getIdValue() : hisStlTransLog.getIdValue());
		queryMsg.setSessionID(transIDH); // 待确认
		queryMsg.setTransIDO(transIDH);
		queryMsg.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
		queryMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
		queryMsg.setMsgReceiver(queryOrg);//

//		queryBody.setOriActionDate(reqBody.getOriActionDate());
		queryBody.setOriActionDate(transLog != null ? transLog.getIntTxnDate() : hisStlTransLog.getIntTxnDate());
		queryBody.setOriReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
		queryBody.setOriTransactionID(transLog != null ? transLog.getRcvOprId() : hisStlTransLog.getRcvOprId());
		queryBody.setOriActivityCode(reqBody.getOriActivityCode());
		queryMsg.setBody(queryBody);

		logger.debug("移动渠道交易结果查询,start 向省发起充值结果查询");
		queryRtMsg = sendCrmJmsMessage.sendMsg(queryMsg);
		logger.debug("移动渠道交易结果查询,end   向省发起充值结果查询");
		if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
				queryRtMsg.getRspCode())) {
			
			String bankId = transLog != null ? transLog.getBankId() : hisStlTransLog.getBankId();
			String idValue = transLog != null ? transLog.getIdValue() : hisStlTransLog.getIdValue();
			logger.warn("移动渠道交易结果查询,CRM前置超时!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
					new Object[]{queryRtMsg.getTransIDH(),transIDH,
					bankId,idValue,queryRtMsg.getRspCode(),queryRtMsg.getRspDesc()});
			log.warn("移动渠道交易结果查询,CRM前置超时!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
					new Object[]{queryRtMsg.getTransIDH(),transIDH,
					bankId,idValue,queryRtMsg.getRspCode(),queryRtMsg.getRspDesc()});
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			String errCode = queryRtMsg.getRspCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr .getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return null;
		}
		MsgHandle.unmarshaller(queryRtBody, (String) queryRtMsg.getBody());
		queryRtMsg.setBody(queryRtBody);

		txnLog.setRcvActivityCode(queryMsg.getActivityCode());
		txnLog.setRcvRouteType(queryMsg.getRouteType());
		txnLog.setRcvRouteVal(queryMsg.getRouteValue());
		txnLog.setRcvVersion(queryMsg.getVersion());

		txnLog.setRcvTransDt(StrUtil.subString(queryMsg.getTransIDOTime(), 0, 8));
		txnLog.setRcvTransTm(queryMsg.getTransIDOTime());
		txnLog.setRcvTransId(queryMsg.getTransIDO());
		txnLog.setRcvTranshDt(StrUtil.subString(queryRtMsg.getTransIDHTime(),0, 8));
		txnLog.setRcvTranshTm(queryRtMsg.getTransIDHTime());
		txnLog.setRcvTranshId(queryRtMsg.getTransIDH());
		txnLog.setRcvOprDt(StrUtil.subString(queryMsg.getTransIDOTime(), 0, 8));
		
		//平台发给省的报文如果有TransactionID字段，那么RcvOprId值填TransactionID，
		//如果报文中没有TransactionID，那么RcvOprId不用填 UR单：UPAY_DT-243
		txnLog.setRcvOprId(transLog != null ? transLog.getRcvOprId() : hisStlTransLog.getRcvOprId());
//		txnLog.setRcvOprId(UUIDGenerator.generateUUID());


		return queryRtMsg;
	}

	/**
	 * 初始化交易日志
	 * 
	 * @param txnLog
	 * @param seqId
	 * @param transIDH
	 * @param intTxnDate
	 * @param transIDHTime
	 * @param reqMsg
	 * @param transCode
	 * @param resMsg
	 * @param reqBody
	 */
	private void initLog(UpayCsysTxnLog txnLog, Long seqId, String transIDH,
			String intTxnDate, String transIDHTime, CrmMsgVo reqMsg,
			UpayCsysTransCode transCode, CrmMsgVo resMsg,
			CrmTransQueryReqVo reqBody) {
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
		txnLog.setReqTransDt(StrUtil.subString(reqMsg.getTransIDOTime(), 0, 8));
		txnLog.setReqVersion(reqMsg.getVersion());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReqBipCode(reqMsg.getBIPCode());
		txnLog.setReqActivityCode(reqMsg.getActivityCode());
		txnLog.setReqDomain(reqMsg.getMsgSender());
		txnLog.setReqRouteType(reqMsg.getRouteType());
		txnLog.setReqRouteVal(reqMsg.getRouteValue());
		txnLog.setReqSessionId(reqMsg.getSessionID());

		txnLog.setReqTransId(reqMsg.getTransIDO());
		txnLog.setReqTransTm(reqMsg.getTransIDOTime());
		txnLog.setReqTransDt(StrUtil.subString(reqMsg.getTransIDOTime(), 0, 8));
		txnLog.setReqTranshDt(StrUtil.subString(resMsg.getTransIDHTime(), 0, 8));
		txnLog.setReqTranshId(resMsg.getTransIDH());
		txnLog.setReqTranshTm(resMsg.getTransIDHTime());
		txnLog.setReqOprId(UUIDGenerator.generateUUID());
		txnLog.setReqOprDt(StrUtil.subString(reqMsg.getTransIDOTime(), 0, 8));
		txnLog.setReqOprTm(reqMsg.getTransIDOTime());

//		txnLog.setOriOrgId(reqBody.getOriReqSys());
		txnLog.setOriReqDate(reqBody.getOriActionDate());
		txnLog.setOriOprTransId(reqBody.getOriTransactionID());

		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
	}

	@SuppressWarnings("unused")
	private CrmMsgVo bak() {
		return null;
		/*
		*//** 查询银行支付结果 */
		/*
		*//** 报文头 */
		/*
		 * BankMsgVo forwardMsg = new BankMsgVo();
		 * forwardMsg.setActivityCode(CommonConstant
		 * .BankTrans.Bank05.getValue());
		 * forwardMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
		 * forwardMsg.setReqDate(DateUtil.getDateyyyyMMdd());
		 * forwardMsg.setReqTransID(transIDH);
		 * forwardMsg.setReqDateTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		 * forwardMsg
		 * .setActionCode(CommonConstant.ActionCode.Requset.getValue());
		 * forwardMsg.setRcvSys(bankID);
		 * forwardMsg.setReqChannel(CommonConstant.
		 * CnlType.CmccOwn.getValue());// 请求中没有，银行是必填 // 签名内容
		 * forwardMsg.setSignFlag(null); forwardMsg.setCerID(null);
		 * forwardMsg.setSignValue(null); BankTransQueryReqVo forwardBody = new
		 * BankTransQueryReqVo();
		 * forwardBody.setActionDate(transLog.getIntTxnDate());
		 * forwardBody.setOriReqSys(ExcConstant.BANK_REQ_SYS);
		 * forwardBody.setTransactionID(transLog.getIntTxnSeq());
		 * forwardBody.setActivityCode(reqBody.getActivityCode()); if
		 * (checkFlag) {
		 * 
		 * txnLog.setRcvActivityCode(forwardMsg.getActivityCode());
		 * txnLog.setRcvDomain(forwardMsg.getRcvSys());
		 * txnLog.setRcvTransId(forwardMsg.getReqTransID());
		 * txnLog.setRcvTransTm(forwardMsg.getReqDateTime());
		 * txnLog.setRcvTranshDt(forwardMsg.getReqDate());
		 * txnLog.setRcvOprDt(forwardBody.getActionDate());
		 * txnLog.setRcvOprId(forwardBody.getTransactionID());
		 * 
		 * forwardMsg.setBody(forwardBody); logger.info( "start 银行交易结果查询" );
		 * forwardMsg = sendBankJmsMessage.sendMsg(forwardMsg); logger.info(
		 * "return 银行渠道交易结果查询" );
		 * 
		 * 
		 * BankTransQueryResVo forwarRtBody = new BankTransQueryResVo();
		 * MsgHandle.unmarshaller(forwarRtBody, forwardMsg.getBody()
		 * .toString());
		 * 
		 * // resBody.setActionDate(reqBody.getActionDate()); //
		 * resBody.setTransactionID(reqBody.getTransactionID()); //
		 * resBody.setRspCode(forwarRtBody.getRspCode()); //
		 * resBody.setRspInfo(forwarRtBody.getRspInfo()); // //
		 * resMsg.setRspCode(forwardMsg.getRspCode()); //
		 * resMsg.setRspDesc(forwardMsg.getRspDesc());
		 * 
		 * if ("020A00".equals(forwardMsg.getRspCode())) {
		 * logger.info("success 原交易支付成功"); // 查询省充值结果
		 *//** 报文头 */
		/*
		 * CrmMsgVo queryMsg = new CrmMsgVo(); CrmMsgVo queryRtMsg = new
		 * CrmMsgVo(); String queryOrg = transLog.getRcvDomain();
		 * queryMsg.setTransCode(transCode);
		 * queryMsg.setVersion(ExcConstant.CRM_VERSION);
		 * queryMsg.setTestFlag(testFlag);
		 * queryMsg.setBIPCode(CommonConstant.Bis.Bis18.getValue());
		 * queryMsg.setActivityCode(CommonConstant.CrmTrans.Crm09 .getValue());
		 * queryMsg.setActionCode(CommonConstant.ActionCode.Requset
		 * .getValue());
		 * queryMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
		 * queryMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
		 * queryMsg.setRouteType(CommonConstant.routeType.RouteProvince
		 * .getValue()); queryMsg.setRouteValue(queryOrg);
		 * queryMsg.setSessionID(transIDH); // 待确认
		 * queryMsg.setTransIDO(transIDH);
		 * queryMsg.setTransIDOTime(transIDHTime);
		 * queryMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC .getValue());
		 * queryMsg.setMsgReceiver(queryOrg);//
		 *//** 报文体 */
		/*
		 * CrmTransQueryReqVo queryBody = new CrmTransQueryReqVo();
		 * CrmTransQueryResVo queryRtBody = new CrmTransQueryResVo();
		 * queryBody.setActionDate(reqBody.getActionDate());
		 * queryBody.setOriReqSys(transLog.getRcvDomain());
		 * queryBody.setTransactionID(transLog.getRcvOprId());
		 * queryBody.setActivityCode(reqBody.getActivityCode());
		 * queryMsg.setBody(queryBody);
		 * 
		 * if (orgStatusCheck(queryOrg)) { queryRtMsg =
		 * sendCrmJmsMessage.sendMsg(queryMsg);
		 * MsgHandle.unmarshaller(queryRtBody, (String) queryRtMsg.getBody());
		 * 
		 * txnLog.setRcvTransId(queryMsg.getTransIDO());
		 * txnLog.setRcvTransTm(queryMsg.getTransIDOTime());
		 * txnLog.setRcvTransDt(StrUtil.subString( queryMsg.getTransIDOTime(),
		 * 0, 8)); txnLog.setRcvTranshId(queryRtMsg.getTransIDH());
		 * txnLog.setRcvTranshTm(queryRtMsg.getTransIDHTime());
		 * txnLog.setRcvTranshDt(StrUtil.subString(
		 * queryRtMsg.getTransIDHTime(), 0, 8));
		 * txnLog.setRcvActivityCode(queryMsg.getActivityCode());
		 * txnLog.setRcvDomain(queryMsg.getMsgReceiver());
		 * 
		 * txnLog.setOuterTransId(forwardMsg.getReqTransID());
		 * txnLog.setOuterTransTm(forwardMsg.getReqDateTime());
		 * txnLog.setOuterTransDt(forwardMsg.getReqDate());
		 * txnLog.setOuterTranshId(forwardMsg.getRcvTransID());
		 * txnLog.setOuterTranshTm(forwardMsg.getRcvDateTime());
		 * txnLog.setOuterTranshDt(forwardMsg.getRcvDate());
		 * txnLog.setOuterActivityCode(forwardMsg.getActivityCode());
		 * txnLog.setOuterDomain(forwardMsg.getRcvSys());
		 * 
		 * txnLog.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		 * 
		 * if (MessageHandler.getCrmErrCode("0000").equals(
		 * queryMsg.getRspCode()) &&
		 * MessageHandler.getCrmErrCode("0000").equals(
		 * queryRtBody.getRspCode())) { logger.info(" 原交易充值成功"); if
		 * (CommonConstant.TxnStatus.TxnSuccess.getValue()
		 * .equals(transLog.getStatus())) { logger.info(" 平台记录原交易成功");
		 * 
		 * resMsg.setRspCode(MessageHandler .getCrmErrCode("0000"));
		 * resMsg.setRspDesc(MessageHandler .getCrmErrMsg("0000"));
		 * resMsg.setRspType(CommonConstant.CrmRspType.Success .getValue());
		 * resBody.setRspCode(MessageHandler .getCrmErrCode("0000"));
		 * resBody.setRspInfo(MessageHandler .getCrmErrMsg("0000"));
		 * resMsg.setBody(resBody);
		 * 
		 * txnLog.setChlRspCode(resMsg.getRspCode());
		 * txnLog.setChlRspDesc(resMsg.getRspDesc());
		 * txnLog.setChlRspType(resMsg.getRspType());
		 * txnLog.setChlSubRspCode(resBody.getRspCode());
		 * txnLog.setChlSubRspDesc(resBody.getRspInfo());
		 * upayCsysTxnLogService.modify(txnLog);
		 * 
		 * return resMsg; } else { logger.info(" 平台记录原交易失败"); txnLog attributes
		 * 
		 * String errCode = transLog.getChlRspCode();
		 * resBody.setRspCode(MessageHandler .getCrmErrCode(errCode));
		 * resBody.setRspInfo(MessageHandler .getCrmErrMsg(errCode));
		 * resMsg.setRspCode(MessageHandler .getCrmErrCode("2998"));
		 * resMsg.setRspDesc(MessageHandler .getCrmErrMsg("2998"));
		 * resMsg.setRspType(CommonConstant.CrmRspType.BusErr .getValue());
		 * resMsg.setBody(resBody);
		 * 
		 * txnLog.setChlRspCode(resMsg.getRspCode());
		 * txnLog.setChlRspDesc(resMsg.getRspDesc());
		 * txnLog.setChlRspType(resMsg.getRspType());
		 * txnLog.setChlSubRspCode(resBody.getRspCode());
		 * txnLog.setChlSubRspDesc(resBody.getRspInfo());
		 * upayCsysTxnLogService.modify(txnLog);
		 * 
		 * return resMsg; }
		 * 
		 * } else { logger.info(" 原交易充值失败"); // attributes String errCode =
		 * CrmErrorCodeCache .getCrmErrCode(queryRtBody.getRspCode());
		 * resBody.setRspCode(MessageHandler .getCrmErrCode(errCode));
		 * resBody.setRspInfo(MessageHandler.getCrmErrMsg(errCode));
		 * resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
		 * resMsg.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
		 * resMsg.setRspType(CommonConstant.CrmRspType.BusErr .getValue());
		 * resMsg.setBody(resBody);
		 * 
		 * txnLog.setChlRspCode(resMsg.getRspCode());
		 * txnLog.setChlRspDesc(resMsg.getRspDesc());
		 * txnLog.setChlRspType(resMsg.getRspType());
		 * txnLog.setChlSubRspCode(resBody.getRspCode());
		 * txnLog.setChlSubRspDesc(resBody.getRspInfo());
		 * upayCsysTxnLogService.modify(txnLog); return resMsg; } } else {
		 * logger.info( "fail 查询原交易充值结果，crm接收方" );
		 * resBody.setRspCode(MessageHandler.getCrmErrCode("2A05"));
		 * resBody.setRspInfo(MessageHandler.getCrmErrMsg("2A05") + "接收方" +
		 * queryOrg + "交易权限关闭");
		 * 
		 * txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
		 * txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
		 * txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr .getValue());
		 * txnLog.setChlSubRspCode(resBody.getRspCode());
		 * txnLog.setChlSubRspDesc(resBody.getRspInfo());
		 * txnLog.setStatus(CommonConstant.TxnStatus.TxnFail .getValue());
		 * txnLog.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		 * upayCsysTxnLogService.modify(txnLog);
		 * 
		 * resMsg.setRspCode(txnLog.getChlRspCode());
		 * resMsg.setRspDesc(txnLog.getChlRspDesc());
		 * resMsg.setRspType(CommonConstant.CrmRspType.BusErr .getValue());
		 * resMsg.setBody(resBody);
		 * 
		 * return resMsg; }
		 * 
		 * } else { logger.info("fail 原交易支付失败 ");
		 * 
		 * String errCode = CrmErrorCodeCache.getCrmErrCode(forwardMsg
		 * .getRspCode());
		 * resBody.setRspCode(MessageHandler.getCrmErrCode(errCode));
		 * resBody.setRspInfo(MessageHandler.getCrmErrMsg(errCode));
		 * resMsg.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
		 * resMsg.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
		 * resMsg.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
		 * resMsg.setBody(resBody);
		 * 
		 * txnLog.setChlRspCode(resMsg.getRspCode());
		 * txnLog.setChlRspDesc(resMsg.getRspDesc());
		 * txnLog.setChlRspType(resMsg.getRspType());
		 * txnLog.setChlSubRspCode(resBody.getRspCode());
		 * txnLog.setChlSubRspDesc(resBody.getRspInfo());
		 * upayCsysTxnLogService.modify(txnLog);
		 * 
		 * return resMsg; }
		 * 
		 * } else { logger.info( "fail 银行渠道交易结果查询 接收方机构状态异常" );
		 * resBody.setRspCode(MessageHandler.getCrmErrCode("2A05"));
		 * resBody.setRspInfo(MessageHandler.getCrmErrMsg("2A05") + "接收方" +
		 * bankID + "交易权限关闭");
		 * 
		 * txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
		 * txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
		 * txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
		 * txnLog.setChlSubRspCode(resBody.getRspCode());
		 * txnLog.setChlSubRspDesc(resBody.getRspInfo());
		 * txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
		 * txnLog.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		 * upayCsysTxnLogService.modify(txnLog);
		 * 
		 * resMsg.setRspCode(txnLog.getChlRspCode());
		 * resMsg.setRspDesc(txnLog.getChlRspDesc());
		 * resMsg.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
		 * resMsg.setBody(resBody);
		 * 
		 * return resMsg; }
		 */
	}
}
