package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧主号签约信息同步应答报文体
 */
public class BankMainMobileBindRespVo {
	
	@CustomAnnotation(path="Body.SubID")
	private String SubID;
	
	@CustomAnnotation(path="Body.SubTime")
	private String SubTime;
	
	@CustomAnnotation(path="Body.BankAcctID")
	private String BankAcctID;
	
	@CustomAnnotation(path="Body.BankAcctType")
	private String BankAcctType;

	public String getSubID() {
		return SubID;
	}

	public void setSubID(String subID) {
		SubID = subID;
	}

	public String getSubTime() {
		return SubTime;
	}

	public void setSubTime(String subTime) {
		SubTime = subTime;
	}

	public String getBankAcctID() {
		return BankAcctID;
	}

	public void setBankAcctID(String bankAcctID) {
		BankAcctID = bankAcctID;
	}

	public String getBankAcctType() {
		return BankAcctType;
	}

	public void setBankAcctType(String bankAcctType) {
		BankAcctType = bankAcctType;
	}
	
	

}
