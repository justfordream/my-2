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
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CmuSignRequest;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultRsp;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.json.JacksonUtils;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * @author ning.z 网厅发起签约
 * 
 */
@Controller("gateSignAction")
@Scope("prototype")
public class GateSignAction extends AbsBaseAction<Object, CoreResultRsp> {

	@Autowired
	private IUpayCsysTxnLogTmpService uPayCsysTxnLogTmpService;

	@Override
	public CoreResultRsp execute(Object param) throws AppBizException {
		logger.debug("GateSignAction execute(Object)- start 支付网关签");

		UpayCsysTxnLogTmp logTmp = new UpayCsysTxnLogTmp();
		CoreResultRsp webRsp = new CoreResultRsp();
		UpayCsysTransCode transCode = null;
		String transIDH = Serial.genSerialNo(CommonConstant.Sequence.IntSeq.getValue());// 落地方交易流水号
		try {
			CmuSignRequest reqMsg = JacksonUtils.json2Bean(param.toString(),CmuSignRequest.class);
			String sessionId = reqMsg.getSessionID();
			logger.debug("网厅发起签约,解析报文成功");
			String reqTransId = Serial.genSerialNo(CommonConstant.Sequence.IntSeq.getValue());
			String transIDHTime = DateUtil.getDateyyyyMMddHHmmss();
			String intTxnDate = DateUtil.getDateyyyyMMdd();// 从数据库获取
			Long seqId = upayCsysSeqMapInfoService.selectSeqValue(ExcConstant.TXN_LOG_SEQ); // 交易流水seq
			Long bainSeqId = upayCsysSeqMapInfoService.selectSeqValue(ExcConstant.BIND_INFO_SEQ); // 签约信息seq
			if (!StringUtils.isBlank(reqMsg.getTransCode())) {
				
				//根据发起发机构以及报文体操作流水号，查找交易流水是否重复 by xuyunbo 2013-11-26
				Map<String, Object> paramss = new HashMap<String, Object>();
				
				paramss.put("reqDomain",reqMsg.getOrigDomain());
				paramss.put("reqOprId",reqMsg.getTransactionID());
				UpayCsysTxnLogTmp upayCsysTxnLogTmp=  uPayCsysTxnLogTmpService.findObj(paramss);
				 
				if (upayCsysTxnLogTmp != null) {
					log.error("该交易为重复交易！流水号[{}]",reqMsg.getSessionID());
					webRsp.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
					webRsp.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc()
							+ "流水号："+reqMsg.getTransactionID());
					webRsp.setBackURL(logTmp.getBackUrl());   
					webRsp.setServerURL(logTmp.getServerUrl());
					logger.debug("GateSignAction execute(Object)- end 支付网关签");
					return webRsp;

				}
				
				Map<String, Object> paramse = new HashMap<String, Object>();
				paramse.put("reserved2",reqMsg.getSessionID());
				UpayCsysTxnLogTmp upayCsysTxnLogTmp1=  uPayCsysTxnLogTmpService.findObj(paramse);
				 
				if (upayCsysTxnLogTmp1 != null) {
					log.error("该交易为重复交易！流水号[{}]",reqMsg.getSessionID());
					webRsp.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
					webRsp.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc()
							+ "流水号："+reqMsg.getTransactionID());
					webRsp.setBackURL(logTmp.getBackUrl());   
					webRsp.setServerURL(logTmp.getServerUrl());
					logger.debug("GateSignAction execute(Object)- end 支付网关签");
					return webRsp;
				}
				
				
				logger.debug("网厅发起签约,会话标示: {} ,查询{} 的交易方式和业务渠道", sessionId,reqMsg.getTransCode());
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("transCode", reqMsg.getTransCode());
				transCode = upayCsysTransCodeService.findObj(params);
				logger.debug("网厅发起签约,查询{} 的结果{}", reqMsg.getTransCode(), transCode);
				if (transCode != null) {
					logTmp.setPayMode(transCode.getPayMode());
					logTmp.setBussType(transCode.getBussType());
					logTmp.setBussChl(transCode.getBussChl());// TODO
				} else {
					logger.warn("网厅发起签约,没有:{}交易记录直接返回,内部交易流水:{}", new Object[] {transCode, transIDH });
					log.warn("网厅发起签约,没有:{}交易记录直接返回,内部交易流水:{}", new Object[] {transCode, transIDH });
					webRsp.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
					webRsp.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc()
							+ "失败:没有查询到" + reqMsg.getTransCode()+ " 的交易类型和业务类型");
					webRsp.setBackURL(logTmp.getBackUrl());
					webRsp.setServerURL(logTmp.getServerUrl());
					logger.debug("GateSignAction execute(Object)- end 支付网关签");
					return webRsp;
				}
			}
