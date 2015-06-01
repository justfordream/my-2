package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

public class MobileShopQueryMsgReqVo extends BaseMsgVo{
	@CustomAnnotation(path = "Body.QueryType")
	@NotNull(message="019A21:QueryType参数不正确")
	@Pattern(regexp = "01|02", message = "019A21:QueryType参数不正确")
	private String QueryType;
	
	@CustomAnnotation(path = "Body.OriReqSys")
	@NotNull(message="019A30:OriReqSys参数不正确")
	@Pattern(regexp = "[0-9]{4}", message = "019A30:OriReqSys参数不正确")
	private String OriReqSys;
	
	@CustomAnnotation(path = "Body.OriReqDate")
	@NotNull(message="019A31:OriReqDate参数不正确")
	@Pattern(regexp = "[0-9]{8}", message = "019A31:OriReqDate参数不正确")
	private String OriReqDate;
	
	@CustomAnnotation(path = "Body.OriReqTransID")
//	@NotNull(message="019A24:OriReqTransID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A32:OriReqTransID参数不正确")
	private String OriReqTransID;
	
	@CustomAnnotation(path = "Body.OriOrderID")
//	@NotNull(message="019A25:OriOrderID参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A25:OriOrderID参数不正确")
	private String OriOrderID;

	public String getQueryType() {
		return QueryType;
	}

	public void setQueryType(String queryType) {
		QueryType = queryType;
	}

	public String getOriReqSys() {
		return OriReqSys;
	}

	public void setOriReqSys(String oriReqSys) {
		OriReqSys = oriReqSys;
	}

	public String getOriReqDate() {
		return OriReqDate;
	}

	public void setOriReqDate(String oriReqDate) {
		OriReqDate = oriReqDate;
	}

	public String getOriReqTransID() {
		return OriReqTransID;
	}

	public void setOriReqTransID(String oriReqTransID) {
		OriReqTransID = oriReqTransID;
	}

	public String getOriOrderID() {
		return OriOrderID;
	}

	public void setOriOrderID(String oriOrderID) {
		OriOrderID = oriOrderID;
	}
	
}
