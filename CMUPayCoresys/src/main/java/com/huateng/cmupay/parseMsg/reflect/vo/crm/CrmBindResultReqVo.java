package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧签约结果通知请求报文体
 */
public class CrmBindResultReqVo {
	
	@CustomAnnotation(path="SvcCont.ContResReq.SessionID")
	@NotNull(message="sessionID不能为空")
	@Pattern(regexp="[0-9,A-Z,a-z]{20}",message="sessionID格式不正确")
	private String sessionID;
	
	@CustomAnnotation(path="SvcCont.ContResReq.IDType")
	@NotNull(message="iDType不能为空")
	@Pattern(regexp="[0-9]{2}",message="iDType格式不正确")
	private String IDType;
	
	@CustomAnnotation(path="SvcCont.ContResReq.IDValue")
	@NotNull(message="iDValue不能为空")
//	@Length(max=32,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	
	@CustomAnnotation(path="SvcCont.ContResReq.SubID")
	@NotNull(message="subID不能为空")
	@Pattern(regexp="[0-9,A-Z,a-z]{22}",message="subID格式不正确")
	private String subID;
	
	@CustomAnnotation(path="SvcCont.ContResReq.SubTime")
	@NotNull(message="subTime不能为空")
	@Pattern(regexp="[0-9]{14}",message="subTime格式不正确")
	private String subTime;
	
	@CustomAnnotation(path="SvcCont.ContResReq.TransactionID")
	@NotNull(message="transactionID不能为空")
	@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="TransactionID格式不正确")
	private String transactionID;
	
	@CustomAnnotation(path="SvcCont.ContResReq.ActionDate")
	@NotNull(message="actionDate不能为空")
	@Pattern(regexp="[0-9]{8}",message="actionDate格式不正确")
	private String actionDate;
	//约束？
	@CustomAnnotation(path="SvcCont.ContResReq.BankAcctID")
	private String bankAcctID;
	
	@CustomAnnotation(path="SvcCont.ContResReq.BankAcctType")
	@NotNull(message="bankAcctType不能为空")
	@Pattern(regexp="[0-9]",message="bankAcctType格式不正确")
	private String bankAcctType;
	
	@CustomAnnotation(path="SvcCont.ContResReq.BankID")
	private String bankID;
	@CustomAnnotation(path="SvcCont.ContResReq.PayType")
	@NotNull(message="payType不能为空")
	@Pattern(regexp="[0-9]",message="payType格式不正确")
	private String payType;
	//约束？
	@CustomAnnotation(path="SvcCont.ContResReq.RechThreshold")
	private Long rechThreshold;
	//约束？
	@CustomAnnotation(path="SvcCont.ContResReq.RechAmount")
	private Long rechAmount;
	
	@CustomAnnotation(path="SvcCont.ContResReq.SettleDate")
	private String settleDate;
	
	
	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
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

	public Long getRechThreshold() {
		return rechThreshold;
	}

	public void setRechThreshold(Long rechThreshold) {
		this.rechThreshold = rechThreshold;
	}

	public Long getRechAmount() {
		return rechAmount;
	}

	public void setRechAmount(Long rechAmount) {
		this.rechAmount = rechAmount;
	}

	public String getBankID() {
		return bankID;
	}

	public void setBankID(String bankID) {
		this.bankID = bankID;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	
}
