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
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultPayReq;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultPayRes;
import com.huateng.toolbox.json.JacksonUtils;

/**
 * 网关支付结果通知
 * 
 * @author zeng.j
 * 
 * 
 */
@Controller("simpleGatePayNoticeAction")
@Scope("prototype")
public class SimpleGatePayNoticeAction extends
		AbsBaseAction<Object, CoreResultPayRes> {

	@Autowired
	private IUpayCsysTxnLogTmpService upayCsysTxnLogTmpService;

	@Override
	public CoreResultPayRes execute(Object param) throws AppBizException {
		logger.debug("SimpleGatePayNoticeAction execute(Object) - start"
				+ param.toString());
		CoreResultPayReq reqMsg = null;
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		UpayCsysTxnLogTmp logTmp = null;
		CoreResultPayRes resMsg = new CoreResultPayRes();
		try {
			reqMsg = JacksonUtils.json2Bean(param.toString(),
					CoreResultPayReq.class);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderId", reqMsg.getOrderID());
			txnLog = upayCsysTxnLogService.findObj(params);
			logTmp = upayCsysTxnLogTmpService.findObj(params);
			if (null == logTmp) {
				logger.warn("网关支付结果通知,订单号: {},原支付交易不存在", reqMsg.getOrderID());
				log.warn("网关支付结果通知,订单号: {},原支付交易不存在", reqMsg.getOrderID());
				resMsg.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
				resMsg.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc()
						+ "fail原支付交易不存在");
				return resMsg;
			}
			resMsg.setMerID(logTmp.getMerId());
			resMsg.setCurType(CommonConstant.CurType.rmbType.getValue());
			resMsg.setMerVAR(logTmp.getMerVar());
			resMsg.setOrderID(logTmp.getOrderId());
			resMsg.setOrderTime(logTmp.getOrderTm());
			resMsg.setPayed(logTmp.getPayAmt().toString());
			if (null == txnLog) {
				logger.warn("网关支付结果通知,订单号: {},未收到银行支付结果通知", reqMsg.getOrderID());
				log.warn("网关支付结果通知,订单号: {},未收到银行支付结果通知", reqMsg.getOrderID());
			} else {
				logger.info("网关支付结果通知,订单号: {} ,银行支付结果通知状态： {}",
						reqMsg.getOrderID(), txnLog.getStatus());
				if (CommonConstant.TxnStatus.TxnSuccess.getValue().equals(
						txnLog.getStatus())) {
					logger.info("网关支付结果通知,订单号: {} ,网关支付后台结果通知为成功 ",
							reqMsg.getOrderID());
					log.succ("网关支付结果通知,订单号: {} ,网关支付后台结果通知为成功 ",
							reqMsg.getOrderID());
				} else {
					logger.error("网关支付结果通知,订单号: {} ,网关支付后台结果通知为失败,或者未通知 ",
							reqMsg.getOrderID());
					log.error("网关支付结果通知,订单号: {} ,网关支付后台结果通知为失败 ,或者未通知",
							reqMsg.getOrderID());
				}
			}
			resMsg.setRspCode(RspCodeConstant.Gate.GATE_0000.getValue());
			resMsg.setRspInfo(RspCodeConstant.Gate.GATE_0000.getDesc());
			resMsg.setBackURL(logTmp.getBackUrl());
			resMsg.setServerURL(logTmp.getServerUrl());
			logger.debug("SimpleGatePayNoticeAction execute(Object) - end");
			return resMsg;
		} catch (Exception e) {
			log.error("网关支付结果通知,内部异常!内部流水:{}",
					new Object[] { logTmp.getIntTxnSeq() });
			logger.error("网关支付结果通知,内部异常,描述:{}", e);
			resMsg.setRspCode(RspCodeConstant.Gate.GATE_9999.getValue());
			resMsg.setRspInfo(RspCodeConstant.Gate.GATE_9999.getDesc());
			resMsg.setServerURL(null != logTmp && null != logTmp.getServerUrl()
					&& !"".equals(logTmp.getServerUrl()) ? txnLog
					.getServerUrl() : null);
			resMsg.setServerURL(null != logTmp && null != logTmp.getServerUrl()
					&& !"".equals(logTmp.getBackUrl()) ? txnLog.getBackUrl()
					: null);
			return resMsg;
		}

	}

}
