package com.huateng.cmupay.parseMsg.reflect.vo.crm;


import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧用户身份信息验证请求报文体
 */
public class CrmCompareUserInfoReqVo {
	
	@CustomAnnotation(path="SvcCont.IdentityCheckReq.IDType")
	private String idType;
	
	@CustomAnnotation(path="SvcCont.IdentityCheckReq.IDValue")
	private String idValue;
	
	@CustomAnnotation(path="SvcCont.IdentityCheckReq.UserIDType")
	private String userIDType;
	
	@CustomAnnotation(path="SvcCont.IdentityCheckReq.UserID")
	private String userID;
	
	@CustomAnnotation(path="SvcCont.IdentityCheckReq.UserName")
	private String userName;

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	


}
