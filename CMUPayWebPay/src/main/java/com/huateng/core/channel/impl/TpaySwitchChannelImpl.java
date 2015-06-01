/**
 * 
 */
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

import com.huateng.bean.CoreResultPayRes;
import com.huateng.cmupay.service.TUPayRemoteService;
import com.huateng.core.channel.TpaySwitchChannel;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.link.LingSignChannel;
import com.huateng.core.util.RequestUtil;
import com.huateng.link.Link2External;
import com.huateng.log.MessageLogger;
import com.huateng.utils.WebBackToMer;
import com.huateng.vo.MsgData;
import com.huateng.vo.TPayShopOpenData;
import com.huateng.vo.TpayData;

/**
 * @author Administrator
 * 
 */
public class TpaySwitchChannelImpl implements TpaySwitchChannel {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(TpaySwitchChannelImpl.class);

	/**
	 * 数据集合
	 */
	protected Map<String, Map<String, Map<String, Link2External>>> switchMaps;

	@Value("${TPAY_PROVICE_LIST}")
	private String tpayProviceList;

	@Value("${TPAY_SHOP_CHECK_STATUS}")
	private String tpayShopCheckStatus;

	@Value("${TPAY_CHECK_STATUS}")
	private String tpayCheckSwitch;

	@Autowired
	private TUPayRemoteService tPaySecurityService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.huateng.core.channel.TpaySwitchChannel#validateShopPayLink(com.huateng
	 * .bean.ShopData)
	 */
	@Override
	public void validateTPayLink(TpayData tpayData) {
		try {
			if (StringUtils.equals(tpayData.getStatus(),
					CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {

				// 设置为银联
				String cmuID = CoreConstant.BankCode.TPAY.getCode();

				Map<String, String> plainMap = tpayData.assemlyPayPlainMap();

				logger.info("省份{}订单号{}支付验签集合：{}", new Object[] { cmuID,
						tpayData.getOrderId(), plainMap });
				upayLog.debug("省份:{}订单号{}支付验签字符串：{}", new Object[] { cmuID,
						tpayData.getOrderId() });

				boolean result = true;

				logger.debug("省份{}验签开关{}", new Object[] { cmuID,
						tpayShopCheckStatus });

				if (CoreConstant.CHECK_STATUS.equals(tpayShopCheckStatus)
						&& this.isCheckProArea(cmuID)) {
					result = tPaySecurityService.verify(
							CoreConstant.BankCode.TPAY.getCode(), plainMap);
				}

				logger.debug("省份{}订单号{}支付请求验签结果：{}", new Object[] { cmuID,
						tpayData.getOrderId(), result });
				upayLog.debug("省份{}订单号{}支付请求验签结果:{}", new Object[] { cmuID,
						tpayData.getOrderId(), result });

				if (!result) {
					logger.debug("省份{}订单号{}支付请求验签失败.", cmuID,
							tpayData.getOrderId());
					upayLog.error("省份{}订单号{}支付请求验签失败.", cmuID,
							tpayData.getOrderId());
					tpayData.setStatus(CoreConstant.BankResultCode.UPAY_B_014A06
							.getCode());
				} else {
					logger.debug("省份{}.订单号{}支付请求验签成功", cmuID,
							tpayData.getOrderId());
				}

			}
		} catch (Exception e) {
			logger.error("省份:{}缴费验签错误{}",
					new Object[] { tpayData.getMerId(), e.getMessage() });
			upayLog.error("省份:{}缴费验签错误{}", new Object[] {
					tpayData.getOrderId(), e.getMessage() });
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.huateng.core.channel.TpaySwitchChannel#transferShopPayLink(com.huateng
	 * .bean.ShopData, com.huateng.bean.CoreResultRsp)
	 */
	@Override
	public MsgData transferTPayLink(CoreResultPayRes coreRsp) {

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
				.getShopPayKey(params, tpayCheckSwitch)));

		String toHtml = WebBackToMer.getBackResult(params,
				coreRsp.getBackURL(), true);

		msgData.setRedirectHtml(toHtml);
		// msgData.setUrl(coreRsp.getServerURL());
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
		String proAreas = tpayProviceList;
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

	/**
	 * set注入
	 * 
	 * @param switchMaps
	 */
	public void setSwitchMaps(
			Map<String, Map<String, Map<String, Link2External>>> switchMaps) {
		this.switchMaps = switchMaps;
	}

	@Override
	public void validateTPayShopOpenLink(TPayShopOpenData tPayShopOpenData) {
		if (StringUtils.equals(tPayShopOpenData.getStatus(),
				CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {

			// 设置为银联
			String cmuID = CoreConstant.BankCode.TPAY.getCode();

			Map<String, String> plainMap = tPayShopOpenData
					.assemlyPayPlainMap();

			logger.info("省份{}订单号{}开通认证支付验签集合：{}", new Object[] { cmuID,
					tPayShopOpenData.getAccNo(), plainMap });
			upayLog.debug("省份:{}订单号{}开通认证支付验签字符串：{}", new Object[] { cmuID,
					tPayShopOpenData.getAccNo() });

			boolean result = true;

			logger.debug("省份{}验签开关{}", new Object[] { cmuID,
					tpayShopCheckStatus });

			if (CoreConstant.CHECK_STATUS.equals(tpayShopCheckStatus)
					&& this.isCheckProArea(cmuID)) {
				result = tPaySecurityService.verify(
						CoreConstant.BankCode.TPAY.getCode(), plainMap);
			}

			logger.debug("省份{}订单号{}开通认证支付请求验签结果：{}", new Object[] { cmuID,
					tPayShopOpenData.getAccNo(), result });
			upayLog.debug("省份{}订单号{}开通认证支付请求验签结果:{}", new Object[] { cmuID,
					tPayShopOpenData.getAccNo(), result });

			if (!result) {
				logger.debug("省份{}订单号{}开通认证支付请求验签失败.", cmuID,
						tPayShopOpenData.getAccNo());
				upayLog.error("省份{}订单号{}开通认证支付请求验签失败.", cmuID,
						tPayShopOpenData.getAccNo());
				tPayShopOpenData
						.setStatus(CoreConstant.BankResultCode.UPAY_B_014A06
								.getCode());
			} else {
				logger.debug("省份{}.订单号{}开通认证支付请求验签成功", cmuID,
						tPayShopOpenData.getAccNo());
			}

		}
	}

	@Override
	public MsgData transferTPayShopOpenLink(CoreResultPayRes coreRsp,
			TPayShopOpenData tPayShopOpenData) {

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
		params.put("OrderTime",
				StringUtils.isNotBlank(coreRsp.getOrderTime()) ? coreRsp
						.getOrderTime().trim() : "");
		params.put("RspInfo",
				StringUtils.isNotBlank(coreRsp.getRspInfo()) ? coreRsp
						.getRspInfo().trim() : "");
		params.put("MerVAR",
				StringUtils.isNotBlank(coreRsp.getMerVAR()) ? coreRsp
						.getMerVAR().trim() : "");
		params.put("MerID",
				StringUtils.isNotBlank(coreRsp.getMerID()) ? coreRsp.getMerID()
						.trim() : "");

		// params.put("BankAcctID", StringUtils.isNotBlank(tPayShopOpenData
		// .getAccNo()) ? tPayShopOpenData.getAccNo() : "");

		// params.put("CustomerInfo", StringUtils.isNotBlank(tPayShopOpenData
		// .getCustomerInfo()) ? tPayShopOpenData.getCustomerInfo() : "");

		// params.put("PayCardType", StringUtils.isNotBlank(tPayShopOpenData
		// .getPayCardType()) ? tPayShopOpenData.getPayCardType() : "");

		params.put("ActivateStatus", StringUtils.isNotBlank(tPayShopOpenData
				.getActivateStatus()) ? tPayShopOpenData.getActivateStatus()
				: "");

		params.put("Sig", RequestUtil.paseEncode(new LingSignChannel()
				.getShopPayKey(params, tpayCheckSwitch)));

		String toHtml = WebBackToMer.getBackResult(params,
				coreRsp.getBackURL(), true);

		msgData.setRedirectHtml(toHtml);
		// msgData.setUrl(coreRsp.getServerURL());
		msgData.setParams(params);

		return msgData;
	}

}
