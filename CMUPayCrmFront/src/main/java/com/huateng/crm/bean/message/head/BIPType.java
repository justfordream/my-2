package com.huateng.crm.bean.message.head;

/**
 * 交易类型信息
 * 
 * @author Gary
 * 
 */
public class BIPType {
	/**
	 * 业务功能代码(参见业务交易代码表)
	 */
	private String BIPCode;
	/**
	 * 交易代码(参见业务交易代码表)
	 */
	private String ActivityCode;
	/**
	 * 交易动作代码(0：请求;1：应答)
	 */
	private String ActionCode;

	public String getBIPCode() {
		return BIPCode;
	}

	public void setBIPCode(String bIPCode) {
		BIPCode = bIPCode;
	}

	public String getActivityCode() {
		return ActivityCode;
	}

	public void setActivityCode(String activityCode) {
		ActivityCode = activityCode;
	}

	public String getActionCode() {
		return ActionCode;
	}

	public void setActionCode(String actionCode) {
		ActionCode = actionCode;
	}

}
