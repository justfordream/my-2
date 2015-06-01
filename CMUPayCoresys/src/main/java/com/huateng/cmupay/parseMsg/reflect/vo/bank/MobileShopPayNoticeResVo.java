package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * 返回给移动商城的支付结果通知报文对象
 * @author Manzhizhen
 *
 */
public class MobileShopPayNoticeResVo extends BaseMsgVo{
	@CustomAnnotation(path = "Body.OrderID")
	@NotNull(message="019A27:OrderID参数不正确")
	@Pattern(regexp = "\\S{32}", message = "019A27:OrderID参数不正确")
	private String OrderID;
	
	@CustomAnnotation(path = "Body.SettleDate")
	@NotNull(message="025A05:SettleDate参数不正确")
	@Pattern(regexp = "\\d{8}", message = "025A05:SettleDate参数不正确")
	private String SettleDate;
	
	@CustomAnnotation(path = "Body.SettleOrg")
	@NotNull(message="025A05:SettleOrg参数不正确")
	@Pattern(regexp = "\\S{4}", message = "025A05:SettleOrg参数不正确")
	private String SettleOrg;
	
	@CustomAnnotation(path = "Body.ResultCode")
	@NotNull(message="025A05:ResultCode参数不正确")
	@Pattern(regexp = "\\S{6}", message = "025A05:ResultCode参数不正确")
	private String ResultCode;
	
	@CustomAnnotation(path = "Body.ResultDesc")
	@NotNull(message="025A05:ResultDesc参数不正确")
	@Pattern(regexp = "\\.{256}", message = "025A05:ResultDesc参数不正确")
	private String ResultDesc;

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getSettleDate() {
		return SettleDate;
	}

	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
	}

	public String getSettleOrg() {
		return SettleOrg;
	}

	public void setSettleOrg(String settleOrg) {
		SettleOrg = settleOrg;
	}

	public String getResultCode() {
		return ResultCode;
	}

	public void setResultCode(String resultCode) {
		ResultCode = resultCode;
	}

	public String getResultDesc() {
		return ResultDesc;
	}

	public void setResultDesc(String resultDesc) {
		ResultDesc = resultDesc;
	}
	
}
