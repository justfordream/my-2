package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogHisService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
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
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 银行交易结果查询接口
 * 
 * @author zeng.j
 * 
 */
@Controller("bankTransQueryAction")
@Scope("prototype")
public class BankTransQueryAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {

	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	
	@Autowired
	private IUpayCsysTxnLogHisService upayCsysTxnLogHisService;


	@Override
	public BankMsgVo execute(BankMsgVo msgVo) throws AppBizException {
		logger.debug("BankTransQueryAction execute(Object) - start");
		// 请求报文
		BankMsgVo reqMsg = msgVo;
		BankTransQueryReqVo reqBody = new BankTransQueryReqVo();
		BankTransQueryResVo resBody = new BankTransQueryResVo();
		String transIDH = msgVo.getTxnSeq();
		BankMsgVo resMsg = reqMsg;
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		try {
			String intTxnDate = msgVo.getTxnDate();// 从数据库获取
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			reqMsg.setBody(reqBody);
			resBody.setOriRcvDate(reqBody.getOriReqDate());
			resBody.setOriRcvTransID(reqBody.getOriReqTransID());
			// 响应报文
			/* 作为银行的接收方交易流水，同时作为upss向crm发起交易的发起方交易流水 */
			
			/* 作为银行的接收方交易时间，同时作为upss向crm发起交易的发起方交易时间 */
//			String transIDHTimeyyyyMMddHHmmssSSS = DateUtil
//					.getDateyyyyMMddHHmmssSSS();
			String transIDHTime = msgVo.getTxnTime();
			String transactionId = transIDH;
			Long seqId = msgVo.getSeqId();
			
			resMsg.setRcvDate(intTxnDate);
			resMsg.setRcvDateTime(transIDHTime);
			resMsg.setRcvTransID(transIDH);
			resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqDomain", reqMsg.getReqSys());
			param.put("reqTransId", reqMsg.getReqTransID());
			UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService.findObj(param);
			if (upayCsysTxnLog != null) {
				logger.warn("操作流水重复!Bank流水:{},UPAY流水:{},发起银行:{},",
						new Object[]{reqMsg.getReqTransID(),transIDH,reqMsg.getReqSys()});
				log.warn("操作流水重复!Bank流水:{},UPAY流水:{},发起银行:{},",
						new Object[]{reqMsg.getReqTransID(),transIDH,reqMsg.getReqSys()});
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				resMsg.setBody(resBody);
				return resMsg;
			}

			/** 交易代码 */
			UpayCsysTransCode transCode = reqMsg.getTransCode();
			initLog(txnLog, seqId, transIDH, intTxnDate,
					transIDHTime, reqMsg, transCode, resMsg, reqBody);
			txnLog.setSettleDate(reqMsg.getReqDate());
			upayCsysTxnLogService.add(txnLog);

			/* 验证消息 */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				log.warn("银行交易结果查询接口!报文体校验失败:{},内部交易流水:{},发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys() });
				logger.warn("银行交易结果查询接口!报文体校验失败:{},内部交易流水:{},发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys() });
				
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_014A04.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc()
						+ checkrtn);
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A04.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc()
						+ checkrtn);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				resMsg.setRspCode(RspCodeConstant.Bank.BANK_014A04.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc());
				resMsg.setBody(resBody);
				logger.debug("BankTransQueryAction execute(Object) - end");
				return resMsg;
			}
			// 查询原交易及处理
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reqTransId", reqBody.getOriReqTransID());
			params.put("reqDomain", reqBody.getOriReqSys());
			params.put("reqTransDt", reqBody.getOriReqDate());
			UpayCsysTxnLog transLog = upayCsysTxnLogService.findObj(params);
			UpayCsysTxnLogHis hisStlTransLog = null;
			if (null == transLog) {
				// 前线库查不到去后线库查
				hisStlTransLog = upayCsysTxnLogHisService.findHisStlObj(params);
				// 如果后线库也查不到，则报原交易不存在
				if(hisStlTransLog == null) {
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
//					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
//						.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					resMsg.setBody(resBody);
					logger.debug("BankTransQueryAction execute(Object) - end");
					return resMsg;
				}
			}

			String forwardOrg = (transLog != null ? transLog.getRcvDomain() : hisStlTransLog.getRcvDomain());// 转发方机构代码
			if(StringUtils.isBlank(forwardOrg)){
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_012A99.getDesc());
				if(CommonConstant.TxnStatus.InitStatus.getValue().equalsIgnoreCase(transLog != null ? transLog.getStatus() : hisStlTransLog.getStatus())){
					log.warn("银行交易结果查询接口!原交易未发送到省充值:{},内部交易流水:{},发起方:{}",
							new Object[] { reqBody.getOriReqTransID(), transIDH,reqMsg.getReqSys() });
					logger.warn("银行交易结果查询接口!原交易未发送到省充值:{},内部交易流水:{},发起方:{}",
							new Object[] { reqBody.getOriReqTransID(), transIDH,reqMsg.getReqSys() });
					
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A99.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A08.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A08.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					
					resBody.setOriRspCode(RspCodeConstant.Bank.BANK_014A08.getValue());
					resBody.setOriRspDesc(RspCodeConstant.Bank.BANK_014A08.getDesc());
					resMsg.setBody(resBody);
					logger.debug("BankTransQueryAction execute(Object) - end");
					return resMsg;
				}else if(CommonConstant.TxnStatus.TxnFail.getValue().equalsIgnoreCase(transLog != null ? transLog.getStatus() : hisStlTransLog.getStatus())){
					log.warn("银行交易结果查询接口!原交易失败:{},内部交易流水:{},发起方:{}",
							new Object[] { reqBody.getOriReqTransID(), transIDH,reqMsg.getReqSys() });
					logger.warn("银行交易结果查询接口!原交易失败:{},内部交易流水:{},发起方:{}",
							new Object[] { reqBody.getOriReqTransID(), transIDH,reqMsg.getReqSys() });
					
					String rspCode = transLog != null ? transLog.getChlSubRspCode() : hisStlTransLog.getChlSubRspCode();
					String rspDesc = transLog != null ? transLog.getChlSubRspDesc() : hisStlTransLog.getChlSubRspDesc();
					
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A99.getDesc());
					txnLog.setChlSubRspCode(rspCode);
					txnLog.setChlSubRspDesc(rspDesc);
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					
					resBody.setOriRspCode(rspCode);
					resBody.setOriRspDesc(rspDesc);
					resMsg.setBody(resBody);
					logger.debug("BankTransQueryAction execute(Object) - end");
					return resMsg;
				}
			}
			
