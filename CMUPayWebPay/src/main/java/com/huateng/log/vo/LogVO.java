package com.huateng.log.vo;

/**
 * 
 * @author Gary
 * 
 */
public class LogVO {
	private String logCode;
	private String orderNo;
	private String interType;
	private String operType;
	private String interNo;
	private String asys;
	private String zsys;
	private String provCode;
	private String callTime;
	private String dealCode;
	private String errinfo;
	private String status;

	/**
	 * 日志编码，各业务支撑系统用于标识日志记录的标识号 是索引字段之一。
	 * 
	 * @return the logCode
	 */
	public String getLogCode() {
		return logCode;
	}

	/**
	 * @param logCode
	 *            the logCode to set
	 */
	public void setLogCode(String logCode) {
		this.logCode = logCode;
	}

	/**
	 * 业务订单号，标识一个业务流程实例从发起系统一直到业务办理完成的唯一编号。
	 * 
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * @param orderNo
	 *            the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 接口类型，客户端、服务端
	 * 
	 * @return the interType
	 */
	public String getInterType() {
		return interType;
	}

	/**
	 * @param interType
	 *            the interType to set
	 */
	public void setInterType(String interType) {
		this.interType = interType;
	}

	/**
	 * 服务类型编码，例如：卡开通、卡调整等
	 * 
	 * @return the operType
	 */
	public String getOperType() {
		return operType;
	}

	/**
	 * @param operType
	 *            the operType to set
	 */
	public void setOperType(String operType) {
		this.operType = operType;
	}

	/**
	 * 接口编码
	 * 
	 * @return the interNo
	 */
	public String getInterNo() {
		return interNo;
	}

	/**
	 * @param interNo
	 *            the interNo to set
	 */
	public void setInterNo(String interNo) {
		this.interNo = interNo;
	}

	/**
	 * 当前系统编码
	 * 
	 * @return the asys
	 */
	public String getAsys() {
		return asys;
	}

	/**
	 * 
	 * 
	 * @param asys
	 *            the asys to set
	 */
	public void setAsys(String asys) {
		this.asys = asys;
	}

	/**
	 * 对方系统编码
	 * 
	 * @return the zsys
	 */
	public String getZsys() {
		return zsys;
	}

	/**
	 * @param zsys
	 *            the zsys to set
	 */
	public void setZsys(String zsys) {
		this.zsys = zsys;
	}

	/**
	 * 省代码
	 * 
	 * @return the provCode
	 */
	public String getProvCode() {
		return provCode;
	}

	/**
	 * @param provCode
	 *            the provCode to set
	 */
	public void setProvCode(String provCode) {
		this.provCode = provCode;
	}

	/**
	 * 接口调用时间 对此字段做R003校验
	 * 
	 * @return the callTime
	 */
	public String getCallTime() {
		return callTime;
	}

	/**
	 * @param callTime
	 *            the callTime to set
	 */
	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}

	/**
	 * 处理编码
	 * 
	 * @return the dealCode
	 */
	public String getDealCode() {
		return dealCode;
	}

	/**
	 * @param dealCode
	 *            the dealCode to set
	 */
	public void setDealCode(String dealCode) {
		this.dealCode = dealCode;
	}

	/**
	 * 错误日志
	 * 
	 * @return the errinfo
	 */
	public String getErrinfo() {
		return errinfo;
	}

	/**
	 * @param errinfo
	 *            the errinfo to set
	 */
	public void setErrinfo(String errinfo) {
		this.errinfo = errinfo;
	}

	/**
	 * 
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String format() {
		StringBuffer sb = new StringBuffer();
		// sb.append("BL##");
		sb.append(this.logCode + "#");
		sb.append(this.orderNo + "#");
		sb.append(this.interType + "#");
		sb.append(this.operType + "#");
		sb.append(this.interNo + "#");
		sb.append(this.asys + "#");
		sb.append(this.zsys + "#");
		sb.append(this.provCode + "#");
		sb.append(this.callTime + "#");
		sb.append(this.dealCode + "#");
		sb.append(this.errinfo);
		//sb.append(this.status);
		// sb.append("##LB");
		return sb.toString();
	}
}
