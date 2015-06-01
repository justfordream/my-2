package com.huateng.cmupay.cert;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import ccit.security.bssp.CAUtility;
import ccit.security.bssp.bean.CERT_INFO;
import ccit.security.bssp.bean.DecP7SignOutInfo;
import ccit.security.bssp.bean.RSAKeyPair;
import ccit.security.bssp.ex.CCITSecurityException;
import ccit.security.bssp.ex.CrypException;

public class SecurityAPI {
	/**
	 * 生成RSA密钥对
	 * 
	 * @param modulusLen
	 *            密钥模长,bit数(支持512,1024,2048)
	 * @return RSAKeyPair
	 * @throws CrypException
	 */
	public static RSAKeyPair generateRSAKeyPair(int modulusLen) throws CrypException {
		return CAUtility.generateRSAKeyPair(modulusLen);
	}

	/**
	 * 摘要运算
	 * 
	 * @param src
	 *            ：要做摘要的数据
	 * @param algorithm
	 *            ：摘要算法名常数
	 * @throws CrypException
	 * @return：摘要值
	 */
	public static byte[] digest(byte src[], int algorithm) throws CrypException {
		return CAUtility.digest(src, algorithm);
	}

	/**
	 * 文件摘要运算
	 * 
	 * @param filePath
	 *            ：摘要文件的绝对路径
	 * @param algorithm
	 *            ：摘要算法名常数
	 * @throws CrypException
	 * @return：摘要值
	 */
	public static byte[] digest(String filePath, int algorithm) throws CrypException {
		return CAUtility.digest(filePath, algorithm);
	}

	/**
	 * 生成指定长度的随机ascii 字符串
	 * 
	 * @param length
	 *            :要生成的随机序列的长度
	 * @return 随机序列
	 * @throws CrypException
	 */
	public static String generateRandomAscii(int length) throws CCITSecurityException {
		return CAUtility.generateRandomAscii(length);
	}

	/**
	 * 生成指定长度的随机数字字符串
	 * 
	 * @param length
	 *            :要生成的随机序列的长度
	 * @return 随机序列
	 * @throws CrypException
	 */
	public static String generateRandomNumeric(int length) throws CCITSecurityException {
		return CAUtility.generateRandomNumeric(length);
	}

	/**
	 * 生成指定长度的随机字母字符串
	 * 
	 * @param length
	 *            :要生成的随机序列的长度
	 * @return 随机序列
	 * @throws CrypException
	 */
	public static String generateRandomAlphabetic(int length) throws CCITSecurityException {
		return CAUtility.generateRandomAlphabetic(length);
	}

	/**
	 * 生成指定长度的随机字母与数字混合字符串
	 * 
	 * @param length
	 *            :要生成的随机序列的长度
	 * @return 随机序列
	 * @throws CrypException
	 */
	public static String generateRandomAlphanumeric(int length) throws CCITSecurityException {
		return CAUtility.generateRandomAlphanumeric(length);
	}

	/**
	 * 签名
	 * 
	 * @param type
	 *            ：算法名常数
	 * @param privateKey
	 *            ：私有密钥字节数组（DER，Base64 格式均可）
	 * @param indata
	 *            ：要签名的数据
	 * @return ：签名后的数据 （DER 编码）
	 * @throws CrypException
	 */
	public static byte[] sign(int type, byte[] privateKey, byte[] indata) throws CrypException {
		return CAUtility.sign(type, privateKey, indata);
	}

	/**
	 * 需要做签名的数据，采用SHA1WITHRSA算法
	 * 
	 * @param indata
	 * @return 签名后的数据
	 * @throws SecurityException
	 */
	public byte[] signature(byte[] indata, PrivateKey priKey) throws SecurityException {
		return null;
	}

