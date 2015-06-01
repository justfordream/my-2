package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧银行发票信息查询请求报文体
 */
public class BankPrintInvoiceQueryReqVo {
	
	@CustomAnnotation(path="Body.BankProv")
	@NotNull(message="019A34:BankProv参数不正确")
	@Pattern(regexp="[0-9]{3}",message="019A34:BankProv参数不正确")
	private String bankProv; 
	
	@CustomAnnotation(path="Body.OriReqSys")
	@NotNull(message="019A30:OriReqSys参数不正确")
	@Pattern(regexp="[0-9]{4}",message="019A30:OriReqSys参数不正确")
	private String oriReqSys;
	
	@CustomAnnotation(path="Body.OriReqDate")
	@NotNull(message="019A31:OriReqDate参数不正确")
	@Pattern(regexp="[0-9]{8}",message="019A31:OriReqDate参数不正确")
	private String oriReqDate;
	
	@CustomAnnotation(path="Body.OriReqTransID")
	@NotNull(message="019A32:OriReqTransID参数不正确")
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="019A32:OriReqTransID参数不正确")
	private String oriReqTransID;

	public String getBankProv() {
		return bankProv;
	}

	public void setBankProv(String bankProv) {
		this.bankProv = bankProv;
	}

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

	public String getOriReqTransID() {
		return oriReqTransID;
	}

	public void setOriReqTransID(String oriReqTransID) {
		this.oriReqTransID = oriReqTransID;
	}



	
	
}
