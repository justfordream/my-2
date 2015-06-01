package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧缴费请求报文体
 */
public class BankConsumeReqVo {

	@CustomAnnotation(path = "Body.IDType")
	@NotNull(message = "IDType格式不正确")
	@Pattern(regexp = "01", message = "IDType格式不正确")
	private String IDType;

	@CustomAnnotation(path = "Body.IDValue")
	@NotNull(message = "IDValue格式不正确")
//	@Length(min=11,max=11,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;
	
	@CustomAnnotation(path = "Body.Payed")
	@NotNull(message = "Payed格式不正确")
	@Pattern(regexp = "[0-9]{1,9}", message = "Payed格式不正确")
	private String payed;//用户缴纳金额，单位为“分” TODO

	public String getPayed() {
		return payed;
	}

	public void setPayed(String payed) {
		this.payed = payed;
	}

	public String getIDType() {
		return IDType;
	}

	public void setIDType(String idType) {
		this.IDType = idType;
	}

	public String getIDValue() {
		return IDValue;
	}

	public void setIDValue(String idValue) {
		this.IDValue = idValue;
	}

}
