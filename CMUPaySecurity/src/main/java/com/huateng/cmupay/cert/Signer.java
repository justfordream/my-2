package com.huateng.cmupay.cert;

import java.security.SignatureException;

/**
 * <p>
 * Title: 签名接口
 * </p>
 * <p>
 * Description:提供签名功能
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

public interface Signer {
	
	/**
	 * 对<code>plainText</code>进行签名
	 * <p>
	 *  注意：
	 * <ul>
	 * 1. 签名所用私钥、算法及Provider等在具体实现中指定
	 * </ul>
	 * <ul>
	 * 2. <code>plainText</code>和返回的签名为字符串类型，编码所用character set由JVM的系统属性file.encoding指定
	 * </ul>
	 * <p>
	 * @param plainText 待签名的明文
	 * @return 签名结果
	 * @throws SignatureException
	 */
	public  String sign(String plainText) throws SignatureException;

}

