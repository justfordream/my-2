package com.huateng.cmupay.action;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DataSourceInstances;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.service.system.IUpayBatParamService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogHisService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmReverseBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
import com.huateng.cmupay.models.his.UpayBatParam;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmReverseMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmRevokeMsgRspVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 银行渠道发起撤单交易 只能撤单30天
 * @author hdm 
 */
@Controller("bankRevokeAction")
@Scope("prototype")
public class BankRevokeAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {
//	private @Value("${countDay}") String countDay;
	@Autowired
	private IUpayCsysTxnLogHisService upayCsysTxnLogHisService;
	@Autowired
	private CrmReverseBus crmReverseBus;
	@Autowired
	private IUpayBatParamService upayBatParamService;

	@Override
	public BankMsgVo execute(BankMsgVo paramData) throws AppBizException {

		logger.debug("执行Bank撤单交易");
		BankMsgVo msgVo = paramData;
		BankMsgVo msgVoRtn = msgVo;
		BankMsgReqVo reqBody = new BankMsgReqVo();
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		String transIDH = msgVo.getTxnSeq();
		String transIDHTime = paramData.getTxnTime();
		String intTxnDate = msgVo.getTxnDate();
		Long seqValue = msgVo.getSeqId();
		UpayCsysTransCode transCode = msgVo.getTransCode();
		UpayCsysTxnLogHis hisLog = new UpayCsysTxnLogHis();
		UpayCsysTxnLog logg = new UpayCsysTxnLog();
		try {
			 MsgHandle.unmarshaller(reqBody, (String) msgVo.getBody());
			 
			 Map<String, Object> param = new HashMap<String, Object>();
			 param.put("reqDomain", msgVo.getReqSys());
			 param.put("reqTransId", msgVo.getReqTransID());
			 UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService.findObj(param);
			 if (upayCsysTxnLog != null) {
				 logger.warn("操作流水重复!Bank流水:{},UPAY流水:{},发起银行:{},",
						new Object[]{msgVo.getReqTransID(),transIDH,msgVo.getReqSys()});
				 log.warn("操作流水重复!Bank流水:{},UPAY流水:{},发起银行:{},",
							new Object[]{msgVo.getReqTransID(),transIDH,msgVo.getReqSys()});
				 msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				 msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				 msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				 msgVoRtn.setRcvDate(intTxnDate);
				 msgVoRtn.setRcvDateTime(transIDHTime);
				 msgVoRtn.setRcvTransID(transIDH);
				 msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());
				 return msgVoRtn;
			 }

			logger.info("begin 添加Bank渠道撤单交易流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
			log.info("begin 添加Bank渠道撤单交易流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
					new Object[]{msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
			initLog(txnLog, seqValue, transIDH, intTxnDate, transIDHTime, msgVo, transCode, msgVoRtn, reqBody);
			logger.info("end 添加Bank渠道撤单交易流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
					new Object[]{msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
			log.info("end 添加Bank渠道撤单交易流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
				new Object[]{msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});

			String validateMsg = this.validateModel(reqBody);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.info("body validate success,intTxnSeq:{}",new Object[]{transIDH});
			} else {
				logger.warn("报文格式错误:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{validateMsg, msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
				log.warn("报文格式错误:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{validateMsg, msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
				String code = validateMsg.split(":")[0];
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRspCode(code);
				msgVoRtn.setRspDesc(validateMsg);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

				txnLog.setChlRspCode(code);
				txnLog.setChlRspDesc(validateMsg);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}

			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyyMMdd");
			java.util.Date date1 = myFormatter.parse(msgVo.getReqDate());
			java.util.Date date2 = myFormatter.parse(reqBody.getOriReqDate());
			long date3 = (date1.getTime() - date2.getTime())/ (24 * 60 * 60 * 1000);// 表示相隔天数

			if (Long.valueOf(CommonConstant.SpeSymbol.ZERO.toString()).equals(date3)) {
				logger.info("当日交易!需隔日进行撤单:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
				log.info("当日交易!需隔日进行撤单:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A02.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A02.getDesc());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}

			boolean flag = date3 < Long.valueOf(
					DictCodeCache.getDictCode(DictConst.DictId.RevokeDaysMax.getValue(),
							DictConst.CodeId.RevokeDaysMax.getValue()).getCodeValue2());
			if (!flag) {
				logger.info("超过时限!不允许退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
				log.info("超过时限!不允许退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A20.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A20.getDesc());
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRcvDate(intTxnDate);
				msgVoRtn.setRcvTransID(transIDH);
				msgVoRtn.setRcvDateTime(transIDHTime);
				msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("reqDomain", reqBody.getOriReqSys());// 发起方机构代码
			map.put("reqTransDt", reqBody.getOriReqDate());// 发起方交易请求的日期
			map.put("reqTransId", reqBody.getOriReqTransID());// 发起方操作流水号
/*			DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
			UpayCsysTxnLog upayLog = upayCsysTxnLogService.findObj(map);

			if (upayLog != null) {
				logger.warn("原交易存在:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
				log.warn("原交易存在:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
						new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
				String mainOrgId = upayLog.getRcvDomain();
//				String mainOrgId = SysMapCache.getProvCd(upayLog.getIdValue()).getSysCd();upayLog
				if(StringUtils.isBlank(mainOrgId)){
					logger.warn("原交易未发往CRM进行充值:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{mainOrgId, msgVo.getReqTransID(), transIDH,
							msgVo.getReqSys(),upayLog.getIdValue()});
					log.warn("原交易未发往CRM进行充值:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{mainOrgId, msgVo.getReqTransID(), transIDH,
							msgVo.getReqSys(),upayLog.getIdValue()});
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A04.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A04.getDesc());
//					msgVoRtn.setRspCode(upayLog.getChlSubRspCode());
//					msgVoRtn.setRspDesc(upayLog.getChlSubRspDesc());
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
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);
					
					return msgVoRtn;
				}
				
//				String  orgFlag = offOrgTrans(msgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode());
				// 查询该交易的号码段属于移动还是联通电信的。
				ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(upayLog.getIdValue());
				String orgFlag = offOrgTrans(msgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode(), 
							provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.CHINA_MOBILE.getType());
//				boolean orgFlag = isO2OTransOn(msgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode());
//				boolean orgFlag = orgStatusCheck(mainOrgId);// 手机号码归属省(充值的号码)
				if (orgFlag != null) {
					logger.warn("此业务渠道无此权限:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{mainOrgId, msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
					log.warn("此业务渠道无此权限:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{mainOrgId, msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
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
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);
					return msgVoRtn;
				}
				addInitLs(txnLog, upayLog);

				
				if (CommonConstant.TxnStatus.TxnSuccess.toString().equals(upayLog.getStatus())
						|| CommonConstant.TxnStatus.TxnFail.toString().equals(upayLog.getStatus())) {
					logger.warn("原交易的状态为00/01!向CRM发起冲正(返销):{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
					log.warn("原交易的状态为00/01!向CRM发起冲正(返销):{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
					if (CommonConstant.YesOrNo.Yes.toString().equals(upayLog.getRefundFlag())) {
						logger.info("原交易已完成退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
						log.warn("原交易已完成退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
						msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A15.getValue());
						msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A15.getDesc());
						msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
						msgVoRtn.setRcvDate(intTxnDate);
						msgVoRtn.setRcvTransID(transIDH);
						msgVoRtn.setRcvDateTime(transIDHTime);
						msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

						txnLog.setChlRspCode(msgVoRtn.getRspCode());
						txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
						txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
						txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						return msgVoRtn;
					}
					logger.warn("原交易未退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
					log.warn("原交易未退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
					if (CommonConstant.YesOrNo.Yes.toString().equals(upayLog.getReverseFlag())) {
						logger.info("原交易已被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
						log.warn("原交易已被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
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
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						return msgVoRtn;
					}
					logger.warn("原交易未被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
					log.warn("原交易未被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
					if (!CommonConstant.ReconciliationFlag.reconciliationFg.toString().equals(upayLog.getReconciliationFlag())) {
						logger.warn("原交易未完成对账:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
						log.warn("原交易未完成对账:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
						msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A19.getValue());
						msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A19.getDesc());
						msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
						msgVoRtn.setRcvDate(intTxnDate);
						msgVoRtn.setRcvTransID(transIDH);
						msgVoRtn.setRcvDateTime(transIDHTime);
						msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());
						txnLog.setChlRspCode(msgVoRtn.getRspCode());
						txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
						txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
						txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						return msgVoRtn;
					}
					logger.info("原交易已完成对账:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
					log.warn("原交易已完成对账:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),upayLog.getIdValue()});
					
					CrmMsgVo msg19 = new CrmMsgVo();
					msg19.setTransCode(transCode);
					msg19.setVersion(ExcConstant.CRM_VERSION);
					msg19.setTestFlag(testFlag);
					msg19.setBIPCode(CommonConstant.Bip.Bis19.getValue());
					msg19.setActivityCode(CommonConstant.CrmTrans.Crm08.getValue());
					msg19.setActionCode(CommonConstant.ActionCode.Requset.getValue());
					msg19.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
					msg19.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
					msg19.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
					msg19.setRouteValue(upayLog.getIdValue());
					msg19.setSessionID(transIDH);
					msg19.setTransIDO(transIDH);
					msg19.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
					msg19.setMsgSender(msgVo.getRcvSys());
					msg19.setMsgReceiver(mainOrgId);
					
					CrmReverseMsgReqVo reqVo = new CrmReverseMsgReqVo();
					reqVo.setOriReqSys(ExcConstant.BANK_REQ_SYS);
					reqVo.setOriActionDate(upayLog.getIntTxnDate());
					reqVo.setOriTransactionID(upayLog.getRcvOprId());//此流水应该选择原充值流水
					reqVo.setRevokeReason(reqBody.getRevokeReason());
					reqVo.setTransactionID(Serial.genSerialNos(CommonConstant.Sequence.OprId.toString()));
					reqVo.setSettleDate(msgVo.getReqDate());// UPSS请求BOSS时必填,银行请求过来的日期
					msg19.setBody(reqVo);

					txnLog.setIntTxnSeq(reqVo.getTransactionID());
					
					logger.info("begin 核心向CRM转发撤单请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
							new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
							msgVo.getReqSys(),upayLog.getIdValue()});
					log.warn("begin 核心向CRM转发撤单请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
							new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
							msgVo.getReqSys(),upayLog.getIdValue()});
					Map<String, Object> params = new HashMap<String, Object>();
					CrmMsgVo crmMsgVoRtn = crmReverseBus.execute(msg19, params, txnLog, null);
					logger.info("end 核心向CRM转发撤单请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
							new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
							msgVo.getReqSys(),upayLog.getIdValue()});
					log.warn("end 核心向CRM转发撤单请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
							new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
							msgVo.getReqSys(),upayLog.getIdValue()});
					
					if (crmMsgVoRtn.getBody() == null || crmMsgVoRtn.getBody().equals("")) {
						log.error("撤单返回报文体为空,内部交易流水号:{},业务应答方:{}" , 
								new Object[] {transIDH,crmMsgVoRtn.getTransIDH()});
						logger.error("撤单返回报文体为空,内部交易流水号:{},业务应答方:{}" , 
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
						log.error("撤单返回超时,内部交易流水号:{},业务应答方:{}" , 
								new Object[] {transIDH,crmMsgVoRtn.getTransIDH()});
						logger.error("撤单返回超时,内部交易流水号:{},业务应答方:{}" , 
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
					
					CrmRevokeMsgRspVo rspVo = new CrmRevokeMsgRspVo();
					MsgHandle.unmarshaller(rspVo,(String) crmMsgVoRtn.getBody());
					
					if ((crmMsgVoRtn.getRspCode()).equals(RspCodeConstant.Wzw.WZW_0000.getValue())
							&& (rspVo.getRspCode()).equals(RspCodeConstant.Crm.CRM_0000.getValue())) {
						
						logger.info("CRM应答成功!流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{},响应码:{},响应描述:{}", 
								new Object[]{crmMsgVoRtn.getTransIDH(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),upayLog.getIdValue(),rspVo.getRspCode(),rspVo.getRspInfo()});
						log.succ("CRM应答成功!流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{},响应码:{},响应描述:{}", 
								new Object[]{crmMsgVoRtn.getTransIDH(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),upayLog.getIdValue(),rspVo.getRspCode(),rspVo.getRspInfo()});
						msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
						msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
						msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
						msgVoRtn.setRcvDate(intTxnDate);
						msgVoRtn.setRcvTransID(transIDH);
						msgVoRtn.setRcvDateTime(transIDHTime);
						msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

						addInitNewAndOldLog(txnLog, crmMsgVoRtn, msgVoRtn, msg19, upayLog, logg, transIDH, intTxnDate, transIDHTime, reqVo);
						logger.info("修改原纪录及新增交易流水:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{txnLog.getIntTxnSeq(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),upayLog.getIdValue()});
						log.warn("修改原纪录及新增交易流水:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{txnLog.getIntTxnSeq(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),upayLog.getIdValue()});
						
						return msgVoRtn;
					} else {
						logger.warn("CRM应答失败!流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{},响应码:{},响应描述:{}", 
								new Object[]{crmMsgVoRtn.getTransIDH(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),upayLog.getIdValue(),rspVo.getRspCode(),rspVo.getRspInfo()});
						log.warn("CRM应答失败!流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{},响应码:{},响应描述:{}", 
								new Object[]{crmMsgVoRtn.getTransIDH(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),upayLog.getIdValue(),rspVo.getRspCode(),rspVo.getRspInfo()});
						
						String errCode = rspVo.getRspCode();
						errCode = BankErrorCodeCache.getBankErrCode(errCode);
						msgVoRtn.setRspCode(errCode);
						msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
						msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
						msgVoRtn.setRcvTransID(crmMsgVoRtn.getTransIDH());
						msgVoRtn.setRcvDate(crmMsgVoRtn.getTxnDate());
						msgVoRtn.setRcvDateTime(crmMsgVoRtn.getTxnTime());
						msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

						addRcvLog(txnLog, crmMsgVoRtn, msgVoRtn, msg19, rspVo, transIDH, intTxnDate, transIDHTime,reqVo);
						logger.info("修改新增交易流水:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{txnLog.getIntTxnSeq(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),upayLog.getIdValue()});
						log.warn("修改新增交易流水:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{txnLog.getIntTxnSeq(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),upayLog.getIdValue()});
						
						return msgVoRtn;
					}
				} else {
					logger.warn("原交易状态不为00/01!返回交易处理中:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
							msgVo.getReqSys(),upayLog.getIdValue()});
					log.warn("原交易状态不为00/01!返回交易处理中:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
							msgVo.getReqSys(),upayLog.getIdValue()});
					
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_014A08.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_014A08.getDesc());
					msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
					msgVoRtn.setRcvDate(intTxnDate);
					msgVoRtn.setRcvTransID(transIDH);
					msgVoRtn.setRcvDateTime(transIDHTime);
					msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

					txnLog.setChlRspCode(msgVoRtn.getRspCode());
					txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
					txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
					txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);
					logger.debug("BankRevokeAction execute(Object) - end");
					return msgVoRtn;
				}
			} else {	*/
				logger.info("需查询交易流水历史表:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
				log.info("需查询交易流水历史表:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{}", 
						new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_HIS);
				UpayCsysTxnLogHis logHis = upayCsysTxnLogHisService.findHisStlObj(map);
				
				if (logHis != null) {
					logger.info("原纪录存在:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),logHis.getIdValue()});
					log.info("原纪录存在:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),logHis.getIdValue()});
					String mainOrgId = logHis.getRcvDomain();
//					String mainOrgId = SysMapCache.getProvCd(logHis.getIdValue()).getSysCd();
					if(StringUtils.isBlank(mainOrgId)){
						logger.info("原交易未发往CRM进行充值:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{mainOrgId, msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),logHis.getIdValue()});
						log.info("原交易未发往CRM进行充值:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{mainOrgId, msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),logHis.getIdValue()});
						msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A04.getValue());
						msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A04.getDesc());
//						msgVoRtn.setRspCode(logHis.getChlSubRspCode());
//						msgVoRtn.setRspDesc(logHis.getChlSubRspDesc());
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
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						return msgVoRtn;
					}
					
					ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(logHis.getIdValue());
					String  orgFlag = offOrgTrans(msgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode(),
							provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//					boolean orgFlag = isO2OTransOn(msgVo.getReqSys(), mainOrgId, paramData.getTransCode().getTransCode());
//					boolean orgFlag = orgStatusCheck(mainOrgId);// 充值手机号码的归属省
					if (orgFlag != null) {
						logger.warn("落地方机构服务的权限关闭:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{mainOrgId, msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),logHis.getIdValue()});
						log.warn("落地方机构服务的权限关闭:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{mainOrgId, msgVo.getReqTransID(), transIDH,msgVo.getReqSys(),logHis.getIdValue()});
						
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
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						return msgVoRtn;
					}
					addInitLgs(txnLog, logHis);
					
					if (CommonConstant.TxnStatus.TxnSuccess.toString().equals(logHis.getStatus())
							|| CommonConstant.TxnStatus.TxnFail.toString().equals(logHis.getStatus())) {
						logger.info("原交易状态为00/01!向CRM发起冲正(返销):{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
						log.info("原交易状态为00/01!向CRM发起冲正(返销):{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
						
						if (CommonConstant.YesOrNo.Yes.toString().equals(logHis.getRefundFlag())) {
							logger.info("原交易已完成退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
										msgVo.getReqSys(),logHis.getIdValue()});
							log.info("原交易已完成退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
										msgVo.getReqSys(),logHis.getIdValue()});
							msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A15.getValue());
							msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A15.getDesc());
							msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
							msgVoRtn.setRcvDate(intTxnDate);
							msgVoRtn.setRcvTransID(transIDH);
							msgVoRtn.setRcvDateTime(transIDHTime);
							msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

							txnLog.setChlRspCode(msgVoRtn.getRspCode());
							txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
							txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
							txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
							txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
							txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
							DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
							upayCsysTxnLogService.modify(txnLog);
							return msgVoRtn;
						}
						logger.info("原交易未完成退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
						log.info("原交易未完成退费:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
						if (CommonConstant.YesOrNo.Yes.toString().equals(logHis.getReverseFlag())) {
							logger.info("原交易已被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
										msgVo.getReqSys(),logHis.getIdValue()});
							log.info("原交易已被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
										msgVo.getReqSys(),logHis.getIdValue()});
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
							txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
							txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
							DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
							upayCsysTxnLogService.modify(txnLog);
							return msgVoRtn;
						}
						logger.info("原交易未被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
						log.info("原交易未被冲正:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
						
						Map<String, Object> reconMap = new HashMap<String, Object>();
						reconMap.put("paramType", "duizhang");// 设置查询标识
						reconMap.put("paramCode1", logHis.getSettleDate());// 结算日期
						reconMap.put("status", "00");// 设置参数为有效
						reconMap.put("paramValue", "00");// 设置为已对账
					
//						if (!CommonConstant.ReconciliationFlag.reconciliationFg.toString().equals(logHis.getReconciliationFlag())) {
						// 如果原交易中SettleDate字段为空，我们则认为该交易未对账
						if(logHis.getSettleDate() == null || upayBatParamService.findObj(reconMap) == null) {
							logger.info("原交易未完成对账:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
										msgVo.getReqSys(),logHis.getIdValue()});
							log.info("原交易未完成对账:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
										msgVo.getReqSys(),logHis.getIdValue()});
							msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_013A19.getValue());
							msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_013A19.getDesc());
							msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
							msgVoRtn.setRcvDate(intTxnDate);
							msgVoRtn.setRcvTransID(transIDH);
							msgVoRtn.setRcvDateTime(transIDHTime);
							msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

							txnLog.setChlRspCode(msgVoRtn.getRspCode());
							txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
							txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
							txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
							txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
							txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
							DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
							upayCsysTxnLogService.modify(txnLog);
							return msgVoRtn;
						}
						
						logger.info("原交易已完成对账:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
						log.info("原交易已完成对账:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
						
						CrmMsgVo msg19 = new CrmMsgVo();
						msg19.setTransCode(transCode);
						msg19.setVersion(ExcConstant.CRM_VERSION);
						msg19.setTestFlag(testFlag);
						msg19.setBIPCode(CommonConstant.Bip.Bis19.getValue());
						msg19.setActivityCode(CommonConstant.CrmTrans.Crm08.getValue());
						msg19.setActionCode(CommonConstant.ActionCode.Requset.getValue());
						msg19.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
						msg19.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
						msg19.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
						msg19.setRouteValue(logHis.getIdValue());
						msg19.setSessionID(transIDH); 
						msg19.setTransIDO(transIDH);
						msg19.setTransIDOTime(StrUtil.subString(transIDHTime,0, 14));
						msg19.setMsgSender(msgVo.getRcvSys());
						msg19.setMsgReceiver(mainOrgId);
						
						CrmReverseMsgReqVo reqVo = new CrmReverseMsgReqVo();
						reqVo.setOriReqSys(ExcConstant.BANK_REQ_SYS);
						reqVo.setOriActionDate(logHis.getIntTxnDate());
						reqVo.setOriTransactionID(logHis.getRcvOprId());//此流水应该选择原充值流水
						reqVo.setRevokeReason(reqBody.getRevokeReason());
						reqVo.setSettleDate(msgVo.getReqDate());// UPSS请求BOSS时必填,银行请求过来的日期
//						reqVo.setTransactionID(Serial.genSerialNos(CommonConstant.Sequence.OprId.toString()));
						//TransactionID设置成32位
						reqVo.setTransactionID(Serial.genSerialNum(CommonConstant.Sequence.OprId.toString()));
						msg19.setBody(reqVo);

						/*txnLog.setIntTxnSeq(reqVo.getTransactionID());*/
						txnLog.setIntTxnSeq(msgVoRtn.getRcvTransID());
						
						logger.info("begin 核心向CRM转发返销(冲正)请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},发往的省,手机号码:{}", 
								new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),mainOrgId,logHis.getIdValue()});
						log.info("begin 核心向CRM转发返销(冲正)请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},发往的省,手机号码:{}", 
								new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),mainOrgId,logHis.getIdValue()});
						Map<String, Object> params = new HashMap<String, Object>();
						CrmMsgVo crmMsgVoRtn = crmReverseBus.execute(msg19,params, txnLog, null);
						logger.info("end 核心向CRM转发返销(冲正)请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},发往的省,手机号码:{}", 
								new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),mainOrgId,logHis.getIdValue()});
						log.info("end 核心向CRM转发返销(冲正)请求流水号:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},发往的省,手机号码:{}", 
								new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),mainOrgId,logHis.getIdValue()});

						
						if (crmMsgVoRtn.getBody() == null || crmMsgVoRtn.getBody().equals("")) {
							log.error("返销返回报文体为空,内部交易流水号:{},业务应答方:{}" , 
									new Object[] {transIDH,crmMsgVoRtn.getTransIDH()});
							logger.error("返销返回报文体为空,内部交易流水号:{},业务应答方:{}" , 
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
						
						CrmRevokeMsgRspVo rspVo = new CrmRevokeMsgRspVo();
						MsgHandle.unmarshaller(rspVo,(String) crmMsgVoRtn.getBody());
						
						if ((crmMsgVoRtn.getRspCode()).equals(RspCodeConstant.Wzw.WZW_0000.getValue()) && (rspVo.getRspCode()).equals(RspCodeConstant.Crm.CRM_0000.getValue())) {
							logger.info("CRM应答成功!流水号:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}, 应答码 , 应答描述", 
									new Object[]{crmMsgVoRtn.getTransIDH(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue(), rspVo.getRspCode(), rspVo.getRspInfo()});
							log.succ("CRM应答成功!流水号:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}, 应答码 , 应答描述", 
									new Object[]{crmMsgVoRtn.getTransIDH(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue(), rspVo.getRspCode(), rspVo.getRspInfo()});
							
							msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
							msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
							msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
							msgVoRtn.setRcvDate(intTxnDate);
							msgVoRtn.setRcvTransID(transIDH);
							msgVoRtn.setRcvDateTime(transIDHTime);
							msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

							addInitRcvLg(txnLog, crmMsgVoRtn, msgVoRtn, logHis, reqVo);
							logger.info("修改新增交易流水:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{txnLog.getIntTxnSeq(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
							log.info("修改新增交易流水:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{txnLog.getIntTxnSeq(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
							addInitOldLg(hisLog, logHis);
							logger.info("修改原纪录:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
							log.info("修改原纪录:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}", 
									new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
							
							logger.debug("结束银行撤单交易流程");
							return msgVoRtn;
						} else {
							String errCode = rspVo.getRspCode();
							errCode = BankErrorCodeCache.getBankErrCode(errCode);
							logger.warn("CRM应答失败!流水号:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}, 应答码 , 应答描述", 
									new Object[]{crmMsgVoRtn.getTransIDH(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue(), rspVo.getRspCode(), rspVo.getRspInfo()});
							log.warn("CRM应答失败!流水号:{}, Bank操作流水:{} ,内部交易流水号intTxnSeq:{},发起银行:{},手机号码:{}, 应答码 , 应答描述", 
									new Object[]{crmMsgVoRtn.getTransIDH(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue(), rspVo.getRspCode(), rspVo.getRspInfo()});
							
							msgVoRtn.setRspCode(errCode);
							msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
							msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
							msgVoRtn.setRcvTransID(crmMsgVoRtn.getTransIDH());
							msgVoRtn.setRcvDate(crmMsgVoRtn.getTxnDate());
							msgVoRtn.setRcvDateTime(crmMsgVoRtn.getTxnTime());
							msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

							addInitFailLgs(txnLog, crmMsgVoRtn, msgVoRtn, msg19, rspVo);
							logger.info("修改新增交易流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
									new Object[]{txnLog.getIntTxnSeq(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
							log.info("修改新增交易流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
									new Object[]{txnLog.getIntTxnSeq(), msgVo.getReqTransID(), transIDH,
									msgVo.getReqSys(),logHis.getIdValue()});
							
							logger.debug("结束银行撤单交易流程");
							return msgVoRtn;
						}
					} else {
						logger.info("原交易状态不为00/01:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),logHis.getIdValue()});
						log.info("原交易状态不为00/01:{}, Bank操作流水:{} ,内部流水:{},发起银行:{},手机号码:{}", 
								new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,
								msgVo.getReqSys(),logHis.getIdValue()});
						msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_014A08.getValue());
						msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_014A08.getDesc());
						msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
						msgVoRtn.setRcvDate(intTxnDate);
						msgVoRtn.setRcvTransID(transIDH);
						msgVoRtn.setRcvDateTime(transIDHTime);
						msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

						txnLog.setChlRspCode(msgVoRtn.getRspCode());
						txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
						txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
						txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						return msgVoRtn;
					}
				} else {
					logger.info("交易流水历史表不存在该记录:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
					log.info("交易流水历史表不存在该记录:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
							new Object[]{reqBody.getOriReqTransID(), msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
					msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
					msgVoRtn.setRcvDate(intTxnDate);
					msgVoRtn.setRcvTransID(transIDH);
					msgVoRtn.setRcvDateTime(transIDHTime);
					msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());

					txnLog.setChlRspCode(msgVoRtn.getRspCode());
					txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
					txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
					txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);
					return msgVoRtn;
				}
/*			}	*/
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			logger.error("运行异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
					msgVo.getReqSys()});
			logger.error("运行异常:",e);
			log.error("运行异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
					msgVo.getReqSys()});
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
			logger.error("应用业务层异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
					msgVo.getReqSys()});
			logger.error("应用业务层异常:",e);
			log.error("应用业务层异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, msgVo.getReqTransID(), transIDH,
					msgVo.getReqSys()});
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
			
			logger.error("未知异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
			logger.error("未知异常:",e);
			log.error("未知异常!CRM流水:{}, Bank操作流水:{} ,内部流水:{},发起银行:{}", 
					new Object[]{transIDH, msgVo.getReqTransID(), transIDH,msgVo.getReqSys()});
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
		
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setRcvTransID(transIDH);// 接收方交易流水号
			msgVoRtn.setRcvDateTime(transIDHTime);
			msgVoRtn.setRcvDate(intTxnDate);
			msgVoRtn.setBody(CommonConstant.SpeSymbol.BLANK.toString());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			if (CommonConstant.TxnStatus.InitStatus.toString().equals(txnLog.getStatus())|| "".equals(StringUtil.toTrim(txnLog.getStatus()))) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			}
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		}
	}

	private void initLog(UpayCsysTxnLog txnLog, Long seqValue, String transIDH,String intTxnDate, String transIDHTime, BankMsgVo msgVo,
			UpayCsysTransCode transCode, BankMsgVo msgVoRtn,BankMsgReqVo reqBody) {
		txnLog.setSeqId(seqValue);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setBussChl(transCode.getBussChl());// 业务渠道
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setBussType(transCode.getBussType());
//		txnLog.setTxnCat(transCode.getTxnCat());
		txnLog.setReqActivityCode(msgVo.getActivityCode());
		txnLog.setReqDomain(msgVo.getReqSys());

		txnLog.setReqTranshDt(intTxnDate);
		txnLog.setReqTranshId(transIDH);
		txnLog.setReqTranshTm(transIDHTime);
		txnLog.setReqOprDt(msgVo.getReqDate());
		txnLog.setReqOprId(msgVo.getReqTransID());
		txnLog.setReqOprTm(msgVo.getReqDateTime());
		txnLog.setReqTransId(msgVo.getReqTransID());
		txnLog.setReqTransDt(msgVo.getReqDate());
		txnLog.setReqTransTm(msgVo.getReqDateTime());

		txnLog.setReqCnlType(msgVo.getReqChannel());
		txnLog.setOriOrgId(reqBody.getOriReqSys());
		txnLog.setOriOprTransId(reqBody.getOriReqTransID());
		txnLog.setOriReqDate(reqBody.getOriReqDate());
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		txnLog.setSettleDate(msgVo.getReqDate());
		upayCsysTxnLogService.add(txnLog);
	}
	
	private void addInitLs(UpayCsysTxnLog txnLog,UpayCsysTxnLog upayLog){
		txnLog.setMainIdProvince(upayLog.getMainIdProvince());// 主号码归属地
		txnLog.setMainIdType(upayLog.getMainIdType());// 主号码标识类型
		txnLog.setMainIdValue(upayLog.getMainIdValue());// 主号码用户号码
		txnLog.setIdProvince(upayLog.getIdProvince());// 用户号码归属地
		txnLog.setIdType(upayLog.getIdType());// 用户号码标识类型
		txnLog.setIdValue(upayLog.getIdValue());// 用户号码
		txnLog.setUserStatus(upayLog.getUserStatus());// 用户状态
		txnLog.setBalance(upayLog.getBalance());// 用户余额
		txnLog.setSignStatus(upayLog.getSignStatus());// 签约状态
		txnLog.setUserType(upayLog.getUserType());// 用户证件类型
		txnLog.setUserId(upayLog.getUserId());// 用户证件号码
		txnLog.setBankId(upayLog.getBankId());// 银行编号
		txnLog.setBankAcctType(upayLog.getBankAcctType());// 银行帐号类型
		txnLog.setBankAccId(upayLog.getBankAccId());// 银行帐号
		txnLog.setNeedPayAmt(upayLog.getNeedPayAmt());// 应缴金额
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogService.modify(txnLog);
	}
	
	private void addInitLgs(UpayCsysTxnLog txnLog,UpayCsysTxnLogHis logHis){
		txnLog.setMainIdProvince(logHis.getMainIdProvince());// 主号码归属地
		txnLog.setMainIdType(logHis.getMainIdType());// 主号码标识类型
		txnLog.setMainIdValue(logHis.getMainIdValue());// 主号码用户号码
		txnLog.setIdProvince(logHis.getIdProvince());// 用户号码归属地
		txnLog.setIdType(logHis.getIdType());// 用户号码标识类型
		txnLog.setIdValue(logHis.getIdValue());// 用户号码
		txnLog.setUserStatus(logHis.getUserStatus());// 用户状态
		txnLog.setBalance(logHis.getBalance());// 用户余额
		txnLog.setSignStatus(logHis.getSignStatus());// 签约状态
		txnLog.setUserType(logHis.getUserType());// 用户证件类型
		txnLog.setUserId(logHis.getUserId());// 用户证件号码
		txnLog.setBankId(logHis.getBankId());// 银行编号
		txnLog.setBankAcctType(logHis.getBankAcctType());// 银行帐号类型
		txnLog.setBankAccId(logHis.getBankAccId());// 银行帐号
		txnLog.setNeedPayAmt(logHis.getNeedPayAmt());// 应缴金额
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogService.modify(txnLog);
	}
	
	private void addInitRcvLg(UpayCsysTxnLog txnLog, CrmMsgVo crmMsgVoRtn, BankMsgVo msgVoRtn, UpayCsysTxnLogHis logHis ,CrmReverseMsgReqVo reqVo){
		txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
		txnLog.setRcvBipCode(crmMsgVoRtn.getBIPCode());
		txnLog.setRcvActivityCode(crmMsgVoRtn.getActivityCode());
		txnLog.setRcvDomain(crmMsgVoRtn.getMsgReceiver());
		txnLog.setRcvRouteType(crmMsgVoRtn.getRouteType());
		txnLog.setRcvRouteVal(crmMsgVoRtn.getRouteValue());
		txnLog.setRcvSessionId(crmMsgVoRtn.getSessionID());
		txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
		txnLog.setRcvSubRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setRcvTranshId(crmMsgVoRtn.getTransIDH());
		txnLog.setRcvTranshTm(crmMsgVoRtn.getTransIDHTime());
		txnLog.setRcvTranshDt(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setRcvOprId(reqVo.getTransactionID());
		txnLog.setRcvOprDt(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setRcvOprTm(crmMsgVoRtn.getTransIDHTime());
		txnLog.setChlRspCode(msgVoRtn.getRspCode());
		txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
		txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
		txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
		txnLog.setPayAmt(-logHis.getPayAmt());
		txnLog.setSettleDate(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogService.modify(txnLog);
	}
	
	private void addInitOldLg(UpayCsysTxnLogHis hisStlLog, UpayCsysTxnLogHis logHis){
//		hisStlLog.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());
		// 修改UPAY_CSYS_TXN_LOG_HIS_STL表
		hisStlLog.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());// 退费标识
		hisStlLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisStlLog.setSeqId(logHis.getSeqId());
		hisStlLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_HIS);
		upayCsysTxnLogHisService.modifyHisStl(hisStlLog);
		
		// 修改UPAY_CSYS_TXN_LOG_HIS表
		UpayCsysTxnLogHis hisLog = new UpayCsysTxnLogHis();
		hisLog.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());// 退费标识
		hisLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisLog.setSeqId(logHis.getSeqId());
		hisLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogHisService.modify(hisLog);
		
	}
	
	private void addInitFailLgs(UpayCsysTxnLog txnLog, CrmMsgVo crmMsgVoRtn, BankMsgVo msgVoRtn, CrmMsgVo msg19, CrmRevokeMsgRspVo rspVo){
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
		txnLog.setRcvSubRspCode(rspVo.getRspCode());
		txnLog.setRcvSubRspDesc(rspVo.getRspInfo());
		txnLog.setRcvRspType(crmMsgVoRtn.getRspType());
		txnLog.setChlRspCode(msgVoRtn.getRspCode());
		txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
		txnLog.setChlSubRspCode(rspVo.getRspCode());
		txnLog.setChlSubRspDesc(rspVo.getRspInfo());
//		txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.toString());
		txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogService.modify(txnLog);
	}
	
	private void addInitNewAndOldLog(UpayCsysTxnLog txnLog, CrmMsgVo crmMsgVoRtn, BankMsgVo msgVoRtn, CrmMsgVo msg19, UpayCsysTxnLog upayLog,
			UpayCsysTxnLog logg,String transIDH,String intTxnDate, String transIDHTime,CrmReverseMsgReqVo reqVo){
		txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
		txnLog.setRcvBipCode(crmMsgVoRtn.getBIPCode());
		txnLog.setRcvActivityCode(crmMsgVoRtn.getActivityCode());
		txnLog.setRcvDomain(crmMsgVoRtn.getMsgReceiver());
		txnLog.setRcvRouteType(crmMsgVoRtn.getRouteType());
		txnLog.setRcvRouteVal(crmMsgVoRtn.getRouteValue());
		txnLog.setRcvSessionId(crmMsgVoRtn.getSessionID());
		txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
		txnLog.setRcvSubRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setRcvTransDt(intTxnDate);
		txnLog.setRcvTransId(transIDH);
		txnLog.setRcvTransTm(transIDHTime);
		txnLog.setRcvTranshId(crmMsgVoRtn.getTransIDH());
		txnLog.setRcvTranshTm(crmMsgVoRtn.getTransIDHTime());
		txnLog.setRcvTranshDt(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setRcvOprId(reqVo.getTransactionID());//CrmReverseMsgReqVo reqVo
		txnLog.setRcvOprDt(crmMsgVoRtn.getTransIDHTime());
		txnLog.setRcvOprTm(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setChlRspCode(msgVoRtn.getRspCode());
		txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
		txnLog.setChlSubRspCode(msgVoRtn.getRspCode());
		txnLog.setChlSubRspDesc(msgVoRtn.getRspDesc());
		txnLog.setPayAmt(-upayLog.getPayAmt());
		txnLog.setSettleDate(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());

		logg.setSeqId(upayLog.getSeqId());
		logg.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());
//		logg.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());
		logg.setBackFlag(CommonConstant.YesOrNo.Yes.toString());
		logg.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogService.modifyLog(txnLog, logg);
	}
	
	private void addRcvLog(UpayCsysTxnLog txnLog, CrmMsgVo crmMsgVoRtn, BankMsgVo msgVoRtn, CrmMsgVo msg19, CrmRevokeMsgRspVo rspVo,
			String transIDH,String intTxnDate, String transIDHTime,CrmReverseMsgReqVo reqVo){
		txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
		txnLog.setRcvActivityCode(msg19.getActivityCode());
		txnLog.setRcvBipCode(msg19.getBIPCode());
		txnLog.setRcvDomain(msg19.getMsgReceiver());
		txnLog.setRcvRouteType(crmMsgVoRtn.getRouteType());
		txnLog.setRcvRouteVal(crmMsgVoRtn.getRouteValue());
		txnLog.setRcvSessionId(crmMsgVoRtn.getSessionID());
		txnLog.setRcvTransDt(intTxnDate);
		txnLog.setRcvTransId(transIDH);
		txnLog.setRcvTransTm(transIDHTime);
		txnLog.setRcvTranshId(crmMsgVoRtn.getTransIDH());
		txnLog.setRcvTranshTm(crmMsgVoRtn.getTransIDHTime());
		txnLog.setRcvTranshDt(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setRcvOprId(reqVo.getTransactionID());
		txnLog.setRcvOprDt(crmMsgVoRtn.getTransIDHTime());
		txnLog.setRcvOprTm(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
		txnLog.setRcvSubRspCode(rspVo.getRspCode());
		txnLog.setRcvSubRspDesc(rspVo.getRspInfo());
		txnLog.setRcvRspType(crmMsgVoRtn.getRspType());
		txnLog.setChlRspCode(msgVoRtn.getRspCode());
		txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
		txnLog.setChlSubRspCode(rspVo.getRspCode());
		txnLog.setChlSubRspDesc(rspVo.getRspInfo());
//		txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.toString());
		txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogService.modify(txnLog);
	}
}
