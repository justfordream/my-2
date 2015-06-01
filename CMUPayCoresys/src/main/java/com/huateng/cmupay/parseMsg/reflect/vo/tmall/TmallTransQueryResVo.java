package com.huateng.cmupay.parseMsg.reflect.vo.tmall;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author panlg 
 * @version 创建时间：2013-11-23 下午16:55:32 
 * 类说明  浙商天猫全网充值交易状态查询应答报文体
 */
public class TmallTransQueryResVo {
	/*
	 * 请求类型
	 * 01:通过原交易消息头的ReqTransID查询
	 * 02:通过原充值交易的OrderID查询
	 */
	@CustomAnnotation(path = "Body.QueryType")
//	@NotNull(message="019A30:QueryType不能为空")
	@Pattern(regexp="01|02",message="QueryType格式不正确")
	private String queryType;
	
	/*被查询交易处理方交易日期*/
	@CustomAnnotation(path = "Body.OriRcvDate")
	@Pattern(regexp = "[0-9]{8}", message = "OriRcvDate参数不正确")
	private String oriRcvDate;
	
	/*
	 * 被查询交易处理方交易流水号
	 * QueryType=01时填写
	 */
	@CustomAnnotation(path = "Body.OriRcvTransID")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,32}", message = "OriRcvTransID参数不正确")
	private String oriRcvTransID;
	
	/*
	 * 原订单编号
	 * QueryType=02时填写
	 */
	@CustomAnnotation(path = "Body.OriOrderID")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,32}", message = "OriOrderID参数不正确")
	private String oriOrderID;
	
	/*被查询交易应答/错误代码*/
	@CustomAnnotation(path = "Body.OriResultCode")
	@Pattern(regexp = "[a-z,A-Z,0-9]{6}", message = "OriResultCode参数不正确")
	private String oriResultCode;
	
	/*原应答/错误描述*/
	@CustomAnnotation(path = "Body.OriResultDesc")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,256}", message = "OriResultDesc参数不正确")
	private String oriResultDesc;
	
	/*原省BOSS充值处理时间*/
	@CustomAnnotation(path = "Body.OriResultTime")
	@Pattern(regexp = "[0-9]{14}", message = "OriResultTime参数不正确")
	private String oriResultTime;

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getOriRcvDate() {
		return oriRcvDate;
	}

	public void setOriRcvDate(String oriRcvDate) {
		this.oriRcvDate = oriRcvDate;
	}

	public String getOriRcvTransID() {
		return oriRcvTransID;
	}

	public void setOriRcvTransID(String oriRcvTransID) {
		this.oriRcvTransID = oriRcvTransID;
	}

	public String getOriOrderID() {
		return oriOrderID;
	}

	public void setOriOrderID(String oriOrderID) {
		this.oriOrderID = oriOrderID;
	}

	public String getOriResultCode() {
		return oriResultCode;
	}

	public void setOriResultCode(String oriResultCode) {
		this.oriResultCode = oriResultCode;
	}

	public String getOriResultDesc() {
		return oriResultDesc;
	}

	public void setOriResultDesc(String oriResultDesc) {
		this.oriResultDesc = oriResultDesc;
	}

	public String getOriResultTime() {
		return oriResultTime;
	}

	public void setOriResultTime(String oriResultTime) {
		this.oriResultTime = oriResultTime;
	}
	
}
