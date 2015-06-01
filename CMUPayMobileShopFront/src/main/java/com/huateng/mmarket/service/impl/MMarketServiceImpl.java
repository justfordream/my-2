package com.huateng.mmarket.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import com.huateng.mmarket.bean.head.GPay;
import com.huateng.mmarket.bean.head.Header;
import com.huateng.mmarket.bean.head.Sign;
import com.huateng.mmarket.common.parse.TMallXMLParser;
import com.huateng.cmupay.memdb.UpayMemCache;
import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.base.BaseServiceImpl;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.UpayCommon;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.parse.CrmMessageParser;
import com.huateng.core.util.StrUtil;
import com.huateng.log.LogHandle;
import com.huateng.log.MessageLogger;
import com.huateng.mmarket.service.MMarketService;
import com.sun.org.apache.bcel.internal.generic.GOTO;



/**
 * 
 * 
 * @author Gary
 * 
 */
public class MMarketServiceImpl extends BaseServiceImpl implements MMarketService {
	private Logger logger = LoggerFactory.getLogger(getClass());	
	private MessageLogger log = MessageLogger.getLogger(getClass());
	/**
	 * 验签服务
	 */
	@Autowired
	private RemoteService bankSecurityRemoting;
	// @Autowired
	private LogHandle logHandle;	
	/**
	 * 验签开关
	 */
	private @Value("${check.switch}") String checkSwitch;
	/**
	 * 签名开关
	 */
	private @Value("${sign.switch}") String signSwitch;
		
	@Override
	public String checkSign(String client, String xmlContent)
			throws ServiceException {
		logger.debug("......调用商城前置验签方法......");
		if (StringUtils.isBlank(xmlContent)) {
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(checkSwitch)) {
			GPay gPay = null;
			try {
				gPay = TMallXMLParser.parseXmlContent(xmlContent);
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
					UpayCommon.getSysCodeByProvCode(header.getReqSys()),
					header.getReqSys(),
					CoreConstant.ErrorCode.SUCCESS.getDesc());
			
//			System.out.println("HomeProv:"+StrUtil.parseNodeValueFromXml("<HomeProv>", "</HomeProv>", gPay.getBody()));
			
			if (CoreConstant.SignFlag.YES.equals(sign.getSignFlag())) {
				String signText = TMallXMLParser.parseBankSignXml(xmlContent);
				String bankId = "";
				if (CoreConstant.ActionCode.REQ.getCode().equals(
						header.getActionCode())) {
					bankId = header.getReqSys();
				} else if (CoreConstant.ActionCode.RES.getCode().equals(
						header.getActionCode())) {
					bankId = header.getRcvSys();
				}
				boolean isValid = this.bankSecurityRemoting.verify(bankId,
						signText, sign.getSignValue());
				if (!isValid) {
					logger.error("......验签失败,源签名串:[{}],签名串:[{}]",new Object[]{signText,sign.getSignValue()});
					throw new ServiceException("UPAY-B-014A06");// 签名验证失败
				}
				logger.error("......验签成功,源签名串[{}],签名串[{}]",new Object[]{signText,sign.getSignValue()});
				boolean isCertValid = this.bankSecurityRemoting
						.verifyCertificate(sign.getCerID(), bankId);
				if (!isCertValid) {
					logger.error("......验证证书无效.....");
					throw new ServiceException("UPAY-B-014A07");// 签名验证失败
				}
			}
		}

		return xmlContent;
	}

	@SuppressWarnings("static-access")
	@Override
	public String sign(String client, String xmlContent)
			throws ServiceException {
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch)) {
			String singure = TMallXMLParser.parseBankSignXml(xmlContent);
			String certId = null;
			if (StringUtils.isNotBlank(singure)) {
				try {
					singure = this.bankSecurityRemoting.sign(null, singure);
					certId = this.bankSecurityRemoting.getCertId();
					logger.info("......签名后的值[{}]",new Object[]{singure});
					if(StringUtils.isNotBlank(singure)){
						singure = this.paseEncode(singure);
					}					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("......签名失败......");
				}
			}
			xmlContent = TMallXMLParser.convertBankXml(singure, certId,
					xmlContent);
			System.out.println("xmlContent : "+xmlContent);
		}
		return xmlContent;
	}

	@Override
	public String mmarketCheckSign(String client, String xmlContent, GPay gPay)
			throws ServiceException {	
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(checkSwitch)) {
			logger.debug(">>>>>移动商城:验签");
			Header header = gPay.getHeader();
			if (header == null) {
				logger.debug(">>>>>报文头为空");
				throw new ServiceException("UPAY-B-015A05");// 解析报文失败
			}
			Sign sign = gPay.getSign();
			if (sign == null) {
				logger.debug(">>>>>SignValue为空");
				throw new ServiceException("UPAY-B-019A16");// SignValue参数不正确
			}
			/*
			 * 全网监控日志
			 */
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					header.getReqTransID(), header.getActivityCode(),
					UpayCommon.getTransDesc(header.getActivityCode()),
					UpayCommon.getSysCodeByProvCode(header.getReqSys()),
					header.getReqSys(),
					CoreConstant.ErrorCode.SUCCESS.getDesc());
