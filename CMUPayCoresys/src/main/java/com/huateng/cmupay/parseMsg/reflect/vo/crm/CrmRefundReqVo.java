package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 移动渠道缴费退费请求报文体
 * @author hdm
 *
 */
public class CrmRefundReqVo {
	
	//原交易发起方系统标识
	@CustomAnnotation(path="SvcCont.RefundsReq.OriReqSys")
	@NotNull(message="OriReqSys不能为空")
	@Pattern(regexp = "[0-9]{4}", message = "OriReqSys参数不正确")
	private String OriReqSys;
	//原交易发起方操作请求日期
	@CustomAnnotation(path="SvcCont.RefundsReq.OriActionDate")
	@NotNull(message="OriActionDate不能为空")
	@Pattern(regexp = "([0-9]{4}(0[13578]|10|12)(0[1-9]|[12][0-9]|3[0-1]))"	
			+"|([0-9]{4}(0[469]|11)(0[1-9]|[12][0-9]|30))"
			+"|([0-9]{4}02(0[1-9]|[12][0-9]))", message = "OriActionDate参数不正确")
	private String OriActionDate;
	//原操作流水号 
	@CustomAnnotation(path="SvcCont.RefundsReq.OriTransactionID")
	@NotNull(message="OriTransactionID不能为空")
	@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="OriTransactionID格式不正确")
	private String OriTransactionID;
	//冲正原因
	@CustomAnnotation(path="SvcCont.RefundsReq.RevokeReason")
	@NotNull(message="RevokeReason不能为空")
	@Length(min=1,max=256,message="RevokeReason参数不正确")
//	@Pattern(regexp = "[a-z,A-Z,0-9]{1,256}", message = "RevokeReason参数不正确")
	private String RevokeReason;
	//TODO 操作流水号
	@CustomAnnotation(path="SvcCont.RefundsReq.TransactionID")
	@NotNull(message="OriTransactionID不能为空")
	@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="TransactionID格式不正确")
	private String TransactionID;
	//TODO 交易帐期
	@CustomAnnotation(path="SvcCont.RefundsReq.SettleDate")
	@Pattern(regexp="([0-9]{4}(0[13578]|10|12)(0[1-9]|[12][0-9]|3[0-1]))"	
			+"|([0-9]{4}(0[469]|11)(0[1-9]|[12][0-9]|30))"
			+"|([0-9]{4}02(0[1-9]|[12][0-9]))",message="SettleDate参数不正确")
	private String SettleDate;
	
	public String getOriReqSys() {
		return OriReqSys;
	}
	public void setOriReqSys(String oriReqSys) {
		OriReqSys = oriReqSys;
	}
	public String getOriActionDate() {
		return OriActionDate;
	}
	public void setOriActionDate(String oriActionDate) {
		OriActionDate = oriActionDate;
	}
	public String getOriTransactionID() {
		return OriTransactionID;
	}
	public void setOriTransactionID(String oriTransactionID) {
		OriTransactionID = oriTransactionID;
	}
	public String getTransactionID() {
		return TransactionID;
	}
	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}
	public String getSettleDate() {
		return SettleDate;
	}
	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
	}
	public String getRevokeReason() {
		return RevokeReason;
	}
	public void setRevokeReason(String revokeReason) {
		RevokeReason = revokeReason;
	}
}