//			boolean checkFlag = true;// 转发机构交易权限标识

			/** 报文头 */
			CrmMsgVo forwardMsg = new CrmMsgVo();
			forwardMsg.setTransCode(transCode);
			forwardMsg.setVersion(ExcConstant.CRM_VERSION);
			forwardMsg.setTestFlag(testFlag);
			forwardMsg.setBIPCode(CommonConstant.Bip.Bis18.getValue());
			forwardMsg
					.setActivityCode(CommonConstant.CrmTrans.Crm09.getValue());
			forwardMsg.setActionCode(CommonConstant.ActionCode.Requset
					.getValue());
			forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			
//			forwardMsg.setRouteType(CommonConstant.RouteType.RouteProvince
//					.getValue());
//			forwardMsg.setRouteValue(forwardOrg);
			forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone
					.getValue());
			
			String idValue = transLog != null ? transLog.getIdValue() : hisStlTransLog.getIdValue();
			forwardMsg.setRouteValue(idValue);
			
			forwardMsg.setSessionID(transactionId); // 待确认
			forwardMsg.setTransIDO(transIDH);
			forwardMsg.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
			forwardMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
			forwardMsg.setMsgReceiver(forwardOrg);//

			/** 报文体 */
			CrmTransQueryReqVo forwardBody = new CrmTransQueryReqVo();
			forwardBody.setOriActionDate(transLog != null ? transLog.getIntTxnDate() : hisStlTransLog.getIntTxnDate());
//			forwardBody.setOriReqSys(transLog.getRcvDomain());
			forwardBody.setOriReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
			forwardBody.setOriTransactionID(transLog != null ? transLog.getRcvOprId() : hisStlTransLog.getRcvOprId());
//			forwardBody.setOriActivityCode(transLog.getReqActivityCode());
			forwardBody.setOriActivityCode(CommonConstant.CrmTrans.Crm07.getValue());
			forwardMsg.setBody(forwardBody);
			
