package com.huateng.third.bean.userCheckbody;

/**
 *  检查用户帐号
 * @author hys
 *
 */
public class Body {
    private String IDType;
    private String IDValue;
    private String UserName;
    private String UserIDType;
    private String UserID;
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
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getUserIDType() {
		return UserIDType;
	}
	public void setUserIDType(String userIDType) {
		UserIDType = userIDType;
	}
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
    
}
