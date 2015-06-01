package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.thoughtworks.xstream.annotations.XStreamAlias;
/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  用于解析发票的bean
 */
@XStreamAlias("Header")
public class BankHeader {
	
	@XStreamAlias("ActivityCode")
	private String ActivityCode;//交易代码
	

	@XStreamAlias("ReqSys")
	private String ReqSys;//发起方系统
	
	@XStreamAlias("ReqChannel")
    private String ReqChannel;//发起方渠道
	
	@XStreamAlias("ReqDate")
	private String ReqDate;//发起方交易日期
	
	@XStreamAlias("ReqTransID")
	private String ReqTransID;//发起方交易流水号
	
	@XStreamAlias("ReqDateTime")
	private String ReqDateTime;//发起方时间戳
	
	@XStreamAlias("ActionCode")
	private String ActionCode;//交易动作代码

	@XStreamAlias("RcvSys")
	private String RcvSys;//接收方系统
	
	@XStreamAlias("RcvDate")
	private String RcvDate;//接收方交易日期
	
	
	@XStreamAlias("RcvTransID")
	private String RcvTransID;//接收方交易流水号

	@XStreamAlias("RcvDateTime")
	private String RcvDateTime;//接收方时间戳

	@XStreamAlias("RspCode")
	private String RspCode;//应答/错误代码

	@XStreamAlias("RspDesc")
	private String RspDesc;

	public String getActivityCode() {
		return ActivityCode;
	}

	public void setActivityCode(String activityCode) {
		ActivityCode = activityCode;
	}

	public String getReqSys() {
		return ReqSys;
	}

	public void setReqSys(String reqSys) {
		ReqSys = reqSys;
	}

	public String getReqChannel() {
		return ReqChannel;
	}

	public void setReqChannel(String reqChannel) {
		ReqChannel = reqChannel;
	}

	public String getReqDate() {
		return ReqDate;
	}

	public void setReqDate(String reqDate) {
		ReqDate = reqDate;
	}

	public String getReqTransID() {
		return ReqTransID;
	}

	public void setReqTransID(String reqTransID) {
		ReqTransID = reqTransID;
	}

	public String getReqDateTime() {
		return ReqDateTime;
	}

	public void setReqDateTime(String reqDateTime) {
		ReqDateTime = reqDateTime;
	}

	public String getActionCode() {
		return ActionCode;
	}

	public void setActionCode(String actionCode) {
		ActionCode = actionCode;
	}

	public String getRcvSys() {
		return RcvSys;
	}

	public void setRcvSys(String rcvSys) {
		RcvSys = rcvSys;
	}

	public String getRcvDate() {
		return RcvDate;
	}

	public void setRcvDate(String rcvDate) {
		RcvDate = rcvDate;
	}

	public String getRcvTransID() {
		return RcvTransID;
	}

	public void setRcvTransID(String rcvTransID) {
		RcvTransID = rcvTransID;
	}

	public String getRcvDateTime() {
		return RcvDateTime;
	}

	public void setRcvDateTime(String rcvDateTime) {
		RcvDateTime = rcvDateTime;
	}

	public String getRspCode() {
		return RspCode;
	}

	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}

	public String getRspDesc() {
		return RspDesc;
	}

	public void setRspDesc(String rspDesc) {
		RspDesc = rspDesc;
	}

	
}
