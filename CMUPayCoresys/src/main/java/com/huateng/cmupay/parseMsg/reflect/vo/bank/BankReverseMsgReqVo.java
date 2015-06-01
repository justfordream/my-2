package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧冲正请求报文体
 */
public class BankReverseMsgReqVo {

	//原交易发起方系统标识
	@CustomAnnotation(path = "Body.OriReqSys")
	@NotNull(message="019A30:OriReqSys参数不正确")
	@Pattern(regexp = "[0-9]{4}", message = "019A30:OriReqSys参数不正确")
	private String oriReqSys;
	//原交易发起方操作请求日期
	@CustomAnnotation(path = "Body.OriReqDate")
	@NotNull(message="019A31:OriReqDate参数不正确")
	@Pattern(regexp = "[0-9]{8}", message = "019A31:OriReqDate参数不正确")
	private String oriReqDate;
	//原交易发起方流水号
	@CustomAnnotation(path = "Body.OriReqTransID")
	@NotNull(message="019A32:OriReqTransID参数不正确")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,32}", message = "019A32:OriReqTransID参数不正确")
	private String oriReqTransID;
//	//冲正原因
//	@CustomAnnotation(path = "Body.RevokeReason")
//	@NotNull(message="019A33:RevokeReason参数不正确")
//	@Length(max=256,message="019A33:RevokeReason参数不正确")
//	private String RevokeReason;
	
	public String getOriReqSys() {
		return oriReqSys;
	}
	public void setOriReqSys(String oriReqSys) {
		this.oriReqSys = oriReqSys;
	}
	public String getOriReqDate() {
		return oriReqDate;
	}
	public void setOriReqDate(String oriReqDate) {
		this.oriReqDate = oriReqDate;
	}
	
	public String getOriReqTransID() {
		return oriReqTransID;
	}
	public void setOriReqTransID(String oriReqTransID) {
		this.oriReqTransID = oriReqTransID;
	}
}
