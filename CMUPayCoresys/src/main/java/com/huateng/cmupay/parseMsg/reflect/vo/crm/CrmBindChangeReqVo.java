package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

public class CrmBindChangeReqVo {
	//01签约 02 解约 03变更
		@CustomAnnotation(path="SvcCont.AuthReq.DealType")
		@NotNull(message="DealType不能为空")
		@Pattern(regexp="03",message="DealType格式不正确")
		private String dealType;//处理方式
		
		@CustomAnnotation(path="SvcCont.AuthReq.SubID")
		@NotNull(message="SubID不能为空")
		@Pattern(regexp="[0-9,a-z,A-Z]{22}",message="SubID格式不正确")
		private String subID;//签约协议号
		
		@NotNull(message="IDType不能为空")
		@CustomAnnotation(path="SvcCont.AuthReq.IDType")
		@Pattern(regexp="01",message="IDType格式不正确")
		private String IDType;//用户标识类型
		
		@CustomAnnotation(path="SvcCont.AuthReq.IDValue")
//		@Length(max=32,message="IDValue格式不正确")
		@NotNull(message="IDValue不能为空")
		@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
		@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
		private String IDValue;//用户标识
		
		@CustomAnnotation(path="SvcCont.AuthReq.UserCat")
		@Pattern(regexp="[0-9]{1}",message="UserCat格式不正确")
		private String userCat;//用户标识

		@CustomAnnotation(path="SvcCont.AuthReq.BankID")
		@NotNull(message="BankID不能为空")
		@Pattern(regexp="[0-9]{4}",message="BankID格式不正确")
		private String bankID;//银行编码
		
		@CustomAnnotation(path="SvcCont.AuthReq.BankAcctType")
		@NotNull(message="BankAcctType不能为空")
		@Pattern(regexp="0|1",message="BankAcctType格式不正确")
		private String bankAcctType;//银行账号类型
		
		@CustomAnnotation(path="SvcCont.AuthReq.BankAcctID")
		@Pattern(regexp="[0-9,a-z,A-Z]{1,32}|(x{12}|x{16})[0-9,a-z,A-z]{4}",message="BankAcctID格式不正确")
		private String bankAcctID;//银行账号
		
		@CustomAnnotation(path="SvcCont.AuthReq.TransactionID")
		@NotNull(message="TransactionID不能为空")
		@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="TransactionID格式不正确")
		private String transactionID;//操作流水号
		
		@CustomAnnotation(path="SvcCont.AuthReq.SubTime")
//		@NotNull(message="SubTime不能为空")
		@Pattern(regexp="[0-9]{14}",message="SubTime格式不正确")
		private String subTime;//签约关系生成时间
		
		@CustomAnnotation(path="SvcCont.AuthReq.ActionDate")
		@NotNull(message="ActionDate不能为空")
		@Pattern(regexp="[0-9]{8}",message="ActionDate格式不正确")
		private String actionDate;//操作请求日期
		
		@CustomAnnotation(path="SvcCont.AuthReq.UserIDType")
		@Pattern(regexp="[0-9]{2}",message="UserIDType格式不正确")
		private String userIDType;//证件类型
		
		@CustomAnnotation(path="SvcCont.AuthReq.UserID")
//		@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="UserID格式不正确")
		private String userID;//证件编码
		
		@CustomAnnotation(path="SvcCont.AuthReq.UserName")
		@Length(max=32,message="UserName格式不正确")
		private String userName;//用户姓名
		
		@CustomAnnotation(path="SvcCont.AuthReq.CnlTyp")
		@NotNull(message="CnlTyp不能为空")
		@Pattern(regexp="[0-9]{2}",message="CnlTyp格式不正确")
		private String cnlTyp;//发起渠道标识
		
		@CustomAnnotation(path="SvcCont.AuthReq.PayType")
		@NotNull(message="PayType不能为空")
		@Pattern(regexp="0|1",message="PayType格式不正确")
		private String payType;//缴费方式
		
		@CustomAnnotation(path="SvcCont.AuthReq.RechAmount")
		private Long rechAmount;//充值金额
		
		@CustomAnnotation(path="SvcCont.AuthReq.RechThreshold")
		private Long rechThreshold;//阈值
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
		public String getBankID() {
			return bankID;
		}
		public void setBankID(String bankID) {
			this.bankID = bankID;
		}
		public String getBankAcctType() {
			return bankAcctType;
		}
		public void setBankAcctType(String bankAcctType) {
			this.bankAcctType = bankAcctType;
		}

		public String getBankAcctID() {
			return bankAcctID;
		}
		public void setBankAcctID(String bankAcctID) {
			this.bankAcctID = bankAcctID;
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
		public String getCnlTyp() {
			return cnlTyp;
		}
		public void setCnlTyp(String cnlTyp) {
			this.cnlTyp = cnlTyp;
		}
		public String getPayType() {
			return payType;
		}
		public void setPayType(String payType) {
			this.payType = payType;
		}
		public Long getRechAmount() {
			return rechAmount;
		}
		public void setRechAmount(Long rechAmount) {
			this.rechAmount = rechAmount;
		}
		public Long getRechThreshold() {
			return rechThreshold;
		}
		public void setRechThreshold(Long rechThreshold) {
			this.rechThreshold = rechThreshold;
		}
		public String getUserCat() {
			return userCat;
		}
		public void setUserCat(String userCat) {
			this.userCat = userCat;
		}
}
