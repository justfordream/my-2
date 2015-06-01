package com.huateng.tmall.bean.tmall;




import com.huateng.tmall.bean.CustomAnnotation;

/**
 * @author cmt
 * @version 创建时间：2013-3-8 下午6:12:57 类说明 银行侧银行缴费请求报文体
 */
public class TMallPayReqVo {

	@CustomAnnotation(path = "Body.SubID")
	private String SubID;

	@CustomAnnotation(path = "Body.IDType")
	private String IDType;

	@CustomAnnotation(path = "Body.IDValue")
	private String IDValue;

	@CustomAnnotation(path = "Body.Payed")
	private String Payed;

	@CustomAnnotation(path = "Body.HomeProv")
	private String HomeProv;

	/*订单编号*/
	@CustomAnnotation(path = "Body.OrderID")
//	@Pattern(regexp="[0-9,a-z,A-Z]",message="orderId格式不正确")
	private String orderID;
	
	/*支付流水号*/
	@CustomAnnotation(path = "Body.PayTransID")
	private String payTransID;
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
	
	@CustomAnnotation(path="Body.Reserve1")
	private String reserve1;  //保留字段1
	
	@CustomAnnotation(path="Body.Reserve2")
	private String reserve2;  //保留字段2
	
	@CustomAnnotation(path="Body.Reserve3")
	private String reserve3;  //保留字段3
	
	@CustomAnnotation(path="Body.Reserve4")
	private String reserve4;  //保留字段4
	
	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

	public String getReserve4() {
		return reserve4;
	}

	public void setReserve4(String reserve4) {
		this.reserve4 = reserve4;
	}

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

	public String getSubID() {
		return SubID;
	}

	public void setSubID(String subID) {
		SubID = subID;
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

	public String getPayed() {
		return Payed;
	}

	public void setPayed(String payed) {
		Payed = payed;
	}

	public String getHomeProv() {
		return HomeProv;
	}

	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}

}
