/**
 * 
 */
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
import com.huateng.cmupay.controller.cache.BankThrCodeCache;
import com.huateng.cmupay.controller.cache.MerCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.TUpayBankThrCode;
import com.huateng.cmupay.models.TpayCsysMerCodeInfo;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CmuShopPayRequest;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CoreResultRsp;
import com.huateng.cmupay.tools.ValidVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.json.JacksonUtils;
import com.huateng.toolbox.utils.DateUtil;

/**
 * 处理移动商城发起的缴费、支付请求，并保存数据到数据库中
 * 
 * @author zhaojunnan
 * @param <T>
 * 
 */
@Controller("gateTUPayAction")
@Scope("prototype")
public class GateTUPayAction extends AbsBaseAction<Object, CoreResultRsp> {

	@Autowired
	private ITpayCsysTxnLogTmpService tpayCsysTxnLogTmpService;

	@Override
	public CoreResultRsp execute(Object param) throws AppBizException {
		logger.debug("GateTUPayAction execute(Object) - start ");
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
			resMsg.setBackURL(reqMsg.getBackURL());
			resMsg.setServerURL(reqMsg.getServerURL());
			String intTxnTime = DateUtil.getDateyyyyMMddHHmmss();
			String intTxnDate = upayCsysBatCutCtlService
					.findCutOffDate(ExcConstant.CUT_OFF_DATE);
			Long seqValue = upayCsysSeqMapInfoService
					.selectSeqValue(ExcConstant.TXN_LOG_SEQ);

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
					logger.debug("GateTUPayAction execute(Object) - end ");
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
					logger.debug("GateTUPayAction execute(Object) - end ");
					return resMsg;
				}
			}

			// 校验请求报文体格式
			String validateMsg = null;
			if (CommonConstant.payStatus.UPAY_STATUS.getValue().equals(
					reqMsg.getOrderType())) {
				validateMsg = this.validateModel(reqMsg);
			} else {
				validateMsg = this.validateModel(reqMsg,
						ValidVo.CMU_SHOP_PAY_REQUEST_PAY);
			}
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.debug("GateTUPayAction validate success");
			} else {
				logger.warn("订单号：{}, 参数校验错误{}",
						new Object[] { reqMsg.getOrderID(), validateMsg });
				log.warn("订单号：{}, 参数校验错误{}", new Object[] {
						reqMsg.getOrderID(), validateMsg });
				resMsg.setRspCode(RspCodeConstant.Market.MARKET_014A04
						.getValue());
				resMsg.setRspInfo(RspCodeConstant.Market.MARKET_014A04
						.getDesc());
				logger.debug("GateTUPayAction execute(Object) - end ");
				return resMsg;
			}

			ProvincePhoneNum provincePhoneNum = null;
			String idProvince = null;

			// 校验号码归属地,如果支付交易，不校验归属地
			if (CommonConstant.payStatus.UPAY_STATUS.getValue().equals(
					reqMsg.getOrderType())) {
				provincePhoneNum = findProvinceByMobileNumber(reqMsg
						.getIDValue());
				idProvince = provincePhoneNum == null ? null : provincePhoneNum
						.getProvinceCode();
				if (StringUtils.isBlank(idProvince)) {
					logger.warn("订单号：{}, {}该手机号码错误，没有相关的归属地信息", new Object[] {
							reqMsg.getOrderID(), reqMsg.getIDValue() });
					log.warn("订单号：{}, {}该手机号码错误，没有相关的归属地信息", new Object[] {
							reqMsg.getOrderID(), reqMsg.getIDValue() });
					resMsg.setRspCode(RspCodeConstant.Market.MARKET_012A17
							.getValue());
					resMsg.setRspInfo(RspCodeConstant.Market.MARKET_012A17
							.getDesc());
					logger.debug("GateTUPayAction execute(Object) - end ");
					return resMsg;
				}

				// 号码归属地校验失败，返回异常信息
				if (!reqMsg.getHomeProv().equals(idProvince)) {
					logger.warn("订单号：{}, {}省代码和手机号码归属省不一致", new Object[] {
							reqMsg.getOrderID(), reqMsg.getIDValue() });
					log.warn(
							"订单号：{}, {}省代码和手机号码归属省不一致",
							new Object[] { reqMsg.getOrderID(),
									reqMsg.getIDValue() });
					resMsg.setRspCode(RspCodeConstant.Market.MARKET_014A10
							.getValue());
					resMsg.setRspInfo(RspCodeConstant.Market.MARKET_014A10
							.getDesc());
					logger.debug("GateTUPayAction execute(Object) - end ");
					return resMsg;
				}
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

			logTmp.setNeedPayAmt(StringFormat.paseLong(reqMsg.getChargeMoney()));
			logTmp.setPayAmt(StringFormat.paseLong(reqMsg.getPayment()));

			logTmp.setIdType(reqMsg.getIDType());
			logTmp.setIdValue(reqMsg.getIDValue());
			if (CommonConstant.payStatus.UPAY_STATUS.getValue().equals(
					reqMsg.getOrderType())) {
				logTmp.setIdProvince(idProvince);
				logTmp.setPayStatus(CommonConstant.payStatus.UPAY_STATUS
						.getValue());
			} else {
				logTmp.setPayStatus(CommonConstant.payStatus.TUPAY_STATUS
						.getValue());
			}
			logTmp.setBankId(CommonConstant.BankOrgCode.TPAY.getValue());
			logTmp.setBackUrl(reqMsg.getBackURL());

			// 保存MerUrl保存到流水表中的ServerUrl
			logTmp.setServerUrl(reqMsg.getServerURL());
			logTmp.setOrderId(reqMsg.getOrderID());
			logTmp.setMerVar(reqMsg.getMerVar());
			logTmp.setReqDomain(reqMsg.getMerID());
			
			logTmp.setProductShelfNo(reqMsg.getProdShelfNO());
			logTmp.setPayTimeoutDt(reqMsg.getPayTimeoutTime());

			// 如果是支付交易填写移动商城发送的MerId，如果是缴费类交易填写移动商城发来的homeProv（三位需要转成四位）
			if (CommonConstant.payStatus.UPAY_STATUS.getValue().equals(
					reqMsg.getOrderType())) {
				logTmp.setMerId(SysMapCache.getProvCd(
						provincePhoneNum.getProvinceCode()).getSysCd());

			} else {
				logTmp.setMerId(reqMsg.getMerID());
			}
			logTmp.setOrderTm(reqMsg.getOrderTime());
			logTmp.setOrderCnt(StringFormat.paseInt(reqMsg.getProdCnt()));
			logTmp.setProductNo(reqMsg.getProdId());
			logTmp.setCommision(StringFormat.paseInt(reqMsg.getCommision()));
			logTmp.setRebateFee(StringFormat.paseInt(reqMsg.getRebateFee()));
			logTmp.setProdDiscount(StringFormat.paseInt(reqMsg
					.getProdDiscount()));
			logTmp.setCreditCardFee(StringFormat.paseInt(reqMsg
					.getCreditCardFee()));
			logTmp.setServiceFee(StringFormat.paseInt(reqMsg.getServiceFee()));
			logTmp.setActivityNo(reqMsg.getActivityNo());

			logTmp.setBackFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			logTmp.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			logTmp.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
			logTmp.setClientIp(reqMsg.getClientIp());
			
			logTmp.setSettleDate(DateUtil.getDateyyyyMMdd());

			// 设置返回值的扩展属性
			setCoreResultRspProperties(reqMsg, resMsg, provincePhoneNum, logTmp);

			tpayCsysTxnLogTmpService.add(logTmp);
			logger.debug("移动商城缴费流水记录成功{}", reqMsg.getOrderID());

			resMsg.setRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
			resMsg.setRspInfo(RspCodeConstant.Market.MARKET_010A00.getDesc());
			logger.info("订单号:{}, 成功 网关移动商城缴费,内部交易流水:{},应答码:010A00",
					new Object[] { reqMsg.getOrderID(), intTxnSeq });
			log.succ("订单号:{} ,成功 网关移动商城缴费 ,内部交易流水:{},应答码:010A00", new Object[] {
					reqMsg.getOrderID(), intTxnSeq });

			logger.debug("GateShopPayAction execute(Object) - end ");
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
			logger.debug("GateTUPayAction execute(Object) - end ");
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
			logger.debug("GateTUPayAction execute(Object) - end ");
			return resMsg;
		}
	}

	/**
	 * 设置返回值的扩展属性，网关需要将扩展属性里的参数传递给银联
	 * 
	 * @param reqMsg
	 * @param resMsg
	 */
	private void setCoreResultRspProperties(CmuShopPayRequest reqMsg,
			CoreResultRsp resMsg, ProvincePhoneNum provincePhoneNum,
			TpayCsysTxnLog logTmp) {

		// 根据根据机构代码org_code获取mer_id 如果是缴费类交易，需要通过homeProv作为机构代码，查询
		TpayCsysMerCodeInfo tpayCsysMerCodeInfo = null;
		if (CommonConstant.payStatus.UPAY_STATUS.getValue().equals(
				reqMsg.getOrderType())) {
			String orgCode = SysMapCache.getProvCd(
					provincePhoneNum.getProvinceCode()).getSysCd();
			tpayCsysMerCodeInfo = MerCodeCache.getMerInfoByOrgCode(orgCode,
					CommonConstant.BankOrgCode.TPAY.getValue(),
					logTmp.getPayStatus());
		} else {
			tpayCsysMerCodeInfo = MerCodeCache.getMerInfoByOrgCode(
					reqMsg.getMerID(),
					CommonConstant.BankOrgCode.TPAY.getValue(),
					logTmp.getPayStatus(), reqMsg.getShopMerId());
		}

		// TODO 此处需判断异常，查询不到时抛出异常
		if (null != tpayCsysMerCodeInfo) {
			resMsg.getProperties().put("merId", tpayCsysMerCodeInfo.getMerId());
			logTmp.setThrMerId(tpayCsysMerCodeInfo.getMerId());
		}
		if (CommonConstant.BankOrgCode.TPAY.getValue().equals(
				reqMsg.getBankID())) {
			resMsg.getProperties().put("issInsCode", "");
			return;
		}
		// 获取第三方银行代码
		TUpayBankThrCode tUpayBankThrCode = new TUpayBankThrCode();
		tUpayBankThrCode
				.setThrOrgId(CommonConstant.BankOrgCode.TPAY.getValue());
		tUpayBankThrCode.setBankId(reqMsg.getBankID());
		tUpayBankThrCode = BankThrCodeCache.getBankThrCode(tUpayBankThrCode);
		if (null != tUpayBankThrCode) {
			resMsg.getProperties().put("issInsCode",
					tUpayBankThrCode.getThrBankId());
		}
	}

}
