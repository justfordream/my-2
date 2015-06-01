package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

public class MobileShopGetMsgReqVo extends BaseMsgVo{
	@CustomAnnotation(path = "Body.MerID")
	@NotNull(message="014A04:MerID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "014A04:MerID参数不正确")
	private String MerID;
	@CustomAnnotation(path = "Body.MerAbbr")
	@NotNull(message="014A04:MerAbbr参数不正确")
	@Pattern(regexp = "\\w{0,16}", message = "014A04:MerAbbr参数不正确")
	private String MerAbbr;
	@CustomAnnotation(path = "Body.SmsType")
	@NotNull(message="014A04:SmsType参数不正确")
	@Pattern(regexp = "00|01|02|03", message = "014A04:SmsType参数不正确")
	private String SmsType;
	@CustomAnnotation(path = "Body.OrderID")
	@NotNull(message="019A27:OrderID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A27:OrderID参数不正确")
	private String OrderID;
	@CustomAnnotation(path = "Body.OrderType")
	@NotNull(message="014A04:OrderType参数不正确")
	@Pattern(regexp = "01|02", message = "014A04:OrderType参数不正确")
	private String OrderType;
	@CustomAnnotation(path = "Body.OrderTime")
	@NotNull(message="014A04:OrderTime参数不正确")
	@Pattern(regexp = "[0-9]{14}", message = "014A04:OrderTime参数不正确")
	private String OrderTime;
	@CustomAnnotation(path = "Body.BankAcctID")
	@NotNull(message="019A22:BankAcctID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A22:BankAcctID参数不正确")
	private String BankAcctID;
	@CustomAnnotation(path = "Body.PayMent")
	@NotNull(message="019A51:PayMent参数不正确")
	@Min(0)
	@Max(999999999L)
	private Long PayMent;
	@CustomAnnotation(path = "Body.CustomerInfo")
	@NotNull(message="014A04:CustomerInfo参数不正确")
	@Pattern(regexp = "\\w{0,512}", message = "014A04:CustomerInfo参数不正确")
	private String CustomerInfo;
	@CustomAnnotation(path = "Body.ShopMerID")
	@Pattern(regexp = "\\w{0,50}", message = "014A04:ShopMerID参数不正确")
	private String ShopMerID;
	@CustomAnnotation(path = "Body.HomeProv")
	@Pattern(regexp = "[0-9]{3}", message = "019A44:HomeProv参数不正确")
	private String HomeProv;
	public String getMerID() {
		return MerID;
	}
	public void setMerID(String merID) {
		MerID = merID;
	}
	public String getMerAbbr() {
		return MerAbbr;
	}
	public void setMerAbbr(String merAbbr) {
		MerAbbr = merAbbr;
	}
	public String getSmsType() {
		return SmsType;
	}
	public void setSmsType(String smsType) {
		SmsType = smsType;
	}
	public String getOrderID() {
		return OrderID;
	}
	public void setOrderID(String orderID) {
		OrderID = orderID;
	}
	public String getOrderType() {
		return OrderType;
	}
	public void setOrderType(String orderType) {
		OrderType = orderType;
	}
	public String getOrderTime() {
		return OrderTime;
	}
	public void setOrderTime(String orderTime) {
		OrderTime = orderTime;
	}
	public String getBankAcctID() {
		return BankAcctID;
	}
	public void setBankAcctID(String bankAcctID) {
		BankAcctID = bankAcctID;
	}
	public Long getPayMent() {
		return PayMent;
	}
	public void setPayMent(Long payMent) {
		PayMent = payMent;
	}
	public String getCustomerInfo() {
		return CustomerInfo;
	}
	public void setCustomerInfo(String customerInfo) {
		CustomerInfo = customerInfo;
	}
	public String getShopMerID() {
		return ShopMerID;
	}
	public void setShopMerID(String shopMerID) {
		ShopMerID = shopMerID;
	}
	public String getHomeProv() {
		return HomeProv;
	}
	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}
}
