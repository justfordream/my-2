package com.huateng.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.bean.CoreResultPayRes;
import com.huateng.bean.CoreResultReq;
import com.huateng.bean.TenData;
import com.huateng.core.channel.TenPaySwitchChannel;
import com.huateng.core.channel.TpaySwitchChannel;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.TUPayConstant;
import com.huateng.core.exception.AppException;
import com.huateng.core.util.JacksonUtil;
import com.huateng.log.MessageLogger;
import com.huateng.service.TenPayService;
import com.huateng.tcp.BaseTcp;
import com.huateng.vo.MsgData;
import com.huateng.vo.TpayData;

@Service
public class TenPayServiceImpl implements TenPayService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog = MessageLogger
			.getLogger(TenPayServiceImpl.class);

	@Autowired
	private TenPaySwitchChannel tenPaySwitchChannel;

	@Autowired
	private BaseTcp baseTcp;

	@Override
	public MsgData shopTenPayNotice(boolean isPage, TenData tenData)
			throws AppException {

		MsgData msgData = new MsgData();
		try {
			// 检查信息
			logger.debug("......银行支付结果信息校验......");
			this.checkTenInfo(tenData);

			// 验签
			logger.debug("......银行支付结果信息验签......");
			this.tenPaySwitchChannel.validateTenPayLink(tenData);

			// 组装发送给核心的消息
			CoreResultReq coreData = new CoreResultReq();
			this.assemulyCorePay(coreData, tenData);

			String jsonStr = JacksonUtil.beanToStr(coreData);
			String responseText = "";
			boolean isSend = true;

			if (isSend) {

				// 发送信息给核心
				logger.debug("......财付通支付信息发送回核心[" + jsonStr + "]");
				upayLog.info("财付通支付结果信息发送回核心[{}]", jsonStr);
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

			// 生成页面返回给移动商城
			msgData = this.tenPaySwitchChannel.transferTenPayLink(coreRsp);
		} catch (Exception e) {
			logger.error("订单号{}结果通知处理失败",
					new Object[] { tenData.getOut_trade_no(), e.getMessage() });
			upayLog.error("订单号{}结果通知处理失败",
					new Object[] { tenData.getOut_trade_no() });
			throw new AppException(e);
		}

		return msgData;
	}

	private void checkTenInfo(TenData tenData) {
		tenData.setStatus(CoreConstant.BankResultCode.UPAY_B_010A00.getCode());
	}

	/**
	 * 设置发往核心的请求数据
	 * 
	 * @param coreData
	 * @param tpayData
	 */
	private void assemulyCorePay(CoreResultReq coreData, TenData tenData) {
		coreData.setTransCode(CoreConstant.TransCode.TEN_SHOP_PAY_NOTICE
				.getCode());
		coreData.setOrderID(tenData.getOut_trade_no());
	}

}
