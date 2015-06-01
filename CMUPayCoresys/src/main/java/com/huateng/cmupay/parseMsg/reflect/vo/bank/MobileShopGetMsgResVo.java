package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

public class MobileShopGetMsgResVo extends BaseMsgVo{
	@CustomAnnotation(path = "Body.MerID",power=2)
	@NotNull(message="014A04:MerID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "014A04:MerID参数不正确")
	private String MerID;	//1	String	V32	商户标识
	@CustomAnnotation(path = "Body.OrderID",power=2)
	@NotNull(message="019A27:OrderID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A27:OrderID参数不正确")
	private String OrderID;	//1	String	V32	订单编号
	@CustomAnnotation(path = "Body.OrderTime",power=2)
	@NotNull(message="014A04:OrderTime参数不正确")
	@Pattern(regexp = "[0-9]{14}", message = "014A04:OrderTime参数不正确")
	private String OrderTime; //	1	String	F14	订单日期时间
	@CustomAnnotation(path = "Body.BankAcctID",power=2)
	@NotNull(message="019A22:BankAcctID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A22:BankAcctID参数不正确")
	private String BankAcctID;	//1	String	V32	银行帐号
	@CustomAnnotation(path = "Body.Payment",power=2)
	@NotNull(message="019A51:PayMent参数不正确")
	@Min(0)
	@Max(999999999L)
	private Long Payment;	//1	Number	V9.0	订单总金额
	
	public String getMerID() {
		return MerID;
	}
	public void setMerID(String merID) {
		MerID = merID;
	}
	public String getOrderID() {
		return OrderID;
	}
	public void setOrderID(String orderID) {
		OrderID = orderID;
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
	public Long getPayment() {
		return Payment;
	}
	public void setPayment(Long payment) {
		Payment = payment;
	}
}
