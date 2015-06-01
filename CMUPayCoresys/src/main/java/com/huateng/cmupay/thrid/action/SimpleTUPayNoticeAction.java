/**
 * 
 */
package com.huateng.cmupay.thrid.action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.action.AbsBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.TUPayConstant;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultPayReq;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultPayRes;
import com.huateng.toolbox.json.JacksonUtils;

/**
 * 银联支付结果通知，前台页面跳转回统一支付
 * 
 * @author zhaojunnan
 * 
 */
@Controller("simpleTUPayNoticeAction")
@Scope("prototype")
public class SimpleTUPayNoticeAction extends
		AbsBaseAction<Object, CoreResultPayRes> {

	@Autowired
	private ITpayCsysTxnLogService tpayCsysTxnLogService;

	@Autowired
	private ITpayCsysTxnLogTmpService tpayCsysTxnLogTmpService;

	@Override
	public CoreResultPayRes execute(Object param) throws AppBizException {
		logger.debug("SimpleTUPayNoticeAction execute(Object) - start"
				+ param.toString());

		CoreResultPayReq reqMsg = null;
		TpayCsysTxnLog txnLog = new TpayCsysTxnLog();
		TpayCsysTxnLog logTmp = null;
		CoreResultPayRes resMsg = new CoreResultPayRes();
		try {
			reqMsg = JacksonUtils.json2Bean(param.toString(),
					CoreResultPayReq.class);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderId", reqMsg.getOrderID());
			// 根据orderId在第三方交易临时表中查找
			logTmp = tpayCsysTxnLogTmpService.findObj(params);
			if (null == logTmp) {
				logger.warn("银联支付结果通知,订单号: {},原支付交易不存在", reqMsg.getOrderID());
				log.warn("银联支付结果通知,订单号: {},原支付交易不存在", reqMsg.getOrderID());
				resMsg.setRspCode(RspCodeConstant.Market.MARKET_014A05
						.getValue());
				resMsg.setRspInfo(RspCodeConstant.Market.MARKET_014A05
						.getDesc() + "fail原支付交易不存在");
				logger.debug("SimpleTUPayNoticeAction execute(Object) - end");
				return resMsg;
			}
			resMsg.setMerID(logTmp.getMerId());
			resMsg.setCurType(CommonConstant.CurType.rmbType.getValue());
			resMsg.setMerVAR(logTmp.getMerVar());
			resMsg.setOrderID(logTmp.getOrderId());
			resMsg.setOrderTime(logTmp.getOrderTm());
			resMsg.setPayed(logTmp.getPayAmt().toString());

			// 根据orderId在第三方交易表中查找，根据订单状态进行判断
			txnLog = tpayCsysTxnLogService.findObj(params);
			if (null == txnLog) {
				logger.warn("银联支付结果通知,订单号: {},未收到银联后台支付结果通知",
						reqMsg.getOrderID());
				log.warn("银联支付结果通知,订单号: {},未收到银联后台支付结果通知", reqMsg.getOrderID());
			} else {
				logger.info("银联支付结果通知,订单号: {} ,银联支付结果通知状态： {}",
						reqMsg.getOrderID(), txnLog.getStatus());
				if (CommonConstant.TxnStatus.TxnSuccess.getValue().equals(
						txnLog.getStatus())) {
					logger.info("银联支付结果通知,订单号: {} ,银联支付后台结果通知为成功 ",
							reqMsg.getOrderID());
					log.succ("银联支付结果通知,订单号: {} ,银联支付后台结果通知为成功 ",
							reqMsg.getOrderID());
				} else {
					logger.error("银联支付结果通知,订单号: {} ,银联支付后台结果通知为失败,或者未通知 ",
							reqMsg.getOrderID());
					log.error("银联支付结果通知,订单号: {} ,银联支付后台结果通知为失败 ,或者未通知",
							reqMsg.getOrderID());
				}
			}
			resMsg.setRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
			resMsg.setRspInfo(RspCodeConstant.Market.MARKET_010A00.getDesc());
			resMsg.setBackURL(logTmp.getBackUrl());
			resMsg.setServerURL(logTmp.getServerUrl());

			// 根据银行的返回码进行判断
			String tpayRespCode = reqMsg.getProperties().get("respCode");
			if (null != tpayRespCode && !"00".equals(tpayRespCode)) {

				String respCode = TUPayConstant
						.getMMarketErrorCode(tpayRespCode);

				if (null == respCode) {
					respCode = RspCodeConstant.Market.MARKET_015A06.getValue();
				}
				resMsg.setRspCode(respCode);
				resMsg.setRspInfo(null);
			}
			logger.debug("SimpleTUPayNoticeAction execute(Object) - end");
			return resMsg;
		} catch (Exception e) {
			log.error("银联支付结果通知,内部异常!内部流水:{}",
					new Object[] { logTmp.getIntTxnSeq() });
			logger.error("银联支付结果通知,内部异常,描述:{}", e);
			resMsg.setRspCode(RspCodeConstant.Market.MARKET_015A03.getValue());
			resMsg.setRspInfo(RspCodeConstant.Market.MARKET_015A03.getDesc());
			resMsg.setServerURL(null != logTmp && null != logTmp.getServerUrl()
					&& !"".equals(logTmp.getServerUrl()) ? txnLog
					.getServerUrl() : null);
			resMsg.setServerURL(null != logTmp && null != logTmp.getServerUrl()
					&& !"".equals(logTmp.getBackUrl()) ? txnLog.getBackUrl()
					: null);
			logger.debug("SimpleTUPayNoticeAction execute(Object) - end");
			return resMsg;
		}

	}

}
