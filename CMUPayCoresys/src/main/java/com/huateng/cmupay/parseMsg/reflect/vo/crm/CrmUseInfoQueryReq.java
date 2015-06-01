package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧用户信息查询请求报文体
 */

public class CrmUseInfoQueryReq {

	
	@CustomAnnotation(path="SvcCont.AuthReq.IDType")
	private String idType;
	
	@CustomAnnotation(path="SvcCont.AuthReq.IDValue")
	private String idValue;

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdValue() {
		return idValue;
	}

	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}
	
	
	
	
}
