package com.huateng.cmupay.security.adapter;

/**
 * 银行端的加解密
 * @author Administrator
 *
 */
public interface BankSecurityHandle {

	/**
	 * 对称加密、解密
	 * @param type
	 *            : 0、建行；1、浦发
	 * @param bEncrypto
	 *            ：true表示加密；false表示解密
	 * @param inData
	 *            ：加密时输入为明文,解密时输入为密文
	 * @return 加密时输出为密文,解密时输出为明文
	 */
	public String symDecryptPNI(int type, boolean bEncrypto, String inData);
}
