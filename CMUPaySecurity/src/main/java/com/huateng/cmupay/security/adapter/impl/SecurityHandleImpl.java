package com.huateng.cmupay.security.adapter.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcc.boss.security.NodeSecurity;
import com.huateng.cmupay.log.MessageLogger;
import com.huateng.cmupay.security.adapter.SecurityHandle;
import com.toolbox.util.StringUtil;

/**
 * 加解密实现，通过调用加密机
 * 
 * @author Gary
 * 
 */
public class SecurityHandleImpl implements SecurityHandle {
	private static Logger logger = LoggerFactory.getLogger(SecurityHandleImpl.class);
	private MessageLogger upayLog = MessageLogger.getLogger(SecurityHandleImpl.class);
	/**
	 * 成功：返回0 <br>
	 * 失败：返回<0，(错误码待定)
	 */
	@Override
	public int setKey(String encKey) {
		logger.info("调用加密机的安装加密密钥方法,密钥[{}]", encKey);
		upayLog.info("调用加密机的安装加密密钥方法,密钥[{}]", encKey);
		int result = 0;
		try {
			result = NodeSecurity.setKey(encKey);
			logger.info("安装加密密钥成功{}", result);
			upayLog.info("安装加密密钥成功{}", result);
		} catch (Exception e) {
			logger.error("安装加密密钥失败,失败原因[{}]", e.getMessage());
			upayLog.error("安装加密密钥失败,失败原因[{}]", e.getMessage());
			result = -1;
		}
		return result;
	}

	@Override
	public String encryptPIN(String pin) {
		logger.debug("调用加密机的加密方法,明文[{}]", StringUtil.paseLog(pin));
		upayLog.debug("调用加密机的加密方法,明文[{}]",StringUtil.paseLog(pin));
		String result = null;
		try {
			result = NodeSecurity.encryptPIN(pin);
			if(StringUtils.isNotBlank(result)){
				logger.info("原文{}加密成功,结果{}",StringUtil.paseLog(pin),result);
			}else{
				logger.error("加密失败,明文[{}],结果{}", StringUtil.paseLog(pin),result);
				upayLog.error("加密失败,明文[{}],结果{}", StringUtil.paseLog(pin),result);
			}
		} catch (Exception e) {
			logger.error("加密失败,原文{},失败原因[{}]", StringUtil.paseLog(pin),e.getMessage());
			upayLog.error("加密失败,原文{},失败原因[{}]",StringUtil.paseLog(pin), e.getMessage());
		}
		return result;
	}

	@Override
	public String decryptPIN(String encpin) {
		logger.debug("调用加密机的解密方法,密文[{}]", encpin);
		upayLog.debug("调用加密机的解密方法,密文[{}]", encpin);
		String result = null;
		try {
			result = NodeSecurity.decryptPIN(encpin);
			if(StringUtils.isNotBlank(result)){
				logger.info("原文{}解密成功,结果{}",encpin,StringUtil.paseLog(result));
			}else{
				logger.error("原文{}解密失败,结果{}",encpin,StringUtil.paseLog(result));
				upayLog.error("原文{}解密失败,结果{}",encpin,StringUtil.paseLog(result));
			}
		} catch (Exception e) {
			logger.error("密文{}解密失败,失败原因[{}]",encpin, e.getMessage());
			upayLog.error("密文{}解密失败,失败原因[{}]", encpin,e.getMessage());
		}
		return result;
	}

	@Override
	public String generateSha(String srcData) {
		logger.debug("调用加密机的生成摘要方法,原文[{}]", srcData);
		upayLog.debug("调用加密机的生成摘要方法,原文[{}]", srcData);
		String result = null;
		try {
			result = NodeSecurity.generateSha(srcData);
			if(StringUtils.isNotBlank(result)){
				logger.info("原文{}生成摘要成功,摘要字符串[{}]", srcData,result);
			}else{
				logger.error("原文{}生成摘要失败,摘要字符串[{}]", srcData,result);
				upayLog.error("原文{}生成摘要失败,摘要字符串[{}]", srcData,result);
			}
		} catch (Exception e) {
			logger.error("原文{},生成摘要失败,失败原因[{}]", srcData,e.getMessage());
			upayLog.error("原文{},生成摘要失败,失败原因[{}]",srcData,e.getMessage());
		}
		return result;
	}

	@Override
	public boolean isValidSha(String srcData, String macData) {
		logger.debug("调用加密机的验证摘要方法,原文[{}],摘要[{}]", srcData, macData);
		boolean result = false;
		try {
			result = NodeSecurity.isValidSha(srcData, macData);
			if(result){
				logger.info("原文{},验证摘要成功{}",srcData, result);
			}else{
				logger.error("原文{},验证摘要失败{}",srcData, result);
				upayLog.error("原文{},验证摘要失败{}",srcData, result);
			}
		} catch (Exception e) {
			logger.error("原文{},验证摘要失败,失败原因[{}]", srcData,e.getMessage());
			result = false;
		}
		return result;
	}
}
