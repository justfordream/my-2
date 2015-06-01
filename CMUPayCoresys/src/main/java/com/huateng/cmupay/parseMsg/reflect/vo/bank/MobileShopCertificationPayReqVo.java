package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Range;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 移动商城认证支付、缴费请求报文体
 *
 */
public class MobileShopCertificationPayReqVo extends BaseMsgVo {
	
	//商户标识
	@CustomAnnotation(path = "Body.MerID")
	@NotNull(message="019A30:MerID不能为空")
	@Pattern(regexp = "\\w{1,32}", message = "019A30:MerID参数不正确")
	private String MerID;
	
	//订单编号
	@CustomAnnotation(path = "Body.OrderID")
	@NotNull(message="019A27:OrderID不能为空")
	@Pattern(regexp = "\\w{1,32}", message = "019A27:OrderID参数不正确")
	private String OrderID;
	
	//订单日期时间
	@CustomAnnotation(path = "Body.OrderTime")
	@NotNull(message="019A30:OrderTime不能为空")
	@Pattern(regexp = "\\d{14}", message = "019A30:OrderTime参数不正确")
	private String OrderTime;
	
	//银行帐号
	@CustomAnnotation(path = "Body.BankAcctID")
	@NotNull(message="019A22:BankAcctID不能为空")
	@Pattern(regexp = "\\d{1,32}", message = "019A22:BankAcctID参数不正确")
	private String BankAcctID;
	
	//订单总金额
	@CustomAnnotation(path = "Body.Payment")
	@NotNull(message="019A51:Payment不能为空")
	@Range(min=0,max=999999999,message="019A51:Payment参数不正确")
	private Long Payment;
	
	//银行卡验证信息及身份信息
	@CustomAnnotation(path = "Body.CustomerInfo")
	@NotNull(message="019A30:CustomerInfo不能为空")
	@Pattern(regexp = "\\w{0,512}", message = "019A30:CustomerInfo参数不正确")
	private String CustomerInfo;
	
	//币种
	@CustomAnnotation(path = "Body.CurType")
	@NotNull(message="019A30:CurType不能为空")
	@Pattern(regexp = "RMB", message = "019A30:CurType参数不正确")
	private String CurType;
	
	//订单的充值金额
	@CustomAnnotation(path = "Body.ChargeMoney")
	@Max(value=999999999,message="019A52:ChargeMoney参数不正确")
	private Long ChargeMoney;
	
	//移动用户号码
	@CustomAnnotation(path = "Body.IDValue")
	@Pattern(regexp="1[0-9]{10}",message="019A18:IDValue格式不正确")
	private String IDValue;
	
	//移动用户标识的类型
	@CustomAnnotation(path = "Body.IDType")
	@Pattern(regexp="01",message="019A17:IDType参数不正确")
	private String IDType;
	
	//充值手机号码归属省
	@CustomAnnotation(path = "Body.HomeProv")
	@Pattern(regexp = "\\d{3}", message = "019A44:HomeProv参数不正确")
	private String HomeProv;
	
	//订单中的产品数量
	@CustomAnnotation(path = "Body.ProdCnt")
	@Max(value=999999999,message="019A53:ProdCnt参数不正确")
	private Long ProdCnt;
	
	//充值产品编号
	@CustomAnnotation(path = "Body.ProdID")
	@Pattern(regexp = "\\w{0,32}", message = "019A54:ProdID参数不正确")
	private String ProdID;
	
	//佣金
	@CustomAnnotation(path = "Body.Commision")
	@Max(value=999999999,message="019A55:Commision参数不正确")
	private Long Commision;
	
	//积分返点费用
	@CustomAnnotation(path = "Body.RebateFee")
	@Max(value=999999999,message="019A56:RebateFee参数不正确")
	private Long RebateFee;
	
	//产品折减金额
	@CustomAnnotation(path = "Body.ProdDiscount")
	@Max(value=999999999,message="019A57:ProdDiscount参数不正确")
	private Long ProdDiscount;
	
	//信用卡费用
	@CustomAnnotation(path = "Body.CreditCardFee")
	@Max(value=999999999,message="019A58:CreditCardFee参数不正确")
	private Long CreditCardFee;
	
	//服务费用
	@CustomAnnotation(path = "Body.ServiceFee")
	@Max(value=999999999,message="019A59:ServiceFee参数不正确")
	private Long ServiceFee;
	
