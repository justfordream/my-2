package com.huateng.tmall.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.huateng.tmall.bean.head.GPay;
import com.huateng.tmall.bean.head.Header;
import com.huateng.tmall.bean.head.Sign;
import com.huateng.tmall.bean.mapper.TMallEmergencyInfoMapper;
import com.huateng.tmall.common.parse.TMallXMLParser;
import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.base.BaseServiceImpl;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.UpayCommon;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.parse.CrmMessageParser;
import com.huateng.log.LogHandle;
import com.huateng.log.MessageLogger;
import com.huateng.tmall.service.TMallService;



/**
 * 銀行消息發送實現類
 * 
 * @author Gary
 * 
 */
public class TMallServiceImpl extends BaseServiceImpl implements TMallService {
	private Logger logger = LoggerFactory.getLogger(getClass());	
	private MessageLogger log = MessageLogger.getLogger(getClass());
	/**
	 * 验签服务
	 */
	@Autowired
	private RemoteService bankSecurityRemoting;
	// @Autowired
	private LogHandle logHandle;	
	private Map<String,CrmMessageParser> CrmMessageParserMap ;	
	/**
	 * 验签开关
	 */
	private @Value("${check.switch}") String checkSwitch;
	/**
	 * 签名开关
	 */
	private @Value("${sign.switch}") String signSwitch;
	
	/**
	 * 读取内存数据库开关
	 */
	private @Value("${MEMDB_SWITCH}") String memdbSwitch;
	
