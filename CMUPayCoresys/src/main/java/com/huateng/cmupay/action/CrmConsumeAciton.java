package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysPayLimitService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.bank.BankPayBus;
import com.huateng.cmupay.jms.business.crm.CrmChargeBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysPayLimit;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmConsumeReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmConsumeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * @author cmt 缴费 网站签约缴费 BIP1A160 短信签约缴费 BIP1A161 电话签约缴费 BIP1A162 预付费自动缴费
 *         BIP1A163
 */
@Controller("crmConsumeAciton")
@Scope("prototype")
public class CrmConsumeAciton extends AbsBaseAction<CrmMsgVo, CrmMsgVo> {

//	private @Value("${retry.num}")
//	String retryNum;
//	private @Value("${mouthAmt}")
//	String mouthAmt;
	@Autowired
	private IUpayCsysPayLimitService upayCsysPayLimitService;
	@Autowired
	private BankPayBus bankPayBus;
	@Autowired
	private CrmChargeBus crmChargeBus;

	@Override
	public CrmMsgVo execute(CrmMsgVo paramData) throws AppBizException {
		logger.debug("ConsumeCrmAciton execute(Object) - start");
		//充值金额限额标志
		boolean limitFlag = false;
		//得到请求缴费的移动报文
		CrmMsgVo msgVo = paramData;
		//生成平台内部流水号，时间，日期
		String intTxnSeq = msgVo.getTxnSeq();
		String intTxnDate = msgVo.getTxnDate();
		String intTxnTime = msgVo.getTxnTime();
		Long seqValue = msgVo.getSeqId();
		// 得到内部交易代码表信息
		UpayCsysTransCode code = msgVo.getTransCode();
		CrmMsgVo msgVoRtn = new CrmMsgVo();
		// 报文体内容check
		CrmConsumeReqVo bodyMsgVo = new CrmConsumeReqVo();
		CrmConsumeResVo bodyMsgVoRtn = new CrmConsumeResVo();
		
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		txnLog.setSettleDate(intTxnDate);
		UpayCsysBindInfo bindInfo = null;
		try {
			//解析报文体将String转化为对象
			MsgHandle.unmarshaller(bodyMsgVo, msgVo.getBody().toString());
			if(!StringUtils.isBlank(bodyMsgVo.getTransactionID())){
				//根据发起发机构以及报文体操作流水号，查找交易流水是否重复
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("reqDomain", msgVo.getMsgSender());
				param.put("reqOprId", bodyMsgVo.getTransactionID());
				UpayCsysTxnLog upayCsysTxnLog = upayCsysTxnLogService
						.findObj(param);
				if (upayCsysTxnLog != null) {
					logger.warn("网站,短信,电话签约缴费接口!操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},协议号:{},操作流水号:{}",
							new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
							paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getSubID(),bodyMsgVo.getTransactionID()});
					
					log.warn("网站,短信,电话签约缴费接口!操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},协议号:{},操作流水号:{}",
							new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
							paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getSubID(),bodyMsgVo.getTransactionID()});
					
					msgVoRtn = msgVo;
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
					bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc());
					convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
							bodyMsgVoRtn);
					logger.debug("ConsumeCrmAciton execute(Object) - end");
					return msgVoRtn;
				}
			}
			// 生成预处理订单
			txnLog.setSeqId(seqValue);
			txnLog.setIntTxnSeq(intTxnSeq);
			txnLog.setIntTransCode(code.getTransCode());
			txnLog.setIntTxnDate(intTxnDate);
			txnLog.setIntTxnTime(intTxnTime);
			txnLog.setIntMqSeq(msgVo.getMqSeq());
			txnLog.setBussType(code.getBussType());
			txnLog.setBussChl(code.getBussChl());
			txnLog.setPayMode(code.getPayMode());
			txnLog.setMainFlag(CommonConstant.SpeSymbol.SPACE.toString());
			txnLog.setReqVersion(ExcConstant.CRM_VERSION);
			txnLog.setReqBipCode(msgVo.getBIPCode());
			txnLog.setReqActivityCode(msgVo.getActivityCode());
			txnLog.setReqDomain(msgVo.getMsgSender());
			txnLog.setReqRouteType(msgVo.getRouteType());
			txnLog.setReqRouteVal(msgVo.getRouteValue());
			txnLog.setReqSessionId(msgVo.getSessionID());
			txnLog.setReqTransId(msgVo.getTransIDO());
			txnLog.setReqTransDt(StrUtil.subString(msgVo.getTransIDOTime(), 0, 8));
			txnLog.setReqTransTm(msgVo.getTransIDOTime());
			txnLog.setReqTranshId(intTxnSeq);
			txnLog.setReqTranshDt(intTxnDate);
			txnLog.setReqTranshTm(intTxnTime);
			txnLog.setReqOprId(bodyMsgVo.getTransactionID());
			txnLog.setReqOprDt(bodyMsgVo.getActionDate());
			txnLog.setReqOprTm(msgVo.getTransIDOTime());
			txnLog.setReqCnlType(bodyMsgVo.getCnlTyp());
			txnLog.setIdType(bodyMsgVo.getIDType());
			txnLog.setIdValue(bodyMsgVo.getIDValue());
			txnLog.setNeedPayAmt(StringFormat.paseLong(bodyMsgVo.getPayed()));
			txnLog.setPayAmt(StringFormat.paseLong(bodyMsgVo.getPayed()));
			txnLog.setPayedType(bodyMsgVo.getPayedType());
			txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setRcvBipCode(msgVo.getBIPCode());
			ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(bodyMsgVo.getIDValue());
			txnLog.setIdProvince(provincePhoneNum != null ? provincePhoneNum.getProvinceCode() : null);
			upayCsysTxnLogService.add(txnLog);
			//校验报文体格式
			String validateMsg = this.validateModel(bodyMsgVo);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.debug("body validate success");
			} else {
				logger.warn("网站,短信,电话签约缴费接口!报文体格式校验失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},错误字段:{},操作流水号:{}",
						new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
						paramData.getMsgSender(),bodyMsgVo.getIDValue(),validateMsg,bodyMsgVo.getTransactionID()});
				
				log.warn("网站,短信,电话签约缴费接口!报文体格式校验失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},错误字段:{},操作流水号:{}",
						new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
						paramData.getMsgSender(),bodyMsgVo.getIDValue(),validateMsg,bodyMsgVo.getTransactionID()});
				
				msgVoRtn = msgVo;
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_4A99.getDesc()
						+ validateMsg);
				convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
						bodyMsgVoRtn);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A99.getDesc()
						+ ":" + validateMsg);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("ConsumeCrmAciton execute(Object) - end");
				return msgVoRtn;
			}

			// 查询签约信息
			Map<String, Object> paramBind = new HashMap<String, Object>();
			paramBind.put("idType", bodyMsgVo.getIDType());
			paramBind.put("idValue", bodyMsgVo.getIDValue());
			bindInfo = upayCsysBindInfoService.findObj(paramBind);
			if(bindInfo != null && bindInfo.getIdProvince() != null) {
				txnLog.setIdProvince(bindInfo.getIdProvince());
			}
			if (bindInfo == null || (!CommonConstant.BindStatus.Bind.toString().equals(bindInfo.getStatus()))/*|| (!bodyMsgVo.getSubID().equals(bindInfo.getSubId()))*/) {
				logger.warn("网站,短信,电话签约缴费接口!无签约记录!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},协议号:{},缴费操作流水号:{}",
						new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
						paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getSubID(),bodyMsgVo.getTransactionID()});
				log.warn("网站,短信,电话签约缴费接口!无签约记录!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},协议号:{},缴费操作流水号:{}",
						new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
						paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getSubID(),bodyMsgVo.getTransactionID()});
				msgVoRtn = msgVo;
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_2A09.getValue());
				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_2A09.getDesc());
				convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
						bodyMsgVoRtn);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A09.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A09.getDesc() + "签约关系查询结果为空。");
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("ConsumeCrmAciton execute(Object) - end");
				return msgVoRtn;
			}

			// if ("".equals(StringUtil.toTrim(bindInfo.getSubId()))
			// || (!bodyMsgVo.getSubID().equals(bindInfo.getSubId()))) {
			// logger.warn("网站,短信,电话签约缴费接口!intTxnSeq:{},手机号:{}签约关系查询结果为空,签约协议号错误",
			// paramData.getTxnSeq(), bodyMsgVo.getIDValue());
			// msgVoRtn = msgVo;
			// msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			// msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			// msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			// convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			// bodyMsgVoRtn);
			// bodyMsgVoRtn.setRspCode(MessageHandler.getCrmErrCode("2A09"));
			// bodyMsgVoRtn.setRspInfo(MessageHandler.getCrmErrMsg("2A09"));
			// txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			// txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			// .toString());
			// txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			// txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			// txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("2A09"));
			// txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("2A09")
			// + ":" + "签约关系查询结果为空,签约协议号错误。");
			// txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			// upayCsysTxnLogService.modify(txnLog);
			// return msgVoRtn;
			// }
			// if (!CommonConstant.BindStatus.Bind.toString().equals(
			// bindInfo.getStatus())) {
			// logger.warn("网站,短信,电话签约缴费接口!intTxnSeq:{},手机号:{}签约关系查询结果:手机号码与银行没有绑定成功",
			// paramData.getTxnSeq(), bodyMsgVo.getIDValue());
			// msgVoRtn = msgVo;
			// msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			// msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			// msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			// convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			// bodyMsgVoRtn);
			// bodyMsgVoRtn.setRspCode(MessageHandler.getCrmErrCode("2A09"));
			// bodyMsgVoRtn.setRspInfo(MessageHandler.getCrmErrMsg("2A09"));
			// txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			// txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			// .toString());
			// txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			// txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			// txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("2A09"));
			// txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("2A09")
			// + ":" + "手机号码与银行没有绑定成功。");
			// txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			// upayCsysTxnLogService.modify(txnLog);
			// return msgVoRtn;
			// }

			// 放入签约关系信息
			txnLog.setPayType(bindInfo.getPayType());
			txnLog.setMainIdProvince(bindInfo.getMainIdProvince());
			txnLog.setMainIdType(bindInfo.getMainIdType());
			txnLog.setMainIdValue(bindInfo.getMainIdValue());
			txnLog.setIdProvince(bindInfo.getIdProvince());
			// txnLog.setIdType(bindInfo.getIdType());
			txnLog.setUserStatus("");
			txnLog.setUserCat(bindInfo.getUserCat());
			txnLog.setBalance(0L);
			txnLog.setSignStatus(bindInfo.getStatus());
			txnLog.setUserId(bindInfo.getUserId());
			txnLog.setUserName(bindInfo.getUserName());
			txnLog.setUserType(bindInfo.getUserType());
			txnLog.setSubTime(bindInfo.getSignSubTime());

			txnLog.setBankId(bindInfo.getBankId());
			txnLog.setBankAcctType(bindInfo.getBankAcctType());
			txnLog.setBankAccId(bindInfo.getBankAccId());
			txnLog.setSubId(bindInfo.getSubId());
			txnLog.setRechAmount(bindInfo.getRechAmount());
			txnLog.setRechThreshold(bindInfo.getRechThreshold());
			txnLog.setMaxRechAmount(bindInfo.getMaxRechAmount());
			txnLog.setMaxRechThreshold(bindInfo.getMaxRechThreshold());

			// 查询副号码关联主号码

			String payMainFlag = CommonConstant.Mainflag.Master.toString();
			String idType = bindInfo.getIdType();
			String idValue = bindInfo.getIdValue();
			String province = bindInfo.getIdProvince();
			String mainIdType = idType;
			String mainIdValue = idValue;
			String mainProvince = province;
			String bankID = bindInfo.getBankId();
			String thridOrg = msgVo.getMsgSender();
			//主副号标识是主号还是副号  SLave是副号标识
			if (CommonConstant.Mainflag.Slave.toString().equals(
					bindInfo.getMainFlag())) {

				mainIdValue = bindInfo.getMainIdValue();
				mainProvince = bindInfo.getMainIdProvince();
				mainIdType = bindInfo.getMainIdType();
				payMainFlag = CommonConstant.Mainflag.Slave.toString();
				thridOrg = SysMapCache.getProvCd(province).getSysCd();
//				logger.info(
//						"intTxnSeq:{},查询副号码:{}关联主号码,MainIdValue:{},mainIdProvince:{},bankId:{},subId:{}",
//						new Object[] { intTxnSeq, idValue, mainIdValue,
//								mainProvince, bankID, bodyMsgVo.getSubID() });
				//查找副号所对应的的主号是否有签约关系
				Map<String, Object> paramBindMaster = new HashMap<String, Object>();
				paramBindMaster.put("idType", mainIdType);
				paramBindMaster.put("idValue", mainIdValue);
				UpayCsysBindInfo bindInfoMain = upayCsysBindInfoService.findObj(paramBindMaster);
				if (bindInfoMain == null||(!CommonConstant.BindStatus.Bind.toString().equals(bindInfoMain.getStatus()))/*|| (!bodyMsgVo.getSubID().equals(bindInfoMain.getSubId()))*/) {
					
					log.warn("网站,短信,电话签约缴费接口!无主号签约!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},主号:{},缴费号码:{},协议号:{},缴费操作流水号:{}",
							new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
							paramData.getMsgSender(),mainIdValue,bodyMsgVo.getIDValue(),bodyMsgVo.getSubID(),bodyMsgVo.getTransactionID()});
					
					logger.warn("网站,短信,电话签约缴费接口!无主号签约!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},主号:{},缴费号码:{},协议号:{},缴费操作流水号:{}",
							new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
							paramData.getMsgSender(),mainIdValue,bodyMsgVo.getIDValue(),bodyMsgVo.getSubID(),bodyMsgVo.getTransactionID()});
					msgVoRtn = msgVo;
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_2A09.getValue());
					bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_2A09.getDesc());
					convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
							bodyMsgVoRtn);
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.toString());
					txnLog.setChlRspType(msgVoRtn.getRspType());
					txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A09.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_2A09.getDesc() + "副号关联主号签约关系查询结果为空。");
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					logger.debug("ConsumeCrmAciton execute(Object) - end");
					return msgVoRtn;
				} else {
					bankID = bindInfoMain.getBankId();
					txnLog.setBankId(bindInfoMain.getBankId());
					txnLog.setBankAcctType(bindInfoMain.getBankAcctType());
					txnLog.setBankAccId(bindInfoMain.getBankAccId());
					txnLog.setSubId(bindInfoMain.getSubId());
					txnLog.setRechAmount(bindInfoMain.getRechAmount());
					txnLog.setRechThreshold(bindInfoMain.getRechThreshold());
					txnLog.setMaxRechAmount(bindInfoMain.getMaxRechAmount());
					txnLog.setMaxRechThreshold(bindInfoMain
							.getMaxRechThreshold());
				}
			}
	
			// 加入主副号标识
			txnLog.setMainFlag(payMainFlag);
			//判断扣款银行是否有服务权限
			String  orgFlag = offOrgTrans(paramData.getMsgSender(),bankID,thridOrg,code.getTransCode(),
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			boolean orgFlag = isO2OTransOn(paramData.getMsgSender(),bankID,thridOrg,code.getTransCode());
					if (orgFlag != null) {
						logger.warn("网站,短信,电话签约缴费接口!服务的权限关闭!CRM流水:{},CRM操作流水:{},UPAY流水:{}，发起方机构:{},落地方机构:{},第三方方机构:{},业务代码:{},缴费操作流水号:{}",
								new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq, paramData.getMsgSender(),bankID,thridOrg,code.getTransCode(),bodyMsgVo.getTransactionID()});
						
						log.warn("网站,短信,电话签约缴费接口!服务的权限关闭!CRM流水:{},CRM操作流水:{},UPAY流水:{}，,发起方机构:{},落地方机构:{},第三方方机构:{},业务代码:{},缴费操作流水号:{},服务的权限关闭。",
								new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq, paramData.getMsgSender(),bankID,thridOrg,code.getTransCode(),bodyMsgVo.getTransactionID()});
						
					
						
						msgVoRtn = msgVo;
						msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
						msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
						bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_3A35.getDesc());
						convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
								bodyMsgVoRtn);
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
						txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
						txnLog.setChlRspType(msgVoRtn.getRspType());
						txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
						txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A35.getDesc()+orgFlag);
						txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
						upayCsysTxnLogService.modify(txnLog);
						logger.debug("ConsumeCrmAciton execute(Object) - end");
						return msgVoRtn;
						
					}
			//组装限额判断条件
			UpayCsysPayLimit obj = new UpayCsysPayLimit();
			obj.setIdType(mainIdType);
			obj.setIdValue(mainIdValue);
			obj.setIdProvince(mainProvince);
			obj.setPayMonth(intTxnDate.substring(0, 6));
			obj.setDayMaxAmount(StringFormat.paseLong(
					DictCodeCache.getDictCode(DictConst.DictId.ChargeLimit.getValue(), DictConst.CodeId.ChargeLimit.getValue())
					.getCodeValue2()));
			obj.setMonthMaxAmount(StringFormat.paseLong(
					DictCodeCache.getDictCode(DictConst.DictId.ChargeLimit.getValue(), DictConst.CodeId.ChargeLimit.getValue())
					.getCodeValue2()));
			//判断是否超限，未超限将累加
			limitFlag = upayCsysPayLimitService.modifyLimitAdd(obj,
					StringFormat.paseLong(bodyMsgVo.getPayed()));
			if (!limitFlag) {
				logger.warn("网站,短信,电话签约缴费接口!月累计金额超限!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},充值号码:{},主号:{},缴费金额:{},缴费操作流水号:{}",
						new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
						paramData.getMsgSender(),bodyMsgVo.getIDValue(),mainIdValue,bodyMsgVo.getPayed(),bodyMsgVo.getTransactionID()});
				
				log.warn("网站,短信,电话签约缴费接口!月累计金额超限!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},充值号码:{},主号:{},缴费金额:{},缴费操作流水号:{}",
						new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
						paramData.getMsgSender(),bodyMsgVo.getIDValue(),mainIdValue,bodyMsgVo.getPayed(),bodyMsgVo.getTransactionID()});
				
				msgVoRtn = msgVo;
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_3A01.getValue());
//				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_3A01.getDesc());
				bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_3A33.getValue());
				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_3A33.getDesc());
				convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
						bodyMsgVoRtn);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A01.getValue());
