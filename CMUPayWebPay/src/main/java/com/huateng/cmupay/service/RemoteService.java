package com.huateng.cmupay.service;

/**
 * 远程调用签名和验签类
 * 
 * @author Gary
 * 
 */
public interface RemoteService {
	
	/**
	 * 获取建设银行的密钥
	 * @return
	 */
	public  String getSendCCBPayKey();
	/**
	 * 获取建设银行的验签密钥
	 */
	public String getBackCCBPayKey();
	/**
	 * 获取证书序列号
	 * 
	 * @return
	 */
	public String getCertId();

	/**
	 * 签名
	 * 
	 * @param key
	 *            传输的银行编号或者商户编号
	 * @param plainText
	 *            要签名的字符串
	 * @return
	 */
	public String sign(String key, String plainText);

	/**
	 * 验签
	 * 
	 * @param key
	 *            传输的银行编号或者商户编号
	 * @param plainText
	 *            要签名的字符串
	 * @param signature
	 *            已签名的字符串
	 * @return
	 */
	public boolean verify(String key, String plainText, String signature);
	/**
	 * 验证Certificate是否过期或无效
	 * @param certId 获取证书序列号
	 * @param key 传输的银行编号或者商户编号
	 * @return
	 */
	public boolean verifyCertificate(String certId,String key);
}
