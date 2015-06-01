package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧支付结果通知请求报文体
 */
public class BankChargeResultReqVo {
	
	@CustomAnnotation(path="Body.OrderID")
	@NotNull(message="019A27:OrderID参数不正确")
	@Length(min=1,max=64,message="019A27:OrderID参数不正确")
	private String orderID;
	
	@CustomAnnotation(path="Body.MerVAR")
	@NotNull(message="019A28:MerVAR参数不正确")
	@Length(min=1,max=64,message="019A28:MerVAR参数不正确")
	private String merVar;

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getMerVar() {
		return merVar;
	}

	public void setMerVar(String merVar) {
		this.merVar = merVar;
	}
	
	
	

}
