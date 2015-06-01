package com.huateng.third.bean.head;

/**
 * 消息签名
 * 
 * @author Gary
 * 
 */
public class Sign {
	/**
	 * 报文数字签名标志(1：有数字签名 0：无数字签名 )
	 */
	private String SignFlag;
	/**
	 * 证书标识串(CA系统颁发的证书的标识串)
	 */
	private String CerID;
	/**
	 * 签名值(有签名时必填。内容经Base64编码（原始值为128位，含不可显示字符）)
	 */
	private String SignValue;

	public String getSignFlag() {
		return SignFlag;
	}

	public void setSignFlag(String signFlag) {
		SignFlag = signFlag;
	}

	public String getCerID() {
		return CerID;
	}

	public void setCerID(String cerID) {
		CerID = cerID;
	}

	public String getSignValue() {
		return SignValue;
	}

	public void setSignValue(String signValue) {
		SignValue = signValue;
	}

}
