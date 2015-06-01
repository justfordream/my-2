package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultSignReq;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultSignRes;
import com.huateng.toolbox.json.JacksonUtils;
import com.huateng.toolbox.utils.DateUtil;

/**
 * 网关签约结果通知，建行特殊流程
 * 
 * @author zeng.j
 * 
 */
@Controller("simpleGateSignNoticeAction")
@Scope("prototype")
public class SimpleGateSignNoticeAction extends
		AbsBaseAction<Object, CoreResultSignRes> {

	@Autowired
	private IUpayCsysTxnLogTmpService upayCsysTxnLogTmpService;

	@Override
	public CoreResultSignRes execute(Object param) throws AppBizException {
		logger.debug("SimpleGateSignNoticeAction execute(Object) - start ");

		CoreResultSignReq reqMsg = JacksonUtils.json2Bean(param.toString(),
				CoreResultSignReq.class);
		CoreResultSignRes resMsg = new CoreResultSignRes();
		UpayCsysTxnLogTmp logTmp = null;
		try {
			// 查询交易流水
			Map<String, Object> txnParam = new HashMap<String, Object>();
			txnParam.put("orderId", reqMsg.getOrderID());
			logTmp = upayCsysTxnLogTmpService.findObj(txnParam);
			if (logTmp == null) {
				logger.warn("网关签约结果通知,签约交易不存在,订单号:{},签约协议号:{}",
						new Object[] {reqMsg.getOrderID(), reqMsg.getSubID()});
				log.warn("网关签约结果通知,签约交易不存在,订单号:{},签约协议号:{}", 
						new Object[] {reqMsg.getOrderID(), reqMsg.getSubID()});
				resMsg.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
				resMsg.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc() + "签约交易不存在");
				return resMsg;
			}
			resMsg.setActionDate(logTmp.getIntTxnDate());
			resMsg.setBankAcctID(logTmp.getBankAccId());
			resMsg.setBankAcctType(logTmp.getBankAcctType());
			resMsg.setBankID(logTmp.getBankId());
			resMsg.setCLIENTIP(logTmp.getClientIp());
			resMsg.setMCODE(RspCodeConstant.Crm.GATA_CODE_00002.getValue());
			resMsg.setOrigDomain(logTmp.getMerId());
			resMsg.setPayType(logTmp.getPayType());
			resMsg.setRechAmount(String.valueOf(logTmp.getRechAmount()));
			resMsg.setRechThreshold(String.valueOf(logTmp.getRechThreshold()));
			resMsg.setSessionID(logTmp.getReqSessionId());
			resMsg.setSubID(logTmp.getSubId());
			resMsg.setSubTime(logTmp.getSubTime());
			resMsg.setTransactionID(logTmp.getReqOprId());
			resMsg.setUserCat(logTmp.getPayedType());
			resMsg.setUserID(logTmp.getUserId()); 
			resMsg.setUserIDType(logTmp.getUserType());
			resMsg.setUserName(logTmp.getUserName());
			if (CommonConstant.TxnStatus.TxnSuccess.getValue().equals(logTmp.getStatus())) {
				logger.info("网关签约结果通知,网关签约前台结果通知为成功,订单号:{},签约协议号:{}",
						new Object[] {reqMsg.getOrderID(), reqMsg.getSubID()});
				log.succ("网关签约结果通知,网关签约前台结果通知为成功,订单号:{} ,签约协议号:{}",
						new Object[] {reqMsg.getOrderID(), reqMsg.getSubID()});
			} else {
				logger.error("网关签约结果通知,网关签约前台结果通知为失败,或者未通知,订单号:{} ,签约协议号:{}",
						new Object[] {reqMsg.getOrderID(), reqMsg.getSubID()});
				log.error("网关签约结果通知,网关签约前台结果通知为失败 ,或者未通知,订单号:{},签约协议号:{}",
						new Object[] {reqMsg.getOrderID(), reqMsg.getSubID()});
			}
			resMsg.setRspCode(RspCodeConstant.Gate.GATE_0000.getValue());
			resMsg.setRspInfo(RspCodeConstant.Gate.GATE_0000.getDesc());
			resMsg.setBackURL(logTmp.getBackUrl());
			resMsg.setServerURL(logTmp.getServerUrl());
			return resMsg;
		} catch (AppRTException e) {
			log.error("网关签约结果通知,内部异常,订单号:{},签约协议号:{},错误代码:{},描述:{}" , 
					new Object[] {reqMsg.getOrderID(), reqMsg.getSubID(),e.getCode(),e});
			logger.error("网关签约结果通知,订单号:{},签约协议号:{},内部异常,代码:{}" , 
					new Object[] {reqMsg.getOrderID(), reqMsg.getSubID(),e.getCode()});
			logger.error("网关签约结果通知,业务异常:",e);
			// 更新交易流水
			if (null != logTmp) {
				logTmp.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				logTmp.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				upayCsysTxnLogTmpService.modify(logTmp);
			}
			// 拼装网关应答
			resMsg.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			resMsg.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc());
			resMsg.setServerURL(logTmp.getServerUrl());
			resMsg.setBackURL(logTmp.getBackUrl());
			return resMsg;
		} catch (Exception e) {
			logger.error("网关签约结果通知,未知异常!内部流水:{},订单号:{},签约协议号:{},描述:{}", 
					new Object[] {logTmp.getIntTxnSeq(),reqMsg.getOrderID(), reqMsg.getSubID(),e});
			log.error("网关签约结果通知,业务异常:",e);
			log.error("网关签约结果通知,未知异常!内部流水:{},订单号:{},签约协议号:{},描述:{}" , 
					new Object[] {logTmp.getIntTxnSeq(),reqMsg.getOrderID(), reqMsg.getSubID()});
			// 拼装网关应答
			resMsg.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			resMsg.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc());
			resMsg.setServerURL(null != logTmp && null != logTmp.getServerUrl() ? logTmp
					.getServerUrl() : null);
			resMsg.setBackURL(null != logTmp && null != logTmp.getBackUrl() ? logTmp
					.getBackFlag() : null);
			return resMsg;
		}
	}

}
