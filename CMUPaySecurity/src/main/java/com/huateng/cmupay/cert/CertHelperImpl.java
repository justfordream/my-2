package com.huateng.cmupay.cert;

import java.security.SignatureException;

import ccit.security.bssp.CAUtility;
import ccit.security.bssp.bean.CERT_INFO;

import com.huateng.cmupay.constant.CommonConstant;

/**
 * 证书验证实现类
 * <p>
 * 流程分析： 1.甲方构建密钥对儿，将公钥公布给乙方，将私钥保留。 <br>
 * 2.甲方使用私钥加密数据，然后用私钥对加密后的数据签名，发送给乙方签名以及加密后的数据；乙方使用公钥、签名来验证待解密数据是否有效，
 * 如果有效使用公钥对数据解密。 <br>
 * 3.乙方使用公钥加密数据，向甲方发送经过加密后的数据；甲方获得加密数据，通过私钥解密。 <br>
 * </p>
 * 
 * @author Gary
 * 
 */
public class CertHelperImpl implements CertHelper {
	/**
	 * 密钥库密钥
	 */
	private String keyStorePwd;
	/**
	 * 私钥证书密钥
	 */
	private String keyPwd;
	/**
	 * 别名
	 */
	private String alias;
	/**
	 * 公钥证书
	 */
	private String certificatePath;
	/**
	 * 私钥存放的keyStore位置
	 */
	private String keyStorePath;

	@Override
	public CertInfo getCertInfo() throws Exception {
		try {
			CERT_INFO certinfo = CAUtility.getCertInfo(CertificateCoder.getCertificate(certificatePath).getEncoded());
			String sn = certinfo.getSerialNumber();
			CertInfo info = new CertInfo();
			info.setSerialNumber(sn.toUpperCase());
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	/**
	 * 签名
	 */
	@Override
	public String sign(String plainText) throws SignatureException {
		try {
			byte[] data = plainText.getBytes(CommonConstant.ENCODING);
			// 产生签名
			String sign = CertificateCoder.sign(data, keyStorePath, alias, keyStorePwd, keyPwd);
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SignatureException(e);
		}
	}

	/**
	 * 验签
	 */
	@Override
	public boolean signVerify(String plainText, String signature) {
		try {
			byte[] data = plainText.getBytes(CommonConstant.ENCODING);
			
			// 验证签名
			boolean status = CertificateCoder.verify(data, signature, certificatePath);
			return status;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 用私钥进行加密
	 */
	@Override
	public byte[] encryptByPrivateKey(byte[] data) throws Exception {
		byte[] encodedData = CertificateCoder.encryptByPrivateKey(data, keyStorePath, alias, keyStorePwd, keyPwd);
		return encodedData;
	}

	/**
	 * 用公钥进行解密
	 */
	@Override
	public byte[] decryptByPublicKey(byte[] data) throws Exception {
		byte[] decodedData = CertificateCoder.decryptByPublicKey(data, certificatePath);
		return decodedData;
	}

	@Override
	public boolean verifyCertificate() {
		return CertificateCoder.verifyCertificate(certificatePath);
	}

	public void setKeyStorePwd(String keyStorePwd) {
		this.keyStorePwd = keyStorePwd;
	}

	public void setKeyPwd(String keyPwd) {
		this.keyPwd = keyPwd;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}

	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	public static void main(String[] args) {
		try {
			System.out.println(new CertHelperImpl().sign("11"));
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
