package com.huateng.cmupay.thrid.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.action.AbsBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CmuShopPayRequest;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultRsp;
import com.huateng.cmupay.tools.ValidVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.json.JacksonUtils;
import com.huateng.toolbox.utils.DateUtil;

@Controller("gateTUpayOpenAction")
@Scope("prototype")
public class GateTUpayOpenAction extends AbsBaseAction<Object, CoreResultRsp> {

	@Autowired
	private ITpayCsysTxnLogTmpService tpayCsysTxnLogTmpService;

	@Override
	public CoreResultRsp execute(Object param) throws AppBizException {

		logger.debug("GateTUpayOpenAction execute(Object) - start ");

		CmuShopPayRequest reqMsg = null;
		UpayCsysTransCode transCode = null;
		TpayCsysTxnLog logTmp = new TpayCsysTxnLog();
		CoreResultRsp resMsg = new CoreResultRsp();

		// 产生新的序列号标识
		String intTxnSeq = Serial.genSerialNo(CommonConstant.Sequence.IntSeq
				.toString());
		try {

			// 将请求json字符串实例化城CoreResultRsp请求对象
			reqMsg = JacksonUtils.json2Bean(param.toString(),
					CmuShopPayRequest.class);

			String intTxnTime = DateUtil.getDateyyyyMMddHHmmss();
			String intTxnDate = upayCsysBatCutCtlService
					.findCutOffDate(ExcConstant.CUT_OFF_DATE);

			Long seqValue = upayCsysSeqMapInfoService
					.selectSeqValue(ExcConstant.TXN_LOG_SEQ);

			// 校验订单是否重复
			if (!StringUtils.isBlank(reqMsg.getTransCode())) {

				// 根据reqDomain和reqTransId判断交易是否存在
				Map<String, Object> paramss = new HashMap<String, Object>();
				paramss.put("reqDomain", reqMsg.getMerID());
				paramss.put("reqTransId", reqMsg.getOrderID());

				TpayCsysTxnLog upayCsysTxnLogTmp = tpayCsysTxnLogTmpService
						.findObj(paramss);

				if (null != upayCsysTxnLogTmp) {
					log.warn("该交易为重复交易！");
					resMsg.setRspCode(RspCodeConstant.Market.MARKET_013A17
							.getValue());
					resMsg.setRspInfo(RspCodeConstant.Market.MARKET_013A17
							.getDesc() + "reqOprId：" + reqMsg.getOrderID());

					logger.debug("GateTUpayOpenAction execute(Object) - end ");
					return resMsg;
				}

				logger.info("订单号:{},内部交易流水:{},查询{}的交易方式和业务渠道", new Object[] {
						reqMsg.getOrderID(), intTxnSeq, reqMsg.getTransCode() });
				log.info("订单号:{,内部交易流水:{},查询{}的交易方式和业务渠道", new Object[] {
						reqMsg.getOrderID(), intTxnSeq, reqMsg.getTransCode() });

				Map<String, Object> params = new HashMap<String, Object>();
				params.put("transCode", reqMsg.getTransCode());
				transCode = upayCsysTransCodeService.findObj(params);
				logger.info("订单号:{},内部交易流水:{},查询{}的结果",
						new Object[] { reqMsg.getOrderID(), intTxnSeq,
								transCode });
				log.info("订单号:{},内部交易流水:{},查询{}的结果",
						new Object[] { reqMsg.getOrderID(), intTxnSeq,
								transCode });

				if (transCode != null) {
					logTmp.setPayMode(transCode.getPayMode());
					logTmp.setBussType(transCode.getBussType());
					logTmp.setBussChl(transCode.getBussChl());
					logTmp.setReqCnlType(transCode.getBussChl());
				} else {

					logger.error("订单号: {} , 内部交易流水:{},没有 {}查询到相关的业务渠道记录,直接返回",
							new Object[] { reqMsg.getOrderID(), intTxnSeq,
									reqMsg.getTransCode() });
					log.error("订单号: {} ,内部交易流水:{}, 没有 {}查询到相关的业务渠道记录,直接返回",
							new Object[] { reqMsg.getOrderID(), intTxnSeq,
									reqMsg.getTransCode() });

					resMsg.setRspCode(RspCodeConstant.Market.MARKET_013A18
							.getValue());
					resMsg.setRspInfo(RspCodeConstant.Market.MARKET_013A18
							.getDesc());

					logger.debug("GateTUpayOpenAction execute(Object) - end ");
					return resMsg;
				}
			}

			// 校验请求报文体格式
			String validateMsg = this.validateModel(reqMsg,
					ValidVo.TU_PAY_OPEN_REQUEST);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.debug("GateTUpayOpenAction validate success");
			} else {
				logger.warn("订单号：{}, 参数校验错误{}",
						new Object[] { reqMsg.getOrderID(), validateMsg });
				log.warn("订单号：{}, 参数校验错误{}", new Object[] {
						reqMsg.getOrderID(), validateMsg });
				resMsg.setRspCode(RspCodeConstant.Market.MARKET_014A04
						.getValue());
				resMsg.setRspInfo(RspCodeConstant.Market.MARKET_014A04
						.getDesc());
				logger.debug("GateTUpayOpenAction execute(Object) - end ");
				return resMsg;
			}

			logTmp.setSeqId(seqValue);
			logTmp.setIntTxnSeq(intTxnSeq);
			logTmp.setIntTransCode(reqMsg.getTransCode());
			logTmp.setIntTxnDate(intTxnDate);
			logTmp.setIntTxnTime(intTxnTime);

			logTmp.setReqTransId(reqMsg.getOrderID());
			logTmp.setReqTransTm(reqMsg.getOrderTime());

			logTmp.setReqOprId(reqMsg.getOrderID());
			logTmp.setReqOprTm(reqMsg.getOrderTime());

			if (null != reqMsg.getOrderTime()
					&& reqMsg.getOrderTime().length() >= 8) {
				logTmp.setReqTransDt(reqMsg.getOrderTime().substring(0, 8));
				logTmp.setReqOprDt(reqMsg.getOrderTime().substring(0, 8));
			}

			logTmp.setPayAmt(StringFormat.paseLong(reqMsg.getPayment()));

			logTmp.setIdType(reqMsg.getIDType());
			logTmp.setIdValue(reqMsg.getIDValue());

			// 保存银行卡信息
			logTmp.setActivateStatus(CommonConstant.ActivateStatus.Status_0
					.getValue());
			logTmp.setCustomerInfo(reqMsg.getCustomerInfo());
			logTmp.setMobileShopMerId(reqMsg.getMerID());
			logTmp.setBankAccId(reqMsg.getBankAcctID());
			logTmp.setBackUrl(reqMsg.getBackURL());

			// 保存MerUrl保存到流水表中的ServerUrl
			logTmp.setServerUrl(reqMsg.getServerURL());
			logTmp.setOrderId(reqMsg.getOrderID());
			logTmp.setMerVar(reqMsg.getMerVar());
			logTmp.setMerId(reqMsg.getMerID());
			logTmp.setReqDomain(reqMsg.getMerID());
			logTmp.setOrderTm(reqMsg.getOrderTime());

			logTmp.setBackFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			logTmp.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
			logTmp.setClientIp(reqMsg.getClientIp());
			
			logTmp.setSettleDate(DateUtil.getDateyyyyMMdd());
			
			// 第三方数据
			logTmp.setThrMerId(reqMsg.getMerID());
			logTmp.setMerVar(reqMsg.getMerVar());

			tpayCsysTxnLogTmpService.add(logTmp);

			logger.debug("移动商城缴费流水记录成功{}", reqMsg.getOrderID());

			resMsg.setRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
			resMsg.setRspInfo(RspCodeConstant.Market.MARKET_010A00.getDesc());
			logger.info("订单号:{}, 成功 网关移动商城缴费,内部交易流水:{},应答码:010A00",
					new Object[] { reqMsg.getOrderID(), intTxnSeq });
			log.succ("订单号:{} ,成功 网关移动商城缴费 ,内部交易流水:{},应答码:010A00", new Object[] {
					reqMsg.getOrderID(), intTxnSeq });

			logger.debug("GateTUpayOpenAction execute(Object) - end ");
			return resMsg;
		} catch (AppRTException e) {
			log.error("内部异常!内部交易流水号:{}", new Object[] { intTxnSeq });
			logger.error("内部异常,代码:{},内部交易流水号:{}", new Object[] { e.getCode(),
					intTxnSeq });
			logger.error("内部异常:", e);
			resMsg.setRspCode(RspCodeConstant.Market.MARKET_015A03.getValue());
			resMsg.setRspInfo(RspCodeConstant.Market.MARKET_015A03.getDesc());
			logTmp.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			logTmp.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			tpayCsysTxnLogTmpService.modify(logTmp);
			logger.debug("GateTUpayOpenAction execute(Object) - end ");
			return resMsg;
		} catch (Exception e) {
			log.error("内部异常!内部交易流水号:{}", new Object[] { intTxnSeq });
			logger.error("内部异常!内部交易流水号:{}", new Object[] { intTxnSeq });
			logger.error("未知异常:", e);
			resMsg.setRspCode(RspCodeConstant.Market.MARKET_015A03.getValue());
			resMsg.setRspInfo(RspCodeConstant.Market.MARKET_015A03.getDesc());
			logTmp.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			logTmp.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			tpayCsysTxnLogTmpService.modify(logTmp);
			logger.debug("GateTUpayOpenAction execute(Object) - end ");
			return resMsg;
		}
	}
}
