package com.huateng.cmupay.parseMsg.reflect.vo.crm;



import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧主号签约信息同步响应报文体
 */
public class CrmMainMobileBindRespVo {
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.RspCode")
	private String rspCode;//二级返回码
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.RspInfo")
	private String rspInfo;//消息内容
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.TransactionID")
	private String transactionID;//原操作流水号  同请求报文的操作流水号
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.ActionDate")
	private String actionDate;//操作请求日期  同请求报文的操作请求日期
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.SubID")
	private String subID;//   和银行间的签约协议号
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.SettleDate")
	private String settleDate;//   和银行间的签约协议号
	
	@CustomAnnotation(path="SvcCont.ReversalRsp.SubTime")
	private String subTime;
	
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
