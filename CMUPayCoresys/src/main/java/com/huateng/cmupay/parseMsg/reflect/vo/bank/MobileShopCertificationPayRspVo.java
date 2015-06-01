package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 移动商城认证支付、缴费应答报文体
 *
 */
public class MobileShopCertificationPayRspVo extends BaseMsgVo {
	
	//商户标识
	@CustomAnnotation(path = "Body.MerID")
	private String MerID;
	
	//订单编号
	@CustomAnnotation(path = "Body.OrderID")
	private String OrderID;
	
	//订单日期时间
	@CustomAnnotation(path = "Body.OrderTime")
	private String OrderTime;
	
	//银行帐号
	@CustomAnnotation(path = "Body.BankAcctID")
	private String BankAcctID;
	
	//订单总金额
	@CustomAnnotation(path = "Body.Payment")
	private Long Payment;
	
	//帐期日
	@CustomAnnotation(path = "Body.SettleDate")
	private String SettleDate;
	
	//结算机构
	@CustomAnnotation(path = "Body.SettleOrg")
	private String SettleOrg;

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

	public String getSettleDate() {
		return SettleDate;
	}

	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
	}

	public String getSettleOrg() {
		return SettleOrg;
	}

	public void setSettleOrg(String settleOrg) {
		SettleOrg = settleOrg;
	}

	
	

}