	/**
	 * 用公钥验证签名
	 * 
	 * @param type
	 *            - 签名算法
	 * @param publicKey
	 *            － 公钥数据 （DER，Base64 编码均可）
	 * @param indata
	 *            －签名原文（明文）
	 * @param sd
	 *            － 签名值（DER，Base64 编码均可）
	 * @return - 0 为成功
	 */
	public static int verify(int type, byte[] publicKey, byte[] indata, byte[] sd) throws CrypException {
		return CAUtility.verify(type, publicKey, indata, sd);
	}

	/**
	 * 用证书验证签名
	 * 
	 * @param type
	 *            :算法名常数
	 * @param cert
	 *            : 证书字节数组（DER，Base64 编码均可）
	 * @param indata
	 *            :签名原文(明文)
	 * @param sd
	 *            :签名后的数据（DER，Base64 编码均可）
	 * @return:0 为成功
	 * @throws CrypException
	 */
	public static int verifyWithCert(int type, byte[] cert, byte[] indata, byte[] sd) throws CrypException {
		return CAUtility.verifyWithCert(type, cert, indata, sd);
	}

	/**
	 * 验证签名
	 * 
	 * @param pubKey
	 *            公钥
	 * @param signData
	 *            签名后的数据 Base64编码
	 * @param indata
	 *            明文数据
	 * @return 是否验证通过
	 * @throws SecurityException
	 */
	public boolean verifySignature(PublicKey pubKey, byte[] signData, byte[] indata) throws SecurityException {
		return true;
	}

	/**
	 * 验证签名
	 * 
	 * @param cert
	 *            证书
	 * @param signData
	 *            签名后的数据 Base64编码
	 * @param indata
	 *            明文数据
	 * @return 是否验证通过
	 * @throws SecurityException
	 */
	public boolean verifySignatureWithCert(X509Certificate cert, byte[] signData, byte[] indata) {
		return true;
	}

	/**
	 * 非对称 加密、解密
	 * 
	 * @param type
	 *            – 加密算法
	 * @param key
	 *            - 加密时是公钥（DER，Base64 编码均可），解密时是私钥（DER，Base64 编码均可）
	 * @bEncryption － true 为加密，false 为解密
	 * @inData － 加密时输入为要加密原文（明文）,解密时输入为密文（DER，Base64 编码均可）
	 * @return - outdata 加密时输出为密文(DER 编码),解密时输出为明文
	 */
	public static byte[] cryption(int type, byte[] key, boolean bEncryption, byte[] inData) throws CrypException {
		return CAUtility.cryption(type, key, bEncryption, inData);
	}

	/**
	 * 对称加密、解密
	 * 
	 * @param type
	 *            :加密算法
	 * @param key
	 *            ：密钥字节串
	 * @param bEncrypto
	 *            ：true 表示加密；false 表示解密
	 * @param inData
	 *            ：加密时输入为明文,解密时输入为密文（DER，Base64 编码均可）
	 * @param parameter
	 *            :对称加密因子，当加密模式为CBC 时，此参数有效,此参数必须为 octet string 类型
	 * @return 加密时输出为密文(DER 编码),解密时输出为明文
	 * @throws CrypException
	 */
	public static byte[] symCrypto(int type, byte[] key, boolean bEncrypto, byte[] inData, byte[] parameter)
			throws CrypException {
		return CAUtility.symCrypto(type, key, bEncrypto, inData, parameter);
	}

	/**
	 * 对原文前128字节进行对称加密
	 * 
	 * @param inData
	 *            ：原文
	 * @param symKey
	 *            ：对称密钥
	 * @param Algorithm
	 *            :算法
	 * @return
	 * @throws Exception
	 */
	public static byte[] symEncBlockFile(byte[] inData, String symKey, int Algorithm) throws Exception {
		return CAUtility.symEncBlockFile(inData, symKey, Algorithm);
	}

	/**
	 * 对symEncBlockFile方法加密的数据进行解密
	 * 
	 * @param fileContent
	 *            ：加密后的文件内容（字节数组）
	 * @param symKey
	 *            ：对称密钥
	 * @param Algorithm
	 *            ：对称加密算法
	 * @return 解密后的文件（字节数组）
	 * @throws CrypException
	 */
	public static byte[] symDncBlockFile(byte[] fileContent, String symKey, int Algorithm) throws CrypException {
		return CAUtility.symDncBlockFile(fileContent, symKey, Algorithm);
	}

