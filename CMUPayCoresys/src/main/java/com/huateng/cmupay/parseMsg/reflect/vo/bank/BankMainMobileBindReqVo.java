package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧主号签约信息同步请求报文体
 */
public class BankMainMobileBindReqVo {
	@Pattern(regexp="[0-9]{2}",message="019A17:IDType参数不正确")
	@NotNull(message="019A17:IDType参数不正确")
	@CustomAnnotation(path="Body.IDType")
	private String IDType;
	@NotNull(message="019A18:IDValue参数不正确")
	@CustomAnnotation(path="Body.IDValue")
//	@Length(max=32,min=11,message="019A18:IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
//	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="019A22:BankAcctID参数不正确")
	@NotNull(message="019A22:BankAcctID参数不正确")
	@CustomAnnotation(path="Body.BankAcctID")
	private String BankAcctID;
	@Length(min=1,max=32,message="019A41:UserName参数不正确")
	@NotNull(message="019A41:UserName参数不正确")
	@CustomAnnotation(path="Body.UserName")
	private String UserName;
	@Pattern(regexp="[0-9]{2}",message="019A42:UserIDType参数不正确")
	@NotNull(message="019A42:UserIDType参数不正确")
	@CustomAnnotation(path="Body.UserIDType")
	private String UserIDType;
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="019A43:UserID参数不正确")
	@NotNull(message="019A43:UserID参数不正确")
	@CustomAnnotation(path="Body.UserID")
	private String UserID;
	@CustomAnnotation(path="Body.SubID",power='0')
	private String SubID;
	@Pattern(regexp="[0-9]{3}",message="019A44:HomeProv参数不正确")
	@NotNull(message="019A44:HomeProv参数不正确")
	@CustomAnnotation(path="Body.HomeProv")
	private String HomeProv;

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

	public String getBankAcctID() {
		return BankAcctID;
	}

	public void setBankAcctID(String bankAcctID) {
		BankAcctID = bankAcctID;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getSubID() {
		return SubID;
	}

	public void setSubID(String subID) {
		SubID = subID;
	}

	public String getUserIDType() {
		return UserIDType;
	}

	public void setUserIDType(String userIDType) {
		UserIDType = userIDType;
	}

	public String getHomeProv() {
		return HomeProv;
	}

	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}
	
	
	

}
