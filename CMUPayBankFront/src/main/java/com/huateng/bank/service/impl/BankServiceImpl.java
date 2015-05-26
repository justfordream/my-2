package com.huateng.bank.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bank.bean.head.GPay;
import com.huateng.bank.bean.head.Header;
import com.huateng.bank.bean.head.Sign;
import com.huateng.bank.common.parse.BankXMLParser;
import com.huateng.bank.logFormat.MessageLogger;
import com.huateng.bank.service.BankService;
import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.base.BaseServiceImpl;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.UpayCommon;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.DateUtil;
import com.huateng.log.LogHandle;

/**
 * 銀行消息發送實現類
 * 
 * @author Gary
 * 
 */
public class BankServiceImpl extends BaseServiceImpl implements BankService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger log = MessageLogger.getLogger(getClass());
	/**
	 * 验签服务
	 */
	@Autowired
	private RemoteService bankSecurityRemoting;
//	@Autowired
//	private BankSecurityHandle bankSecurityHandle;
	// @Autowired
	private LogHandle logHandle;
	
	/**
	 * 验签开关
	 */
	private @Value("${check.switch}") String checkSwitch;
	
	/**
	 * 签名开关
	 */
	private @Value("${check.switch}") String signSwitch;
	

	public void setLogHandle(LogHandle logHandle) {
		this.logHandle = logHandle;
	}

	@Override
	public String checkSign(String client, String xmlContent)
			throws ServiceException {
		if (StringUtils.isBlank(xmlContent)) {
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}
		//String checkSwitch = CoreCommon.getCheckSwitch();
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(checkSwitch)) {
			logger.debug("......调用Bank前置验签方法......");
			GPay gPay = null;
			try {
				gPay = BankXMLParser.parseXmlContent(xmlContent);
			} catch (Exception e) {
				throw new ServiceException("UPAY-B-015A05");// 解析报文失败
			}
			Header header = gPay.getHeader();
			if (header == null) {
				throw new ServiceException("UPAY-B-015A05");// 解析报文失败
			}
			Sign sign = gPay.getSign();
			if (sign == null) {
				throw new ServiceException("UPAY-B-019A16");// SignValue参数不正确
			}
			/*
			 * 全网监控日志
			 */
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					header.getReqTransID(), header.getActivityCode(),
					UpayCommon.getTransDesc(header.getActivityCode()),
					UpayCommon.getProvCodeBySysCode(header.getReqSys()),
					header.getReqSys(),
					CoreConstant.ErrorCode.SUCCESS.getDesc());
			log.debug("交易代码：{}，发送方交易流水号：{}",
					new Object[] { header.getActivityCode(),
							header.getReqTransID() });

			if (CoreConstant.SignFlag.YES.equals(sign.getSignFlag())) {
				String signText = BankXMLParser.parseBankSignXml(xmlContent);
				String bankId = "";
				if (CoreConstant.ActionCode.REQ.getCode().equals(
						header.getActionCode())) {
					bankId = header.getReqSys();
				} else if (CoreConstant.ActionCode.RES.getCode().equals(
						header.getActionCode())) {
					bankId = header.getRcvSys();
				}
				//logger.debug("......验签源串[" + signText + "]");
				boolean isValid = this.bankSecurityRemoting.verify(bankId,
						signText, sign.getSignValue());
				if (!isValid) {
//					logger.debug("......验签失败,源报文["
//							+ DateUtil.paseLog(xmlContent) + "],源签名串["
//							+ signText + "],签名串[" + sign.getSignValue() + "]");
					logger.error("......验签失败......");
					log.error("验签失败");
					throw new ServiceException("UPAY-B-014A06");// 签名验证失败
				}
//				logger.debug("......验签成功,源报文[" + DateUtil.paseLog(xmlContent)
//						+ "],源签名串[" + signText + "],签名串[" + sign.getSignValue()
//						+ "]");
				logger.debug("......验签成功......");
				log.info("验签成功");
				boolean isCertValid = this.bankSecurityRemoting
						.verifyCertificate(sign.getCerID(), bankId);
				if (!isCertValid) {
					logger.error("......验证证书无效.....");
					log.error("验证证书无效");
					throw new ServiceException("UPAY-B-014A07");// 签名验证失败
				}
			}
		}

		return xmlContent;
	}

	@Override
	public String sign(String client, String xmlContent)
			throws ServiceException {
		//String signSwitch = CoreCommon.getSignSwitch();
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch)) {
			logger.debug("......调用Bank前置签名方法......");
			String singure = BankXMLParser.parseBankSignXml(xmlContent);
			String certId = null;
			if (StringUtils.isNotBlank(singure)) {
				try {
					//logger.debug("......原签名串[" + (singure) + "]");
					singure = this.bankSecurityRemoting.sign(null, singure);
					certId = this.bankSecurityRemoting.getCertId();
					//logger.debug("......签名后的值[" + singure + "]");
					logger.debug("......签名成功,签名前报文{}",xmlContent);
					log.info("签名成功,签名前报文{}",DateUtil.paseLog(xmlContent));
				} catch (Exception e) {
					logger.error("",e);
					logger.error("......签名失败,签名前报文{}",xmlContent);
					log.error("签名失败,签名前报文{}",xmlContent);
				}
			}
			xmlContent = BankXMLParser.convertBankXml(singure, certId,
					xmlContent);
			logger.info("......签名成功,签名后报文{}",DateUtil.paseLog(xmlContent));
		}
		return xmlContent;
	}

	/**
	 * 解密文件
	 * 
	 * @param paseInfo
	 * @return
	 */
