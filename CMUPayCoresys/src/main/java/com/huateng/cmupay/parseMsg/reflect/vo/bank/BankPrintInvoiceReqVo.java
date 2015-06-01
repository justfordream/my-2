package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧发票结果通知请求报文体
 */
@XStreamAlias("Body") 
public class BankPrintInvoiceReqVo {
	@XStreamAlias("OriReqSys")
	@NotNull(message="029A30:OrigReqSys参数不正确")
	@Pattern(regexp="[0-9]{4}",message="029A30:OrigReqSys参数不正确")
	private String oriReqSys;
	@XStreamAlias("OriReqDate")
	@NotNull(message="029A31:OriReqDate参数不正确")
	@Pattern(regexp="[0-9]{8}",message="029A31:OriReqDate参数不正确")
	private String oriReqDate;
	@XStreamAlias("OriReqTransID")
	@NotNull(message="029A32:OriReqTransID参数不正确")
	@Pattern(regexp="[A-Z,0-9,a-z]{1,32}",message="029A32:OriReqTransID参数不正确")
	private String transactionID;
	
	@XStreamImplicit(itemFieldName="Invoice")
	private List<BankInvoinceVo> listBankInvoince;

	public String getOriReqSys() {
		return oriReqSys;
	}

	public void setOriReqSys(String oriReqSys) {
		this.oriReqSys = oriReqSys;
	}



	public String getOriReqDate() {
		return oriReqDate;
	}

	public void setOriReqDate(String oriReqDate) {
		this.oriReqDate = oriReqDate;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public List<BankInvoinceVo> getListBankInvoince() {
		return listBankInvoince;
	}

	public void setListBankInvoince(List<BankInvoinceVo> listBankInvoince) {
		this.listBankInvoince = listBankInvoince;
	}
	
	
	
}
