package com.huateng.crm.bean;

/**
 * CRM请求报文格式
 * 
 * @author Gary
 * 
 */
public class CrmRequestMessage extends CrmMessage{
	
	/**
	 * 附件
	 */
	private String attachfile;

	
	public String getAttachfile() {
		return attachfile;
	}

	public void setAttachfile(String attachfile) {
		this.attachfile = attachfile;
	}

}
