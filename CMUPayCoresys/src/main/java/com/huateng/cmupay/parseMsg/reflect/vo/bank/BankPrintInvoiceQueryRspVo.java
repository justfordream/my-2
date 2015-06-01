package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import java.util.List;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧发票信息查询应答报文体
 */
@XStreamAlias("Body")
public class BankPrintInvoiceQueryRspVo{
	
	@XStreamAlias("PrintStatus")
	private String PrintStatus;
	@XStreamImplicit(itemFieldName="Invoice")
	private List<Invoice> Invoice;
	public String getPrintStatus() {
		return PrintStatus;
	}
	public void setPrintStatus(String printStatus) {
		this.PrintStatus = printStatus;
	}
	public List<Invoice> getListInvoices() {
		return Invoice;
	}
	public void setListInvoices(List<Invoice> listInvoices) {
		this.Invoice = listInvoices;
	}
	
}
@XStreamAlias("Invoice")
class Invoice{
	@XStreamAlias("InvoiceID")
	private String InvoiceID;
	@XStreamImplicit(itemFieldName="InvoiceItem") 
	private List<InvoiceItem> InvoiceItem;
	public String getInvoiceID() {
		return InvoiceID;
	}
	public void setInvoiceID(String invoiceID) {
		this.InvoiceID = invoiceID;
	}
	public List<InvoiceItem> getList() {
		return InvoiceItem;
	}
	public void setList(List<InvoiceItem> list) {
		this.InvoiceItem = list;
	}
}

@XStreamAlias("InvoiceItem")
class InvoiceItem{
	@XStreamAlias("ItemName") 
	private String ItemName;
	@XStreamAlias("ItemValue") 
	private String ItemValue;
	public String getItemName() {
		return ItemName;
	}
	public void setItemName(String itemName) {
		this.ItemName = itemName;
	}
	public String getItemValue() {
		return ItemValue;
	}
	public void setItemValue(String itemValue) {
		this.ItemValue = itemValue;
	}
	
}