package com.huateng.cmupay.tools;

import com.huateng.cmupay.constant.CommonConstant;



/**
 * @author cmt
 * 
 */
public class IdValueCascadeValidator implements IValidator {
	
	

	public String isValid(Object arg1, Object arg2) {

		if (arg1 == null || arg2 == null || "".equals(arg1) || "".equals(arg2))
			return "IDType和IDValue级联验证失败";
		// 手机号码
		if (arg1.toString().equals(CommonConstant.UserSignType.Phone.getValue())) {
			if (arg2.toString().length() < 11 || arg2.toString().length() > 32) {
				return "IDType和IDValue级联验证失败";
			} else {
				return "";
			}
		}
		// 飞信号
		else if (arg1.toString().equals(CommonConstant.UserSignType.Fetion.getValue())) {
			//TODO
			return "";
		}
		// 宽带用户号
		else if (arg1.toString().equals(CommonConstant.UserSignType.WB.getValue())) {
			//TODO
			return "";
		}
		// Email
		else if (arg1.toString().equals(CommonConstant.UserSignType.Email.getValue())) {
			//TODO
			return "";

		} else {
			return "IDType和IDValue级联验证失败";
		}

	}

}