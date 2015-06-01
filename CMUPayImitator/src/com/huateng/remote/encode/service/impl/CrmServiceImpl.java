package com.huateng.remote.encode.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import main.java.com.huateng.sendservlet.httpPostMsgMultipartServlet;
import main.java.com.huateng.util.StringUtils;

import com.huateng.bundle.PropertyBundle;
import com.huateng.constant.Common;
import com.huateng.remote.encode.service.CrmService;
import com.huateng.security.adapter.SecurityHandle;

/**
 * 加解密实现类
 * 
 * @author zeng.j
 * 
 */
@Service("crmService")
public class CrmServiceImpl implements CrmService {
	
	private final Logger log = Logger.getLogger(CrmServiceImpl.class);
	@Value("decodeFlag")
	private String decodeFlag;
	@Value("encode.province")
	private String decodeProvs;
	@Value("decode.tradecode")
	private String decodeTransCodes;
	@Autowired
	private SecurityHandle SecurityHandle;

	public CrmServiceImpl() {

	}

	@Override
	public String encryptXmlBody(String client, String decryptText,
			String provCode, String tradeCode) throws Exception {
		String encryptSwitch = PropertyBundle
				.getConfig(Common.ENCODE_SWITCH_KEY);
		String encryptText = decryptText;

		if (checkIsEncrypt(encryptSwitch, provCode, tradeCode)) {
			log.debug("......调用CRM前置加密方法......");
			log.debug("......落地方[" + client + "]报文体明文[" + decryptText + "],加密开关["
					+ encryptSwitch + "]......");
			try {
				encryptText = this.SecurityHandle.encryptPIN(decryptText);
			} catch (Exception e) {
				log.debug("......加密失败,失败原因：" + e.getMessage());
			}
			log.debug("......落地方[" + client + "]报文体明文[" + encryptText + "],加密后密文["
					+ decryptText + "]......");
		}
		return encryptText;
	}

	@Override
	public String decryptXmlBody(String client, String encryptText,
			String provCode, String tradeCode) throws Exception {
		String decryptSwitch = decodeFlag;
		String decryptText = encryptText;
		if (checkIsDecrypt(decryptSwitch, provCode, tradeCode)) {
			log.debug("......调用CRM前置解密方法......");
			log.debug("......发起方[" + client + "]报文体密文[" + encryptText + "],解密开关["
					+ decryptSwitch + "]......");
			try {
				decryptText = this.SecurityHandle.decryptPIN(encryptText);
			} catch (Exception e) {
				log.debug("......解密失败,失败原因：" + e.getMessage());
			}
			log.debug("......发起方[" + client + "]报文体密文[" + encryptText + "],解密后明文["
					+ decryptText + "]......");
		}

		return decryptText;
	}

	/**
	 * 加密
	 * 
	 * @param encryptSwitch
	 *            加密开关
	 * @param provCode
	 *            省机构编码
	 * @param tradeCode
	 *            交易编码
	 * @return
	 */
	private boolean checkIsEncrypt(String encryptSwitch, String provCode,
			String tradeCode) {
		boolean result = false;
		if (Common.ENCODE_OPEN.equals(encryptSwitch)) {
			result = isEncryptPermit(provCode);
		}
		if (result) {
			result = hasEncryptTradeCode(tradeCode);
		}
		return result;
	}


	/**
	 * 判断某省是否开放加密功能
	 * 
	 * @param provCode
	 *            省机构编码
	 * @return
	 */
	public boolean isEncryptPermit(String provCode) {
		String provs = PropertyBundle.getConfig("encrypt.province");
		if (StringUtils.isBlank(provs) || StringUtils.isBlank(provCode)) {
			return true;
		}
		if (provs.indexOf(provCode) != -1) {
			return true;
		}
		return false;
	}

	/**
	 * 判断某接口是否开发加密功能
	 * 
	 * @param tradeCode
	 * @return
	 */
	public boolean hasEncryptTradeCode(String tradeCode) {
		String tradeCodes = PropertyBundle.getConfig("encrypt.tradecode");
		if (StringUtils.isBlank(tradeCodes) || StringUtils.isBlank(tradeCode)) {
			return true;
		}
		if (tradeCodes.indexOf(tradeCode) != -1) {
			return true;
		}
		return false;
	}

	/**
	 * 解密
	 * 
	 * @param decryptSwitch
	 *            解密开关
	 * @param provCode
	 *            省机构编码
	 * @param tradeCode
	 *            交易编码
	 * @return
	 */
	private boolean checkIsDecrypt(String decryptSwitch, String provCode,
			String tradeCode) {
		boolean result = false;
		if (Common.DECODE_OPEN.equalsIgnoreCase(decryptSwitch)) {
			result = isDecryptPermit(provCode);
		}
		if (result) {
			result = hasDecryptTradeCode(tradeCode);
		}
		return result;
	}

	/**
	 * 判断某省是否开放解密功能
	 * 
	 * @param provCode
	 *            省机构编码
	 * @return
	 */
	private boolean isDecryptPermit(String provCode) {
		String provs = decodeProvs;
		if (StringUtils.isBlank(provs) || StringUtils.isBlank(provCode)) {
			return true;
		}
		if (provs.indexOf(provCode) != -1) {
			return true;
		}
		return false;
	}

	/**
	 * 判断某接口是否开发解密功能
	 * 
	 * @param tradeCode
	 * @return
	 */
	private boolean hasDecryptTradeCode(String tradeCode) {
		String tradeCodes = decodeTransCodes;
		if (StringUtils.isBlank(tradeCodes) || StringUtils.isBlank(tradeCode)) {
			return true;
		}
		if (tradeCodes.indexOf(tradeCode) != -1) {
			return true;
		}
		return false;
	}

}