//				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A01.getDesc() + "crm缴费金额超限。");
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A33.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A33.getDesc() + "crm缴费导致月累计金额超限。");
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("ConsumeCrmAciton execute(Object) - end");
				return msgVoRtn;
			} 
//			if(pressure){//TODO 压力测试添加，后面删除
//				bodyMsgVoRtn.setActionDate(bodyMsgVo.getActionDate());
//				bodyMsgVoRtn.setIDType(bodyMsgVo.getIDType());
//				bodyMsgVoRtn.setIDValue(bodyMsgVo.getIDValue());
//				bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
//				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
//				bodyMsgVoRtn.setTransactionID(bodyMsgVo.getTransactionID());
//				msgVo.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
//				msgVo.setRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
//				msgVo.setBody(bodyMsgVoRtn);
//				return msgVo;
//			}
			// 条件检查月最高缴费额度判断
			// Map<String, Object> params = new HashMap<String, Object>();
			// params.put("idValue", idValue);
			// params.put("payMonth", msgVo.getTxnMouth());
			// UpayCsysPayLimit payLimit =
			// uPayCsysPayLimitService.findObj(params);
			// if (payLimit == null) {
			// payLimit = new UpayCsysPayLimit();
			// payLimit.setMonthAmount(0L);
			//
			// } else if (payLimit.getMonthAmount() == null
			// || "".equals(payLimit.getMonthAmount())) {
			// payLimit = new UpayCsysPayLimit();
			// payLimit.setMonthAmount(0L);
			// }
			// Long monthAmount = payLimit.getMonthAmount();
			// if (monthAmount.longValue() > CommonConstant.PayAmtLimit.mouthAmt
			// .getValue().longValue()) {
			// logger.info("crm缴费金额超限。");
			// logger.error("网站,短信,电话签约缴费接口!3A01:" + MessageHandler.getCrmErrMsg("3A01"));
			// msgVoRtn = msgVo;
			// msgVoRtn.setRspType("0");
			// msgVoRtn.setRspCode("3A01");
			// msgVoRtn.setRspDesc(MessageHandler.getCrmErrMsg("3A01"));
			// convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			// bodyMsgVoRtn);
			// bodyMsgVoRtn.setRspCode("3A01");
			// bodyMsgVoRtn.setRspInfo("crm发起 接收方机构缴费路由关闭。");
			// return msgVoRtn;
			// }
			// 银行支付 at isolation 2
			BankMsgVo bankMsgVo = new BankMsgVo();
			BankMsgVo bankMsgVoRtn = new BankMsgVo();
			bankMsgVo.setTransCode(msgVo.getTransCode());
			bankMsgVo.setReqDate(intTxnDate);
			bankMsgVo.setReqDateTime(intTxnTime);
			bankMsgVo.setReqTransID(intTxnSeq);
			Map<String, String> params1 = new HashMap<String, String>();
			params1.put("cnlTyp", bodyMsgVo.getCnlTyp());
			params1.put("payed", bodyMsgVo.getPayed());
			params1.put("subId", bodyMsgVo.getSubID());
			params1.put("idType", bodyMsgVo.getIDType());
			params1.put("idValue", bodyMsgVo.getIDValue());
			params1.put("homeProv", province);
			params1.put("bankId", bankID);
			//发送扣款报文到银行侧
			bankMsgVoRtn = bankPayBus.execute(bankMsgVo, params1, txnLog,
					bindInfo);
			txnLog.setSettleDate(bankMsgVoRtn.getRcvDate());