	@Autowired
	private TMallEmergencyInfoMapper tmallEmergencyInfoMapper;
	
		
	@Override
	public String checkSign(String client, String xmlContent)
			throws ServiceException {
		logger.debug("......调用TMall前置验签方法......");
		if (StringUtils.isBlank(xmlContent)) {
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}
		//String checkSwitch = CoreCommon.getCheckSwitch();
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
					UpayCommon.getProvCodeBySysCode(header.getReqSys()),
					header.getReqSys(),
					CoreConstant.ErrorCode.SUCCESS.getDesc());

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
				//logger.debug("......验签源串:[{}]",new Object[]{signText});
				boolean isValid = this.bankSecurityRemoting.verify(bankId,
						signText, sign.getSignValue());
				if (!isValid) {
					//logger.debug("......验签失败,源报文:[{}],源签名串:[{}],签名串:[{}]",new Object[]{xmlContent,signText,sign.getSignValue()});
					logger.error("验签失败,源签名串:[{}]",new Object[]{signText});
					log.error("验签失败,源签名串:[{}]",new Object[]{signText});
					throw new ServiceException("UPAY-B-014A06");// 签名验证失败
				}
				//logger.debug("......验签成功,源报文[{}],源签名串[{}],签名串[{}]",new Object[]{xmlContent,signText,sign.getSignValue()});
				logger.debug("验签成功,源签名串[{}],签名串[{}]",new Object[]{signText,sign.getSignValue()});
				log.debug("验签成功");
				boolean isCertValid = this.bankSecurityRemoting
						.verifyCertificate(sign.getCerID(), bankId);
				if (!isCertValid) {
					logger.error(".验证证书无效{}",bankId);
					log.error("验证证书无效{}",bankId);
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
					if(StringUtils.isNotBlank(singure)){
						logger.debug("签名成功");
						singure = this.paseEncode(singure);
					}else{
						log.error("签名失败，签名原文{},签名结果{}",xmlContent,singure);
						logger.error("签名失败，签名原文{},签名结果{}",xmlContent,singure);
					}
				} catch (Exception e) {
					logger.error("......签名错误..{}",xmlContent);
					log.error("签名错误{}",xmlContent);
				}
			}
			xmlContent = TMallXMLParser.convertBankXml(singure, certId,
					xmlContent);
		}
		return xmlContent;
	}
	public String sign( String xmlContent) throws ServiceException
	{
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch))
		{
			String singure = TMallXMLParser.parseBankSignXml(xmlContent);
			String certId = null;
			if (StringUtils.isNotBlank(singure))
			{
				try
				{
					log.info("......签名串[" + (singure) + "]");
					singure = this.bankSecurityRemoting.sign(null, singure);
					certId = this.bankSecurityRemoting.getCertId();
					log.info("......签名后的串[" + singure + "]");
				}
				catch (Exception e)
				{
					log.error("签名失败!", e);
					log.error("......取得签名串失败......");
				}
			}
			xmlContent = TMallXMLParser.convertBankXml(singure, certId, xmlContent);
		}else{

			try {
				GPay gPay = TMallXMLParser.parseXmlContent(xmlContent);
				Sign sign = gPay.getSign();
				if (sign == null) {
					sign = new Sign();
					sign.setSignFlag("");
					sign.setCerID("");
					sign.setSignValue("");
				}
				gPay.setSign(sign);
				xmlContent = TMallXMLParser.parseGPay(gPay);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("组装签名串异常!", e);
				log.error("......取得签名串失败......{}",xmlContent);
				logger.error("组装签名串异常!{}",xmlContent);
			}
		
		}
		return xmlContent;
	}

	@Override
	public String tmallCheckSign(String client, String xmlContent, GPay gPay)
			throws ServiceException {	
		//String checkSwitch = CoreCommon.getCheckSwitch();
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(checkSwitch)) {
			
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
					UpayCommon.getProvCodeBySysCode(CoreConstant.tmall_sys),
					header.getReqSys(),
					CoreConstant.ErrorCode.SUCCESS.getDesc());
			
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
				//logger.debug("......验签源串[{}]",new Object[]{signText});
				boolean isValid = this.bankSecurityRemoting.verify(bankId,
						signText, sign.getSignValue());
				if (!isValid) {
					//logger.debug("......验签失败,源报文[{}],源签名串[{}],签名串[{}]",new Object[]{xmlContent,signText,sign.getSignValue()});
					logger.error("......验签失败......");
					log.error("验签失败");
					throw new ServiceException("UPAY-B-014A06");// 签名验证失败
				}
				//logger.debug("......验签成功,源报文[{}],源签名串[{}],签名串[{}]",new Object[]{xmlContent,signText,sign.getSignValue()});
				logger.debug("......验签成功......");
				log.debug("签名成功");
				boolean isCertValid = this.bankSecurityRemoting.verifyCertificate(sign.getCerID(), bankId);
				if (!isCertValid) {
					logger.error("验证证书无效");
					log.error("验证证书无效");
					throw new ServiceException("UPAY-B-014A07");// 签名验证失败
				}
			}
		}

		return xmlContent;
	}

	@Override
	public String tmallSign(String client, String xmlContent)
			throws ServiceException {
		return null;
	}

	@SuppressWarnings({ "rawtypes"})
	@Override
	public Map assemblyCrmXml(GPay gPay,String txnSeq,String transIDHTime) throws ServiceException {
		logger.debug("......组装省充值请求报文......");		
		Map result = null;
		
		if(StringUtils.isBlank(gPay.getHeader().getActivityCode())){
			throw new ServiceException("UPAY-B-015A05");
		}		
		CrmMessageParser messageParser = CrmMessageParserMap.get(gPay.getHeader().getActivityCode());
		if(messageParser == null){
			//无权限
			throw new ServiceException("UPAY-B-012A05");
		}
		result = messageParser.assemblyCrmReqMsg(gPay, txnSeq, transIDHTime);
		
		return result;
	}
	
		
	@Override
	public GPay assemblyGPayMessage(String xmlContent) throws ServiceException {
		GPay gPay = null;
		if (StringUtils.isBlank(xmlContent)) {
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}
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
		 * 系统监控日志
		 * 
		 */
		log.info("交易代码：{}，发送方交易流水号：{}",
				new Object[]{header.getActivityCode(),header.getReqTransID()});
		return gPay;
	}

	@Override
	public Map assemblyTMallResXml(GPay gpay, String crmResXml,String txnSeq,String transIdHTime) throws ServiceException {
		logger.debug("......解析省前置的响应报文,组装为天猫充值响应报文......");
		Map result = null;
		
		if(StringUtils.isBlank(gpay.getHeader().getActivityCode())){
			throw new ServiceException("UPAY-B-015A05");
		}
		
		CrmMessageParser messageParser = CrmMessageParserMap.get(gpay.getHeader().getActivityCode());
		if(messageParser == null){
			throw new ServiceException("UPAY-B-015A05");
		}		
		result = messageParser.assemblyTMallResMsg(gpay, crmResXml, txnSeq, transIdHTime);		
		
		return result;		
	}
	
	@Override
	public boolean checkIsEmergency() {
		boolean result = false;
		if(CoreConstant.SWITCH_OPEN.equals(memdbSwitch.trim())){
			String emergencyFlag = CoreConstant.EmergencySwitch.EMERGENCY.getFlag();			
			//String flag = UpayMemCache.getTmallEmergency().trim();
			String flag = tmallEmergencyInfoMapper.selectTmallEmergencyFlag();
			if(StringUtils.isNotBlank(flag)){
				if(flag.equals(emergencyFlag)){
					result = true;
				}
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
			return "";
		}
	}
	
	
	@Override
	public void tmallFlowMonitor() throws ServiceException {
						
	}

	public Map<String, CrmMessageParser> getCrmMessageParserMap() {
		return CrmMessageParserMap;
	}

	public void setCrmMessageParserMap(
			Map<String, CrmMessageParser> crmMessageParserMap) {
		CrmMessageParserMap = crmMessageParserMap;
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

	public String getMemdbSwitch() {
		return memdbSwitch;
	}

	public void setMemdbSwitch(String memdbSwitch) {
		this.memdbSwitch = memdbSwitch;
	}
	
	
}
