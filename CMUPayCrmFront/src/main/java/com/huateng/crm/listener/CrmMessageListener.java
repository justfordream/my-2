package com.huateng.crm.listener;

import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huateng.cmupay.security.adapter.SecurityHandle;
import com.huateng.core.base.BaseMessageListener;
import com.huateng.core.common.CoreCommon;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.UpayCommon;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.jms.common.JmsMsgHeader;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.util.BeanUtil;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.bean.message.head.Response;
import com.huateng.crm.common.CRMConstant;
import com.huateng.crm.common.parse.Dom4jXMLParser;
import com.huateng.log.LogHandle;

/**
 * 接收核心平台端的jms请求
 * 
 * @author leon
 * 
 */
public class CrmMessageListener extends BaseMessageListener {
	private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 连接超时时间
	 */
	private String connTimeout;
	/**
	 * 接收响应超时时间
	 */
	private String revTimeout;
	@Autowired
	private LogHandle logHandle;
	private SecurityHandle SecurityHandle;

	@SuppressWarnings("rawtypes")
	@Override
	public void onMessage(Message message) {
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
				logger.debug("...接收的核心信息 :{} ",new Object[]{xmlContent});
				if (StringUtils.isNotBlank(xmlContent)) {
					xmlContent = xmlContent.replaceAll("<SvcCont>",
							"<SvcCont><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>").replaceAll("</SvcCont>",
							"]]></SvcCont>");
					// CrmResponseMessage crmMsg =
					// CrmXMLParser.assumlyCrmResponseMessage(xmlContent);
					InterBOSS reqBoss = Dom4jXMLParser.parseXmlContent(xmlContent);
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
					logger.debug("......根据路由信息向CRM:{},发送信息......",new Object[]{routeMsg.getReqIp()});
					logger.debug("......assumly xmlhead is :{} ",new Object[]{reqBoss.getHeader()});
					logger.debug("......assumly xmlbody is :{} ",new Object[]{ reqBoss.getBody()});
					// InterBOSS boss =
					// CrmXMLParser.parseHeadXML(crmMsg.getXmlhead());
					/*
					 * 全网监控日志
					 */
					
					this.logHandle.info(true, CoreConstant.ErrorCode.SUCCESS.getCode(), reqBoss.getTransInfo()
							.getTransIDO(), reqBoss.getBIPType().getActivityCode(), UpayCommon.getTransDesc(reqBoss
							.getBIPType().getActivityCode()), UpayCommon.getProvCodeBySysCode(reqBoss.getRoutingInfo()
							.getOrigDomain()), reqBoss.getRoutingInfo().getOrigDomain(), CoreConstant.ErrorCode.SUCCESS
							.getDesc());
									
					
					// String xmlBody = crmMsg.getXmlbody();
					/*
					 * 加密报文体
					 */
					String decryptBodyText = reqBoss.getSvcCont();
					// String encryptXmlBody =
					// CrmXMLParser.parseEncryptXmlBody(xmlBody);
					String encryptBodyText = this.encryptXmlBody(routeMsg.getReqIp(), decryptBodyText,"",(reqBoss.getBIPType() == null?"":reqBoss.getBIPType().getActivityCode()));
					reqBoss.setSvcCont(encryptBodyText);
					// if (StringUtils.isBlank(xmlBody)) {
					// throw new ServiceException("UPAY-C-2998");// 失败
					// }
					// if(StringUtils.isNotBlank(xmlBody)){
					// xmlBody =
					// "<?xml version='1.0' encoding='UTF-8'?><InterBOSS><SvcCont><![CDATA["+xmlBody+"]]></SvcCont></InterBOSS>";
					// }
					responseText = this.sendMsg(routeMsg, reqBoss.getHeader(), reqBoss.getBody());
					logger.debug("......接收到CRM:{},响应信息:{} ",new Object[]{routeMsg.getReqIp(),responseText});
					/*
					 * 解密报文体
					 */
					InterBOSS rspBoss = Dom4jXMLParser.parseXmlContent(responseText);
					String resEncryptBodyText = rspBoss.getSvcCont();
					String resDecryptBodyText = this.decryptXmlBody(routeMsg.getReqIp(), resEncryptBodyText,rspBoss.getSNReserve().getMsgReceiver(),rspBoss.getBIPType().getActivityCode());
					rspBoss.setSvcCont(resDecryptBodyText);
					// String body = "<SvcCont>" + rspBoss.getSvcCont() +
					// "</SvcCont>";
//					responseText = this.decryptXmlBody(routeMsg.getReqIp(), rspBoss.getSvcCont());
					// if(StringUtils.isNotBlank(responseText)){
					// responseText = "<![CDATA["+responseText+"]]>";
					// rspBoss.setSvcCont(responseText);
					// }
					// responseText = CrmXMLParser.parseInterBOSS(rspBoss);
					/*
					 * 返回信息
					 */
					logger.debug(".......将CRM:{},响应信息发送核心，信息如下:{}",new Object[]{routeMsg.getReqIp(),rspBoss.getXmlContent()});
					
					/*
					 * 报文体为空情况下：CRM响应给前置的
					 * 支付结果通知(BIPCode:BIP1A164,ActivityCode:T1000164)、
					 * 签约结果通知(BIPCode:BIP1A152、BIP1A153,ActivityCode:T1000156)
					 * 发消息给核心，其它业务直接结束不发消息到核心。
					 * 
					 */			
					if(StringUtils.isBlank(rspBoss.getSvcCont())){
						if((rspBoss.getBIPType().getBIPCode().equals(CoreConstant.BIPType.CHARGE)&&rspBoss.getBIPType().getActivityCode().equals(CoreConstant.BIPType.CHARGE_RESULT_NOTICE))||
								(rspBoss.getBIPType().getBIPCode().equals(CoreConstant.BIPType.NET_BIND)&&rspBoss.getBIPType().getActivityCode().equals(CoreConstant.BIPType.BIND_RESULT_NOTICE))||
								   (rspBoss.getBIPType().getBIPCode().equals(CoreConstant.BIPType.BANK_BIND)&&rspBoss.getBIPType().getActivityCode().equals(CoreConstant.BIPType.BIND_RESULT_NOTICE))){
							super.replayMsg(replyToDest, rspBoss.getXmlContent(), reqTxnSeq);
						}						
					}else{
						super.replayMsg(replyToDest, rspBoss.getXmlContent(), reqTxnSeq);
					}					
				} else {
					throw new ServiceException("UPAY-C-2998");
				}
			} else {
				throw new ServiceException("UPAY-C-2998");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());

