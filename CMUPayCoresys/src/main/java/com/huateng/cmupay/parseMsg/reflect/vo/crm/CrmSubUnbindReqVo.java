package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 副号码解约请求报文体
 * 
 * @author Gary
 * 
 */
public class CrmSubUnbindReqVo {//TODO 添加验证规则
	/**
	 * 处理方式(01：签约；02：解约)
	 */
	@CustomAnnotation(path = "SvcCont.AuthReq.DealType")
	@Pattern(regexp="02",message="DealType格式不正确")
	@NotNull(message = "DealType不能为空")
	private String dealType;
	/**
	 * 签约协议号(主号签约协议号)
	 */
	@CustomAnnotation(path = "SvcCont.AuthReq.SubID")
	@Pattern(regexp="[0-9,A-Z,a-z]{22}",message="subID格式不正确")
	@NotNull(message = "SubId不能为空")
	private String subID;
	/**
	 * 主号标识类型(见附录：用户标识类型)
	 */
	@NotNull(message = "MainIDType不能为空")
	@CustomAnnotation(path = "SvcCont.AuthReq.MainIDType")
	@Pattern(regexp="01|02|03|04",message="MainIDType格式不正确")
	private String mainIDType;
	/**
	 * 主号用户标识编码
	 */
	@NotNull(message = "MainIDValue不能为空")
	@CustomAnnotation(path = "SvcCont.AuthReq.MainIDValue")
//	@Pattern(regexp="[0-9]{1,32}",message="MainIDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="MainIDValue格式不正确")
	private String mainIDValue;
	/**
	 * 副号用户标识类型(见附录：用户标识类型)
	 */
	@CustomAnnotation(path = "SvcCont.AuthReq.IDType")
	@Pattern(regexp="01|02|03|04",message="IDType格式不正确")
	@NotNull(message = "IDType不能为空")
	private String IDType;
	/**
	 * 副号用户标识编码
	 */
	@CustomAnnotation(path = "SvcCont.AuthReq.IDValue")
//	@Length(max=32,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
	@NotNull(message = "IDValue不能为空")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	/**
	 * 操作流水号(TransactionID组成：3位省编码+17位精确到毫秒时间+10位流水。流水号从0000000001开始，步长为1.)
	 */
	@CustomAnnotation(path = "SvcCont.AuthReq.TransactionID")
	@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="TransactionID格式不正确")
	@NotNull(message = "TransactionID不能为空")
	private String transactionID;
	/**
	 * 签约关系生成时间(格式为YYYYMMDDHH24MISS)
	 */
	@CustomAnnotation(path = "SvcCont.AuthReq.SubTime")
	@Pattern(regexp="[0-9]{14}",message="TransactionID格式不正确")
	@NotNull(message = "TransactionID不能为空")
	private String subTime;
	/**
	 * 操作请求日期(格式为YYYYMMDD)
	 */
	@CustomAnnotation(path = "SvcCont.AuthReq.ActionDate")
	@Pattern(regexp="[0-9]{8}",message="ActionDate格式不正确")
	@NotNull(message = "ActionDate不能为空")
	private String actionDate;
	/**
	 * 发起渠道标识(见附录渠道标识)
	 */
	@CustomAnnotation(path = "SvcCont.AuthReq.CnlTyp")
	@Pattern(regexp="00|01|02|03|04|05",message="CnlTyp格式不正确")
	@NotNull( message = "CnlTyp不能为空")
	private String cnlTyp;
	

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getSubID() {
		return subID;
	}

	public void setSubID(String subID) {
		this.subID = subID;
	}

	public String getMainIDType() {
		return mainIDType;
	}

	public void setMainIDType(String mainIDType) {
		this.mainIDType = mainIDType;
	}

	public String getMainIDValue() {
		return mainIDValue;
	}

	public void setMainIDValue(String mainIDValue) {
		this.mainIDValue = mainIDValue;
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

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getSubTime() {
		return subTime;
	}

	public void setSubTime(String subTime) {
		this.subTime = subTime;
	}

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	public String getCnlTyp() {
		return cnlTyp;
	}

	public void setCnlTyp(String cnlTyp) {
		this.cnlTyp = cnlTyp;
	}

}
