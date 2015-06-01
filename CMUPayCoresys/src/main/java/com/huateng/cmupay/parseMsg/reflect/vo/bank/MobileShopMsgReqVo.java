package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.ParentAnnotation;

/**
 * @author oul
 * @version 时间 2013/10/10 类说明 商城前置报文体消息
 * */
public class MobileShopMsgReqVo extends BaseMsgVo{
	@CustomAnnotation(path = "Body.OrderID")
	@NotNull(message="019A27:OrderID不能为空")
	@Pattern(regexp = "\\w{0,32}", message = "019A27:OrderID格式不正确")
	private String OrderID;//订单编号 
	
	@CustomAnnotation(path = "Body.PayTransID")
	@Pattern(regexp = "\\w{0,32}", message = "019A31:PayTransID格式不正确")
	private String PayTransID;//支付流水号
	
	@CustomAnnotation(path = "Body.IDType")
	@NotNull(message="019A17:IDType不能为空")
	@Pattern(regexp = "01", message = "019A17:IDType格式不正确")
	private String IDType;//中国移动用户标识类型
	
	@CustomAnnotation(path = "Body.IDValue")
	@NotNull(message="019A18:IDValue不能为空")
//	@Pattern(regexp = "\\w{0,32}", message = "019A33:IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="019A18:iDValue格式不正确")
	private String IDValue;//中国移动用户ID
	
	@CustomAnnotation(path = "Body.HomeProv")
	@NotNull(message="019A44:HomeProv不能为空")
	@Pattern(regexp = "\\w{3}", message = "019A44:HomeProv格式不正确")
	private String HomeProv;//归属省份
	
	@CustomAnnotation(path = "Body.Payment")
	@NotNull(message="019A51:Payment不能为空")
	@Pattern(regexp = "[0-9]{0,9}", message = "019A51:Payment格式不正确")
	private String Payment;//订单总金额
	
	@CustomAnnotation(path = "Body.ChargeMoney")
	@NotNull(message="019A52:ChargeMoney不能为空")
	@Pattern(regexp = "[0-9]{0,9}", message = "019A52:ChargeMoney格式不正确")
	private String ChargeMoney; //订单的充值金额
	
	@CustomAnnotation(path = "Body.ProdCnt")
	@NotNull(message="019A53:ProdCnt不能为空")
	@Pattern(regexp = "[0-9]{0,9}", message = "019A53:ProdCnt格式不正确")
	private String ProdCnt;//订单中的产品数量
	
	@CustomAnnotation(path = "Body.ProdID")
	@NotNull(message="019A54:ProdID不能为空")
	@Pattern(regexp = "\\w{0,32}", message = "019A54:ProdID格式不正确")
	private String ProdID;//充值产品编号
	
	@CustomAnnotation(path = "Body.Commision")
	@NotNull(message="019A55:Commision不能为空")
	@Pattern(regexp = "[0-9]{0,9}", message = "019A55:Commision格式不正确")
	private String Commision;//佣金
	
	@CustomAnnotation(path = "Body.RebateFee")
	@NotNull(message="019A56:RebateFee不能为空")
	@Pattern(regexp = "[0-9]{0,9}", message = "019A56:RebateFee格式不正确")
	private String RebateFee;//积分返点费用
	
	@CustomAnnotation(path = "Body.ProdDiscount")
	@Pattern(regexp = "[0-9]{0,9}", message = "019A57:ProdDiscount格式不正确")
	private String ProdDiscount;//产品折减金额
	
	@CustomAnnotation(path = "Body.CreditCardFee")
	//选填字段不需要验证空值
	//@NotNull(message="019A42:CreditCardFee不能为空")
	@Pattern(regexp = "[0-9]{0,9}", message = "019A58:CreditCardFee格式不正确")
	private String CreditCardFee; //信用卡费用
	
	@CustomAnnotation(path = "Body.ServiceFee")
	@Pattern(regexp = "[0-9]{0,9}", message = "019A59:ServiceFee格式不正确")
	private String ServiceFee;//服务费用
	
	@CustomAnnotation(path = "Body.PayedType")
	@Pattern(regexp = "\\w{2}", message = "019A61:PayedType格式不正确")
	private String PayedType;//缴费类型
	
	@CustomAnnotation(path = "Body.ActivityNO")
	@Length(min=0,max=32,message="019A60:ActivityNO格式不正确")
	private String ActivityNO;//营销活动编号
	
	@CustomAnnotation(path = "Body.ProdShelfNO")
	@Pattern(regexp = "\\w{0,32}", message = "019A46:ProdShelfNO格式不正确")
	private String ProdShelfNO;//产品上架编号
	
	@CustomAnnotation(path = "Body.Reserve1")
	@Pattern(regexp = "\\w{0,32}", message = "019A62:Reserve1格式不正确")
	private String Reserve1;//保留字段1
	
	@CustomAnnotation(path = "Body.Reserve2")
	@Pattern(regexp = "\\w{0,32}", message = "019A63:Reserve2格式不正确")
	private String Reserve2;//保留字段2
	
	@CustomAnnotation(path = "Body.Reserve3")
	@Pattern(regexp = "\\w{0,32}", message = "019A64:Reserve3格式不正确")
	private String Reserve3;//保留字段3
	
	@CustomAnnotation(path = "Body.Reserve4")
	@Pattern(regexp = "\\w{0,32}", message = "019A65:Reserve4格式不正确")
	private String Reserve4;//保留字段4

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getPayTransID() {
		return PayTransID;
	}

	public void setPayTransID(String payTransID) {
		PayTransID = payTransID;
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

	public String getHomeProv() {
		return HomeProv;
	}

	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}

	public String getPayment() {
		return Payment;
	}

	public void setPayment(String payment) {
		Payment = payment;
	}

	public String getChargeMoney() {
		return ChargeMoney;
	}

	public void setChargeMoney(String chargeMoney) {
		ChargeMoney = chargeMoney;
	}

	public String getProdCnt() {
		return ProdCnt;
	}

	public void setProdCnt(String prodCnt) {
		ProdCnt = prodCnt;
	}

	public String getProdID() {
		return ProdID;
	}

	public void setProdID(String prodID) {
		ProdID = prodID;
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

	public String getPayedType() {
		return PayedType;
	}

	public void setPayedType(String payedType) {
		PayedType = payedType;
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
