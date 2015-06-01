package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧签约缴费应答报文体
 */
public class CrmConsumeResVo{

	@CustomAnnotation(path="SvcCont.PaymentRsp.IDType")
	private String IDType;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.IDValue")
	private String IDValue;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.TransactionID")
	private String transactionID;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.ActionDate")
	private String actionDate;
	
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.RspCode")
	private String rspCode;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.RspInfo")
	private String rspInfo;

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
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
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

	
	
	
	
	
}
