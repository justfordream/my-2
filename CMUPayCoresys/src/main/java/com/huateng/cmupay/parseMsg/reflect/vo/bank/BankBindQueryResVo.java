package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;
/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧签约关系查询应答报文体
 */
public class BankBindQueryResVo {
	@CustomAnnotation(path="Body.SubID")
	private String SubID;
	@CustomAnnotation(path="Body.SubTime")
	private String SubTime;
	@CustomAnnotation(path="Body.BankAcctID")
	private String BankAcctID;
	@CustomAnnotation(path="Body.BankAcctType")
	private String BankAcctType;
	@CustomAnnotation(path="Body.PayType")
	private String PayType;
	@CustomAnnotation(path="Body.RechThreshold")
	private Long RechThreshold;
	@CustomAnnotation(path="Body.RechAmount")
	private Long RechAmount;
	@CustomAnnotation(path="Body.HomeProv")
	private String HomeProv;
	public String getSubID() {
		return SubID;
	}
	public void setSubID(String subID) {
		SubID = subID;
	}
	public String getSubTime() {
		return SubTime;
	}
	public void setSubTime(String subTime) {
		SubTime = subTime;
	}
	public String getBankAcctID() {
		return BankAcctID;
	}
	public void setBankAcctID(String bankAcctID) {
		BankAcctID = bankAcctID;
	}
	public String getBankAcctType() {
		return BankAcctType;
	}
	public void setBankAcctType(String bankAcctType) {
		BankAcctType = bankAcctType;
	}
	public String getPayType() {
		return PayType;
	}
	public void setPayType(String payType) {
		PayType = payType;
	}
	public Long getRechThreshold() {
		return RechThreshold;
	}
	public void setRechThreshold(Long rechThreshold) {
		RechThreshold = rechThreshold;
	}
	public Long getRechAmount() {
		return RechAmount;
	}
	public void setRechAmount(Long rechAmount) {
		RechAmount = rechAmount;
	}
	public String getHomeProv() {
		return HomeProv;
	}
	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}

	
	
}
