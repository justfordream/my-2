package com.huateng.cmupay.parseMsg.reflect.vo.crm; 


import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧主号解约应答报文体
 */
public class CrmMainMobileUnBindResVo {
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.RspCode")
	private String rspCode ;
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.RspInfo")
	private String rspInfo ;
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.TransactionID")
	private String transactionId;
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.ActionDate")
	private String actionDate;
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.SubID")
	private String subId;
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.SettleDate")
	private String settleDate;
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.SubTime")
	private String subTime;
	
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	public String getSubId() {
		return subId;
	}

	public void setSubId(String subId) {
		this.subId = subId;
	}

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

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getSubTime() {
		return subTime;
	}

	public void setSubTime(String subTime) {
		this.subTime = subTime;
	}



}


