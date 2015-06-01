package com.huateng.tmall.bean.crm;

import com.huateng.tmall.bean.CustomAnnotation;



/** 
 * @author panlg
 * @version 创建时间：2013-11-19 上午11:15:57 
 * 类说明  天猫全网浙江发起充值请求报文体
 */
public class TmallConsumeReqVo {
	
	/*订单编号*/
	@CustomAnnotation(path = "Body.OrderID")
	private String orderID;
	
	/*支付流水号*/
	@CustomAnnotation(path = "Body.PayTransID")
	private String payTransID;
	
	@CustomAnnotation(path = "Body.IDType")
	private String IDType;

	@CustomAnnotation(path = "Body.IDValue")
	private String IDValue;
	
	/*归属省份*/
	@CustomAnnotation(path = "Body.HomeProv")
	private String homeProv;
	
	/*订单总金额*/
	@CustomAnnotation(path = "Body.Payment")
	private Long payment;
	
	/*订单的充值金额*/
	@CustomAnnotation(path = "Body.ChargeMoney")
	private Long chargeMoney;
	
	/*订单中的产品数量*/
	@CustomAnnotation(path = "Body.ProdCnt")
	private Long prodCnt;
	
	/*充值产品编号*/
	@CustomAnnotation(path = "Body.ProdID")
	private String prodId;
	
	/*佣金*/
	@CustomAnnotation(path = "Body.Commision")
	private Long commision;
	
	/*积分返点费用*/
	@CustomAnnotation(path = "Body.RebateFee")
	private Long rebateFee;
	
	/*产品折减金额*/ 
	@CustomAnnotation(path = "Body.ProdDiscount")
	private Long prodDiscount;
	
	/*信用卡费用*/ 
	@CustomAnnotation(path = "Body.CreditCardFee")
	private Long creditCardFee;
	
	/*服务费用*/ 
	@CustomAnnotation(path = "Body.ServiceFee")
	private Long serviceFee;
	
	/*缴费类型	 01：缴预存;固定填01*/ 
	@CustomAnnotation(path = "Body.PayedType")
	private String payedType;
	
	/*营销活动编号*/ 
	@CustomAnnotation(path = "Body.ActivityNO")
	private String activityNO;
	
	/*商品上架编码*/ 
	@CustomAnnotation(path = "Body.ProdShelfNO")
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
