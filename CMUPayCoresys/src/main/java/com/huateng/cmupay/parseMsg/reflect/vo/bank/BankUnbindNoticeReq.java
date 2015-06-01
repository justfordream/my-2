package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧解约请求报文体
 */
public class BankUnbindNoticeReq {
	//中国移动用户标识类型
	@CustomAnnotation(path = "Body.IDType")
	private String iDType;
	//中国移动用户ID
	@CustomAnnotation(path = "Body.IDValue")
	private String iDValue;
	//签约协议号
	@CustomAnnotation(path = "Body.SubID")
	private String subID;
	public String getiDType() {
		return iDType;
	}
	public void setiDType(String iDType) {
		this.iDType = iDType;
	}
	public String getiDValue() {
		return iDValue;
	}
	public void setiDValue(String iDValue) {
		this.iDValue = iDValue;
	}
	public String getSubID() {
		return subID;
	}
	public void setSubID(String subID) {
		this.subID = subID;
	}
	
}
