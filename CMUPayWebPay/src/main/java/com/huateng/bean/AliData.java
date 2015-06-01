package com.huateng.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝返回参数
 * 
 * @author Administrator
 * 
 */
public class AliData {

	// 成功标识
	private String is_success;

	// 签名方式
	private String sign_type;
	// 签名
	private String sign;

	// 商户网站唯一订单号
	private String out_trade_no;

	// 商品名称
	private String subject;

	// 支付类型
	private String payment_type;

	// 接口名称
	private String exterface;

	// 支付宝交易号
	private String trade_no;

	// 交易状态
	private String trade_status;

	// 通知校验ID
	private String notify_id;

	// 通知时间
	private String notify_time;

	// 通知类型
	private String notify_type;

	// 卖家支付宝账号
	private String seller_email;

	// 买家支付宝账号
	private String buyer_email;

	// 卖家支付宝账户号
	private String seller_id;

	// 买家支付宝账户号
	private String buyer_id;

	// 交易金额
	private String total_fee;

	// 商品描述
	private String body;

	// 公用回传参数
	private String extra_common_param;

	// 信用支付购票员的代理人ID
	private String agent_user_id;

	// ******************
	private String serverURL;

	private String backURL;

	private String status;

	private String errorMsg;

	public String getIs_success() {
		return is_success;
	}

	public void setIs_success(String is_success) {
		this.is_success = is_success;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public String getExterface() {
		return exterface;
	}

	public void setExterface(String exterface) {
		this.exterface = exterface;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getTrade_status() {
		return trade_status;
	}

	public void setTrade_status(String trade_status) {
		this.trade_status = trade_status;
	}

	public String getNotify_id() {
		return notify_id;
	}

	public void setNotify_id(String notify_id) {
		this.notify_id = notify_id;
	}

	public String getNotify_time() {
		return notify_time;
	}

	public void setNotify_time(String notify_time) {
		this.notify_time = notify_time;
	}

	public String getNotify_type() {
		return notify_type;
	}

	public void setNotify_type(String notify_type) {
		this.notify_type = notify_type;
	}

	public String getSeller_email() {
		return seller_email;
	}

	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}

	public String getBuyer_email() {
		return buyer_email;
	}

	public void setBuyer_email(String buyer_email) {
		this.buyer_email = buyer_email;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public String getBuyer_id() {
		return buyer_id;
	}

	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getExtra_common_param() {
		return extra_common_param;
	}

	public void setExtra_common_param(String extra_common_param) {
		this.extra_common_param = extra_common_param;
	}

	public String getAgent_user_id() {
		return agent_user_id;
	}

	public void setAgent_user_id(String agent_user_id) {
		this.agent_user_id = agent_user_id;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public String getBackURL() {
		return backURL;
	}

	public void setBackURL(String backURL) {
		this.backURL = backURL;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "AliData [is_success=" + is_success + ", sign_type=" + sign_type
				+ ", sign=" + sign + ", out_trade_no=" + out_trade_no
				+ ", subject=" + subject + ", payment_type=" + payment_type
				+ ", exterface=" + exterface + ", trade_no=" + trade_no
				+ ", trade_status=" + trade_status + ", notify_id=" + notify_id
				+ ", notify_time=" + notify_time + ", notify_type="
				+ notify_type + ", seller_email=" + seller_email
				+ ", buyer_email=" + buyer_email + ", seller_id=" + seller_id
				+ ", buyer_id=" + buyer_id + ", total_fee=" + total_fee
				+ ", body=" + body + ", extra_common_param="
				+ extra_common_param + ", agent_user_id=" + agent_user_id
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	public Map<String, String> assemlyPayPlainMap() {
		Map<String, String> plainMap = new HashMap<String, String>();

		plainMap.put("is_success", is_success);
		plainMap.put("sign_type", sign_type);
		plainMap.put("sign", sign);
		plainMap.put("out_trade_no", out_trade_no);
		plainMap.put("subject", subject);
		plainMap.put("payment_type", payment_type);
		plainMap.put("exterface", exterface);
		plainMap.put("trade_no", trade_no);
		plainMap.put("trade_status", trade_status);
		plainMap.put("notify_id", notify_id);
		plainMap.put("notify_time", notify_time);
		plainMap.put("notify_type", notify_type);
		plainMap.put("seller_email", seller_email);
		plainMap.put("buyer_email", buyer_email);
		plainMap.put("seller_id", seller_id);
		plainMap.put("buyer_id", buyer_id);
		plainMap.put("total_fee", total_fee);
		plainMap.put("body", body);
		plainMap.put("extra_common_param", extra_common_param);
		plainMap.put("agent_user_id", agent_user_id);

		return plainMap;
	}
}
