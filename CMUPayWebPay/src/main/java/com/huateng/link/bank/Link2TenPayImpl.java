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
 * 财付通请求信息组装
 * 
 * @author Administrator
 * 
 */
public class Link2TenPayImpl extends Link2External {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog = MessageLogger
			.getLogger(Link2TenPayImpl.class);

	@Autowired
	private TUPayRemoteService tPaySecurityService;

	/**
	 * 请求财付通地址
	 */
	@Value("${TEN.B2C.PAY.URL}")
	private String sendTenUrl;

	/**
	 * 发送至财付通的签名开关
	 */
	@Value("${TEN_SIGN_STATUS}")
	private String tenSignStatus;

	/**
	 * 签名方式
	 */
	@Value("${TEN.B2C.SIGN_TYPE}")
	private String sign_type;

	/**
	 * 接口版本
	 */
	@Value("${TEN.B2C.SERVICE_VERSION}")
	private String service_version;

	/**
	 * 字符集
	 */
	@Value("${TEN.B2C.INPUT_CHARSET}")
	private String input_charset;

	/**
	 * 密钥序号
	 */
	@Value("${TEN.B2C.SIGN_KEY_INDEX}")
	private String sign_key_index;

	/**
	 * 银行类型
	 */
	@Value("${TEN.B2C.BANK_TYPE}")
	private String bank_type;

	/**
	 * 返回URL
	 */
	@Value("${TEN.B2C.RETURN_URL}")
	private String return_url;

	/**
	 * 通知URL
	 */
	@Value("${TEN.B2C.NOTIFY_URL}")
	private String notify_url;

	/**
	 * 币种
	 */
	@Value("${TEN.B2C.FEE_TYPE}")
	private String fee_type;

	@Override
	public MsgData transferSign(CmuData cmuData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MsgData transferPay(CmuData cmuData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MsgData transferShopPay(ShopData shopData) {
		logger.debug("移动商城:{}向到财付通:[{}][{}]发送缴费请求",
				new Object[] { shopData.getMerID(), shopData.getBankID(),
						this.sendUrl });
		upayLog.warn("移动商城:{}向到财付通:[{}][{}]发送缴费请求",
				new Object[] { shopData.getMerID(), shopData.getBankID(),
						this.sendUrl });
		MsgData msgData = new MsgData();
		Map<String, String> data = new HashMap<String, String>();

		// 协议参数5个
		data.put("sign_type", this.sign_type);// 签名方式
		data.put("service_version", this.service_version);// 接口版本
		data.put("input_charset", this.input_charset);// 字符集
		// data.put("sign", "");//签名携带
		data.put("sign_key_index", this.sign_key_index);// 密钥序号

		// 业务参数
		data.put("bank_type", this.bank_type);// 银行类型
		data.put("body", "");// 商品描述
		// data.put("attach", shopData.getOrderID());// 附加数据中存放OrderId,财付通会原值返回
		data.put("return_url", this.return_url);// 返回URL
		data.put("notify_url", this.notify_url);// 通知URL
		// data.put("buyer_id", "");//买方财付通账号
		data.put("partner", shopData.getMerID());// 商户号,由财付通统一分配的10位正整数
		data.put("out_trade_no", shopData.getOrderID());// 商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一
		data.put("total_fee", shopData.getPayment());// 订单总金额，单位为分
		data.put("fee_type", this.fee_type);// 现金支付币种,取值：1（人民币）,默认值是1，暂只支持1
		data.put("spbill_create_ip", shopData.getClientIp());// 用户IP
		// data.put("time_start", shopData.getOrderTime());// 交易起始时间
		// data.put("time_expire", "");// 交易结束时间
		// data.put("transport_fee", "");// 物流费用
		// data.put("product_fee", shopData.getChargeMoney());// 商品费用
		// data.put("goods_tag", shopData.getProdId());// 商品标记

		// 签名
		if (CoreConstant.CHECK_STATUS.equals(tenSignStatus)) {
			data = tPaySecurityService.tenPaySign(data);

			if (data.containsKey("sign")) {
				logger.debug(
						"移动商城:{}向到财付通:[{}][{}]发送请求参数：{}验签成功",
						new Object[] { shopData.getMerID(),
								shopData.getBankID(), this.sendUrl,
								data.toString() });
				upayLog.debug(
						"移动商城:{}向到财付通:[{}][{}]发送支付求参数：{}验签成功",
						new Object[] { shopData.getMerID(),
								shopData.getBankID(), this.sendUrl,
								data.toString() });
			} else {
				logger.debug(
						"移动商城:{}向到财付通:[{}][{}]发送请求参数：{}验签失败",
						new Object[] { shopData.getMerID(),
								shopData.getBankID(), this.sendUrl,
								data.toString() });
				upayLog.debug(
						"移动商城:{}向到财付通:[{}][{}]发送支付求参数：{}验签失败",
						new Object[] { shopData.getMerID(),
								shopData.getBankID(), this.sendUrl,
								data.toString() });
			}
		}

		String redirectHtml = WebBackToMer.getBackResult(data, this.sendTenUrl,
				true);
		msgData.setRedirectHtml(redirectHtml);
		return msgData;
	}

	@Override
	public MsgData transferPayShopOpen(ShopData cmuData) {
		// TODO Auto-generated method stub
		return null;
	}

}
