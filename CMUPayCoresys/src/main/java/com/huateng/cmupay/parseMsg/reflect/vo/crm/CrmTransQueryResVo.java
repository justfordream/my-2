package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  移动侧交易信息查询应答报文体
 */
public class CrmTransQueryResVo {
	@CustomAnnotation(path = "SvcCont.PayStateRsp.OriActionDate")
	@NotNull(message = "OriActionDate格式不正确")
	@Pattern(regexp = "[0-9]{8}", message = "OriActionDate格式不正确")
	private String oriActionDate;
	
	@CustomAnnotation(path = "SvcCont.PayStateRsp.OriTransactionID")
	@NotNull(message = "OriTransactionID格式不正确")
	@Pattern(regexp = "[0-9,a-z,A-Z]{32}", message = "OriTransactionID格式不正确")
	private String oriTransactionID;
	
	@CustomAnnotation(path = "SvcCont.PayStateRsp.RspCode")
	@NotNull(message = "RspCode格式不正确")
	@Pattern(regexp = "[0-9,a-z,A-Z]{4}", message = "RspCode格式不正确")
	private String rspCode;
	
	@CustomAnnotation(path = "SvcCont.PayStateRsp.RspInfo")
	@NotNull(message = "RspInfo格式不正确")
	@Length(min = 1, max = 256)
	private String rspInfo;



	public String getOriActionDate() {
		return oriActionDate;
	}

	public void setOriActionDate(String oriActionDate) {
		this.oriActionDate = oriActionDate;
	}

	public String getOriTransactionID() {
		return oriTransactionID;
	}

	public void setOriTransactionID(String oriTransactionID) {
		this.oriTransactionID = oriTransactionID;
	}

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
