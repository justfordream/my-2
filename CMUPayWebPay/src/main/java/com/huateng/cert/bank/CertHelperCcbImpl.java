package com.huateng.cert.bank;

import java.security.SignatureException;

import CCBSign.RSASig;

import com.huateng.cert.CertHelper;

/**
 * 建设银行签名和验签类
 * 
 * @author Gary
 * 
 */
public class CertHelperCcbImpl implements CertHelper {
	/**
	 * 签名
	 */
	public String sign(String plainText) throws SignatureException {

		return null;
	}

	/**
	 * 验签
	 */
	public boolean signVerify(String plainText, String signature) {
		RSASig sign = new RSASig();
		boolean result = sign.verifySigature(signature, plainText);
		return result;
	}

}
