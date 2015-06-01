package com.huateng.tmall.bean.crm;
import com.huateng.tmall.bean.CustomAnnotation;
/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧充值请求报文体
 */
public class CrmChargeReqVo  {

 
    @CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.IDType")
	private String IDType;
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.IDValue")
	private String IDValue;
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.TransactionID")
	private String TransactionID;

	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.ActionDate")
	private String ActionDate;
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.ActionTime")
	private String ActionTime;
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.Payed")
	private String Payed;
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.BankID")
	private String BankID;
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.CnlTyp")
	private String CnlTyp;
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.PayedType")
	private String PayedType;
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.SettleDate")
	private String SettleDate;
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.BusiTransID")
	private String BusiTransID;//业务流水号
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.PayTransID")
	private String PayTransID;  //支付流水号
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.OrganID")
	private String OrganID;
	//接口改造修改过的属性
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.ChargeMoney")
	private String ChargeMoney;
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.OrderNo")
	private String OrderNo;  //订单号
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.ProductNo")
	private String ProductNo;  //产品编号
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.Payment")
	private String Payment;    //订单总金额
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.OrderCnt")
	private String OrderCnt;   //订单数量
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.Commision")
	private String Commision;  //佣金
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.RebateFee")
	private String RebateFee;  //积分返点费用
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.ProdDiscount")
	private String ProdDiscount;  //产品折减金额
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.CreditCardFee")
	private String CreditCardFee;  //信用卡费用
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.ServiceFee")
	private String ServiceFee;  //电商服务费
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.ActivityNo")
	private String ActivityNo;  //营销活动号
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.ProductShelfNo")
	private String ProductShelfNo;  //商品上架编码
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.Reserve1")
	private String Reserve1;  //保留字段1
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.Reserve2")
	private String Reserve2;  //保留字段2
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.Reserve3")
	private String Reserve3;  //保留字段3
	
	@CustomAnnotation(path="InterBOSS.SvcCont.PaymentReq.Reserve4")
	private String Reserve4;  //保留字段4
	
	
	
	
	
	public String getOrganID() {
		return OrganID;
	}

	public void setOrganID(String organID) {
		OrganID = organID;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public String getPayment() {
		return Payment;
	}

	public void setPayment(String payment) {
		Payment = payment;
	}

	public String getOrderCnt() {
		return OrderCnt;
	}

	public void setOrderCnt(String orderCnt) {
		OrderCnt = orderCnt;
	}

	public String getCommision() {
		return Commision;
	}

	public void setCommision(String commision) {
		Commision = commision;
	}

	public String getRebateFee() {
		return RebateFee;
	}

	public void setRebateFee(String rebateFee) {
		RebateFee = rebateFee;
	}

	public String getProdDiscount() {
		return ProdDiscount;
	}

	public void setProdDiscount(String prodDiscount) {
		ProdDiscount = prodDiscount;
	}

	public String getCreditCardFee() {
		return CreditCardFee;
	}

	public void setCreditCardFee(String creditCardFee) {
		CreditCardFee = creditCardFee;
	}

	public String getServiceFee() {
		return ServiceFee;
	}

	public void setServiceFee(String serviceFee) {
		ServiceFee = serviceFee;
	}

	public String getActivityNo() {
		return ActivityNo;
	}

	public void setActivityNo(String activityNo) {
		ActivityNo = activityNo;
	}

	public String getProductShelfNo() {
		return ProductShelfNo;
	}

	public void setProductShelfNo(String productShelfNo) {
		ProductShelfNo = productShelfNo;
	}

	public String getReserve1() {
		return Reserve1;
	}

	public void setReserve1(String reserve1) {
		Reserve1 = reserve1;
	}

	public String getReserve2() {
		return Reserve2;
	}

	public void setReserve2(String reserve2) {
		Reserve2 = reserve2;
	}

	public String getReserve3() {
		return Reserve3;
	}

	public void setReserve3(String reserve3) {
		Reserve3 = reserve3;
	}

	public String getReserve4() {
		return Reserve4;
	}

	public void setReserve4(String reserve4) {
		Reserve4 = reserve4;
	}

	public String getChargeMoney() {
		return ChargeMoney;
	}

	public void setChargeMoney(String chargeMoney) {
		ChargeMoney = chargeMoney;
	}

	public String getPayTransID() {
		return PayTransID;
	}

	public void setPayTransID(String payTransID) {
		PayTransID = payTransID;
	}

	public String getBusiTransID() {
		return BusiTransID;
	}

	public void setBusiTransID(String busiTransID) {
		BusiTransID = busiTransID;
	}

	public String getIDType() {
		return IDType;
	}

	public void setIDType(String iDType) {
		IDType = iDType;
	}

	public String getIDValue() {
		return IDValue;
	}

	public void setIDValue(String iDValue) {
		IDValue = iDValue;
	}

	public String getTransactionID() {
		return TransactionID;
	}

	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}

	public String getActionDate() {
		return ActionDate;
	}

	public void setActionDate(String actionDate) {
		ActionDate = actionDate;
	}

	public String getPayed() {
		return Payed;
	}

	public void setPayed(String payed) {
		Payed = payed;
	}

	public String getCnlTyp() {
		return CnlTyp;
	}

	public void setCnlTyp(String cnlTyp) {
		CnlTyp = cnlTyp;
	}

	

	public String getPayedType() {
		return PayedType;
	}

	public void setPayedType(String payedType) {
		PayedType = payedType;
	}

	public String getActionTime() {
		return ActionTime;
	}

	public void setActionTime(String actionTime) {
		ActionTime = actionTime;
	}

	public String getBankID() {
		return BankID;
	}

	public void setBankID(String bankID) {
		BankID = bankID;
	}

	public String getSettleDate() {
		return SettleDate;
	}

	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
	}	
}
