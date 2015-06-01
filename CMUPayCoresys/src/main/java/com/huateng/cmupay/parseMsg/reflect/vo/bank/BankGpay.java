package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  用于发票的bean
 */
@XStreamAlias("GPay")
public class BankGpay {
	@XStreamAlias("Header")
	private BankHeader Header;
	@XStreamAlias("Body")
	private BankPrintInvoiceQueryRspVo Body;
	@XStreamAlias("Sign")
	private BankSign Sign;
	public BankHeader getHeader() {
		return Header;
	}
	public void setHeader(BankHeader header) {
		Header = header;
	}
	
	public BankPrintInvoiceQueryRspVo getBody() {
		return Body;
	}
	public void setBody(BankPrintInvoiceQueryRspVo body) {
		Body = body;
	}
	public BankSign getSign() {
		return Sign;
	}
	public void setSign(BankSign sign) {
		Sign = sign;
	}
	
	
	
}
