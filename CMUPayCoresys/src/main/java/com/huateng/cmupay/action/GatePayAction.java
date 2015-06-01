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
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CmuPayRequest;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultRsp;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.cmupay.utils.UUIDGenerator;
import com.huateng.toolbox.json.JacksonUtils;
import com.huateng.toolbox.utils.DateUtil;

/**
 * 网关支付
 * 
 * @author zeng.j
 * 
 */
@Controller("gatePayAction")
@Scope("prototype")
public class GatePayAction extends AbsBaseAction<Object, CoreResultRsp> {

	@Autowired
	private IUpayCsysTxnLogTmpService upayCsysTxnLogTmpService;

	@Override
	public CoreResultRsp execute(Object param) throws AppBizException {
		logger.debug("GatePayAction execute(Object) - start ");
		CmuPayRequest reqMsg = null;
		UpayCsysTransCode transCode = null;
		UpayCsysTxnLogTmp logTmp = new UpayCsysTxnLogTmp();
		CoreResultRsp resMsg = new CoreResultRsp();
		String intTxnSeq = Serial.genSerialNo(CommonConstant.Sequence.IntSeq.toString());
		try {
			reqMsg = JacksonUtils.json2Bean(param.toString(),
					CmuPayRequest.class);
		
			resMsg.setBackURL(reqMsg.getBackURL());
			resMsg.setServerURL(reqMsg.getMerURL());

			String intTxnTime = DateUtil.getDateyyyyMMddHHmmss();
			String intTxnDate = upayCsysBatCutCtlService.findCutOffDate(ExcConstant.CUT_OFF_DATE);
			Long seqValue = upayCsysSeqMapInfoService.selectSeqValue(ExcConstant.TXN_LOG_SEQ);
			if (!StringUtils.isBlank(reqMsg.getTransCode())) {
				
				//根据发起发机构以及报文体操作流水号，查找交易流水是否重复 by xuyunbo 2013-11-26
				Map<String, Object> paramss = new HashMap<String, Object>();
				
				paramss.put("reqDomain",reqMsg.getMerID()); 
				paramss.put("reqTransId", reqMsg.getOrderID());
				UpayCsysTxnLogTmp upayCsysTxnLogTmp=  upayCsysTxnLogTmpService.findObj(paramss);
				
				if (upayCsysTxnLogTmp != null) {
					log.warn("该交易为重复交易");
					resMsg.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
					resMsg.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc()+"reqOprId:"+reqMsg.getOrderID());
					logger.debug("GatePayAction execute(Object) - end ");
					return resMsg;

				}
				
				logger.info("网关支付,订单号:{},内部交易流水:{},查询{}的交易方式和业务渠道", new Object[]{reqMsg.getOrderID(),intTxnSeq,
						reqMsg.getTransCode()});
				log.info("网关支付,订单号:{,内部交易流水:{},查询{}的交易方式和业务渠道", new Object[]{reqMsg.getOrderID(),intTxnSeq,
						reqMsg.getTransCode()});
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("transCode", reqMsg.getTransCode());
				transCode = upayCsysTransCodeService.findObj(params);
				logger.info("网关支付,订单号:{},内部交易流水:{},查询{}的结果", new Object[]{reqMsg.getOrderID(),intTxnSeq,
						transCode});
				log.info("网关支付,订单号:{},内部交易流水:{},查询{}的结果",new Object[]{ reqMsg.getOrderID(),intTxnSeq,
						transCode});
				if (transCode != null) {
					logTmp.setPayMode(transCode.getPayMode());
					logTmp.setBussType(transCode.getBussType());
					logTmp.setBussChl(transCode.getBussChl());
				} else {
					logger.warn("网关支付,订单号: {} , 内部交易流水:{},没有 {}交易记录直接返回",
							new Object[]{reqMsg.getOrderID(),intTxnSeq, reqMsg.getTransCode()});
					log.warn("网关支付,订单号: {} ,内部交易流水:{}, 没有 {}交易记录直接返回",
							new Object[]{reqMsg.getOrderID(),intTxnSeq, reqMsg.getTransCode()});				
					resMsg.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
					resMsg.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc());
					logger.debug("GatePayAction execute(Object) - end ");
					return resMsg;
				}
			}
			
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(reqMsg.getIDValue());	
			String idProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();	

