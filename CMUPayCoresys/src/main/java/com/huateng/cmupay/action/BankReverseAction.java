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
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmReverseBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankReverseMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmReverseMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmReverseMsgResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 银行渠道冲正交易
 * @author h_dm 
 * 		1 记录交易流水-请求 
 * 		2 验证冲正请求报文 
 * 		3 查询原交易并判断： 
 * 			3.1 如果原交易不存在,向银行系统返回该笔交易不存在 
 * 			3.2否则,将冲正请求转发至省CRM系统 
 *		4 将Crm成功响应的报文转发至银行
 *      只能冲正当天的
 */

@Controller("bankReverseAction")
@Scope("prototype")
public class BankReverseAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {
	@Autowired
	private CrmReverseBus crmReverseBus;
	
	@Override
	public BankMsgVo execute(BankMsgVo paramData) throws AppBizException {

		logger.debug("开始执行Bank冲正交易");
		BankMsgVo bankMsgVo = paramData;
		BankMsgVo msgVoRtn = bankMsgVo;
		BankReverseMsgReqVo reqMsg = new BankReverseMsgReqVo();
		UpayCsysTransCode transCode = bankMsgVo.getTransCode();
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		String transIDH = bankMsgVo.getTxnSeq();
		String transIDHTime = paramData.getTxnTime();
		String intTxnDate = bankMsgVo.getTxnDate();
		Long seqValue = bankMsgVo.getSeqId();
		UpayCsysTxnLog logg = new UpayCsysTxnLog();
		try {
			MsgHandle.unmarshaller(reqMsg, (String) bankMsgVo.getBody());
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqDomain", bankMsgVo.getReqSys());
			param.put("reqTransId", bankMsgVo.getReqTransID());
			UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService.findObj(param);
			if (upayCsysTxnLog != null) {
				logger.warn("操作流水重复!Bank流水:{},UPAY流水:{},发起银行:{},",
						new Object[]{bankMsgVo.getReqTransID(),transIDH,
						bankMsgVo.getReqSys()});
				log.warn("操作流水重复!Bank流水:{},UPAY流水:{},发起银行:{},",
						new Object[]{bankMsgVo.getReqTransID(),transIDH,
						bankMsgVo.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());
				return msgVoRtn;
			}

			logger.info("begin 添加Bank渠道冲正交易流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
					new Object[]{bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys()});

			initLog(txnLog, seqValue, transIDH, intTxnDate, transIDHTime, bankMsgVo, transCode, msgVoRtn, reqMsg);
	
			String validateMsg = this.validateModel(reqMsg);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.info("body validate success,intTxnSeq:{}",new Object[]{transIDH});
			} else {
				String codes = validateMsg.split(":")[0];
				logger.warn("报文格式错误:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{validateMsg, bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys()});
				log.warn("报文格式错误:{} ,Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{} ", 
						new Object[]{validateMsg, bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys()});
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvDateTime(transIDHTime );
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRspCode(codes);
				msgVoRtn.setRspDesc(validateMsg);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

				txnLog.setChlRspCode(codes);
				txnLog.setChlRspDesc(validateMsg);
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			if (!intTxnDate.equals(reqMsg.getOriReqDate()) || !bankMsgVo.getReqDate().equals(reqMsg.getOriReqDate())) {
				logger.warn("非当天交易不允许冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys()});
				log.warn("非当天交易不允许冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys()});
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A05.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A05.getDesc()+","+"报文头日期与报文体日期不一致");
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}	
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("reqDomain", reqMsg.getOriReqSys());// 发起方机构代码
			map.put("reqTransId", reqMsg.getOriReqTransID());// 发起方操作流水号
			map.put("reqTransDt", reqMsg.getOriReqDate());// 发起方交易请求的日期
			UpayCsysTxnLog upayLog = upayCsysTxnLogService.findObj(map);
			if (null == upayLog) {
				logger.warn("原交易不存在:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys()});
				log.warn("原交易不存在:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc()+",不需冲正");
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			logger.info("原交易存在:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
					new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys(),upayLog.getIdValue()});
			//log.warn("原交易存在:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
			//		new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys(),upayLog.getIdValue()});
			
			if(!CommonConstant.TxnStatus.TxnSuccess
					.getValue().equals(upayLog.getStatus())) {
				logger.warn("原交易失败，不需要冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys(),upayLog.getIdValue()});
				log.warn("原交易失败，不需要冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,bankMsgVo.getReqSys(),upayLog.getIdValue()});
			
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A04.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A04.getDesc()+",原交易失败，不需要冲正");
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
			
				return msgVoRtn;
			}

			
			String mainOrgId = upayLog.getRcvDomain();// 手机号码归属省
//			String mainOrgId = SysMapCache.getProvCd(upayLog.getIdValue()).getSysCd();// 手机号码归属省
			if(StringUtils.isBlank(mainOrgId)){
				logger.warn("原交易未发往CRM进行充值:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{mainOrgId, bankMsgVo.getReqTransID(), transIDH,
						bankMsgVo.getReqSys(),upayLog.getIdValue()});
				log.warn("原交易未发往CRM进行充值:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{mainOrgId, bankMsgVo.getReqTransID(), transIDH,
						bankMsgVo.getReqSys(),upayLog.getIdValue()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A04.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A04.getDesc());
//				msgVoRtn.setRspCode(upayLog.getChlSubRspCode());
//				msgVoRtn.setRspDesc(upayLog.getChlSubRspDesc());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());
				
				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			
//			String orgFlag = offOrgTrans(bankMsgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode());
			// 查询交易权限得根据号码类型，是移动还是联通电信
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(upayLog.getIdValue());	
			String orgFlag = offOrgTrans(bankMsgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode(), 
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			boolean orgFlag = isO2OTransOn(bankMsgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode());
//			boolean orgFlag = orgStatusCheck(mainOrgId);// 归属省
			if (orgFlag != null) {
				logger.warn("此业务渠道无此权限:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{mainOrgId, bankMsgVo.getReqTransID(), transIDH,
						bankMsgVo.getReqSys(),upayLog.getIdValue()});
				log.warn("此业务渠道无此权限:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{mainOrgId, bankMsgVo.getReqTransID(), transIDH,
						bankMsgVo.getReqSys(),upayLog.getIdValue()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			addInitLg(upayLog, txnLog);
			
			if (CommonConstant.YesOrNo.Yes.getValue().equals(upayLog.getReverseFlag())) {
				logger.warn("原交易已被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,
						bankMsgVo.getReqSys(),upayLog.getIdValue()});
				log.warn("原交易已被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,
						bankMsgVo.getReqSys(),upayLog.getIdValue()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A14.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A14.getDesc());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
				logger.info("原交易未冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
					new Object[]{reqMsg.getOriReqTransID(), bankMsgVo.getReqTransID(), transIDH,
					bankMsgVo.getReqSys(),upayLog.getIdValue()});
				
				txnLog.setRcvTransDt(intTxnDate);
				txnLog.setRcvTransId(transIDH);
				txnLog.setRcvTransTm(transIDHTime);
				txnLog.setRcvSessionId(transIDH);
					
				CrmMsgVo msg19 = new CrmMsgVo();
				msg19.setTransCode(transCode);
				msg19.setVersion(ExcConstant.CRM_VERSION);
				msg19.setTestFlag(testFlag);
				msg19.setBIPCode(CommonConstant.Bip.Bis18.getValue());
				msg19.setActivityCode(CommonConstant.CrmTrans.Crm08.getValue());
				msg19.setActionCode(CommonConstant.ActionCode.Requset.getValue());
				msg19.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
				msg19.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
				msg19.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
				msg19.setRouteValue(upayLog.getIdValue());
				msg19.setSessionID(transIDH);
				msg19.setTransIDO(transIDH);
				msg19.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
				msg19.setMsgSender(bankMsgVo.getRcvSys());
				msg19.setMsgReceiver(mainOrgId);
				
				CrmReverseMsgReqVo reqVo = new CrmReverseMsgReqVo();
				reqVo.setOriReqSys(ExcConstant.BANK_REQ_SYS);
				reqVo.setOriActionDate(upayLog.getIntTxnDate());
				reqVo.setOriTransactionID(upayLog.getRcvOprId());//此流水应该选择原充值流水
				reqVo.setRevokeReason("Bank端发起冲正");//Bank端请求时,没有这个字段
				
//				reqVo.setTransactionID(Serial.genSerialNos(CommonConstant.Sequence.OprId.toString()));
				//TransactionID设置成32位
				reqVo.setTransactionID(Serial.genSerialNum(CommonConstant.Sequence.OprId.toString()));
				reqVo.setSettleDate(bankMsgVo.getReqDate());//UPSS请求BOSS时必填,银行请求过来的日期
				msg19.setBody(reqVo);

//				txnLog.setIntTxnSeq(reqVo.getTransactionID());
				
				logger.info("begin 核心向CRM转发返销(冲正)请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
						new Object[]{transIDH, bankMsgVo.getReqTransID(), transIDH,
						bankMsgVo.getReqSys(),upayLog.getIdValue()});
				
				Map<String, Object> params = new HashMap<String, Object>();
				CrmMsgVo crmMsgVoRtn = crmReverseBus.execute(msg19, params,txnLog, null);
				logger.info("end 核心向CRM转发返销(冲正)请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
						new Object[]{transIDH, bankMsgVo.getReqTransID(), transIDH,
						bankMsgVo.getReqSys(),upayLog.getIdValue()});
				
				String rcvTransDt=StrUtil.subString(crmMsgVoRtn.getTransIDHTime(),0,8);
				
				
				if (crmMsgVoRtn.getBody() == null || crmMsgVoRtn.getBody().equals("")) {
					log.error("冲正返回报文体为空,内部交易流水号:{},业务应答方:{}" , 
							new Object[] {transIDH,crmMsgVoRtn.getTransIDH()});
					logger.error("冲正返回报文体为空,内部交易流水号:{},业务应答方:{}" , 
							new Object[] {transIDH,crmMsgVoRtn.getTransIDH()});
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
					msgVoRtn.setRcvDate(intTxnDate);
					msgVoRtn.setRcvTransID(transIDH);
					msgVoRtn.setRcvDateTime(transIDHTime);
					msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					
					txnLog.setChlRspCode(msgVoRtn.getRspCode());
					txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
					txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
					txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
					txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
					txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
					txnLog.setRcvRspType(crmMsgVoRtn.getRspType());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					return msgVoRtn;
				}
				if(RspCodeConstant.Upay.UPAY_U99998.getValue().equals(crmMsgVoRtn.getRspCode())){
					log.error("冲正返回超时,内部交易流水号:{},业务应答方:{}" , 
							new Object[] {transIDH,crmMsgVoRtn.getTransIDH()});
					logger.error("冲正返回超时,内部交易流水号:{},业务应答方:{}" , 
							new Object[] {transIDH,crmMsgVoRtn.getTransIDH()});
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
					msgVoRtn.setRcvDate(intTxnDate);
					msgVoRtn.setRcvTransID(transIDH);
					msgVoRtn.setRcvDateTime(transIDHTime);
					msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					
					txnLog.setChlRspCode(msgVoRtn.getRspCode());
					txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
					txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
					txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
					txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
					txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
					txnLog.setRcvRspType(crmMsgVoRtn.getRspType());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					return msgVoRtn;
				}
				
				CrmReverseMsgResVo resVo = new CrmReverseMsgResVo();
				MsgHandle.unmarshaller(resVo, (String) crmMsgVoRtn.getBody());
				txnLog.setRcvRspType(crmMsgVoRtn.getRspType());
				txnLog.setRcvSubRspCode(resVo.getRspCode());
				txnLog.setRcvSubRspDesc(resVo.getRspInfo());
				if(RspCodeConstant.Wzw.WZW_0000.getValue().equals(crmMsgVoRtn.getRspCode()) && RspCodeConstant.Crm.CRM_0000.getValue().equals(resVo.getRspCode())){
					logger.info("CRM应答成功!流水号:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}, 应答码 , 应答描述", 
							new Object[]{crmMsgVoRtn.getTransIDH(), bankMsgVo.getReqTransID(), transIDH,
							bankMsgVo.getReqSys(),upayLog.getIdValue(), resVo.getRspCode(), resVo.getRspInfo()});
					log.succ("CRM应答成功!流水号:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}, 应答码 , 应答描述", 
							new Object[]{crmMsgVoRtn.getTransIDH(), bankMsgVo.getReqTransID(), transIDH,
							bankMsgVo.getReqSys(),upayLog.getIdValue(), resVo.getRspCode(), resVo.getRspInfo()});
					
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
					msgVoRtn.setRcvDate(intTxnDate);
					msgVoRtn.setRcvTransID(transIDH);
					msgVoRtn.setRcvDateTime(transIDHTime);
					msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());
//					msgVoRtn.setBody(resVo);

					modifyNewAndOldLog(txnLog,upayLog,crmMsgVoRtn,rcvTransDt,msgVoRtn,logg,reqVo);
					logger.info("银行冲正交易完成,修改原纪录及新增交易流水:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{txnLog.getIntTxnSeq(), bankMsgVo.getReqTransID(), transIDH,
							bankMsgVo.getReqSys(),upayLog.getIdValue()});
					return msgVoRtn;
				}else{
					String errCode = resVo.getRspCode();
					errCode = BankErrorCodeCache.getBankErrCode(errCode);
					logger.warn("CRM应答失败!流水号:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}, 应答码 , 应答描述", 
							new Object[]{crmMsgVoRtn.getTransIDH(), bankMsgVo.getReqTransID(), transIDH,
							bankMsgVo.getReqSys(),upayLog.getIdValue(), resVo.getRspCode(), resVo.getRspInfo()});
					log.warn("CRM应答失败!流水号:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}, 应答码 , 应答描述", 
							new Object[]{crmMsgVoRtn.getTransIDH(), bankMsgVo.getReqTransID(), transIDH,
							bankMsgVo.getReqSys(),upayLog.getIdValue(), resVo.getRspCode(), resVo.getRspInfo()});
					msgVoRtn.setRspCode(errCode);
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
					msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
					msgVoRtn.setRcvTransID(crmMsgVoRtn.getTransIDH());
					msgVoRtn.setRcvDate(crmMsgVoRtn.getTxnDate());
					msgVoRtn.setRcvDateTime(crmMsgVoRtn.getTxnTime());
					msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

					modifyRcvLog(txnLog, crmMsgVoRtn, msgVoRtn, msg19);
					logger.info("修改新增交易流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}'", 
							new Object[]{txnLog.getIntTxnSeq(), bankMsgVo.getReqTransID(), transIDH,
							bankMsgVo.getReqSys()});
					logger.debug("结束银行冲正交易");
					return msgVoRtn;
				}
	} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			logger.error("运行异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, bankMsgVo.getReqTransID(), transIDH,
					bankMsgVo.getReqSys()});
			logger.error("运行异常:",e);
			log.error("运行异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, bankMsgVo.getReqTransID(), transIDH,
					bankMsgVo.getReqSys()});
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setRcvTransID(transIDH);
			msgVoRtn.setRcvDateTime(transIDHTime);
			msgVoRtn.setRcvDate(intTxnDate);
			msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			return msgVoRtn;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			logger.error("业务异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, bankMsgVo.getReqTransID(), transIDH,
					bankMsgVo.getReqSys()});
			logger.error("业务异常:",e);
			log.error("业务异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, bankMsgVo.getReqTransID(), transIDH,
					bankMsgVo.getReqSys()});
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setRcvTransID(transIDH);
			msgVoRtn.setRcvDateTime(transIDHTime);
			msgVoRtn.setRcvDate(intTxnDate);
			msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			return msgVoRtn;
		} catch (Exception e) {
			logger.error("系统异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, bankMsgVo.getReqTransID(), transIDH,
					bankMsgVo.getReqSys()});
			log.error("系统异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, bankMsgVo.getReqTransID(), transIDH,
					bankMsgVo.getReqSys()});
			logger.error("系统异常:",e);
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
		
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
			
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setRcvTransID(transIDH);
			msgVoRtn.setRcvDateTime(transIDHTime);
			msgVoRtn.setRcvDate(intTxnDate);
			msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			if (CommonConstant.TxnStatus.InitStatus.toString().equals(txnLog.getStatus())
					|| "".equals(StringUtil.toTrim(txnLog.getStatus()))) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			}
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		}
	}
	
	private void initLog(UpayCsysTxnLog txnLog, Long seqValue, String transIDH,String intTxnDate, String transIDHTime, BankMsgVo bankMsgVo,
			UpayCsysTransCode transCode, BankMsgVo msgVoRtn,BankReverseMsgReqVo reqMsg) {
		txnLog.setSeqId(seqValue);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setBussChl(transCode.getBussChl());//业务渠道
		txnLog.setBussType(transCode.getBussType());
//		txnLog.setTxnCat(transCode.getTxnCat());
		txnLog.setReqBipCode(transCode.getReqBipCode());
		txnLog.setReqActivityCode(transCode.getReqActivityCode());
		txnLog.setReqDomain(bankMsgVo.getReqSys());// 请求方应用域代码 
		
		txnLog.setReqTranshDt(intTxnDate);
		txnLog.setReqTranshId(transIDH);
		txnLog.setReqTranshTm(transIDHTime);
		txnLog.setReqTransId(bankMsgVo.getReqTransID());
		txnLog.setReqTransDt(bankMsgVo.getReqDate());
		txnLog.setReqTransTm(bankMsgVo.getReqDateTime());
		txnLog.setReqOprDt(bankMsgVo.getReqDate());
		txnLog.setReqOprId(bankMsgVo.getReqTransID());
		txnLog.setReqOprTm(bankMsgVo.getReqDateTime());
		
		txnLog.setReqCnlType(bankMsgVo.getReqChannel());// 发起方渠道标识 
		txnLog.setOriOrgId(reqMsg.getOriReqSys());
		txnLog.setOriOprTransId(reqMsg.getOriReqTransID());
		txnLog.setOriReqDate(reqMsg.getOriReqDate());
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		txnLog.setSettleDate(bankMsgVo.getReqDate());
		upayCsysTxnLogService.add(txnLog);
	}
	
	private void addInitLg(UpayCsysTxnLog upayLog,UpayCsysTxnLog txnLog){
		txnLog.setMainIdProvince(upayLog.getMainIdProvince());// 主号码归属地
		txnLog.setMainIdType(upayLog.getMainIdType());// 主号码标识类型
		txnLog.setMainIdValue(upayLog.getMainIdValue());// 主号码用户号码
		txnLog.setIdProvince(upayLog.getIdProvince());// 用户号码归属地
		txnLog.setIdType(upayLog.getIdType());// 用户号码标识类型
		txnLog.setIdValue(upayLog.getIdValue());// 用户号码
		txnLog.setUserStatus(upayLog.getUserStatus());// 用户状态
		txnLog.setUserCat(upayLog.getUserCat());// 用户类型
		txnLog.setBalance(upayLog.getBalance());// 用户余额
		txnLog.setSignStatus(upayLog.getSignStatus());// 签约状态
		txnLog.setUserType(upayLog.getUserType());// 用户证件类型
		txnLog.setUserId(upayLog.getUserId());// 用户证件号码
		txnLog.setBankId(upayLog.getBankId());// 银行编号
		txnLog.setBankAcctType(upayLog.getBankAcctType());// 银行帐号类型
		txnLog.setBankAccId(upayLog.getBankAccId());// 银行帐号
		txnLog.setNeedPayAmt(upayLog.getNeedPayAmt());// 应缴金额
		upayCsysTxnLogService.modify(txnLog);
	}
	
	private void modifyNewAndOldLog(UpayCsysTxnLog txnLog,UpayCsysTxnLog upayLog, CrmMsgVo crmMsgVoRtn,
			String rcvTransDt, BankMsgVo msgVoRtn,UpayCsysTxnLog logg ,CrmReverseMsgReqVo reqVo){
		txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
		txnLog.setRcvBipCode(crmMsgVoRtn.getBIPCode());
		txnLog.setRcvActivityCode(crmMsgVoRtn.getActivityCode());
		txnLog.setRcvDomain(crmMsgVoRtn.getMsgReceiver());
		txnLog.setRcvRouteType(crmMsgVoRtn.getRouteType());
		txnLog.setRcvRouteVal(crmMsgVoRtn.getRouteValue());
		txnLog.setRcvSessionId(crmMsgVoRtn.getSessionID());
		txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
		txnLog.setRcvTranshId(crmMsgVoRtn.getTransIDH());
		txnLog.setRcvTranshTm(crmMsgVoRtn.getTransIDHTime());
		txnLog.setRcvTranshDt(rcvTransDt);	
		txnLog.setRcvOprId(reqVo.getTransactionID());
		txnLog.setRcvOprDt(crmMsgVoRtn.getTransIDHTime());
		txnLog.setRcvOprTm(rcvTransDt);
		txnLog.setChlRspCode(msgVoRtn.getRspCode());
		txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
		txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
		txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
		txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
		txnLog.setPayAmt(-upayLog.getPayAmt());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		
		logg.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());
		logg.setBackFlag(CommonConstant.YesOrNo.Yes.toString());
		logg.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		logg.setSeqId(upayLog.getSeqId());
		upayCsysTxnLogService.modifyLog(txnLog, logg);
	}
	
	private void modifyRcvLog(UpayCsysTxnLog txnLog, CrmMsgVo crmMsgVoRtn, BankMsgVo msgVoRtn, CrmMsgVo msg19){
		txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
		txnLog.setRcvActivityCode(msg19.getActivityCode());
		txnLog.setRcvBipCode(msg19.getBIPCode());
		txnLog.setRcvDomain(msg19.getMsgReceiver());
		txnLog.setRcvRouteType(crmMsgVoRtn.getRouteType());
		txnLog.setRcvRouteVal(crmMsgVoRtn.getRouteValue());
		txnLog.setRcvSessionId(crmMsgVoRtn.getSessionID());
		txnLog.setRcvTranshId(crmMsgVoRtn.getTransIDH());
		txnLog.setRcvTranshTm(crmMsgVoRtn.getTransIDHTime());
		txnLog.setRcvTranshDt(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
		txnLog.setRcvSubRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setRcvSubRspDesc(crmMsgVoRtn.getRspDesc());
		txnLog.setRcvRspType(crmMsgVoRtn.getRspType());
		txnLog.setChlRspCode(msgVoRtn.getRspCode());
		txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
		txnLog.setChlSubRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setChlSubRspDesc(crmMsgVoRtn.getRspDesc());
		txnLog.setChlRspType(crmMsgVoRtn.getRspType());
		txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogService.modify(txnLog);
	}
	
}