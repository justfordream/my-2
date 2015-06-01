package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

public class MobileShopOpenReqVo extends BaseMsgVo{
	@CustomAnnotation(path = "Body.MerID")
	@NotNull(message="014A04:MerId参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "014A04:MerID参数不正确")
	private String MerID;
	
	@CustomAnnotation(path = "Body.QueryType")
	@NotNull(message="014A04:QueryType参数不正确")
	@Pattern(regexp = "00|01", message = "014A04:QueryType参数不正确")
	private String QueryType;
	@CustomAnnotation(path = "Body.BankAcctID")
	@Pattern(regexp = "\\w{0,32}", message = "019A21:BankAcctID参数不正确")
	private String BankAcctID;
	
	@CustomAnnotation(path = "Body.CustomerInfo")
	@Pattern(regexp = "\\w{0,512}", message = "014A04:CustomerInfo参数不正确")
	private String CustomerInfo;

	public String getMerID() {
		return MerID;
	}

	public void setMerID(String merID) {
		MerID = merID;
	}

	public String getQueryType() {
		return QueryType;
	}

	public void setQueryType(String queryType) {
		QueryType = queryType;
	}

	public String getBankAcctID() {
		return BankAcctID;
	}

	public void setBankAcctID(String bankAcctID) {
		BankAcctID = bankAcctID;
	}

	public String getCustomerInfo() {
		return CustomerInfo;
	}

	public void setCustomerInfo(String customerInfo) {
		CustomerInfo = customerInfo;
	}
}
