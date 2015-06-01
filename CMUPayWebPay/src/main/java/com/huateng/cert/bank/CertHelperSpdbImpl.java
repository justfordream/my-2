/*
 * Copyright All rights reserved.
 * Use is subject to license terms.
 */ 
package com.huateng.cert.bank;
/**
 * <p>
 * Title: 验签接口实现
 * </p>
 * <p>
 * Description:浦发证书操作接口实现
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SignatureException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.payment.client.core.MerchantSignVerify;
import com.huateng.cert.CertHelper;

/**
 * <p></p>
 * @author xuxw
 * @version	    : 1.0
 * createDate	: 2008-6-16
 */
public class CertHelperSpdbImpl implements CertHelper {

	/**
	 * 浦发银行配置文件路径
	 */
	private String b2cMerchantFilePath;
	
	private Log log = LogFactory.getLog(this.getClass());

	public CertHelperSpdbImpl() {
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void init() throws FileNotFoundException, IOException {
		
	}

	/* (non-Javadoc)
	 * @see com.huateng.bank.cert.Signer#sign(java.lang.String, byte[], java.lang.String)
	 */
	public String sign(String plainText) throws SignatureException {
		try {
			
			String signMsg = MerchantSignVerify.merchantSignData_ABA(plainText);
		 	return signMsg;
			
		} catch (Exception e) {
			log.error("Sign Error!");
			e.printStackTrace();
			throw new SignatureException();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.huateng.bank.cert.SignVerifier#signVerify(java.lang.String, java.lang.String, byte[])
	 */
	/**
	 * Verify signature
	 */
	public boolean signVerify(String plainText, String signature) {
		try {
			boolean verifyResult = MerchantSignVerify.merchantVerifyPayGate_ABA(signature,plainText);
			if (!verifyResult){//验签出错
				String errMsg= "验证签名失败：return code:";
				log.error(errMsg);
				return false;
			}
		} catch (Exception e) {
			String errMsg= "验证签名失败！";
			log.error(errMsg);
			e.printStackTrace();
		}

		return true;	
	}


	public String getB2cMerchantFilePath() {
		return b2cMerchantFilePath;
	}

	public void setB2cMerchantFilePath(String merchantFilePath) {
		b2cMerchantFilePath = merchantFilePath;
	}
	
}