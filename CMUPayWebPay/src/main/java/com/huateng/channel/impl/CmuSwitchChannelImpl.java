package com.huateng.channel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.CmuData;
import com.huateng.bean.CoreResultRsp;
import com.huateng.bean.ShopData;
import com.huateng.channel.CmuSwitchChannel;
import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.exception.AppException;
import com.huateng.core.link.LingSignChannel;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.util.RequestUtil;
import com.huateng.link.Link2External;
import com.huateng.log.LogHandle;
import com.huateng.log.MessageLogger;
import com.huateng.utils.WebBackToMer;
import com.huateng.vo.MsgData;

/**
 * 处理省厅请求
 * 
 * @author Gary
 * 
 */
public class CmuSwitchChannelImpl implements CmuSwitchChannel {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(CmuSwitchChannelImpl.class);
	/**
	 * 数据集合
	 */
	protected Map<String, Map<String, Map<String, Link2External>>> switchMaps;

	@Value("${SHOP_BANK_LIST}")
	private String shopBankList;

	@Value("${PROVICE_LIST}")
	private String proviceList;

	@Value("${CHECK_STATUS}")
	private String checkStatus;

	@Value("${SHOP_CHECK_STATUS}")
	private String shopShopStatus;

	@Autowired
	private RemoteService cmuSecurityRemoting;

	@Autowired
	private RemoteService bankSecurityRemoting;

	@Autowired
	private LogHandle logHandle;

