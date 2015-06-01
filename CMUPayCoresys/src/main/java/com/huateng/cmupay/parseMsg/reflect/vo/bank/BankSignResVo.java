package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧预签约响应报文体
 */
public class BankSignResVo {
	//等待会话标识
	@CustomAnnotation(path = "Body.SessionID")
	private String SessionID;
	//中国移动用户类型
	@CustomAnnotation(path = "Body.UserCat")
	private String UserCat;
	//是否可以签约
	@CustomAnnotation(path = "Body.AutoPayable")
	private String AutoPayable;
	//充值阀值
	@CustomAnnotation(path = "Body.DefRechThreshold")
	private Long DefRechThreshold;
	//最大充值阀值
	@CustomAnnotation(path = "Body.MaxRechThreshold")
	private Long MaxRechThreshold;
	//缺省充值额度
	@CustomAnnotation(path = "Body.DefRechAmount")
	private Long DefRechAmount;
	//最大充值额度
	@CustomAnnotation(path = "Body.MaxRechAmount")
	private Long MaxRechAmount;
	//银行标识
	@CustomAnnotation(path = "Body.BankID")
	private String BankID;
	//签约协议号
	@CustomAnnotation(path = "Body.SubID")
	private String SubID;
	//归属省
	@CustomAnnotation(path = "Body.HomeProv")
	private String HomeProv;
	
	public String getBankID() {
			return BankID;
	}
	public void setBankID(String bankID) {
			BankID = bankID;
	}
	public String getSubID() {
			return SubID;
	}
	public void setSubID(String subID) {
			SubID = subID;
	}
	public String getSessionID() {
		return SessionID;
	}
	public void setSessionID(String sessionID) {
		SessionID = sessionID;
	}

	public String getUserCat() {
		return UserCat;
	}

	public void setUserCat(String userCat) {
		UserCat = userCat;
	}

	public String getAutoPayable() {
		return AutoPayable;
	}

	public void setAutoPayable(String autoPayable) {
		AutoPayable = autoPayable;
	}
	public Long getDefRechThreshold() {
		return DefRechThreshold;
	}
	public void setDefRechThreshold(Long defRechThreshold) {
		DefRechThreshold = defRechThreshold;
	}
	public Long getMaxRechThreshold() {
		return MaxRechThreshold;
	}
	public void setMaxRechThreshold(Long maxRechThreshold) {
		MaxRechThreshold = maxRechThreshold;
	}
	public Long getDefRechAmount() {
		return DefRechAmount;
	}
	public void setDefRechAmount(Long defRechAmount) {
		DefRechAmount = defRechAmount;
	}
	public void setMaxRechAmount(Long maxRechAmount) {
		MaxRechAmount = maxRechAmount;
	}
	public Long getMaxRechAmount() {
		return MaxRechAmount;
	}
	public String getHomeProv() {
		return HomeProv;
	}
	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}


	

}
