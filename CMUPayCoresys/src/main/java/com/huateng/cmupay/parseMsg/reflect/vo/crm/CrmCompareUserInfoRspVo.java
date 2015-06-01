package com.huateng.cmupay.parseMsg.reflect.vo.crm;



import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧用户身份信息验证应答报文体
 */
public class CrmCompareUserInfoRspVo {
	
	@CustomAnnotation(path="SvcCont.IdentityCheckRsp.RspCode")
	private String rspCode;
	
	@CustomAnnotation(path="SvcCont.IdentityCheckRsp.RspInfo")
	private String rspInfo;

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public String getRspInfo() {
		return rspInfo;
	}

	public void setRspInfo(String rspInfo) {
		this.rspInfo = rspInfo;
	}

	
}
