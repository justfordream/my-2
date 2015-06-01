package com.huateng.cmupay.thrid.action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.action.AbsBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.TUPayConstant;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultPayReq;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultPayRes;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.json.JacksonUtils;
import com.huateng.toolbox.utils.DateUtil;

@Controller("tuPayOpenNoticeAction")
@Scope("prototype")
public class TUPayOpenNoticeAction extends
		AbsBaseAction<Object, CoreResultPayRes> {

	@Autowired
	private ITpayCsysTxnLogService tpayCsysTxnLogService;
	@Autowired
	private ITpayCsysTxnLogTmpService tpayCsysTxnLogTmpService;

	@Override
	public CoreResultPayRes execute(Object param) throws AppBizException {
		logger.debug("SimpleTUPayNoticeAction execute(Object) - start"
				+ param.toString());

		// 交易流水临时表对象
		TpayCsysTxnLog transTmpLog = null;

		// 新建一个交易流水表对象
		TpayCsysTxnLog txnLog = new TpayCsysTxnLog();

		// 返回数据
		CoreResultPayRes resMsg = new CoreResultPayRes();

		// 请求数据
		CoreResultPayReq reqMsg = null;

		// 内部交易码
		UpayCsysTransCode transCode = null;

		// 生成流水号
		Long seqId = upayCsysSeqMapInfoService
				.selectSeqValue(ExcConstant.TXN_LOG_SEQ);

		// 生成内部交易时间
		String intTxnDate = upayCsysBatCutCtlService
				.findCutOffDate(ExcConstant.CUT_OFF_DATE);

		// 产生新的序列号标识
		String intTxnSeq = Serial.genSerialNo(CommonConstant.Sequence.IntSeq
				.toString());

		String intTxnTime = DateUtil.getDateyyyyMMddHHmmssSSS();

		try {

			reqMsg = JacksonUtils.json2Bean(param.toString(),
					CoreResultPayReq.class);
			String orderId = reqMsg.getOrderID();

			// 根据orderId和status查找支付流水
			logger.debug("查找流水 oderId: {},status: {}", orderId,
					CommonConstant.TxnStatus.TxnSuccess.getValue());
			Map<String, Object> paramsData = new HashMap<String, Object>();
			paramsData.put("orderId", orderId);
			paramsData.put("activateStatus",
					CommonConstant.ActivateStatus.Status_0.getValue());

			// 查询交易流水临时表
			transTmpLog = tpayCsysTxnLogTmpService.findObj(paramsData);
			if (null == transTmpLog) {
				logger.warn("银联开通认证支付结果通知,订单号: {},原支付交易不存在",
						reqMsg.getOrderID());
				log.warn("银联开通认证支付结果通知,订单号: {},原支付交易不存在", reqMsg.getOrderID());
				resMsg.setRspCode(RspCodeConstant.Market.MARKET_014A05
						.getValue());
				resMsg.setRspInfo(RspCodeConstant.Market.MARKET_014A05
						.getDesc() + "fail原支付交易不存在");
				logger.debug("TUPayOpenNoticeAction execute(Object) - end");
				return resMsg;
			}

			resMsg.setMerID(transTmpLog.getMerId());
			resMsg.setCurType(CommonConstant.CurType.rmbType.getValue());
			resMsg.setMerVAR(transTmpLog.getMerVar());
			resMsg.setOrderID(transTmpLog.getOrderId());
			resMsg.setOrderTime(transTmpLog.getOrderTm());
			resMsg.setPayed(transTmpLog.getPayAmt().toString());

			// 校验内部交易码
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("transCode", reqMsg.getTransCode());
			transCode = upayCsysTransCodeService.findObj(params);

			logger.info("订单号:{},内部交易流水:{},查询{}的结果",
					new Object[] { reqMsg.getOrderID(), intTxnSeq, transCode });
			log.info("订单号:{},内部交易流水:{},查询{}的结果",
					new Object[] { reqMsg.getOrderID(), intTxnSeq, transCode });

			if (transCode != null) {
				txnLog.setPayMode(transCode.getPayMode());
				txnLog.setBussType(transCode.getBussType());
				txnLog.setBussChl(transCode.getBussChl());
				txnLog.setReqCnlType(transCode.getBussChl());
			} else {

				logger.error(
						"订单号: {} , 内部交易流水:{},没有 {}查询到相关的业务渠道记录,直接返回",
						new Object[] { reqMsg.getOrderID(), intTxnSeq,
								reqMsg.getTransCode() });
				log.error(
						"订单号: {} ,内部交易流水:{}, 没有 {}查询到相关的业务渠道记录,直接返回",
						new Object[] { reqMsg.getOrderID(), intTxnSeq,
								reqMsg.getTransCode() });

				resMsg.setRspCode(RspCodeConstant.Market.MARKET_013A18
						.getValue());
				resMsg.setRspInfo(RspCodeConstant.Market.MARKET_013A18
						.getDesc());
				resMsg.setBackURL(transTmpLog.getBackUrl());

				logger.debug("TUPayOpenNoticeAction execute(Object) - end ");
				return resMsg;
			}

			// 查询交易流水表
			// paramsData.put("activateStatus",
			// reqMsg.getProperties().get("activateStatus"));
			TpayCsysTxnLog trans = tpayCsysTxnLogService.findObj(paramsData);
			if (null != trans) {

				logger.warn("银联开通认证支付结果通知,订单号: {},为重复交易", reqMsg.getOrderID());
				log.warn("银联开通认证支付结果通知,订单号: {},为重复交易", reqMsg.getOrderID());
				resMsg.setRspCode(RspCodeConstant.Market.MARKET_013A17
						.getValue());
				resMsg.setRspInfo(RspCodeConstant.Market.MARKET_013A17
						.getDesc() + "reqOprId：" + reqMsg.getOrderID());
				resMsg.setBackURL(transTmpLog.getBackUrl());

				logger.debug("TUPayOpenNoticeAction execute(Object) - end ");
				return resMsg;

			}

			// 将数据冲交易临时表中迁移至交易流水表
			BeanUtils.copyProperties(transTmpLog, txnLog);
			txnLog.setSeqId(seqId);
			txnLog.setIntTxnSeq(intTxnSeq);
			txnLog.setIntTxnDate(intTxnDate);
			txnLog.setIntTxnTime(intTxnTime);
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setThrVersion(reqMsg.getProperties().get("version"));
			txnLog.setActivateStatus(reqMsg.getProperties().get(
					"activateStatus"));
			txnLog.setCustomerInfo(reqMsg.getProperties().get("customerInfo"));
			txnLog.setBankAccId(reqMsg.getProperties().get("accNo"));
			txnLog.setPayCardType(reqMsg.getProperties().get("payCardType"));
			txnLog.setThrMerId(reqMsg.getProperties().get("merId"));
			txnLog.setSettleDate(DateUtil.getDateyyyyMMdd());

			txnLog.setSettleDate(DateUtil.getDateyyyyMMdd());

			resMsg.setRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
			resMsg.setRspInfo(RspCodeConstant.Market.MARKET_010A00.getDesc());
			resMsg.setBackURL(txnLog.getBackUrl());
			resMsg.setServerURL(txnLog.getServerUrl());

			// 保存交易流水表数据
			tpayCsysTxnLogService.add(txnLog);

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
			logger.debug("TUPayOpenNoticeAction execute(Object) - end");

			return resMsg;
		} catch (Exception e) {
			log.error("银联开通认证支付结果通知,内部异常!内部流水:{}",
					new Object[] { transTmpLog.getIntTxnSeq() });
			logger.error("银联开通认证支付结果通知,内部异常,描述:{}", e);
			resMsg.setRspCode(RspCodeConstant.Market.MARKET_015A03.getValue());
			resMsg.setRspInfo(RspCodeConstant.Market.MARKET_015A03.getDesc());
			resMsg.setServerURL(null != transTmpLog
					&& null != transTmpLog.getServerUrl()
					&& !"".equals(transTmpLog.getServerUrl()) ? txnLog
					.getServerUrl() : null);
			resMsg.setServerURL(null != transTmpLog
					&& null != transTmpLog.getServerUrl()
					&& !"".equals(transTmpLog.getBackUrl()) ? txnLog
					.getBackUrl() : null);
			logger.debug("TUPayOpenNoticeAction execute(Object) - end");
			return resMsg;
		}

	}
}
