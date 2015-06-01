package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * crm撤单响应报文体
 * @author hdm
 *
 */
public class CrmRevokeMsgRspVo {

	@CustomAnnotation(path="SvcCont.RefundsRsp.OriReqSys")
	private String oriReqSys;
	@CustomAnnotation(path="SvcCont.RefundsRsp.OriActionDate")
	private String oriActionDate;
	@CustomAnnotation(path="SvcCont.RefundsRsp.OriTransactionID")
	private String oriTransactionID;
	@CustomAnnotation(path="SvcCont.RefundsRsp.RspCode")
	private String rspCode;
	@CustomAnnotation(path="SvcCont.RefundsRsp.RspInfo")
	private String rspInfo;
	@CustomAnnotation(path="SvcCont.RefundsRsp.TransactionID")
	private String transactionID;
	@CustomAnnotation(path="SvcCont.RefundsRsp.SettleDate")
	private String settleDate;
	
	public String getOriReqSys() {
		return oriReqSys;
	}
	public void setOriReqSys(String oriReqSys) {
		this.oriReqSys = oriReqSys;
	}
	public String getOriActionDate() {
		return oriActionDate;
	}
	public void setOriActionDate(String oriActionDate) {
		this.oriActionDate = oriActionDate;
	}
	public String getOriTransactionID() {
		return oriTransactionID;
	}
	public void setOriTransactionID(String oriTransactionID) {
		this.oriTransactionID = oriTransactionID;
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
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}
	
}