	/**
	 * 使用根证书验证证书有效性
	 * 
	 * @param cert
	 *            - 待验证书（DER，Base64 编码均可）
	 * @param caCert
	 *            － 验证证书（DER，Base64 编码均可）
	 * @return - 0 成功； 1 失败
	 */
	public static int verifyCert(byte[] cert, byte[] caCert) throws CrypException {
		return CAUtility.verifyCert(cert, caCert);
	}

	/**
	 * 根据CRL 验证证书有效性
	 * 
	 * @param cert
	 * @param crl
	 * @return boolean:true 为证书有效，false 为证书无效 如果crl 和证书不是同一签发者， 任何证书都会被系统认为已吊销
	 * @throws CCITSecurityException
	 */
	public static boolean verifyCertificateByCrl(byte[] cert, byte[] crl) throws CCITSecurityException {
		return CAUtility.verifyCertificateByCrl(cert, crl);
	}

	/**
	 * 验证证书是否跟根证签发
	 * 
	 * @param userCert
	 *            用户证书
	 * @param rootCert
	 *            根证书
	 * @return 验证是否通过
	 * @throws SecurityException
	 */
	public boolean verifyCert(X509Certificate userCert, X509Certificate rootCert) throws SecurityException {
		return true;
	}

	/**
	 * 验证证书是否吊销
	 * 
	 * @param userCert
	 *            用户证书
	 * @param x509crl
	 *            CRL吊销列表
	 * @return 证书是否被吊销
	 */
	public boolean verifyCRL(X509Certificate userCert, X509CRL x509crl) {
		return true;
	}

	/**
	 * 使用证书封装数字信封
	 * 
	 * @param type
	 *            － 数字信封算法名;
	 * @param cert
	 *            － 证书（DER，Base64 编码均可）
	 * @param inData
	 *            － 明文
	 * @return outData － 数字信封（DER 编码）
	 * @throws CrypException
	 */
	public static byte[] sealEnvelopWithCert(int type, byte[] cert, byte[] inData) throws CrypException {
		return CAUtility.sealEnvelopWithCert(type, cert, inData);
	}

	/**
	 * 使用公钥封装数字信封
	 * 
	 * @param type
	 *            － 数字信封算法名;
	 * @param key
	 *            － 公钥（DER，Base64 编码均可）
	 * @param inData
	 *            － 明文
	 * @return outData －数字信封（DER 编码）
	 * @throws CrypException
	 */
	public static byte[] sealEnvelop(int type, byte[] key, byte[] inData) throws CrypException {
		return CAUtility.sealEnvelop(type, key, inData);
	}

	/**
	 * 解密数字信封
	 * 
	 * @param type
	 *            － 数字信封算法名;
	 * @param key
	 *            － 私钥（DER，Base64 编码均可）
	 * @param envelop
	 *            － 数字信封（DER，Base64 编码均可）
	 * @return inData － 明文
	 * @throws CrypException
	 */
	public static byte[] openEnvelop(int type, byte[] key, byte[] envelop) throws CrypException {
		return CAUtility.openEnvelop(type, key, envelop);
	}

	/**
	 * 证书解析
	 * 
	 * @param cert
	 *            － 证书（DER，Base64 编码均可）
	 * @return 返回值：CERT_INFO
	 * @throws CrypException
	 */
	public static CERT_INFO getCertInfo(byte[] cert) throws CrypException {
		return CAUtility.getCertInfo(cert);
	}

	/**
	 * 根据oid 获取证书私有扩展项的值
	 * 
	 * @param cert
	 *            :需要解析的证书（DER，Base64 编码均可）
	 * @param oid
	 *            :证书私有扩展项的oid
	 * @return ：证书扩展项的值
	 * @throws CrypException
	 */
	public static byte[] getCertExtInfoString(byte[] cert, String oid) throws CrypException {
		return CAUtility.getCertExtInfoString(cert, oid);
	}

