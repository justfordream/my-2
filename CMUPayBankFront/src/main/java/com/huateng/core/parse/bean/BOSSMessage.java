package com.huateng.core.parse.bean;

/**
 * 请求报文bean
 * 
 * @author Gary
 * 
 */
public class BOSSMessage {
	/**
	 * 请求头
	 */
	private String xmlhead;
	/**
	 * 请求体
	 */
	private String xmlbody;
	/**
	 * 附件
	 */
	private String attachfile;

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

	public String getAttachfile() {
		return attachfile;
	}

	public void setAttachfile(String attachfile) {
		this.attachfile = attachfile;
	}

}
