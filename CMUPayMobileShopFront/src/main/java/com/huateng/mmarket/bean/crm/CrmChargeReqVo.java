package com.huateng.mmarket.bean.crm;

import com.huateng.mmarket.bean.CustomAnnotation;
/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧充值请求报文体
 */
public class CrmChargeReqVo  {


    @CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.IDType")
	private String IDType;
	
	@CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.IDValue")
	private String IDValue;
	
	@CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.TransactionID")
	private String TransactionID;

	@CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.ActionDate")
	private String ActionDate;
	
	@CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.ActionTime")
	private String ActionTime;
	
	@CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.Payed")
	private String Payed;
	
	@CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.BankID")
	private String BankID;
	
	@CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.CnlTyp")
	private String CnlTyp;
	
	@CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.PayedType")
	private String PayedType;
	
	@CustomAnnotation(path="InterBoss.SvcCont.PaymentReq.SettleDate")
	private String SettleDate;
	
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

	public String getPayed() {
		return Payed;
	}

	public void setPayed(String payed) {
		Payed = payed;
	}

	public String getCnlTyp() {
		return CnlTyp;
	}

	public void setCnlTyp(String cnlTyp) {
		CnlTyp = cnlTyp;
	}

	

	public String getPayedType() {
		return PayedType;
	}

	public void setPayedType(String payedType) {
		PayedType = payedType;
	}

	public String getActionTime() {
		return ActionTime;
	}

	public void setActionTime(String actionTime) {
		ActionTime = actionTime;
	}

	public String getBankID() {
		return BankID;
	}

	public void setBankID(String bankID) {
		BankID = bankID;
	}

	public String getSettleDate() {
		return SettleDate;
	}

	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
	}	
}
