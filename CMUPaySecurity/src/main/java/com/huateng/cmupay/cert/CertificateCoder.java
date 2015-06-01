package com.huateng.cmupay.cert;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.Cipher;

import ccit.security.bssp.CAUtility;
import ccit.security.bssp.bean.CERT_INFO;

import com.huateng.cmupay.constant.CommonConstant;

/**
 * 证书组件
 * 
 * @author Gary
 * 
 */
public class CertificateCoder extends Coder {

	/**
	 * keytool -v -list -keystore 9711_keystore 
	 */
	
	/**
	 * 由KeyStore获得私钥
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static PrivateKey getPrivateKey(String keyStorePath, String alias, String keyStorePwd, String keyPwd)
			throws Exception {
		KeyStore ks = getKeyStore(keyStorePath, keyStorePwd);
		PrivateKey key = (PrivateKey) ks.getKey(alias, keyPwd.toCharArray());
		return key;
	}

	/**
	 * 由Certificate获得公钥
	 * 
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	private static PublicKey getPublicKey(String certificatePath) throws Exception {
		Certificate certificate = getCertificate(certificatePath);
		PublicKey key = certificate.getPublicKey();
		return key;
	}

	/**
	 * 获得Certificate
	 * 
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	public static Certificate getCertificate(String certificatePath) throws Exception {
		CertificateFactory certificateFactory = CertificateFactory.getInstance(CommonConstant.X509);
		FileInputStream in = new FileInputStream(certificatePath);

		Certificate certificate = certificateFactory.generateCertificate(in);
		in.close();

		return certificate;
	}

	/**
	 * 获得Certificate
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static Certificate getCertificate(String keyStorePath, String alias, String password) throws Exception {
		KeyStore ks = getKeyStore(keyStorePath, password);
		Certificate certificate = ks.getCertificate(alias);

		return certificate;
	}

	/**
	 * 获得KeyStore
	 * 
	 * @param keyStorePath
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static KeyStore getKeyStore(String keyStorePath, String password) throws Exception {
		FileInputStream is = new FileInputStream(keyStorePath);
		KeyStore ks = KeyStore.getInstance(CommonConstant.KEY_STORE);
		ks.load(is, password.toCharArray());
		is.close();
		return ks;
	}

	/**
	 * 用私钥进行加密
	 * 
	 * @param data
	 *            要加密的明文
	 * @param keyStorePath
	 *            key store存放路径
	 * @param alias
	 *            别名
	 * @param keyStorePwd
	 *            密钥库密钥
	 * @param keyPwd
	 *            私钥证书密钥
	 * @return 返回加密后的密文
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath, String alias, String keyStorePwd,
			String keyPwd) throws Exception {
		// 取得私钥
		PrivateKey privateKey = getPrivateKey(keyStorePath, alias, keyStorePwd, keyPwd);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);

	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String keyStorePath, String alias, String keyStorePwd,
			String keyPwd) throws Exception {
		// 取得私钥
		PrivateKey privateKey = getPrivateKey(keyStorePath, alias, keyStorePwd, keyPwd);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		return cipher.doFinal(data);

	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String certificatePath) throws Exception {

		// 取得公钥
		PublicKey publicKey = getPublicKey(certificatePath);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		return cipher.doFinal(data);

	}

	/**
	 * 使用公钥进行解密
	 * 
	 * @param data
	 *            要解密的数据
	 * @param certificatePath
	 *            公钥证书路径
	 * @return 返回解密后的明文
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String certificatePath) throws Exception {
		// 取得公钥
		PublicKey publicKey = getPublicKey(certificatePath);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);

		return cipher.doFinal(data);

	}

	/**
	 * 验证Certificate
	 * 
	 * @param certificatePath
	 * @return
	 */
	public static boolean verifyCertificate(String certificatePath) {
		return verifyCertificate(new Date(), certificatePath);
	}

	/**
	 * 验证Certificate是否过期或无效
	 * 
	 * @param date
	 * @param certificatePath
	 * @return
	 */
	public static boolean verifyCertificate(Date date, String certificatePath) {
		boolean status = true;
		try {
			// 取得证书
			Certificate certificate = getCertificate(certificatePath);
			// 验证证书是否过期或无效
			status = verifyCertificate(date, certificate);
		} catch (Exception e) {
			status = false;
		}
		return status;
	}

