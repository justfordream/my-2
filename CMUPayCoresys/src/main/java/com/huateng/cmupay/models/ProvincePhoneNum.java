package com.huateng.cmupay.models;

import com.huateng.cmupay.constant.CommonConstant;

public class ProvincePhoneNum {
	private String provinceCode; // 省代码
	private int phoneNumFlag = CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType(); // 手机号码类型： 1：移动， 2：联通或电信, 0:未知号码类型

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public int getPhoneNumFlag() {
		return phoneNumFlag;
	}

	public void setPhoneNumFlag(int phoneNumFlag) {
		this.phoneNumFlag = phoneNumFlag;
	}
	
	@Override
	public String toString() {
		return "ProvinceCode：" + provinceCode + "  PhoneNumType：" + phoneNumFlag;
	}
}
