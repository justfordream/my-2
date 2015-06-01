package com.huateng.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 财付通返回参数
 * 
 * @author zhaojn
 * 
 */
public class TenData {

	private String sign_type;

	private String service_version;

	private String input_charset;

	private String sign;

	private String sign_key_index;

	private String trade_mode;

	private String trade_state;

	private String pay_info;

	private String partner;

	private String bank_type;

	private String bank_billno;

	private String total_fee;

	private String fee_type;

	private String notify_id;

	private String transaction_id;

	private String out_trade_no;

	private String attach;

	private String time_end;

	private String transport_fee;

	private String product_fee;

	private String discount;

	private String buyer_alias;

	// ******************
	private String serverURL;

	private String backURL;

	private String status;

	private String errorMsg;

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getService_version() {
		return service_version;
	}

	public void setService_version(String service_version) {
		this.service_version = service_version;
	}

	public String getInput_charset() {
		return input_charset;
	}

	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSign_key_index() {
		return sign_key_index;
	}

	public void setSign_key_index(String sign_key_index) {
		this.sign_key_index = sign_key_index;
	}

	public String getTrade_mode() {
		return trade_mode;
	}

	public void setTrade_mode(String trade_mode) {
		this.trade_mode = trade_mode;
	}

	public String getTrade_state() {
		return trade_state;
	}

	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
	}

	public String getPay_info() {
		return pay_info;
	}

	public void setPay_info(String pay_info) {
		this.pay_info = pay_info;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getBank_type() {
		return bank_type;
	}

	public void setBank_type(String bank_type) {
		this.bank_type = bank_type;
	}

	public String getBank_billno() {
		return bank_billno;
	}

	public void setBank_billno(String bank_billno) {
		this.bank_billno = bank_billno;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getNotify_id() {
		return notify_id;
	}

	public void setNotify_id(String notify_id) {
		this.notify_id = notify_id;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getTime_end() {
		return time_end;
	}

	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}

	public String getTransport_fee() {
		return transport_fee;
	}

	public void setTransport_fee(String transport_fee) {
		this.transport_fee = transport_fee;
	}

	public String getProduct_fee() {
		return product_fee;
	}

	public void setProduct_fee(String product_fee) {
		this.product_fee = product_fee;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getBuyer_alias() {
		return buyer_alias;
	}

	public void setBuyer_alias(String buyer_alias) {
		this.buyer_alias = buyer_alias;
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
		return "TenData [sign_type=" + sign_type + ", service_version="
				+ service_version + ", input_charset=" + input_charset
				+ ", sign=" + sign + ", sign_key_index=" + sign_key_index
				+ ", trade_mode=" + trade_mode + ", trade_state=" + trade_state
				+ ", pay_info=" + pay_info + ", partner=" + partner
				+ ", bank_type=" + bank_type + ", bank_billno=" + bank_billno
				+ ", total_fee=" + total_fee + ", fee_type=" + fee_type
				+ ", notify_id=" + notify_id + ", transaction_id="
				+ transaction_id + ", out_trade_no=" + out_trade_no
				+ ", attach=" + attach + ", time_end=" + time_end
				+ ", transport_fee=" + transport_fee + ", product_fee="
				+ product_fee + ", discount=" + discount + ", buyer_alias="
				+ buyer_alias + "]";
	}

	public Map<String, String> assemlyPayPlainMap() {
		Map<String, String> plainMap = new HashMap<String, String>();

		plainMap.put("sign_type", sign_type);
		plainMap.put("service_version", service_version);
		plainMap.put("input_charset", input_charset);
		plainMap.put("sign", sign);
		plainMap.put("sign_key_index", sign_key_index);

		plainMap.put("trade_mode", trade_mode);
		plainMap.put("trade_state", trade_state);
		plainMap.put("pay_info", pay_info);
		plainMap.put("partner", partner);
		plainMap.put("bank_type", bank_type);
		plainMap.put("bank_billno", bank_billno);
		plainMap.put("total_fee", total_fee);
		plainMap.put("fee_type", fee_type);
		plainMap.put("notify_id", notify_id);
		plainMap.put("transaction_id", transaction_id);
		plainMap.put("out_trade_no", out_trade_no);
		plainMap.put("attach", attach);
		plainMap.put("time_end", time_end);
		plainMap.put("transport_fee", transport_fee);
		plainMap.put("product_fee", product_fee);
		plainMap.put("discount", discount);
		plainMap.put("buyer_alias", buyer_alias);

		return plainMap;
	}

}
