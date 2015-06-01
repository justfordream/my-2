package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧银行支付结果通知请求报文体
 */
public class BankPayNoticeReqVo {
	@NotNull(message="019A27:OrderID参数不正确")
	@CustomAnnotation(path="Body.OrderID")
	@Length(min=1,max=64,message="019A27:OrderID参数长度不正确")
	private String orderID;
	//@NotNull(message="019A12:RspCode参数不正确")
	@CustomAnnotation(path="Body.RspCode")
	@Pattern(message="019A12:RspCode参数不正确", regexp = "[0-9,a-z,A-Z]{1,6}")
	private String rspCode;
	//@NotNull(message="019A13:RspInfo参数不正确")
	@CustomAnnotation(path="Body.RspInfo")
	@Length(min=1,max=256,message="019A13:RspInfo参数不正确")
	private String rspInfo;
	@NotNull(message="019A28:MerVAR参数不正确")
	@CustomAnnotation(path="Body.MerVAR")
	@Length(min=1,max=64,message="019A28:MerVAR参数长度不正确")
	private String merVAR;
	//@NotNull(message="019A16:Sig参数不正确")
	@CustomAnnotation(path="Body.Sig")
	@Length(min=1,max=256,message="019A16:Sig参数不正确")
	private String sig;
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getRspCode() {
		return rspCode;
	}
	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}
	public String getRspInfo() {
		return rspInfo;
	}
	public void setRspInfo(String rspInfo) {
		this.rspInfo = rspInfo;
	}
	public String getMerVAR() {
		return merVAR;
	}
	public void setMerVAR(String merVAR) {
		this.merVAR = merVAR;
	}
	public String getSig() {
		return sig;
	}
	public void setSig(String sig) {
		this.sig = sig;
	}
	
	
	
}
