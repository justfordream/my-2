/**
 * 
 */
package com.huateng.link.bank;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.CmuData;
import com.huateng.bean.ShopData;
import com.huateng.cmupay.service.TUPayRemoteService;
import com.huateng.core.common.CoreConstant;
import com.huateng.link.Link2External;
import com.huateng.log.MessageLogger;
import com.huateng.utils.WebBackToMer;
import com.huateng.vo.MsgData;

/**
 * @author Administrator
 * 
 */
public class Link2TPayImpl extends Link2External {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(Link2TPayImpl.class);

	@Autowired
	private TUPayRemoteService tPaySecurityService;

	@Value("${TPAY_SIGN_STATUS}")
	private String tpaySignStatus;

	/**
	 * 版本号
	 */
	private String version;

	/**
	 * 编码格式
	 */
	private String encoding;

	/**
	 * 交易类型
	 */
	private String txnType;

	/**
	 * 开通认证支付交易类型
	 */
	private String payOpenTxnType;

	/**
	 * 交易子类
	 */
	private String txnSubType;

	/**
	 * 开通认证支付交易子类
	 */
	private String payOpenTxnSubType;

	/**
	 * 产品类型
	 */
	private String bizType;

	/**
	 * 开通认证支付产品类型
	 */
	private String payOpenBizType;

	/**
	 * 接入商户类型
	 */
	private String accessType;

	/**
	 * 商户类型
	 */
	private String merType;

	/**
	 * 消息通知地址
	 */
	private String frontUrl;

	/**
	 * 后台通知地址
	 */
	private String backUrl;

	/**
	 * 开通认证支付通知地址
	 */
	private String payOpenBackUrl;

	/**
	 * 失败通知地址
	 */
	private String frontFailUrl;

	/**
	 * 开通银联支付地址
	 */
	private String payOpenSendUrl;

	/**
	 * 
	 * @return
	 */

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getTxnSubType() {
		return txnSubType;
	}

