package com.huateng.cmupay.cert;
/**
 * <p>
 * Title: 验签接口
 * </p>
 * <p>
 * Description:提供数字签名的验证功能
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: 上海华腾软件系统有限公司
 * </p>
 * 
 * @author 网关支付
 * @version 1.0
 */
public interface SignVerifier {
	
	/**
	 * 根据<code>plainText</code>和签名的证书，对<code>signature</code>进行验证
	 * <p>
	 *  注意：验签名所用证书、算法及Provider等在具体实现中指定
	 * <p>
	 * @param plainText 明文
	 * @param signature 待验签的签名
	 * @return <code>true</code> 如果验签通过，否则返回<code>false</code>
	 */
	public  boolean signVerify(String plainText, String signature);

}

