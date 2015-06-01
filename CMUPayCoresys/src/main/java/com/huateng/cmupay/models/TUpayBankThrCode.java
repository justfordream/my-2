package com.huateng.cmupay.models;

import java.io.Serializable;

/** 
 * @author oul  
 * @version 创建时间：2014-4-10 
 * 类说明  银行代码表
 * 对应的数据库表：TPAY_CSYS_THR_BANK_CODE
 */
public class TUpayBankThrCode implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//银行代码    统一支付跟省份、移动商城的代码
	String bankId;
	//银行名称
	String bankName;
	//第三方机构代码    银联/支付宝/财付通
	String thrOrgId;
	//第三方银行代码
	String thrBankId;
	//保留域1
	String reserved1;
	//保留域2
	String reserved2;
	//保留域3
	String reserved3;
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getThrOrgId() {
		return thrOrgId;
	}
	public void setThrOrgId(String thrOrgId) {
		this.thrOrgId = thrOrgId;
	}
	public String getThrBankId() {
		return thrBankId;
	}
	public void setThrBankId(String thrBankId) {
		this.thrBankId = thrBankId;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