//			txnLog.setReqOprId(bankMsgVoRtn.getRcvTransID());
//			txnLog.setReqOprDt(bankMsgVoRtn.getRcvDate());
//			txnLog.setReqOprTm(bankMsgVoRtn.getRcvDateTime());
			/*
			 * if (MessageHandler.getUpayErrCode("U99998").equals(
			 * bankMsgVoRtn.getRspCode())) {
			 * logger.info("请求银行支付交易我方超时未收到相应，银行端交易结果查询。");
			 * 
			 * Map<String, String> params2 = new HashMap<String, String>();
			 * params2.put("cnlTyp", bodyMsgVo.getCnlTyp());
			 * params2.put("bankId", bindInfo.getBankId()); bankMsgVoRtn =
			 * bankQueryResultBus.execute(bankMsgVo, params2, upayCsysTxnLog,
			 * bindInfo);
			 * 
			 * 
			 * Map<String, String> params10 = new HashMap<String, String>();
			 * params10.put("cnlTyp", bodyMsgVo.getCnlTyp());
			 * params10.put("bankId", bindInfo.getBankId());
			 * params10.put("reverseTransId", bankMsgVo.getReqTransID());
			 * params10.put("reverseTransDt", bankMsgVo.getReqDate());
			 * params10.put("reverseOriReqSys", ExcConstant.BANK_REQ_SYS);
			 * BankResultQueryReverseHandler handler = new
			 * BankResultQueryReverseHandler();
			 * handler.setHeadTransMsg(bankMsgVo);
			 * handler.setBodyTransMsg(null);
			 * handler.setHeadReverseMsg(bankMsgVo);
			 * handler.setBodyReverseMsg(null);
			 * handler.setTxnLog(upayCsysTxnLog);
			 * handler.setUpayCsysTxnLogService(upayCsysTxnLogService);
			 * handler.setBankQueryResultBus(bankQueryResultBus);
			 * handler.setBankReverseBus(bankReverseBus);
			 * handler.setParams(params10); Task task = new Task();
			 * task.setTaskHandler(handler); try { TaskQueue.queue.put(task); }
			 * catch (InterruptedException e) { logger.error("网站,短信,电话签约缴费接口!查询之后冲正业务调度失败。",
			 * e); }
			 * 
			 * // 超时冲正 logger.info("请求银行支付交易我方超时未收到相应，冲正处理。"); Map<String,
			 * String> params3 = new HashMap<String, String>();
			 * params3.put("cnlTyp", bodyMsgVo.getCnlTyp());
			 * params3.put("bankId", bindInfo.getBankId());
			 * params3.put("reverseTransId", bankMsgVo.getReqTransID());
			 * params3.put("reverseTransDt", bankMsgVo.getReqDate());
			 * params3.put("reverseOriReqSys", ExcConstant.BANK_REQ_SYS);
			 * bankReverseBus.execute(bankMsgVo, params3, txnLog, bindInfo);
			 * msgVoRtn = msgVo;
			 * msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			 * msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			 * bodyMsgVoRtn);
			 * bodyMsgVoRtn.setRspCode(MessageHandler.getCrmErrCode("5A01"));
			 * bodyMsgVoRtn.setRspInfo(MessageHandler.getCrmErrMsg("5A01"));
			 * txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			 * .toString());
			 * txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			 * txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("5A01"));
			 * txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("5A01") + ":"
			 * + "请求银行支付交易我方超时未收到相应，冲正处理。");
			 * txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			 * upayCsysTxnLogService.modifyLimit(txnLog, bindInfo); return
			 * msgVoRtn;
			 * 
			 * } else if (MessageHandler.getBankErrCode("025A07").equals(
			 * bankMsgVoRtn.getRspCode())) { //
			 * logger.info("请求银行支付交易银行方超时收到银行端超时应答吗，银行端交易结果查询。"); // Map<String,
			 * String> params2 = new HashMap<String, String>(); //
			 * params2.put("cnlTyp", bodyMsgVo.getCnlTyp()); //
			 * params2.put("bankId", bindInfo.getBankId()); // bankMsgVoRtn =
			 * bankQueryResultBus.execute(bankMsgVo, params2, // upayCsysTxnLog,
			 * bindInfo); // 超时冲正 logger.info("请求银行支付交易银行方超时收到银行端超时应答吗，冲正处理。");
			 * Map<String, String> params3 = new HashMap<String, String>();
			 * params3.put("reverseTransId", bankMsgVo.getReqTransID());
			 * params3.put("reverseTransDt", bankMsgVo.getReqDate());
			 * params3.put("reverseOriReqSys", ExcConstant.BANK_REQ_SYS);
			 * params3.put("cnlTyp", bodyMsgVo.getCnlTyp());
			 * params3.put("bankId", bindInfo.getBankId());
			 * bankReverseBus.execute(bankMsgVo, params3, txnLog, bindInfo);
			 * msgVoRtn = msgVo;
			 * msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			 * msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			 * bodyMsgVoRtn);
			 * bodyMsgVoRtn.setRspCode(MessageHandler.getCrmErrCode("5A01"));
			 * bodyMsgVoRtn.setRspInfo(MessageHandler.getCrmErrMsg("5A01"));
			 * txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			 * .toString());
			 * txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			 * txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("5A01"));
			 * txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("5A01") + ":"
			 * + "请求银行支付交易银行方超时收到银行端超时应答吗，冲正处理。");
			 * txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			 * upayCsysTxnLogService.modifyLimit(txnLog, bindInfo); return
			 * msgVoRtn;
			 * 
			 * } else
			 */
			//判断银行返回的应答码是不是成功的交易
			if (!(RspCodeConstant.Bank.BANK_020A00.getValue().equals(bankMsgVoRtn
					.getRspCode()))) {
				log.warn("网站,短信,电话签约缴费接口!扣款失败!UPAY流水:{},发起省:{},缴费号码:{},主号:{},银行:{},发给银行流水:{},银行返回流水:{},BANK应答码:{}",
						new Object[]{intTxnSeq,paramData.getMsgSender(),bodyMsgVo.getIDValue(),
						mainIdValue,bankID,bankMsgVoRtn.getReqTransID(),bankMsgVoRtn.getRcvTransID(),bankMsgVoRtn.getRspCode()});
				
				String errCode = CrmErrorCodeCache.getCrmErrCode(bankMsgVoRtn
						.getRspCode());
				logger.warn("网站,短信,电话签约缴费接口!扣款失败!UPAY流水:{},发起省:{},缴费号码:{},主号:{},银行:{},发给银行流水:{},银行返回流水:{},BANK应答码:{}",
						new Object[]{intTxnSeq,paramData.getMsgSender(),bodyMsgVo.getIDValue(),
						mainIdValue,bankID,bankMsgVoRtn.getReqTransID(),bankMsgVoRtn.getRcvTransID(),bankMsgVoRtn.getRspCode()});
				// 拼接支付失败xml返回
				msgVoRtn = msgVo;
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				bodyMsgVoRtn.setRspCode(errCode);
				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
				convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
						bodyMsgVoRtn);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode)
						 + "请求银行支付交易失败。");
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysPayLimitService.modifyLimit(txnLog, bindInfo);
				logger.debug("ConsumeCrmAciton execute(Object) - end");
				return msgVoRtn;
			}

			// 充值操作
			CrmMsgVo chargeMsgVo = new CrmMsgVo();
			Map<String, String> params = new HashMap<String, String>();
			chargeMsgVo.setBIPCode(msgVo.getBIPCode());
			chargeMsgVo.setActivityCode(msgVo.getActivityCode());
			chargeMsgVo.setOrigDomain(msgVo.getHomeDomain());
			chargeMsgVo.setHomeDomain(msgVo.getOrigDomain());
			chargeMsgVo.setSessionID(intTxnSeq);
			chargeMsgVo.setTransIDO(intTxnSeq);
			chargeMsgVo.setTransIDOTime(StrUtil.subString(intTxnTime, 0, 14));
			chargeMsgVo.setRouteType(CommonConstant.RouteType.RoutePhone
					.toString());
			chargeMsgVo.setRouteValue(bodyMsgVo.getIDValue());
			
			chargeMsgVo.setTransIDC(msgVo.getTransIDC());
			chargeMsgVo.setConvID(msgVo.getConvID());
			chargeMsgVo.setCutOffDay(msgVo.getCutOffDay());
			chargeMsgVo.setOSNTime(msgVo.getOSNTime());
			chargeMsgVo.setOSNDUNS(msgVo.getOSNDUNS());
			chargeMsgVo.setHSNDUNS(msgVo.getHSNDUNS());
			chargeMsgVo.setMsgSender(msgVo.getMsgSender());
			chargeMsgVo.setMsgReceiver(msgVo.getMsgReceiver());
			chargeMsgVo.setPriority(msgVo.getPriority());
			chargeMsgVo.setServiceLevel(msgVo.getServiceLevel());
			chargeMsgVo.setSvcContType(msgVo.getSvcContType());

			// 判断是给主还是副号码充值
			// 充值的时候要判断是主号码还是副号码
			if (CommonConstant.Mainflag.Master.toString().equals(payMainFlag)) {
				// bodyChargeVo.setIDValue(bodyMsgVo.getIDValue());
				chargeMsgVo.setMsgReceiver(msgVo.getMsgSender());
				chargeMsgVo.setHSNDUNS(msgVo.getOSNDUNS());
				txnLog.setRcvDomain(msgVo.getMsgSender());
			} else {
				String provId = province;
				String orgId = SysMapCache.getProvCd(provId).getSysCd();
				String dunsCd = SysMapCache.getProvCd(provId).getDunsCd();
				// bodyChargeVo.setIDValue(bodyMsgVo.getIDValue());
				chargeMsgVo.setMsgReceiver(orgId);
				chargeMsgVo.setOSNDUNS(dunsCd);
				txnLog.setRcvDomain(orgId);
			}
			
			params.put("idType", bodyMsgVo.getIDType());
			params.put("idValue", bodyMsgVo.getIDValue());
