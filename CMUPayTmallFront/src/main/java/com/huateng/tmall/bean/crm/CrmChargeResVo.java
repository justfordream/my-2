package com.huateng.tmall.bean.crm;

import com.huateng.tmall.bean.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧充值响应报文体
 */

public class CrmChargeResVo{

	@CustomAnnotation(path="SvcCont.PaymentRsp.IDType")
	private String IDType;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.IDValue")
	private String IDValue;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.TransactionID")
	private String TransactionID;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.ActionDate")
	private String ActionDate;
	
/*	@CustomAnnotation(path="SvcCont.PaymentRsp.UserCat")
	private String UserCat;*/
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.RspCode")
	private String RspCode;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.RspInfo")
	private String RspInfo;

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
		return TransactionID;
	}

	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}

	public String getActionDate() {
		return ActionDate;
	}

	public void setActionDate(String actionDate) {
		ActionDate = actionDate;
	}

/*	public String getUserCat() {
		return UserCat;
	}

	public void setUserCat(String userCat) {
		UserCat = userCat;
	}
*/
	public String getRspCode() {
		return RspCode;
	}

	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}

	public String getRspInfo() {
		return RspInfo;
	}

	public void setRspInfo(String rspInfo) {
		RspInfo = rspInfo;
	}

	
	
	
	
	
}
