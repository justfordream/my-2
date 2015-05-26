package com.huateng.core.remoting.impl;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bank.bean.head.GPay;
import com.huateng.bank.bean.head.Header;
import com.huateng.bank.bean.head.Sign;
import com.huateng.bank.common.BankConstant;
import com.huateng.bank.common.parse.BankXMLParser;
import com.huateng.bank.logFormat.MessageLogger;
import com.huateng.cmupay.service.BankSecurityHandle;
import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.UpayCommon;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.jms.common.JmsMsgHeader;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.remoting.BankRemoting;
import com.huateng.core.util.DateUtil;
import com.huateng.core.util.JacksonUtils;
import com.huateng.log.LogHandle;

/**
 * @author cmt
 * @version 创建时间：2013-7-12 上午11:20:15 类说明 发送消息到银行
 */
// @Controller("bankRemoting")
public class BankRemotingImpl implements BankRemoting {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger log = MessageLogger.getLogger(getClass());
//	/**
//	 * 连接超时时间
//	 */
//	private String connTimeout;
//	/**
//	 * 接收响应超时时间
//	 */
//	private String revTimeout;
	/**
	 * 验签服务
	 */
	private RemoteService remoteService;

	private static BankSecurityHandle bankSecurityHandle;
	
	/**
	 * 验签开关
	 */
	private @Value("${check.switch}") String checkSwitch;
	/**
	 * 签名开关
	 */
	private @Value("${check.switch}") String signSwitch;
	/**
	 * 连接超时时间
	 */
	private @Value("${http.conn.timeout}") String connTimeout;
	
	/**
	 * 接收响应超时时间
	 */
	private @Value("${http.rev.timeout}") String revTimeout;
	
	

	public static BankSecurityHandle getBankSecurityHandle() {
		return bankSecurityHandle;
	}

	public static void setBankSecurityHandle(
			BankSecurityHandle bankSecurityHandle) {
		BankRemotingImpl.bankSecurityHandle = bankSecurityHandle;
	}

	private LogHandle logHandle;

	public void setLogHandle(LogHandle logHandle) {
		this.logHandle = logHandle;
	}

