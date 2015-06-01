package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 副号码签约校验
 * @author ning.z
 *
 */
public class CrmSubBindCheckReqVo {
	//主号标识类型
	@CustomAnnotation(path = "SvcCont.SubCheckReq.MainIDType")
	@NotNull(message="MainIDType不能为空")
	@Pattern(regexp="01",message="MainIDType格式不正确")
	private String mainIDType;
	//主号用户标识编码
	@CustomAnnotation(path = "SvcCont.SubCheckReq.MainIDValue")
	@NotNull(message="MainIDValue不能为空")
//	@Length(min=1,max=32,message="MainIDValue格式不正确")
	@Pattern(regexp="^1[0-9]{10}",message="MainIDValue格式不正确")
	private String mainIDValue;
	//副号用户标识类型
	@CustomAnnotation(path = "SvcCont.SubCheckReq.SubIDType")
	@NotNull(message="SubIDType不能为空")
	@Pattern(regexp="01",message="SubIDType格式不正确")
	private String subIDType;
	//副号用户标识编码
	@CustomAnnotation(path = "SvcCont.SubCheckReq.SubIDValue")
	@NotNull(message="SubIDValue不能为空")
//	@Length(min=1,max=32,message="SubIDValue格式不正确")
	@Pattern(regexp="^1[0-9]{10}",message="SubIDValue格式不正确")
	private String subIDValue;
	public String getMainIDType() {
		return mainIDType;
	}
	public void setMainIDType(String mainIDType) {
		this.mainIDType = mainIDType;
	}
	public String getMainIDValue() {
		return mainIDValue;
	}
	public void setMainIDValue(String mainIDValue) {
		this.mainIDValue = mainIDValue;
	}
	public String getSubIDType() {
		return subIDType;
	}
	public void setSubIDType(String subIDType) {
		this.subIDType = subIDType;
	}
	public String getSubIDValue() {
		return subIDValue;
	}
	public void setSubIDValue(String subIDValue) {
		this.subIDValue = subIDValue;
	}


	
	
	
	
	
}
