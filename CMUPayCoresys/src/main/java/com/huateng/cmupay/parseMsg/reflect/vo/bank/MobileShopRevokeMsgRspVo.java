package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

public class MobileShopRevokeMsgRspVo extends BaseMsgVo {
	
	//订单编号 
	@CustomAnnotation(path = "Body.OrderID")
	@NotNull(message="019A27:OrderID不能为空")
	@Pattern(regexp = "\\w{32}", message = "019A27:OrderID参数不正确")
	private String OrderID;
	
	//原交易发起方标识
	@CustomAnnotation(path = "Body.OriReqSys")
	@NotNull(message="019A30:OriReqSys不能为空")
	@Pattern(regexp = "\\d{4}", message = "019A30:OriReqSys参数不正确")
	private String OriReqSys;
	
	//原订单编号
	@CustomAnnotation(path = "Body.OriOrderID")
	@NotNull(message="014A04:请求报文数据错误")
	@Pattern(regexp = "\\w{32}", message = "014A04:请求报文数据错误")
	private String OriOrderID;
	
	//退单交易的帐期日
	@CustomAnnotation(path = "Body.SettleDate")
	@Pattern(regexp = "\\d{8}", message = "014A04:请求报文数据错误")
	private String SettleDate;

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
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

	public String getSettleDate() {
		return SettleDate;
	}

	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
	}


}
