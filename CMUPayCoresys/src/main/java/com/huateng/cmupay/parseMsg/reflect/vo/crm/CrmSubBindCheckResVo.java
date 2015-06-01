package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 副号码签约校验响应请求报文体
 * @author ning.z
 *
 */
public class CrmSubBindCheckResVo {
	//消息返回码
	@CustomAnnotation(path = "SvcCont.SubCheckRsp.RspCode")
	private String rspCode;
	//消息内容
	@CustomAnnotation(path = "SvcCont.SubCheckRsp.RspInfo")
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