	//营销活动编号
	@CustomAnnotation(path = "Body.ActivityNO")
	@Pattern(regexp = "\\w{0,32}", message = "019A60:ActivityNO参数不正确")
	private String ActivityNO;
	
	//商品上架编码
	@CustomAnnotation(path = "Body.ProdShelfNO")
	@Pattern(regexp = "\\w{0,32}", message = "019A30:ProdShelfNO参数不正确")
	private String ProdShelfNO;
	
	//订单类型
	@CustomAnnotation(path = "Body.OrderType")
	@NotNull(message="019A30:OrderType不能为空")
	@Pattern(regexp = "\\d{2}", message = "019A30:OrderType参数不正确")
	private String OrderType;
	
	//订单支付超时时间
	@CustomAnnotation(path = "Body.PayTimeoutTime")
	@Pattern(regexp = "\\d{0,14}", message = "019A30:PayTimeoutTime参数不正确")
	private String PayTimeoutTime;
	
	//二级商户号
	@CustomAnnotation(path = "Body.ShopMerId")
	//@NotNull(message="019A30:ShopMerId不能为空")
	@Pattern(regexp = "\\w{0,50}", message = "019A30:ShopMerId参数不正确")
	private String ShopMerId;
	
	//银行编码
	@CustomAnnotation(path = "Body.BankID")
	@NotNull(message="019A30:BankID不能为空")
	@Pattern(regexp = "\\d{4}", message = "019A30:BankID参数不正确")
	private String BankID;

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


	public String getCustomerInfo() {
		return CustomerInfo;
	}

	public void setCustomerInfo(String customerInfo) {
		CustomerInfo = customerInfo;
	}

	public String getCurType() {
		return CurType;
	}

	public void setCurType(String curType) {
		CurType = curType;
	}

	public String getIDValue() {
		return IDValue;
	}

	public void setIDValue(String iDValue) {
		IDValue = iDValue;
	}

	public String getIDType() {
		return IDType;
	}

	public Long getPayment() {
		return Payment;
	}

	public void setPayment(Long payment) {
		Payment = payment;
	}

	public Long getChargeMoney() {
		return ChargeMoney;
	}

	public void setChargeMoney(Long chargeMoney) {
		ChargeMoney = chargeMoney;
	}

	public void setIDType(String iDType) {
		IDType = iDType;
	}

	public String getHomeProv() {
		return HomeProv;
	}

	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}

	public Long getProdCnt() {
		return ProdCnt;
	}

	public void setProdCnt(Long prodCnt) {
		ProdCnt = prodCnt;
	}

	public String getProdID() {
		return ProdID;
	}

	public void setProdID(String prodID) {
		ProdID = prodID;
	}

	public Long getCommision() {
		return Commision;
	}

	public void setCommision(Long commision) {
		Commision = commision;
	}

	public Long getRebateFee() {
		return RebateFee;
	}

	public void setRebateFee(Long rebateFee) {
		RebateFee = rebateFee;
	}

	public Long getProdDiscount() {
		return ProdDiscount;
	}

	public void setProdDiscount(Long prodDiscount) {
		ProdDiscount = prodDiscount;
	}

	public Long getCreditCardFee() {
		return CreditCardFee;
	}

	public void setCreditCardFee(Long creditCardFee) {
		CreditCardFee = creditCardFee;
	}

	public Long getServiceFee() {
		return ServiceFee;
	}

	public void setServiceFee(Long serviceFee) {
		ServiceFee = serviceFee;
	}

	public String getActivityNO() {
		return ActivityNO;
	}

	public void setActivityNO(String activityNO) {
		ActivityNO = activityNO;
	}

	public String getProdShelfNO() {
		return ProdShelfNO;
	}

	public void setProdShelfNO(String prodShelfNO) {
		ProdShelfNO = prodShelfNO;
	}

	public String getOrderType() {
		return OrderType;
	}

	public void setOrderType(String orderType) {
		OrderType = orderType;
	}

	public String getPayTimeoutTime() {
		return PayTimeoutTime;
	}

	public void setPayTimeoutTime(String payTimeoutTime) {
		PayTimeoutTime = payTimeoutTime;
	}


	public String getShopMerId() {
		return ShopMerId;
	}

	public void setShopMerId(String shopMerId) {
		ShopMerId = shopMerId;
	}

	public String getBankID() {
		return BankID;
	}

	public void setBankID(String bankID) {
		BankID = bankID;
	}
	
	
}