	/**
	 * 得到公钥对象
	 * 
	 * @param pubkey
	 *            公钥字符 base64编码
	 * @return 公钥对象
	 * @throws SecurityException
	 */
	public PublicKey getPubKey(String pubkey) throws SecurityException {
		return null;
	}

	/**
	 * 根据字符串证书获取证书对象
	 * 
	 * @param cer
	 *            证书文件 base64编码
	 * @return 证书对象
	 * @throws SecurityException
	 */
	public X509Certificate getX509Cert(String cer) throws SecurityException {
		return null;
	}

	/**
	 * 获取KeyStore 对象
	 * 
	 * @param keyStorePath
	 *            jks文件私钥
	 * @param keyStorePass
	 *            keystore 密码
	 * @return KeyStore 对象
	 */
	public KeyStore getKeySore(String keyStorePath, String keyStorePass) {
		return null;
	}

	/**
	 * 根据别名获取公钥
	 * 
	 * @param alias
	 *            证书别名
	 * @return 公钥对象
	 */
	public PublicKey getPublicKey(String alias) {
		return null;
	}

	/**
	 * 获取私钥
	 * 
	 * @param alias
	 *            私钥别名
	 * @param pass
	 *            私钥保护密码
	 * @return 私钥对象
	 */
	public PrivateKey getPrivateKey(String alias, String pass) {
		return null;
	}

	/**
	 * 根据crl文件路径获取CRL对象
	 * 
	 * @param path
	 * @return CRL对象
	 */
	public X509CRL getX509crl(String path) {
		return null;
	}

	/**
	 * 根据别名获取证书对象
	 * 
	 * @param alias
	 * @return 证书对象
	 */
	public X509Certificate getCert(String alias) {
		return null;
	}

	/**
	 * mac 运算
	 * 
	 * @param algorithmType
	 *            :mac 算法常数
	 * @param key
	 *            ：mac 运算的密钥
	 * @param inData
	 *            ：需要进行mac 运算的数据
	 * @return：mac 运算输出数据
	 * @throws CCITSecurityException
	 */
	public static byte[] mac(int algorithmType, String key, byte[] inData) throws CCITSecurityException {
		return CAUtility.mac(algorithmType, key, inData);
	}

	/**
	 * base64 编码运算
	 * 
	 * @param inData
	 *            :需要base64 编码的数据
	 * @return：base64 编码后的数据
	 * @throws CrypException
	 */
	public static byte[] base64Encode(byte[] inData) throws CrypException {
		return CAUtility.base64Encode(inData);
	}

	/**
	 * base64 解码运算
	 * 
	 * @param inData
	 *            :需要base64 解码的数据
	 * @return：base64 解码后的数据
	 * @throws CrypException
	 */
	public static byte[] base64Decode(byte[] inData) throws CrypException {
		return CAUtility.base64Decode(inData);
	}

	/**
	 * 解密p7 签名包
	 * 
	 * @param derP7SignedData
	 *            :p7 签名包（DER 编码）
	 * @return:DecP7SignOutInfo
	 * @throws CCITSecurityException
	 */
	public static DecP7SignOutInfo decodeSign(byte[] derP7SignedData) throws CCITSecurityException {
		return CAUtility.decodeSign(derP7SignedData);
	}

	/**
	 * 编码不带签名的p7 数字信封
	 * 
	 * @param encCertificate
	 *            ：：接收者证书(DER 编码)
	 * @param symmAlgorithm
	 *            :对称算法参数（支持3DES 算法）
	 * @param inData
	 *            :原始数据
	 * @return:编码后的数据(DER 编码)
	 * @throws CCITSecurityException
	 */
	public static byte[] encodeEnvelopedData(byte[] encCertificate, int symmAlgorithm, byte[] inData)
			throws CCITSecurityException {
		return CAUtility.encodeEnvelopedData(encCertificate, symmAlgorithm, inData);
	}

