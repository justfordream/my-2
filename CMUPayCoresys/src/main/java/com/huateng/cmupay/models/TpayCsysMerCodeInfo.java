package com.huateng.cmupay.models;

import java.io.Serializable;

/**
 * 
 * 商户代码表映射  对应表：UPAY_CSYS_MER_CODE
 *
 */
public class TpayCsysMerCodeInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//商户代码
	private String merId;
	
	//第三方支付机构代码
	private String thrOrgCode;
	
	//机构代码
	private String orgCode;
	
	//商户类型
	private String merType;
	
	//商户名称
	private String merName;
	
	//商户简称
	private String merShortName;
	
	//商户备注
	private String merNote;
	
	//商户等级
	private String merLevel;
	
	//所属商户ID
	private String fatherMerId;
	
	//保留域
	private String reserved1;
	private String reserved2;
	private String reserved3;
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
	}
	public String getThrOrgCode() {
		return thrOrgCode;
	}
	public void setThrOrgCode(String thrOrgCode) {
		this.thrOrgCode = thrOrgCode;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getMerType() {
		return merType;
	}
	public void setMerType(String merType) {
		this.merType = merType;
	}
	public String getMerName() {
		return merName;
	}
	public void setMerName(String merName) {
		this.merName = merName;
	}
	public String getMerShortName() {
		return merShortName;
	}
	public void setMerShortName(String merShortName) {
		this.merShortName = merShortName;
	}
	public String getMerNote() {
		return merNote;
	}
	public void setMerNote(String merNote) {
		this.merNote = merNote;
	}
	public String getMerLevel() {
		return merLevel;
	}
	public void setMerLevel(String merLevel) {
		this.merLevel = merLevel;
	}
	public String getFatherMerId() {
		return fatherMerId;
	}
	public void setFatherMerId(String fatherMerId) {
		this.fatherMerId = fatherMerId;
	}
	public String getReserved1() {
		return reserved1;
	}
	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}
	public String getReserved2() {
		return reserved2;
	}
	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}
	public String getReserved3() {
		return reserved3;
	}
	public void setReserved3(String reserved3) {
		this.reserved3 = reserved3;
	}
	
	

}
