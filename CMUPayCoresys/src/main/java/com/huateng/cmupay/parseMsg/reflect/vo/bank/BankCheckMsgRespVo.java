package com.huateng.cmupay.parseMsg.reflect.vo.bank;


import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧银行账号校验响应报文体
 */
public class BankCheckMsgRespVo {
	
	@CustomAnnotation(path="Body.BankAcctType",power='0')
	private String bankAcctType;

	public String getBankAcctType() {
		return bankAcctType;
	}

	public void setBankAcctType(String bankAcctType) {
		this.bankAcctType = bankAcctType;
	}

	
	

}
