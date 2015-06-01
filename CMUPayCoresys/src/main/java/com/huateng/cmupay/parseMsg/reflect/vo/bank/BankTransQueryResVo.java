package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧交易状态查询响应报文体
 */
public class BankTransQueryResVo {
	@CustomAnnotation(path = "Body.OriRcvDate")
	@Pattern(regexp = "[0-9]{8}", message = "OriRcvDate参数不正确")
	private String oriRcvDate;
	@CustomAnnotation(path = "Body.OriRcvTransID")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,32}", message = "OriRcvTransID参数不正确")
	private String oriRcvTransID;
	@CustomAnnotation(path = "Body.OriRspCode")
	@Pattern(regexp = "[a-z,A-Z,0-9]{4}", message = "OriRspCode参数不正确")
	private String oriRspCode;
	@CustomAnnotation(path = "Body.OriRspDesc")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,256}", message = "OriRspDesc参数不正确")
	private String oriRspDesc;



	public String getOriRcvDate() {
		return oriRcvDate;
	}

	public void setOriRcvDate(String oriRcvDate) {
		this.oriRcvDate = oriRcvDate;
	}

	public String getOriRcvTransID() {
		return oriRcvTransID;
	}

	public void setOriRcvTransID(String oriRcvTransID) {
		this.oriRcvTransID = oriRcvTransID;
	}

	public String getOriRspCode() {
		return oriRspCode;
	}

	public void setOriRspCode(String oriRspCode) {
		this.oriRspCode = oriRspCode;
	}

	public String getOriRspDesc() {
		return oriRspDesc;
	}

	public void setOriRspDesc(String oriRspDesc) {
		this.oriRspDesc = oriRspDesc;
	}
}