//	public String symPaseDncrypt(String xmlContent) {
//		GPay gPay = null;
//		if (StringUtils.isBlank(xmlContent))
//			return "";
//		String newXmlContent = xmlContent;
//		try {
//			gPay = BankXMLParser.parseXmlContent(xmlContent);
//		} catch (Exception e) {
//			logger.error("解密文件操作转换成XML 错误！{}", e.getMessage());
//		}
//		Header header = gPay.getHeader();
//		try {
//			if (BankXMLParser.isEncryptSwitch(header.getActivityCode())) {
//				StringBuffer sb = new StringBuffer(CoreConstant.START_INDEX);
//				sb.append(gPay.getBody());
//				sb.append(CoreConstant.END_INDEX);
//				com.huateng.bank.bean.userCheckbody.Body body = new com.huateng.bank.bean.userCheckbody.Body();
//				Document docBody = DocumentHelper.parseText(sb.toString());
//				Element rootBody = docBody.getRootElement();
//				BankXMLParser.settingElement(body, rootBody);
//				body.setUserID(paseSecurity(body.getUserID()));
//				body.setUserName(paseSecurity(body.getUserName()));
//				String newXmlbody = BankXMLParser.parseToXml(body);
//				String subXML = newXmlbody.substring(CoreConstant.START_INDEX
//						.length() + 1);
//				newXmlbody = subXML.substring(0, subXML.length()
//						- CoreConstant.END_INDEX.length());
//				gPay.setBody(newXmlbody);
//				newXmlContent = BankXMLParser.parseToXml(gPay);
//			}
//		} catch (DocumentException e) {
//			e.printStackTrace();
//			logger.error("解密文件操作转换成对象 错误！{}", e.getMessage());
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("解密文件操作 错误！{}", e.getMessage());
//		}
//		return newXmlContent;
//		return null;
//	}

//	private String paseSecurity(String paseInfo) {
//		return bankSecurityHandle.symDecryptPNI(
//				CoreConstant.BankEncrypt.CCB_CNCRYPT, false, paseInfo);
//	}

	public String getCheckSwitch() {
		return checkSwitch;
	}

	public void setCheckSwitch(String checkSwitch) {
		this.checkSwitch = checkSwitch;
	}

	public String getSignSwitch() {
		return signSwitch;
	}

	public void setSignSwitch(String signSwitch) {
		this.signSwitch = signSwitch;
	}	

}
