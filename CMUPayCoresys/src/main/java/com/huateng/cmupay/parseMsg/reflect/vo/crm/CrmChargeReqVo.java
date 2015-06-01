package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;
/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧充值请求报文体
 */
public class CrmChargeReqVo  {


	@CustomAnnotation(path="SvcCont.PaymentReq.IDType")
	@NotNull(message="IDType不能为空")
	@Pattern(regexp="[0-9]{2}",message="IDType格式不正确")
	private String IDType;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.IDValue")
	@NotNull(message="IDValue不能为空")
//	@Length(max=32,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.TransactionID")
	@NotNull(message="TransactionID不能为空")
	@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="TransactionID格式不正确")
	private String TransactionID;

	//接口改造新增属性
	@CustomAnnotation(path="SvcCont.PaymentReq.BusiTransID")
	@Length(max=32,message="BusiTransID格式不正确")
	private String BusiTransID;  //业务流水号
	
	@CustomAnnotation(path="SvcCont.PaymentReq.PayTransID")
	@Length(max=32,message="PayTransID格式不正确")
	private String PayTransID;  //支付流水号
	
	@CustomAnnotation(path="SvcCont.PaymentReq.ActionDate")
	@NotNull(message="ActionDate不能为空")
	@Pattern(regexp="[0-9]{8}",message="ActionDate格式不正确")
	private String ActionDate;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.ActionTime")
	@NotNull(message="ActionTime不能为空")
	@Pattern(regexp="[0-9]{14}",message="ActionTime格式不正确")
	private String ActionTime;
	
	//接口改造修改过的属性
	@CustomAnnotation(path="SvcCont.PaymentReq.ChargeMoney")
	@NotNull(message="ChargeMoney不能为空")
	@Pattern(regexp="[0-9]{1,9}",message="ChargeMoney格式不正确")
	private String ChargeMoney;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.OrganID")
	@NotNull(message="OrganID不能为空")
	@Pattern(regexp="[0-9]{4}",message="OrganID格式不正确")
	private String OrganID;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.CnlTyp")
	@NotNull(message="CnlTyp不能为空")
	@Pattern(regexp="[0-9]{2}",message="CnlTyp格式不正确")
	private String CnlTyp;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.PayedType")
	@NotNull(message="CnlTyp不能为空")
	@Pattern(regexp="[0-9]{2}",message="CnlTyp格式不正确")
	private String PayedType;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.SettleDate")
	@NotNull(message="SettleDate不能为空")
	@Pattern(regexp="[0-9]{8}",message="SettleDate格式不正确")
	private String SettleDate;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.OrderNo")
	@Length(max=32,message="OrderNo格式不正确")
	private String OrderNo;  //订单号
	
	@CustomAnnotation(path="SvcCont.PaymentReq.ProductNo")
	@Length(max=32,message="ProductNo格式不正确")
	private String ProductNo;  //产品编号
	
	@CustomAnnotation(path="SvcCont.PaymentReq.Payment")
	@Length(max=9,message="Payment格式不正确")
	private String Payment;    //订单总金额
	
	@CustomAnnotation(path="SvcCont.PaymentReq.OrderCnt")
	@Length(max=9,message="OrderCnt格式不正确")
	private String OrderCnt;   //订单数量
	
	@CustomAnnotation(path="SvcCont.PaymentReq.Commision")
	@Length(max=9,message="Commision格式不正确")
	private String Commision;  //佣金
	
	@CustomAnnotation(path="SvcCont.PaymentReq.RebateFee")
	@Length(max=9,message="RebateFee格式不正确")
	private String RebateFee;  //积分返点费用
	
	@CustomAnnotation(path="SvcCont.PaymentReq.ProdDiscount")
	@Length(max=9,message="ProdDiscount格式不正确")
	private String ProdDiscount;  //产品折减金额
	
	@CustomAnnotation(path="SvcCont.PaymentReq.CreditCardFee")
	@Length(max=9,message="CreditCardFee格式不正确")
	private String CreditCardFee;  //信用卡费用
	
	@CustomAnnotation(path="SvcCont.PaymentReq.ServiceFee")
	@Length(max=9,message="ServiceFee格式不正确")
	private String ServiceFee;  //电商服务费
	
	@CustomAnnotation(path="SvcCont.PaymentReq.ActivityNo")
	@Length(max=9,message="ActivityNo格式不正确")
	private String ActivityNo;  //营销活动号
	
	@CustomAnnotation(path="SvcCont.PaymentReq.ProductShelfNo")
	@Length(max=9,message="ProductShelfNo格式不正确")
	private String ProductShelfNo;  //商品上架编码
	
	@CustomAnnotation(path="SvcCont.PaymentReq.Reserve1")
	private String Reserve1;  //保留字段1
	
	@CustomAnnotation(path="SvcCont.PaymentReq.Reserve2")
	private String Reserve2;  //保留字段2
	
	@CustomAnnotation(path="SvcCont.PaymentReq.Reserve3")
	private String Reserve3;  //保留字段3
	
	@CustomAnnotation(path="SvcCont.PaymentReq.Reserve4")
	private String Reserve4;  //保留字段4
	
	
	
	
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

	/*public String getPayed() {
		return Payed;
	}

	public void setPayed(String payed) {
		Payed = payed;
	}*/

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

/*	public String getBankID() {
		return BankID;
	}

	public void setBankID(String bankID) {
		BankID = bankID;
	}
*/
	public String getSettleDate() {
		return SettleDate;
	}

	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
	}

	public String getChargeMoney() {
		return ChargeMoney;
	}

	public void setChargeMoney(String chargeMoney) {
		ChargeMoney = chargeMoney;
	}

	public String getOrganID() {
		return OrganID;
	}

	public void setOrganID(String organID) {
		OrganID = organID;
	}

	public String getBusiTransID() {
		return BusiTransID;
	}

	public void setBusiTransID(String busiTransID) {
		BusiTransID = busiTransID;
	}

	public String getPayTransID() {
		return PayTransID;
	}

	public void setPayTransID(String payTransID) {
		PayTransID = payTransID;
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

	
}