	@SuppressWarnings("finally")
	@Override
	public String sendMsg(String header, String... args) {
		String resp2CoreText = "";
		String responseText = "";
		try {
			if (StringUtils.isNotBlank(args[0])) {
				/**
				 * 路由信息
				 */
				JmsMsgHeader routeMsg = JacksonUtils.json2Bean(header,
						JmsMsgHeader.class);
				logger.info("银行前置接受核心的请求,报文内容{}",new Object[]{DateUtil.paseLog(args[0])});
				/**
				 * 把xml转换成对象
				 */
				GPay gPay = BankXMLParser.parseXmlContent(args[0]);

				Header head = gPay.getHeader();
				
				/**
				 * 判断是否可以加解密
				 */
				//args[0] = paseBody(gPay);

				this.logHandle.info(true,
						CoreConstant.ErrorCode.SUCCESS.getCode(),
						head.getReqTransID(), head.getActivityCode(),
						UpayCommon.getTransDesc(head.getActivityCode()),
						UpayCommon.getProvCodeBySysCode(head.getReqSys()),
						head.getReqSys(),
						CoreConstant.ErrorCode.SUCCESS.getDesc());
				log.debug("交易代码：{}，接收方交易流水号：{}",
						new Object[] { head.getActivityCode(),
								head.getReqTransID() });
//				logger.debug("......根据路由信息向银行[" + routeMsg.getReqIp()+ "]发送信息 : " + DateUtil.paseLog(args[0]));				
				logger.info("......根据路由信息向银行[{}]发送信息 :{} ",new Object[]{routeMsg.getReqIp(),DateUtil.paseLog(args[0])});
				logger.debug("......根据路由信息向银行[{}]发送信息 :{} ",new Object[]{routeMsg.getReqIp(),args[0]});
				responseText = this.sendMsg(routeMsg, args[0]);
//				logger.debug("......接收到银行[" + routeMsg.getReqIp() + "]响应信息: "+ responseText);
				logger.info("......银行前置接收到银行[{}]响应信息:{} ",new Object[]{routeMsg.getReqIp(),DateUtil.paseLog(responseText)});
				logger.debug("......银行前置接收到银行[{}]响应信息:{} ",new Object[]{routeMsg.getReqIp(),responseText});
				log.info("银行前置接收到银行[{}]响应信息",new Object[]{routeMsg.getReqIp()});
				/*
				 * 验签
				 */
				resp2CoreText = this.checkSign(routeMsg.getReqIp(),
						responseText);

			} else {
				logger.error("......BankMessageListener接收核心消息失败,核心返回消息格式错误(TextMessage)......");
				log.error("......BankMessageListener接收核心消息失败,核心返回消息格式错误(TextMessage)......");
				throw new ServiceException("UPAY-B-015A05");
			}
		} catch (ServiceException e) {
			logger.error("......BankMessageListener处理核心消息失败,失败原因:"
					+ e.getMessage());
			ErrorBean bean = null;
			try {
				bean = ErrorConfigUtil.getBankError(e.getMessage());
			} catch (Exception e1) {
				bean = ErrorConfigUtil.getBankError("UPAY-B-015A06");
			}
			String rspCode = bean.getOuterCode();
			String rspDesc = bean.getDesc();
			GPay gPay = new GPay();
			Header head = new Header();
			head.setRspCode(rspCode);
			head.setRspDesc(rspDesc);
			gPay.setHeader(head);
			log.error("错误码:{},错误描述:{}", new Object[]{head.getRspCode(),head.getRspDesc()});
			String resText = "";
			try {
				resText = BankXMLParser.parseToXml(gPay);
				logger.error("<异常情况>银行前置响应给核心的响应报文为:[{}]", resText);
			} catch (Exception e1) {
				//e1.printStackTrace();
				logger.error("",e);
			}
			resp2CoreText = resText;
		} finally {
			return resp2CoreText;
		}

	}

