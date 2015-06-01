/**
 * 
 */
package com.huateng.core.remoting.impl;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.CoreConstant.TenPayMsg;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.jms.common.JmsMsgHeader;
import com.huateng.core.remoting.TenPayRemoting;
import com.huateng.core.util.DateUtil;
import com.huateng.core.util.JacksonUtils;
import com.huateng.log.LogHandle;
import com.huateng.third.logFormat.MessageLogger;
import com.huateng.third.service.TUPayRemoteService;

/**
 * @author Manzhizhen
 *
 */
public class TenPayRemotingImpl implements TenPayRemoting {
	/**
	 * 验签服务
	 */
	@Autowired
	private TUPayRemoteService remoteService;
	
	/**
	 * 验签开关
	 */
	private @Value("${check.tenpay.switch}")
	String checkSwitch;
	/**
	 * 签名开关
	 */
	private @Value("${check.tenpay.switch}")
	String signSwitch;
	
	/**
	 * 连接超时时间
	 */
	private @Value("${http.conn.timeout}")
	String connTimeout;

	/**
	 * 接收响应超时时间
	 */
	private @Value("${http.rev.timeout}")
	String revTimeout;
	
	private LogHandle logHandle;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger log = MessageLogger.getLogger(getClass());

	@SuppressWarnings("finally")
	@Override
	public Object sendMsg(String header, Map<String, String> paramsMap) {
		String responseStr = null;
		Map<String, String> responseMap = null;
		try {
			if (MapUtils.isNotEmpty(paramsMap)) {
				String orderId = paramsMap.get("#orderId");
				String intTxnSeq = paramsMap.get("#intTxnSeq");
				String merId = paramsMap.get(TenPayMsg.PARTNER.getValue());
				String notifyId = paramsMap.get(TenPayMsg.NOTIFY_ID.getValue());
				
				/**
				 * 路由信息
				 */
				JmsMsgHeader routeMsg = JacksonUtils.json2Bean(header,
						JmsMsgHeader.class);
				logger.info("（财付通）支付前置接受核心的请求,报文内容{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
						new Object[] { DateUtil.printMapLog(paramsMap), orderId, intTxnSeq, merId, notifyId});

				this.logHandle.info(true,
						CoreConstant.ErrorCode.SUCCESS.getCode(),
						orderId, "", "", "", "",
						CoreConstant.ErrorCode.SUCCESS.getDesc());

				log.debug("订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
						new Object[] {orderId, intTxnSeq, merId, notifyId});

				logger.info("......根据路由信息开始向财付通[{}]发送信息 :{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}", new Object[] {
						routeMsg.getReqIp(), DateUtil.printMapLog(paramsMap), orderId, intTxnSeq, merId, notifyId});
				logger.debug("......根据路由信息开始向财付通[{}]发送信息 :{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}", new Object[] {
						routeMsg.getReqIp(), DateUtil.printMapLog(paramsMap), orderId, intTxnSeq, merId, notifyId});

				/**
				 * 开始发送消息
				 */
				responseStr = sendToTenPay(routeMsg, paramsMap);

				logger.info("......支付前置接收到财付通[{}]响应信息:{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
						new Object[] { routeMsg.getReqIp(), responseStr, orderId, intTxnSeq, merId, notifyId});
				logger.debug("......支付前置接收到财付通[{}]响应信息:{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
						new Object[] { routeMsg.getReqIp(), responseStr, orderId, intTxnSeq, merId, notifyId});
				log.info("......支付前置接收到财付通[{}]响应信息:{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
						new Object[] { routeMsg.getReqIp(), responseStr, orderId, intTxnSeq, merId, notifyId});
				
				responseMap = switchXml2Map(responseStr);
				
				/**
				 * 验签
				 */
				responseMap = checkSign(responseMap);

			} else {
				logger.error("......支付前置TenPayRemotingImpl接收核心的消息Map为空！");
				log.error("......支付前置TenPayRemotingImpl接收核心的消息Map为空！");
				throw new ServiceException("UPAY-B-015A05");
			}
			
		} catch (ServiceException e) {
			logger.error("......支付前置TUpayRemotingImpl处理核心消息失败,失败原因:"
					+ e.getMessage());
			log.error("......支付前置TUpayRemotingImpl处理核心消息失败,失败原因:"
					+ e.getMessage());
			
		} finally {
			return responseMap;
		}
	}
	
