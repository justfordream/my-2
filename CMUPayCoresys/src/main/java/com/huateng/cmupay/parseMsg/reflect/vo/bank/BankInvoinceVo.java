package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  用于解析发票打印结果通知bean
 */
@XStreamAlias("Invoice")
public class BankInvoinceVo {
	
	@XStreamAlias("InvoiceID")
	@NotNull(message="019A35:InvoiceID格式不正确")
	@Pattern(regexp="[0-9,A-Z,a-z]{0,30}",message="019A35:InvoiceID格式不正确")
	private String invoiceID;
	
	@XStreamAlias("PrintStatus")
	@NotNull(message="019A36:PrintStatus格式不正确")
	@Pattern(regexp="[0-1]",message="019A36:PrintStatus格式不正确")
	private String printStatus;
	
	@XStreamAlias("InvoiceCode")
	//@Pattern(regexp="[0-9,A-Z,a-z]{12}",message="019A37:InvoiceCode格式不正确")
	private String invoiceCode;
	
	@XStreamAlias("InvoiceNo")
	//@Pattern(regexp="[0-9,A-Z,a-z]{8}",message="019A38:InvoiceNo格式不正确")
	private String invoiceNo;
	
	@XStreamAlias("PrintDate")
	@NotNull(message="019A39:PrintDate格式不正确")
	@Pattern(regexp="([0-9]{4}(0[13578]|10|12)(0[1-9]|[12][0-9]|3[0-1]))"	
			+"|([0-9]{4}(0[469]|11)(0[1-9]|[12][0-9]|30))"
			+"|([0-9]{4}02(0[1-9]|[12][0-9]))",message="019A39:PrintDate格式不正确")
	private String printDate;
	
	public String getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}
	public String getPrintStatus() {
		return printStatus;
	}
	public void setPrintStatus(String printStatus) {
		this.printStatus = printStatus;
	}
	public String getInvoiceCode() {
		return invoiceCode;
	}
	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getPrintDate() {
		return printDate;
	}
	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}
	
}
