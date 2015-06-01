package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧银行申请撤单请求报文体
 */
public class BankMsgReqVo {
	//原交易发起方系统标识
	@CustomAnnotation(path = "Body.OriReqSys")
	@NotNull(message="019A30:OriReqSys参数不正确")
	@Pattern(regexp = "[0-9]{4}", message = "019A30:OriReqSys参数不正确")
	private String OriReqSys;
	//原交易发起方操作请求日期
	@CustomAnnotation(path = "Body.OriReqDate")
	@NotNull(message="019A31:OriReqDate参数不正确")
	@Pattern(regexp = "[0-9]{8}", message = "019A31:OriReqDate参数不正确")
	private String OriReqDate;
	//原交易发起方操作流水号
	@CustomAnnotation(path = "Body.OriReqTransID")
	@NotNull(message="019A32:OriReqTransID参数不正确")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,32}", message = "019A32:OriReqTransID参数不正确")
	private String OriReqTransID;
	//冲正原因
	@CustomAnnotation(path = "Body.RevokeReason")
	@NotNull(message="019A33:RevokeReason参数不正确")
	@Length(max=256,message="019A33:RevokeReason参数不正确")
//	@Pattern(regexp = "[a-z,A-Z,0-9]{1,256}", message = "RevokeReason参数不正确")
	private String RevokeReason;
	
	public String getOriReqSys() {
		return OriReqSys;
	}
	public void setOriReqSys(String oriReqSys) {
		OriReqSys = oriReqSys;
	}

	public String getOriReqDate() {
		return OriReqDate;
	}
	public void setOriReqDate(String oriReqDate) {
		OriReqDate = oriReqDate;
	}
	public String getOriReqTransID() {
		return OriReqTransID;
	}
	public void setOriReqTransID(String oriReqTransID) {
		OriReqTransID = oriReqTransID;
	}
	public String getRevokeReason() {
		return RevokeReason;
	}
	public void setRevokeReason(String revokeReason) {
		RevokeReason = revokeReason;
	}
}