//			String checkFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode());
			// 查询该交易的号码段属于移动还是联通电信的。
			ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(idValue);
			String checkFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode(), 
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			checkFlag = isO2OTransOn(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode());
//			checkFlag = orgStatusCheck(forwardOrg);
			if (checkFlag == null) {
				txnLog.setRcvTransId(transIDH);
				txnLog.setRcvTransTm(transIDHTime);
				txnLog.setRcvTransDt(intTxnDate);
				logger.debug("sendCrmJmsMessage.sendMsg(forwardMsg) - start,intTxnSeq:{}",new Object[]{transIDH});
				forwardMsg = sendCrmJmsMessage.sendMsg(forwardMsg);
				logger.debug("sendCrmJmsMessage.sendMsg(forwardMsg) - end,intTxnSeq:{}",new Object[]{transIDH});
				
				//平台发给省的报文如果有TransactionID字段，那么RcvOprId值填TransactionID，
				//如果报文中没有TransactionID，那么RcvOprId不用填 UR单：UPAY_DT-243
				txnLog.setRcvOprId(transLog != null ? transLog.getRcvOprId() : hisStlTransLog.getRcvOprId());
				//txnLog.setRcvOprId(UUIDGenerator.generateUUID());
				
				txnLog.setRcvOprDt(StrUtil.subString(forwardMsg.getTransIDHTime(), 0, 8));
				txnLog.setRcvOprTm(forwardMsg.getTransIDHTime());
				CrmTransQueryResVo forwardRtBody = new CrmTransQueryResVo();
				if (forwardMsg.getBody() == null
						|| "".equals(forwardMsg.getBody().toString())
						|| "null".equalsIgnoreCase(forwardMsg.getBody()
								.toString())) {
					log.warn("银行交易结果查询接口!CRM返回报文体为空!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
							reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),idValue });
					logger.warn("银行交易结果查询接口!CRM返回报文体为空!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
							reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),idValue });
					
					String errCode = RspCodeConstant.Bank.BANK_015A06.getValue();
					errCode = BankErrorCodeCache.getBankErrCode(errCode);
					//txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr .getValue());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					txnLog.setChlRspCode(errCode);
					txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
					txnLog.setChlSubRspCode(errCode);
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					resBody.setOriRspCode(errCode);
					resBody.setOriRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
					resMsg.setRspCode(errCode);
					resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
					resMsg.setBody(resBody);
					logger.debug("BankTransQueryAction execute(Object) - end");
					return resMsg;
				}
				if(RspCodeConstant.Upay.UPAY_U99998.getValue().equals(forwardMsg.getRspCode())){
					log.warn("银行交易结果查询接口!CRM前置响应超时!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
									reqMsg.getReqSys(),forwardMsg.getMsgReceiver() ,idValue});
					logger.warn("银行交易结果查询接口!CRM前置响应超时!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
									reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),idValue });
					
					String errCode =forwardMsg.getRspCode();
					errCode = BankErrorCodeCache.getBankErrCode(errCode);
					//txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr .getValue());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					txnLog.setChlRspCode(errCode);
					txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
					txnLog.setChlSubRspCode(errCode);
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					resBody.setOriRspCode(errCode);
					resBody.setOriRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
					resMsg.setRspCode(errCode);
					resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
					resMsg.setBody(resBody);
					logger.debug("BankTransQueryAction execute(Object) - end");
					return resMsg;
				}
				MsgHandle.unmarshaller(forwardRtBody, forwardMsg.getBody()
						.toString());

//				resBody.setOriRcvDate(forwardRtBody.getOriActionDate());
//				resBody.setOriRcvTransID(forwardRtBody.getOriTransactionID());
				resBody.setOriRspCode(BankErrorCodeCache.getBankErrCode(forwardRtBody.getRspCode()));
				resBody.setOriRspDesc(forwardRtBody.getRspInfo());

				// 更新交易流水
				txnLog.setRcvActivityCode(forwardMsg.getActivityCode());
				txnLog.setRcvBipCode(forwardMsg.getBIPCode());
