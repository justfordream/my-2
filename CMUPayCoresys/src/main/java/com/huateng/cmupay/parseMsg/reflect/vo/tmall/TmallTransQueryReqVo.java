package com.huateng.cmupay.parseMsg.reflect.vo.tmall;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author panlg  
 * @version 创建时间：2013-11-23 下午16:39:21 
 * 类说明  浙商天猫全网充值交易状态查询请求报文体
 */
public class TmallTransQueryReqVo {
	/*
	 * 请求类型
	 * 01:通过原交易消息头的ReqTransID查询
	 * 02:通过原充值交易的OrderID查询
	 */
	@CustomAnnotation(path = "Body.QueryType")
	@NotNull(message="019A30:QueryType不能为空")
	@Pattern(regexp="01|02",message="queryType格式不正确")
	private String queryType;
	
	@CustomAnnotation(path = "Body.OriReqSys")
	@NotNull(message="019A30:OriReqSys格式不正确")
	@Pattern(regexp = "[0-9]{4}", message = "OriReqSys参数不正确")
	private String oriReqSys;

	@CustomAnnotation(path = "Body.OriReqDate")
	@NotNull(message="019A31:OriReqDate格式不正确")
	@Pattern(regexp = "[0-9]{8}", message = "OriReqDate参数不正确")
	private String oriReqDate;

	/*
	 * 原业交易请求流水号
	 * QueryType=01，原交易消息头的ReqTransID
	 */
	@CustomAnnotation(path = "Body.OriReqTransID")
//	@NotNull(message="019A32:OriReqTransID不能为空")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,32}", message = "OriReqTransID参数不正确")
	private String oriReqTransID;

	/*
	 * 原订单编号
	 * QueryType=02，填充值交易的OrderID
	 */
	@CustomAnnotation(path = "Body.OriOrderID")
//	@NotNull(message="019A17:OriOrderID不能为空")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,32}", message = "OriOrderID参数不正确")
	private String oriOrderID;

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
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

	public String getOriOrderID() {
		return oriOrderID;
	}

	public void setOriOrderID(String oriOrderID) {
		this.oriOrderID = oriOrderID;
	}
	
}
