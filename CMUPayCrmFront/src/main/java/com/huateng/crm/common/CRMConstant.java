package com.huateng.crm.common;


/**
 * 公共静态类
 * 
 * @author Gary
 * 
 */
public class CRMConstant {
	/**
	 * 报文头参数名称
	 */
	public final static String XML_HEAD = "xmlhead";
	/**
	 * 报文体参数名称
	 */
	public final static String XML_BODY = "xmlbody";
	/**
	 * 附近参数名称
	 */
	public final static String ATTACH_FILE = "attachfile";
	
	public final static String XML_ROOT_ELEMENT_NAME = "InterBOSS";
	public final static String SVC_CONT_ELEMENT_NAME = "SvcCont";
	
	
	public final static	String INST_ID = UUIDGenerator.generateUUID().toString();
}
