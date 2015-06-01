package com.huateng.cmupay.security.adapter;

/**
 * 加解密接口
 * 
 * @author Gary
 * 
 */
public interface SecurityHandle {
	/**
	 * 安装加密密钥，然后命令配置服务器更新配置文件，执行新旧密钥的并存期处理。
	 * 
	 * @param encKey
	 *            交换中心返回的加密密钥的密文。
	 * @return 成功：返回0 <br>
	 *         失败：返回<0，(错误码待定)
	 */
	public int setKey(String encKey);

	/**
	 * 数据加密功能
	 * 
	 * @param pin
	 *            明文
	 * @return 成功： 返回加密的字符串，16进制编码的加密字符串 <br>
	 *         失败： 返回null
	 */
	public String encryptPIN(String pin);

	/**
	 * 解密数据功能
	 * 
	 * @param encpin
	 *            密文，16进制编码的加密字符串
	 * @return 成功：返回明文<br>
	 *         失败：返回NULL
	 */
	public String decryptPIN(String encpin);

	/**
	 * 生成摘要
	 * 
	 * @param srcData
	 *            原文
	 * @return 返回SHA 1算法加密的字符串，20个字节二进制SHA one编码，40字节16进制SHA-1编码<br>
	 *         失败： 返回null
	 */
	public String generateSha(String srcData);

	/**
	 * 验证摘要
	 * 
	 * @param srcData
	 *            原文
	 * @param macData
	 *            摘要，20个字节二进制SHA -1编码，40字节16进制SHA -1编码
	 * @return 成功：true<br>
	 *         失败：false
	 */
	public boolean isValidSha(String srcData, String macData);
}
