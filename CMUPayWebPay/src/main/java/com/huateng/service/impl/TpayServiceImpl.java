/**
 * 
 */
package com.huateng.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.bean.CoreResultPayRes;
import com.huateng.bean.CoreResultReq;
import com.huateng.core.channel.TpaySwitchChannel;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.TUPayConstant;
import com.huateng.core.exception.AppException;
import com.huateng.core.util.JacksonUtil;
import com.huateng.log.MessageLogger;
import com.huateng.service.TpayService;
import com.huateng.tcp.BaseTcp;
import com.huateng.vo.MsgData;
import com.huateng.vo.TPayShopOpenData;
import com.huateng.vo.TpayData;

/**
 * @author zhaojunnan
 * 
 */
@Service
public class TpayServiceImpl implements TpayService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog = MessageLogger
			.getLogger(TpayServiceImpl.class);

	@Autowired
	private TpaySwitchChannel tpaySwitchChannel;

	@Autowired
	private BaseTcp baseTcp;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.huateng.service.TpayService#shopTpayNotice(boolean,
	 * com.huateng.vo.TpayData)
	 */
	@Override
	public MsgData shopTpayNotice(boolean isPage, TpayData tpayData)
			throws AppException {

		MsgData msgData = new MsgData();
		try {
			// 检查信息
			logger.debug("......银行支付结果信息校验......");
			this.checkPayInfo(tpayData);

			// 验签
			logger.debug("......银行支付结果信息验签......");
			this.tpaySwitchChannel.validateTPayLink(tpayData);

			// 组装发送给核心的消息
			CoreResultReq coreData = new CoreResultReq();
			this.assemulyCorePay(coreData, tpayData);

			String jsonStr = JacksonUtil.beanToStr(coreData);
			String responseText = "";
			boolean isSend = true;

			if (isSend) {

				// 发送信息给核心
				logger.debug("......银行签约支付信息发送回核心[" + jsonStr + "]");
				upayLog.info("银行签约结果信息发送回核心[{}]", jsonStr);
				responseText = baseTcp.sendMessage(jsonStr);

				logger.debug("......接收核心支付结果反馈信息[" + responseText + "]");
				upayLog.info("接收核心签约结果反馈信息[{}]", responseText);
			}

			// 转换核心返回的结果
			CoreResultPayRes coreRsp = JacksonUtil.strToBean(
					CoreResultPayRes.class, responseText);

			// 如果错误码是四位，需要转六位
			if (coreRsp.getRspCode().length() == 4) {
				coreRsp.setRspCode(TUPayConstant.getMMarketErrorCode(coreRsp
						.getRspCode()));
				coreRsp.setRspInfo(TUPayConstant.Market.getDescByValue(coreRsp
						.getRspCode()));
			}

			// 银联应答核心的RspInfo可能为空
			if (coreRsp.getRspInfo() == null) {
				coreRsp.setRspInfo(TUPayConstant.Market.getDescByValue(coreRsp
						.getRspCode()));
			}
			coreRsp.setCurType(tpayData.getCurrencyCode());

			// 生成页面返回给移动商城
			msgData = this.tpaySwitchChannel.transferTPayLink(coreRsp);
		} catch (Exception e) {
			logger.error("订单号{}结果通知处理失败", new Object[] { tpayData.getOrderId(),
					e.getMessage() });
			upayLog.error("订单号{}结果通知处理失败",
					new Object[] { tpayData.getOrderId() });
			throw new AppException(e);
		}

		return msgData;
	}

	@Override
	public MsgData payShopOpenNotice(boolean isPage,
			TPayShopOpenData tPayShopOpenData) throws AppException {

		MsgData msgData = new MsgData();
		try {
			// 检查信息
			logger.debug("......银行开通认证支付页面结果信息校验......");
			this.checkPayShopOpenInfo(tPayShopOpenData);

			// 验签
			logger.debug("......银行开通认证支付页面结果信息验签......");
			this.tpaySwitchChannel.validateTPayShopOpenLink(tPayShopOpenData);

			// 组装发送给核心的消息
			CoreResultReq coreData = new CoreResultReq();
			this.assemulyCorePayShopOpen(coreData, tPayShopOpenData);

			String jsonStr = JacksonUtil.beanToStr(coreData);
			String responseText = "";
			boolean isSend = true;

			if (isSend) {

				// 发送信息给核心
				logger.debug("......银行签约开通认证支付页面信息发送回核心[" + jsonStr + "]");
				upayLog.info("银行开通认证支付页面结果信息发送回核心[{}]", jsonStr);
				responseText = baseTcp.sendMessage(jsonStr);

				logger.debug("......接收核心开通认证支付页面结果反馈信息[" + responseText + "]");
				upayLog.info("接收核心开通认证支付页面结果反馈信息[{}]", responseText);
			}

			// 转换核心返回的结果
			CoreResultPayRes coreRsp = JacksonUtil.strToBean(
					CoreResultPayRes.class, responseText);

			// 如果错误码是四位，需要转六位
			if (coreRsp.getRspCode().length() == 4) {
				coreRsp.setRspCode(TUPayConstant.getMMarketErrorCode(coreRsp
						.getRspCode()));
				coreRsp.setRspInfo(TUPayConstant.Market.getDescByValue(coreRsp
						.getRspCode()));
			}

			// 银联应答核心的RspInfo可能为空
			if (coreRsp.getRspInfo() == null) {
				coreRsp.setRspInfo(TUPayConstant.Market.getDescByValue(coreRsp
						.getRspCode()));
			}

			// 生成页面返回给移动商城
			msgData = this.tpaySwitchChannel.transferTPayShopOpenLink(coreRsp,
					tPayShopOpenData);
		} catch (Exception e) {
			logger.error(
					"订单号{}结果通知处理失败",
					new Object[] { tPayShopOpenData.getAccNo(), e.getMessage() });
			upayLog.error("订单号{}结果通知处理失败",
					new Object[] { tPayShopOpenData.getAccNo() });
			throw new AppException(e);
		}

		return msgData;
	}

	/**
	 * 设置发往核心的请求数据
	 * 
	 * @param coreData
	 * @param tpayData
	 */
	private void assemulyCorePay(CoreResultReq coreData, TpayData tpayData) {
		coreData.setTransCode(CoreConstant.TransCode.TSHOP_PAY_NOTICE.getCode());
		coreData.setOrderID(tpayData.getOrderId());
		coreData.getProperties().put("respCode", tpayData.getRespCode());
	}

	/**
	 * 设置发往核心的请求数据
	 * 
	 * @param coreData
	 * @param tpayData
	 */
	private void assemulyCorePayShopOpen(CoreResultReq coreData,
			TPayShopOpenData tPayShopOpenData) {
		coreData.setTransCode(CoreConstant.TransCode.PAY_SHOP_OPEN_NOTICE
				.getCode());

		// OrderId在请求放保留域中
		coreData.setOrderID(tPayShopOpenData.getReqReserved());

		// 将银联的返回参数都放入扩展属性中
		coreData.getProperties().put("version", tPayShopOpenData.getVersion());
		coreData.getProperties().put("certId", tPayShopOpenData.getCertId());
		coreData.getProperties().put("signature",
				tPayShopOpenData.getSignature());
		coreData.getProperties()
				.put("encoding", tPayShopOpenData.getEncoding());
		coreData.getProperties().put("txnType", tPayShopOpenData.getTxnType());
		coreData.getProperties().put("txnSubType",
				tPayShopOpenData.getTxnSubType());
		coreData.getProperties().put("bizType", tPayShopOpenData.getBizType());
		coreData.getProperties().put("accessType",
				tPayShopOpenData.getAccessType());
		coreData.getProperties().put("merId", tPayShopOpenData.getMerId());
		coreData.getProperties().put("accNo", tPayShopOpenData.getAccNo());
		coreData.getProperties().put("reqReserved",
				tPayShopOpenData.getReqReserved());
		coreData.getProperties()
				.put("reserved", tPayShopOpenData.getReserved());
		coreData.getProperties()
				.put("respCode", tPayShopOpenData.getRespCode());
		coreData.getProperties().put("respMsg", tPayShopOpenData.getRespMsg());
		coreData.getProperties().put("activateStatus",
				tPayShopOpenData.getActivateStatus());
		coreData.getProperties().put("payCardType",
				tPayShopOpenData.getPayCardType());
		coreData.getProperties().put("customerInfo",
				tPayShopOpenData.getCustomerInfo());
		coreData.getProperties().put("temporaryPayInfo",
				tPayShopOpenData.getTemporaryPayInfo());

	}

	private void checkPayInfo(TpayData tpayData) {
		tpayData.setStatus(CoreConstant.BankResultCode.UPAY_B_010A00.getCode());
	}

	private void checkPayShopOpenInfo(TPayShopOpenData tPayShopOpenData) {
		tPayShopOpenData.setStatus(CoreConstant.BankResultCode.UPAY_B_010A00
				.getCode());
	}

}
