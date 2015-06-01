package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧退费请求报文体
 */
public class BankRefundReqVo {

	//原交易发起方系统标识
	@CustomAnnotation(path = "Body.OriReqSys")
	private String OriReqSys;
	//原交易发起方操作请求日期
	@CustomAnnotation(path = "Body.OriReqDate")
	private String OriReqDate;
	//原交易发起方操作流水号
	@CustomAnnotation(path = "Body.OriReqTransID")
	private String OriReqTransID;
	
	//原交易类型,区分消息交易还是文件托收----->1：消息交易,2：文件交易
	@CustomAnnotation(path = "Body.OriTransType")
	private String OriTransType;
	
	//冲正原因
	@CustomAnnotation(path = "Body.RevokeReason")
	private String revokeReason;
	public String getOriReqSys() {
		return OriReqSys;
	}
	public void setOriReqSys(String oriReqSys) {
		OriReqSys = oriReqSys;
	}

	public String getOriReqDate() {
		return OriReqDate;
	}
	public void setOriReqDate(String oriReqDate) {
		OriReqDate = oriReqDate;
	}
	public String getOriReqTransID() {
		return OriReqTransID;
	}
	public void setOriReqTransID(String oriReqTransID) {
		OriReqTransID = oriReqTransID;
	} 

	public String getOriTransType() {
		return OriTransType; 
	}
	public void setOriTransType(String oriTransType) {
		OriTransType = oriTransType;
	}
	public String getRevokeReason() {
		return revokeReason;
	}
	public void setRevokeReason(String revokeReason) {
		this.revokeReason = revokeReason;
	}
	
}
