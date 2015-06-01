package com.huateng.core.base;

import com.huateng.core.exception.ServiceException;


/**
 * 基类接口
 * 
 * @author Gary
 * 
 */
public interface BaseService {
	/**
	 * 报文内容
	 * 
	 * @param xmlContent
	 * @return
	 */
	public String sendMsg(String orgId,String client,String xmlContent) throws ServiceException;
	/**
	 * 对报文体加密
	 * 
	 * @param decryptText
	 *            明文
	 * @return 密文
	 */
	public  String encryptXmlBody(String client, String decryptText,String provCode,String bipCode,String tradeCode) throws ServiceException;

	/**
	 * 对报文体解密
	 * 
	 * @param encryptText
	 *            报文体密文
	 * @return 明文
	 */
	public  String decryptXmlBody(String client, String encryptText,String provCode,String bipCode,String tradeCode) throws ServiceException;
}
