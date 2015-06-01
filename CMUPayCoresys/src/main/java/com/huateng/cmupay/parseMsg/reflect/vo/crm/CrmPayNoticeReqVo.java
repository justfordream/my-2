package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧支付结果通知请求报文体
 */
public class CrmPayNoticeReqVo {
	@CustomAnnotation(path="SvcCont.PaymentReq.RspCode")
	private String rspCode;
	@CustomAnnotation(path="SvcCont.PaymentReq.RspInfo")
	private String rspInfo;
	@CustomAnnotation(path="SvcCont.PaymentReq.OrderID")
	private String orderID;
	@CustomAnnotation(path="SvcCont.PaymentReq.MerVAR")
	private String merVAR;
	@CustomAnnotation(path="SvcCont.PaymentReq.Sig")
	private String sig;
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
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
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