//				txnLog.setRcvDomain(forwardMsg.getHomeDomain());
				txnLog.setRcvDomain(forwardOrg);
				txnLog.setRcvRouteType(forwardMsg.getRouteType());
				txnLog.setRcvRouteVal(forwardMsg.getRouteValue());
				txnLog.setRcvSessionId(forwardMsg.getSessionID());//
				txnLog.setRcvRspCode(forwardMsg.getRspCode());
				txnLog.setRcvRspDesc(forwardMsg.getRspDesc());
				txnLog.setChlRspCode(resBody.getOriRspCode());
				txnLog.setChlRspDesc(resBody.getOriRspDesc());
				txnLog.setChlSubRspCode(resBody.getOriRspCode());
				txnLog.setChlSubRspDesc(resBody.getOriRspDesc());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());

				txnLog.setRcvTranshId(forwardMsg.getTransIDH());
				txnLog.setRcvTranshTm(forwardMsg.getTransIDHTime());
				txnLog.setRcvTranshDt(StrUtil.subString(
						forwardMsg.getTransIDHTime(), 0, 8));

				if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
						forwardMsg.getRspCode())
						&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
								forwardRtBody.getRspCode())) {
					log.succ("CRM响应成功!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),idValue});
					logger.info("CRM响应成功!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),idValue });
					
						txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
						txnLog.setRcvSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
						txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
						//txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
						//txnLog.setChlRspType(CommonConstant.CrmRspType.Success.getValue());
						txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
						txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
						txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());

						resBody.setOriRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
						resBody.setOriRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
						resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
						resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					

				} else {
					log.warn("银行交易结果查询接口!CRM响应失败!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),idValue});
					logger.warn("银行交易结果查询接口!CRM响应失败!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),idValue });
					
					String errName = BankErrorCodeCache.getBankErrCode(forwardRtBody.getRspCode());
					txnLog.setRcvSubRspCode(forwardRtBody.getRspCode());
					txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.getDescByValue(forwardRtBody.getRspCode()));
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					txnLog.setChlSubRspCode(errName);
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errName));
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());

					resBody.setOriRspCode(errName);
					resBody.setOriRspDesc(RspCodeConstant.Bank.getDescByValue(errName));
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				}
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				resMsg.setBody(resBody);
				logger.debug("BankTransQueryAction execute(Object) - end");
				return resMsg;
			} else {
				
				log.warn("银行交易结果查询接口!落地方机构状态异常:{},内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { forwardOrg, transIDH,
						reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),idValue });
				logger.warn("银行交易结果查询接口!落地方机构状态异常:{},内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { forwardOrg, transIDH,
						reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),idValue });
				resBody.setOriRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				resBody.setOriRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc()
						+ "接收方" + forwardOrg + "交易权限关闭");

//				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
//						.getValue());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				resMsg.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getValue());

				resMsg.setBody(resBody);
				logger.debug("BankTransQueryAction execute(Object) - end");
				return resMsg;
			}
		} catch (AppRTException e) {
			String errCode = e.getCode();
			log.error("银行交易结果查询接口!运行异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {RspCodeConstant.Bank.getDescByValue(errCode),
							transIDH, reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!运行异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { errCode,
							transIDH, reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!运行异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
//			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			resBody.setOriRspCode(errCode);
			resBody.setOriRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setBody(resBody);
			logger.debug("BankTransQueryAction execute(Object) - end");
			return resMsg;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.error("银行交易结果查询接口!业务异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {RspCodeConstant.Bank.getDescByValue(errCode),
							transIDH, reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!业务异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { errCode,
							transIDH, reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!业务异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
//			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			resBody.setOriRspCode(errCode);
			resBody.setOriRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setBody(resBody);
			logger.debug("BankTransQueryAction execute(Object) - end");
			return resMsg;
		} catch (Exception e) {
			String errCode = RspCodeConstant.Bank.BANK_015A06.getValue();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.error("银行交易结果查询接口!系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {transIDH, reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {transIDH, reqMsg.getReqSys() });
			logger.error("银行交易结果查询接口!系统异常:",e);
//			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode)+":"+e.getMessage());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode)+":"+e.getMessage());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			resBody.setOriRspCode(errCode);
			resBody.setOriRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setRspCode(errCode);
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
			resMsg.setBody(resBody);
			logger.debug("BankTransQueryAction execute(Object) - end");
			return resMsg;
		}
	}

	/**
	 * 初始化交易流水
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
			String intTxnDate, String transIDHTime, BankMsgVo reqMsg,
			UpayCsysTransCode transCode, BankMsgVo resMsg,
			BankTransQueryReqVo reqBody) {
		/** 交易流水 */
		txnLog.setSeqId(seqId);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setIntMqSeq(reqMsg.getMqSeq());
//		txnLog.setTxnCat(transCode.getTxnCat());
		txnLog.setBussType(transCode.getBussType());
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setBussChl(transCode.getBussChl());
		txnLog.setReqActivityCode(reqMsg.getActivityCode());
		txnLog.setReqTransId(reqMsg.getReqTransID());
		txnLog.setReqTransDt(reqMsg.getReqDate());
		txnLog.setReqTransTm(reqMsg.getReqDateTime());
		txnLog.setReqOprDt(reqMsg.getReqDate());
		txnLog.setReqOprId(reqMsg.getReqTransID());
		txnLog.setReqOprTm(reqMsg.getReqDateTime());			
		txnLog.setReqCnlType(reqMsg.getReqChannel());
		txnLog.setReqDomain(reqMsg.getReqSys());
		txnLog.setReqTranshDt(intTxnDate);
		txnLog.setReqTranshId(transIDH);
		txnLog.setReqTranshTm(transIDHTime);

		txnLog.setOriOrgId(reqBody.getOriReqSys());
		txnLog.setOriReqDate(reqBody.getOriReqDate());
		txnLog.setOriOprTransId(reqBody.getOriReqTransID());

		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
	}
}
