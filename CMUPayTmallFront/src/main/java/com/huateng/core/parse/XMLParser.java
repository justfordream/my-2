package com.huateng.core.parse;

/**
 * 请求报文接口
 * 
 * @author Gary
 * 
 */
public interface XMLParser {
	/**
	 * 组合请求报文信息
	 * 
	 * @return
	 */
	public String createMessage();

	/**
	 * 获取报文头
	 * 
	 * @return
	 */
	public String getXmlHead();

	/**
	 * 获取报文体
	 * 
	 * @return
	 */
	public String getXmlBody();

	/**
	 * 获取附件
	 * 
	 * @return
	 */
	public String getAttachfile();
}
