package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 副号码签约信息同步请求报文体
 * @author zeng.j
 *
 */
public class CrmSubBindMsgUpdateReqVo {
	//处理方式
	@CustomAnnotation(path = "SvcCont.AuthReq.DealType")
	@NotNull(message="DealType不能为空")
	@Pattern(regexp="[0-9]{2}",message="DealType格式不正确")
	private String dealType;
	//签约协议号
	@CustomAnnotation(path = "SvcCont.AuthReq.SubID")
	@NotNull(message="subID不能为空")
	@Pattern(regexp="[0-9,A-Z,a-z]{22}",message="subID格式不正确")
	private String subID;
	//主号标识类型
	@CustomAnnotation(path = "SvcCont.AuthReq.MainIDType")
	@NotNull(message="MainIDType不能为空")
	@Pattern(regexp="[0-9]{2}",message="MainIDType格式不正确")
	private String mainIDType;
	//主号用户标识编码
	@CustomAnnotation(path = "SvcCont.AuthReq.MainIDValue")
	@NotNull(message="MainIDValue不能为空")
//	@Length(min=1,max=32,message="MainIDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="MainIDValue格式不正确")
	private String mainIDValue;
	//副号用户标识类型
	@CustomAnnotation(path = "SvcCont.AuthReq.IDType")
	@NotNull(message="iDType不能为空")
	@Pattern(regexp="[0-9]{2}",message="iDType格式不正确")
	private String IDType;
	//副号用户标识编码
	@CustomAnnotation(path = "SvcCont.AuthReq.IDValue")
	@NotNull(message="iDValue不能为空")
//	@Length(min=1,max=32,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	//操作流水号
	@CustomAnnotation(path = "SvcCont.AuthReq.TransactionID")
	@NotNull(message="transactionID不能为空")
	@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="TransactionID格式不正确")
	private String transactionID;
	//签约关系生成时间
	@CustomAnnotation(path = "SvcCont.AuthReq.SubTime")
	@NotNull(message="subTime不能为空")
	@Pattern(regexp="[0-9]{14}",message="subTime格式不正确")
	private String subTime;
	//操作请求日期
	@CustomAnnotation(path = "SvcCont.AuthReq.ActionDate")
	@NotNull(message="actionDate不能为空")
	@Pattern(regexp="[0-9]{8}",message="actionDate格式不正确")
	private String actionDate;
	//发起渠道标识
	@CustomAnnotation(path = "SvcCont.AuthReq.CnlTyp")
	@NotNull(message="CnlTyp不能为空")
	@Pattern(regexp="[0-9]{2}",message="CnlTyp格式不正确")
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
