package com.huateng.cmupay.service.impl;

import java.security.SignatureException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.huateng.cmupay.cert.CertHelper;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.log.MessageLogger;
import com.huateng.cmupay.models.SecurityUtil;
import com.huateng.cmupay.service.RemoteService;
import com.toolbox.util.StringUtil;

/**
 * 银行远程签名和验签实现类
 * 
 * @author Gary
 * 
 */
public class BankRemoteServiceImpl implements RemoteService {
	private static Logger logger = LoggerFactory.getLogger(BankRemoteServiceImpl.class);
	private MessageLogger upayLog=MessageLogger.getLogger(BankRemoteServiceImpl.class);

	
	private Map<String, Map<String, CertHelper>> certMaps;
	
	/**
	 * 发送到银行端的签名值
	 */
	private String ccb_send_pay_key;
    
	/**
	 * 接收银行端的验签
	 */
	private String ccb_back_pay_key;
	/**
	 * 银行端的验签开关
	 */
	private String bank_test_flag;
	
	@Override
	public String getSendCCBPayKey() {
		return ccb_send_pay_key;
	}

	@Override
	public String getBackCCBPayKey() {
		return ccb_back_pay_key;
	}

	/**
	 * 银行签名接口
	 * 
	 * @param bankId
	 *            银行编号
	 * @parma plainText 原字符串
	 * @return 成功 签名串<br>
	 *         失败 null
	 */
	@Override
	public String sign(String bankId, String plainText) {
		logger.debug("调用验签服务器的签名方法,发起方[{}],原文[{}]",bankId,StringUtil.paseLog(plainText));
		String result = null;
		try {
			CertHelper certHelper=SecurityUtil.getBankTestFlag(certMaps, bankId,bank_test_flag);
			result = certHelper.sign(plainText);
			if(StringUtils.isBlank(result)){
				logger.error("发起方{}签名失败,原文{}",bankId,StringUtil.paseLog(plainText));
				upayLog.error("发起方{}签名失败，原文{}",bankId,StringUtil.paseLog(plainText));
			}else{
				upayLog.info("发起方{}签名成功,原文{},结果{}",new Object[]{bankId,StringUtil.paseLog(plainText),result});
				logger.info("发起方{}签名成功,原文{},结果{}",new Object[]{bankId,StringUtil.paseLog(plainText),result});
			}
		} catch (SignatureException e) {
			logger.error("发起方{}签名失败{},失败原因[{}]",new Object[]{bankId,StringUtil.paseLog(plainText),e.getMessage()});
			upayLog.error("发起方{}签名失败{},失败原因[{}]",new Object[]{bankId,StringUtil.paseLog(plainText),e.getMessage()});
			result = null;
		}
		return result;
	}

	/**
	 * 银行验签接口
	 * 
	 * @param bankId银行编号
	 * @param plainText原字符串
	 * @param signature已签名字符串
	 */
	@Override
	public boolean verify(String bankId, String plainText, String signature) {
		logger.debug("调用验签服务器的验签方法,发起方[{}],原文[{}],签名串[{}]",new Object[]{bankId,StringUtil.paseLog(plainText),signature});
		boolean result = false;
		try {
			CertHelper certHelper=SecurityUtil.getBankTestFlag(certMaps, bankId,bank_test_flag);
			result = certHelper.signVerify(plainText, signature);
			if(result){
				logger.info("银行{}验签成功,原文[{}]" ,new Object[]{bankId,StringUtil.paseLog(plainText)});
			}else{
				logger.error("银行{}验签失败,原文[{}],签名串[{}]",new Object[]{bankId,StringUtil.paseLog(plainText),signature});
				upayLog.error("银行{}验签失败,原文[{}],签名串[{}]",new Object[]{bankId,StringUtil.paseLog(plainText),signature});
			}
		} catch (Exception e) {
			logger.error("银行{}验签失败,原文{}签名串{},失败原因[{}]",new Object[]{bankId,StringUtil.paseLog(plainText),signature,e.getMessage()});
			upayLog.error("银行{}验签失败,原文{}签名串{},失败原因[{}]",new Object[]{bankId,StringUtil.paseLog(plainText),signature,e.getMessage()});
			result = false;
		}
		return result;
	}

	@Override
	public String getCertId() {
		String certId = null;
		try {
			Map<String, CertHelper> helperObj = certMaps.get(CommonConstant.CMU);
			CertHelper certHelper = helperObj.get(CommonConstant.DEFAULT);
			certId = certHelper.getCertInfo().getSerialNumber();
			logger.info(".获取证书编号成功,证书编号[{}]",certId);
		} catch (Exception e) {
			logger.error("获取证书编号失败,失败原因[{}]",e.getMessage());
			certId = null;
		}
		return certId;
	}

	@Override
	public boolean verifyCertificate(String certId, String bankId) {
		logger.info("验证Certificate有效性方法,验证[{}]的证书,证书编号[{}]......",bankId,certId);
		boolean result = false;
		try {
			CertHelper certHelper=SecurityUtil.getBankTestFlag(certMaps, bankId,bank_test_flag);
			result = certHelper.verifyCertificate();
			logger.info("验证Certificate有效性成功{}" , result);
		} catch (Exception e) {
			logger.error("验证Certificate有效性失败,失败原因[{}]",e.getMessage());
			result = false;
		}
		return result;
	}

	public void setCertMaps(Map<String, Map<String, CertHelper>> certMaps) {
		this.certMaps = certMaps;
	}


	public String getCcb_send_pay_key() {
		return ccb_send_pay_key;
	}

	public void setCcb_send_pay_key(String ccb_send_pay_key) {
		this.ccb_send_pay_key = ccb_send_pay_key;
	}

	public String getCcb_back_pay_key() {
		return ccb_back_pay_key;
	}

	public void setCcb_back_pay_key(String ccb_back_pay_key) {
		this.ccb_back_pay_key = ccb_back_pay_key;
	}

	public String getBank_test_flag() {
		return bank_test_flag;
	}

	public void setBank_test_flag(String bank_test_flag) {
		this.bank_test_flag = bank_test_flag;
	}
	
}
