package com.huateng.cmupay.parseMsg.reflect.vo.bank; 

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:28 
 * 类说明  银行侧缴费信息查询请求报文体
 */
public class BankUserCousumeQueryReqVo {
	
	


	@CustomAnnotation(path="Body.OriReqSys")
	@NotNull(message="9999:oriReqSys格式不正确")
	@Pattern(regexp="[0-9,A-Z,a-z]{4}",message="oriReqSys格式不正确")
	private String OriReqSys ;
	
	@CustomAnnotation(path="Body.ActionDate")
	@NotNull(message="9999:ActionDate格式不正确")
	@Pattern(regexp="[0-9]{8}",message="ActionDate格式不正确")
	private String ActionDate ;
	
	@CustomAnnotation(path="Body.TransactionID")
	@NotNull(message="9999:TransactionID格式不正确")
	@Length(min=1,max=32,message="TransactionID格式不正确")
	private String TransactionID;

	public String getOriReqSys() {
		return OriReqSys;
	}

	public void setOriReqSys(String oriReqSys) {
		OriReqSys = oriReqSys;
	}

	public String getActionDate() {
		return ActionDate;
	}

	public void setActionDate(String actionDate) {
		ActionDate = actionDate;
	}

	public String getTransactionID() {
		return TransactionID;
	}

	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}

	
	
	
	

}