	/**
	 * 根据路由信息主动发送信息到指定路由
	 * 
	 * @param routeMsg
	 *            路由信息
	 * @param param
	 *            发送的信息
	 * @return
	 * @throws ServiceException
	 * @throws Exception
	 */
	protected String sendMsg(JmsMsgHeader routeMsg, final String xmlContent)
			throws ServiceException {
		if (routeMsg == null) {
			throw new ServiceException("UPAY-B-015A06");
		}
		String protocol = routeMsg.getProtocol();
		if (StringUtils.isBlank(protocol)) {
			throw new ServiceException("UPAY-B-015A06");
		}
		if (!protocol.toLowerCase().equals("http")
				&& !protocol.toLowerCase().equals("https")) {
			throw new ServiceException("UPAY-B-015A06");
		}
		String reqIp = routeMsg.getReqIp();
		if (StringUtils.isBlank(reqIp)) {
			throw new ServiceException("UPAY-B-015A06");
		}

		String reqPort = routeMsg.getReqPort();
		if (StringUtils.isBlank(reqPort)) {
			throw new ServiceException("UPAY-B-015A06");
		}
		String reqPath = routeMsg.getReqPath() == null ? "" : routeMsg
				.getReqPath();
		logger.debug("协议类型: " + protocol + ",请求IP: " + reqIp + ",请求端口号: "
				+ reqPort + ",请求地址: " + reqPath);
		String responseText = null;
		try {
			String requestURL = protocol + "://" + reqIp + ":" + reqPort
					+ reqPath;
			logger.debug("Request URL is: " + requestURL);
			//connTimeout = CoreCommon.getConnectionTimeOut();
			//revTimeout = CoreCommon.getReceiveTimeOut();
			if (StringUtils.isBlank(connTimeout)
					|| StringUtils.isBlank(revTimeout)) {
				throw new ServiceException("UPAY-B-015A06");
			}
			HttpClient httpClient = this.getHttpClient(connTimeout, revTimeout);
			httpClient.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET,
					CoreConstant.MSG_ENCODING);
			HttpPost post = new HttpPost(requestURL);
			//logger.debug("......签名前报文 : " + DateUtil.paseLog(xmlContent));
			String newXmlContent = this.sign(reqIp, xmlContent);
			//logger.debug("......签名后报文 : " + DateUtil.paseLog(newXmlContent));
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			formParams.add(new BasicNameValuePair(BankConstant.REQ_XML_DATA,
					newXmlContent));
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(
					formParams, CoreConstant.MSG_ENCODING);
			post.setEntity(uefEntity);
			HttpResponse response = httpClient.execute(post);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entitys = response.getEntity();
				if (entitys != null) {
					responseText = EntityUtils.toString(entitys,
							CoreConstant.MSG_ENCODING);
				}
			}
			httpClient.getConnectionManager().shutdown();
		} catch (ConnectTimeoutException e) {
			logger.error(e.getMessage());
			log.error("银行前置连接银行超时");
			throw new ServiceException("UPAY-B-015A07");// 系统错误
		} catch (SocketTimeoutException e) {
			logger.error(e.getMessage());
			log.error("银行前置连接银行超时");
			throw new ServiceException("UPAY-B-015A07");// 超时未收到响应
		} catch(ConnectException e){
			logger.error(e.getMessage());
			log.error("银行前置连接银行超时");
			throw new ServiceException("UPAY-B-015A07");
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException("UPAY-B-015A06");// 未知错误
		}
		if (StringUtils.isBlank(responseText)) {
			throw new ServiceException("UPAY-B-015A07");
		}
		return responseText;
	}

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
				//logger.debug("......验签源串[" + DateUtil.paseLog(signText) + "]");
				boolean isValid = this.remoteService.verify(bankId, signText,
						sign.getSignValue());
				if (!isValid) {
//					logger.debug("......验签失败,源报文["+ DateUtil.paseLog(xmlContent) + "],源签名串["
//							+ DateUtil.paseLog(signText) + "],签名串["+ sign.getSignValue() + "]");
					logger.error("......验签失败......");
					log.error("......验签失败......");
					throw new ServiceException("UPAY-B-014A06");// 签名验证失败
				}
