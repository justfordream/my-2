package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 
 * @author fuyx
 *  CRM预签约请求报文体
 */
public class CrmSignMsgReqVo {
	
	@CustomAnnotation(path="SvcCont.PreContInfoReq.IDType")
	private String IDType;
	@CustomAnnotation(path="SvcCont.PreContInfoReq.IDValue")
	private String IDValue;
	@CustomAnnotation(path="SvcCont.PreContInfoReq.BankID")
	private String BankID;
	
	public String getBankID() {
		return BankID;
	}
	public void setBankID(String bankID) {
		BankID = bankID;
	}
	public String getIDType() {
		return IDType;
	}
	public void setIDType(String iDType) {
		IDType = iDType;
	}
	public String getIDValue() {
		return IDValue;
	}
	public void setIDValue(String iDValue) {
		IDValue = iDValue;
	}

}
