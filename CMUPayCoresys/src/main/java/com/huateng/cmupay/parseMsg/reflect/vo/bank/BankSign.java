package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  用于解析发票的bean
 */
@XStreamAlias("Sign")
public class BankSign {

	@XStreamAlias("SignFlag")
	private String SignFlag;
	@XStreamAlias("CerID")
	private String CerID;
	@XStreamAlias("SignValue")
	private String SignValue;
	public String getSignFlag() {
		return SignFlag;
	}
	public void setSignFlag(String signFlag) {
		SignFlag = signFlag;
	}
	public String getCerID() {
		return CerID;
	}
	public void setCerID(String cerID) {
		CerID = cerID;
	}
	public String getSignValue() {
		return SignValue;
	}
	public void setSignValue(String signValue) {
		SignValue = signValue;
	}
	
	
	
}