//			System.out.println("HomeProv:"+StrUtil.parseNodeValueFromXml("<HomeProv>", "</HomeProv>", gPay.getBody()));
			if (CoreConstant.SignFlag.YES.equals(sign.getSignFlag())) {
				String signText = TMallXMLParser.parseBankSignXml(xmlContent);
				String bankId = "";
				if (CoreConstant.ActionCode.REQ.getCode().equals(
						header.getActionCode())) {
					bankId = header.getReqSys();
				} else if (CoreConstant.ActionCode.RES.getCode().equals(
						header.getActionCode())) {
					bankId = header.getRcvSys();
				}
				boolean isValid = this.bankSecurityRemoting.verify(bankId,
						signText, sign.getSignValue());
				if (!isValid) {
					logger.error("......验签失败......");
					throw new ServiceException("UPAY-B-014A06");// 签名验证失败
				}
				boolean isCertValid = this.bankSecurityRemoting.verifyCertificate(sign.getCerID(), bankId);
				if (!isCertValid) {
					logger.error("......验证证书无效.....");
					throw new ServiceException("UPAY-B-014A07");// 签名验证失败
				}
				logger.debug("......验签成功......");
			}
		}

		return xmlContent;
	}

	@Override
	public String tmallSign(String client, String xmlContent)
			throws ServiceException {
		return null;
	}

//	@SuppressWarnings({ "rawtypes"})
//	@Override
//	public Map assemblyCrmXml(GPay gPay,String txnSeq,String transIDHTime) throws ServiceException {
//		logger.debug("......组装省充值请求报文......");		
//		Map result = null;
//		
//		if(StringUtils.isBlank(gPay.getHeader().getActivityCode())){
//			throw new ServiceException("UPAY-B-015A05");
//		}		
//		CrmMessageParser messageParser = CrmMessageParserMap.get(gPay.getHeader().getActivityCode());
//		if(messageParser == null){
//			throw new ServiceException("UPAY-B-015A05");
//		}
//		result = messageParser.assemblyCrmReqMsg(gPay, txnSeq, transIDHTime);
//		
//		return result;
//	}
	
		
	@Override
	public GPay assemblyGPayMessage(String xmlContent) throws ServiceException {
		GPay gPay = null;
		if (StringUtils.isBlank(xmlContent)) {
			logger.error("==========xmlContent为空!!!");
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}
		try {
			gPay = TMallXMLParser.parseXmlContent(xmlContent);
		} catch (Exception e) {
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}
		Header header = gPay.getHeader();
		if (header == null) {
			logger.error("==========header为空，解析失败！！！");
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}
		Sign sign = gPay.getSign();
		if (sign == null) {
			logger.error("==========sign为空，解析失败！！！");
			throw new ServiceException("UPAY-B-019A16");// SignValue参数不正确
		}
		/*
		 * 系统监控日志
		 * 
		 */
		log.info("处理结果："+CoreConstant.ErrorCode.SUCCESS.getDesc()+"，交易代码：{}，发送方交易流水号：{}",
				new Object[]{header.getActivityCode(),header.getReqTransID()});
		return gPay;
	}

//	@SuppressWarnings("rawtypes")
//	@Override
//	public Map assemblyTMallResXml(GPay gpay, String crmResXml,String txnSeq,String transIdHTime) throws ServiceException {
//		logger.debug("解析省前置的响应报文,组装为移动商城充值响应报文......");
//		Map result = null;
//		if(StringUtils.isBlank(gpay.getHeader().getActivityCode())){
//			throw new ServiceException("UPAY-B-015A05");
//		}
//		CrmMessageParser messageParser = CrmMessageParserMap.get(gpay.getHeader().getActivityCode());
//		if(messageParser == null){
//			throw new ServiceException("UPAY-B-015A05");
//		}		
//		result = messageParser.assemblyTMallResMsg(gpay, crmResXml, txnSeq, transIdHTime);		
//		
//		return result;		
//	}
	
	@Override
	public boolean checkIsEmergency() {
		boolean result = false;
		String emergencyFlag = CoreConstant.EmergencySwitch.EMERGENCY.getFlag();
		String flag = UpayMemCache.getTmallEmergency().trim();
		if(StringUtils.isNotBlank(flag)){
			if(flag.equals(emergencyFlag)){
				result = true;
			}
		}
			
		return result;
	}
	
	
	/**
	 * 特殊字符转换
	 * 
	 * @param str
	 * @return
	 */
	public static String paseDecode(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}
		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 特殊字符转换
	 * 
	 * @param str
	 * @return
	 */
	public static String paseEncode(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	
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

	public void setLogHandle(LogHandle logHandle) {
		this.logHandle = logHandle;
	}
}
