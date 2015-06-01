package com.huateng.crm.bean;

/**
 * CRM报文格式
 * 
 * @author Gary
 * 
 */
public class CrmMessage {
	/**
	 * 请求头
	 */
	private String xmlhead;
	/**
	 * 请求体
	 */
	private String xmlbody;

	public String getXmlhead() {
		return xmlhead;
	}

	public void setXmlhead(String xmlhead) {
		this.xmlhead = xmlhead;
	}

	public String getXmlbody() {
		return xmlbody;
	}

	public void setXmlbody(String xmlbody) {
		this.xmlbody = xmlbody;
	}

}
