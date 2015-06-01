package com.huateng.tmall.bean.crm;

import com.huateng.tmall.bean.CustomAnnotation;

/**
 * @author qingxue.li 
 * 类说明 移动侧交易信息查询应答报文体
 */
public class CrmTransQueryResVo {
	@CustomAnnotation(path = "SvcCont.PayStateRsp.OriActionDate")
	private String oriActionDate;
	@CustomAnnotation(path = "SvcCont.PayStateRsp.OriTransactionID")
	private String oriTransactionID;
	@CustomAnnotation(path = "SvcCont.PayStateRsp.RspCode")
	private String rspCode;
	@CustomAnnotation(path = "SvcCont.PayStateRsp.RspInfo")
	private String rspInfo;

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

}
