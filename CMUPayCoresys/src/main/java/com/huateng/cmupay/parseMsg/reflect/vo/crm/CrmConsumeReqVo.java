package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧签约缴费请求报文体
 */
public class CrmConsumeReqVo  {


	@CustomAnnotation(path="SvcCont.PaymentReq.IDType")
	@NotNull(message="IDType不能为空")
	@Pattern(regexp="01",message="IDType格式不正确")
	private String IDType;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.IDValue")
	@NotNull(message="IDValue不能为空")
//	@Length(max=32,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.TransactionID")
	@NotNull(message="TransactionID不能为空")
	@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="TransactionID格式不正确")
	private String transactionID;

	@NotNull(message="ActionDate不能为空")
	@Pattern(regexp="[0-9]{8}",message="ActionDate格式不正确")
	@CustomAnnotation(path="SvcCont.PaymentReq.ActionDate")
	private String actionDate;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.Payed")
	@NotNull(message="Payed不能为空")
	@Pattern(regexp="[0-9]{1,9}",message="Payed格式不正确")
	private String payed;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.CnlTyp")
	@NotNull(message="CnlTyp不能为空")
	@Pattern(regexp="00|01|02|03|04|05",message="CnlTyp格式不正确")
	private String cnlTyp;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.SubID")
	@NotNull(message="SubID不能为空")
	@Pattern(regexp="[0-9,A-Z,a-z]{22}",message="SubID格式不正确")
	private String subID;
	
	@CustomAnnotation(path="SvcCont.PaymentReq.PayedType")
	@NotNull(message="PayedType不能为空")
	@Pattern(regexp="[0-9]{2}",message="PayedType格式不正确")
	private String payedType;

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

	

	public String getPayed() {
		return payed;
	}

	public void setPayed(String payed) {
		this.payed = payed;
	}

	public String getCnlTyp() {
		return cnlTyp;
	}
	
	public void setCnlTyp(String cnlTyp) {
		this.cnlTyp = cnlTyp;
	}

	public String getSubID() {
		return subID;
	}

	public void setSubID(String subID) {
		this.subID = subID;
	}

	public String getPayedType() {
		return payedType;
	}

	public void setPayedType(String payedType) {
		this.payedType = payedType;
	}

	public static void main(String[] args) {
		String str="[1-9]\\d{0,4}|[1-4]\\d{5}|500000";
		String num="1";
		System.out.println(num.matches(str));
	}
}
