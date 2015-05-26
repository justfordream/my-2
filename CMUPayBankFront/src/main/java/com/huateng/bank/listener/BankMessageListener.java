package com.huateng.bank.listener;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.huateng.bank.bean.head.GPay;
import com.huateng.bank.bean.head.Header;
import com.huateng.bank.common.BankConstant;
import com.huateng.bank.common.parse.BankXMLParser;
import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.base.BaseMessageListener;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.UpayCommon;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.jms.common.JmsMsgHeader;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.util.BeanUtil;
import com.huateng.log.LogHandle;

/**
 * 监听银行端的jms请求
 * 
 * @author leon
 * 
 */
public class BankMessageListener extends BaseMessageListener {
	private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 连接超时时间
	 */
	private String connTimeout;
	/**
	 * 接收响应超时时间
	 */
	private String revTimeout;
	/**
	 * 验签服务
	 */
	private RemoteService remoteService;
	@Autowired
	private LogHandle logHandle;

	@SuppressWarnings("rawtypes")
	@Override
	public void onMessage(Message message) {
		logger.debug("......BankMessageListener监听核心消息......");
		Destination replyToDest = null;
		String reqTxnSeq = "";
		String responseText = "";
		try {
			/*
			 * 接受到文字消息
			 */
			if (message instanceof TextMessage) {
				replyToDest = message.getJMSReplyTo();// 回复queue
				reqTxnSeq = message.getStringProperty(CoreConstant.RequestMsg.REQ_TXN_SEQ);// 回复流水
				String xmlContent = ((TextMessage) message).getText();
				logger.debug("...接收的核心信息 : " + xmlContent);
				if (StringUtils.isNotBlank(xmlContent)) {
					/*
					 * 路由信息
					 */
					Map routeMap = null;
					Object routeObj = message.getObjectProperty("routeMsg");
					if (routeObj != null) {
						routeMap = (Map) routeObj;
					} else {
						routeMap = new HashMap();
					}
					JmsMsgHeader routeMsg = BeanUtil.mapToBean(routeMap, JmsMsgHeader.class);
					/*
					 * 根据路由信息发送数据
					 */
					/*
					 * 全网监控日志
					 */
					GPay gPay = BankXMLParser.parseXmlContent(xmlContent);
					Header header = gPay.getHeader();
					this.logHandle.info(true, CoreConstant.ErrorCode.SUCCESS.getCode(), header.getReqTransID(),
							header.getActivityCode(), UpayCommon.getTransDesc(header.getActivityCode()),
							UpayCommon.getProvCodeBySysCode(header.getReqSys()), header.getReqSys(),
							CoreConstant.ErrorCode.SUCCESS.getDesc());

					logger.debug("......根据路由信息向银行[" + routeMsg.getReqIp() + "]发送信息 : " + xmlContent);
					responseText = this.sendMsg(routeMsg, xmlContent);
					logger.debug("......接收到银行[" + routeMsg.getReqIp() + "]响应信息: " + responseText);
					/*
					 * 验签
					 */
					responseText = this.checkSign(routeMsg.getReqIp(), responseText);

				} else {
					throw new ServiceException("UPAY-B-015A05");
				}
				/*
				 * 返回信息
				 */
				super.replayMsg(replyToDest, responseText, reqTxnSeq);
			} else {
				logger.debug("......BankMessageListener接收核心消息失败,核心返回消息格式错误(TextMessage)......");
				throw new ServiceException("UPAY-B-015A05");
			}
		} catch (Exception e) {
			logger.error("......BankMessageListener处理核心消息失败,失败原因:" + e.getMessage());
			ErrorBean bean = null;
			try {
				bean = ErrorConfigUtil.getBankError(e.getMessage());
			} catch (Exception e1) {
				bean = ErrorConfigUtil.getBankError("UPAY-B-015A06");
			}
			String rspCode = bean.getOuterCode();
			String rspDesc = bean.getDesc();
			GPay gPay = new GPay();
			Header header = new Header();
			header.setRspCode(rspCode);
			header.setRspDesc(rspDesc);
			gPay.setHeader(header);
			String resText = "";
			try {
				resText = BankXMLParser.parseToXml(gPay);
			} catch (Exception e1) {
				//e1.printStackTrace();
				logger.error("",e);
			}
			super.replayMsg(replyToDest, resText, reqTxnSeq);
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
	protected String sendMsg(JmsMsgHeader routeMsg, final String xmlContent) throws ServiceException {
		if (routeMsg == null) {
			throw new ServiceException("UPAY-B-015A06");
		}
		String protocol = routeMsg.getProtocol();
		if (StringUtils.isBlank(protocol)) {
			throw new ServiceException("UPAY-B-015A06");
		}
		if (!protocol.toLowerCase().equals("http") && !protocol.toLowerCase().equals("https")) {
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
		String reqPath = routeMsg.getReqPath() == null ? "" : routeMsg.getReqPath();
		logger.debug("协议类型: " + protocol + ",请求IP: " + reqIp + ",请求端口号: " + reqPort + ",请求地址: " + reqPath);
		String responseText = null;
		try {
			String requestURL = protocol + "://" + reqIp + ":" + reqPort + reqPath;
			logger.debug("Request URL is: " + requestURL);
			HttpClient httpClient = this.getHttpClient(connTimeout, revTimeout);
			HttpPost post = new HttpPost(requestURL);
			logger.debug("......签名前报文 : " + xmlContent);
			String newXmlContent = this.sign(reqIp, xmlContent);
			logger.debug("......签名后报文 : " + newXmlContent);
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			formParams.add(new BasicNameValuePair(BankConstant.REQ_XML_DATA, newXmlContent));
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, CoreConstant.MSG_ENCODING);
			post.setEntity(uefEntity);
			HttpResponse response = httpClient.execute(post);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entitys = response.getEntity();
				if (entitys != null) {
					responseText = EntityUtils.toString(entitys, CoreConstant.MSG_ENCODING);
				}
			}
			httpClient.getConnectionManager().shutdown();
		} catch (ConnectTimeoutException e) {
			logger.error(e.getMessage());
			throw new ServiceException("UPAY-B-015A03");// 系统错误
		} catch (SocketTimeoutException e) {
			logger.error(e.getMessage());
			throw new ServiceException("UPAY-B-015A07");// 超时未收到响应
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException("UPAY-B-015A06");// 未知错误
		}
		if (StringUtils.isBlank(responseText)) {
			throw new ServiceException("UPAY-B-015A06");
		}
		return responseText;
	}

	public String checkSign(String client, String xmlContent) throws ServiceException {
//		logger.debug("......调用Bank前置验签方法......");
//		if (StringUtils.isBlank(xmlContent)) {
//			throw new ServiceException("UPAY-B-015A06");// 解析报文失败
//		}
//		String checkSwitch = CoreCommon.getCheckSwitch();
//		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(checkSwitch)) {
//			GPay gPay = null;
//			try {
//				gPay = BankXMLParser.parseXmlContent(xmlContent);
//			} catch (Exception e) {
//				throw new ServiceException("UPAY-B-015A06");// 解析报文失败
//			}
//			Header header = gPay.getHeader();
//			if (header == null) {
//				throw new ServiceException("UPAY-B-015A06");// 解析报文失败
//			}
//			Sign sign = gPay.getSign();
//			if (sign == null) {
//				throw new ServiceException("UPAY-B-019A16");// SignValue参数不正确
//			}
//			/*
//			 * 全网监控日志
//			 */
//			this.logHandle.info(false, CoreConstant.ErrorCode.SUCCESS.getCode(), header.getReqTransID(),
//					header.getActivityCode(), UpayCommon.getTransDesc(header.getActivityCode()),
//					UpayCommon.getProvCodeBySysCode(header.getReqSys()), header.getReqSys(),
//					CoreConstant.ErrorCode.SUCCESS.getDesc());
//
//			if (CoreConstant.SignFlag.YES.equals(sign.getSignFlag())) {
//				String signText = BankXMLParser.parseBankSignXml(xmlContent);
//				String bankId = "";
//				if (CoreConstant.ActionCode.REQ.getCode().equals(header.getActionCode())) {
//					bankId = header.getReqSys();
//				} else if (CoreConstant.ActionCode.RES.getCode().equals(header.getActionCode())) {
//					bankId = header.getRcvSys();
//				}
//				logger.debug("......验签源串[" + signText + "]");
//				boolean isValid = this.remoteService.verify(bankId, signText, sign.getSignValue());
//				if (!isValid) {
//					logger.debug("......验签失败,源报文[" + xmlContent + "],源签名串[" + signText + "],签名串[" + sign.getSignValue()
//							+ "]");
//					throw new ServiceException("UPAY-B-014A06");// 签名验证失败
//				}
//				logger.debug("......验签成功,源报文[" + xmlContent + "],源签名串[" + signText + "],签名串[" + sign.getSignValue()
//						+ "]");
//				boolean isCertValid = this.remoteService.verifyCertificate(sign.getCerID(), bankId);
//				if (!isCertValid) {
//					logger.debug("......验证证书无效.....");
//					throw new ServiceException("UPAY-B-014A07");// 签名验证失败
//				}
//			}
//		}

		return xmlContent;
	}

	public String sign(String client, String xmlContent) throws ServiceException {
//		String signSwitch = CoreCommon.getSignSwitch();
//		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch)) {
//			String singure = BankXMLParser.parseBankSignXml(xmlContent);
//			String certId = null;
//			if (StringUtils.isNotBlank(singure)) {
//				try {
//					logger.debug("......原签名串[" + (singure) + "]");
//					singure = this.remoteService.sign(null, singure);
//					certId = this.remoteService.getCertId();
//					logger.debug("......签名后的值[" + singure + "]");
//				} catch (Exception e) {
//					e.printStackTrace();
//					logger.debug("......签名失败......");
//				}
//			}
//			xmlContent = BankXMLParser.convertBankXml(singure, certId, xmlContent);
//		}
		return xmlContent;
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
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, iTimeout);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, iSoTimeout);
		HttpClient customerHttpClient = new DefaultHttpClient(params);
		return customerHttpClient;
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

	public RemoteService getRemoteService() {
		return remoteService;
	}

	public void setRemoteService(RemoteService remoteService) {
		this.remoteService = remoteService;
	}

}
