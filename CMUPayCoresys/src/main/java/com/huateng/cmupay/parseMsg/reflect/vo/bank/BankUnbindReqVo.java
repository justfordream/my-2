package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧解约请求报文体
 */
public class BankUnbindReqVo {
	//中国移动用户标识类型
	@CustomAnnotation(path = "Body.IDType")
	@NotNull(message="019A17:IDType参数不正确")
	@Pattern(regexp="01|02|03|04",message="019A17:IDType参数不正确")
	private String IDType;
	//中国移动用户ID
	@CustomAnnotation(path = "Body.IDValue")
	@NotNull(message="019A18:IDValue参数不正确")
//	@Length(max=32,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="019A18:iDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	//签约协议号
	@CustomAnnotation(path = "Body.SubID")
	@NotNull(message="019A20:subID参数不正确")
	@Pattern(regexp="[0-9]{22}",message="019A20:subID参数不正确")
	private String subID;
	public String getIDType() {
		return IDType;
	}
	public void setIDType(String iDType) {
		this.IDType = iDType;
	}
	public String getIDValue() {
		return IDValue;
	}
	public void setIDValue(String iDValue) {
		this.IDValue = iDValue;
	}
	public String getSubID() {
		return subID;
	}
	public void setSubID(String subID) {
		this.subID = subID;
	}
	
}