	public void setTxnSubType(String txnSubType) {
		this.txnSubType = txnSubType;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getMerType() {
		return merType;
	}

	public void setMerType(String merType) {
		this.merType = merType;
	}

	public String getFrontUrl() {
		return frontUrl;
	}

	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getFrontFailUrl() {
		return frontFailUrl;
	}

	public void setFrontFailUrl(String frontFailUrl) {
		this.frontFailUrl = frontFailUrl;
	}

	public String getPayOpenTxnType() {
		return payOpenTxnType;
	}

	public void setPayOpenTxnType(String payOpenTxnType) {
		this.payOpenTxnType = payOpenTxnType;
	}

	public String getPayOpenTxnSubType() {
		return payOpenTxnSubType;
	}

	public void setPayOpenTxnSubType(String payOpenTxnSubType) {
		this.payOpenTxnSubType = payOpenTxnSubType;
	}

	public String getPayOpenBizType() {
		return payOpenBizType;
	}

	public void setPayOpenBizType(String payOpenBizType) {
		this.payOpenBizType = payOpenBizType;
	}

	public String getPayOpenBackUrl() {
		return payOpenBackUrl;
	}

	public void setPayOpenBackUrl(String payOpenBackUrl) {
		this.payOpenBackUrl = payOpenBackUrl;
	}

	public String getPayOpenSendUrl() {
		return payOpenSendUrl;
	}

	public void setPayOpenSendUrl(String payOpenSendUrl) {
		this.payOpenSendUrl = payOpenSendUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.huateng.link.Link2External#transferSign(com.huateng.bean.CmuData)
	 */
	@Override
	public MsgData transferSign(CmuData cmuData) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.huateng.link.Link2External#transferPay(com.huateng.bean.CmuData)
	 */
	@Override
	public MsgData transferPay(CmuData cmuData) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.huateng.link.Link2External#transferShopPay(com.huateng.bean.ShopData)
	 */
	@Override
	public MsgData transferShopPay(ShopData cmuData) {
		logger.debug("省份:{}向到银联:[{}][{}]发送请求",
				new Object[] { cmuData.getMerID(), cmuData.getBankID(),
						this.sendUrl });
		upayLog.debug("省份:{}向到银联:[{}][{}]发送求",
				new Object[] { cmuData.getMerID(), cmuData.getBankID(),
						this.sendUrl });

		MsgData msgData = new MsgData();
		Map<String, String> data = new HashMap<String, String>();
		// 01版本号
		data.put("version", version);
		// 02编码方式
		data.put("encoding", encoding);
		// 03证书ID 签名时携带
		// data.put("certId", tPaySecurityService.getSignCertId());
		// 05交易类型
		data.put("txnType", this.txnType);
		// 06交易子类
		data.put("txnSubType", this.txnSubType);
		// 07产品类型
		data.put("bizType", this.bizType);
		// 08消息通知地址
		data.put("frontUrl", this.frontUrl);
		// 09后台通知地址
		data.put("backUrl", this.backUrl);
		// 10接入商户类型
		data.put("accessType", this.accessType);
		// 11商户类型
		data.put("merType", this.merType);
		// 12从商户表中获取，商户代码(根据机构代码org_code获取mer_id)
		data.put("merId",
				cmuData.getProperties().get("merId") != null ? cmuData
						.getProperties().get("merId") : "");
		// 13二级商户代码
		data.put("subMerId", "");
		// 14二级商户名称
		data.put("subMerName", "");
		// 15二级商户简称
		data.put("subMerAbbr", "");
		// 16商户订单号
		data.put("orderId", cmuData.getOrderID());
		// 17订单发送时间
		data.put("txnTime", cmuData.getOrderTime());
		// 18账号类型
		data.put("accType", "");
		// 19账号
		data.put("accNo", "");
		// 20交易金额
		data.put("txnAmt", cmuData.getPayment());
		// 21币种
		data.put("currencyCode", "");
		// 22持卡人身份信息 customerInfo
		data.put("customerInfo", "");
		// 23订单超时时间隔
		data.put("orderTimeoutInterval", "");
		// 24订单超时时间
		data.put("payTimeoutTime", cmuData.getReserve2());
		// 25默认支付方式defaultPayType
		data.put("defaultPayType", "");
		// 26支持支付方式 supPayType
		data.put("supPayType", "");
		// 27自定义支付方式 customPayType
		data.put("customPayType", "");
		// 28发卡机构代码 (根据BankId在银行代码表中查找thr_bank_id)
		data.put("issInsCode",
				cmuData.getProperties().get("issInsCode") != null ? cmuData
						.getProperties().get("issInsCode") : "");
		// 29商户摘要 merNote
		data.put("merNote", "");
		// 30终端号 termId
		data.put("termId", "");
		// 31终端类型 termType
		data.put("termType", "");
		// 32交互方式 interactMode
		data.put("interactMode", "");
		// 33商户端用户ID merUserId
		data.put("merUserId", "");
		// 34商品风险类别标识：支付001，缴费111
		if (CoreConstant.ShopPayStatus.TUPAY_STATUS.getValue().equals(
				cmuData.getReserve1())) {
			data.put("shippingFlag", "001");
		} else if (CoreConstant.ShopPayStatus.UPAY_STATUS.getValue().equals(
				cmuData.getReserve1())) {
			data.put("shippingFlag", "111");
		}
		// 35收货国家代码 shippingCountryCode
		data.put("shippingCountryCode", "");
		// 36收货省代码 shippingProvinceCode
		data.put("shippingProvinceCode", "");
		// 37收货市代码 shippingCityCode
		data.put("shippingCityCode", "");
		// 38收货地区代码 shippingDistrictCode
		data.put("shippingDistrictCode", "");
		// 39收货街道地址 shippingStreet
		data.put("shippingStreet", "");
		// 40商品类别 commodityCategory
		data.put("commodityCategory", "");
		// 41商品名称 commodityName
		data.put("commodityName", "");
		// 42商品URL commodityUrl
		data.put("commodityUrl", "");
		// 43商品单价 commodityUnitPrice
		data.put("commodityUnitPrice", "");
		// 44商品数量 commodityQty
		data.put("commodityQty", "");
		// 45请求方保留域 reqReserved
		data.put("reqReserved", cmuData.getReserve1());
		// 46保留域 reserved
		data.put("reserved", "");
		// 47持卡人IP customerIp
		data.put("customerIp", "");
		// 48商户端用户注册时间 merUserRegDt
		data.put("merUserRegDt", "");
		// 49 商户端用户注册邮箱 merUserEmail
		data.put("merUserEmail", "");
		// 50加密证书ID encryptCertId
		data.put("encryptCertId", "");
		// 51终端信息域 userMac
		data.put("userMac", "");
		// 52失败交易前台跳转地址
		data.put("frontFailUrl", this.frontFailUrl);
		// 53 分期付款期数 numberOfInstallments
		data.put("numberOfInstallments", "");
		logger.debug("省份:{}向到银联:[{}][{}]发送请求参数：{}",
				new Object[] { cmuData.getMerID(), cmuData.getBankID(),
						this.sendUrl, data.toString() });
		upayLog.debug("省份:{}向到银联:[{}][{}]发送支付求参数：{}",
				new Object[] { cmuData.getMerID(), cmuData.getBankID(),
						this.sendUrl, data.toString() });

		// 签名
		if (CoreConstant.CHECK_STATUS.equals(tpaySignStatus)) {
			data = tPaySecurityService.sign(
					CoreConstant.BankCode.TPAY.getCode(), data);

			if (data.containsKey("signature")) {
				logger.debug("省份:{}向到银联:[{}][{}]发送请求参数：{}验签成功", new Object[] {
						cmuData.getMerID(), cmuData.getBankID(), this.sendUrl,
						data.toString() });
				upayLog.debug("省份:{}向到银联:[{}][{}]发送支付求参数：{}验签成功", new Object[] {
						cmuData.getMerID(), cmuData.getBankID(), this.sendUrl,
						data.toString() });
			} else {
				logger.debug("省份:{}向到银联:[{}][{}]发送请求参数：{}验签失败", new Object[] {
						cmuData.getMerID(), cmuData.getBankID(), this.sendUrl,
						data.toString() });
				upayLog.debug("省份:{}向到银联:[{}][{}]发送支付求参数：{}验签失败", new Object[] {
						cmuData.getMerID(), cmuData.getBankID(), this.sendUrl,
						data.toString() });
			}
		}

		String redirectHtml = WebBackToMer.getBackResult(data, this.sendUrl,
				true);
		msgData.setRedirectHtml(redirectHtml);
		return msgData;
	}

	@Override
	public MsgData transferPayShopOpen(ShopData shopData) {

		logger.debug("省份:{}向到银联:[{}][{}]发送开通认证支付请求",
				new Object[] { shopData.getMerID(), shopData.getBankID(),
						this.sendUrl });
		upayLog.debug("省份:{}向到银联:[{}][{}]发送开通认证支付请求",
				new Object[] { shopData.getMerID(), shopData.getBankID(),
						this.sendUrl });

		MsgData msgData = new MsgData();
		Map<String, String> data = new HashMap<String, String>();
		// 01版本号
		data.put("version", version);
		// 04编码方式
		data.put("encoding", encoding);
		// 05 交易类型 txnType
		data.put("txnType", payOpenTxnType);
		// 06 交易子类 txnSubType
		data.put("txnSubType", payOpenTxnSubType);
		// 07 产品类型 bizType
		data.put("bizType", payOpenBizType);
		// 08 接入类型 accessType
		data.put("accessType", "");
		// 09 商户代码 merId
		data.put("merId",
				shopData.getProperties().get("merId") != null ? shopData
						.getProperties().get("merId") : "");
		// 10 帐号类型 accType
		data.put("accType", "");
		// 11 帐号 accNo
		data.put("accNo", shopData.getBankAcctID());
		// 12 请求方保留域 reqReserved ,将OrderId放入请求方保留域中
		data.put("reqReserved", shopData.getOrderID());
		// 13 保留域 reserved
		data.put("reserved", "");
		// 14 银行卡验证信息及身份信息 customerInfo
		data.put("customerInfo", shopData.getCustomerInfo());
		// 15 加密证书ID encryptCertId
		data.put("encryptCertId", "");
		// 16 终端信息域 userMac
		data.put("userMac", "");
		// 17 风控评级信息域 riskRateInfo
		data.put("riskRateInfo", "");
		// 18 后台通知地址 backUrl
		data.put("backUrl", payOpenBackUrl);
		// 19 商户订单号 orderId
		data.put("orderId", shopData.getOrderID());
		// 20 订单发送时间 txnTime
		data.put("txnTime", shopData.getOrderTime());
		// 21 交易金额 txnAmt
		data.put("txnAmt", shopData.getPayment());
		// 22 交易币种 currencyCode
		data.put("currencyCode", "");
		// 23 收货地址-详细 shippingStreet
		data.put("shippingStreet", "");
		// 24 持卡人IP customerIp
		data.put("customerIp", "");
		// 25 终端信息域 userMac
		data.put("userMac", "");
		// 26 商品风险类别标识 shippingFlag
		data.put("shippingFlag", "");

		// 签名
		if (CoreConstant.CHECK_STATUS.equals(tpaySignStatus)) {
			data = tPaySecurityService.sign(
					CoreConstant.BankCode.TPAY.getCode(), data);

			if (data.containsKey("signature")) {
				logger.debug("省份:{}向到银联:[{}][{}]发送请求参数：{}验签成功", new Object[] {
						shopData.getMerID(), shopData.getBankID(),
						this.sendUrl, data.toString() });
				upayLog.debug("省份:{}向到银联:[{}][{}]发送支付求参数：{}验签成功", new Object[] {
						shopData.getMerID(), shopData.getBankID(),
						this.sendUrl, data.toString() });
			} else {
				logger.debug("省份:{}向到银联:[{}][{}]发送请求参数：{}验签失败", new Object[] {
						shopData.getMerID(), shopData.getBankID(),
						this.sendUrl, data.toString() });
				upayLog.debug("省份:{}向到银联:[{}][{}]发送支付求参数：{}验签失败", new Object[] {
						shopData.getMerID(), shopData.getBankID(),
						this.sendUrl, data.toString() });
			}
		}

		String redirectHtml = WebBackToMer.getBackResult(data,
				this.payOpenSendUrl, true);
		msgData.setRedirectHtml(redirectHtml);

		return msgData;
	}
}
