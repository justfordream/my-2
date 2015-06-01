package com.huateng.mmarket.bean;

/**
 * 天猫报文格式
 * 
 * @author Gary
 * 
 */
public class TMallRequestMessage implements Runnable{

	private String xmldata;

	public String getXmldata() {
		return xmldata;
	}

	public void setXmldata(String xmldata) {
		this.xmldata = xmldata;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
