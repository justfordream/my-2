package com.huateng.cmupay.parseMsg.reflect.vo.crm;



import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧银行账号校验应答报文体
 */
public class CrmCheckMsgRespVo {
	 
	@CustomAnnotation(path="SvcCont.AuthRsp.RspCode")
	private String rspCode;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.RspInfo")
	private String rspInfo;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.AccountInfo.BankAcctType")
	private String bankAcctType;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.AccountInfo.BankID")
	private String bankID;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.AccountInfo.AuthNum",power='0')
	private Long authNum;
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
	public String getBankAcctType() {
		return bankAcctType;
	}
	public void setBankAcctType(String bankAcctType) {
		this.bankAcctType = bankAcctType;
	}
	public String getBankID() {
		return bankID;
	}
	public void setBankID(String bankID) {
		this.bankID = bankID;
	}
	public Long getAuthNum() {
		return authNum;
	}
	public void setAuthNum(Long authNum) {
		this.authNum = authNum;
	}
	
	
}