//				logger.debug("......验签成功,源报文[" + DateUtil.paseLog(xmlContent)+ "],源签名串[" + DateUtil.paseLog(signText) + "],签名串["
//						+ sign.getSignValue() + "]");
				logger.debug("......验签成功......");
				boolean isCertValid = this.remoteService.verifyCertificate(
						sign.getCerID(), bankId);
				if (!isCertValid) {
					logger.error("......验证证书无效.....");
					log.error("......验证证书无效.....");
					throw new ServiceException("UPAY-B-014A07");// 签名验证失败
				}
			}
		}

		return xmlContent;
	}

	public String sign(String client, String xmlContent)
			throws ServiceException {
		//String signSwitch = CoreCommon.getSignSwitch();
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch)) {
			logger.debug("......调用Bank前置签名方法.....");
			String singure = BankXMLParser.parseBankSignXml(xmlContent);
			String certId = null;
			if (StringUtils.isNotBlank(singure)) {
				try {
//					logger.debug("......原签名串[" + (DateUtil.paseLog(singure))
//							+ "]");
					singure = this.remoteService.sign(null, singure);
					certId = this.remoteService.getCertId();
//					logger.debug("......签名后的值[" + DateUtil.paseLog(singure)
//							+ "]");
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
	 * 加密相关的数据
	 * 
	 * @param gPay
	 * @throws Exception
	 */
	private String paseBody(GPay gPay) throws Exception {
//		if (gPay == null) {
//			return "";
//		}
//		String paseXMLContent = BankXMLParser.parseToXml(gPay);
//		if (BankXMLParser.isEncryptSwitch(gPay.getHeader().getActivityCode())) {
//			logger.debug("加密操作业务代码：{}", gPay.getHeader().getActivityCode());
//			String resXMLBody = null;
//			StringBuffer sb = new StringBuffer(CoreConstant.START_INDEX);
//			sb.append(gPay.getBody());
//			sb.append(CoreConstant.END_INDEX);
//			if (CoreConstant.EncryptActiveCode.CRM_BANK_CHECK.getCode().equals(
//					gPay.getHeader().getActivityCode())) {
//				com.huateng.bank.bean.body.Body body = new com.huateng.bank.bean.body.Body();
//				Document docBody = DocumentHelper.parseText(sb.toString());
//				Element rootBody = docBody.getRootElement();
//				BankXMLParser.settingElement(body, rootBody);
//				body.setBankAcctID(paseSecurity(body.getBankAcctID()));
//				body.setUserID(paseSecurity(body.getUserID()));
//				body.setUserName(paseSecurity(body.getUserName()));
//				resXMLBody = BankXMLParser.parseToXml(body);
//			} else if (CoreConstant.EncryptActiveCode.CRM_SIGN_CODE.getCode()
//					.equals(gPay.getHeader().getActivityCode())) {
//				com.huateng.bank.bean.signBody.Body body = new com.huateng.bank.bean.signBody.Body();
//				Document docBody = DocumentHelper.parseText(sb.toString());
//				Element rootBody = docBody.getRootElement();
//				BankXMLParser.settingElement(body, rootBody);
//				body.setBankAcctID(paseSecurity(body.getBankAcctID()));
//				body.setUserID(paseSecurity(body.getUserID()));
//				body.setUserName(paseSecurity(body.getUserName()));
//				resXMLBody = BankXMLParser.parseToXml(body);
//			}
//			if (StringUtils.isNotBlank(resXMLBody)) {
//				String subXML = resXMLBody.substring(CoreConstant.START_INDEX
//						.length() + 1);
//				resXMLBody = subXML.substring(0, subXML.length()
//						- CoreConstant.END_INDEX.length());
//				gPay.setBody(resXMLBody);
//			}
//			paseXMLContent = BankXMLParser.parseToXml(gPay);
//		}
		return null;
	}

	private static String paseSecurity(String paseInfo) {
		return bankSecurityHandle.symDecryptPNI(
				CoreConstant.BankEncrypt.CCB_CNCRYPT, true, paseInfo);
	}

	/**
	 * 
	 * @param timeout
	 * @param soTimeout
	 * @return
	 */
	private HttpClient getHttpClient(String timeout, String soTimeout) {
		HttpParams params = new BasicHttpParams();
		int iTimeout = 60000;
		if (StringUtils.isNotBlank(timeout)) {
			iTimeout = Integer.valueOf(timeout).intValue();
		}
		int iSoTimeout = 60000;
		if (StringUtils.isNotBlank(soTimeout)) {
			iSoTimeout = Integer.valueOf(soTimeout).intValue();
		}
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				iTimeout);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, iSoTimeout);
		HttpClient customerHttpClient = new DefaultHttpClient(params);
		return customerHttpClient;
	}

	public RemoteService getRemoteService() {
		return remoteService;
	}

	public void setRemoteService(@Qualifier RemoteService remoteService) {
		this.remoteService = remoteService;
	}

	public String getCheckSwitch() {
		return checkSwitch;
	}

	public void setCheckSwitch(String checkSwitch) {
		this.checkSwitch = checkSwitch;
	}

	public String getConnTimeout() {
		return connTimeout;
	}

	public void setConnTimeout(String connTimeout) {
		this.connTimeout = connTimeout;
	}

	public String getRevTimeout() {
		return revTimeout;
	}

	public void setRevTimeout(String revTimeout) {
		this.revTimeout = revTimeout;
	}

	public String getSignSwitch() {
		return signSwitch;
	}

	public void setSignSwitch(String signSwitch) {
		this.signSwitch = signSwitch;
	}
	
	

}
