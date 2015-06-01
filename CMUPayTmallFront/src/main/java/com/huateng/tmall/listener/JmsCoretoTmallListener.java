package com.huateng.tmall.listener;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.TimeUtil;
import com.huateng.log.MessageLogger;
import com.huateng.tmall.bean.head.GPay;
import com.huateng.tmall.bean.head.Header;
import com.huateng.tmall.bean.head.Sign;
import com.huateng.tmall.common.parse.TMallXMLParser;
/**
 * @author cmt
 *
 */
public class JmsCoretoTmallListener implements MessageListener {
	private Logger log = LoggerFactory.getLogger(JmsCoretoTmallListener.class);

	private MessageLogger messlog = MessageLogger.getLogger(JmsCoretoTmallListener.class);

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
	
	private @Value("${url}") String url;
	private @Value("${sign.switch}") String signSwitch;
	
	
	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}

	public String getSignSwitch() {
		return signSwitch;
	}


	public void setSignSwitch(String signSwitch) {
		this.signSwitch = signSwitch;
	}

	/**
	 *
	 */

	 ExecutorService executor = null;

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


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(final Message message) {
		log.info("......JmsCoretoTmallListener 从核心监听一个消息......");
		/*
		 * 接受到文字消息
		 */
				String xmlContent = null;
				String responseText="";
				try
				{
					if (message instanceof TextMessage)
					{
						 xmlContent = ((TextMessage) message).getText();
						log.info("...从核心接收的消息内容: " + xmlContent);
						if (StringUtils.isNotBlank(xmlContent))
						{
							GPay gPay = TMallXMLParser.parseXmlContent(xmlContent);
							Header header = gPay.getHeader();
							log.info("#" + header.getReqTransID() + "#消息处理时间#" + TimeUtil.now());
							log.info("......发送充值结果给天猫的地址[" + url+ "]#### 发送的消息内容:" + xmlContent);
							messlog.info("......发送充值结果给天猫的地址[" + url+ "]#### " );
							responseText=sendMsg(url, xmlContent, header.getReqTransID());
							log.info("天猫充值交易完成,运营商返回给天猫前置的消息内容：{}",responseText);
						}
						else{
							throw new ServiceException("UPAY-B-015A05");//
						}
					}
					else
					{
						log.error("......JmsCoretoTmallListener 接收充值消息内容失败  (消息为空)......");
						messlog.error("......JmsCoretoTmallListener 接收充值消息内容失败  (消息为空)......");
						throw new ServiceException("UPAY-B-015A05");// 系统错误
					}
				}
				catch (Exception e)
				{
					log.error("处理核心到天猫的消息异常", e);
					log.error("天猫前置解析核心的充值结果通知异常{}",xmlContent);
					messlog.error("天猫前置解析核心的充值结果通知异常{}",xmlContent);
				}
	
	}
	
