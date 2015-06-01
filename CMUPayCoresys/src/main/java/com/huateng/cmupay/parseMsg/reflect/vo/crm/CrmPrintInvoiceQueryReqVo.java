package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧发票信息查询请求报文体
 */
public class CrmPrintInvoiceQueryReqVo {
	
	@CustomAnnotation(path="SvcCont.InvoicesFindReq.TransactionID")
	private  String transactionID;
	
	@CustomAnnotation(path="SvcCont.InvoicesFindReq.IDValue")
	private  String idValue;
	
	@CustomAnnotation(path="SvcCont.InvoicesFindReq.SettleDate")
	private  String settleDate;
	
	@CustomAnnotation(path="SvcCont.InvoicesFindReq.Payed")
	private  Long payed;
	
	@CustomAnnotation(path="SvcCont.InvoicesFindReq.BankID")
	private  String bankID;
	
	@CustomAnnotation(path="SvcCont.InvoicesFindReq.CnlTyp")
	private  String cnlTyp;
	
	@CustomAnnotation(path="SvcCont.InvoicesFindReq.BankProv")
	private  String bankProv;
	
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getIdValue() {
		return idValue;
	}
	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}

	
	public String getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}
	public Long getPayed() {
		return payed;
	}
	public void setPayed(Long payed) {
		this.payed = payed;
	}
	public String getBankID() {
		return bankID;
	}
	public void setBankID(String bankID) {
		this.bankID = bankID;
	}
	public String getCnlTyp() {
		return cnlTyp;
	}
	public void setCnlTyp(String cnlTyp) {
		this.cnlTyp = cnlTyp;
	}
	public String getBankProv() {
		return bankProv;
	}
	public void setBankProv(String bankProv) {
		this.bankProv = bankProv;
	}
	
}
