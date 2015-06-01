package com.huateng.cmupay.parseMsg.reflect.vo.crm;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧发票打印请求报文体
 */
public class CrmPrintInvoinceReqVo {
	@CustomAnnotation(path="SvcCont.InvoicesPrintReq.TransactionID")
	private String transactionID;
	@CustomAnnotation(path="SvcCont.InvoicesPrintReq.IDValue")
	private String idValue;
	@CustomAnnotation(path="SvcCont.InvoicesPrintReq.SettleDate")
	private String settleDate;
	@CustomAnnotation(path="SvcCont.InvoicesPrintReq.Payed")
	private Long payed;
	@CustomAnnotation(path="SvcCont.InvoicesPrintReq.BankID")
	private String bankID;
	@CustomAnnotation(path="SvcCont.InvoicesPrintReq.InvoiceID")
	private String invoiceID;
	@CustomAnnotation(path="SvcCont.InvoicesPrintReq.PrintDate")
	private String printDate;
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
	public String getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}
	public String getPrintDate() {
		return printDate;
	}
	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}
	
	
	
}
