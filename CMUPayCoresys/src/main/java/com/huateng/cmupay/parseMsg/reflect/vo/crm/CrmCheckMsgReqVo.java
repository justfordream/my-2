package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧银行账号校验请求报文体
 */
public class CrmCheckMsgReqVo {
	
	@CustomAnnotation(path="SvcCont.AuthReq.TransactionID")
	@NotNull(message="TransactionID不能为空")
	@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="TransactionID格式不正确")
	private String transactionID;
	
	@CustomAnnotation(path="SvcCont.AuthReq.ActionDate")
	@NotNull(message="ActionDate不能为空")
	@Pattern(regexp="([0-9]{4}(0[13578]|10|12)(0[1-9]|[12][0-9]|3[0-1]))"	
			+"|([0-9]{4}(0[469]|11)(0[1-9]|[12][0-9]|30))"
			+"|([0-9]{4}02(0[1-9]|[12][0-9]))",message="ActionDate格式不正确")
	private String actionDate;
	
	
	@CustomAnnotation(path="SvcCont.AuthReq.IDType")
	@NotNull(message="IDType不能为空")
	@Pattern(regexp="01",message="IDType格式不正确")
	private String IDType;
	
	@CustomAnnotation(path="SvcCont.AuthReq.IDValue")
	@NotNull(message="IDValue不能为空")
//	@Length(max=32,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	
	@CustomAnnotation(path="SvcCont.AuthReq.UserIDType")
	@NotNull(message="UserIDType不能为空")
	@Pattern(regexp="0[0-7]|99",message="UserIDType格式不正确")
	private String userIDType;
	
	@CustomAnnotation(path="SvcCont.AuthReq.UserID")
	@NotNull(message="UserID不能为空")
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="UserID格式不正确")
	private String userID;
	
	@CustomAnnotation(path="SvcCont.AuthReq.UserName")
	@NotNull(message="UserName不能为空")
	@Length(min=1,max=32,message="UserName格式不正确")
	private String userName;
	
	@CustomAnnotation(path="SvcCont.AuthReq.BankAcctID")
	@NotNull(message="BankAcctID不能为空")
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="BankAcctID格式不正确")
	private String bankAcctID;
	
	@CustomAnnotation(path="SvcCont.AuthReq.BankAcctType")
	@Pattern(regexp="0",message="BankAcctType格式不正确")
	private String bankAcctType;
	
	@CustomAnnotation(path="SvcCont.AuthReq.BankID")
	@NotNull(message="BankID不能为空")
	@Pattern(regexp="000[4-9]|0002|[0-9][0-9][1-9][0-9]",message="BankID格式不正确")
	private String bankID;

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	public String getIDType() {
		return IDType;
	}

	public void setIDType(String iDType) {
		this.IDType = iDType;
	}

	public String getIDValue() {
		return IDValue;
	}

	public void setIDValue(String iDValue) {
		this.IDValue = iDValue;
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

	public String getBankAcctID() {
		return bankAcctID;
	}

	public void setBankAcctID(String bankAcctID) {
		this.bankAcctID = bankAcctID;
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
	
	
	
	
}
