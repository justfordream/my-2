package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

public class MobileShopQueryMsgResVo extends BaseMsgVo{
	@CustomAnnotation(path = "Body.QueryType")
	@NotNull(message="019A11:QueryType参数不正确")
	@Pattern(regexp = "{2}", message = "019A11:QueryType参数不正确")
	private String QueryType;
	
	@CustomAnnotation(path = "Body.OriRcvDate")
	@NotNull(message="019A47:OriRcvDate参数不正确")
	@Pattern(regexp = "{8}", message = "019A47:OriRcvDate参数不正确")
	private String OriRcvDate;
	
	@CustomAnnotation(path = "Body.OriRcvTransID")
	@NotNull(message="019A48:OriRcvTransID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A48:OriRcvTransID参数不正确")
	private String OriRcvTransID;
	
	@CustomAnnotation(path = "Body.OriOrderID")
	@NotNull(message="019A14:OriOrderID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A14:OriOrderID参数不正确")
	private String OriOrderID;
	
	@CustomAnnotation(path = "Body.OriResultCode")
	@NotNull(message="019A15:OriResultCode参数不正确")
	@Pattern(regexp = "\\w{6}", message = "019A15:OriResultCode参数不正确")
	private String OriResultCode;
	
	@CustomAnnotation(path = "Body.OriResultDesc")
	@NotNull(message="019A16:OriResultDesc参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A16:OriResultDesc参数不正确")
	private String OriResultDesc;
	
	@CustomAnnotation(path = "Body.OriResultTime")
	@NotNull(message="019A17:OriResultTime参数不正确")
	@Pattern(regexp = "{14}", message = "019A17:OriResultTime参数不正确")
	private String OriResultTime;


	public String getQueryType() {
		return QueryType;
	}

	public void setQueryType(String queryType) {
		QueryType = queryType;
	}

	public String getOriRcvDate() {
		return OriRcvDate;
	}

	public void setOriRcvDate(String oriRcvDate) {
		OriRcvDate = oriRcvDate;
	}

	public String getOriRcvTransID() {
		return OriRcvTransID;
	}

	public void setOriRcvTransID(String oriRcvTransID) {
		OriRcvTransID = oriRcvTransID;
	}

	public String getOriOrderID() {
		return OriOrderID;
	}

	public void setOriOrderID(String oriOrderID) {
		OriOrderID = oriOrderID;
	}

	public String getOriResultCode() {
		return OriResultCode;
	}

	public void setOriResultCode(String oriResultCode) {
		OriResultCode = oriResultCode;
	}

	public String getOriResultDesc() {
		return OriResultDesc;
	}

	public void setOriResultDesc(String oriResultDesc) {
		OriResultDesc = oriResultDesc;
	}

	public String getOriResultTime() {
		return OriResultTime;
	}

	public void setOriResultTime(String oriResultTime) {
		OriResultTime = oriResultTime;
	}

}
