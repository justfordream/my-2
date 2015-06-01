package com.huateng.tmall.bean.crm;

import com.huateng.tmall.bean.CustomAnnotation;



/** 
 * @author panlg
 * @version 创建时间：2013-11-19 上午11:15:57 
 * 类说明  天猫全网浙江发起充值结果通知报文体
 */
public class TmallConsumeResVo {
	/*（天猫）充值请求的发起方交易流水号*/
	@CustomAnnotation(path = "Body.OriReqTransID")
	private String oriReqTransID;
	
	/*原交易日期*/
	@CustomAnnotation(path = "Body.OriReqDate")
	private String oriReqDate;
	
	/*电商的订单编号*/
	/*订单编号*/
	@CustomAnnotation(path = "Body.OrderID")
	private String orderID;
	
	/*充值结果代码*/
	@CustomAnnotation(path = "Body.ResultCode")
	private String resultCode;
	
	/*充值结果描述*/
	@CustomAnnotation(path = "Body.ResultDesc")
	private String resultDesc;
	
	/*省BOSS充值处理时间*/
	@CustomAnnotation(path = "Body.ResultTime")
	private String resultTime;

	public String getOriReqTransID() {
		return oriReqTransID;
	}

	public void setOriReqTransID(String oriReqTransID) {
		this.oriReqTransID = oriReqTransID;
	}

	public String getOriReqDate() {
		return oriReqDate;
	}

	public void setOriReqDate(String oriReqDate) {
		this.oriReqDate = oriReqDate;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public String getResultTime() {
		return resultTime;
	}

	public void setResultTime(String resultTime) {
		this.resultTime = resultTime;
	}
}