	/**
	 * 编码不带签名的多人p7 数字信封
	 * 
	 * @param encCertificate
	 *            ：接收者证书列表(DER 编码)
	 * @param symmAlgorithm
	 *            :对称算法参数（支持3DES 算法）
	 * @param inData
	 *            :原始数据
	 * @return:编码后的数据(DER 编码)
	 * @throws CCITSecurityException
	 */
	public static byte[] encodeEnvelopWithEncCerts(List<byte[]> encCertificate, int symmAlgorithm, byte[] inData)
			throws CCITSecurityException {
		return CAUtility.encodeEnvelopWithEncCerts(encCertificate, symmAlgorithm, inData);
	}

	/**
	 * 编码带签名的p7 数字信封
	 * 
	 * @param encCertificate
	 *            ：：接收者证书(DER 编码)
	 * @param prikey
	 *            ：：私钥
	 * @param signerCertificate
	 *            ：：签名者证书(DER 编码)
	 * @param encCertificate
	 *            ：：接受者证书(DER 编码)
	 * @param algorithmType
	 *            :签名 算法参数
	 * @param symmAlgorithm
	 *            :对称算法
	 * @param inData
	 *            :原始数据
	 * @return:编码后的数据(DER 编码)
	 * @throws CCITSecurityException
	 */
	public static byte[] encodeSignedEnvelopWithEncCert(byte[] prikey, byte[] signerCertificate, int algorithmType,
			byte[] encCertificate, int symmAlgorithm, byte[] inData) throws CCITSecurityException {
		return CAUtility.encodeSignedEnvelopWithEncCert(prikey, signerCertificate, algorithmType, encCertificate,
				symmAlgorithm, inData);
	}

	/**
	 * 编码带签名的p7 多人数字信封
	 * 
	 * @param encCertificate
	 *            ：：接收者证书(DER 编码)
	 * @param prikey
	 *            ：：私钥
	 * @param signerCertificate
	 *            ：：签名者证书(DER 编码)
	 * @param encCertificate
	 *            ：：接受者证书(DER 编码)
	 * @param algorithmType
	 *            :签名 算法参数
	 * @param symmAlgorithm
	 *            :对称算法
	 * @param inData
	 *            :原始数据
	 * @return:编码后的数据(DER 编码)
	 * @throws CCITSecurityException
	 */
	public static byte[] encodeSignedEnvelopWithEncCerts(byte[] prikey, byte[] signerCertificate, int algorithmType,
			List<byte[]> encCertificate, int symmAlgorithm, byte[] inData) throws CCITSecurityException {
		return CAUtility.encodeSignedEnvelopWithEncCerts(prikey, signerCertificate, algorithmType, encCertificate,
				symmAlgorithm, inData);
	}

	/**
	 * 解密不带签名的P7 格式的数字信封
	 * 
	 * @param encCertificate
	 *            原证书 base64 编码
	 * @param derP7Data
	 *            编码后的不带签名的多人p7 数字信封 base64 编码
	 * @param priKey
	 *            证书私钥 base64 编码
	 * @return 解密后明文 base64 编码
	 * @throws CrypException
	 * @throws CCITSecurityException
	 */
	public static byte[] openEnvelopPkcs7(byte[] encCertificate, byte[] derP7Data, byte[] priKey)
			throws CCITSecurityException, CrypException {
		return CAUtility.openEnvelopPkcs7(encCertificate, derP7Data, priKey);
	}

	/**
	 * 使用私钥对数据进行P7 格式的签名 Parameters: type 签名算法 privateKey 私钥，base64 编码 inData
	 * 要签名的数据，base64 编码 cert 签名者证书，base64 编码 Returns: 带签名值的P7 数据，base64 编码
	 * 
	 * @throws CCITSecurityException
	 * @throws CMSException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static byte[] PKCS7Signature(int type, byte[] privateKey, byte[] inData, byte[] cert)
			throws CCITSecurityException {
		return CAUtility.PKCS7Signature(type, privateKey, inData, cert);
	}
}