//	/**
//	 * 异常返回报文
//	 * 
//	 * @param errorCode
//	 * @param xml
//	 * @param request
//	 * @param response
//	 */
//	private void writeReturnMessage(String errorCode, String xml) {
//		HttpPost post;
//		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
//		UrlEncodedFormEntity uefEntity;
//		HttpResponse response;
//		try {
//			/*
//			 * 设置请求公共信息(编码等)
//			 */
//			/*this.settingRequest(request);*/
//			/*
//			 * 设置响应公共信息(编码等)
//			 */
//			/*this.settingResponse(response);*/
//			ErrorBean bean = null;
//			GPay gPay = null;
//			HttpClient httpClient;
//			Header header = null;
//			if(StringUtils.isNotBlank(xml)){
//				try {
//					bean = ErrorConfigUtil.getBankError(errorCode);
//					gPay = TMallXMLParser.parseXmlContent(xml);
//					header = gPay.getHeader();
//					if(header == null){
//						header = new Header();
//					}
//				} catch (Exception e1) {
//					gPay = new GPay();
//					header = new Header();
//					bean = ErrorConfigUtil.getBankError("UPAY-B-015A06");
//				}
//			}else{
//				gPay = new GPay();
//				header = new Header();
//				bean = ErrorConfigUtil.getBankError("UPAY-B-015A05");
//			}
//			
//			String rspCode = bean.getOuterCode();
//			String rspDesc = bean.getDesc();
//
//			/*
//			 * 应答/错误代码
//			 */
//			header.setRspCode(rspCode);
//			/*
//			 * 应答/错误描述
//			 */
//			header.setRspDesc(rspDesc);
//			/*
//			 * 交易动作代码
//			 */
//			header.setActionCode(CommonConstant.ActionCode.Respone.getValue());
//			/*
//			 * 接收方交易日期
//			 */
//			header.setRcvDate(DateUtil.getDateyyyyMMdd());
//			/*
//			 * 接收方交易流水号
//			 */
//			header.setRcvTransID(Serial.genSerialNoss(CommonConstant.Sequence.IntSeq.toString()));
//			/*
//			 * 接收方时间戳
//			 */
//			header.setRcvDateTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//			
//			gPay.setHeader(header);
//			
//			//there is no body in resText 
//			gPay.setBody("");			
//			log.error("发起方交易流水号:{},错误码:{},错误描述:{}", new Object[]{header.getReqTransID(),header.getRspCode(),header.getRspDesc()});
//			messlog.error("发起方交易流水号:{},错误码:{},错误描述:{}", new Object[]{header.getReqTransID(),header.getRspCode(),header.getRspDesc()});
//			String resText = TMallXMLParser.parseGPay(gPay);			
//			//TMallService tmallService = (TMallService) ServiceFactory.getInstance().findService("tmallService");
//			//resText = tmallService.sign("", resText);
//			resText = this.sign("", resText);
//			log.info("<异常情况>天猫前置响应给天猫的响应信息为:[{}]",resText);
//			//httpClient = this.getHttpClient(connTimeout, revTimeout);
//			post = new HttpPost(url);
//			formParams.add(new BasicNameValuePair("xmldata", resText));
//			uefEntity = new UrlEncodedFormEntity(formParams,CoreConstant.MSG_ENCODING);
//			post.setEntity(uefEntity);
//			//如果发送请求失败，重试3次
//			for (int i = 0; i < CoreConstant.TRY_TIMES; i++)
//			{
//				try
//				{
//					httpClient = this.getHttpClient(connTimeout, revTimeout);
//					response = httpClient.execute(post);
//					
//					log.info("http response : " + response.getStatusLine().getStatusCode());
//					log.debug("第 "+(i + 1)+" 次,发送");
//					log.debug("发送给浙商的报文为："+resText);
//					messlog.debug("第 "+(i + 1)+" 次,发送");
//					if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode())
//					{
//						HttpEntity entitys = response.getEntity();
//						if (entitys != null)
//						{
//						String	responseText = EntityUtils.toString(entitys, CoreConstant.MSG_ENCODING);
//						responseText = URLDecoder.decode(responseText,  CoreConstant.MSG_ENCODING);  
//						if(null == responseText || "".equals(responseText)){
//							log.info("第"+(i + 1)+"次,运营商返回给天猫前置的消息内容为空进行重发");
//							messlog.info("第"+(i + 1)+"次,运营商返回给天猫前置的消息内容为空进行重发");
//							continue;
//						}else{
//							log.info("运营商返回给天猫前置的消息内容："+responseText);
//							messlog.info("异常时,运营商返回给天猫前置的的消息内容为："+responseText);
//							httpClient.getConnectionManager().shutdown();
//							break;
//						}
//						
//						
//						}
//
//					}
//				}
//				catch (ConnectTimeoutException e)
//				{
//					if (i < CoreConstant.TRY_TIMES-1)
//					{
//						log.warn("重发第  " + (i + 1) + "次 " + e);
//						continue;
//					}
//					else if (i == CoreConstant.TRY_TIMES-1)
//					{
//						log.error("重发最后一次 " + e);
//						throw new ServiceException("UPAY-B-015A03");// 系统错误
//					}
//				}
//				catch (SocketTimeoutException e)
//				{
//					if (i < CoreConstant.TRY_TIMES-1)
//					{
//						log.warn("重发第  " + (i + 1) + "次 " + e);
//						continue;
//					}
//					else if (i == CoreConstant.TRY_TIMES-1)
//					{
//						log.error("重发最后一次 " + e);
//						throw new ServiceException("UPAY-B-015A07");// 超时未收到响应
//					}
//				}
//				catch (Exception e)
//				{
//					if (i < CoreConstant.TRY_TIMES-1)
//					{
//						log.warn("重发第  " + (i + 1) + "次 " + e);
//						continue;
//					}
//					else if (i == CoreConstant.TRY_TIMES-1)
//					{
//						log.error("重发最后一次 " + e);
//						throw new ServiceException("UPAY-B-015A06");// 错误
//					}
//				}
//			}
//			
//			
//		} catch (Exception e1) {
//			log.error("",e1);
//		}
//	}
//	
//	
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
	protected String sendMsg(String url, final String xmlContent, String tranid) throws ServiceException
	{
	
		String responseText = null;
		HttpClient httpClient;
		HttpPost post;
		String newXmlContent;
		UrlEncodedFormEntity uefEntity;
		HttpResponse response;
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		try
		{
			log.debug("请求发送的 URL地址是: " + url);
			post = new HttpPost(url);
			log.info("......签名以前的消息内容 : " + xmlContent);

			log.debug("#" + tranid + "#begin#" + TimeUtil.now());
			newXmlContent = this.sign("", xmlContent);
			log.debug("#" + tranid + "#end#" + TimeUtil.now());
			log.info("......签名以后的消息内容: " + newXmlContent);
			
			formParams.add(new BasicNameValuePair("xmldata", newXmlContent));
			uefEntity = new UrlEncodedFormEntity(formParams,CoreConstant.MSG_ENCODING);
			post.setEntity(uefEntity);
		}
		catch (Exception e1)
		{
			log.error("sendMsg():" , e1);
			log.error("签名,组装报文失败{}",xmlContent);
			throw new ServiceException("UPAY-B-015A05");// 系统错误
		}
		//如果发送请求失败，重试3次
		for (int i = 0; i < CoreConstant.TRY_TIMES; i++)
		{
			try
			{
				log.info("第 {} 次,发送",i + 1);
				log.info("发送给浙商的报文为："+newXmlContent);
				httpClient = this.getHttpClient(connTimeout, revTimeout);
				response = httpClient.execute(post);
				log.info("http response : " + response.getStatusLine().getStatusCode());

				//messlog.debug("第 {} 次,发送",i + 1);
				if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode())
				{
					HttpEntity entitys = response.getEntity();
					if (entitys != null)
					{
						responseText = EntityUtils.toString(entitys, CoreConstant.MSG_ENCODING);
						responseText = URLDecoder.decode(responseText,  CoreConstant.MSG_ENCODING);  
						
						if(null == responseText || "".equals(responseText)){
							log.info("第{}次,运营商返回给天猫前置的消息内容为空进行重发",i + 1);
							messlog.warn("第{}次,运营商返回给天猫前置的消息内容为空进行重发",i + 1);
							continue;
						}else{
							
							httpClient.getConnectionManager().shutdown();
							break;
						}
						
					}

				}
			}
			catch (ConnectTimeoutException e)
			{
				if (i < CoreConstant.TRY_TIMES-1)
				{
					log.warn("重发第  {}次 " ,i + 1);
					continue;
				}
				else if (i == CoreConstant.TRY_TIMES-1)
				{
					log.error("重发最后一次 " + e);
					throw new ServiceException("UPAY-B-015A03");// 系统错误
				}
			}
			catch (SocketTimeoutException e)
			{
				if (i < CoreConstant.TRY_TIMES-1)
				{
					log.warn("重发第  {}次 " ,i + 1);
					continue;
				}
				else if (i == CoreConstant.TRY_TIMES-1)
				{
					log.error("重发最后一次 " + e);
					throw new ServiceException("UPAY-B-015A07");// 超时未收到响应
				}
			}
			catch (Exception e)
			{
				if (i < CoreConstant.TRY_TIMES-1)
				{
					log.warn("重发第  {}次 ",i + 1);
					continue;
				}
				else if (i == CoreConstant.TRY_TIMES-1)
				{
					log.error("重发最后一次 " + e);
					throw new ServiceException("UPAY-B-015A06");// 错误
				}
			}
		}
		return responseText;
	}
	
	public String sign(String client, String xmlContent) throws ServiceException
	{
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch))
		{
			String singure = TMallXMLParser.parseBankSignXml(xmlContent);
			String certId = null;
			if (StringUtils.isNotBlank(singure))
			{
				try
				{
					log.debug("......签名串[" + (singure) + "]");
					singure = this.remoteService.sign(null, singure);
					certId = this.remoteService.getCertId();
					log.debug("......签名后的串[" + singure + "]");
				}
				catch (Exception e)
				{
					log.error("签名失败!", e);
					log.error("......取得签名串失败..{}....",xmlContent);
					messlog.error("签名失败{}",xmlContent);
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
				messlog.error("组装签名串异常!{}",xmlContent);
			}
		}
		return xmlContent;
	}
	
	/**
	 * 
	 * @param timeout
	 * @param soTimeout
	 * @return
	 */
	private HttpClient getHttpClient(String timeout, String soTimeout)
	{
		HttpParams params = new BasicHttpParams();
		int iTimeout = 60000;
		if (StringUtils.isNotBlank(timeout))
		{
			iTimeout = Integer.valueOf(timeout).intValue();
		}
		int iSoTimeout = 60000;
		if (StringUtils.isNotBlank(soTimeout))
		{
			iSoTimeout = Integer.valueOf(soTimeout).intValue();
		}
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, iTimeout);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, iSoTimeout);
		HttpClient customerHttpClient = new DefaultHttpClient(params);
		return customerHttpClient;
	}
}