	/**
	 * 校验签约信息
	 */
	public void validateSignLink(CmuData cmuData) throws AppException {
		try {
			if (StringUtils.equals(cmuData.getStatus(),
					CoreConstant.CmuErrorCode.SUCCESS)) {
				String cmuID = cmuData.getOrigDomain();
				String plainText = cmuData.assemlySignPlainText();
				logger.debug("..省份{}....签约验签字符串{}", new Object[] { cmuID,
						plainText });
				boolean result = true;
				logger.debug("......验签开关......{}", checkStatus);
				if (CoreConstant.CHECK_STATUS.equals(checkStatus)
						&& this.isCheckProArea(cmuID)) {
					result = cmuSecurityRemoting.verify(cmuID, plainText,
							cmuData.getSig());
				}
				logger.info("...省份{}...签约请求验签结果：{}", new Object[] { cmuID,
						result });
				if (!result) {
					logger.error("省份{}签约请求验签失败,原文{}", cmuID, plainText);
					upayLog.error("省份{}签约请求验签失败,原文{}", cmuID, plainText);
					cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0021);
				} else {
					logger.debug("...省份{}...签约请求验签成功......", cmuID);
				}
			}
		} catch (Exception e) {
			logger.error("省份:{}验签错误{}", new Object[] { cmuData.getOrigDomain(),
					e.getMessage() });
			e.printStackTrace();
			throw new AppException(e);
		}
	}

	/**
	 * 校验支付信息
	 */
	public void validatePayLink(CmuData cmuData) throws AppException {
		try {
			if (StringUtils.equals(cmuData.getStatus(),
					CoreConstant.CmuErrorCode.SUCCESS)) {
				String cmuID = cmuData.getMerID();
				String plainText = cmuData.assemlyPayPlainText();
				logger.info("省份{}订单号{}支付验签字符串：{}", new Object[] { cmuID,
						cmuData.getOrderID(), plainText });
				upayLog.debug("省份:{}订单号{}支付验签字符串：{}", new Object[] { cmuID,
						cmuData.getOrderID() });
				boolean result = true;
				logger.debug("省份{}验签开关{}", new Object[] { cmuID, checkStatus });
				if (CoreConstant.CHECK_STATUS.equals(checkStatus)
						&& this.isCheckProArea(cmuID)) {
					result = cmuSecurityRemoting.verify(cmuID, plainText,
							cmuData.getSig());
				}
				logger.debug("省份{}订单号{}支付请求验签结果：{}", new Object[] { cmuID,
						cmuData.getOrderID(), result });
				if (!result) {
					logger.error("省份{}订单号{}支付请求验签失败.", cmuID,
							cmuData.getOrderID());
					upayLog.error("省份{}订单号{}支付请求验签失败.", cmuID,
							cmuData.getOrderID());
					cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0021);
				} else {
					logger.debug("省份{}.订单号{}支付请求验签成功", cmuID,
							cmuData.getOrderID());
				}
			}
		} catch (Exception e) {
			logger.error("省份:{}开通认证支付验签错误{}", new Object[] {
					cmuData.getMerID(), e.getMessage() });
			e.printStackTrace();
			throw new AppException(e);
		}
	}

	/**
	 * 校验支付信息
	 */
	public void validateShopPayLink(ShopData cmuData) {
		try {
			if (StringUtils.equals(cmuData.getStatus(),
					CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {
				String cmuID = cmuData.getMerID();
				String plainText = cmuData.assemlyPayPlainText();
				logger.info("省份{}订单号{}支付验签字符串：{}", new Object[] { cmuID,
						cmuData.getOrderID(), plainText });
				upayLog.debug("省份:{}订单号{}支付验签字符串：{}", new Object[] { cmuID,
						cmuData.getOrderID() });
				boolean result = true;
				logger.debug("省份{}验签开关{}",
						new Object[] { cmuID, shopShopStatus });
				if (CoreConstant.CHECK_STATUS.equals(shopShopStatus)
						&& this.isCheckProArea(cmuID)) {
					result = bankSecurityRemoting.verify(cmuID, plainText,
							cmuData.getSig());
				}
				logger.debug("省份{}订单号{}支付请求验签结果：{}", new Object[] { cmuID,
						cmuData.getOrderID(), result });
				upayLog.debug("省份{}订单号{}支付请求验签结果:{}", new Object[] { cmuID,
						cmuData.getOrderID(), result });
				if (!result) {
					logger.debug("省份{}订单号{}支付请求验签失败.", cmuID,
							cmuData.getOrderID());
					upayLog.error("省份{}订单号{}支付请求验签失败.", cmuID,
							cmuData.getOrderID());
					cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_014A06
							.getCode());
				} else {
					logger.debug("省份{}.订单号{}支付请求验签成功", cmuID,
							cmuData.getOrderID());
				}
			}
		} catch (Exception e) {
			logger.error("省份:{}缴费验签错误{}",
					new Object[] { cmuData.getMerID(), e.getMessage() });
			upayLog.error("省份:{}缴费验签错误{}", new Object[] { cmuData.getOrderID(),
					e.getMessage() });
			e.printStackTrace();
		}
	}

	/**
	 * 向银行发送签约信息
	 */
	public MsgData transferSignLink(CmuData cmuData) throws AppException {
		MsgData msgData = new MsgData();
		try {
			if (StringUtils.equals(cmuData.getStatus(),
					CoreConstant.CmuErrorCode.SUCCESS)) {
				Map<String, Map<String, Link2External>> map = switchMaps
						.get(CoreConstant.TRANSFER);
				if (map != null) {
					Map<String, Link2External> merMap = map
							.get(CoreConstant.CMU);
					Link2External link2External = merMap.get(cmuData
							.getBankID());
					msgData = link2External.transferSign(cmuData);
					this.logHandle.info(true,
							CoreConstant.ErrorCode.SUCCESS.getCode(),
							CoreConstant.TransCode.SIGN.getCode(),
							CoreConstant.TransCode.SIGN.getDesc(),
							cmuData.getOrigDomain(), cmuData.getBankID(),
							"统一支付网关向银行[" + cmuData.getBankID() + "]发起的签约交易");
					upayLog.debug(
							"状态：{}交易码:{}订单号:{}省份：{}向银行{}发起签约请求",
							new Object[] {
									CoreConstant.ErrorCode.SUCCESS.getCode(),
									CoreConstant.TransCode.SIGN.getCode(),
									cmuData.getSessionID(),
									cmuData.getOrigDomain(),
									cmuData.getBankID() });
				}
			} else {
				logger.debug("订单号{}处理错误 直接返回给省网厅,返回状态{}",
						cmuData.getSessionID(), cmuData.getStatus());
				Map<String, String> formParams = new HashMap<String, String>();
				ErrorBean bean = ErrorConfigUtil.getErrorBean(
						ErrorConfigUtil.CMU, cmuData.getStatus());
				formParams.put("RspCode", bean.getOuterCode());
				formParams.put("RspInfo", bean.getErrorMsg());
				formParams.put("SessionID", cmuData.getSessionID());
				formParams.put("SubID", "");
				formParams.put("SubTime", "");
				formParams.put("BankAcctID", "");
				formParams.put("BankAcctType", "");
				formParams.put("BankID", cmuData.getBankID());
				formParams.put("TransactionID", cmuData.getTransactionID());
				formParams.put("OrigDomain", cmuData.getOrigDomain());
				formParams.put("CLIENTIP", cmuData.getCLIENTIP());
				formParams.put("ActionDate", "");
				formParams.put("UserIDType", "");
				formParams.put("UserID", "");
				formParams.put("UserName", "");
				formParams.put("PayType", cmuData.getPayType());
				formParams.put("RechAmount", cmuData.getRechAmount());
				formParams.put("RechThreshold", cmuData.getRechThreshold());
				formParams.put("UserCat", "");
				formParams.put("MCODE", CoreConstant.TRANS_SIGN_CODE);
				String sig = new LingSignChannel().getSignKey(formParams,
						checkStatus);
				formParams.put("Sig", RequestUtil.paseEncode(sig));
				String formHtml = WebBackToMer.getBackResult(formParams,
						cmuData.getBackURL(), true);
				msgData.setRedirectHtml(formHtml);
			}
			return msgData;
		} catch (Exception e) {
			logger.error(
					"省份:{}订单号{}发送签约错误{}",
					new Object[] { cmuData.getOrigDomain(),
							cmuData.getSessionID(), e.getMessage() });
			throw new AppException(e);
		}
	}

	/**
	 * 向银行发送支付信息
	 */
	public MsgData transferPayLink(CmuData cmuData, CoreResultRsp coreRsp)
			throws AppException {
		MsgData msgData = new MsgData();
		try {
			if (StringUtils.equals(cmuData.getStatus(),
					CoreConstant.CmuErrorCode.SUCCESS)) {
				Map<String, Map<String, Link2External>> map = switchMaps
						.get(CoreConstant.TRANSFER);
				if (map != null) {
					Map<String, Link2External> merMap = map
							.get(CoreConstant.CMU);
					Link2External link2External = merMap.get(cmuData
							.getBankID());
					msgData = link2External.transferPay(cmuData);
					this.logHandle.info(true,
							CoreConstant.ErrorCode.SUCCESS.getCode(),
							CoreConstant.TransCode.PAY.getCode(),
							cmuData.getOrderID(), cmuData.getCmuID(),
							cmuData.getBankID(),
							"统一支付网关向银行[" + cmuData.getBankID() + "]发起的缴费交易");
					upayLog.debug(
							"状态：{}交易码:{}订单号:{}省份：{}向银行{}发起缴费请求",
							new Object[] {
									CoreConstant.ErrorCode.SUCCESS.getCode(),
									CoreConstant.TransCode.PAY.getCode(),
									cmuData.getOrderID(), cmuData.getCmuID(),
									cmuData.getBankID() });
				}
			} else {
				logger.debug("省份{}#订单号{}处理结果异常直接返回省份,返回代码{}#########",
						new Object[] { cmuData.getMerID(),
								cmuData.getOrderID(), cmuData.getStatus() });
				Map<String, String> formParams = new HashMap<String, String>();
				ErrorBean bean = ErrorConfigUtil.getErrorBean(
						ErrorConfigUtil.CMU, cmuData.getStatus());
				formParams.put("RspCode", bean.getOuterCode());
				formParams.put("RspInfo", bean.getErrorMsg());

				formParams.put("MerID", cmuData.getMerID());
				formParams.put("OrderID", cmuData.getOrderID());
				formParams.put("MerVAR", cmuData.getMerVAR());
				formParams.put("OrderTime", cmuData.getOrderTime());
				formParams.put("Payed", cmuData.getPayed());
				formParams.put("CurType", cmuData.getCurType());
				formParams.put("MCODE", CoreConstant.TRANS_PAY_CODE);

				String sig = new LingSignChannel().getPayKey(formParams,
						checkStatus);
				formParams.put("Sig", RequestUtil.paseEncode(sig));
				String formHtml = WebBackToMer.getBackResult(formParams,
						cmuData.getBackURL(), true);
				msgData.setRedirectHtml(formHtml);
			}
			return msgData;
		} catch (Exception e) {
			logger.error(
					"省份:{}订单号{}发送签约错误{}",
					new Object[] { cmuData.getOrigDomain(),
							cmuData.getOrderID(), e.getMessage() });
			upayLog.error(
					"省份:{}订单号{}发送签约错误",
					new Object[] { cmuData.getOrigDomain(),
							cmuData.getOrderID() });
			throw new AppException(e);
		}
	}

	/**
	 * 向银行发送支付信息
	 * 
	 * @throws AppException
	 */
	public MsgData transferShopPayLink(ShopData cmuData, CoreResultRsp coreRsp)
			throws AppException {
		MsgData msgData = new MsgData();
		try {
			if (StringUtils.equals(cmuData.getStatus(),
					CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {
				Map<String, Map<String, Link2External>> map = switchMaps
						.get(CoreConstant.TRANSFER);
				if (map != null) {
					Map<String, Link2External> merMap = map
							.get(CoreConstant.CMU);

					// // 移动商城充值缴费，走银联
					// Link2External link2External = merMap
					// .get(CoreConstant.BankCode.TPAY.getCode());

					// 如果配置就相应的银行，否则走银联
					Link2External link2External = null;
					if (shopBankList.indexOf(cmuData.getBankID()) >= 0) {
						link2External = merMap.get(cmuData.getBankID());
					} else {
						link2External = merMap.get(CoreConstant.BankCode.TPAY
								.getCode());
					}
					msgData = link2External.transferShopPay(cmuData);
					this.logHandle
							.info(true,
									CoreConstant.BankResultCode.UPAY_B_010A00
											.getCode(), cmuData.getTransCode(),
									cmuData.getOrderID(),
									cmuData.getHomeProv(), cmuData.getBankID(),
									"统一支付网关向银行[" + cmuData.getBankID()
											+ "]发起的缴费交易");
					logger.info(
							"状态：{}交易码:{}订单号:{}省份：{}向银行{}发起缴费请求",
							new Object[] {
									CoreConstant.BankResultCode.UPAY_B_010A00
											.getCode(),
									CoreConstant.TransCode.PAY.getCode(),
									cmuData.getOrderID(),
									cmuData.getHomeProv(), cmuData.getBankID() });
					logger.info("向银行发送支付请求,{}", msgData.getRedirectHtml());
				}
			} else {
				logger.info("处理结果失败，直接返回省份,省份{}#订单号{},返回代码{}#########",
						new Object[] { cmuData.getMerID(),
								cmuData.getOrderID(), cmuData.getStatus() });
				Map<String, String> formParams = new HashMap<String, String>();
				// ErrorBean bean = ErrorConfigUtil.getErrorBean(
				// ErrorConfigUtil.BANK, cmuData.getStatus());
				formParams.put("RspCode", coreRsp.getRspCode());
				formParams.put("RspInfo", coreRsp.getRspInfo());
				formParams.put("OrderID", cmuData.getOrderID());
				formParams.put("MerVAR", cmuData.getMerVar());
				String sig = new LingSignChannel().getShopPayKey(formParams,
						checkStatus);
				formParams.put("Sig", RequestUtil.paseEncode(sig));
				String formHtml = WebBackToMer.getBackResult(formParams,
						cmuData.getBackURL(), true);
				msgData.setRedirectHtml(formHtml);
			}
			return msgData;
		} catch (Exception e) {
			logger.error("省份:{}订单号{}发送签约错误{}",
					new Object[] { cmuData.getHomeProv(), cmuData.getOrderID(),
							e.getMessage() });
			upayLog.error(
					"省份:{}订单号{}发送签约错误",
					new Object[] { cmuData.getHomeProv(), cmuData.getOrderID() });
			throw new AppException(e);
		}
	}

	/**
	 * 
	 * 根据省份判断是否加密、解密 (all 全部打开,部分打开 4位省份代码用逗号隔开)
	 * 
	 * @return true 需要加密
	 */
	public boolean isCheckProArea(String sysProCode) {
		String proAreas = proviceList;
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

	public void setSwitchMaps(
			Map<String, Map<String, Map<String, Link2External>>> switchMaps) {
		this.switchMaps = switchMaps;
	}

	@Override
	public void validatePayShopOpenLink(ShopData shopData) throws AppException {
		try {
			if (StringUtils.equals(shopData.getStatus(),
					CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {
				String cmuID = shopData.getMerID();
				String plainText = shopData.assemlyPayPlainText();
				logger.info("移动商城{}订单号{}开通认证支付验签字符串：{}", new Object[] { cmuID,
						shopData.getOrderID(), plainText });
				upayLog.debug("移动商城:{}订单号{}开通认证支付验签字符串：{}", new Object[] {
						cmuID, shopData.getOrderID() });
				boolean result = true;
				logger.debug("移动商城{}验签开关{}", new Object[] { cmuID,
						shopShopStatus });
				if (CoreConstant.CHECK_STATUS.equals(shopShopStatus)
						&& this.isCheckProArea(cmuID)) {
					result = bankSecurityRemoting.verify(cmuID, plainText,
							shopData.getSig());
				}
				logger.debug("移动商城{}订单号{}开通认证支付请求验签结果：{}", new Object[] {
						cmuID, shopData.getOrderID(), result });
				upayLog.debug("移动商城{}订单号{}开通认证支付请求验签结果:{}", new Object[] {
						cmuID, shopData.getOrderID(), result });
				if (!result) {
					logger.debug("移动商城{}订单号{}开通认证支付请求验签失败.", cmuID,
							shopData.getOrderID());
					upayLog.error("移动商城{}订单号{}开通认证支付请求验签失败.", cmuID,
							shopData.getOrderID());
					shopData.setStatus(CoreConstant.BankResultCode.UPAY_B_014A06
							.getCode());
				} else {
					logger.debug("移动商城{}.订单号{}开通认证支付请求验签成功", cmuID,
							shopData.getOrderID());
				}
			}
		} catch (Exception e) {
			logger.error("移动商城:{}开通认证支付验签错误{}",
					new Object[] { shopData.getMerID(), e.getMessage() });
			upayLog.error("移动商城:{}开通认证支付验签错误{}",
					new Object[] { shopData.getOrderID(), e.getMessage() });
			e.printStackTrace();
		}
	}

	@Override
	public MsgData transferePayShopOpenLink(ShopData shopData,
			CoreResultRsp coreRsp) throws AppException {
		MsgData msgData = new MsgData();
		try {
			if (StringUtils.equals(shopData.getStatus(),
					CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {
				Map<String, Map<String, Link2External>> map = switchMaps
						.get(CoreConstant.TRANSFER);
				if (map != null) {
					Map<String, Link2External> merMap = map
							.get(CoreConstant.CMU);

					// 如果配置就相应的银行，否则走银联
					Link2External link2External = merMap
							.get(CoreConstant.BankCode.TPAY.getCode());
					msgData = link2External.transferPayShopOpen(shopData);
					this.logHandle
							.info(true,
									CoreConstant.BankResultCode.UPAY_B_010A00
											.getCode(),
									shopData.getTransCode(), shopData
											.getOrderID(), shopData
											.getHomeProv(), shopData
											.getBankID(), "统一支付网关向银行["
											+ shopData.getBankID()
											+ "]发起开通认证支付");
					logger.info(
							"状态：{}交易码:{}订单号:{}省份：{}向银行{}发起开通认证支付",
							new Object[] {
									CoreConstant.BankResultCode.UPAY_B_010A00
											.getCode(),
									CoreConstant.TransCode.PAY.getCode(),
									shopData.getOrderID(),
									shopData.getHomeProv(),
									shopData.getBankID() });
					logger.info("向银行发送开通认证支付请求,{}", msgData.getRedirectHtml());
				}
			} else {
				logger.info(
						"处理结果失败，直接返回移动商城,省份{}#订单号{},返回代码{}#########",
						new Object[] { shopData.getMerID(),
								shopData.getOrderID(), shopData.getStatus() });
				Map<String, String> formParams = new HashMap<String, String>();
				// ErrorBean bean = ErrorConfigUtil.getErrorBean(
				// ErrorConfigUtil.BANK, cmuData.getStatus());
				formParams.put("RspCode", coreRsp.getRspCode());
				formParams.put("RspInfo", coreRsp.getRspInfo());
				formParams.put("OrderID", shopData.getOrderID());
				formParams.put("MerVAR", shopData.getMerVar());
				String sig = new LingSignChannel().getShopPayKey(formParams,
						checkStatus);
				formParams.put("Sig", RequestUtil.paseEncode(sig));
				String formHtml = WebBackToMer.getBackResult(formParams,
						shopData.getBackURL(), true);
				msgData.setRedirectHtml(formHtml);
			}
			return msgData;
		} catch (Exception e) {
			logger.error(
					"移动商城:{}订单号{}发送开通认证支付错误{}",
					new Object[] { shopData.getHomeProv(),
							shopData.getOrderID(), e.getMessage() });
			upayLog.error(
					"移动商城:{}订单号{}发送开通认证支付错误",
					new Object[] { shopData.getHomeProv(),
							shopData.getOrderID() });
			throw new AppException(e);
		}
	}

}
