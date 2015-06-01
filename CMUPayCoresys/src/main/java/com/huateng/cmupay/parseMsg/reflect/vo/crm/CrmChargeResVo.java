package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧充值响应报文体
 */

public class CrmChargeResVo{

	@CustomAnnotation(path="SvcCont.PaymentRsp.IDType")
	@NotNull(message="IDType不能为空")
	@Pattern(regexp="[0-9]{2}",message="IDType格式不正确")
	private String IDType;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.IDValue")
	@NotNull(message="IDValue不能为空")
//	@Length(max=32,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.TransactionID")
	@NotNull(message="TransactionID不能为空")
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="TransactionID格式不正确")
	private String TransactionID;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.ActionDate")
	@NotNull(message="ActionDate不能为空")
	@Pattern(regexp="[0-9]{8}",message="ActionDate格式不正确")
	private String ActionDate;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.UserCat")
	@NotNull(message="UserCat不能为空")
	@Pattern(regexp="[0-9]{1}",message="UserCat格式不正确")
	private String UserCat;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.RspCode")
	@NotNull(message="RspCode不能为空")
	@Pattern(regexp="[0-9,a-z,A-Z]{4}",message="RspCode格式不正确")
	private String RspCode;
	
	@CustomAnnotation(path="SvcCont.PaymentRsp.RspInfo")
	@NotNull(message="RspInfo不能为空")
	@Length(min=1,max=256)
	private String RspInfo;

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

	public String getTransactionID() {
		return TransactionID;
	}

	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}

	public String getActionDate() {
		return ActionDate;
	}

	public void setActionDate(String actionDate) {
		ActionDate = actionDate;
	}

	public String getUserCat() {
		return UserCat;
	}

	public void setUserCat(String userCat) {
		UserCat = userCat;
	}

	public String getRspCode() {
		return RspCode;
	}

	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}

	public String getRspInfo() {
		return RspInfo;
	}

	public void setRspInfo(String rspInfo) {
		RspInfo = rspInfo;
	}

	
	
	
	
	
}
