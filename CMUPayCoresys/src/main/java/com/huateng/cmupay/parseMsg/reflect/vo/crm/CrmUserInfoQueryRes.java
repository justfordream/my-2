package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;


/**
 * 省Crm端用户应缴费查询交易报文应答
 * @author ning.z
 *
 */
public class CrmUserInfoQueryRes {

	@CustomAnnotation(path="SvcCont.AuthRsp.IDType")
	private String idType;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.IDValue")
	private String idValue;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.UserName")
	private String userName;

	@CustomAnnotation(path="SvcCont.AuthRsp.UserStatus")
	private String userStatus;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.UserCat")
	private String userCat;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.HomeProv")
	private String homeProv;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.Balance")
	private Long balance;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.PayAmount")
	private Long payAmount;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.SignStatus")
	private String signStatus;
	
	@CustomAnnotation(path="SvcCont.AuthRsp.BankID")
	private String bankId;
	
//	@CustomAnnotation(path="SvcCont.AuthRsp.RspCode")
//	private String rspCode;
//	
//	@CustomAnnotation(path="SvcCont.AuthRsp.RspInfo")
//	private String rspInfo;
//
//	
//	
//	
//	public String getRspCode() {
//		return rspCode;
//	}
//
//	public void setRspCode(String rspCode) {
//		this.rspCode = rspCode;
//	}
//
//	public String getRspInfo() {
//		return rspInfo;
//	}
//
//	public void setRspInfo(String rspInfo) {
//		this.rspInfo = rspInfo;
//	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdValue() {
		return idValue;
	}

	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserCat() {
		return userCat;
	}

	public void setUserCat(String userCat) {
		this.userCat = userCat;
	}

	public String getHomeProv() {
		return homeProv;
	}

	public void setHomeProv(String homeProv) {
		this.homeProv = homeProv;
	}



	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public Long getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Long payAmount) {
		this.payAmount = payAmount;
	}

	public String getSignStatus() {
		return signStatus;
	}

	public void setSignStatus(String signStatus) {
		this.signStatus = signStatus;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
