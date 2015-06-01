package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

public class MobileShopOpenResVo extends BaseMsgVo{
	@CustomAnnotation(path = "Body.MerId",power=2)
	@NotNull(message="019A21:MerId参数不正确")
	@Pattern(regexp = "\\w{0,32}", message = "019A21:MerId参数不正确")
	private String MerId;
	@CustomAnnotation(path = "Body.ActivateStatus",power=2)
	@NotNull(message="019A21:ActivateStatus参数不正确")
	@Pattern(regexp = "0|1|2|3", message = "019A21:ActivateStatus参数不正确")
	private String ActivateStatus;
	public String getMerId() {
		return MerId;
	}
	public void setMerId(String merId) {
		MerId = merId;
	}
	public String getActivateStatus() {
		return ActivateStatus;
	}
	public void setActivateStatus(String activateStatus) {
		ActivateStatus = activateStatus;
	}
}
