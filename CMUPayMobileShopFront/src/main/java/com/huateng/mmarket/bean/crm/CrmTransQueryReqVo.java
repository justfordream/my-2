package com.huateng.mmarket.bean.crm;

import com.huateng.mmarket.bean.CustomAnnotation;
/**
 * @author qingxue.li 
 * 类说明 交易结果查询请求报文体
 */
public class CrmTransQueryReqVo {
	@CustomAnnotation(path = "SvcCont.PayStateReq.OriReqSys")
	private String oriReqSys;
	@CustomAnnotation(path = "SvcCont.PayStateReq.OriActionDate")
	private String oriActionDate;
	@CustomAnnotation(path = "SvcCont.PayStateReq.OriTransactionID")
	private String oriTransactionID;
	@CustomAnnotation(path = "SvcCont.PayStateReq.OriActivityCode")
	private String oriActivityCode;

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

	public String getOriActivityCode() {
		return oriActivityCode;
	}

	public void setOriActivityCode(String oriActivityCode) {
		this.oriActivityCode = oriActivityCode;
	}

}
