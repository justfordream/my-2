package com.huateng.cmupay.parseMsg.reflect.vo.tmall;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author panlg
 * @version 创建时间：2013-11-19 上午11:15:57 
 * 类说明  天猫全网浙江发起充值结果通知报文体
 */
public class TmallConsumeResVo {
	/*（天猫）充值请求的发起方交易流水号*/
	@CustomAnnotation(path = "Body.OriReqTransID")
	@NotNull(message="oriReqTransID不能为空")
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="oriReqTransID格式不正确")
	private String oriReqTransID;
	
	/*原交易日期*/
	@CustomAnnotation(path = "Body.OriReqDate")
	@NotNull(message="oriReqDate不能为空")
	@Pattern(regexp = "([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))", message = "019A04:oriReqDate参数不正确")
	private String oriReqDate;
	
	/*电商的订单编号*/
	/*订单编号*/
	@CustomAnnotation(path = "Body.OrderID")
	@NotNull(message="orderId不能为空")
	@Pattern(regexp="[0-9,a-z,A-Z]{1,32}",message="orderId格式不正确")
	private String orderID;
	
	/*充值结果代码*/
	@CustomAnnotation(path = "Body.ResultCode")
	@NotNull(message="resultCode不能为空")
	private String resultCode;
	
	/*充值结果描述*/
	@CustomAnnotation(path = "Body.ResultDesc")
	@NotNull(message="resultDesc不能为空")
	@Length(max=256,message="resultDesc超过256个字节")
	private String resultDesc;
	
	/*省BOSS充值处理时间*/
	@CustomAnnotation(path = "Body.ResultTime")
	private String resultTime;

	public String getOriReqTransID() {
		return oriReqTransID;
	}

	public void setOriReqTransID(String oriReqTransID) {
		this.oriReqTransID = oriReqTransID;
	}

	public String getOriReqDate() {
		return oriReqDate;
	}

	public void setOriReqDate(String oriReqDate) {
		this.oriReqDate = oriReqDate;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public String getResultTime() {
		return resultTime;
	}

	public void setResultTime(String resultTime) {
		this.resultTime = resultTime;
	}
}
