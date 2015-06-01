package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 副号码签约信息响应请求报文体
 * @author zeng.j
 *
 */
public class CrmSubBindMsgUpdateResVo {
	//消息返回码
	@CustomAnnotation(path = "SvcCont.ReversalRsp.RspCode")
	private String rspCode;
	//消息内容
	@CustomAnnotation(path = "SvcCont.ReversalRsp.RspInfo")
	private String rspInfo;
	//原操作流水号
	@CustomAnnotation(path = "SvcCont.ReversalRsp.TransactionID")
	private String transactionID;
	//操作请求日期
	@CustomAnnotation(path = "SvcCont.ReversalRsp.ActionDate")
	private String actionDate;
	//签约协议号
	@CustomAnnotation(path = "SvcCont.ReversalRsp.SubID")
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
