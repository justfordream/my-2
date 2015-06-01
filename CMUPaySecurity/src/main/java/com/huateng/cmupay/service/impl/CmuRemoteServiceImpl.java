package com.huateng.cmupay.service.impl;

import java.security.SignatureException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.cmupay.cert.CertHelper;
import com.huateng.cmupay.log.MessageLogger;
import com.huateng.cmupay.models.SecurityUtil;
import com.huateng.cmupay.service.RemoteService;
import com.toolbox.util.StringUtil;

/**
 * 省网厅远程签名和验签实现类
 * 
 * @author Gary
 * 
 */
public class CmuRemoteServiceImpl implements RemoteService {
	private static Logger logger = LoggerFactory.getLogger(CmuRemoteServiceImpl.class);
	private MessageLogger upayLog=MessageLogger.getLogger(CmuRemoteServiceImpl.class);
	
	private Map<String, Map<String, CertHelper>> certMaps;
	/**
	 * 省份的验签开关
	 */
	private String cmu_test_flag;
	
	public String getCmu_test_flag() {
		return cmu_test_flag;
	}

	public void setCmu_test_flag(String cmu_test_flag) {
		this.cmu_test_flag = cmu_test_flag;
	}


	/**
	 * 签名接口
	 * 
	 * @param merId
	 *            商户编号
	 * @parma plainText 原字符串
	 * @return 成功 签名串<br>
	 *         失败 null
	 */
	@Override
	public String sign(String merId, String plainText) {
		logger.debug("调用验签服务器的签名方法,发起方[{}],原文[{}]",merId,StringUtil.paseLog(plainText));
		String result = null;
		try {
			CertHelper certHelper = SecurityUtil.getCmuTestFlag(certMaps, merId,cmu_test_flag);
			result = certHelper.sign(plainText);
			if(StringUtils.isNotBlank(result)){
				logger.info("发起方{}验签成功,签名结果{}",merId, result);
				upayLog.info("发起方{}验签成功,签名结果{}",merId, result);
			}else{
				logger.error("发起方{}验签失败,签名串{}",merId,StringUtil.paseLog(plainText));
				upayLog.error("发起方{}验签失败,签名串{}",merId,StringUtil.paseLog(plainText));
			}
		} catch (SignatureException e) {
			logger.error("发起方{}签名失败,{}失败原因[{}]",new Object[]{merId,StringUtil.paseLog(plainText),e.getMessage()});
			upayLog.error("发起方{}签名失败,{}失败原因[{}]",new Object[]{merId,StringUtil.paseLog(plainText),e.getMessage()});
			result = null;
		}
		return result;
	}

	/**
	 * 网厅验签接口
	 * 
	 * @param merId
	 *            商户编号
	 * @param plainText原字符串
	 * @param signature已签名字符串
	 */
	@Override
	public boolean verify(String merId, String plainText, String signature) {
		logger.debug("调用验签服务器的验签方法,发起方[{}],原串{},签名串[{}]",new Object[]{merId,StringUtil.paseLog(plainText),signature});
		boolean result = false;
		try {
			CertHelper certHelper = SecurityUtil.getCmuTestFlag(certMaps, merId,cmu_test_flag);
			result = certHelper.signVerify(plainText, signature);
			if(result){
				logger.info("发起方{} 验签成功",merId);
				upayLog.info("发起方{} 验签成功",merId);
			}else{
				logger.error("发起方{}验签失败,原串{},签名串[{}]",new Object[]{merId,StringUtil.paseLog(plainText),signature});
				upayLog.error("发起方{}验签失败,原串{},签名串[{}]",new Object[]{merId,StringUtil.paseLog(plainText),signature});
			}
		} catch (Exception e) {
			logger.error("发起方{}验签失败,原文{},失败原因[{}]",new Object[]{merId,StringUtil.paseLog(plainText),e.getMessage()});
			upayLog.error("发起方{}验签失败,原文{},失败原因{}",new Object[]{merId,StringUtil.paseLog(plainText), e.getMessage()});
			result = false;
		}
		return result;
	}

	@Override
	public String getCertId() {
		logger.debug("调用验签服务器的获取证书编号方法");
		String certId = null;
		try {
			CertHelper certHelper = SecurityUtil.getCmuTestFlag(certMaps, null,cmu_test_flag);
			certId = certHelper.getCertInfo().getSerialNumber();
			logger.debug("获取证书编号成功,证书编号[{}]",certId);
		} catch (Exception e) {
			logger.error("获取证书编号失败,失败原因[{}]",e.getMessage());
			upayLog.error("获取证书编号失败,失败原因[{}]",e.getMessage());
		}
		return certId;
	}

	@Override
	public boolean verifyCertificate(String certId, String cmuId) {
		logger.debug("调用验签服务器的验证Certificate有效性方法,验证[{}]的证书,证书编号[{}]",cmuId,certId);
		boolean result = false;
		try {
			CertHelper certHelper = SecurityUtil.getCmuTestFlag(certMaps, cmuId,cmu_test_flag);
			result = certHelper.verifyCertificate();
			logger.debug("验证Certificate有效性成功,结果{}",result);
		} catch (Exception e) {
			logger.error("验证Certificate有效性失败,失败原因[{}]",e.getMessage());
			upayLog.error("验证Certificate有效性失败,失败原因[{}]",e.getMessage());
			result = false;
		}
		return result;
	}

	public void setCertMaps(Map<String, Map<String, CertHelper>> certMaps) {
		this.certMaps = certMaps;
	}

	@Override
	public String getSendCCBPayKey() {
		return null;
	}

	@Override
	public String getBackCCBPayKey() {
		return null;
	}
}
