package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

public class MobileShopRevokeMsgReqVo extends BaseMsgVo {
	
	//订单编号 
	@CustomAnnotation(path = "Body.OrderId")
	@NotNull(message="019A27:OrderId不能为空")
	@Pattern(regexp = "\\w{0,32}", message = "019A27:OrderId参数不正确")
	private String OrderId;
	
	//原交易发起方标识
	@CustomAnnotation(path = "Body.OriReqSys")
	@NotNull(message="019A30:OrigReqSys不能为空")
	@Pattern(regexp = "\\d{4}", message = "019A30:OrigReqSys参数不正确")
	private String OriReqSys;
	
	//原订单编号
	@CustomAnnotation(path = "Body.OriOrderID")
	@NotNull(message="014A04:OriOrderID不能为空")
	@Pattern(regexp = "\\w{0,32}", message = "014A04:OriOrderID参数不正确")
	private String OriOrderID;
	
	//原交易日期
	@CustomAnnotation(path = "Body.OriReqDate")
	@NotNull(message="019A31:OriReqDate不能为空")
	@Pattern(regexp = "\\d{8}", message = "019A31:OriReqDate参数不正确")
	private String OriReqDate;
	
	//退款金额
	@CustomAnnotation(path = "Body.Payment")
	@Pattern(regexp = "\\d{0,12}", message = "019A51:Payment参数不正确")
	private String Payment;
	
	//退款原因
	@CustomAnnotation(path = "Body.RevokeReason")
	@NotNull(message="019A33:RevokeReason参数不正确")
	private String RevokeReason;
	
	//原交易交易代码
	@CustomAnnotation(path = "Body.OriActivityCode")
	@NotNull(message="019A30:OriActivityCode不能为空")
	@Pattern(regexp = "\\d{6}", message = "019A30:OriActivityCode参数不正确")
	private String OriActivityCode;
	
	//原交易第三方支付机构
	@CustomAnnotation(path = "Body.OriBankID")
	private String OriBankID;
	
	@CustomAnnotation(path = "Body.Reserve1")
	private String Reserve1;
	
	@CustomAnnotation(path = "Body.Reserve2")
	private String Reserve2;
	
	@CustomAnnotation(path = "Body.Reserve3")
	private String Reserve3;
	

	public String getOriActivityCode() {
		return OriActivityCode;
	}

	public void setOriActivityCode(String oriActivityCode) {
		OriActivityCode = oriActivityCode;
	}

	public String getOriBankID() {
		return OriBankID;
	}

	public void setOriBankID(String oriBankID) {
		OriBankID = oriBankID;
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

	public String getRevokeReason() {
		return RevokeReason;
	}

	public void setRevokeReason(String revokeReason) {
		RevokeReason = revokeReason;
	}

	public String getPayment() {
		return Payment;
	}

	public void setPayment(String payment) {
		Payment = payment;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		OrderId = orderId;
	}

	public String getOriReqSys() {
		return OriReqSys;
	}

	public void setOriReqSys(String oriReqSys) {
		OriReqSys = oriReqSys;
	}

	public String getOriOrderID() {
		return OriOrderID;
	}

	public void setOriOrderID(String oriOrderID) {
		OriOrderID = oriOrderID;
	}

	public String getOriReqDate() {
		return OriReqDate;
	}

	public void setOriReqDate(String oriReqDate) {
		OriReqDate = oriReqDate;
	}
	

}
