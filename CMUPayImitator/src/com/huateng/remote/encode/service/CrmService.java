package com.huateng.remote.encode.service;

/**
 * CRM 处理服务类
 * 
 * @author zeng.j
 * 
 */
public interface CrmService {
	/**
	 * 对报文体加密
	 * 
	 * @param decryptText
	 *            明文
	 * @return 密文
	 */
	public String encryptXmlBody(String client, String decryptText,
			String provCode, String tradeCode) throws Exception;

	/**
	 * 对报文体解密
	 * 
	 * @param encryptText
	 *            报文体密文
	 * @return 明文
	 */
	public String decryptXmlBody(String client, String encryptText,
			String provCode, String tradeCode) throws Exception;

}