	/**
	 * 验证证书是否过期或无效
	 * 
	 * @param date
	 * @param certificate
	 * @return
	 */
	private static boolean verifyCertificate(Date date, Certificate certificate) {
		boolean status = true;
		try {
			X509Certificate x509Certificate = (X509Certificate) certificate;
			x509Certificate.checkValidity(date);
		} catch (Exception e) {
			status = false;
		}
		return status;
	}

	/**
	 * 签名
	 * 
	 * @param sign
	 *            源串
	 * @param keyStorePath
	 *            密钥库路径
	 * @param alias
	 *            密钥库别名
	 * @param keyStorePwd
	 *            密钥库密码
	 * @param keyPwd
	 *            私钥证书密钥
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] sign, String keyStorePath, String alias, String keyStorePwd, String keyPwd)
			throws Exception {
		// 获取私钥
		KeyStore ks = getKeyStore(keyStorePath, keyStorePwd);
		// 取得私钥
		PrivateKey privateKey = (PrivateKey) ks.getKey(alias, keyPwd.toCharArray());
		// 构建签名
		Signature signature = Signature.getInstance(CommonConstant.SIGNATURE_METHOD);
		signature.initSign(privateKey);
		signature.update(sign);
		byte[] b = signature.sign();
		byte[] newB = SecurityAPI.base64Encode(b);
		return new String(newB, CommonConstant.ENCODING);
	}

	/**
	 * 验证签名
	 * 
	 * @param data
	 *            源串
	 * @param sign
	 *            签名串
	 * @param certificatePath
	 *            公钥证书路径
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, String sign, String certificatePath) throws Exception {
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);
		// 获得公钥
		PublicKey publicKey = x509Certificate.getPublicKey();
		// 构建签名
		Signature signature = Signature.getInstance(CommonConstant.SIGNATURE_METHOD);
		signature.initVerify(publicKey);
		signature.update(data);
		byte[] b = SecurityAPI.base64Decode(sign.getBytes(CommonConstant.ENCODING));
		return signature.verify(b);

	}
	

	/**
	 * 验证Certificate
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 */
	public static boolean verifyCertificate(Date date, String keyStorePath, String alias, String password) {
		boolean status = true;
		try {
			Certificate certificate = getCertificate(keyStorePath, alias, password);
			status = verifyCertificate(date, certificate);
		} catch (Exception e) {
			status = false;
		}
		return status;
	}

	/**
	 * 验证Certificate
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 */
	public static boolean verifyCertificate(String keyStorePath, String alias, String password) {
		return verifyCertificate(new Date(), keyStorePath, alias, password);
	}

	/**
	 * 获取证书信息
	 * 
	 * @param certificatePath
	 *            公钥证书路径
	 * @return 返回证书信息
	 * @throws Exception
	 */
	public static CertInfo getCertInfo(String certificatePath) throws Exception {
		CertInfo info = new CertInfo();
		// 获得证书
		CERT_INFO certinfo = CAUtility.getCertInfo(CertificateCoder.getCertificate(certificatePath).getEncoded());
		String sn = certinfo.getSerialNumber();
		info.setSerialNumber(sn.toUpperCase());
		return info;
	}

	public static void main(String[] args) throws UnsupportedEncodingException, Exception {
		
		String plainText ="<Header><ActivityCode>012001</ActivityCode>                          <ReqSys>0051</ReqSys><ReqChannel>81</ReqChannel><ReqDate>20130924</ReqDate><ReqTransID>010012201309241737557655816022</ReqTransID><ReqDateTime>20130924023755765</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0001</RcvSys></Header>|<Body><IDType>01</IDType> <IDValue>13473390543</IDValue> <Payed>1</Payed></Body>";
        String singure =null;
		singure = CertificateCoder.sign(plainText.getBytes("UTF-8"), "D:/work/CMUPay/code_/CMUPaySecurity/trunk/CMUPaySecurity/src/main/resources/cert/cmsz_keystore", "cmszcs", "changeit", "changeit");
        boolean result = CertificateCoder.verify(plainText.getBytes("UTF-8"), singure, "D:/work/CC/huangys_s_upay_dev_2/cvob_upay/c_upay_code/CMUPaySecurity/src/main/resources/cert/cmszTest.cer");
		
		System.out.println("=="+result);
		
	}
}
