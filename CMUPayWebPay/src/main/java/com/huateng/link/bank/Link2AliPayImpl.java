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
import com.huateng.utils.AmountUtil;
import com.huateng.utils.WebBackToMer;
import com.huateng.vo.MsgData;

public class Link2AliPayImpl extends Link2External {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(Link2AliPayImpl.class);

	@Autowired
	private TUPayRemoteService tPaySecurityService;

	/**
	 * 发送至财付通的签名开关
	 */
	@Value("${ALI.SIGN.STATUS}")
	private String aliSignStatus;

	/**
	 * 请求支付宝地址
	 */
	@Value("${ALI.B2C.PAY.URL}")
	private String sendAliUrl;

	/**
	 * Service接口
	 */
	@Value("${ALI.PAY.SERVICE}")
	private String service;

	/**
	 * 参数编码字符集
	 */
	@Value("${ALI.PAY.INPUTCHARSET}")
	private String _input_charset;

	/**
	 * 签名方式
	 */
	@Value("${ALI.PAY.SIGN.TYPE}")
	private String sign_type;

	/**
	 * 服务器异步通知页面路径
	 */
	@Value("${ALI.PAY.NOTIFY.URL}")
	private String notify_url;

	/**
	 * 页面跳转同步通知页面路径
	 */
	@Value("${ALI.PAY.RETURN_URL}")
	private String return_url;

	/**
	 * 请求出错时的通知页面路径
	 */
	@Value("${ALI.PAY.ERROR.NOTIFY.URL}")
	private String error_notify_url;

	/**
	 * 支付类型
	 */
	@Value("${ALI.PAY.PAYMENT.TYPE}")
	private String payment_type;

	/**
	 * 默认支付方式
	 */
	@Value("${ALI.PAY.PAYMETHOD}")
	private String paymethod;

	/**
	 * 扫码支付方式
	 */
	@Value("${ALI.PAY.QR.PAY.MODE}")
	private String qr_pay_mode;

	@Override
	public MsgData transferSign(CmuData cmuData) {
		return null;
	}

	@Override
	public MsgData transferPay(CmuData cmuData) {
		return null;
	}

	@Override
	public MsgData transferShopPay(ShopData shopData) {

		logger.debug("移动商城:{}到支付宝:[{}][{}]发送缴费请求",
				new Object[] { shopData.getMerID(), shopData.getBankID(),
						this.sendUrl });
		upayLog.warn("移动商城:{}到支付宝:[{}][{}]发送缴费请求",
				new Object[] { shopData.getMerID(), shopData.getBankID(),
						this.sendUrl });

		MsgData msgData = new MsgData();

		Map<String, String> data = new HashMap<String, String>();

		// 接口名称 service
		data.put("service", this.service);

		// TODO 合作者身份ID partner
		data.put("partner", shopData.getMerID());

		// 参数编码字符集 _input_charset
		data.put("_input_charset", this._input_charset);

		// 签名方式 sign_type
		data.put("sign_type", this.sign_type);

		// 签名 sign
		// data.put("sign", "");

		// 服务器异步通知页面路径 notify_url
		data.put("notify_url", this.notify_url);

		// 页面跳转同步通知页面路径 return_url
		data.put("return_url", this.return_url);

		// 请求出错时的通知页面路径 error_notify_url
		data.put("error_notify_url", this.error_notify_url);

		// 商户网站唯一订单号 out_trade_no
		data.put("out_trade_no", shopData.getOrderID());

		// TODO 商品名称 subject
		data.put("subject", "");

		// 支付类型 payment_type
		data.put("payment_type", this.payment_type);

		// 卖家支付宝账号 seller_email
		// data.put("seller_email", "");

		// 买家支付宝账号 buyer_email
		// data.put("buyer_email", "");

		// 卖家支付宝账户号 seller_id
		// data.put("seller_id", "");

		// 买家支付宝账户号 buyer_id
		// data.put("buyer_id", "");

		// 卖家别名支付宝账号 seller_account_name
		// data.put("seller_account_name", "");

		// 买家别名支付宝账号 buyer_account_name
		// data.put("buyer_account_name", "");

		// 商品单价 price
		// data.put("price", "");

		// 交易金额 total_fee
		data.put("total_fee", AmountUtil.fromFenToYuan(shopData.getPayment()));

		// 购买数量 quantity
		data.put("quantity", shopData.getProdCnt());

		// 商品描述 body
		// data.put("body", "");

		// 商品展示网址 show_url
		// data.put("show_url", "");

		// 默认支付方式 paymethod
		data.put("paymethod", this.paymethod);

		// 支付渠道 enable_paymethod
		// data.put("enable_paymethod", "");

		// 网银支付时是否做CTU校验 need_ctu_check
		// data.put("need_ctu_check", "");

		// 提成类型 royalty_type
		// data.put("royalty_type", "");

		// 分润账号集 royalty_parameters
		// data.put("royalty_parameters", "");

		// 防钓鱼时间戳 anti_phishing_key
		// data.put("anti_phishing_key", "");

		// 客户端IP exter_invoke_ip
		data.put("exter_invoke_ip", shopData.getClientIp());

		// 公用回传参数 extra_common_param
		// data.put("extra_common_param", "");

		// 公用业务扩展参数 extend_param
		// data.put("extend_param", "");

		// 超时时间 it_b_pay
		// data.put("it_b_pay", "");

		// 自动登录标识 default_login
		// data.put("default_login", "");

		// 商户申请的产品类型 product_type
		// data.put("product_type", "");

		// 快捷登录授权令牌 token
		// data.put("token", "");

		// 商户回传业务参数 item_orders_info
		// data.put("item_orders_info", "");

		// 商户买家签约号 sign_id_ext
		// data.put("sign_id_ext", "");

		// 商户买家签约名 sign_name_ext
		// data.put("sign_name_ext", "");

		// 扫码支付方式 qr_pay_mode
		data.put("qr_pay_mode", this.qr_pay_mode);

		// 签名
		if (CoreConstant.CHECK_STATUS.equals(aliSignStatus)) {
			data = tPaySecurityService.alipaySign(data);

			if (data.containsKey("sign")) {
				logger.debug("移动商城:{}支付宝:[{}][{}]发送请求参数：{}验签成功", new Object[] {
						shopData.getMerID(), shopData.getBankID(),
						this.sendUrl, data.toString() });
				upayLog.debug(
						"移动商城:{}支付宝:[{}][{}]发送支付求参数：{}验签成功",
						new Object[] { shopData.getMerID(),
								shopData.getBankID(), this.sendUrl,
								data.toString() });
			} else {
				logger.debug("移动商城:{}支付宝:[{}][{}]发送请求参数：{}验签失败", new Object[] {
						shopData.getMerID(), shopData.getBankID(),
						this.sendUrl, data.toString() });
				upayLog.debug(
						"移动商城:{}支付宝:[{}][{}]发送支付求参数：{}验签失败",
						new Object[] { shopData.getMerID(),
								shopData.getBankID(), this.sendUrl,
								data.toString() });
			}
		}

		String redirectHtml = WebBackToMer.getBackResult(data, this.sendAliUrl,
				true);
		msgData.setRedirectHtml(redirectHtml);

		return msgData;
	}

	@Override
	public MsgData transferPayShopOpen(ShopData shopData) {
		return null;
	}
}
