package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧发起的签约结果通知请求报文体
 */
public class BankBindReulstReqVo {
	
	@CustomAnnotation(path="Body.SessionID")
	@NotNull(message="019A19:sessionID格式不正确")
	@Pattern(regexp="[0-9,A-Z,a-z]{20}",message="019A19:sessionID格式不正确")
	private String sessionID;
	
	@CustomAnnotation(path="Body.SubID")
	@NotNull(message="019A20:subID格式不正确")
	@Pattern(regexp="[0-9]{22}",message="019A20:subID格式不正确")
	private String subID;
	
	@CustomAnnotation(path="Body.SubTime")
	@NotNull(message="019A21:subTime格式不正确")
	@Pattern(regexp="(([0-9]{4}(0[13578]|10|12)(0[1-9]|[12][0-9]|3[0-1]))"	
			+"|([0-9]{4}(0[469]|11)(0[1-9]|[12][0-9]|30))"
			+"|([0-9]{4}02(0[1-9]|[12][0-9])))"
			+"(0[0-9]|1[0-9]|2[0-3])([0-5][0-9][0-5][0-9])",message="019A21:subTime格式不正确")
	private String subTime;
	
	@CustomAnnotation(path="Body.BankAcctID",power='0')
	private String bankAcctID;
	
	
	@NotNull(message="019A23:bankAcctType格式不正确")
	@Pattern(regexp="[0-1]",message="019A23:bankAcctType格式不正确")
	@CustomAnnotation(path="Body.BankAcctType")
	private String bankAcctType;
	
	@CustomAnnotation(path="Body.PayType",power='0')
	@Pattern(regexp="[0-1]",message="019A24:payType格式不正确")
	private String payType;
	
	@CustomAnnotation(path="Body.RechThreshold",power='0')
	@Pattern(regexp="[0-9]*",message="019A25:RechThreshold格式不正确")
	private String  rechThreshold;
	
	@CustomAnnotation(path="Body.RechAmount",power='0')
	@Pattern(regexp="[0-9]*",message="019A26:RechAmount格式不正确")
	private String rechAmount;

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getSubID() {
		return subID;
	}

	public void setSubID(String subID) {
		this.subID = subID;
	}

	public String getSubTime() {
		return subTime;
	}

	public void setSubTime(String subTime) {
		this.subTime = subTime;
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

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getRechThreshold() {
		return rechThreshold;
	}

	public void setRechThreshold(String rechThreshold) {
		this.rechThreshold = rechThreshold;
	}

	public String getRechAmount() {
		return rechAmount;
	}

	public void setRechAmount(String rechAmount) {
		this.rechAmount = rechAmount;
	}
}
