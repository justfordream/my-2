package com.huateng.core.remoting.impl;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.remoting.MMarketRemoting;
import com.huateng.log.MessageLogger;
import com.huateng.mmarket.common.parse.TMallXMLParser;

/**
 * 移动商城前置提供Hessian接口的实现
 * 
 * @author Manzhizhen
 * 
 */
public class MMarketRemotingImpl implements MMarketRemoting {
	private Logger log = LoggerFactory.getLogger("MMarketRemotingImpl");
	private MessageLogger logger = MessageLogger.getLogger("MMarketRemotingImpl");

	private @Value("${sign.switch}")
	String signSwitch;

	/**
	 * 连接超时时间
	 */
	@Value("${market.connTimeout}")
	private String connTimeout;
	/**
	 * 接收响应超时时间
	 */
	@Value("${market.revTimeout}")
	private String revTimeout;
	/**
	 * 验签服务
	 */
	@Autowired
	private RemoteService cmuSecurityRemoting;

	protected ExecutorService executor = null;

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
		return cmuSecurityRemoting;
	}

	public void setRemoteService(RemoteService cmuSecurityRemoting) {
		this.cmuSecurityRemoting = cmuSecurityRemoting;
	}

	@Override
	public String sendMsg(String marketUrl, String... args) {
		String resText = "";
		String newXmlContent;
		String xmlContent = "";
		/*
		 * 接受到文字消息
		 */
		try {
			if (StringUtils.isNotBlank(args[0])) {
				xmlContent = args[0];
				log.info("移动商城前置收到核心的支付、缴费结果通知请求报文: [{}]", xmlContent);

				newXmlContent = this.sign("", xmlContent);
				log.debug("签名后的值{}", newXmlContent);

				resText = send(marketUrl, newXmlContent);
				log.info("移动商城支付、缴费完成，收到移动商城返回的充值结果通知应答报文:[{}]", resText);
				if (resText == null || resText.trim().equals("")) {
					log.warn("[移动商城->移动商城前置]充值结果应答报文为空!原请求报文:[{}]",xmlContent);
				}
			} else {
				logger.error("移动商城支付结果通知，接受核心返回报文为空！");
				log.error("移动商城支付结果通知，接受核心返回报文为空！");
			}
		} catch (Exception e) {
			log.error("", e);
			log.error("移动商城支付结果通知异常,原报文{}", xmlContent);
		}
		return resText;
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
	protected String send(String url, final String xmlContent)
			throws ServiceException {
		String responseText = null;
		HttpClient httpClient;
		HttpPost post;
		UrlEncodedFormEntity uefEntity;
		HttpResponse response;
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		try {
			log.debug("发往商城url: {}", url);
			httpClient = this.getHttpClient(connTimeout, revTimeout);
			post = new HttpPost(url);
			formParams.add(new BasicNameValuePair("xmldata", xmlContent));
			uefEntity = new UrlEncodedFormEntity(formParams,
					CoreConstant.MSG_ENCODING);
			post.setEntity(uefEntity);
		} catch (Exception e) {
			log.error("向移动商城发送结果通知失败{}, 错误信息：{}", new Object[]{xmlContent, e.getMessage()} );
			throw new ServiceException("UPAY-C-5A05");// 系统错误
		}
		try {
			response = httpClient.execute(post);
			log.debug("http response : "
					+ response.getStatusLine().getStatusCode());
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entitys = response.getEntity();
				if (entitys != null) {
					responseText = EntityUtils.toString(entitys,CoreConstant.MSG_ENCODING);
				}
			} else {
				log.warn("发送商城地址错误!地址:{}", url);
				logger.warn("发送商城地址错误!地址:{}", url);
			}
			httpClient.getConnectionManager().shutdown();
		} catch (ConnectTimeoutException e) {
			log.error("移动商城，支付、缴费结果通知超时,通知报文{},系统错误:{} ",xmlContent,e);
			throw new ServiceException("UPAY-C-5A03");
		} catch (SocketTimeoutException e) {
			log.error("移动商城，支付、缴费结果通知超时,通知报文{},发送超时未收到响应:{}",xmlContent, e);
			throw new ServiceException("UPAY-C-5A07");
		} catch (Exception e) {
			log.error("移动商城，支付、缴费结果通知失败,通知报文{},发送失败:{} ",xmlContent, e);
			throw new ServiceException("UPAY-C-2998");
		}
		return responseText;
	}

	public String sign(String client, String xmlContent)
			throws ServiceException {
		log.debug("签名开关{}", this.signSwitch);
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(this.signSwitch)) {
			String singure = TMallXMLParser.parseBankSignXml(xmlContent);
			String sign = "";
			String certId = null;
			if (StringUtils.isNotBlank(singure)) {
				try {
					log.debug("......签名原串[" + (singure) + "]");
					certId = this.cmuSecurityRemoting.getCertId();
					sign = this.cmuSecurityRemoting.sign(null, singure);
					if (StringUtils.isBlank(sign)) {
						log.error("签名失败,签约原串{}", singure);
					} else {
						log.debug("签名成功,签名原串{},签名结果{}", singure, sign);
					}
				} catch (Exception e) {
					log.error("签名失败check sign failed!", e);
					log.error("签名失败，签名原串【{}】", singure);
				}
			}
			xmlContent = TMallXMLParser
					.convertBankXml(sign, certId, xmlContent);
		}
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
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				iTimeout);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, iSoTimeout);
		HttpClient customerHttpClient = new DefaultHttpClient(params);
		return customerHttpClient;
	}

}