//			params.put("transactionID", Serial.genSerialNos(CommonConstant.Sequence.OprId.getValue()));
			//TransactionID设置成32位
			params.put("transactionID", Serial.genSerialNum(CommonConstant.Sequence.OprId.getValue()));

			params.put("actionDate", intTxnDate);
			params.put("actionTime", StrUtil.subString(intTxnTime, 0, 14));
			params.put("cnlTyp", txnLog.getReqCnlType());
			params.put("payedType", "".equals(StringUtil.toTrim(txnLog
					.getPayedType())) ? "01" : txnLog.getPayedType());
			params.put("settleDate", bankMsgVoRtn.getRcvDate());
			params.put("retryNum", DictCodeCache.getDictCode(DictConst.DictId.RetryTimes.getValue(), 
					DictConst.CodeId.RetryTimes.getValue()).getCodeValue2());
			
			//新充值改造的字段
//			params1.put("busiTransID", String.valueOf(intTxnSeq));
//			params1.put("payTransID", String.valueOf(intTxnSeq));
//			params1.put("transactionID", String.valueOf(intTxnSeq));
//			params.put("organID", String.valueOf(txnLog.getReqDomain()));//机构编码
			params.put("organID", String.valueOf(bankID));//机构编码
			params.put("chargeMoney", String.valueOf(txnLog.getPayAmt()));
			params.put("payment",String.valueOf( txnLog.getPayAmt()));
			
