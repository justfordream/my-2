package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧手机用户身份验证请求报文体
 */
public class BankCompareUserInfoReqVo {
	
	@CustomAnnotation(path="Body.IDType")
	@NotNull(message="019A17:IDType参数不正确")
	@Pattern(regexp="01",message="019A17:IDType参数不正确")
	private String IDType;
	
	@CustomAnnotation(path="Body.IDValue")
	@NotNull(message="019A18:IDValue参数不正确")
//	@Length(max=32,min=11,message="019A18:IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="019A18:IDValue格式不正确")
//	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	
	@CustomAnnotation(path="Body.UserName")
	@NotNull(message="019A41:UserName参数不正确")
	@Length(min=1,max=32,message="019A41:UserName参数不正确")
	private String userName;
	
	@CustomAnnotation(path="Body.UserIDType")
	@NotNull(message="019A42:UserIDType参数不正确")
	@Pattern(regexp="0[0-7]|99",message="019A42:UserIDType参数不正确")
	private String userIDType;
	
	@CustomAnnotation(path="Body.UserID")
	@NotNull(message="019A43:UserID参数不正确")
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="019A43:UserID参数不正确")
	private String userID;

	public String getIDType() {
		return IDType;
	}

	public void setIDType(String idType) {
		this.IDType = idType;
	}

	public String getIDValue() {
		return IDValue;
	}

	public void setIDValue(String idValue) {
		this.IDValue = idValue;
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
