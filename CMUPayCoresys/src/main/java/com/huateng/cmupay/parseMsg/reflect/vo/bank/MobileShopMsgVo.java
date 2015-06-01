package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.ParentAnnotation;

/**
 * @author oul
 * @version 时间 2013/10/10 类说明 商城前置头消息
 * */
public class MobileShopMsgVo extends BaseMsgVo{
	/**
	 * 报文头信息
	 * */
	@CustomAnnotation(path = "GPay.Header.ActivityCode")
	@NotNull(message = "019A01:ActivityCode参数不正确")
	@Pattern(regexp = "[0-9]{1,6}", message = "019A01:ActivityCode参数不正确")
	private String ActivityCode;//交易代码
	
	@CustomAnnotation(path = "GPay.Header.ReqSys")
	@NotNull(message = "019A02:ReqSys参数不正确")
	@Pattern(regexp = "0055", message = "019A02:ReqSys参数不正确")
	private String ReqSys;//发起方系统
	
	@CustomAnnotation(path = "GPay.Header.ReqChannel")
	@NotNull(message = "019A03:ReqChannel参数不正确")
	@Pattern(regexp = "\\d{2}", message = "019A03:ReqChannel参数不正确")
	private String ReqChannel;//发起方渠道
	
	@CustomAnnotation(path = "GPay.Header.ReqDate")
	@NotNull(message = "019A04:ReqDate参数不正确")
	@Pattern(regexp = "[0-9]{8}", message = "019A04:ReqDate参数不正确")
	private String ReqDate;//发起方交易日期
	
	@CustomAnnotation(path = "GPay.Header.ReqTransID")
	@NotNull(message = "019A05:ReqTransID参数不正确")
	@Pattern(regexp = "[0-9,a-z,A-Z]{1,32}", message = "019A05:ReqTransID参数不正确")
	private String ReqTransID;//发起方交易流水号
	
	@CustomAnnotation(path = "GPay.Header.ReqDateTime")
	@NotNull(message = "019A06:ReqDateTime参数不正确")
	@Pattern(regexp = "[0-9]{17}", message = "019A06:ReqDateTime参数不正确")
	private String ReqDateTime;//发起方时间戳
	
	@CustomAnnotation(path = "GPay.Header.ActionCode")
	@NotNull(message = "019A07:ActionCode参数不正确")
	@Pattern(regexp = "[0]", message = "019A07:ActionCode参数不正确")
	private String ActionCode;//交易动作代码
	
	@CustomAnnotation(path = "GPay.Header.RcvSys")
	@NotNull(message = "019A08:RcvSys参数不正确")
	@Pattern(regexp = "0001", message = "019A08:RcvSys参数不正确")
	private String RcvSys;//接收方系统
	
	@CustomAnnotation(path = "GPay.Header.RcvDate")
	@Pattern(regexp = "[0-9]{8}", message = "019A09:RcvDate参数不正确")
	private String RcvDate;//接收方交易日期
	
	@CustomAnnotation(path = "GPay.Header.RcvTransID")
	@Pattern(regexp = "\\w{1,32}", message = "019A10:RcvTransID参数不正确")
	private String RcvTransID;//接收方交易流水
	
	@CustomAnnotation(path = "GPay.Header.RcvDateTime")
	@Pattern(regexp = "[0-9]{17}", message = "019A11:RcvDateTime参数不正确")
	private String RcvDateTime;//接收方交易时间戳
	
	@CustomAnnotation(path = "GPay.Header.RspCode")
	@Pattern(regexp = "\\w{6}", message = "019A12:RspCode参数不正确")
	private String RspCode;//应答/错误代码
	
	@CustomAnnotation(path = "GPay.Header.RspDesc")
	@Pattern(regexp = "\\w{1,128}", message = "019A13:RspDesc参数不正确")
	private String RspDesc;
	
	/**
	 * 报文体
	 * */
	@ParentAnnotation
	@CustomAnnotation(path = "GPay.Body", power = '2')
	private Object Body;
	
	/**
	 * 签名信息
	 * */
	@CustomAnnotation(path = "GPay.Sign.SignFlag")
	private String SignFlag;// 报文数字签名标志

	@CustomAnnotation(path = "GPay.Sign.CerID")
	private String CerID;// 证书标识串

	@CustomAnnotation(path = "GPay.Sign.SignValue")
	private String SignValue;

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

	public Object getBody() {
		if (Body == null || "".equals(Body)) {
			return "";
		}
		return Body;
	}

	public void setBody(Object Body) {
		this.Body = Body;
	}

	public String getSignFlag() {
		return SignFlag;
	}

	public void setSignFlag(String signFlag) {
		SignFlag = signFlag;
	}

	public String getCerID() {
		return CerID;
	}

	public void setCerID(String cerID) {
		CerID = cerID;
	}

	public String getSignValue() {
		return SignValue;
	}

	public void setSignValue(String signValue) {
		SignValue = signValue;
	}
	
}