			ErrorBean bean = null;
			try {
				bean = ErrorConfigUtil.getCrmError(e.getMessage());
			} catch (Exception e1) {
				bean = ErrorConfigUtil.getCrmError("UPAY-C-2998");
			}
			String rspCode = bean.getOuterCode();
			String rspInfo = bean.getDesc();
			InterBOSS boss = new InterBOSS();
			Response res = new Response();
			res.setRspType("2");
			res.setRspDesc(rspInfo);
			res.setRspCode(rspCode);
			boss.setResponse(res);
			try {
				responseText = boss.getXmlContent();
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}

			super.replayMsg(replyToDest, responseText, reqTxnSeq);
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
	 * @throws Exception
	 */
	protected String sendMsg(JmsMsgHeader routeMsg, String xmlHead, String xmlBody) throws ServiceException {
		if (routeMsg == null) {
			throw new ServiceException("UPAY-C-2998");
			// return "路由信息有误";
		}
		String protocol = routeMsg.getProtocol();
		if (StringUtils.isBlank(protocol)) {
			throw new ServiceException("UPAY-C-2998");
		}
		if (!protocol.toLowerCase().equals("http") && !protocol.toLowerCase().equals("https")) {
			throw new ServiceException("UPAY-C-2998");
		}
		String reqIp = routeMsg.getReqIp();
		if (StringUtils.isBlank(reqIp)) {
			throw new ServiceException("UPAY-C-2998");
		}
		String reqPort = routeMsg.getReqPort();
		if (StringUtils.isBlank(reqPort)) {
			throw new ServiceException("UPAY-C-2998");
		}
		String reqPath = routeMsg.getReqPath() == null ? "" : routeMsg.getReqPath();
		logger.debug("协议类型:{},请求IP:{},请求端口号:{},请求地址:{} ",new Object[]{protocol,reqIp,reqPort,reqPath});
		String responseText = null;
		try {
			String requestURL = protocol + "://" + reqIp + ":" + reqPort + reqPath;
			logger.debug("Request URL is:{} ",new Object[]{requestURL});

			HttpClient httpclient = this.getHttpClient(connTimeout, revTimeout);
			HttpPost post = new HttpPost(requestURL);
			StringBody head = new StringBody(xmlHead, Charset.forName(CoreConstant.MSG_ENCODING));
			StringBody body = new StringBody(xmlBody, Charset.forName(CoreConstant.MSG_ENCODING));
			MultipartEntity entity = new MultipartEntity();
			entity.addPart(CRMConstant.XML_HEAD, head);
			entity.addPart(CRMConstant.XML_BODY, body);
			post.setEntity(entity);
			HttpResponse returnMsg = httpclient.execute(post);
			if (HttpStatus.SC_OK == returnMsg.getStatusLine().getStatusCode()) {
				HttpEntity entitys = returnMsg.getEntity();
				if (entitys != null) {
					responseText = EntityUtils.toString(entitys, CoreConstant.MSG_ENCODING);
				}
			}
			httpclient.getConnectionManager().shutdown();
		} catch (ConnectTimeoutException e) {
			logger.error(e.getMessage());
			throw new ServiceException("UPAY-C-5A03");// 系统错误
		} catch (SocketTimeoutException e) {
			logger.error(e.getMessage());
			throw new ServiceException("UPAY-C-5A07");// 超时未收到响应
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException("UPAY-C-2998");
		}
		if (StringUtils.isBlank(responseText)) {
			throw new ServiceException("UPAY-C-2998");
		}
		return responseText;
	}

