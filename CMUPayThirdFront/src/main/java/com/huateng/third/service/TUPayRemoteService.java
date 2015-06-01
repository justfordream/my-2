package com.huateng.third.service;

import java.util.Map;

/**
 * 银联提供的对外签名验签的接口
 * @author hys
 *
 */
public interface TUPayRemoteService {

	/**
	 * 银联的签名接口
	 * @param certId
	 * @param signDate
	 * @return
	 */
	public Map<String,String> sign(String certId,Map<String,String> signDate);
	
	/**
	 * 银联的验签接口
	 * @param certId
	 * @param verifyDate
	 * @return
	 */
	public boolean verify(String certId,Map<String,String> verifyDate);
	
	/**
	 * 将Map形式的表单交易数据转换为key1=value1&key2=value2的形式
	 * @param data
	 * @return
	 */
	public  String coverMap2String(Map<String, String> data);
	
	/**
	 * 将形如key=value&key=value的字符串转换为相应的Map对象
	 * @param res
	 * @return
	 */
    public  Map<String, String> coverResultString2Map(String res);
    
    /**
     * 密码加密，输入参数依次为卡号、密码、字符集。
     * @param card
     * @param pwd
     * @param encoding
     * @return
     */
    public  String encryptPin(String card, String pwd, String encoding);

	/**
	 * 获取签名的证书ID
	 * @return
	 */
	public String getSignCertId();
	
	/**
	 * 获取加解密的证书ID
	 * @return
	 */
	public String getEncryptCertId();
	
	/**
	 * 财付通签名接口方法
	 * return Map	
	 */ 
	public Map<String, String> tenPaySign(Map<String, String> tenpaysignDate);
	
	/**
	 * 财付通验签接口方法
	 * return Map	
	 */ 
	public boolean tenPayVerify(Map<String, String> tenpaysignDate);
	
	/**
	 * 支付宝签名接口方法
	 * return Map	
	 */ 
	public Map<String, String> alipaySign(Map<String, String> tenpaysignDate);
	
	/**
	 * 支付宝验签接口方法
	 * return Map	
	 */ 
	public boolean alipayVerify(Map<String, String> tenpaysignDate);
}
