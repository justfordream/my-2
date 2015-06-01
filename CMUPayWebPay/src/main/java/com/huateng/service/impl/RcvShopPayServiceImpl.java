package com.huateng.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.huateng.service.RcvShopPayService;
import com.huateng.tcp.BaseTcp;
import com.huateng.utils.MessageCheck;
import com.huateng.vo.MsgData;

/**
 * 支付信息业务处理类
 * 
 * @author Gary
 * 
 */
@Service
public class RcvShopPayServiceImpl implements RcvShopPayService {

	private Logger logger = LoggerFactory.getLogger("RcvShopPayServiceImpl");

	@Value("${SHOP_BANK_LIST}")
	private String shopBankList;

	@Autowired
	private BaseTcp baseTcp;

	@Autowired
	private CmuSwitchChannel cmuSwitchChannel;

	@Autowired
	private LogHandle logHandle;

	public void setBaseTcp(BaseTcp baseTcp) {
		this.baseTcp = baseTcp;
	}

	public MsgData sendPayRequest(ShopData cmuData) throws AppException {
		/*
		 * 来源信息验证
		 */
		logger.debug("...省份{}.支付请求参数校验", cmuData.getMerID());
		MessageCheck.checkShopPay(cmuData);
		/*
		 * 验签
		 */
		logger.debug("..省份{}支付请求验签.", cmuData.getMerID());
		cmuSwitchChannel.validateShopPayLink(cmuData);

		if (shopBankList.indexOf(cmuData.getBankID()) >= 0
				&& !cmuData.getBankID().equals(
						CoreConstant.BankCode.TEN.getCode())
				&& !cmuData.getBankID().equals(
						CoreConstant.BankCode.ALI.getCode())) {
			cmuData.setTransCode(CoreConstant.TransCode.SHOP_PAY.getCode());
			logHandle.info(false, cmuData.getStatus(),
					CoreConstant.TransCode.SHOP_PAY.getCode(),
					CoreConstant.TransCode.SHOP_PAY.getDesc(),
					cmuData.getMerID(),
					CommonFunction.getSysCodeByProvCode(cmuData.getMerID()),
					cmuData.getStatusDesc());
			cmuData.getProperties().put("BankID", cmuData.getBankID());
		} else {
			cmuData.setTransCode(CoreConstant.TransCode.TSHOP_PAY.getCode());
			logHandle.info(false, cmuData.getStatus(),
					CoreConstant.TransCode.TSHOP_PAY.getCode(),
					CoreConstant.TransCode.TSHOP_PAY.getDesc(),
					cmuData.getMerID(),
					CommonFunction.getSysCodeByProvCode(cmuData.getMerID()),
					cmuData.getStatusDesc());
		}
		CoreResultRsp coreRsp = null;
		if (cmuData.getStatus().equals(
				CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {
			String jsonStr = JacksonUtil.beanToStr(cmuData);
			logger.info("省份{}支付请求发送到核心[{}]", cmuData.getMerID(), jsonStr);
			String responseText = baseTcp.sendMessage(jsonStr);

			logger.info("...省份{}...接收到核心返回支付请求响应信息:[{}]", cmuData.getMerID(),
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
			cmuData.setProperties(coreRsp.getProperties());
			cmuData.setStatus(coreRsp.getRspCode());
		} else {
			coreRsp = new CoreResultRsp();
			coreRsp.setRspCode(cmuData.getStatus());
			coreRsp.setRspInfo(cmuData.getStatusDesc());
		}
		/*
		 * 转发信息至指定银行
		 */
		MsgData msgData = cmuSwitchChannel
				.transferShopPayLink(cmuData, coreRsp);
		return msgData;
	}

}