			if(StringUtils.isBlank(idProvince)){
				logger.warn("网关支付,订单号：{}, {}该手机号码错误，没有相关的归属地信息",
						new Object[]{reqMsg.getOrderID(),reqMsg.getIDValue()});
				log.warn("网关支付,订单号：{}, {}该手机号码错误，没有相关的归属地信息",
						new Object[]{reqMsg.getOrderID(),reqMsg.getIDValue()});
				resMsg.setRspCode(RspCodeConstant.Crm.CRM_2A22.getValue());
				resMsg.setRspInfo(RspCodeConstant.Crm.CRM_2A22.getDesc());
				logger.debug("GatePayAction execute(Object) - end ");
				return resMsg;
			}
			
			//验证归属省 
			if(!reqMsg.getMerID().equals(idProvince+"1")){
				logger.warn("会话流水：{}, {}省代码和手机号码归属省不一致",
						new Object[]{reqMsg.getOrderID(),reqMsg.getIDValue()});
				log.warn("会话流水：{}, {}省代码和手机号码归属省不一致",
						new Object[]{reqMsg.getOrderID(),reqMsg.getIDValue()});
				resMsg.setRspCode(RspCodeConstant.Market.MARKET_014A10.getValue());
				resMsg.setRspInfo(RspCodeConstant.Market.MARKET_014A10.getDesc());
				return resMsg;
			}
			
			logTmp.setSeqId(seqValue);
			logTmp.setIntTxnSeq(intTxnSeq);
			logTmp.setIntTransCode(reqMsg.getTransCode());
			logTmp.setIntTxnDate(intTxnDate);
			logTmp.setIntTxnTime(intTxnTime);
			logTmp.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());

			logTmp.setReqCnlType(CommonConstant.CnlType.BankEleChannel.getValue());
			logTmp.setReqTransId(reqMsg.getOrderID());
			logTmp.setReqOprId(UUIDGenerator.generateUUID());
			logTmp.setReqTranshTm(reqMsg.getOrderTime());
			logTmp.setReqOprTm(reqMsg.getOrderTime());
			logTmp.setPayAmt(StringFormat.paseLong(reqMsg.getPayed()));
			logTmp.setIdType(reqMsg.getIDType());
			logTmp.setIdValue(reqMsg.getIDValue());
			logTmp.setIdProvince(idProvince);
			logTmp.setBankId(reqMsg.getBankID());
			logTmp.setBackUrl(reqMsg.getBackURL());
			logTmp.setServerUrl(reqMsg.getMerURL());
			logTmp.setOrderId(reqMsg.getOrderID());
			logTmp.setMerVar(reqMsg.getMerVAR());
			logTmp.setReqDomain(reqMsg.getMerID());
			logTmp.setMerId(reqMsg.getMerID());
			logTmp.setOrderTm(reqMsg.getOrderTime());

			logTmp.setBackFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			logTmp.setClientIp(reqMsg.getCLIENTIP());
			upayCsysTxnLogTmpService.add(logTmp);
			logger.debug("网关支付,success 记录网关支付交易流水");

			resMsg.setRspCode(RspCodeConstant.Gate.GATE_0000.getValue());
			resMsg.setRspInfo(RspCodeConstant.Gate.GATE_0000.getDesc());
			logTmp.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			logTmp.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			logger.info("网关支付,订单号:{}, 成功 网关支付,内部交易流水:{},应答码:0000",new Object[]{reqMsg.getOrderID(),intTxnSeq});
			log.succ("网关支付,订单号:{} ,成功 网关支付 ,内部交易流水:{},应答码:0000",new Object[]{reqMsg.getOrderID(),intTxnSeq});
			upayCsysTxnLogTmpService.modify(logTmp);
			logger.debug("GatePayAction execute(Object) - end ");
			return resMsg;
		} catch (AppRTException e) {
			log.error("网关支付,内部异常!内部交易流水号:{}" , new Object[] {intTxnSeq});
			logger.error("网关支付,内部异常,代码:{},内部交易流水号:{}" , new Object[] {e.getCode(),intTxnSeq});
			logger.error("网关支付,内部异常:",e);
			resMsg.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			resMsg.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc());
			logTmp.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			logTmp.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			upayCsysTxnLogTmpService.modify(logTmp);
			logger.debug("GatePayAction execute(Object) - end ");
			return resMsg;
		} catch (Exception e) {
			log.error("网关支付,内部异常!内部交易流水号:{}" , new Object[] {intTxnSeq});
			logger.error("网关支付,内部异常!内部交易流水号:{}" , new Object[] {intTxnSeq});
			logger.error("网关支付,未知异常:",e);
			resMsg.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			resMsg.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc());
			logTmp.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			logTmp.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			upayCsysTxnLogTmpService.modify(logTmp);
			logger.debug("GatePayAction execute(Object) - end ");
			return resMsg;
		}
	}

}
