package com.huateng.core.util;

import CCBSign.RSASig;
/**
 * 签名和验签工具类
 * 
 * @author Gary
 * 
 */
public class SignatureVerification {
	/**
	 * 签名
	 * 
	 * @param txnString
	 *            待签名的字符串
	 * @param orgId
	 *            机构编号
	 * @return
	 */
	public static String sign(String txnString, String orgId) {
		return null;
	}

	/**
	 * 验签
	 * 
	 * @param txnString
	 *            原字符串
	 * @param signString
	 *            签名字符串
	 * @param orgId
	 *            机构编号
	 * @return
	 */
	public static boolean verifySign(String txnString, String signString, String publicKey) {
		RSASig sig=new RSASig();
		sig.setPublicKey(publicKey);
		return sig.verifySigature(signString, txnString);
	}

}