	/**
	 * 根据路由信息主动发送信息到指定路由
	 * @param routeMsg
	 *            路由信息
	 * @param paramsMap
	 *            发送的信息
	 * @return
	 * @throws ServiceException
	 * @throws Exception
	 */
	protected String sendToTenPay(
			JmsMsgHeader routeMsg, final Map<String, String> paramsMap) throws ServiceException {
		
		String orderId = paramsMap.get("#orderId");
		String intTxnSeq = paramsMap.get("#intTxnSeq");
		String merId = paramsMap.get(TenPayMsg.PARTNER.getValue());
		String notifyId = paramsMap.get(TenPayMsg.NOTIFY_ID.getValue());
		
		paramsMap.remove("#orderId");
		paramsMap.remove("#intTxnSeq");
		
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

			if (StringUtils.isBlank(connTimeout)
					|| StringUtils.isBlank(revTimeout)) {
				throw new ServiceException("UPAY-B-015A06");
			}

			/**
			 * 开始进行签名
			 */
			sign(paramsMap);

			/**
			 * 发送消息
			 */
			HttpParams params = new BasicHttpParams();
			int iTimeout = 60000;
			if (StringUtils.isNotBlank(connTimeout)) {
				iTimeout = Integer.valueOf(connTimeout).intValue();
			}
			
			int iSoTimeout = 60000;
			if (StringUtils.isNotBlank(revTimeout)) {
				iSoTimeout = Integer.valueOf(revTimeout).intValue();
			}
			
			params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, iTimeout);
			params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, iSoTimeout);
			HttpClient customerHttpClient = new DefaultHttpClient(params);

			HttpPost post = new HttpPost(requestURL);
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			for(Map.Entry<String, String> entry : paramsMap.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, CoreConstant.MSG_ENCODING);
			post.setEntity(uefEntity);
			
			HttpResponse response = customerHttpClient.execute(post);
			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entitys = response.getEntity();
				if (entitys != null) {
					responseText = EntityUtils.toString(entitys, CoreConstant.MSG_ENCODING);
				}
			}
			
			customerHttpClient.getConnectionManager().shutdown();
			
		} catch (SocketTimeoutException e) {
			logger.error("支付前置连接财付通超时:{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
					new Object[]{e.getMessage(), orderId, intTxnSeq, merId, notifyId});
			log.error("支付前置连接财付通超时:{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
					new Object[]{e.getMessage(), orderId, intTxnSeq, merId, notifyId});
			throw new ServiceException("UPAY-B-015A07");// 超时未收到响应
			
		} catch (ConnectException e) {
			logger.error("支付前置连接财付通出错:{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
					new Object[]{e.getMessage(), orderId, intTxnSeq, merId, notifyId});
			log.error("支付前置连接财付通出错:{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
					new Object[]{e.getMessage(), orderId, intTxnSeq, merId, notifyId});
			throw new ServiceException("UPAY-B-015A07");
			
		} catch (Exception e) {
			logger.error("支付前置发送消息到财付通失败:{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
					new Object[]{e.getMessage(), orderId, intTxnSeq, merId, notifyId});
			log.error("支付前置发送消息到财付通失败:{},订单号:{},内部交易流水号:{},商户号:{},财付通通知ID:{}",
					new Object[]{e.getMessage(), orderId, intTxnSeq, merId, notifyId});
			throw new ServiceException("UPAY-B-015A06");// 未知错误
		}
		
		if (StringUtils.isEmpty(responseText)) {
			throw new ServiceException("UPAY-B-015A07");
		}
		
		return responseText;
	}

	/**
	 * 对Map数据进行签名
	 * 
	 * @param client
	 * @param xmlContent
	 * @return
	 * @throws ServiceException
	 */
	public Map<String, String> sign(Map<String, String> paramsMap)
			throws ServiceException {
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch)) {
			logger.debug("......开始去调用加密机来执行支付前置签名.....");
			
			log.info("签名前报文{}", DateUtil.printMapLog(paramsMap));
			logger.info("......签名前报文{}", DateUtil.printMapLog(paramsMap));
			
			/**
			 * 去加密机调用签名方法
			 */
			paramsMap = remoteService.tenPaySign(paramsMap);

			log.info("签名成功,签名后报文{}", DateUtil.printMapLog(paramsMap));
			logger.info("......签名成功,签名后报文{}", DateUtil.printMapLog(paramsMap));
		}
		return paramsMap;
	}
	
	public Map<String, String> checkSign(Map<String, String> paramsMap) throws ServiceException {
		if (MapUtils.isEmpty(paramsMap)) {
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}
		
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(checkSwitch)) {
			logger.debug("......调用支付前置验签方法......");

			String merId = paramsMap.get(TenPayMsg.PARTNER.getValue());
			String thrOrderId = paramsMap.get(TenPayMsg.TRANSACTION_ID.getValue());
			String orderId = paramsMap.get(TenPayMsg.OUT_TRADE_NO.getValue());
			
			/*
			 * 全网监控日志
			 */
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					orderId, "", "", merId, "",
					CoreConstant.ErrorCode.SUCCESS.getDesc());

			boolean isValid = remoteService.tenPayVerify(paramsMap);
			
			if (!isValid) {
				logger.error("......验签失败......订单号:{},商户代码:{},财付通订单号:{}", new Object[]{orderId, merId, thrOrderId});
				log.error("......验签失败......订单号:{},商户代码:{},财付通订单号:{}", new Object[]{orderId, merId, thrOrderId});
				throw new ServiceException("UPAY-B-014A06");// 签名验证失败
			}
		}

		return paramsMap;
	}
	
	/**
	 * 支付结果通知查询的返回信息是XML格式的，需要转换成Map
	 * @param xmlStr
	 * @return
	 * @throws DocumentException
	 */
	private Map<String, String> switchXml2Map(String xmlStr) throws DocumentException {
		Map<String, String> responseMap = new HashMap<String, String>();
		
		Document doc = DocumentHelper.parseText(xmlStr);
		Element rootE = doc.getRootElement();
		if(rootE != null) {
			List<Element> elements = rootE.elements();
			if(elements != null) {
				for(Element element : elements){
					responseMap.put(element.getName().trim(), element.getTextTrim());
				}
			}
		}
		
		return responseMap;
	}
	
	
	public void setCheckSwitch(String checkSwitch) {
		this.checkSwitch = checkSwitch;
	}

	public void setSignSwitch(String signSwitch) {
		this.signSwitch = signSwitch;
	}

	public void setConnTimeout(String connTimeout) {
		this.connTimeout = connTimeout;
	}

	public void setRevTimeout(String revTimeout) {
		this.revTimeout = revTimeout;
	}

	public void setLogHandle(LogHandle logHandle) {
		this.logHandle = logHandle;
	}
}
