package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;
/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧签约关系查询请求报文
 */
public class BankBindQueryReqVo {
	
	@CustomAnnotation(path="Body.IDType")
	@NotNull(message="019A17:IDType格式不正确")
	@Pattern(regexp="01",message="019A17:IDType格式不正确")
	private String IDType;
	
	@CustomAnnotation(path="Body.IDValue")
	@NotNull(message="019A18:IDValue格式不正确")
	//@Length(min=11,max=32,message="019A18:IDValue格式不正确") 
	@Pattern(regexp="1[0-9]{10}",message="019A18:IDValue格式不正确")
//	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;

	public String getIDType() {
		return IDType;
	}

	public void setIDType(String iDType) {
		IDType = iDType;
	}

	public String getIDValue() {
		return IDValue;
	}

	public void setIDValue(String iDValue) {
		IDValue = iDValue;
	}
	
	
}
