package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.ParentAnnotation;

/**
 * @author cmt
 * @version 创建时间：2013-3-8 下午6:12:57 类说明 银行侧银行消息头内容
 */
public class BankMsgVo extends BaseMsgVo {
	public BankMsgVo() {
		super();
	}

	/**
	 * 报文头信息
	 */
	@CustomAnnotation(path = "GPay.Header.ActivityCode")
	@NotNull(message = "019A01:ActivityCode参数不正确")
	@Pattern(regexp = "[0-9]{1,6}", message = "019A01:ActivityCode参数不正确")
	private String ActivityCode;// 交易代码

	@CustomAnnotation(path = "GPay.Header.ReqSys")
	@NotNull(message = "019A02:ReqSys参数不正确")
	@Pattern(regexp = "000[4-9]|0002|[0-9][0-9][1-9][0-9]", message = "019A02:ReqSys参数不正确")
	private String ReqSys;// 发起方系统

	@CustomAnnotation(path = "GPay.Header.ReqChannel")
	@NotNull(message = "019A03:ReqChannel参数不正确")
	@Pattern(regexp = "[0-9]{2}", message = "019A03:ReqChannel参数不正确")
	private String ReqChannel;// 发起方渠道

	@CustomAnnotation(path = "GPay.Header.ReqDate")
	@NotNull(message = "019A04:ReqDate参数不正确")
	@Pattern(regexp = "([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))", message = "019A04:ReqDate参数不正确")
	private String ReqDate;// 发起方交易日期

	@CustomAnnotation(path = "GPay.Header.ReqTransID")
	@NotNull(message = "019A05:ReqTransID参数不正确")
	@Pattern(regexp = "[0-9,a-z,A-Z]{1,32}", message = "019A05:ReqTransID参数不正确")
	private String ReqTransID;// 发起方交易流水号

	@CustomAnnotation(path = "GPay.Header.ReqDateTime")
	@NotNull(message = "019A06:ReqDateTime参数不正确")
	@Pattern(regexp = "([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3})", message = "019A06:ReqDateTime参数不正确")
	private String ReqDateTime;// 发起方时间戳

	@CustomAnnotation(path = "GPay.Header.ActionCode")
	@NotNull(message = "019A07:ActionCode参数不正确")
	@Pattern(regexp = "[0]", message = "019A07:ActionCode参数不正确")
	private String ActionCode;// 交易动作代码

	@CustomAnnotation(path = "GPay.Header.RcvSys")
	@NotNull(message = "019A08:RcvSys参数不正确")
	@Pattern(regexp = "0001", message = "019A08:RcvSys参数不正确")
	private String RcvSys;// 接收方系统
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RcvDate")
	// @Pattern(regexp="[0-9]{8}",message="019A09:RcvDate参数不正确")
	private String RcvDate;// 接收方交易日期
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RcvTransID")
	// @Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="019A10:RcvTransID参数不正确")
	private String RcvTransID;// 接收方交易流水号
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RcvDateTime")
	// @Pattern(regexp="[0-9]{17}",message="019A11:RcvDateTime参数不正确")
	private String RcvDateTime;// 接收方时间戳
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RspCode")
	// @Pattern(regexp="[0-9]{6}",message="019A12:RspCode参数不正确")
	private String RspCode;// 应答/错误代码
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RspDesc")
	// @Length(min=1,max=128,message="019A13:RspDesc参数不正确")
	private String RspDesc;
//	//支付流水号
//	@CustomAnnotation(path = "GPay.Header.PayTransID")
//	private String payTransID;
	
//	public String getPayTransID() {
//		return payTransID;
//	}
//
//	public void setPayTransID(String payTransID) {
//		this.payTransID = payTransID;
//	}

	/**
	 * 报文体
	 */
	@ParentAnnotation
	@CustomAnnotation(path = "GPay.Body", power = '2')
	// @Valid
	private Object Body; // 特定字段

//	/**
//	 * 签名信息
//	 */
//	@CustomAnnotation(path = "GPay.Sign.SignFlag")
//	private String SignFlag;// 报文数字签名标志
//
//	@CustomAnnotation(path = "GPay.Sign.CerID")
//	private String CerID;// 证书标识串
//	// 约束
//	@CustomAnnotation(path = "GPay.Sign.SignValue")
//	private String SignValue;

	public String getActivityCode() {
		return ActivityCode;
	}

	public void setActivityCode(String activityCode) {
		this.ActivityCode = activityCode;
	}

	public String getReqSys() {
		return ReqSys;
	}

	public void setReqSys(String reqSys) {
		this.ReqSys = reqSys;
	}

	public String getReqChannel() {
		return ReqChannel;
	}

	public void setReqChannel(String reqChannel) {
		this.ReqChannel = reqChannel;
	}

	public String getReqDate() {
		return ReqDate;
	}

	public void setReqDate(String reqDate) {
		this.ReqDate = reqDate;
	}

	public String getReqTransID() {
		return ReqTransID;
	}

	public void setReqTransID(String reqTransID) {
		this.ReqTransID = reqTransID;
	}

	public String getReqDateTime() {
		return ReqDateTime;
	}

	public void setReqDateTime(String reqDateTime) {
		this.ReqDateTime = reqDateTime;
	}

	public String getRcvSys() {
		return RcvSys;
	}

	public void setRcvSys(String rcvSys) {
		this.RcvSys = rcvSys;
	}

	public String getRcvDate() {
		return RcvDate;
	}

	public void setRcvDate(String rcvDate) {
		this.RcvDate = rcvDate;
	}

	public String getRcvTransID() {
		return RcvTransID;
	}

	public void setRcvTransID(String rcvTransID) {
		this.RcvTransID = rcvTransID;
	}

	public String getRcvDateTime() {
		return RcvDateTime;
	}

	public void setRcvDateTime(String rcvDateTime) {
		this.RcvDateTime = rcvDateTime;
	}

	public String getRspCode() {
		return RspCode;
	}

	public void setRspCode(String rspCode) {
		this.RspCode = rspCode;
	}

	public String getRspDesc() {
		return RspDesc;
	}

	public void setRspDesc(String rspDesc) {
		this.RspDesc = rspDesc;
	}


	public Object getBody() {
		if (Body == null || "".equals(Body)) {
			return "";
		}
		return Body;
	}

	public void setBody(Object body) {
		this.Body = body;
	}

	public String getActionCode() {
		return ActionCode;
	}

	public void setActionCode(String actionCode) {
		this.ActionCode = actionCode;
	}

}
