package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DataSourceInstances;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysPayLimitService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.bank.BankRefundBus;
import com.huateng.cmupay.jms.business.crm.CrmReverseBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
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
 * 移动渠道缴费冲正
 * 
 * @author hdm 本地数据库数据的状态,不一定是最终状态,需要向CRM和Bank发起冲正或返销 1、客服系统向统一支付系统发送冲正请求
 *         2、核心平台对冲正交易请求做冲正预判 2.1
 *         原交易的状态(status)是否是00/01：若是,再进行一些判断,如原交易是否存在,退费的交易是否已冲正等 2.1.1.1
 *         核心平台向CRM发起返销请求, 2.1.1.1.1 当CRM返回成功的时候,UPSS再向Bank发起冲正请求:
 *         Bank返回成功的时候,则向CRM返回成功 2.2. 若否,则直接返回交易处理中
 *         当天的冲正
 */
@Controller("crmReverseAction")
@Scope("prototype")
public class CrmReverseAction extends AbsBaseAction<CrmMsgVo, CrmMsgVo> {
	@Autowired
	private CrmReverseBus crmReverseBus;
	@Autowired
	private BankRefundBus bankRefundBus;
	@Autowired
	private IUpayCsysPayLimitService upayCsysPayLimitService;

	@Override
	public CrmMsgVo execute(CrmMsgVo paramData) throws AppBizException {
		
		logger.debug("开始执行CRM冲正交易");
		CrmMsgVo crmMsgVo = paramData;
		CrmReverseMsgReqVo reqMsg = new CrmReverseMsgReqVo();
		CrmMsgVo msgVoRtn = crmMsgVo;
		CrmReverseMsgResVo resBody = new CrmReverseMsgResVo();
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		UpayCsysTransCode transCode = crmMsgVo.getTransCode();
		
		String transIDH = crmMsgVo.getTxnSeq();
		String transIDHTime = crmMsgVo.getTxnTime();
		String intTxnDate = crmMsgVo.getTxnDate();
		Long seqId = crmMsgVo.getSeqId();
		txnLog.setSettleDate(intTxnDate);
		try {
			MsgHandle.unmarshaller(reqMsg, (String) crmMsgVo.getBody());
			
			if(StringUtils.isBlank(reqMsg.getTransactionID())){
				msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
				msgVoRtn.setRouteType(CommonConstant.RouteType.RouteProvince.getValue());
				msgVoRtn.setRouteValue(crmMsgVo.getRouteValue());
				msgVoRtn.setTransIDH(transIDH);
				msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
				resBody.setOriReqSys(reqMsg.getOriReqSys());
				resBody.setOriActionDate(reqMsg.getOriActionDate());
				resBody.setOriTransactionID(reqMsg.getOriTransactionID());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A99.getDesc() +" TransactionID不能为空 TransactionID格式不正确");
				resBody.setTransactionID(reqMsg.getTransactionID());
				msgVoRtn.setBody(resBody);
				
				logger.debug("结束CRM冲正交易");
				return msgVoRtn;
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqDomain", crmMsgVo.getMsgSender());
			param.put("reqOprId", reqMsg.getTransactionID());
			UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService.findObj(param);
			if (upayCsysTxnLog != null) {
				logger.warn("移动渠道缴费冲正,操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},",
						new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				log.warn("移动渠道缴费冲正,操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},",
						new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resBody.setOriReqSys(reqMsg.getOriReqSys());
				resBody.setOriActionDate(reqMsg.getOriActionDate());
				resBody.setOriTransactionID(reqMsg.getOriTransactionID());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc());
				resBody.setTransactionID(reqMsg.getTransactionID());
				msgVoRtn.setBody(resBody);
				
				logger.debug("结束CRM冲正交易");
				return msgVoRtn;
			}

			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			msgVoRtn.setRouteType(CommonConstant.RouteType.RouteProvince.getValue());
			msgVoRtn.setRouteValue(crmMsgVo.getRouteValue());
			msgVoRtn.setTransIDH(transIDH);
			msgVoRtn.setTransIDHTime(StrUtil.subString(transIDHTime, 0, 14));
			resBody.setOriReqSys(reqMsg.getOriReqSys());
			resBody.setOriActionDate(reqMsg.getOriActionDate());
			resBody.setOriTransactionID(reqMsg.getOriTransactionID());

			logger.debug("移动渠道缴费冲正,begin 添加CRM渠道冲正交易流水:{} ,内部交易流水号intTxnSeq:{}", 
					crmMsgVo.getTransIDO(), transIDH);
			initLog(txnLog, seqId, transIDH, intTxnDate, transIDHTime, crmMsgVo, transCode, msgVoRtn, reqMsg);
			logger.debug("移动渠道缴费冲正,end   添加CRM渠道冲正交易流水:{} ,内部交易流水号intTxnSeq:{}",  
					crmMsgVo.getTransIDO(), transIDH);
			
			String checkrtn = validateModel(reqMsg);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				logger.warn("移动渠道缴费冲正,格式校验失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},错误字段:{}" ,
						new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),checkrtn});
				log.warn("移动渠道缴费冲正,格式校验失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},错误字段:{}" ,
						new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),checkrtn});
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A99.getDesc()+ checkrtn);
				upayCsysTxnLogService.modify(txnLog);

				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A99.getDesc()+ checkrtn);
				resBody.setTransactionID(reqMsg.getTransactionID());
				msgVoRtn.setBody(resBody);
				
				logger.debug("结束CRM冲正交易");
				return msgVoRtn;
			}

			String reqDate = StrUtil.subString(crmMsgVo.getTransIDOTime(), 0, 8);
			if (!intTxnDate.equals(reqMsg.getOriActionDate())|| !reqMsg.getOriActionDate().equals(reqDate)) {
				logger.warn("移动渠道缴费冲正,原交易非当日交易不能被冲正:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqDate,reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				log.warn("移动渠道缴费冲正,原交易非当日交易不能被冲正:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqDate,reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A05.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A05.getDesc()+ "," + "报文头日期与报文体日期不一致");
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_3A05.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A05.getDesc() + ","+ "报文头日期与报文体日期不一致");
				resBody.setTransactionID(reqMsg.getTransactionID());
				msgVoRtn.setBody(resBody);
				
				logger.debug("结束CRM冲正交易");
				return msgVoRtn;
			}

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reqDomain", reqMsg.getOriReqSys());// 原交易发起方系统标识
			params.put("reqOprId", reqMsg.getOriTransactionID());// 原交易发起方流水号
			params.put("reqTransDt", reqMsg.getOriActionDate());// 原交易发起方日期
			UpayCsysTxnLog upayLog = upayCsysTxnLogService.findObj(params);

			if (null == upayLog) {
				logger.warn("移动渠道缴费冲正,原交易不存在:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				log.warn("移动渠道缴费冲正,原交易不存在:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
						new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender()});
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A05.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A05.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setReqOprTm(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_4A05.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A05.getDesc());
				resBody.setTransactionID(reqMsg.getTransactionID());
				msgVoRtn.setBody(resBody);
				
				logger.debug("结束CRM冲正交易");
				return msgVoRtn;
			} else if (CommonConstant.TxnStatus.TxnSuccess.getValue().equals(upayLog.getStatus())
					|| CommonConstant.TxnStatus.TxnFail.getValue().equals(upayLog.getStatus())) {
				
//				String mainOrgId = SysMapCache.getProvCd(upayLog.getIdValue()).getSysCd();// 充值手机号码的归属省
				String mainOrgId = upayLog.getRcvDomain();// 充值手机号码的归属省
				if(StringUtils.isBlank(mainOrgId)){
					logger.warn("移动渠道缴费冲正,原交易未发往CRM进行充值:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					log.warn("移动渠道缴费冲正,原交易未发往CRM进行充值:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A04.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A04.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					
					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					resBody.setOriReqSys(reqMsg.getOriReqSys());
					resBody.setOriTransactionID(reqMsg.getOriTransactionID());
					resBody.setOriActionDate(reqMsg.getOriActionDate());
//					resBody.setRspCode(upayLog.getChlSubRspCode());
//					resBody.setRspInfo(upayLog.getChlSubRspDesc());
					resBody.setRspCode(RspCodeConstant.Crm.CRM_3A04.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A04.getDesc());
					resBody.setTransactionID(reqMsg.getTransactionID());
					msgVoRtn.setBody(resBody);
					
					logger.debug("结束CRM冲正交易");
					return msgVoRtn;
				}
//				boolean orgFlag = orgStatusCheck(mainOrgId);// 归属省,判断转发机构的权限
//				if (orgFlag == false) {
//					logger.info("落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
//							new Object[]{mainOrgId,reqMsg.getTransactionID(),transIDH,
//							paramData.getMsgSender(),upayLog.getIdValue()});
//					log.warn("落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
//							new Object[]{mainOrgId,reqMsg.getTransactionID(),transIDH,
//							paramData.getMsgSender(),upayLog.getIdValue()});
//					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
//					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
//					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A35.getDesc());
//					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
//					upayCsysTxnLogService.modify(txnLog);
//
//					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
//					resBody.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
//					resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A35.getDesc());
//					resBody.setTransactionID(reqMsg.getTransactionID());
//					msgVoRtn.setBody(resBody);
//					return msgVoRtn;
//				}
				String bankID = upayLog.getOuterDomain();
//				String bankID = upayLog.getBankId();
				if(StringUtils.isBlank(bankID)){
					logger.warn("移动渠道缴费冲正,原交易未发往Bank进行扣款:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					log.warn("移动渠道缴费冲正,原交易未发往Bank进行扣款:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A04.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A04.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					
					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					resBody.setOriReqSys(reqMsg.getOriReqSys());
					resBody.setOriTransactionID(reqMsg.getOriTransactionID());
					resBody.setOriActionDate(reqMsg.getOriActionDate());
					resBody.setRspCode(RspCodeConstant.Crm.CRM_3A04.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A04.getDesc());
//					resBody.setRspCode(upayLog.getChlSubRspCode());
//					resBody.setRspInfo(upayLog.getChlSubRspDesc());
					resBody.setTransactionID(reqMsg.getTransactionID());
					msgVoRtn.setBody(resBody);
					
					logger.debug("结束CRM冲正交易");
					return msgVoRtn;
				}
				
//				boolean bankFlag =isO2OTransOn(paramData.getMsgSender(),bankID,paramData.getTransCode().getTransCode());;
//				boolean orgFls = isO2OTransOn(paramData.getMsgSender(),bankID,mainOrgId,paramData.getTransCode().getTransCode());
				
				// 查询该交易的号码段属于移动还是联通电信的。
				ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(upayLog.getIdValue());
				String  orgFls = offOrgTrans(paramData.getMsgSender(),bankID,mainOrgId,paramData.getTransCode().getTransCode(),
						provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());

				if(orgFls != null){
					logger.warn("移动渠道缴费冲正,内部流水intTxnSeq:{},发起方机构:{},落地方机构:{},第三方方机构:{},业务代码:{}服务的权限关闭。",
							new Object[]{transIDH, paramData.getMsgSender(),bankID,mainOrgId,paramData.getTransCode().getTransCode()});
					
					log.warn("移动渠道缴费冲正,内部流水intTxnSeq:{},发起方机构:{},落地方机构:{},第三方方机构:{},业务代码:{}服务的权限关闭。",
							new Object[]{transIDH, paramData.getMsgSender(),bankID,mainOrgId,paramData.getTransCode().getTransCode()});
//					logger.info("落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
//							new Object[]{bankID,reqMsg.getTransactionID(),transIDH,
//							paramData.getMsgSender(),upayLog.getIdValue()});
//					log.warn("落地方机构服务的权限关闭:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
//							new Object[]{bankID,reqMsg.getTransactionID(),transIDH,
//							paramData.getMsgSender(),upayLog.getIdValue()});
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A35.getDesc()+orgFls);
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					resBody.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A35.getDesc());
					resBody.setTransactionID(reqMsg.getTransactionID());
					msgVoRtn.setBody(resBody);
					
					logger.debug("结束CRM冲正交易");
					return msgVoRtn;
				}
				
				addInitLog(txnLog, upayLog);
				logger.info("移动渠道缴费冲正,修改新增流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{txnLog.getIntTxnSeq(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				log.info("移动渠道缴费冲正,修改新增流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{txnLog.getIntTxnSeq(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				
				if (!CommonConstant.BussType.OnlineConsumeBus.getValue().equals(upayLog.getBussType())) {
					logger.warn("移动渠道缴费冲正,原交易非缴费类型不支持冲正:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					log.warn("移动渠道缴费冲正,原交易非缴费类型不支持冲正:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A02.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A02.getDesc()+ "," + "原交易非缴费类型,不支持冲正");
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					resBody.setRspCode(RspCodeConstant.Crm.CRM_4A02.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A02.getDesc()+ "," + "原交易非缴费类型,不支持冲正");
					resBody.setTransactionID(reqMsg.getTransactionID());
					msgVoRtn.setBody(resBody);
					
					logger.debug("结束CRM冲正交易");
					return msgVoRtn;
				}

				if (CommonConstant.YesOrNo.Yes.getValue().equals(upayLog.getReverseFlag())) {
					logger.warn("移动渠道缴费冲正,原交易不能再次冲正:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					log.warn("移动渠道缴费冲正,原交易不能再次冲正:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A14.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A14.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					resBody.setRspCode(RspCodeConstant.Crm.CRM_3A14.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_3A14.getDesc());
					resBody.setTransactionID(reqMsg.getTransactionID());
					msgVoRtn.setBody(resBody);
					
					logger.debug("结束CRM冲正交易");
					return msgVoRtn;
				}
				logger.info("移动渠道缴费冲正,原交易未被冲正:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				log.info("移动渠道缴费冲正,原交易未被冲正:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				
				CrmMsgVo msg19 = new CrmMsgVo();
				msg19.setTransCode(transCode);
				msg19.setVersion(ExcConstant.CRM_VERSION);
				msg19.setTestFlag(testFlag);//
				msg19.setBIPCode(CommonConstant.Bip.Bis16.getValue());
				msg19.setActivityCode(CommonConstant.CrmTrans.Crm08.getValue());
				msg19.setActionCode(CommonConstant.ActionCode.Requset.getValue());
				msg19.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
				msg19.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
				msg19.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
				msg19.setRouteValue(upayLog.getIdValue());
				msg19.setSessionID(transIDH);
				msg19.setTransIDO(transIDH);
				msg19.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
				msg19.setMsgSender(CommonConstant.OrgDomain.UPSS.getValue());
				msg19.setMsgReceiver(mainOrgId);
				
				CrmReverseMsgReqVo reqVo = new CrmReverseMsgReqVo();
				reqVo.setOriReqSys(upayLog.getRcvDomain());// 应该填写原充值的省份(mainOrgId这个可以)
				reqVo.setOriActionDate(upayLog.getRcvOprDt());
				reqVo.setOriTransactionID(upayLog.getRcvOprId());// 应该填写原充值的流水
				reqVo.setRevokeReason(reqMsg.getRevokeReason());
				
//				reqVo.setTransactionID(Serial.genSerialNos(CommonConstant.Sequence.OprId.toString()));
				//TransactionID设置成32位
				reqVo.setTransactionID(Serial.genSerialNum(CommonConstant.Sequence.OprId.toString()));
//				reqVo.setTransactionID(transIDH);
				reqVo.setSettleDate(StrUtil.subString(transIDHTime, 0, 8));
				msg19.setBody(reqVo);

				logger.info("移动渠道缴费冲正,begin 核心向CRM转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{transIDH,reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				log.info("移动渠道缴费冲正,begin 核心向CRM转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{transIDH,reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				Map<String, Object> pars = new HashMap<String, Object>();
				CrmMsgVo crmMsgVoRtn = crmReverseBus.execute(msg19, pars,txnLog, null);
				logger.info("移动渠道缴费冲正,end 核心向CRM转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{transIDH,reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				log.info("移动渠道缴费冲正,end 核心向CRM转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{transIDH,reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});

				modifyRcvLs(txnLog, transIDH, intTxnDate, transIDHTime, crmMsgVoRtn, msg19, mainOrgId, reqVo);
				logger.info("移动渠道缴费冲正,修改添加交易流水记录:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{txnLog.getIntTxnSeq(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				log.info("移动渠道缴费冲正,修改添加交易流水记录:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{txnLog.getIntTxnSeq(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				
				CrmReverseMsgResVo resVo = new CrmReverseMsgResVo();// CRM冲正应答报文体
				MsgHandle.unmarshaller(resVo, (String) crmMsgVoRtn.getBody());

//				String rtBodyStr = (String) crmMsgVoRtn.getBody();
				if (crmMsgVoRtn.getBody()==null || crmMsgVoRtn.getBody().equals("")) {
					logger.warn("移动渠道缴费冲正,应答报文体为空:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM应答码:{},CRM应答描述:{}" ,
							new Object[]{crmMsgVoRtn.getTransIDH(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue(),crmMsgVoRtn.getRspCode(),crmMsgVoRtn.getRspDesc()});
					log.warn("移动渠道缴费冲正,应答报文体为空:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM应答码:{},CRM应答描述:{}" ,
							new Object[]{crmMsgVoRtn.getTransIDH(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue(),crmMsgVoRtn.getRspCode(),crmMsgVoRtn.getRspDesc()});
					String errCode =RspCodeConstant.Crm.CRM_5A06.getValue();
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setRcvSubRspCode(errCode);
					txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlSubRspCode(errCode);
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					resBody.setRspCode(errCode);
					resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
					resBody.setTransactionID(reqMsg.getTransactionID());
					msgVoRtn.setBody(resBody);
					
					logger.debug("结束CRM冲正交易");
					return msgVoRtn;
				}
				if(RspCodeConstant.Upay.UPAY_U99998.getValue().equals(crmMsgVoRtn.getRspCode())){
					logger.warn("移动渠道缴费冲正,CRM前置超时!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{}" ,
							new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					log.warn("移动渠道缴费冲正,CRM前置超时!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{}" ,
							new Object[]{crmMsgVoRtn.getTransIDH(),reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setRcvSubRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
					txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					resBody.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
					resBody.setRspInfo(RspCodeConstant.Crm.CRM_5A07.getDesc());
					resBody.setTransactionID(reqMsg.getTransactionID());
					msgVoRtn.setBody(resBody);
					
					logger.debug("结束CRM冲正交易");
					return msgVoRtn;
				}
				
				if (!RspCodeConstant.Crm.CRM_0000.getValue().equals(resVo.getRspCode())) {
					logger.warn("移动渠道缴费冲正,CRM应答失败!流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM报文体应答码:{},CRM报文体应答描述:{}" ,
							new Object[]{crmMsgVoRtn.getTransIDH(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue(),resVo.getRspCode(),resVo.getRspInfo()});
					log.warn("移动渠道缴费冲正,CRM应答失败!流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM报文体应答码:{},CRM报文体应答描述:{}" ,
							new Object[]{crmMsgVoRtn.getTransIDH(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue(),resVo.getRspCode(),resVo.getRspInfo()});
					String errCode = resVo.getRspCode();
					errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
					txnLog.setRcvRspCode(errCode);
					txnLog.setRcvRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
					txnLog.setRcvSubRspCode(errCode);
					txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
					txnLog.setChlSubRspCode(errCode);
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);

					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					resBody.setRspCode(errCode);
					resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
					resBody.setTransactionID(reqMsg.getTransactionID());
					msgVoRtn.setBody(resBody);
					
					logger.debug("结束CRM冲正交易");
					return msgVoRtn;
				} else {
					logger.info("移动渠道缴费冲正,CRM应答成功!流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM报文体应答码:{},CRM报文体应答描述:{}" ,
							new Object[]{crmMsgVoRtn.getTransIDH(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue(),resVo.getRspCode(),resVo.getRspInfo()});
					log.succ("移动渠道缴费冲正,CRM应答成功!流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{},CRM报文体应答码:{},CRM报文体应答描述:{}" ,
							new Object[]{crmMsgVoRtn.getTransIDH(),reqMsg.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue(),resVo.getRspCode(),resVo.getRspInfo()});
					
					upayCsysPayLimitService.modifyLimtsDells(upayLog);// 需要先扣除限额表金额(本次冲正的金额)

					txnLog.setRcvSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
					txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);
					
					BankMsgVo msgReq = new BankMsgVo();
					msgReq.setActivityCode(CommonConstant.BankTrans.Bank06.getValue());
					msgReq.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
					msgReq.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
					msgReq.setReqDate(intTxnDate);
					msgReq.setReqTransID(transIDH);
					msgReq.setReqDateTime(transIDHTime);
					msgReq.setActionCode(CommonConstant.ActionCode.Requset.getValue());
					msgReq.setRcvSys(upayLog.getBankId());// 接收方系统 往哪家银行发
					
					BankReverseMsgReqVo reqMg = new BankReverseMsgReqVo();// 向Bank发起冲正请求报文体
					reqMg.setOriReqSys(ExcConstant.BANK_REQ_SYS);// upay向Bank发起缴费发起方
					reqMg.setOriReqTransID(upayLog.getIntTxnSeq());// upay向Bank发起缴费流水号
					reqMg.setOriReqDate(upayLog.getIntTxnDate());// upay向Bank发起缴费日期
					msgReq.setBody(reqMg);

					txnLog.setOuterVersion(ExcConstant.CRM_VERSION);
					txnLog.setBussChl(transCode.getBussChl());// 业务渠道
					txnLog.setOuterActivityCode(msgReq.getActivityCode());
					txnLog.setOuterDomain(upayLog.getBankId());
					txnLog.setOuterRouteType(msgVoRtn.getRouteType());
					txnLog.setOuterRouteVal(msgVoRtn.getRouteValue());
					txnLog.setOuterSessionId(msgVoRtn.getSessionID());
					DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
					upayCsysTxnLogService.modify(txnLog);
					
					logger.info("移动渠道缴费冲正,begin 核心向Bank转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					log.info("移动渠道缴费冲正,begin 核心向Bank转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					Map<String, Object> pas = new HashMap<String, Object>();
					BankMsgVo send2Bank = bankRefundBus.execute(msgReq, pas, txnLog, null);
					txnLog.setSettleDate(send2Bank.getRcvDate());
					logger.info("移动渠道缴费冲正,end 核心向Bank转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					log.info("移动渠道缴费冲正,end 核心向Bank转发返销(冲正)请求流水号:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
							new Object[]{transIDH,reqVo.getTransactionID(),transIDH,
							paramData.getMsgSender(),upayLog.getIdValue()});
					

					modifyOuterLs(txnLog, send2Bank,upayLog, msgReq, transCode, msgVoRtn);
					logger.info("移动渠道缴费冲正,修改添加交易流水记录:{},UPAY流水:{},发送银行:{}" ,
							new Object[]{txnLog.getIntTxnSeq(),transIDH,upayLog.getBankId()});
					log.info("移动渠道缴费冲正,修改添加交易流水记录:{},UPAY流水:{},发送银行:{}" ,
							new Object[]{txnLog.getIntTxnSeq(),transIDH,upayLog.getBankId()});
					
					if (!RspCodeConstant.Bank.BANK_020A00.getValue().equals(send2Bank.getRspCode())) {
						
						logger.warn("移动渠道缴费冲正,Bank应答失败!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
								new Object[]{send2Bank.getRcvTransID(),transIDH,
								upayLog.getBankId(),upayLog.getIdValue(),send2Bank.getRspCode(),send2Bank.getRspDesc()});
						log.warn("移动渠道缴费冲正,Bank应答失败!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
								new Object[]{send2Bank.getRcvTransID(),transIDH,
								upayLog.getBankId(),upayLog.getIdValue(),send2Bank.getRspCode(),send2Bank.getRspDesc()});
						
						upayCsysPayLimitService.modifyLimtsAdds(upayLog);// 银行冲正失败的时候,需要将减去的金额回退

						String errCode = send2Bank.getRspCode();
						errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
						msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
						msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						resBody.setRspCode(errCode);
						resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
						resBody.setTransactionID(reqMsg.getTransactionID());
						msgVoRtn.setBody(resBody);

						txnLog.setRcvRspCode(send2Bank.getRspCode());
						txnLog.setRcvRspDesc(send2Bank.getRspDesc());
						txnLog.setRcvSubRspCode(errCode);
						txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
						txnLog.setOuterRspCode(send2Bank.getRspCode());
						txnLog.setOuterRspDesc(send2Bank.getRspDesc());
						txnLog.setOuterSubRspCode(send2Bank.getRspCode());
						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setChlSubRspCode(errCode);
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
						upayCsysTxnLogService.modify(txnLog);
						
						logger.debug("结束CRM冲正交易");
						return msgVoRtn;
					} else {
						logger.info("移动渠道缴费冲正,Bank应答成功!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
								new Object[]{send2Bank.getRcvTransID(),transIDH,
								upayLog.getBankId(),upayLog.getIdValue(),send2Bank.getRspCode(),send2Bank.getRspDesc()});
						log.succ("移动渠道缴费冲正,Bank应答成功!流水号:{},UPAY流水:{},发送银行:{},手机号码:{},Bank应答码:{},Bank应答描述:{}" ,
								new Object[]{send2Bank.getRcvTransID(),transIDH,
								upayLog.getBankId(),upayLog.getIdValue(),send2Bank.getRspCode(),send2Bank.getRspDesc()});
						msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
						msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
						msgVoRtn.setRspType(CommonConstant.CrmRspType.Success.toString());
						resBody.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
						resBody.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
						resBody.setTransactionID(reqMsg.getTransactionID());
						resBody.setSettleDate(send2Bank.getRcvDate());
						msgVoRtn.setBody(resBody);

						modifySuccNewAndOldLog(txnLog, upayLog, msgVoRtn, send2Bank);
						logger.info("移动渠道缴费冲正,成功修改新增流水状态:{},UPAY流水:{},发送银行:{},手机号码:{}" ,
								new Object[]{txnLog.getIntTxnSeq(),transIDH,upayLog.getBankId(),upayLog.getIdValue()});
						log.info("移动渠道缴费冲正,成功修改新增流水状态:{},UPAY流水:{},发送银行:{},手机号码:{}" ,
								new Object[]{txnLog.getIntTxnSeq(),transIDH,upayLog.getBankId(),upayLog.getIdValue()});
						
						logger.debug("结束CRM冲正交易");
						return msgVoRtn;
					}
				}
			} else {
				logger.warn("移动渠道缴费冲正,原交易状态不是00/01:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				log.warn("移动渠道缴费冲正,原交易状态不是00/01:{},CRM操作流水:{},UPAY流水:{},发起省:{},手机号码:{}" ,
						new Object[]{reqMsg.getOriTransactionID(),reqMsg.getTransactionID(),transIDH,
						paramData.getMsgSender(),upayLog.getIdValue()});
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A30.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A30.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);

				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				resBody.setRspCode(RspCodeConstant.Crm.CRM_4A30.getValue());
				resBody.setRspInfo(RspCodeConstant.Crm.CRM_4A30.getDesc());
				resBody.setTransactionID(reqMsg.getTransactionID());
				msgVoRtn.setBody(resBody);
				
				logger.debug("结束CRM冲正交易");
				return msgVoRtn;
			}
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			logger.error("移动渠道缴费冲正,运行异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},异常:{}" ,
					new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
					paramData.getMsgSender(),errCode});
			log.error("移动渠道缴费冲正,运行异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
					new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
					paramData.getMsgSender()});
			logger.error("运行异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			resBody.setRspCode(errCode);
			resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			resBody.setTransactionID(reqMsg.getTransactionID());
			msgVoRtn.setBody(resBody);
			
			logger.debug("结束CRM冲正交易");
			return msgVoRtn;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			logger.error("移动渠道缴费冲正,系统业务异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},异常:{}" ,
					new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
					paramData.getMsgSender(),errCode});
			log.error("移动渠道缴费冲正,系统业务异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
					new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
					paramData.getMsgSender()});
			logger.error("移动渠道缴费冲正,业务异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			resBody.setRspCode(errCode);
			resBody.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			resBody.setTransactionID(reqMsg.getTransactionID());
			msgVoRtn.setBody(resBody);
			
			logger.debug("结束CRM冲正交易");
			return msgVoRtn;
		} catch (Exception e) {
			String errCode =RspCodeConstant.Crm.CRM_5A06.getValue();
			logger.error("移动渠道缴费冲正,系统异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
					new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
					paramData.getMsgSender()});
			log.error("移动渠道缴费冲正,系统异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{}" ,
					new Object[]{paramData.getTransIDO(),reqMsg.getTransactionID(),transIDH,
					paramData.getMsgSender()});
			logger.error("移动渠道缴费冲正,系统异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode)+":"+e.getMessage());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);

			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			resBody.setRspCode(errCode);
			
			//注释掉输出到应答报文的错误信息(该信息可能包含SQL异常) 20131213 modify by weiyi
//			String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_230?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_230);
//			resBody.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc()+":"+errDesc);
			
			resBody.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc());
			
			resBody.setTransactionID(reqMsg.getTransactionID());
			msgVoRtn.setBody(resBody);
			
			logger.debug("结束CRM冲正交易");
			return msgVoRtn;
		}
	}

	private void initLog(UpayCsysTxnLog txnLog, Long seqId, String transIDH,String intTxnDate, String transIDHTime, CrmMsgVo crmMsgVo,
			UpayCsysTransCode transCode, CrmMsgVo msgVoRtn,CrmReverseMsgReqVo reqMsg) {
		txnLog.setSeqId(seqId);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setIntMqSeq(crmMsgVo.getMqSeq());
//		txnLog.setTxnCat(transCode.getTxnCat());
		txnLog.setBussType(transCode.getBussType());
		txnLog.setBussChl(transCode.getBussChl());
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setSettleDate(StrUtil.subString(transIDHTime, 0, 8));
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
		txnLog.setReqTransDt(StrUtil.subString(crmMsgVo.getTransIDOTime(), 0, 8));
		txnLog.setReqVersion(crmMsgVo.getVersion());
		txnLog.setReqBipCode(crmMsgVo.getBIPCode());
		txnLog.setReqActivityCode(crmMsgVo.getActivityCode());
		txnLog.setReqDomain(crmMsgVo.getMsgSender());
		txnLog.setReqRouteType(crmMsgVo.getRouteType());
		txnLog.setReqRouteVal(crmMsgVo.getRouteValue());
		txnLog.setReqSessionId(crmMsgVo.getSessionID());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReqTransId(crmMsgVo.getTransIDO());
		txnLog.setReqTransTm(crmMsgVo.getTransIDOTime());
		txnLog.setReqTransDt(StrUtil.subString(msgVoRtn.getTransIDOTime(), 0, 8));
		txnLog.setReqTranshId(msgVoRtn.getTransIDH());
		txnLog.setReqTranshTm(msgVoRtn.getTransIDHTime());
		txnLog.setReqTranshDt(StrUtil.subString(msgVoRtn.getTransIDHTime(), 0, 8));
		txnLog.setReqOprId(reqMsg.getTransactionID());
		txnLog.setReqOprDt(StrUtil.subString(crmMsgVo.getTransIDHTime(), 0, 8));
		txnLog.setReqCnlType(transCode.getBussChl());
		txnLog.setOriOrgId(reqMsg.getOriReqSys());
		txnLog.setOriReqDate(reqMsg.getOriActionDate());
		txnLog.setOriOprTransId(reqMsg.getOriTransactionID());
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogService.add(txnLog);
	}
	
	private void addInitLog(UpayCsysTxnLog txnLog,UpayCsysTxnLog upayLog){
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
	
	private void modifyRcvLs(UpayCsysTxnLog txnLog, String transIDH,String intTxnDate, String transIDHTime,
			CrmMsgVo crmMsgVoRtn, CrmMsgVo msg19, String mainOrgId, CrmReverseMsgReqVo reqVo){
		txnLog.setRcvTransId(transIDH);
		txnLog.setRcvTransTm(transIDHTime);
		txnLog.setRcvTransDt(intTxnDate);
		txnLog.setRcvOprId(reqVo.getTransactionID());
		txnLog.setRcvOprDt(txnLog.getIntTxnDate());
		txnLog.setRcvOprTm(txnLog.getIntTxnTime());
		txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
		txnLog.setRcvActivityCode(msg19.getActivityCode());
		txnLog.setRcvBipCode(msg19.getBIPCode());
		txnLog.setRcvDomain(mainOrgId);
		txnLog.setRcvRouteType(crmMsgVoRtn.getRouteType());
		txnLog.setRcvRouteVal(crmMsgVoRtn.getRouteValue());
		txnLog.setRcvSessionId(crmMsgVoRtn.getSessionID());
	}
	
	private void modifyOuterLs(UpayCsysTxnLog txnLog, BankMsgVo send2Bank,UpayCsysTxnLog upayLog, BankMsgVo msgReq,
			UpayCsysTransCode transCode,CrmMsgVo msgVoRtn){
//		txnLog.setOuterVersion(ExcConstant.CRM_VERSION);
//		txnLog.setBussChl(transCode.getBussChl());// 业务渠道
//		txnLog.setOuterActivityCode(msgReq.getActivityCode());
//		txnLog.setOuterDomain(upayLog.getBankId());
//		txnLog.setOuterRouteType(msgVoRtn.getRouteType());
//		txnLog.setOuterRouteVal(msgVoRtn.getRouteValue());
//		txnLog.setOuterSessionId(msgVoRtn.getSessionID());
		txnLog.setOuterTransId(send2Bank.getRcvTransID());
		txnLog.setOuterTransDt(send2Bank.getRcvDate());
		txnLog.setOuterTransTm(send2Bank.getRcvDateTime());
		txnLog.setOuterTranshId(send2Bank.getRcvTransID());
		txnLog.setOuterTranshDt(send2Bank.getRcvDate());
		txnLog.setOuterTranshTm(send2Bank.getRcvDateTime());
		txnLog.setOuterOprId(send2Bank.getRcvTransID());
		txnLog.setOuterOprDt(send2Bank.getRcvDate());
		txnLog.setOuterOprTm(send2Bank.getRcvDateTime());
	}
	
	private void modifySuccNewAndOldLog(UpayCsysTxnLog txnLog,UpayCsysTxnLog upayLog, CrmMsgVo msgVoRtn, BankMsgVo send2Bank){
		txnLog.setOuterRspCode(send2Bank.getRspCode());// 接收方返回码
		txnLog.setOuterRspDesc(send2Bank.getRspDesc());
		txnLog.setOuterSubRspCode(send2Bank.getRspCode());
		txnLog.setRcvRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		txnLog.setRcvRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		txnLog.setRcvSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		txnLog.setRcvSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		txnLog.setChlRspCode(msgVoRtn.getRspCode());
		txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
		txnLog.setChlRspType(msgVoRtn.getRspType());
		txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
		txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
		txnLog.setSettleDate(send2Bank.getRcvDate());
		txnLog.setPayAmt(-upayLog.getPayAmt());// 成功返回,需要设置成负值TODO
		txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());// 正常
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());

		upayLog.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());// 原交易冲正标识
		upayLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		upayCsysTxnLogService.modifyLog(txnLog, upayLog);// 同时更新原交易和新增的交易
	}
}