//			params1.put("orderNo", txnLog.getOrderId());
//			params1.put("productNo", txnLog.getProductNo());
			//发往省的报文需要带payment字段
//			params1.put("payment",String.valueOf( txnLog.getPayAmt()));
//			params1.put("orderCnt", txnLog.getOrderCnt()+"");
//			params1.put("commision", txnLog.getCommision()+"");
//			params1.put("rebateFee", txnLog.getRebateFee()+"");
//			params1.put("prodDiscount",txnLog.getProdDiscount()+"");
//			params1.put("creditCardFee",txnLog.getCreditCardFee()+"");
//			params1.put("serviceFee", txnLog.getServiceFee()+"");
//			params1.put("activityNo",txnLog.getActivityNo());
//			params1.put("productShelfNo", txnLog.getProductShelfNo());
			
//			params1.put("reserve1","");
//			params1.put("reserve2","");
//			params1.put("reserve3","");
//			params1.put("reserve4","");
			
			//发送充值请求到手机号码归属省
			CrmMsgVo chargeMsgVoRtn = crmChargeBus.execute(chargeMsgVo,
					params, txnLog, bindInfo);
			txnLog.setUserCat(bindInfo.getUserCat()); // crmChargeBus.execute中由于167充值接口没有该字段，所以会赋值错误，得重新在这里赋值。
			// 报文体返回为null说明网状网到crm端超时。
			/*
			 * if(chargeTimeOutRtnCode(chargeMsgVoRtn)){
			 * logger.info("crm端充值超时。"); // 判断是给主还是副号码充值 Map<String, String>
			 * params6 = new HashMap<String, String>(); params6.put("msgSender",
			 * msgVo.getMsgSender()); params6.put("idValue",
			 * bodyMsgVo.getIDValue()); params6.put("origDomain",
			 * msgVo.getHomeDomain()); params6.put("homeDomain",
			 * msgVo.getOrigDomain()); CrmMsgVo queryMsgVoRtn =
			 * crmQueryResultBus.execute(msgVo,params6, txnLog, bindInfo);
			 * if(queryTimeOutRtnCode(queryMsgVoRtn)){
			 * logger.info("crm端查询充值超时。"); msgVoRtn = msgVo;
			 * msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			 * msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			 * bodyMsgVoRtn);
			 * bodyMsgVoRtn.setRspCode(MessageHandler.getCrmErrCode("5A13"));
			 * bodyMsgVoRtn.setRspInfo(MessageHandler.getCrmErrMsg("5A13"));
			 * txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			 * txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * txnLog.
			 * setChlRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("5A13"));
			 * txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("5A13"));
			 * txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			 * txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
			 * upayCsysTxnLogService.modifyLimit(txnLog, bindInfo); return
			 * msgVoRtn; } CrmTransQueryResVo queryBodyMsgRtnVo =
			 * (CrmTransQueryResVo)queryMsgVoRtn.getBody(); if
			 * ((MessageHandler.getWzwErrCode
			 * ("0000").equals(queryMsgVoRtn.getRspCode())) &&
			 * (!MessageHandler.getCrmErrCode("0000").equals(
			 * queryBodyMsgRtnVo.getRspCode()))) {
			 * logger.info("报文体返回为null说明网状网到crm端超时,查询之后，进行冲正处理。"); // 冲正
			 * Map<String, String> params5 = new HashMap<String, String>();
			 * params5.put("cnlTyp", bodyMsgVo.getCnlTyp());
			 * params5.put("bankId", bindInfo.getBankId());
			 * params5.put("reverseTransDt", bodyChargeVo.getActionDate());
			 * params5.put("reverseOriReqSys", ExcConstant.BANK_REQ_SYS);
			 * params5.put("reverseTransId", bodyChargeVo.getTransactionID());
			 * bankReverseBus.execute(bankMsgVo, params5, txnLog, bindInfo); //
			 * 拼接充值失败xml返回 msgVoRtn = msgVo;
			 * msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			 * msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			 * bodyMsgVoRtn);
			 * bodyMsgVoRtn.setRspCode(MessageHandler.getCrmErrCode("5A02"));
			 * bodyMsgVoRtn.setRspInfo(MessageHandler.getCrmErrMsg("5A02"));
			 * txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			 * txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * txnLog.
			 * setChlRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("5A02"));
			 * txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("5A02") + ":"
			 * + "报文体返回为null说明网状网到crm端超时，查询之后，进行冲正处理。");
			 * txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			 * upayCsysTxnLogService.modifyLimit(txnLog, bindInfo); return
			 * msgVoRtn;
			 * 
			 * } }
			 */

			// 报文体返回为null说明网状网到crm端超时。
			/*
			 * if(chargeTimeOutRtnCode(chargeMsgVoRtn)){
			 * logger.info("crm端充值超时。"); // 判断是给主还是副号码充值 Map<String, String>
			 * params6 = new HashMap<String, String>(); params6.put("msgSender",
			 * msgVo.getMsgSender()); params6.put("idValue",
			 * bodyMsgVo.getIDValue()); params6.put("origDomain",
			 * msgVo.getHomeDomain()); params6.put("homeDomain",
			 * msgVo.getOrigDomain()); CrmMsgVo queryMsgVoRtn =
			 * crmQueryResultBus.execute(msgVo,params6, txnLog, bindInfo);
			 * if(queryTimeOutRtnCode(queryMsgVoRtn)){
			 * logger.info("crm端查询充值超时。"); msgVoRtn = msgVo;
			 * msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			 * msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			 * bodyMsgVoRtn);
			 * bodyMsgVoRtn.setRspCode(MessageHandler.getCrmErrCode("5A13"));
			 * bodyMsgVoRtn.setRspInfo(MessageHandler.getCrmErrMsg("5A13"));
			 * txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			 * txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * txnLog.
			 * setChlRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("5A13"));
			 * txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("5A13"));
			 * txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			 * txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
			 * upayCsysTxnLogService.modifyLimit(txnLog, bindInfo); return
			 * msgVoRtn; } CrmTransQueryResVo queryBodyMsgRtnVo =
			 * (CrmTransQueryResVo)queryMsgVoRtn.getBody(); if
			 * ((MessageHandler.getWzwErrCode
			 * ("0000").equals(queryMsgVoRtn.getRspCode())) &&
			 * (!MessageHandler.getCrmErrCode("0000").equals(
			 * queryBodyMsgRtnVo.getRspCode()))) {
			 * logger.info("报文体返回为null说明网状网到crm端超时,查询之后，进行冲正处理。"); // 冲正
			 * Map<String, String> params5 = new HashMap<String, String>();
			 * params5.put("cnlTyp", bodyMsgVo.getCnlTyp());
			 * params5.put("bankId", bindInfo.getBankId());
			 * params5.put("reverseTransDt", bodyChargeVo.getActionDate());
			 * params5.put("reverseOriReqSys", ExcConstant.BANK_REQ_SYS);
			 * params5.put("reverseTransId", bodyChargeVo.getTransactionID());
			 * bankReverseBus.execute(bankMsgVo, params5, txnLog, bindInfo); //
			 * 拼接充值失败xml返回 msgVoRtn = msgVo;
			 * msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			 * msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			 * bodyMsgVoRtn);
			 * bodyMsgVoRtn.setRspCode(MessageHandler.getCrmErrCode("5A02"));
			 * bodyMsgVoRtn.setRspInfo(MessageHandler.getCrmErrMsg("5A02"));
			 * txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			 * txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * txnLog.
			 * setChlRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("5A02"));
			 * txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("5A02") + ":"
			 * + "报文体返回为null说明网状网到crm端超时，查询之后，进行冲正处理。");
			 * txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			 * upayCsysTxnLogService.modifyLimit(txnLog, bindInfo); return
			 * msgVoRtn;
			 * 
			 * } }
			 */
			
			//判断充值应答是否没有报文体
			if (chargeMsgVoRtn.getBody() == null ||chargeMsgVoRtn.getBody().equals("")) {
				logger.warn("网站,短信,电话签约缴费接口!充值应答报文空!CRM充值流水:{},CRM应答流水:{},UPAY流水:{},充值省:{},充值号码:{},主号:{}",
						new Object[]{chargeMsgVoRtn.getTransIDO(),chargeMsgVoRtn.getTransIDH(),intTxnSeq,chargeMsgVo.getMsgReceiver(),
						bodyMsgVo.getIDValue(),mainIdValue});
				
				log.warn("网站,短信,电话签约缴费接口!充值应答报文空!CRM充值流水:{},CRM应答流水:{},UPAY流水:{},充值省:{},充值号码:{},主号:{}",
						new Object[]{chargeMsgVoRtn.getTransIDO(),chargeMsgVoRtn.getTransIDH(),intTxnSeq,chargeMsgVo.getMsgReceiver(),
						bodyMsgVo.getIDValue(),mainIdValue});
				
				msgVoRtn = msgVo;
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_5A07.getDesc()+":充值网状网超时");
				convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
						bodyMsgVoRtn);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(chargeMsgVoRtn.getRspCode());
				txnLog.setChlRspDesc(chargeMsgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc()+":充值网状网超时");
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modifyLimit(txnLog, bindInfo);
				logger.debug("ConsumeCrmAciton execute(Object) - end");
				return msgVoRtn;
			}
			CrmChargeResVo bodyChargeVoRtn = chargeMsgVoRtn.getBody() == null
					|| chargeMsgVoRtn.getBody().equals("") ? null
					: (CrmChargeResVo) chargeMsgVoRtn.getBody();
			/*
			 * // 报文头不冲正返回码 List<String> chargeHeadCode =
			 * Arrays.asList(ExcConstant.CHARGER_HEAD_CODE); // 报文体不冲正返回码
			 * List<String> chargeBodyCode =
			 * Arrays.asList(ExcConstant.CHARGER_BODY_CODE); if
			 * (chargeHeadCode.contains(chargeMsgVoRtn.getRspCode()) ||
			 * chargeBodyCode.contains(bodyChargeVoRtn.getRspCode())) {
			 * logger.info("报文头返回码：" + chargeMsgVoRtn.getRspCode() + "报文体返回码：" +
			 * bodyChargeVoRtn.getRspCode() + ":不需要冲正。");
			 * txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
			 * } else { logger.info("crm充值交易失败，进行银行端冲正操作，返回码：" +
			 * msgVoRtn.getRspCode()); // 冲正 Map<String, String> params5 = new
			 * HashMap<String, String>(); params5.put("cnlTyp",
			 * bodyMsgVo.getCnlTyp()); params5.put("bankId",
			 * bindInfo.getBankId()); params5.put("reverseTransDt",
			 * bodyChargeVo.getActionDate()); params5.put("reverseOriReqSys",
			 * ExcConstant.BANK_REQ_SYS); params5.put("reverseTransId",
			 * bodyChargeVo.getTransactionID());
			 * bankReverseBus.execute(bankMsgVo, params5, txnLog, bindInfo); //
			 * 拼接充值失败xml返回 msgVoRtn = msgVo;
			 * msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			 * msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			 * convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
			 * bodyMsgVoRtn);
			 * bodyMsgVoRtn.setRspCode(MessageHandler.getCrmErrCode("5A02"));
			 * bodyMsgVoRtn.setRspInfo(MessageHandler.getCrmErrMsg("5A02"));
			 * txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
			 * .toString());
			 * txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			 * txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			 * txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("5A02"));
			 * txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("5A02") + ":"
			 * + "crm充值交易失败，进行银行端冲正操作。");
			 * txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			 * upayCsysTxnLogService.modifyLimit(txnLog, bindInfo); return
			 * msgVoRtn; }
			 */
			
			txnLog.setRcvRspType(chargeMsgVoRtn.getRspType());
			txnLog.setRcvRspCode(chargeMsgVoRtn.getRspCode());
			txnLog.setRcvRspDesc(chargeMsgVoRtn.getRspDesc());
			
			// 内部超时
			if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(chargeMsgVoRtn.getRspCode())) {	
				log.warn("网站,短信,电话签约缴费接口!UPAY超时!CRM充值流水:{},CRM应答流水:{},UPAY流水:{},充值省:{},充值号码:{},主号:{}",
						new Object[]{chargeMsgVoRtn.getTransIDO(),chargeMsgVoRtn.getTransIDH(),intTxnSeq,chargeMsgVo.getMsgReceiver(),
						bodyMsgVo.getIDValue(),mainIdValue});
				logger.warn("网站,短信,电话签约缴费接口!UPAY超时!CRM充值流水:{},CRM应答流水:{},UPAY流水:{},充值省:{},充值号码:{},主号:{}",
						new Object[]{chargeMsgVoRtn.getTransIDO(),chargeMsgVoRtn.getTransIDH(),intTxnSeq,chargeMsgVo.getMsgReceiver(),
						bodyMsgVo.getIDValue(),mainIdValue});
				
				String errCode = CrmErrorCodeCache.getCrmErrCode("U99998");
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				msgVoRtn = msgVo;
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				bodyMsgVoRtn.setRspCode(errCode);
				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
				convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
						bodyMsgVoRtn);
			} 
			if(!(RspCodeConstant.Crm.CRM_0000.getValue().equals(bodyChargeVoRtn.getRspCode()))){
//				String errCode = CrmErrorCodeCache.getCrmErrCode(bodyMsgVoRtn.getRspCode());
				
				log.warn("网站,短信,电话签约缴费接口!充值失败!UPAY流水:{},发起省:{},充值号码:{},CRM应答码:{}",
						new Object[]{intTxnSeq,paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyChargeVoRtn.getRspCode()});
				logger.warn("网站,短信,电话签约缴费接口!充值失败!UPAY流水:{},发起省:{},充值号码:{},CRM应答码:{}",
						new Object[]{intTxnSeq,paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyChargeVoRtn.getRspCode()});
				msgVoRtn = msgVo;
				msgVoRtn.setRspType(chargeMsgVoRtn.getRspType());
				msgVoRtn.setRspCode(chargeMsgVoRtn.getRspCode());
				msgVoRtn.setRspDesc(chargeMsgVoRtn.getRspDesc());
				bodyMsgVoRtn.setRspCode(bodyChargeVoRtn.getRspCode());
				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.getDescByValue(bodyChargeVoRtn.getRspCode()));
				convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,bodyMsgVoRtn);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(bodyChargeVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(bodyChargeVoRtn.getRspCode()));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				
				upayCsysPayLimitService.modifyLimit(txnLog, bindInfo);
			}else {
				
				log.info("网站,短信,电话签约缴费接口!充值成功!UPAY流水:{},发起省:{},充值号码:{},CRM应答码:{}",
						new Object[]{intTxnSeq,paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyChargeVoRtn.getRspCode()});
				logger.info("网站,短信,电话签约缴费接口!充值成功!UPAY流水:{},发起省:{},充值号码:{},CRM应答码:{}",
						new Object[]{intTxnSeq,paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyChargeVoRtn.getRspCode()});
				msgVoRtn = msgVo;
				msgVoRtn.setRspType(chargeMsgVoRtn.getRspType());
				msgVoRtn.setRspCode(chargeMsgVoRtn.getRspCode());
				msgVoRtn.setRspDesc(chargeMsgVoRtn.getRspDesc());
				bodyMsgVoRtn.setRspCode(bodyChargeVoRtn.getRspCode());
				bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.getDescByValue(bodyChargeVoRtn.getRspCode()));
				convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,bodyMsgVoRtn);
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlRspCode(msgVoRtn.getRspCode());
				txnLog.setChlRspDesc(msgVoRtn.getRspDesc());
				txnLog.setChlSubRspCode(bodyChargeVoRtn.getRspCode());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(bodyChargeVoRtn.getRspCode()));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			}
			
			// UpayCsysBillPay upayCsysBillPay = new UpayCsysBillPay();
			// BeanUtils.copyProperties(txnLog, upayCsysBillPay);
			// upayCsysBillPay
			// .setInvoiceFlag(CommonConstant.YesOrNo.No.toString());
			// upayCsysBillPay
			// .setStatus(CommonConstant.BillPayStatus.ConfirmStatus
			// .toString());
			// upayCsysAllTxnLogService.addAll(upayCsysBillPay, obj, txnLog);
			upayCsysTxnLogService.modify(txnLog);
			logger.debug("ConsumeCrmAciton execute(Object) - end");
			log.succ("内部流水:{},返回码:{}",new Object[]{intTxnSeq,msgVoRtn.getRspCode()});
			logger.debug("ConsumeCrmAciton execute(Object) - end");
			return msgVoRtn;
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			log.error("网站,短信,电话签约缴费接口!运行期异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},充值号码:{},缴费操作流水号:{}",
					new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
					paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getTransactionID()});
			logger.error("网站,短信,电话签约缴费接口!运行期异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},充值号码:{},缴费操作流水号:{},异常{}",
					new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
					paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getTransactionID(),e.getMessage()});
			logger.error("网站,短信,电话签约缴费接口!运行期异常,UPAY流水: " + intTxnSeq, e);
			msgVoRtn = msgVo;
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			bodyMsgVoRtn.setRspCode(errCode);
			bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
					bodyMsgVoRtn);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode) + ":"
					+ e.getMessage());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			if (limitFlag) {
				upayCsysTxnLogService.modifyLimit(txnLog, bindInfo);
			} else {
				upayCsysTxnLogService.modify(txnLog);
			}
			logger.debug("ConsumeCrmAciton execute(Object) - end");
			return msgVoRtn;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			log.error("网站,短信,电话签约缴费接口!系统异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},充值号码:{},缴费操作流水号:{}",
					new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
					paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getTransactionID()});
			logger.error("网站,短信,电话签约缴费接口!系统异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},充值号码:{},缴费操作流水号:{},异常{}",
					new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
					paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getTransactionID(),e.getMessage()});
			logger.error("网站,短信,电话签约缴费接口!系统异常,UPAY流水: " + intTxnSeq, e);
			msgVoRtn = msgVo;
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			bodyMsgVoRtn.setRspCode(errCode);
			bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
					bodyMsgVoRtn);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode) + ":"
					+ e.getMessage());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			if (limitFlag) {
				upayCsysTxnLogService.modifyLimit(txnLog, bindInfo);
			} else {
				upayCsysTxnLogService.modify(txnLog);
			}
			logger.debug("ConsumeCrmAciton execute(Object) - end");
			return msgVoRtn;
		} catch (Exception e) {
			log.error("网站,短信,电话签约缴费接口!未知异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},充值号码:{},缴费操作流水号:{}",
					new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
					paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getTransactionID()});
			logger.error("网站,短信,电话签约缴费接口!未知异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},充值号码:{},缴费操作流水号:{},异常{}",
					new Object[]{paramData.getTransIDO(),bodyMsgVo.getTransactionID(),intTxnSeq,
					paramData.getMsgSender(),bodyMsgVo.getIDValue(),bodyMsgVo.getTransactionID(),e.getMessage()});
			logger.error("网站,短信,电话签约缴费接口!系统异常,UPAY流水: " + intTxnSeq, e);
			msgVoRtn = msgVo;
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			bodyMsgVoRtn.setRspCode(RspCodeConstant.Crm.CRM_5A06.getValue());
			
			//注释掉输出到应答报文的错误信息(该信息可能包含SQL异常) 20131213 modify by weiyi
