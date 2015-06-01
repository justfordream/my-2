package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author fan_kui
 *	银行端发票打印结果通知中 发票打印结果信息bean
 */
@XStreamAlias("Invoice")
public class Invoince {
	
	@XStreamAlias("InvoiceID")
	@NotNull(message="019A35:InvoiceID格式不正确")
	@Pattern(regexp="[0-9,A-Z,a-z]{0,30}",message="019A35:InvoiceID格式不正确")
	private String invoiceID;
	
	@XStreamAlias("PrintStatus")
	@NotNull(message="019A36:PrintStatus格式不正确")
	@Pattern(regexp="[0-9]{1}",message="019A36:PrintStatus格式不正确")
	private String printStatus;
	
	@XStreamAlias("InvoiceCode")
	//@Pattern(regexp="[0-9,A-Z,a-z]{12}",message="019A37:InvoiceCode格式不正确")
	private String invoiceCode;
	
	@XStreamAlias("InvoiceNo")
	//@Pattern(regexp="[0-9,A-Z,a-z]{8}",message="019A38:InvoiceNo格式不正确")
	private String invoiceNo;
	
	@XStreamAlias("PrintDate")
	@NotNull(message="019A39:PrintDate格式不正确")
	@Pattern(regexp="(201[3-9](0[13578]|10|12)(0[1-9]|[12][0-9]|3[0-1]))"	
			+"|(201[3-9](0[469]|11)(0[1-9]|[12][0-9]|30))"
			+"|(201[3-9]02(0[1-9]|[12][0-9]))",message="019A39:PrintDate格式不正确")
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
