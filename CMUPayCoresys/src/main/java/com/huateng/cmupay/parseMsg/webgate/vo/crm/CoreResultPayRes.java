package com.huateng.cmupay.parseMsg.webgate.vo.crm;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 中国移动统一支付系统（Web子系统）->银行网银系统的“支付”交易<br>
 * 将用户重定向回中国移动网上营业厅（HTTP响应）
 * 
 * @author Gary
 * 
 */
public class CoreResultPayRes {
	/**
	 * 商户标识
	 */
	@JsonProperty
	private String MerID;
	/**
	 * 订单标识
	 */
	@JsonProperty
	private String OrderID;
	/**
	 * 支付结果，标识支付是否成功以及失败的原因
	 */
	@JsonProperty
	private String RspCode;
	/**
	 * 对于支付结果的描述信息
	 */
	@JsonProperty
	private String RspInfo;
	/**
	 * 省公司自定义，当统一支付系统通知支付结果时，携带此变量，省公司可以用此变量维护session等
	 */
	@JsonProperty
	private String MerVAR;
	/**
	 * 交易日期时间
	 */
	@JsonProperty
	private String OrderTime;

	/**
	 * 订单金额，单位为“分”
	 */
	@JsonProperty
	private String Payed;

	/**
	 * 币种，默认人民币
	 */
	@JsonProperty
	private String CurType;
	/**
	 * UPAY10002 支付请求将用户重定向回中国省公司移动网上营业厅时填写
	 */
	@JsonProperty
	private String MCODE;
	/**
	 * 银行签名
	 */
	@JsonProperty
	private String Sig;
	/**
	 * 后台结果回传URL
	 */
	@JsonProperty
	private String ServerURL;

	/**
	 * 用户界面返回URL
	 */
	@JsonProperty
	private String BackURL;
	@JsonIgnore
	public String getMerID() {
		return MerID;
	}

	@JsonIgnore
	public void setMerID(String merID) {
		MerID = merID;
	}

	@JsonIgnore
	public String getOrderID() {
		return OrderID;
	}

	@JsonIgnore
	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	@JsonIgnore
	public String getRspCode() {
		return RspCode;
	}

	@JsonIgnore
	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}

	@JsonIgnore
	public String getRspInfo() {
		return RspInfo;
	}

	@JsonIgnore
	public void setRspInfo(String rspInfo) {
		RspInfo = rspInfo;
	}

	@JsonIgnore
	public String getMerVAR() {
		return MerVAR;
	}

	@JsonIgnore
	public void setMerVAR(String merVAR) {
		MerVAR = merVAR;
	}

	@JsonIgnore
	public String getOrderTime() {
		return OrderTime;
	}

	@JsonIgnore
	public void setOrderTime(String orderTime) {
		OrderTime = orderTime;
	}

	@JsonIgnore
	public String getPayed() {
		return Payed;
	}

	@JsonIgnore
	public void setPayed(String payed) {
		Payed = payed;
	}

	@JsonIgnore
	public String getCurType() {
		return CurType;
	}

	@JsonIgnore
	public void setCurType(String curType) {
		CurType = curType;
	}

	@JsonIgnore
	public String getMCODE() {
		return MCODE;
	}

	@JsonIgnore
	public void setMCODE(String mCODE) {
		MCODE = mCODE;
	}

	@JsonIgnore
	public String getSig() {
		return Sig;
	}

	@JsonIgnore
	public void setSig(String sig) {
		Sig = sig;
	}
	@JsonIgnore
	public String getServerURL() {
		return ServerURL;
	}

	@JsonIgnore
	public void setServerURL(String serverURL) {
		ServerURL = serverURL;
	}

	@JsonIgnore
	public String getBackURL() {
		return BackURL;
	}

	@JsonIgnore
	public void setBackURL(String backURL) {
		BackURL = backURL;
	}
	/**
	 * 获取签名字符串
	 * 
	 * @return
	 */
	public String assemlyPlainText() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(this.MerID)) {
			sb.append("MERID=" + this.MerID + "|");
		}
		if (StringUtils.isNotBlank(this.OrderID)) {
			sb.append("ORDERID=" + this.OrderID + "|");
		}
		if (StringUtils.isNotBlank(this.RspCode)) {
			sb.append("RSPCODE=" + this.RspCode + "|");
		}
		if (StringUtils.isNotBlank(this.MerVAR)) {
			sb.append("MERVAR=" + this.MerVAR + "|");
		}
		if (StringUtils.isNotBlank(this.OrderTime)) {
			sb.append("ORDERTIME=" + this.OrderTime + "|");
		}
		if (StringUtils.isNotBlank(this.Payed)) {
			sb.append("PAYED=" + this.Payed + "|");
		}
		if (StringUtils.isNotBlank(this.CurType)) {
			sb.append("CURTYPE=" + this.CurType + "|");
		}
		if (StringUtils.isNotBlank(this.MCODE)) {
			sb.append("MCODE=" + this.MCODE + "|");
		}
		return sb.toString();
	}

}
