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
import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.service.system.IUpayBatParamService;
import com.huateng.cmupay.controller.service.system.IUpayCsysPayLimitService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogHisService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.bank.BankRefundBus;
import com.huateng.cmupay.jms.business.crm.CrmReverseBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
import com.huateng.cmupay.models.his.UpayBatParam;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankRefundReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmRefundReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmRefundRspVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmReverseMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmReverseMsgResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.StrUtil;

/**
 * 移动渠道缴费退费
 * 只能撤单30天
*/
@Controller("crmRefundAction")
@Scope("prototype")
public class CrmRefundAction extends AbsBaseAction<CrmMsgVo, CrmMsgVo> {

//	private @Value("${countDay}") String countDay;
	@Autowired
	private IUpayCsysTxnLogHisService upayCsysTxnLogHisService;
	@Autowired
	private IUpayCsysPayLimitService upayCsysPayLimitService;
	@Autowired
	private BankRefundBus bankRefundBus;
	@Autowired
	private CrmReverseBus crmReverseBus;
	@Autowired
	private IUpayBatParamService upayBatParamService;

	@SuppressWarnings("unused")
	@Override
	public CrmMsgVo execute(CrmMsgVo paramData) throws AppBizException {
		
		logger.debug("开始执行CRM退费交易");
		CrmMsgVo msgVo = paramData;
		CrmRefundReqVo reqVo = new CrmRefundReqVo();// 请求报文体
		CrmMsgVo msgRtn = msgVo;// 返回报文
		String transIDH = msgVo.getTxnSeq();
		String intTxnDate = msgVo.getTxnDate();
		Long seqValue = msgVo.getSeqId();
		String transIDHTime = paramData.getTxnTime();

		/*返回报文的部分字段*/ 
		msgRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
		msgRtn.setRouteType(CommonConstant.RouteType.RouteProvince.getValue());
		msgRtn.setRouteValue(msgVo.getRouteValue());
		msgRtn.setTransIDH(transIDH);
		msgRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
		
		CrmRefundRspVo rspVo = new CrmRefundRspVo();// 返回报文体
		UpayCsysTransCode transCode = msgVo.getTransCode();
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		txnLog.setSettleDate(intTxnDate);
		UpayCsysTxnLog logg = new UpayCsysTxnLog();
		UpayCsysTxnLogHis hisLog = new UpayCsysTxnLogHis();

		try {
			MsgHandle.unmarshaller(reqVo, (String) msgVo.getBody());
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqDomain", msgVo.getMsgSender());
			param.put("reqOprId", reqVo.getTransactionID());
			UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService.findObj(param);
			if (upayCsysTxnLog != null) {
				logger.warn("移动渠道缴费退费,操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},",
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				log.warn("移动渠道缴费退费,操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},",
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				rspVo.setOriReqSys(reqVo.getOriReqSys());
				rspVo.setOriTransactionID(reqVo.getOriTransactionID());
				rspVo.setOriActionDate(reqVo.getOriActionDate());
				rspVo.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
				rspVo.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc());
				rspVo.setTransactionID(reqVo.getTransactionID()); 
				msgRtn.setBody(rspVo);
				
				logger.debug("结束CRM退费交易");
				return msgRtn;
			}

			logger.info("移动渠道缴费退费,begin 添加CRM渠道退费交易流水:{} ,内部交易流水号intTxnSeq:{},发起省:{}", 
					new Object[]{msgVo.getTransIDO(),transIDH,msgVo.getMsgSender()});
			log.info("移动渠道缴费退费,begin 添加CRM渠道退费交易流水:{} ,内部交易流水号intTxnSeq:{},发起省:{}", 
					new Object[]{msgVo.getTransIDO(), transIDH,msgVo.getMsgSender()});
			initLog(txnLog, seqValue, transIDH, intTxnDate, transIDHTime, msgVo, transCode, msgRtn, reqVo);
			logger.info("移动渠道缴费退费,end 添加CRM渠道退费交易流水:{} ,内部交易流水号intTxnSeq:{},发起省:{}", 
					new Object[]{msgVo.getTransIDO(), transIDH,msgVo.getMsgSender()});
			log.info("移动渠道缴费退费,end 添加CRM渠道退费交易流水:{} ,内部交易流水号intTxnSeq:{},发起省:{}", 
					new Object[]{msgVo.getTransIDO(), transIDH,msgVo.getMsgSender()});
			
			String validateMsg = this.validateModel(reqVo);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.debug("body validate success");
			} else {
				logger.warn("移动渠道缴费退费,格式校验失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},错误字段:{}" ,
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender(),validateMsg});
				log.warn("移动渠道缴费退费,格式校验失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},错误字段:{}" ,
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender(),validateMsg});
				msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				rspVo.setOriReqSys(reqVo.getOriReqSys());
				rspVo.setOriTransactionID(reqVo.getOriTransactionID());
				rspVo.setOriActionDate(reqVo.getOriActionDate());
				rspVo.setRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				rspVo.setRspInfo(RspCodeConstant.Crm.CRM_4A99.getDesc()+ validateMsg);
				rspVo.setTransactionID(reqVo.getTransactionID());
				msgRtn.setBody(rspVo);

				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A99.getDesc()+ validateMsg);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("结束CRM退费交易");
				return msgRtn;
			}

			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyyMMdd"); 
			java.util.Date date1 = myFormatter.parse(StrUtil.subString(msgVo.getTransIDOTime(), 0, 8));
			java.util.Date date2 = myFormatter.parse(reqVo.getOriActionDate());
			long date3 = (date1.getTime() - date2.getTime())/ (24 * 60 * 60 * 1000);
			logger.info("移动渠道缴费退费,请求日期与原交易日期的相隔天数:{},内部交易流水号:{}",
					date3, transIDH);
			log.info("移动渠道缴费退费,请求日期与原交易日期的相隔天数:{},内部交易流水号:{}",
					date3, transIDH);
			if (Long.valueOf(CommonConstant.SpeSymbol.ZERO.toString()).equals(date3)) {
				logger.warn("移动渠道缴费退费,当日交易不允许退费:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				log.warn("移动渠道缴费退费,当日交易不允许退费:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				rspVo.setOriReqSys(reqVo.getOriReqSys());
				rspVo.setOriTransactionID(reqVo.getOriTransactionID());
				rspVo.setOriActionDate(reqVo.getOriActionDate());
				rspVo.setRspCode(RspCodeConstant.Crm.CRM_3A02.getValue());
				rspVo.setRspInfo(RspCodeConstant.Crm.CRM_3A02.getDesc());
				rspVo.setTransactionID(reqVo.getTransactionID());
				msgRtn.setBody(rspVo);

				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(msgRtn.getRspType());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A02.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A02.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("结束CRM退费交易");
				return msgRtn;
			}
			boolean flag = date3 < Long.valueOf(DictCodeCache.getDictCode(DictConst.DictId.RevokeDaysMax.getValue(),
					DictConst.CodeId.RevokeDaysMax.getValue()).getCodeValue2());
			if (!flag) {
				logger.warn("移动渠道缴费退费,超过时限不允许退费!原交易流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				log.warn("移动渠道缴费退费,超过时限不允许退费!原交易流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				rspVo.setOriReqSys(reqVo.getOriReqSys());
				rspVo.setOriTransactionID(reqVo.getOriTransactionID());
				rspVo.setOriActionDate(reqVo.getOriActionDate());
				rspVo.setRspCode(RspCodeConstant.Crm.CRM_3A20.getValue());
				rspVo.setRspInfo(RspCodeConstant.Crm.CRM_3A20.getDesc());
				rspVo.setTransactionID(reqVo.getTransactionID());
				msgRtn.setBody(rspVo);

				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(msgRtn.getRspType());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A20.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A20.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
				upayCsysTxnLogService.modify(txnLog);
				
				logger.debug("结束CRM退费交易");
				return msgRtn;
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("reqDomain", reqVo.getOriReqSys());
			map.put("reqTransDt", reqVo.getOriActionDate());
			map.put("reqOprId", reqVo.getOriTransactionID());
			
				/* 由于目前交易流水表中可以存3天的数据, 因此在交易流水表中无法查到记录的时候,需要查询交易流水历史表*/
				logger.info("移动渠道缴费退费,查询交易流水历史表:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				log.info("移动渠道缴费退费,查询交易流水历史表:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_HIS);
				UpayCsysTxnLogHis logHis = upayCsysTxnLogHisService.findHisStlObj(map);
				if (logHis == null) {
					logger.warn("移动渠道缴费退费,交易流水历史表中无原交易:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender()});
					log.warn("移动渠道缴费退费,交易流水历史表中无原交易:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender()});
					msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					rspVo.setOriReqSys(reqVo.getOriReqSys());
					rspVo.setOriTransactionID(reqVo.getOriTransactionID());
					rspVo.setOriActionDate(reqVo.getOriActionDate());
					rspVo.setRspCode(RspCodeConstant.Crm.CRM_4A05.getValue());
					rspVo.setRspInfo(RspCodeConstant.Crm.CRM_4A05.getDesc());
					rspVo.setTransactionID(reqVo.getTransactionID());
					msgRtn.setBody(rspVo);

					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(msgRtn.getRspType());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A05.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A05.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);
					
					logger.debug("结束CRM退费交易");
					return msgRtn;
				}
				logger.info("移动渠道缴费退费,交易流水历史表中存在原交易:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender(),logHis.getIdValue()});
				log.info("移动渠道缴费退费,交易流水历史表中存在原交易:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender(),logHis.getIdValue()});
//				String mainOrgId = SysMapCache.getProvCd(logHis.getIdValue()).getSysCd();// 手机号码充值归属省
				String mainOrgId = logHis.getRcvDomain();
				if(StringUtils.isBlank(mainOrgId)){
					logger.warn("移动渠道缴费退费,原交易未发往CRM进行充值:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					log.warn("移动渠道缴费退费,原交易未发往CRM进行充值:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A04.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A04.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);
					
					msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					rspVo.setOriReqSys(reqVo.getOriReqSys());
					rspVo.setOriTransactionID(reqVo.getOriTransactionID());
					rspVo.setOriActionDate(reqVo.getOriActionDate());
					rspVo.setRspCode(RspCodeConstant.Crm.CRM_3A04.getValue());
					rspVo.setRspInfo(RspCodeConstant.Crm.CRM_3A04.getDesc());
					rspVo.setTransactionID(reqVo.getTransactionID());
					msgRtn.setBody(rspVo);
					
					logger.debug("结束CRM退费交易");
					return msgRtn;
				}
				
				String bankID = logHis.getOuterDomain();
//				String bankID = logHis.getBankId();
				if(StringUtils.isBlank(bankID)){
					logger.warn("移动渠道缴费退费,原交易未发往Bank进行扣款:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					log.warn("移动渠道缴费退费,原交易未发往Bank进行扣款:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A04.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A04.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);
					
					msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					rspVo.setOriReqSys(reqVo.getOriReqSys());
					rspVo.setOriTransactionID(reqVo.getOriTransactionID());
					rspVo.setOriActionDate(reqVo.getOriActionDate());
					rspVo.setRspCode(RspCodeConstant.Crm.CRM_3A04.getValue());
					rspVo.setRspInfo(RspCodeConstant.Crm.CRM_3A04.getDesc());
					rspVo.setTransactionID(reqVo.getTransactionID());
					msgRtn.setBody(rspVo);
					
					logger.debug("结束CRM退费交易");
					return msgRtn;
				}
				ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(logHis.getIdValue());
				String  orgFls = offOrgTrans(paramData.getMsgSender(),bankID,mainOrgId,paramData.getTransCode().getTransCode(), 
						provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
				if(orgFls != null){
					logger.warn("移动渠道缴费退费,内部流水intTxnSeq:{},发起方机构:{},落地方机构:{},第三方方机构:{},业务代码:{}服务的权限关闭。",
							new Object[]{transIDH, paramData.getMsgSender(),bankID,mainOrgId,paramData.getTransCode().getTransCode()});
					log.warn("移动渠道缴费退费,内部流水intTxnSeq:{},发起方机构:{},落地方机构:{},第三方方机构:{},业务代码:{}服务的权限关闭。",
							new Object[]{transIDH, paramData.getMsgSender(),bankID,mainOrgId,paramData.getTransCode().getTransCode()});
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A35.getDesc()+orgFls);
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);

					msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					rspVo.setOriReqSys(reqVo.getOriReqSys());
					rspVo.setOriTransactionID(reqVo.getOriTransactionID());
					rspVo.setOriActionDate(reqVo.getOriActionDate());
					rspVo.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
					rspVo.setRspInfo(RspCodeConstant.Crm.CRM_3A35.getDesc());
					rspVo.setTransactionID(reqVo.getTransactionID());
					msgRtn.setBody(rspVo);
					
					logger.debug("结束CRM退费交易");
					return msgRtn;
				}
				
				addInitLs(txnLog, logHis);
				logger.info("移动渠道缴费退费,成功添加到交易流水表 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{txnLog.getIntTxnSeq(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender(),logHis.getIdValue()});
				log.info("移动渠道缴费退费,成功添加到交易流水表 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{txnLog.getIntTxnSeq(),reqVo.getTransactionID(),transIDH,
						paramData.getMsgSender(),logHis.getIdValue()});

				if (CommonConstant.TxnStatus.TxnSuccess.toString().equals(logHis.getStatus())
						|| CommonConstant.TxnStatus.TxnFail.toString().equals(logHis.getStatus())) {
					logger.info("移动渠道缴费退费,原交易状态为00/01向CRM发起冲正 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					log.info("移动渠道缴费退费,原交易状态为00/01向CRM发起冲正 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					
					 if(!(CommonConstant.BussType.OnlineConsumeBus.toString().equals(logHis.getBussType())||CommonConstant.BussType.OnlineChargeBus.toString().equals(logHis.getBussType()))){
						 logger.warn("移动渠道缴费退费,原交易非缴费交易 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
									new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
									paramData.getMsgSender(),logHis.getIdValue()});
						 log.warn("移动渠道缴费退费,原交易非缴费交易 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
									new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
									paramData.getMsgSender(),logHis.getIdValue()});
						 
						 msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						 msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						 msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
						 rspVo.setOriReqSys(reqVo.getOriReqSys());
						 rspVo.setOriTransactionID(reqVo.getOriTransactionID());
						 rspVo.setOriActionDate(reqVo.getOriActionDate());
						 rspVo.setRspCode(RspCodeConstant.Crm.CRM_4A02.getValue());
						 rspVo.setRspInfo(RspCodeConstant.Crm.CRM_4A02.getDesc());
						 rspVo.setTransactionID(reqVo.getTransactionID());
						 msgRtn.setBody(rspVo);
						
						 txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						 txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						 txnLog.setChlRspType(msgRtn.getRspType());
						 txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A02.getValue());
						 txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A02.getDesc());
						 txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						 txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						 DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						 upayCsysTxnLogService.modify(txnLog);
						 
						 logger.debug("结束CRM退费交易");
						 return msgRtn;
					 }
					 logger.info("移动渠道缴费退费,原交易是缴费类交易 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
					 log.info("移动渠道缴费退费,原交易是缴费类交易 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
					 
					 if(CommonConstant.YesOrNo.Yes.toString().equals(logHis.getRefundFlag())) {
						logger.warn("移动渠道缴费退费,原交易已退费 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
									new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
									paramData.getMsgSender(),logHis.getIdValue()});
						log.warn("移动渠道缴费退费,原交易已退费:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
									new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
									paramData.getMsgSender(),logHis.getIdValue()});
						msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
						rspVo.setOriReqSys(reqVo.getOriReqSys());
						rspVo.setOriTransactionID(reqVo.getOriTransactionID());
						rspVo.setOriActionDate(reqVo.getOriActionDate());
						rspVo.setRspCode(RspCodeConstant.Crm.CRM_3A15.getValue());// 3A15，-该笔交易已完成退费
						rspVo.setRspInfo(RspCodeConstant.Crm.CRM_3A15.getDesc());
						rspVo.setTransactionID(reqVo.getTransactionID());
						msgRtn.setBody(rspVo);

						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setChlRspType(msgRtn.getRspType());
						txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A15.getValue());
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A15.getDesc());
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						
						logger.debug("结束CRM退费交易");
						return msgRtn;
					}
					logger.info("移动渠道缴费退费,原交易未退费 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
					log.info("移动渠道缴费退费,原交易未退费 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
					if (CommonConstant.YesOrNo.Yes.toString().equals(logHis.getReverseFlag())) {
						logger.warn("移动渠道缴费退费,原交易已被冲正 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
					    log.warn("移动渠道缴费退费,原交易已被冲正 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
					    
						msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
						rspVo.setOriReqSys(reqVo.getOriReqSys());
						rspVo.setOriTransactionID(reqVo.getOriTransactionID());
						rspVo.setOriActionDate(reqVo.getOriActionDate());
						rspVo.setRspCode(RspCodeConstant.Crm.CRM_3A14.getValue());
						rspVo.setRspInfo(RspCodeConstant.Crm.CRM_3A14.getDesc());
						rspVo.setTransactionID(reqVo.getTransactionID());
						msgRtn.setBody(rspVo);

						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setChlRspType(msgRtn.getRspType());
						txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A14.getValue());
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A14.getDesc());
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						
						logger.debug("结束CRM退费交易");
						return msgRtn;
					}
					logger.info("移动渠道缴费退费,原交易未被冲正 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
				    log.info("移动渠道缴费退费,原交易未被冲正 :{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
				    
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("paramType", "duizhang");// 设置查询标识
					params.put("paramCode1", logHis.getSettleDate());// 结算日期
					params.put("status", "00");// 设置参数为有效
					params.put("paramValue", "00");// 设置为已对账
//					if (!CommonConstant.ReconciliationFlag.reconciliationFg.toString().equals(logHis.getReconciliationFlag())) {// 暂时这么写,以后写到常量中
					// 如果原交易中SettleDate字段为空，我们则认为该交易未对账
					if(logHis.getSettleDate() == null || upayBatParamService.findObj(params) == null) {
						logger.warn("移动渠道缴费退费,原交易未完成对账:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
					    log.warn("移动渠道缴费退费,原交易未完成对账:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
					    
						msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
						rspVo.setOriReqSys(reqVo.getOriReqSys());
						rspVo.setOriTransactionID(reqVo.getOriTransactionID());
						rspVo.setOriActionDate(reqVo.getOriActionDate());
						rspVo.setRspCode(RspCodeConstant.Crm.CRM_3A19.getValue());
						rspVo.setRspInfo(RspCodeConstant.Crm.CRM_3A19.getDesc());
						rspVo.setTransactionID(reqVo.getTransactionID());
						msgRtn.setBody(rspVo);

						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setChlRspType(msgRtn.getRspType());
						txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A19.getValue());
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A19.getDesc());
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						
						logger.debug("结束CRM退费交易");
						return msgRtn;
					}
					logger.info("移动渠道缴费退费,原交易已完成对账:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
				    log.info("移动渠道缴费退费,原交易已完成对账:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					
					CrmMsgVo msg19 = new CrmMsgVo();
					msg19.setTransCode(transCode);
					msg19.setVersion(ExcConstant.CRM_VERSION);
					msg19.setTestFlag(testFlag);
					msg19.setBIPCode(CommonConstant.Bip.Bis17.getValue());
					msg19.setActivityCode(CommonConstant.CrmTrans.Crm08.getValue());
					msg19.setActionCode(CommonConstant.ActionCode.Requset.getValue());
					msg19.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
					msg19.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
					msg19.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
					//route值取号码
					msg19.setRouteValue(logHis.getIdValue());
					msg19.setSessionID(transIDH);
					msg19.setTransIDO(transIDH);
					msg19.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
					msg19.setMsgSender(CommonConstant.OrgDomain.UPSS.getValue());
					msg19.setMsgReceiver(mainOrgId);
					
					CrmReverseMsgReqVo reqs = new CrmReverseMsgReqVo();
					reqs.setOriReqSys(logHis.getRcvDomain());
					reqs.setOriTransactionID(logHis.getRcvOprId());
					reqs.setOriActionDate(reqVo.getOriActionDate());
					
//					reqs.setTransactionID(Serial.genSerialNos(CommonConstant.Sequence.OprId.toString()));
					//TransactionID设置成32位
					reqs.setTransactionID(Serial.genSerialNum(CommonConstant.Sequence.OprId.toString()));
					reqs.setRevokeReason(reqVo.getRevokeReason());
					reqs.setSettleDate(StrUtil.subString(msgVo.getTransIDOTime(), 0, 8));
					msg19.setBody(reqs);
					
					txnLog.setRcvTransId(transIDH);
					txnLog.setRcvTransTm(transIDHTime);
					txnLog.setRcvTransDt(intTxnDate);
					txnLog.setRcvOprId(reqs.getTransactionID());
					txnLog.setRcvOprDt(txnLog.getIntTxnDate());
					txnLog.setRcvOprTm(txnLog.getIntTxnTime());
					txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
					txnLog.setRcvActivityCode(msg19.getActivityCode());
					txnLog.setRcvDomain(mainOrgId);
					//设置接收方渠道标识 by xuyunbo 20131203
					txnLog.setReqCnlType(CommonConstant.CnlType.CmccOwn.getValue());

					logger.info("移动渠道缴费退费,begin 核心向CRM转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					log.info("移动渠道缴费退费,begin 核心向CRM转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					Map<String, Object> pars = new HashMap<String, Object>();
					CrmMsgVo crmMsgVoRtn = crmReverseBus.execute(msg19, pars,txnLog, null);
					logger.info("移动渠道缴费退费,end 核心向CRM转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					log.info("移动渠道缴费退费,end 核心向CRM转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});

					modifyRcvLs(txnLog, transIDH, intTxnDate, transIDHTime, msg19, mainOrgId, crmMsgVoRtn, reqs);
					logger.info("移动渠道缴费退费,修改添加交易流水记录:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{txnLog.getIntTxnSeq(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					log.info("移动渠道缴费退费,修改添加交易流水记录:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{txnLog.getIntTxnSeq(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});

					//20130905  14:58  添加返回报文体为空和返回超时的情况
					String rtBodyStr = (String) crmMsgVoRtn.getBody();
					if (crmMsgVoRtn.getBody()==null || crmMsgVoRtn.getBody().equals("")) {
						String errCode = RspCodeConstant.Crm.CRM_5A06.getValue();
						errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
						logger.warn("移动渠道缴费退费,应答报文体为空:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM应答码:{},CRM应答描述:{}" ,
								new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue(),crmMsgVoRtn.getRspCode(),crmMsgVoRtn.getRspDesc()});
						log.warn("移动渠道缴费退费,应答报文体为空:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM应答码:{},CRM应答描述:{}" ,
								new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue(),crmMsgVoRtn.getRspCode(),crmMsgVoRtn.getRspDesc()});
						msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
						msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						rspVo.setOriReqSys(reqVo.getOriReqSys());
						rspVo.setOriTransactionID(reqVo.getOriTransactionID());
						rspVo.setOriActionDate(reqVo.getOriActionDate());
						rspVo.setRspCode(errCode);
						rspVo.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
						rspVo.setTransactionID(reqVo.getTransactionID());
						msgRtn.setBody(rspVo);

						txnLog.setChlRspType(msgRtn.getRspType());
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setChlSubRspCode(errCode);
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						
						logger.debug("结束CRM退费交易");
						return msgRtn;
					}
					
					if(RspCodeConstant.Upay.UPAY_U99998.getValue().equals(crmMsgVoRtn.getRspCode())){
						logger.warn("移动渠道缴费退费,CRM前置超时!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{}" ,
								new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
						log.warn("移动渠道缴费退费,CRM前置超时!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{}" ,
								new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
						msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
						msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						rspVo.setOriReqSys(reqVo.getOriReqSys());
						rspVo.setOriTransactionID(reqVo.getOriTransactionID());
						rspVo.setOriActionDate(reqVo.getOriActionDate());
						rspVo.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
						rspVo.setRspInfo(RspCodeConstant.Crm.CRM_5A07.getDesc());
						rspVo.setTransactionID(reqVo.getTransactionID());
						msgRtn.setBody(rspVo);

						txnLog.setChlRspType(msgRtn.getRspType());
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						
						logger.debug("结束CRM退费交易");
						return msgRtn;
					}
					
					txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
					txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
					
					CrmReverseMsgResVo resVo = new CrmReverseMsgResVo();
					MsgHandle.unmarshaller(resVo,(String) crmMsgVoRtn.getBody());
					if (!RspCodeConstant.Crm.CRM_0000.getValue().equals(resVo.getRspCode())) {
						String errCode = resVo.getRspCode();
						errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
						logger.warn("移动渠道缴费退费,CRM应答失败!流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM报文体应答码:{},CRM报文体应答描述:{}" ,
								new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue(),resVo.getRspCode(),resVo.getRspInfo()});
						log.warn("移动渠道缴费退费,CRM应答失败!流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM报文体应答码:{},CRM报文体应答描述:{}" ,
								new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue(),resVo.getRspCode(),resVo.getRspInfo()});
						
						txnLog.setRcvSubRspCode(errCode);
						txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
						txnLog.setChlSubRspCode(errCode);
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);

						msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
						rspVo.setOriReqSys(reqVo.getOriReqSys());
						rspVo.setOriTransactionID(reqVo.getOriTransactionID());
						rspVo.setOriActionDate(reqVo.getOriActionDate());
						rspVo.setRspCode(errCode);
						rspVo.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
						rspVo.setTransactionID(reqVo.getTransactionID());
						msgRtn.setBody(rspVo);
						
						logger.debug("结束CRM退费交易");
						return msgRtn;
					} else {
						logger.info("移动渠道缴费退费,CRM应答成功!流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM报文体应答码:{},CRM报文体应答描述:{}" ,
								new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue(),resVo.getRspCode(),resVo.getRspInfo()});
						log.succ("移动渠道缴费退费,CRM应答成功!流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM报文体应答码:{},CRM报文体应答描述:{}" ,
								new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue(),resVo.getRspCode(),resVo.getRspInfo()});
						upayCsysPayLimitService.modifyLimtsDs(logHis);// 需要先扣除限额表金额(本次冲正的金额)

						txnLog.setRcvSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
						txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);
						
						logger.info("移动渠道缴费退费,intTxnSeq:{},拼凑发往Bank的报文",transIDH);
						BankMsgVo msgReq = new BankMsgVo();
						msgReq.setActivityCode(CommonConstant.BankTrans.Bank04.getValue());
						msgReq.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
						msgReq.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
						msgReq.setReqDate(intTxnDate);
						msgReq.setReqTransID(transIDH);
						msgReq.setReqDateTime(transIDHTime);
						msgReq.setActionCode(CommonConstant.ActionCode.Requset.getValue());
						msgReq.setRcvSys(logHis.getBankId());
						
						BankRefundReqVo refundReqVo = new BankRefundReqVo();
						refundReqVo.setOriReqSys(ExcConstant.BANK_REQ_SYS);
						refundReqVo.setOriReqTransID(logHis.getIntTxnSeq());
						
						refundReqVo.setOriReqDate(logHis.getIntTxnDate());
						refundReqVo.setOriTransType(CommonConstant.TransType.TransMs.toString());
						refundReqVo.setRevokeReason(reqVo.getRevokeReason());
						msgReq.setBody(refundReqVo);
						
						txnLog.setOuterVersion(ExcConstant.CRM_VERSION);
						txnLog.setBussChl(transCode.getBussChl());// 业务渠道
						txnLog.setOuterActivityCode(msgReq.getActivityCode());
						txnLog.setOuterDomain(logHis.getBankId());
						txnLog.setOuterRouteType(msgRtn.getRouteType());
						txnLog.setOuterRouteVal(msgRtn.getRouteValue());
						txnLog.setOuterSessionId(msgRtn.getSessionID());
						DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
						upayCsysTxnLogService.modify(txnLog);

						logger.info("移动渠道缴费退费,begin 核心向Bank转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
						log.info("移动渠道缴费退费,begin 核心向Bank转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
						Map<String, Object> pas = new HashMap<String, Object>();
						BankMsgVo send2Bank = bankRefundBus.execute(msgReq,pas, txnLog, null);
						txnLog.setSettleDate(send2Bank.getRcvDate());
						logger.info("移动渠道缴费退费,end 核心向Bank转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});
						log.info("移动渠道缴费退费,end 核心向Bank转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
								new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
								paramData.getMsgSender(),logHis.getIdValue()});

						modifyOuterLs(txnLog, send2Bank);
						logger.info("移动渠道缴费退费,修改添加交易流水记录:{},UPAY流水:{},发送银行:{}" ,
								new Object[]{txnLog.getIntTxnSeq(),transIDH,logHis.getBankId()});
						log.info("移动渠道缴费退费,修改添加交易流水记录:{},UPAY流水:{},发送银行:{}" ,
								new Object[]{txnLog.getIntTxnSeq(),transIDH,logHis.getBankId()});

						if (!RspCodeConstant.Bank.BANK_020A00.getValue().equals(send2Bank.getRspCode())) {
							String errCode = send2Bank.getRspCode();
							errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
							logger.warn("移动渠道缴费退费,Bank应答失败!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
									new Object[]{send2Bank.getRcvTransID(),transIDH,
									logHis.getBankId(),logHis.getIdValue(),send2Bank.getRspCode(),send2Bank.getRspDesc()});
							log.warn("移动渠道缴费退费,Bank应答失败!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
									new Object[]{send2Bank.getRcvTransID(),transIDH,
									logHis.getBankId(),logHis.getIdValue(),send2Bank.getRspCode(),send2Bank.getRspDesc()});
							
							DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
							upayCsysPayLimitService.modifyLimtsAs(logHis);// 银行冲正失败的时候,需要将减去的金额回退
							
							msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
							msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
							msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
							rspVo.setOriReqSys(reqVo.getOriReqSys());
							rspVo.setOriTransactionID(reqVo.getOriTransactionID());
							rspVo.setOriActionDate(reqVo.getOriActionDate());
							rspVo.setRspCode(errCode);
							rspVo.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
							rspVo.setTransactionID(reqVo.getTransactionID());
							msgRtn.setBody(rspVo);

							txnLog.setOuterRspCode(send2Bank.getRspCode());
							txnLog.setOuterRspDesc(send2Bank.getRspDesc());
							txnLog.setOuterSubRspCode(send2Bank.getRspCode());
							txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
							txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
							txnLog.setChlSubRspCode(errCode);
							txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
							txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
							txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
							DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
							upayCsysTxnLogService.modify(txnLog);
							
							logger.debug("结束CRM退费交易");
							return msgRtn;
						} else {
							logger.info("移动渠道缴费退费,Bank应答成功!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
									new Object[]{send2Bank.getRcvTransID(),transIDH,
									logHis.getBankId(),logHis.getIdValue(),send2Bank.getRspCode(),send2Bank.getRspDesc()});
							log.succ("移动渠道缴费退费,Bank应答成功!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
									new Object[]{send2Bank.getRcvTransID(),transIDH,
									logHis.getBankId(),logHis.getIdValue(),send2Bank.getRspCode(),send2Bank.getRspDesc()});
							msgRtn.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
							msgRtn.setRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
							msgRtn.setRspType(CommonConstant.CrmRspType.Success.toString());
							rspVo.setOriReqSys(reqVo.getOriReqSys());
							rspVo.setOriTransactionID(reqVo.getOriTransactionID());
							rspVo.setOriActionDate(reqVo.getOriActionDate());
							rspVo.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
							rspVo.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
							rspVo.setTransactionID(reqVo.getTransactionID());
							rspVo.setSettleDate(send2Bank.getRcvDate());
							msgRtn.setBody(rspVo);

							modifyNewLogs(txnLog, logHis, msgRtn, msgVo, transCode, msgReq);
							logger.info("移动渠道缴费退费,成功修改新增流水状态:{},UPAY流水:{},发送银行:{},手机号码:{}" ,
									new Object[]{txnLog.getIntTxnSeq(),transIDH,logHis.getBankId(),logHis.getIdValue()});
							log.info("移动渠道缴费退费,成功修改新增流水状态:{},UPAY流水:{},发送银行:{},手机号码:{}" ,
									new Object[]{txnLog.getIntTxnSeq(),transIDH,logHis.getBankId(),logHis.getIdValue()});
							
							modifyOldLogsStatus(logHis, hisLog);
							logger.info("移动渠道缴费退费,成功修改原纪录:{},UPAY流水:{},发送银行:{},手机号码:{}" ,
									new Object[]{txnLog.getIntTxnSeq(),transIDH,logHis.getBankId(),logHis.getIdValue()});
							log.info("移动渠道缴费退费,成功修改原纪录:{},UPAY流水:{},发送银行:{},手机号码:{}" ,
									new Object[]{txnLog.getIntTxnSeq(),transIDH,logHis.getBankId(),logHis.getIdValue()});
							
							logger.debug("结束执行CRM退费交易");
							
							return msgRtn;
						}
					}
				} else {
					logger.warn("移动渠道缴费退费,原交易状态不是00/01:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					log.warn("移动渠道缴费退费,原交易状态不是00/01:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqVo.getOriTransactionID(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),logHis.getIdValue()});
					
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A30.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A30.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);

					msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					rspVo.setOriReqSys(reqVo.getOriReqSys());
					rspVo.setOriTransactionID(reqVo.getOriTransactionID());
					rspVo.setOriActionDate(reqVo.getOriActionDate());
					rspVo.setRspCode(RspCodeConstant.Crm.CRM_4A30.getValue());
					rspVo.setRspInfo(RspCodeConstant.Crm.CRM_4A30.getDesc());
					rspVo.setTransactionID(reqVo.getTransactionID());
					msgRtn.setBody(rspVo);
					
					logger.debug("结束CRM退费交易");
					return msgRtn;
				}
/*			}	*/
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			logger.error("移动渠道缴费退费,运行异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},异常码:{}" ,
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
					paramData.getMsgSender(),errCode});
			log.error("移动渠道缴费退费,运行异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
					paramData.getMsgSender()});
			logger.error("移动渠道缴费退费,运行异常:",e);
			msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			rspVo.setOriReqSys(reqVo.getOriReqSys());
			rspVo.setOriTransactionID(reqVo.getOriTransactionID());
			rspVo.setOriActionDate(reqVo.getOriActionDate());
			rspVo.setRspCode(errCode);
			rspVo.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			rspVo.setTransactionID(reqVo.getTransactionID());
			msgRtn.setBody(rspVo);

			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(msgRtn.getRspType());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
			upayCsysTxnLogService.modify(txnLog);
			
			logger.debug("结束CRM退费交易");
			return msgRtn;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			logger.error("移动渠道缴费退费,系统业务异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
					paramData.getMsgSender()});
			log.error("移动渠道缴费退费,系统业务异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
					paramData.getMsgSender()});
			logger.error("移动渠道缴费退费,业务异常:",e);
			msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			rspVo.setOriReqSys(reqVo.getOriReqSys());
			rspVo.setOriTransactionID(reqVo.getOriTransactionID());
			rspVo.setOriActionDate(reqVo.getOriActionDate());
			rspVo.setRspCode(errCode);
			rspVo.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			rspVo.setTransactionID(reqVo.getTransactionID());
			msgRtn.setBody(rspVo);

			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspType(msgRtn.getRspType());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
			upayCsysTxnLogService.modify(txnLog);
			
			logger.debug("移动渠道缴费退费,结束CRM退费交易");
			return msgRtn;
		} catch (Exception e) {
			String errCode = RspCodeConstant.Crm.CRM_5A06.getValue();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			logger.error("移动渠道缴费退费,系统异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
					paramData.getMsgSender()});
			log.error("移动渠道缴费退费,系统异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),transIDH,
					paramData.getMsgSender()});
			logger.error("移动渠道缴费退费,未知异常:",e);
			msgRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			msgRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			rspVo.setOriReqSys(reqVo.getOriReqSys());
			rspVo.setOriTransactionID(reqVo.getOriTransactionID());
			rspVo.setOriActionDate(reqVo.getOriActionDate());
			rspVo.setRspCode(errCode);
			
			//注释掉输出到应答报文的错误信息(该信息可能包含SQL异常) 20131213 modify by weiyi