	/**
	 * 对报文体解密
	 * 
	 * @param encryptText
	 *            报文体密文
	 * @return 明文
	 */
	@Override
	protected String decryptXmlBody(String client, String encryptText,String provCode,String tradeCode) throws ServiceException {
		
//		String decryptSwitch = CoreCommon.getDecryptSwitch();
//		String decryptText = encryptText;
//		
//		if (CoreCommon.checkIsDecrypt(decryptSwitch, provCode,"",tradeCode)) {
//			logger.debug("......调用CRM前置解密方法......");
//			logger.debug("......发起方:{},报文体密文:{},解密开关:{}......",new Object[]{client,encryptText,decryptSwitch});
//			try {
//				decryptText = this.SecurityHandle.decryptPIN(encryptText);
//			} catch (Exception e) {
//				logger.debug("......解密失败,失败原因:{}",new Object[]{ e.getMessage()});
//				throw new ServiceException("UPAY-C-2998");
//			}
//			logger.debug("......发起方:{},报文体密文:{},解密后明文:{}......",new Object[]{client,encryptText,decryptText});
//		}
//		return decryptText;
		return null;
	}

	@Override
	protected String encryptXmlBody(String client, String decryptText,String provCode,String tradeCode) throws ServiceException {
		
//		String encryptSwitch = CoreCommon.getEncryptSwitch();
//		String encryptText = decryptText;
//		
//		if (CoreCommon.checkIsEncrypt(encryptSwitch, provCode,"",tradeCode)) {
//			logger.debug("......调用CRM前置加密方法......");
//			logger.debug("......落地方:{},报文体明文:{},加密开关:{}......",new Object[]{client,decryptText,encryptSwitch});
//			try {
//				encryptText = this.SecurityHandle.encryptPIN(decryptText);
//			} catch (Exception e) {
//				logger.debug("......加密失败,失败原因：" + e.getMessage());
//				throw new ServiceException("UPAY-C-2998");
//			}
//			logger.debug("......落地方:{},报文体明文:{},加密后密文:{}......",new Object[]{client,encryptText,decryptText});
//		}
//		
//		return encryptText;
		return null;
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
	public SecurityHandle getSecurityHandle() {
		return SecurityHandle;
	}

	public void setSecurityHandle(SecurityHandle securityHandle) {
		SecurityHandle = securityHandle;
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
	
}
