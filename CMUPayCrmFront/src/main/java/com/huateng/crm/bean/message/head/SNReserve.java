package com.huateng.crm.bean.message.head;

/**
 * 机构不填，SN保留信息<br>
 * 落地方在接到请求时、发起方在接到应答时读取
 * 
 * @author Gary
 * 
 */
public class SNReserve {
	/**
	 * SN交易流水号(由网状网发起方SN填写)
	 */
	private String TransIDC;
	/**
	 * SN处理标识(只有发起方SN使用)
	 */
	private String ConvID;
	/**
	 * 日切点(格式：yyyymmdd，清分对帐用)
	 */
	private String CutOffDay;
	/**
	 * 处理时间(发起方SN接到请求的时间YYYYMMDDHHMMSS)
	 */
	private String OSNTime;
	/**
	 * 发起方交换节点代码(参见全国交换节点编码表)
	 */
	private String OSNDUNS;
	/**
	 * 归属方交换节点代码(参见全国交换节点编码表)
	 */
	private String HSNDUNS;
	/**
	 * 发起方机构编码(参见全国机构编码表)
	 */
	private String MsgSender;
	/**
	 * 归属方机构编码(参见全国机构编码表)
	 */
	private String MsgReceiver;
	/**
	 * 交易优先级(从0到99)
	 */
	private String Priority;
	/**
	 * 服务级别(从0到99)
	 */
	private String ServiceLevel;
	/**
	 * 报文体类型(01-XML报文 ,02-二进制数据, 03-XML报文和二进制数据混合 )
	 */
	private String SvcContType;

	public String getTransIDC() {
		return TransIDC;
	}

	public void setTransIDC(String transIDC) {
		TransIDC = transIDC;
	}

	public String getConvID() {
		return ConvID;
	}

	public void setConvID(String convID) {
		ConvID = convID;
	}

	public String getCutOffDay() {
		return CutOffDay;
	}

	public void setCutOffDay(String cutOffDay) {
		CutOffDay = cutOffDay;
	}

	public String getOSNTime() {
		return OSNTime;
	}

	public void setOSNTime(String oSNTime) {
		OSNTime = oSNTime;
	}

	public String getOSNDUNS() {
		return OSNDUNS;
	}

	public void setOSNDUNS(String oSNDUNS) {
		OSNDUNS = oSNDUNS;
	}

	public String getHSNDUNS() {
		return HSNDUNS;
	}

	public void setHSNDUNS(String hSNDUNS) {
		HSNDUNS = hSNDUNS;
	}

	public String getMsgSender() {
		return MsgSender;
	}

	public void setMsgSender(String msgSender) {
		MsgSender = msgSender;
	}

	public String getMsgReceiver() {
		return MsgReceiver;
	}

	public void setMsgReceiver(String msgReceiver) {
		MsgReceiver = msgReceiver;
	}

	public String getPriority() {
		return Priority;
	}

	public void setPriority(String priority) {
		Priority = priority;
	}

	public String getServiceLevel() {
		return ServiceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		ServiceLevel = serviceLevel;
	}

	public String getSvcContType() {
		return SvcContType;
	}

	public void setSvcContType(String svcContType) {
		SvcContType = svcContType;
	}

}
