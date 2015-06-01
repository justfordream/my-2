package com.huateng.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.bean.CoreResultRsp;
import com.huateng.bean.ShopData;
import com.huateng.channel.CmuSwitchChannel;
import com.huateng.core.common.CommonFunction;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.TUPayConstant;
import com.huateng.core.exception.AppException;
import com.huateng.core.util.JacksonUtil;
import com.huateng.log.LogHandle;
import com.huateng.service.RcvPayShopOpenService;
import com.huateng.tcp.BaseTcp;
import com.huateng.utils.MessageCheck;
import com.huateng.vo.MsgData;

@Service
public class RcvPayShopOpenServiceImpl implements RcvPayShopOpenService {

	private Logger logger = LoggerFactory
			.getLogger("RcvPayShopOpenServiceImpl");

	@Autowired
	private BaseTcp baseTcp;

	@Autowired
	private CmuSwitchChannel cmuSwitchChannel;

	@Autowired
	private LogHandle logHandle;

	@Override
	public MsgData sendPayShopOpenRequest(ShopData shopData)
			throws AppException {
		/*
		 * 来源信息验证
		 */
		logger.debug("...移动商城{}.开通认证支付请求参数校验", shopData.getMerID());
		MessageCheck.checkPayShopOpen(shopData);
		/*
		 * 验签
		 */
		logger.debug("...移动商城{}.开通认证支付请求参数校验", shopData.getMerID());
		cmuSwitchChannel.validatePayShopOpenLink(shopData);

		// 交易码为开通认证支付
		shopData.setTransCode(CoreConstant.TransCode.PAY_SHOP_OPEN.getCode());
		logHandle.info(false, shopData.getStatus(),
				CoreConstant.TransCode.PAY_SHOP_OPEN.getCode(),
				CoreConstant.TransCode.PAY_SHOP_OPEN.getDesc(),
				shopData.getMerID(),
				CommonFunction.getSysCodeByProvCode(shopData.getMerID()),
				shopData.getStatusDesc());

		CoreResultRsp coreRsp = null;
		if (shopData.getStatus().equals(
				CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {
			String jsonStr = JacksonUtil.beanToStr(shopData);
			logger.info("省份{}支付请求发送到核心[{}]", shopData.getMerID(), jsonStr);
			String responseText = baseTcp.sendMessage(jsonStr);

			logger.info("...省份{}...接收到核心返回支付请求响应信息:[{}]", shopData.getMerID(),
					responseText);
			coreRsp = JacksonUtil.strToBean(CoreResultRsp.class, responseText);

			// 如果错误码是四位，需要转六位
			if (coreRsp.getRspCode().length() == 4) {
				coreRsp.setRspCode(TUPayConstant.getMMarketErrorCode(coreRsp
						.getRspCode()));
				coreRsp.setRspInfo(TUPayConstant.Market.getDescByValue(coreRsp
						.getRspCode()));
			}

			// 将返回的扩展属性放入ShopData对象中
			shopData.setProperties(coreRsp.getProperties());
			shopData.setStatus(coreRsp.getRspCode());
		} else {
			coreRsp = new CoreResultRsp();
			coreRsp.setRspCode(shopData.getStatus());
			coreRsp.setRspInfo(shopData.getStatusDesc());
		}
		/*
		 * 转发信息至指定银行
		 */
		MsgData msgData = cmuSwitchChannel.transferePayShopOpenLink(shopData,
				coreRsp);
		return msgData;
	}

}
