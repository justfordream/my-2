package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 副号码解约响应报文体
 * 
 * @author Gary
 * 
 */
public class CrmSubUnbindResVo {
	/**
	 * 消息返回码(0000-成功；0001-已经签约其他号码； 0002-签约关系不存在；0003-不容许加为副号码； 9999-其它错误)
	 */
	@CustomAnnotation(path = "SvcCont.ReversalRsp.RspCode")
	@Pattern(regexp="[0-9,a-z,A-Z]{4}",message="RspCode格式不正确")
	private String rspCode;
	/**
	 * 消息内容
	 */
	@CustomAnnotation(path = "SvcCont.ReversalRsp.RspInfo")
	@Pattern(regexp=".*{512}",message="RspInfo格式不正确")
	private String rspInfo;
	/**
	 * 原操作流水号(同请求报文的操作流水号)
	 */
	@CustomAnnotation(path = "SvcCont.ReversalRsp.TransactionID")
	@Pattern(regexp="[0-9]{1,32}",message="TransactionID格式不正确")
	private String transactionID;
	/**
	 * 操作请求日期(同请求报文的操作请求日期)
	 */
	@CustomAnnotation(path = "SvcCont.ReversalRsp.ActionDate")
	@Pattern(regexp="[0-9]{8}",message="ActionDate格式不正确")
	private String actionDate;
	/**
	 * 签约协议号(和银行间的签约协议号)
	 */
	@CustomAnnotation(path = "SvcCont.ReversalRsp.SubID")
	@Pattern(regexp="[0-9]{22}",message="SubID格式不正确")
	private String subID;

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public String getRspInfo() {
		return rspInfo;
	}

	public void setRspInfo(String rspInfo) {
		this.rspInfo = rspInfo;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	public String getSubID() {
		return subID;
	}

	public void setSubID(String subID) {
		this.subID = subID;
	}

}
