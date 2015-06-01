package com.huateng.core.channel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.AliData;
import com.huateng.bean.CoreResultPayRes;
import com.huateng.cmupay.service.TUPayRemoteService;
import com.huateng.core.channel.AliPaySwitchChannel;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.link.LingSignChannel;
import com.huateng.core.util.RequestUtil;
import com.huateng.link.Link2External;
import com.huateng.log.MessageLogger;
import com.huateng.utils.WebBackToMer;
import com.huateng.vo.MsgData;

public class AliPaySwitchChannelImpl implements AliPaySwitchChannel {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(AliPaySwitchChannelImpl.class);

	/**
	 * 数据集合
	 */
	protected Map<String, Map<String, Map<String, Link2External>>> switchMaps;

	@Value("${ALI_PROVICE_LIST}")
	private String aliProviceList;

	@Value("${ALI_SHOP_CHECK_STATUS}")
	private String aliShopCheckStatus;

	@Value("${ALI_CHECK_STATUS}")
	private String aliCheckSwitch;

	@Autowired
	private TUPayRemoteService tPaySecurityService;

	@Override
	public void validateAliPayLink(AliData aliData) {
		try {
			if (StringUtils.equals(aliData.getStatus(),
					CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {

				String bankId = CoreConstant.BankCode.ALI.getCode();

				Map<String, String> plainMap = aliData.assemlyPayPlainMap();

				logger.info("支付宝{}订单号：{}支付验签集合：{}", new Object[] { bankId,
						aliData.getOut_trade_no(), plainMap });
				upayLog.debug("支付宝:{}订单号：{}支付验签字符串：{}", new Object[] { bankId,
						aliData.getOut_trade_no() });

				boolean result = true;

				logger.debug("支付宝{}验签开关{}", new Object[] { bankId,
						aliShopCheckStatus });

				if (CoreConstant.CHECK_STATUS.equals(aliShopCheckStatus)
						&& this.isCheckProArea(bankId)) {
					result = tPaySecurityService.alipayVerify(plainMap);
				}

				logger.debug("支付宝{}订单号：{}支付请求验签结果：{}", new Object[] { bankId,
						aliData.getOut_trade_no(), result });
				upayLog.debug("支付宝{}订单号：{}支付请求验签结果:{}", new Object[] { bankId,
						aliData.getOut_trade_no(), result });

				if (!result) {
					logger.debug("支付宝{}订单号：{}支付请求验签失败.", bankId,
							aliData.getOut_trade_no());
					upayLog.error("支付宝{}订单号：{}支付请求验签失败.", bankId,
							aliData.getOut_trade_no());
					aliData.setStatus(CoreConstant.BankResultCode.UPAY_B_014A06
							.getCode());
				} else {
					logger.debug("支付宝{}.订单号：{}支付请求验签成功", bankId,
							aliData.getOut_trade_no());
				}

			}
		} catch (Exception e) {
			logger.error(
					"支付宝{}.订单号：:{}缴费验签错误{}",
					new Object[] { CoreConstant.BankCode.TEN.getCode(),
							aliData.getOut_trade_no(), e.getMessage() });
			upayLog.error(
					"支付宝{}.订单号：:{}缴费验签错误{}",
					new Object[] { CoreConstant.BankCode.TEN.getCode(),
							aliData.getOut_trade_no(), e.getMessage() });
			e.printStackTrace();
		}
	}

	@Override
	public MsgData transferAliPayLink(CoreResultPayRes coreRsp) {

		MsgData msgData = new MsgData();
		Map<String, String> params = new HashMap<String, String>();

		params.put("OrderID",
				StringUtils.isNotBlank(coreRsp.getOrderID()) ? coreRsp
						.getOrderID().trim() : "");
		if (StringUtils.isNotBlank(coreRsp.getOrderID())) {
			params.put("OrderID", coreRsp.getOrderID().trim());
		} else {
			params.put("OrderID", "");
		}

		params.put("RspCode",
				StringUtils.isNotBlank(coreRsp.getRspCode()) ? coreRsp
						.getRspCode().trim() : "");
		params.put("RspInfo",
				StringUtils.isNotBlank(coreRsp.getRspInfo()) ? coreRsp
						.getRspInfo().trim() : "");
		params.put("MerVAR",
				StringUtils.isNotBlank(coreRsp.getMerVAR()) ? coreRsp
						.getMerVAR().trim() : "");
		params.put("Sig", RequestUtil.paseEncode(new LingSignChannel()
				.getShopPayKey(params, aliCheckSwitch)));

		String toHtml = WebBackToMer.getBackResult(params,
				coreRsp.getBackURL(), true);

		msgData.setRedirectHtml(toHtml);
		msgData.setParams(params);

		return msgData;
	}

	/**
	 * 
	 * 根据省份判断是否加密、解密 (all 全部打开,部分打开 4位省份代码用逗号隔开)
	 * 
	 * @return true 需要加密
	 */
	public boolean isCheckProArea(String sysProCode) {
		String proAreas = aliProviceList;
		if (StringUtils.equals(proAreas, CoreConstant.ALL)) {
			return true;
		} else {
			List<String> proAreaList = new ArrayList<String>();
			String[] proArea = proAreas.split(",");
			boolean isCheckCode = false;
			if (proArea.length == 1) {
				proAreaList.add(proAreas);
			} else {
				for (String proCode : proArea) {
					proAreaList.add(proCode);
				}
			}
			for (String sysCode : proAreaList) {
				if (StringUtils.equals(sysProCode, sysCode)) {
					isCheckCode = true;
					break;
				}
			}
			return isCheckCode;
		}
	}

}
