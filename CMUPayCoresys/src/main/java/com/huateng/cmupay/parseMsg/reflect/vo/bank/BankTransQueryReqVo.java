package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧交易状态查询请求报文体
 */
public class BankTransQueryReqVo {
	@CustomAnnotation(path = "Body.OriReqSys")
	@NotNull(message="019A30:OriReqSys格式不正确")
	@Pattern(regexp = "[0-9]{4}", message = "OriReqSys参数不正确")
	private String oriReqSys;

	@CustomAnnotation(path = "Body.OriReqDate")
	@NotNull(message="019A31:OriReqDate格式不正确")
	@Pattern(regexp = "[0-9]{8}", message = "OriReqDate参数不正确")
	private String oriReqDate;

	@CustomAnnotation(path = "Body.OriReqTransID")
	@NotNull(message="019A32:OriReqTransID格式不正确")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,32}", message = "OriReqTransID参数不正确")
	private String oriReqTransID;

	@CustomAnnotation(path = "Body.OriActivityCode")
	/*@NotNull(message="019A17:OriActivityCode格式不正确")*/
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,6}", message = "OriActivityCode参数不正确")
	private String oriActivityCode;



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

	public String getOriActivityCode() {
		return oriActivityCode;
	}

	public void setOriActivityCode(String oriActivityCode) {
		this.oriActivityCode = oriActivityCode;
	}

	public String getOriReqSys() {
		return oriReqSys;
	}

	public void setOriReqSys(String oriReqSys) {
		this.oriReqSys = oriReqSys;
	}

}
