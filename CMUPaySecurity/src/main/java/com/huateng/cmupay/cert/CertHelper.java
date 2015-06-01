package com.huateng.cmupay.cert;

/**
 * <p>
 * Title: 数字证书接口
 * </p>
 * <p>
 * Description:数字证书操作助手接口，提供签名和验签功能
 * </p>
 * 
 * @author Gary
 */
public interface CertHelper extends Signer, SignVerifier {
	/**
	 * 获取证书信息
	 * @return
	 * @throws Exception
	 */
	public CertInfo getCertInfo() throws Exception;
	/**
	 * 用私钥进行加密
	 * 
	 * @param data
	 *            要加密的明文
	 * @return 返回加密后的密文
	 * @throws Exception
	 */
	public byte[] encryptByPrivateKey(byte[] data) throws Exception;

	/**
	 * 使用公钥进行解密
	 * 
	 * @param data
	 *            要解密的数据
	 * @return 返回解密后的明文
	 * @throws Exception
	 */
	public byte[] decryptByPublicKey(byte[] data) throws Exception;
	
	/**
	 * 验证Certificate是否过期或无效
	 * @param certId 获取证书序列号
	 * @param key 传输的银行编号或者商户编号
	 * @return
	 */
	public boolean verifyCertificate();
}