//			String idProvince = upayCsysImsiLdCdService
//					.findProvinceByMobileNumber(reqMsg.getIDValue());
//			String idProvince = ProvAreaCache.getProvAreaByPrimary(reqMsg.getIDValue());
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(reqMsg.getIDValue());
			String idProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
			if(StringUtils.isBlank(idProvince)){
				logger.warn("网厅发起签约,会话流水{},{}该手机号码错误,没有查询到相关的归属地！", new Object[] {sessionId,reqMsg.getIDValue()});
				log.warn("网厅发起签约,会话流水{},{}该手机号码错误,没有查询到相关的归属地！", new Object[] {sessionId,reqMsg.getIDValue() });
				webRsp.setRspCode(RspCodeConstant.Crm.CRM_2A22.getValue());
				webRsp.setRspInfo(RspCodeConstant.Crm.CRM_2A22.getDesc()
						+ reqMsg.getIDValue()+"该手机号码错误,没有查询到相关的归属地！");
				webRsp.setBackURL(logTmp.getBackUrl());
				webRsp.setServerURL(logTmp.getServerUrl());
				logger.debug("GateSignAction execute(Object)- end 支付网关签");
				return webRsp;
			}
			//验证归属省 
			if(!reqMsg.getOrigDomain().equals(idProvince+"1")){
				logger.warn("会话流水：{}, {}省代码和手机号码归属省不一致",
						new Object[]{sessionId,reqMsg.getIDValue()});
				log.warn("会话流水：{}, {}省代码和手机号码归属省不一致",
						new Object[]{sessionId,reqMsg.getIDValue()});
				webRsp.setRspCode(RspCodeConstant.Market.MARKET_014A10.getValue());
				webRsp.setRspInfo(RspCodeConstant.Market.MARKET_014A10.getDesc());
				return webRsp;
			}
			
			// 记录交易流水
			logTmp.setSeqId(seqId);
			logTmp.setIntTxnDate(intTxnDate);// 内部交易日期
			logTmp.setIntTxnSeq(transIDH);
			logTmp.setIntTransCode(reqMsg.getTransCode());
			logTmp.setIntTxnTime(transIDHTime);

			logTmp.setReqSessionId(reqMsg.getSessionID());
			logTmp.setOrderId(reqMsg.getSessionID());
            
			logTmp.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
			logTmp.setReqBipCode("-");
			logTmp.setReqActivityCode("");
			logTmp.setReqTransId(reqMsg.getSessionID());
			logTmp.setReqOprId(reqMsg.getTransactionID());
			logTmp.setReqOprDt("");
			logTmp.setReqOprTm(reqMsg.getSubTime());
			logTmp.setReqDomain(reqMsg.getOrigDomain());
			logTmp.setReconciliationFlag(CommonConstant.ReConllection.conllection_0.getValue());
			logTmp.setMerId(reqMsg.getOrigDomain());
			logTmp.setReqTransDt("");
			logTmp.setReqTranshTm(reqMsg.getSubTime());
			logTmp.setMainFlag(CommonConstant.Mainflag.Master.getValue());
			logTmp.setIdType(reqMsg.getIDType());
			logTmp.setIdValue(reqMsg.getIDValue());
			logTmp.setIdProvince(idProvince);
			logTmp.setOriOrgId(null);// TODO
			logTmp.setOriOprTransId(null);// 原交易流水号
			logTmp.setOriReqDate(null);//
			logTmp.setBackFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setBankId(reqMsg.getBankID());
			logTmp.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setLastUpdTime(transIDHTime);
			logTmp.setBackUrl(reqMsg.getBackURL());
			logTmp.setServerUrl(reqMsg.getServerURL());
			logTmp.setPayType(reqMsg.getPayType());
			logTmp.setClientIp(reqMsg.getCLIENTIP());
			if (StringUtils.isNotBlank(logTmp.getPayType())
					&& CommonConstant.PayWay.AutoConsume.getValue().equals(logTmp.getPayType())) {
				logTmp.setRechAmount(StringFormat.paseLong(reqMsg.getRechAmount()));
				logTmp.setRechThreshold(StringFormat.paseLong(reqMsg.getRechThreshold()));
			}
			// 用于银行签约结果通知时标识此交易
			logTmp.setReserved2(reqMsg.getSessionID());
			uPayCsysTxnLogTmpService.add(logTmp);
			logger.debug("网厅发起签约,记录交易流水成功");

			logger.info("网厅发起签约,会话标示: {} , 查询签约关系,解析后的报文： {} ", sessionId,
					JacksonUtils.bean2Json(reqMsg));
			Map<String, Object> mainParam = new HashMap<String, Object>();
			mainParam.put("idType", reqMsg.getIDType());
			mainParam.put("idValue", reqMsg.getIDValue());
			UpayCsysBindInfo mainInfo = upayCsysBindInfoService.findObj(mainParam);
			// TODO 判断是否已签约为副号码
			if (mainInfo == null) {
				logger.info("网厅发起签约,会话标示:{} ,该号码:{}无签约记录，可以签约", sessionId,reqMsg.getIDValue());
				mainInfo = new UpayCsysBindInfo();
				logger.info("网厅发起签约,接收的省: {}", reqMsg.getOrigDomain());
				mainInfo.setIdProvince(reqMsg.getOrigDomain());
				mainInfo.setSeqId(bainSeqId);
				mainInfo.setSignCnlType("02");// TODO
//				String provId = upayCsysImsiLdCdService.findProvinceByMobileNumber(reqMsg.getIDValue());
//				String provId = ProvAreaCache.getProvAreaByPrimary(reqMsg.getIDValue());
				ProvincePhoneNum subProvincePhoneNum = findProvinceByMobileNumber(reqMsg.getIDValue());
				String provId = subProvincePhoneNum == null ? null : subProvincePhoneNum.getProvinceCode();
//				String provId = findProvinceByMobileNumber(reqMsg.getIDValue());
				mainInfo.setSignOrgId(SysMapCache.getProvCd(provId).getSysCd());
				mainInfo.setMainFlag(CommonConstant.Mainflag.Master.getValue());
				mainInfo.setPayType("");
				mainInfo.setIdType(reqMsg.getIDType());
				mainInfo.setIdValue(reqMsg.getIDValue());
				mainInfo.setStatus(CommonConstant.BindStatus.PreBind.getValue());
				mainInfo.setLastUpdOprid("");
				mainInfo.setLastUpdTime(transIDHTime);
				mainInfo.setBankId(reqMsg.getBankID());
				mainInfo.setReserved1(reqMsg.getSessionID());
				mainInfo.setPayType(reqMsg.getPayType());
				if (StringUtils.isNotBlank(logTmp.getPayType())
						&& CommonConstant.PayWay.AutoConsume.getValue().equals(logTmp.getPayType())) {
					mainInfo.setRechAmount(StringFormat.paseLong(reqMsg.getRechAmount()));
					mainInfo.setRechThreshold(StringFormat.paseLong(reqMsg.getRechThreshold()));
				}
				// 更新交易流水
				logTmp.setChlRspCode(RspCodeConstant.Gate.GATE_0000.getValue());
				logTmp.setChlRspDesc(RspCodeConstant.Gate.GATE_0000.getDesc());
				logTmp.setChlSubRspCode(RspCodeConstant.Gate.GATE_0000.getValue());
				logTmp.setChlSubRspDesc(RspCodeConstant.Gate.GATE_0000.getDesc());
				logTmp.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
				// upayCsysBindInfoService.modifyLogTmpAndAddBindInfo(mainInfo,
				// logTmp);
				uPayCsysTxnLogTmpService.modify(logTmp);
				// 拼装网关应答
				webRsp.setRspCode(RspCodeConstant.Gate.GATE_0000.getValue());
				webRsp.setRspInfo(RspCodeConstant.Gate.GATE_0000.getDesc());
				webRsp.setBackURL(logTmp.getBackUrl());
				webRsp.setServerURL(logTmp.getServerUrl());
				logger.debug("GateSignAction execute(Object)- end 支付网关签");
				return webRsp;
			} else {
				if (mainInfo.getStatus().equals(
						CommonConstant.BindStatus.Bind.getValue())) {
					logger.warn("网厅发起签约,会话标示:{} , 该号码: {} 已经签约，不能再签约,内部交易流水:{}",
							new Object[] { sessionId, reqMsg.getIDValue(),transIDH });
					log.warn("网厅发起签约,会话标示:{} , 该号码: {} 已经签约，不能再签约,内部交易流水:{}",
							new Object[] { sessionId, reqMsg.getIDValue(),transIDH });
					// TODO 更新交易流水
					logTmp.setChlRspCode(RspCodeConstant.Crm.CRM_2A10.getValue());
					logTmp.setChlRspDesc("该号码已签约");
					logTmp.setChlSubRspCode(RspCodeConstant.Crm.CRM_2A10.getValue());
					logTmp.setChlSubRspDesc("该号码已签约");
					logTmp.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					uPayCsysTxnLogTmpService.modify(logTmp);
					// 拼装网关应答
					webRsp.setRspCode(RspCodeConstant.Crm.CRM_2A10.getValue());
					webRsp.setRspInfo("成功:该号码已签约");
					webRsp.setBackURL(logTmp.getBackUrl());
					webRsp.setServerURL(logTmp.getServerUrl());
					logger.debug("GateSignAction execute(Object)- end 支付网关签");
					return webRsp;
				} else {
					logger.info("网厅发起签约,会话标示:{},该号码:{}签约状态为未签约,可以签约", new Object[] {
							sessionId, reqMsg.getIDValue() });
					mainInfo.setIdProvince(reqMsg.getOrigDomain());
					mainInfo.setSignCnlType("");// TODO
					mainInfo.setSignOrgId("");
					mainInfo.setMainFlag(CommonConstant.Mainflag.Master.getValue());
					mainInfo.setPayType("");
					mainInfo.setIdType(reqMsg.getIDType());
					mainInfo.setIdValue(reqMsg.getIDValue());
					mainInfo.setStatus(CommonConstant.BindStatus.PreBind.getValue());
					mainInfo.setLastUpdOprid("");
					mainInfo.setLastUpdTime(transIDHTime);
					mainInfo.setBankId(reqMsg.getBankID());
					mainInfo.setReserved1(reqMsg.getSessionID());
					// 拼装网关应答
					logTmp.setChlRspCode(RspCodeConstant.Gate.GATE_0000.getValue());
					logTmp.setChlRspDesc(RspCodeConstant.Gate.GATE_0000.getDesc());
					logTmp.setChlSubRspCode(RspCodeConstant.Gate.GATE_0000.getValue());
					logTmp.setChlSubRspDesc(RspCodeConstant.Gate.GATE_0000.getDesc());
					logTmp.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
					// upayCsysBindInfoService.modifyLogTmpAndBinfInfo(mainInfo,
					// logTmp);
					uPayCsysTxnLogTmpService.modify(logTmp);
					webRsp.setRspCode(RspCodeConstant.Gate.GATE_0000.getValue());
					webRsp.setRspInfo(RspCodeConstant.Gate.GATE_0000.getDesc());
					webRsp.setBackURL(logTmp.getBackUrl());
					webRsp.setServerURL(logTmp.getServerUrl());
					logger.debug("GateSignAction execute(Object)- end 支付网关签");
					return webRsp;
				}
			}
		} catch (AppRTException e) {
			log.error("网厅发起签约,内部异常!内部交易流水号:{}", new Object[] {transIDH });
			logger.error("网厅发起签约,内部异常,代码:{},内部交易流水号:{}",
					new Object[] { e.getCode(),transIDH });
			logger.error("网厅发起签约,内部异常:",e);
			// TODO 更新交易流水
			logTmp.setChlRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			logTmp.setChlRspDesc(RspCodeConstant.Gate.GATE_9999.getDesc());
			logTmp.setChlSubRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			logTmp.setChlSubRspDesc(RspCodeConstant.Gate.GATE_9999.getDesc());
			logTmp.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			uPayCsysTxnLogTmpService.modify(logTmp);
			// 拼装网关应答
			webRsp.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			webRsp.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc());
			webRsp.setBackURL(logTmp.getBackUrl());
			webRsp.setServerURL(logTmp.getServerUrl());
			logger.debug("GateSignAction execute(Object)- end 支付网关签");
			return webRsp;
		} catch (AppBizException e) {
			log.error("网厅发起签约,业务异常!内部交易流水号:{}", new Object[] {transIDH });
			logger.error("网厅发起签约,业务异常,代码:{},内部交易流水号:{}",
					new Object[] { e.getCode(),  transIDH });
			logger.error("网厅发起签约,业务异常:",e);
			// TODO 更新交易流水
			logTmp.setChlRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			logTmp.setChlRspDesc(RspCodeConstant.Gate.GATE_9999.getDesc());
			logTmp.setChlSubRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			logTmp.setChlSubRspDesc(RspCodeConstant.Gate.GATE_9999.getDesc());
			logTmp.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			uPayCsysTxnLogTmpService.modify(logTmp);
			// 拼装网关应答
			webRsp.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			webRsp.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc());
			webRsp.setBackURL(logTmp.getBackUrl());
			webRsp.setServerURL(logTmp.getServerUrl());
			logger.debug("GateSignAction execute(Object)- end 支付网关签");
			return webRsp;
		} catch (Exception e) {
			log.error("网厅发起签约,内部异常!内部交易流水号:{}", new Object[] {transIDH });
			logger.error("网厅发起签约,内部异常,内部交易流水号:{}", new Object[] { transIDH });
			logger.error("网厅发起签约,未知异常:",e);
			if ("99".equals(logTmp.getStatus())|| "".equals(StringUtil.toTrim(logTmp.getStatus()))) {
				logTmp.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			}
			// TODO 更新交易流水
			logTmp.setChlRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			logTmp.setChlRspDesc(RspCodeConstant.Gate.GATE_9999.getDesc());
			logTmp.setChlSubRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			logTmp.setChlSubRspDesc(RspCodeConstant.Gate.GATE_9999.getDesc());
			uPayCsysTxnLogTmpService.modify(logTmp);
			// 拼装网关应答
			webRsp.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			webRsp.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc());
			webRsp.setBackURL(logTmp.getBackUrl());
			webRsp.setServerURL(logTmp.getServerUrl());
			logger.debug("GateSignAction execute(Object)- end 支付网关签");
			return webRsp;
		}
	}

}