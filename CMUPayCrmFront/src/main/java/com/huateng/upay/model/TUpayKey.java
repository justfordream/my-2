package com.huateng.upay.model;

import java.io.Serializable;

/**
 * 密钥实体
 * 
 * @author Gary
 * 
 */
@SuppressWarnings("serial")
public class TUpayKey implements Serializable {
	/**
	 * 主键
	 */
	private String objId;
	/**
	 * 流水号
	 */
	private String transIDO;
	/**
	 * 更新时间
	 */
	private String transIDOTime;
	/**
	 * 操作人员
	 */
	private String operateUser;
	/**
	 * 操作时间
	 */
	private String operateTime;
	/**
	 * 密钥
	 */
	private String newKey;
	/**
	 * 状态[0:成功;1：失败]
	 */
	private String status;
	/**
	 * @return the objId
	 */
	public String getObjId() {
		return objId;
	}

	/**
	 * @param objId
	 *            the objId to set
	 */
	public void setObjId(String objId) {
		this.objId = objId;
	}

	/**
	 * @return the transIDO
	 */
	public String getTransIDO() {
		return transIDO;
	}

	/**
	 * @param transIDO
	 *            the transIDO to set
	 */
	public void setTransIDO(String transIDO) {
		this.transIDO = transIDO;
	}

	/**
	 * @return the transIDOTime
	 */
	public String getTransIDOTime() {
		return transIDOTime;
	}

	/**
	 * @param transIDOTime
	 *            the transIDOTime to set
	 */
	public void setTransIDOTime(String transIDOTime) {
		this.transIDOTime = transIDOTime;
	}

	/**
	 * @return the operateUser
	 */
	public String getOperateUser() {
		return operateUser;
	}

	/**
	 * @param operateUser
	 *            the operateUser to set
	 */
	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}

	/**
	 * @return the operateTime
	 */
	public String getOperateTime() {
		return operateTime;
	}

	/**
	 * @param operateTime
	 *            the operateTime to set
	 */
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	/**
	 * @return the newKey
	 */
	public String getNewKey() {
		return newKey;
	}

	/**
	 * @param newKey
	 *            the newKey to set
	 */
	public void setNewKey(String newKey) {
		this.newKey = newKey;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
