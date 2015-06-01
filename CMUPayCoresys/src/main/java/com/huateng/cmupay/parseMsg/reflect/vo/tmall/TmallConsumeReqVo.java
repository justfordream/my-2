package com.huateng.cmupay.parseMsg.reflect.vo.tmall;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author panlg
 * @version 创建时间：2013-11-19 上午11:15:57 
 * 类说明  天猫全网浙江发起充值请求报文体
 */
public class TmallConsumeReqVo {
	
	/*订单编号*/
	@CustomAnnotation(path = "Body.OrderID")
	@NotNull(message="orderId不能为空")
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="orderId格式不正确")
	private String orderID;
	
	/*支付流水号*/
	@CustomAnnotation(path = "Body.PayTransID")
	@Pattern(regexp="[0-9,a-z,A-Z]{0,32}",message="payTransID格式不正确")
	private String payTransID;
	
	@CustomAnnotation(path = "Body.IDType")
	@NotNull(message = "IDType格式不正确")
	@Pattern(regexp = "01", message = "IDType格式不正确")
	private String IDType;

	@CustomAnnotation(path = "Body.IDValue")
	@NotNull(message = "IDValue格式不正确")
//	@Length(min=11,max=11,message="IDValue格式不正确")
//	@Pattern(regexp="[0-9]{11}",message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="IDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	
	/*归属省份*/
	@CustomAnnotation(path = "Body.HomeProv")
	@NotNull(message = "homeProv不能为空")
	@Pattern(regexp="[0-9]{3}",message="homeProv格式不正确")
	private String homeProv;
	
	/*订单总金额*/
	@CustomAnnotation(path = "Body.Payment")
	@NotNull(message = "payment不能为空")
//	@Pattern(regexp="[0-9]{1,9}",message="payment格式不正确")
	@Min(0)
	@Max(999999999L)
	private Long payment;
	
	/*订单的充值金额*/
	@CustomAnnotation(path = "Body.ChargeMoney")
	@NotNull(message = "chargeMoney不能为空")
//	@Pattern(regexp="[0-9]{1,9}",message="chargeMoney格式不正确")
	@Min(0)
	@Max(999999999L)
	private Long chargeMoney;
	
	
	/*订单中的产品数量*/
	@CustomAnnotation(path = "Body.ProdCnt")
	@NotNull(message = "prodCnt不能为空")
//	@Pattern(regexp="[0-9]{1,9}",message="prodCnt格式不正确")
	@Min(0)
	@Max(999999999L)
	private Long prodCnt;
	
	/*充值产品编号*/
	@CustomAnnotation(path = "Body.ProdID")
	@NotNull(message = "prodId不能为空")
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="prodId格式不正确")
	private String prodId;
	
	/*佣金*/
	@CustomAnnotation(path = "Body.Commision")
	@NotNull(message = "commision不能为空")
//	@Pattern(regexp="[0-9]{1,9}",message="commision格式不正确")
	@Min(0)
	@Max(999999999L)
	private Long commision;
	
	/*积分返点费用*/
	@CustomAnnotation(path = "Body.RebateFee")
	@NotNull(message = "rebateFee不能为空")
//	@Pattern(regexp="[0-9]{1,9}",message="rebateFee格式不正确")
	@Min(0)
	@Max(999999999L)
	private Long rebateFee;
	
	/*产品折减金额*/ 
	@CustomAnnotation(path = "Body.ProdDiscount")
//	@Pattern(regexp="[0-9]{0,9}",message="prodDiscount格式不正确")
	@Max(999999999L)
	private Long prodDiscount;
	
	/*信用卡费用*/ 
	@CustomAnnotation(path = "Body.CreditCardFee")
//	@Pattern(regexp="[0-9]{0,9}",message="creditCardFee格式不正确")
//	@Min(1)
	@Max(999999999L)
	private Long creditCardFee;
	
	/*服务费用*/ 
	@CustomAnnotation(path = "Body.ServiceFee")
//	@Pattern(regexp="[0-9]{0,9}",message="serviceFee格式不正确")
	@Max(999999999L)
	private Long serviceFee;
	
	/*缴费类型	 01：缴预存;固定填01*/ 
	@CustomAnnotation(path = "Body.PayedType")
	@Pattern(regexp = "01", message = "payedType格式不正确")
	private String payedType;
	
	/*营销活动编号*/ 
	@CustomAnnotation(path = "Body.ActivityNO")
	@Length(min=0,max=32,message="activityNO格式不正确")
	private String activityNO;
	
	/*商品上架编码*/ 
	@CustomAnnotation(path = "Body.ProdShelfNO")
	@Pattern(regexp="[0-9,a-z,A-Z]{0,32}",message="prodShelfNO格式不正确")
	private String prodShelfNO;

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getPayTransID() {
		return payTransID;
	}

	public void setPayTransID(String payTransID) {
		this.payTransID = payTransID;
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
		return homeProv;
	}

	public void setHomeProv(String homeProv) {
		this.homeProv = homeProv;
	}

	public Long getPayment() {
		return payment;
	}

	public void setPayment(Long payment) {
		this.payment = payment;
	}

	public Long getChargeMoney() {
		return chargeMoney;
	}

	public void setChargeMoney(Long chargeMoney) {
		this.chargeMoney = chargeMoney;
	}

	public Long getProdCnt() {
		return prodCnt;
	}

	public void setProdCnt(Long prodCnt) {
		this.prodCnt = prodCnt;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public Long getCommision() {
		return commision;
	}

	public void setCommision(Long commision) {
		this.commision = commision;
	}

	public Long getRebateFee() {
		return rebateFee;
	}

	public void setRebateFee(Long rebateFee) {
		this.rebateFee = rebateFee;
	}

	public Long getProdDiscount() {
		return prodDiscount;
	}

	public void setProdDiscount(Long prodDiscount) {
		this.prodDiscount = prodDiscount;
	}

	public Long getCreditCardFee() {
		return creditCardFee;
	}

	public void setCreditCardFee(Long creditCardFee) {
		this.creditCardFee = creditCardFee;
	}

	public Long getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(Long serviceFee) {
		this.serviceFee = serviceFee;
	}

	public String getPayedType() {
		return payedType;
	}

	public void setPayedType(String payedType) {
		this.payedType = payedType;
	}

	public String getActivityNO() {
		return activityNO;
	}

	public void setActivityNO(String activityNO) {
		this.activityNO = activityNO;
	}

	public String getProdShelfNO() {
		return prodShelfNO;
	}

	public void setProdShelfNO(String prodShelfNO) {
		this.prodShelfNO = prodShelfNO;
	}
	
}