//			String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_230?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_230);
//			bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc()+":"+errDesc);
			
			bodyMsgVoRtn.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc());
			
			convertMsgVoRtn(intTxnSeq, intTxnTime, msgVoRtn, bodyMsgVo,
					bodyMsgVoRtn);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A06.getDesc() + ":"
					+ e.getMessage());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			if (limitFlag) {
				upayCsysTxnLogService.modifyLimit(txnLog, bindInfo);
			} else {
				upayCsysTxnLogService.modify(txnLog);
			}
			logger.debug("ConsumeCrmAciton execute(Object) - end");
			return msgVoRtn;
		}
	}
	private void convertMsgVoRtn(String intTxnSeq, String intTxnTime,
			CrmMsgVo msgVoRtn, CrmConsumeReqVo bodyMsgVo,
			CrmConsumeResVo bodyMsgVoRtn) {
		msgVoRtn.setTestFlag(testFlag);
		msgVoRtn.setTransIDH(intTxnSeq);
		msgVoRtn.setTransIDHTime(StrUtil.subString(intTxnTime, 0, 14));
		msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.toString());
		bodyMsgVoRtn.setActionDate(bodyMsgVo.getActionDate());
		bodyMsgVoRtn.setIDType(bodyMsgVo.getIDType());
		bodyMsgVoRtn.setIDValue(bodyMsgVo.getIDValue());
		bodyMsgVoRtn.setTransactionID(bodyMsgVo.getTransactionID());
		msgVoRtn.setBody(bodyMsgVoRtn);
	}
}

