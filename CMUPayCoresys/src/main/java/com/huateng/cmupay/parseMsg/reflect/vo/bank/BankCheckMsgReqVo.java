package com.huateng.cmupay.parseMsg.reflect.vo.bank;



import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧银行账号校验请求报文体  
 */
public class BankCheckMsgReqVo {
	
	@CustomAnnotation(path="Body.BankAcctID")
	private String bankAcctID;//用户的银行账户信息
	
	@CustomAnnotation(path="Body.UserName")
	private String userName;//用户姓名
	
	@CustomAnnotation(path="Body.UserIDType")
	private String userIDType;//用户证件类型
	
	@CustomAnnotation(path="Body.UserID")
	private String userID;//用户证件号码

	public String getBankAcctID() {
		return bankAcctID;
	}

	public void setBankAcctID(String bankAcctID) {
		this.bankAcctID = bankAcctID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserIDType() {
		return userIDType;
	}

	public void setUserIDType(String userIDType) {
		this.userIDType = userIDType;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	

	
}
