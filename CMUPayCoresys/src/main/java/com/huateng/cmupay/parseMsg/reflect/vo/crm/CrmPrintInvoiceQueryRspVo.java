package com.huateng.cmupay.parseMsg.reflect.vo.crm;


import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧发票信息查询应答报文体
 */
@XStreamAlias("InvoicesFindRsp") 
public class CrmPrintInvoiceQueryRspVo {
	
	@XStreamAlias("RspCode")
	private  String RspCode;
	@XStreamAlias("RspInfo")
	private  String RspInfo;
	@XStreamAlias("HomeProv")
	private  String HomeProv;	
	@XStreamImplicit(itemFieldName="Invoice")
	private List<Invoice> Invoice;
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
	public String getHomeProv() {
		return HomeProv;
	}
	public void setHomeProv(String homeProv) {
		this.HomeProv = homeProv;
	}
	public List<Invoice> getListInvoices() {
		return Invoice;
	}
	public void setListInvoices(List<Invoice> listInvoices) {
		this.Invoice = listInvoices;
	}
	
	@XStreamAlias("Invoice")
	public class Invoice{
		@XStreamAlias("InvoiceID")
		private String InvoiceID;
		@XStreamAlias("AcctDate")
		private String AcctDate;
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
		public String getAcctDate() {
			return AcctDate;
		}
		public void setAcctDate(String acctDate) {
			AcctDate = acctDate;
		}

		
		
	}
	
	@XStreamAlias("InvoiceItem")
	public class InvoiceItem{
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
	
	
}



