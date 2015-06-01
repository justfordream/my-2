package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;


 
/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧用户应缴费用查询应答报文体
 */
public class BankUserInfoQueryResVo {

	
	/**
	 * 归属省
	 */
	@CustomAnnotation(path="Body.HomeProv")
	private String HomeProv;
	
	/**
	 * 用户姓名
	 */
	@CustomAnnotation(path="Body.UserName")
	private String UserName;
	
	/**
	 * 用户类型
	 * 预付费/后付费
	 */
	@CustomAnnotation(path="Body.UserCat")
	private String UserCat;
	
	/**
	 * 余额
	 */
	@CustomAnnotation(path="Body.Balance")
	private Long Balance;
	
	/**
	 * 应缴话费
	 */
	@CustomAnnotation(path="Body.PayAmount")
	private Long PayAmount;

	
	public String getHomeProv() {
		return HomeProv;
	}

	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserCat() {
		return UserCat;
	}

	public void setUserCat(String userCat) {
		UserCat = userCat;
	}

	public Long getBalance() {
		return Balance;
	}

	public void setBalance(Long balance) {
		Balance = balance;
	}

	public Long getPayAmount() {
		return PayAmount;
	}

	public void setPayAmount(Long payAmount) {
		PayAmount = payAmount;
	}


}
