package com.huateng.cert.cmu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SignatureException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.payment.client.core.MerchantSignVerify;
import com.huateng.cert.CertHelper;

/**
 * <p>
 * 验签接口实现
 * </p>
 * 
 * @author Gary
 */
public class CertHelperShenZhenImpl implements CertHelper {

	/**
	 * 浦发银行配置文件路径
	 */
	private String shenzhenMerchantFilePath;
	
	private Log log = LogFactory.getLog(this.getClass());

	public CertHelperShenZhenImpl() {
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void init() throws FileNotFoundException, IOException {

	}

	/**
	 * 签名
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

	/**
	 * Verify signature 验签
	 */
	public boolean signVerify(String plainText, String signature) {
		try {
			boolean verifyResult = MerchantSignVerify.merchantVerifyPayGate_ABA(signature, plainText);
			if (!verifyResult) {// 验签出错
				String errMsg = "验证签名失败：return code:";
				log.error(errMsg);
				return false;
			}
		} catch (Exception e) {
			String errMsg = "验证签名失败！";
			log.error(errMsg);
			e.printStackTrace();
		}

		return true;
	}

	public String getShenzhenMerchantFilePath() {
		return shenzhenMerchantFilePath;
	}

	public void setShenzhenMerchantFilePath(String shenzhenMerchantFilePath) {
		this.shenzhenMerchantFilePath = shenzhenMerchantFilePath;
	}

}