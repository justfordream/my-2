package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧解约请求报文体
 */
public class BankUnbindResVo {
	//银行标识
	@CustomAnnotation(path = "Body.BankID")
	@NotNull(message="bankID参数不正确")
	@Pattern(regexp="[0-9]{4}",message="bankID参数不正确")
    private String bankID;

	public String getBankID() {
		return bankID;
	}

	public void setBankID(String bankID) {
		this.bankID = bankID;
	}
}