//			String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_230?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_230);
//			rspVo.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc()+":"+errDesc);
			
			rspVo.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc());
			rspVo.setTransactionID(reqVo.getTransactionID());
			msgRtn.setBody(rspVo);

			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode)+":"+e.getMessage());
			DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
			upayCsysTxnLogService.modify(txnLog);
			
			logger.debug("结束CRM退费交易");
			return msgRtn;
		}
	}

	private void initLog(UpayCsysTxnLog txnLog, Long seqValue, String transIDH,String intTxnDate, String transIDHTime, CrmMsgVo msgVo,
			UpayCsysTransCode transCode, CrmMsgVo msgRtn, CrmRefundReqVo reqVo) {
		
		txnLog.setSeqId(seqValue);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setBussChl(transCode.getBussChl());// 业务渠道
		txnLog.setBussType(transCode.getBussType());
//		txnLog.setTxnCat(transCode.getTxnCat());
		txnLog.setSettleDate(StrUtil.subString(transIDHTime, 0, 8));
		txnLog.setReqVersion(msgVo.getVersion());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReqBipCode(transCode.getReqBipCode());
		txnLog.setReqActivityCode(msgVo.getActivityCode());
		txnLog.setReqDomain(msgVo.getMsgSender());
		txnLog.setReqRouteType(msgVo.getRouteType());
		txnLog.setReqRouteVal(msgVo.getRouteValue());
		txnLog.setReqSessionId(msgVo.getSessionID());
		txnLog.setReqTransId(msgVo.getTransIDO());
		txnLog.setReqTransTm(msgVo.getTransIDOTime());
		txnLog.setReqTransDt(StrUtil.subString(msgVo.getTransIDOTime(), 0, 8));
		txnLog.setReqTranshId(transIDH);
		txnLog.setReqTranshDt(intTxnDate);
		txnLog.setReqTranshTm(transIDHTime);
		txnLog.setReqOprId(reqVo.getTransactionID());// 重新打开20130702 15:47
		txnLog.setReqOprDt(StrUtil.subString(msgVo.getTransIDOTime(), 0, 8));
		txnLog.setReqOprTm(msgVo.getTransIDOTime());
		txnLog.setOriOrgId(reqVo.getOriReqSys());
		txnLog.setOriOprTransId(reqVo.getOriTransactionID());
		txnLog.setOriReqDate(reqVo.getOriActionDate());
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
		upayCsysTxnLogService.add(txnLog);
	}
	
	private void addInitLg(UpayCsysTxnLog txnLog , UpayCsysTxnLog upayLog){
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
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogService.modify(txnLog);
	}
	
	private void addInitLs(UpayCsysTxnLog txnLog , UpayCsysTxnLogHis logHis){
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
	
	private void modifyRcvLs(UpayCsysTxnLog txnLog, String transIDH,String intTxnDate, String transIDHTime,
			CrmMsgVo msg19, String mainOrgId, CrmMsgVo crmMsgVoRtn, CrmReverseMsgReqVo reqs){
//		txnLog.setRcvTransId(transIDH);
//		txnLog.setRcvTransTm(transIDHTime);
//		txnLog.setRcvTransDt(intTxnDate);
//		txnLog.setRcvOprId(reqs.getTransactionID());
//		txnLog.setRcvOprDt(txnLog.getIntTxnDate());
//		txnLog.setRcvOprTm(txnLog.getIntTxnTime());
//		txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
//		txnLog.setRcvActivityCode(msg19.getActivityCode());
//		txnLog.setRcvDomain(mainOrgId);
		txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
		txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
		txnLog.setRcvRouteType(crmMsgVoRtn.getRouteType());
		txnLog.setRcvRouteVal(crmMsgVoRtn.getRouteValue());
		txnLog.setRcvSessionId(crmMsgVoRtn.getSessionID());
		upayCsysTxnLogService.modify(txnLog);
		
	}
	
	private void modifyOuterLs(UpayCsysTxnLog txnLog, BankMsgVo send2Bank){
		txnLog.setSettleDate(send2Bank.getRcvDate());
		txnLog.setOuterTransId(send2Bank.getRcvTransID());
		txnLog.setOuterTransDt(send2Bank.getRcvDate());
		txnLog.setOuterTransTm(send2Bank.getRcvDateTime());
		txnLog.setOuterTranshId(send2Bank.getRcvTransID());
		txnLog.setOuterTranshDt(send2Bank.getRcvDate());
		txnLog.setOuterTranshTm(send2Bank.getRcvDateTime());
		txnLog.setOuterOprId(send2Bank.getRcvTransID());// 外部方操作流水号
		txnLog.setOuterOprDt(send2Bank.getRcvDate());// 外部方操作请求日期
		txnLog.setOuterOprTm(send2Bank.getRcvDateTime());// 外部方操作请求时间
		upayCsysTxnLogService.modify(txnLog);
	}
	
	private void modifySuccNewAndOldLog(UpayCsysTxnLog txnLog, UpayCsysTxnLog upayLog, UpayCsysTxnLog logg,
			CrmMsgVo msgRtn, BankMsgVo msgReq, UpayCsysTransCode transCode, CrmMsgVo msgVo){
//		txnLog.setOuterVersion(ExcConstant.CRM_VERSION);
//		txnLog.setBussChl(transCode.getBussChl());// 业务渠道
//		txnLog.setOuterActivityCode(msgReq.getActivityCode());
//		txnLog.setOuterDomain(msgVo.getOrigDomain());
//		txnLog.setOuterRouteType(msgRtn.getRouteType());
//		txnLog.setOuterRouteVal(msgRtn.getRouteValue());
//		txnLog.setOuterSessionId(msgRtn.getSessionID());
		txnLog.setOuterRspCode(RspCodeConstant.Crm.CRM_0000.getValue());// 接收方返回码
		txnLog.setOuterRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		txnLog.setOuterSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		txnLog.setChlRspCode(msgRtn.getRspCode());
		txnLog.setChlRspDesc(msgRtn.getRspDesc());
		txnLog.setChlRspType(msgRtn.getRspType());
		txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		txnLog.setPayAmt(-upayLog.getPayAmt());
		txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());

		logg.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());// 退费标识
		logg.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		logg.setSeqId(upayLog.getSeqId());
		logg.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogService.modifyLog(txnLog, logg);// 同时更新原交易和新增的交易
	}
	
	private void modifyNewLogs(UpayCsysTxnLog txnLog, UpayCsysTxnLogHis logHis, CrmMsgVo msgRtn,
			CrmMsgVo msgVo, UpayCsysTransCode transCode, BankMsgVo msgReq){
		
		txnLog.setOuterRspCode(RspCodeConstant.Crm.CRM_0000.getValue());// 接收方返回码
		txnLog.setOuterRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		txnLog.setOuterSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());

		txnLog.setChlRspCode(msgRtn.getRspCode());
		txnLog.setChlRspDesc(msgRtn.getRspDesc());
		txnLog.setChlRspType(msgRtn.getRspType());
		txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		txnLog.setPayAmt(-logHis.getPayAmt());// 成功返回,需要设置成负值TODO
		txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());// 正常
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogService.modify(txnLog);// 更新新增流水
	}
	
	private void modifyOldLogsStatus(UpayCsysTxnLogHis logHis,UpayCsysTxnLogHis hisStlLog){
		// 修改UPAY_CSYS_TXN_LOG_HIS_STL
		hisStlLog.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());// 退费标识
		hisStlLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisStlLog.setSeqId(logHis.getSeqId());
		hisStlLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_HIS);
		upayCsysTxnLogHisService.modifyHisStl(hisStlLog);// 更新原纪录
		
		// 修改UPAY_CSYS_TXN_LOG_HIS
		UpayCsysTxnLogHis hisLog = new UpayCsysTxnLogHis();
		hisLog.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());// 退费标识
		hisLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisLog.setSeqId(logHis.getSeqId());
		hisLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_HIS);
		upayCsysTxnLogHisService.modify(hisLog);// 更新原纪录
	}
	
}