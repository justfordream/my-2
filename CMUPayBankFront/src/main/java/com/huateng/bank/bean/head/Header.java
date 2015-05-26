package com.huateng.bank.bean.head;

import org.apache.commons.lang3.StringUtils;

/**
 * 报文头
 * 
 * @author Gary
 * 
 */
public class Header {
	/**
	 * 交易代码(具体取值参见附录)
	 */
	private String ActivityCode;
	/**
	 * 发起方系统(具体取值参见附录)
	 */
	private String ReqSys;
	/**
	 * 发起方渠道(具体取值参见附录)
	 */
	private String ReqChannel;
	/**
	 * 发起方交易日期(取值格式为YYYYMMDD。发起方交易日期（按照发起方的对账日，有可能与发起方时间戳中的日期不一致）)
	 */
	private String ReqDate;
	/**
	 * 发起方交易流水号(发起方确保该字段的当日唯一性，任何交易均由“发起方系统”、“发起方交易日期”和“发起方交易流水号”唯一确定)
	 */
	private String ReqTransID;
	/**
	 * 发起方时间戳(发起方发起交易的具体时间戳（基于发起方系统时间），格式为YYYYMMDDHHMMSSmmm)
	 */
	private String ReqDateTime;
	/**
	 * 交易动作代码(0：请求，1：应答)
	 */
	private String ActionCode;
	/**
	 * 接收方系统(具体取值参见附录)
	 */
	private String RcvSys;
	/**
	 * 接收方交易日期(接收方处理请求的日期（按照接收方的对账日，有可能与接收方时间戳中的日期不一致），格式为YYYYMMD； 交易动作代码为1时必填 )
	 */
	private String RcvDate;
	/**
	 * 接收方交易流水号(交易动作代码为1时必填)
	 */
	private String RcvTransID;
	/**
	 * 接收方时间戳(接收方处理请求的时间（基于接收方系统时间），格式为YYYYMMDDHHMMSSmmm； 交易动作代码为1时必填 )
	 */
	private String RcvDateTime;
	/**
	 * 应答/错误代码(具体取值参见附录； 交易动作代码为1时必填 )
	 */
	private String RspCode;
	/**
	 * 应答/错误描述(交易动作代码为1时必填)
	 */
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

	public String plainText() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<ActivityCode>" + ActivityCode + "</ActivityCode>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<ReqSys>" + ReqSys + "</ReqSys/>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<ReqChannel>" + ReqChannel + "</ReqChannel>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<ReqDate>" + ReqDate + "</ReqDate>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<ReqTransID>" + ReqTransID + "</ReqTransID>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<ReqDateTime>" + ReqDateTime + "</ReqDateTime>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<ActionCode>" + ActionCode + "</ActionCode>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<RcvSys>" + RcvSys + "</RcvSys>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<RcvDate>" + RcvDate + "</RcvDate>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<RcvTransID>" + RcvTransID + "</RcvTransID>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<RcvDateTime>" + RcvDateTime + "</RcvDateTime>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<RspCode>" + RspCode + "</RspCode>");
		}
		if (StringUtils.isNotBlank(ActivityCode)) {
			sb.append("<RspDesc>" + RspDesc + "</RspDesc>");
		}
		return sb.toString();
	}

}
