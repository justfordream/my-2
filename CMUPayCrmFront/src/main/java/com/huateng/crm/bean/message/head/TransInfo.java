package com.huateng.crm.bean.message.head;

/**
 * 交易流水信息
 * 
 * @author Gary
 * 
 */
public class TransInfo {
	/**
	 * 业务流水号(发起方填写的包含此交易业务的流水号)
	 */
	private String SessionID;
	/**
	 * 发起方交易流水号(在发起方唯一标识一个交易的流水号，系统内唯一)
	 */
	private String TransIDO;
	/**
	 * 处理时间(发起方发起请求的时间YYYYMMDDHHMMSS)
	 */
	private String TransIDOTime;
	/**
	 * 落地方交易流水号(在落地方唯一标识一个交易的流水号，系统内唯一)
	 */
	private String TransIDH;
	/**
	 * 处理时间(落地方处理请求的时间YYYYMMDDHHMMSS)
	 */
	private String TransIDHTime;

	public String getSessionID() {
		return SessionID;
	}

	public void setSessionID(String sessionID) {
		SessionID = sessionID;
	}

	public String getTransIDO() {
		return TransIDO;
	}

	public void setTransIDO(String transIDO) {
		TransIDO = transIDO;
	}

	public String getTransIDOTime() {
		return TransIDOTime;
	}

	public void setTransIDOTime(String transIDOTime) {
		TransIDOTime = transIDOTime;
	}

	public String getTransIDH() {
		return TransIDH;
	}

	public void setTransIDH(String transIDH) {
		TransIDH = transIDH;
	}

	public String getTransIDHTime() {
		return TransIDHTime;
	}

	public void setTransIDHTime(String transIDHTime) {
		TransIDHTime = transIDHTime;
	}

}
