package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 
 * @author ning.z
 *  CRM预签约应答报文体
 */
public class CrmSignMsgRespVo {
	
	@CustomAnnotation(path="SvcCont.PreContInfoRsp.RspCode")
	private String RspCode;
	@CustomAnnotation(path="SvcCont.PreContInfoRsp.RspInfo")
	private String RspInfo;
	@CustomAnnotation(path="SvcCont.PreContInfoRsp.PreContInfo.SessionID")
	private String SessionID;
	@CustomAnnotation(path="SvcCont.PreContInfoRsp.PreContInfo.UserCat")
	private String UserCat;
	@CustomAnnotation(path="SvcCont.PreContInfoRsp.PreContInfo.AutoPayable")
	private String AutoPayable;
	@CustomAnnotation(path="SvcCont.PreContInfoRsp.PreContInfo.DefRechThreshold")
	private Long DefRechThreshold;
	@CustomAnnotation(path="SvcCont.PreContInfoRsp.PreContInfo.MaxRechThreshold")
	private Long MaxRechThreshold;
	@CustomAnnotation(path="SvcCont.PreContInfoRsp.PreContInfo.DefRechAmount")
	private Long DefRechAmount;
	@CustomAnnotation(path="SvcCont.PreContInfoRsp.PreContInfo.MaxRechAmount")
	private Long MaxRechAmount;
	

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
	public Long getMaxRechAmount() {
		return MaxRechAmount;
	}
	public void setMaxRechAmount(Long maxRechAmount) {
		MaxRechAmount = maxRechAmount;
	}
	public String getRspCode() {
		return RspCode;
	}
	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}
	public String getRspInfo() {
		return RspInfo;
	}
	public void setRspInfo(String rspInfo) {
		RspInfo = rspInfo;
	}

	
	
}
