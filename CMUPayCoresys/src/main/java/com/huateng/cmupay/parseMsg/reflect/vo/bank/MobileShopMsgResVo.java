package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;
import com.huateng.cmupay.parseMsg.reflect.handle.ParentAnnotation;

/**
 * @author oul
 * @version 时间 2013/10/10 类说明 响应报文
 * */
public class MobileShopMsgResVo extends BaseMsgVo{
	@CustomAnnotation(path="Body.OriReqTransID")
	@NotNull(message="019A32:OriReqTransID不能为空")
	@Pattern(regexp=".{0,32}",message="019A32:OriReqTransID格式不正确")
	private String OriReqTransID;//原交易发起方交易流水号
	
	@CustomAnnotation(path="Body.OriReqDate")
	@NotNull(message="019A31:OriReqDate不能为空")
	@Pattern(regexp=".{8}",message="019A31:OriReqDate格式不正确")
	private String OriReqDate;//原交易日期
	
	@CustomAnnotation(path="Body.OrderID")
	@NotNull(message="019A27:OrderID不能为空")
	@Pattern(regexp=".{0,32}",message="019A27:OrderID格式不正确")
	private String OrderID;//订单编号
	
	@CustomAnnotation(path="Body.ResultCode")
	@NotNull(message="019A63:ResultCode不能为空")
	@Pattern(regexp=".{6}",message="019A63:ResultCode格式不正确")
	private String ResultCode;//充值结果代码
	
	@CustomAnnotation(path="Body.ResultDesc")
	@NotNull(message="019A64:ResultDesc不能为空")
	@Pattern(regexp=".{0,256}",message="019A64:ResultDesc格式不正确")
	private String ResultDesc;//充值结果描述
	
	@CustomAnnotation(path="Body.ResultTime")
	@Pattern(regexp=".{14}",message="019A65:ResultTime格式不正确")
	private String ResultTime;//省Boss充值处理时间

	public String getOriReqTransID() {
		return OriReqTransID;
	}

	public void setOriReqTransID(String oriReqTransID) {
		OriReqTransID = oriReqTransID;
	}

	public String getOriReqDate() {
		return OriReqDate;
	}

	public void setOriReqDate(String oriReqDate) {
		OriReqDate = oriReqDate;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
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

	public String getResultTime() {
		return ResultTime;
	}

	public void setResultTime(String resultTime) {
		ResultTime = resultTime;
	}
	
}
