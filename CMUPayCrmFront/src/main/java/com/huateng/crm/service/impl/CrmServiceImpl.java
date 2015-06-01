package com.huateng.crm.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.crm.logFormat.MessageLogger;
import com.huateng.cmupay.security.adapter.SecurityHandle;
import com.huateng.core.base.BaseServiceImpl;
import com.huateng.core.common.CoreCommon;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.UpayCommon;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.DateUtil;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.common.parse.Dom4jXMLParser;
import com.huateng.crm.service.CrmService;
import com.huateng.log.LogHandle;

/**
 * CRM服务处理类
 * 
 * @author Gary
 * 
 */
public class CrmServiceImpl extends BaseServiceImpl implements CrmService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private MessageLogger log = MessageLogger.getLogger(getClass());
	
	//@Autowired
	private LogHandle logHandle;
	
	/**
	 * 加密开关
	 */
	private @Value("${encrypt.switch}") String encryptSwitch;
	/**
	 * 解密开关
	 */
	private @Value("${decrypt.switch}") String decryptSwitch;
	
	/**
	 * 加密交易代码
	 */
	private @Value("${encrypt.tradecode}") String encryptTradeCodes;
	/**
	 * 解密交易代码
	 */
	private @Value("${decrypt.tradecode}") String decryptTradeCodes;
	
	/**
	 * 加密操作的省份代码
	 */
	private @Value("${encrypt.province}") String encryptProvinces;
	/**
	 * 解密操作的省份代码
	 */
	private @Value("${decrypt.province}") String decryptProvinces;
	
	
	public void setLogHandle(LogHandle logHandle) {
		this.logHandle = logHandle;
	}

	@Autowired
	private SecurityHandle SecurityHandle;

	@Override
	public InterBOSS assemblyXmlContent(String client, String xmlHead, String xmlBody) throws ServiceException {
		if (StringUtils.isBlank(xmlHead)) {
			throw new ServiceException("UPAY-C-2998");// 失败
		}
		if (StringUtils.isBlank(xmlBody)) {
			throw new ServiceException("UPAY-C-5A05");// 解析报文失败
		}
		InterBOSS boss = Dom4jXMLParser.readXML(xmlHead, xmlBody);
		/*
		 * 解密
		 */
		// String encryptXmlBody = CrmXMLParser.parseEncryptXmlBody(xmlBody);
		String encryptBodyText = boss.getSvcCont();
		String decryptBodyText = this.decryptXmlBody(client, encryptBodyText,(boss.getSNReserve()==null?"":boss.getSNReserve().getMsgSender()),(boss.getBIPType()==null?"":boss.getBIPType().getBIPCode()),(boss.getBIPType()==null?"":boss.getBIPType().getActivityCode()));
		boss.setSvcCont(decryptBodyText);
		if (StringUtils.isBlank(xmlBody)) {
			throw new ServiceException("UPAY-C-2998");// 失败
		}
		try {
			// InterBOSS boss = CrmXMLParser.parseHeadXML(xmlHead);
			/*
			 * 全网监控日志
			 */

//			this.logHandle.info(false, CoreConstant.ErrorCode.SUCCESS.getCode(), boss.getTransInfo().getTransIDO(),
//					boss.getBIPType().getActivityCode(), UpayCommon.getTransDesc(boss.getBIPType().getActivityCode()),
//					UpayCommon.getProvCodeBySysCode(boss.getRoutingInfo().getOrigDomain()), boss.getRoutingInfo()
//							.getOrigDomain(), CoreConstant.ErrorCode.SUCCESS.getDesc());
			this.logHandle.info(false, CoreConstant.ErrorCode.SUCCESS.getCode(), boss.getTransInfo().getTransIDO(),
					boss.getBIPType().getActivityCode(), UpayCommon.getTransDesc(boss.getBIPType().getActivityCode()),
					UpayCommon.getProvCodeBySysCode(boss.getSNReserve().getMsgSender()), boss.getRoutingInfo()
							.getOrigDomain(), CoreConstant.ErrorCode.SUCCESS.getDesc());
			
			log.debug("交易代码：{}，发起方交易流水号：{}",
					new Object[]{boss.getBIPType().getActivityCode(), boss.getTransInfo().getTransIDO()});
			
			// String svcCont = CrmXMLParser.parseBodyXml(xmlBody);
			// boss.setSvcCont(svcCont);
			return boss;
		} catch (Exception e) {
			logger.error("",e);
			throw new ServiceException("UPAY-C-5A05");// 解析报文失败
		}
	}

	/**
	 * 对报文体解密
	 * 
	 * @param encryptText
	 *            报文体密文
	 * @return 明文
	 */
	@Override
	public String decryptXmlBody(String client, String encryptText,String provCode,String bipCode,String tradeCode) throws ServiceException {

		//String decryptSwitch = CoreCommon.getDecryptSwitch();
		String decryptText = encryptText;
		//if (CoreCommon.checkIsDecrypt(decryptSwitch, provCode,bipCode,tradeCode)) {
		if (CoreCommon.checkIsDecrypt(decryptSwitch, provCode,bipCode,tradeCode,decryptTradeCodes,decryptProvinces)) {
			logger.debug("......调用CRM前置解密方法......");
			//logger.info("......发起方:{},报文体密文:{},解密开关:{}......",new Object[]{client,encryptText,decryptSwitch});
			logger.debug("......发起方:{},解密开关:{}......",new Object[]{client,decryptSwitch});
			try {
				decryptText = this.SecurityHandle.decryptPIN(encryptText);
			} catch (Exception e) {
				logger.error("......解密失败,失败原因:{}",new Object[]{ e.getMessage()});
				log.error("报文解密失败");
				throw new ServiceException("UPAY-C-2998");
			}
			//logger.info("......发起方:{},报文体密文:{},解密后明文:{}......",new Object[]{client,encryptText,DateUtil.paseLog(decryptText)});
			logger.debug("......解密成功......");
			log.info("报文解密成功");
		}

		return decryptText;
	}

	@Override
	public String encryptXmlBody(String client, String decryptText,String provCode,String bipCode,String tradeCode) throws ServiceException {

		//String encryptSwitch = CoreCommon.getEncryptSwitch();
		String encryptText = decryptText;

		//if (CoreCommon.checkIsEncrypt(encryptSwitch, provCode,bipCode,tradeCode)) {
		if (CoreCommon.checkIsEncrypt(encryptSwitch, provCode,bipCode,tradeCode,encryptTradeCodes,encryptProvinces)) {
			logger.debug("......调用CRM前置加密方法......");
			//logger.info("......落地方:{},报文体明文:{},加密开关:{}......",new Object[]{client,DateUtil.paseLog(decryptText),encryptSwitch});
			logger.debug("......落地方:{},加密开关:{}......",new Object[]{client,encryptSwitch});
			try {
				encryptText = this.SecurityHandle.encryptPIN(decryptText);
			} catch (Exception e) {
				logger.error("......加密失败,失败原因：" + e.getMessage());
				log.error("报文加密失败");
				throw new ServiceException("UPAY-C-2998");
			}
			//logger.info("......落地方:{},报文体密文:{},加密前的明文:{}......",new Object[]{client,encryptText,DateUtil.paseLog(decryptText)});
			logger.debug("......加密成功......");
			log.info("报文加密成功");
		}
		return encryptText;
	}

	public String getEncryptSwitch() {
		return encryptSwitch;
	}

	public void setEncryptSwitch(String encryptSwitch) {
		this.encryptSwitch = encryptSwitch;
	}

	public String getDecryptSwitch() {
		return decryptSwitch;
	}

	public void setDecryptSwitch(String decryptSwitch) {
		this.decryptSwitch = decryptSwitch;
	}

	public String getEncryptTradeCodes() {
		return encryptTradeCodes;
	}

	public void setEncryptTradeCodes(String encryptTradeCodes) {
		this.encryptTradeCodes = encryptTradeCodes;
	}

	public String getDecryptTradeCodes() {
		return decryptTradeCodes;
	}

	public void setDecryptTradeCodes(String decryptTradeCodes) {
		this.decryptTradeCodes = decryptTradeCodes;
	}

	public String getEncryptProvinces() {
		return encryptProvinces;
	}

	public void setEncryptProvinces(String encryptProvinces) {
		this.encryptProvinces = encryptProvinces;
	}

	public String getDecryptProvinces() {
		return decryptProvinces;
	}

	public void setDecryptProvinces(String decryptProvinces) {
		this.decryptProvinces = decryptProvinces;
	}	
}
