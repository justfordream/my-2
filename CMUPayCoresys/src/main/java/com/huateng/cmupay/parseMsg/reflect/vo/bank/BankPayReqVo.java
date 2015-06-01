package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CascadeAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧银行缴费请求报文体
 */
public class BankPayReqVo  {

	@CustomAnnotation(path="Body.SubID")
	@NotNull(message = "SubID格式不正确")
	@Pattern(regexp = "[0-9,a-z,A-Z]{2}", message = "SubID格式不正确")
	private String SubID;
	
	@CustomAnnotation(path="Body.IDType")
	@NotNull(message = "IDType格式不正确")
	@Pattern(regexp = "[0-9]{2}", message = "IDType格式不正确")
	private String IDType;
	
	@CustomAnnotation(path="Body.IDValue")
	@NotNull(message = "IDValue格式不正确")
//	@Length(max=32,message="IDValue格式不正确")
	@Pattern(regexp="1[0-9]{10}",message="iDValue格式不正确")
	@CascadeAnnotation(cascadeMethod = "getIDType", method = "getIDValue", constraintClazzPath = "com.huateng.cmupay.tools.IdValueCascadeValidator")
	private String IDValue;

	@CustomAnnotation(path="Body.Payed")
	@NotNull(message = "Payed格式不正确")
	@Pattern(regexp = "[0-9]{1,9}", message = "Payed格式不正确")
	private String Payed;
	
	@CustomAnnotation(path="Body.HomeProv")
	@NotNull(message = "HomeProv格式不正确")
	@Pattern(regexp = "[0-9]{3}", message = "HomeProv格式不正确")
	private String HomeProv;

	public String getSubID() {
		return SubID;
	}

	public void setSubID(String subID) {
		SubID = subID;
	}

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

	public String getPayed() {
		return Payed;
	}

	public void setPayed(String payed) {
		Payed = payed;
	}

	public String getHomeProv() {
		return HomeProv;
	}

	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}

	
	
}
