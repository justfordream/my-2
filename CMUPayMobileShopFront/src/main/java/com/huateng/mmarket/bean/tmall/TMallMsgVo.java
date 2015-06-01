package com.huateng.mmarket.bean.tmall;

import com.huateng.mmarket.bean.CustomAnnotation;
import com.huateng.mmarket.bean.ParentAnnotation;

/**
 * @author cmt
 * @version 创建时间：2013-3-8 下午6:12:57 类说明 银行侧银行消息头内容
 */
public class TMallMsgVo {
	public TMallMsgVo() {
		super();
	}

	/**
	 * 报文头信息
	 */
	@CustomAnnotation(path = "GPay.Header.ActivityCode")
	private String ActivityCode;// 交易代码

	@CustomAnnotation(path = "GPay.Header.ReqSys")
	private String ReqSys;// 发起方系统

	@CustomAnnotation(path = "GPay.Header.ReqChannel")
	private String ReqChannel;// 发起方渠道

	@CustomAnnotation(path = "GPay.Header.ReqDate")
	private String ReqDate;// 发起方交易日期

	@CustomAnnotation(path = "GPay.Header.ReqTransID")
	private String ReqTransID;// 发起方交易流水号

	@CustomAnnotation(path = "GPay.Header.ReqDateTime")
	private String ReqDateTime;// 发起方时间戳

	@CustomAnnotation(path = "GPay.Header.ActionCode")
	private String ActionCode;// 交易动作代码

	@CustomAnnotation(path = "GPay.Header.RcvSys")
	private String RcvSys;// 接收方系统
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RcvDate")
	private String RcvDate;// 接收方交易日期
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RcvTransID")
	private String RcvTransID;// 接收方交易流水号
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RcvDateTime")
	private String RcvDateTime;// 接收方时间戳
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RspCode")
	private String RspCode;// 应答/错误代码
	// 约束 ？
	@CustomAnnotation(path = "GPay.Header.RspDesc")
	private String RspDesc;

	/**
	 * 报文体
	 */
	@ParentAnnotation
	@CustomAnnotation(path = "GPay.Body", power = '2')
	private Object Body; // 特定字段

	/**
	 * 验签字段
	 */
	@ParentAnnotation
	@CustomAnnotation(path = "GPay.Sign", power = '2')
	private Object Sign; // 特定字段

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

	public Object getSign() {
		if (Sign == null || "".equals(Sign)) {
			return "";
		}
		return Sign;
	}

	public void setSign(Object sign) {
		Sign = sign;
	}
}
