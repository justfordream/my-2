package com.huateng.core.remoting.impl;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.params.HttpMethodParams;
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
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.common.CoreConstant;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.jms.common.JmsMsgHeader;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.remoting.CrmTmallRemoting;
import com.huateng.core.util.JacksonUtils;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.bean.message.head.Response;
import com.huateng.crm.common.CRMConstant;
import com.huateng.crm.logFormat.MessageLogger;
import com.huateng.log.LogHandle;

/**
 * 天猫应急
 * 
 */
public class CrmTmallRemotingImpl implements CrmTmallRemoting {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger log = MessageLogger.getLogger(getClass());
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

	// private SecurityHandle SecurityHandle;

	@SuppressWarnings("finally")
	@Override
	public String sendMsg(String header, String... args) {
		String resp2CoreText = "";
		String responseText = "";
		try {
			if (StringUtils.isNotBlank(args[0])
					&& StringUtils.isNotBlank(args[1])) {
				String xmlHeader = args[0];
				args[1] = args[1].replaceAll("<SvcCont>",
						"<SvcCont><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>").replaceAll("</SvcCont>",
						"]]></SvcCont>");
				String xmlBody = args[1];
				// 获取路由信息
				JmsMsgHeader routeMsg = JacksonUtils.json2Bean(header,
						JmsMsgHeader.class);
				/*
				 * 根据路由信息发送数据
				 */
				logger.info("......根据路由信息向CRM[{}]发送信息......",
						new Object[] { routeMsg.getReqIp() });
				logger.info("......assumly xmlhead is :{}",
						new Object[] { xmlHeader });
				logger.info("......assumly xmlbody is :{}",
						new Object[] { xmlBody });
				/*
				 * 全网监控日志
				 */
				// this.logHandle.info(true,
				// CoreConstant.ErrorCode.SUCCESS.getCode(),
				// reqBoss.getTransInfo().getTransIDO(),
				// reqBoss.getBIPType().getActivityCode(),
				// UpayCommon.getTransDesc(reqBoss.getBIPType().getActivityCode()),
				// UpayCommon.getProvCodeBySysCode(reqBoss.getRoutingInfo().getOrigDomain()),
				// reqBoss.getRoutingInfo().getOrigDomain(),CoreConstant.ErrorCode.SUCCESS.getDesc());

				// this.logHandle.info(true,
				// CoreConstant.ErrorCode.SUCCESS.getCode(),
				// reqBoss.getTransInfo().getTransIDO(),
				// reqBoss.getBIPType().getActivityCode(),
				// UpayCommon.getTransDesc(reqBoss.getBIPType().getActivityCode()),
				// "",
				// reqBoss.getRoutingInfo().getOrigDomain(),CoreConstant.ErrorCode.SUCCESS.getDesc());
				/*
				 * 加密报文体
				 */
				// String decryptBodyText = reqBoss.getSvcCont();
				// String encryptBodyText =
				// this.encryptXmlBody(routeMsg.getReqIp(), decryptBodyText,
				// "",(reqBoss.getBIPType() == null ? "" :
				// reqBoss.getBIPType().getActivityCode()));
				// reqBoss.setSvcCont(encryptBodyText);
				// 发送消息到网状网
				responseText = this.sendMsg(routeMsg, xmlHeader, xmlBody);
				logger.info("......接收到CRM:[{}]响应信息:{}",
						new Object[] { routeMsg.getReqIp(), responseText });
				/*
				 * 解密报文体
				 */
				// InterBOSS rspBoss =
				// Dom4jXMLParser.parseXmlContent(responseText);
				// String resEncryptBodyText = rspBoss.getSvcCont();
				// String resDecryptBodyText =
				// this.decryptXmlBody(routeMsg.getReqIp(),
				// resEncryptBodyText,rspBoss.getSNReserve().getMsgReceiver(),rspBoss.getBIPType().getActivityCode());
				// rspBoss.setSvcCont(resDecryptBodyText);
				/*
				 * 返回信息
				 */
				logger.info(".......将CRM:[{}]响应信息发送天猫前置，信息如下:{}", new Object[] {
						routeMsg.getReqIp(), responseText });
				resp2CoreText = responseText;
				/*
				 * 报文体为空情况下：CRM响应给前置的
				 * 支付结果通知(BIPCode:BIP1A164,ActivityCode:T1000164)、
				 * 签约结果通知(BIPCode:BIP1A152、BIP1A153,ActivityCode:T1000156)
				 * 发消息给核心，其它业务直接结束不发消息到核心。
				 */
				// if(StringUtils.isBlank(rspBoss.getSvcCont())){
				// if((rspBoss.getBIPType().getBIPCode().equals(CoreConstant.BIPType.CHARGE)&&rspBoss.getBIPType().getActivityCode().equals(CoreConstant.BIPType.CHARGE_RESULT_NOTICE))||
				// (rspBoss.getBIPType().getBIPCode().equals(CoreConstant.BIPType.NET_BIND)&&rspBoss.getBIPType().getActivityCode().equals(CoreConstant.BIPType.BIND_RESULT_NOTICE))||
				// (rspBoss.getBIPType().getBIPCode().equals(CoreConstant.BIPType.BANK_BIND)&&rspBoss.getBIPType().getActivityCode().equals(CoreConstant.BIPType.BIND_RESULT_NOTICE))){
				// resp2CoreText = rspBoss.getXmlContent();
				// }
				// }else{
				// resp2CoreText = rspBoss.getXmlContent();
				// }
			} else {
				logger.error("省前置接收天猫前置的报文内容为空");
				log.error("省前置接收天猫前置的报文内容为空");
				throw new ServiceException("UPAY-C-2998");
			}
		} catch (ServiceException e) {
			logger.error(e.getMessage());

			ErrorBean bean = null;
			ErrorBean bean1 = null;
			try {
				bean = ErrorConfigUtil.getCrmError(e.getMessage());
			} catch (Exception e1) {
				bean = ErrorConfigUtil.getCrmError("UPAY-C-2998");
			}
			
			bean1 = ErrorConfigUtil.getCrmError("UPAY-C-2998");			
			String rspCode = bean1.getOuterCode();
			String rspInfo = bean1.getDesc();			
			InterBOSS boss = new InterBOSS();
			Response res = new Response();
			res.setRspType("2");
			res.setRspDesc(rspInfo);
			res.setRspCode(rspCode);
			boss.setResponse(res);
			
			//---------------------------
			if(!bean.getOuterCode().equals("2998")){
				rspCode = bean.getOuterCode();
				rspInfo = bean.getDesc();
				StringBuffer sb = new StringBuffer();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				sb.append("<PaymentRsp><RspCode>");
				sb.append(rspCode);
				sb.append("</RspCode>");
				sb.append("<RspInfo>");
				sb.append(rspInfo);
				sb.append("</RspInfo></PaymentRsp>");			
				boss.setSvcCont(sb.toString());	
			}					
			//---------------------------
			
			log.error("错误码:{},错误描述:{}", new Object[]{boss.getResponse().getRspCode(),boss.getResponse().getRspDesc()});						
			try {
				resp2CoreText = boss.getXmlContent();
				logger.error("<异常情况>省前置响应给天猫前置的响应信息为:{}", resp2CoreText);
			} catch (ServiceException e1) {
				logger.error("", e1);
			}
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
	 * @throws Exception
	 */
	protected String sendMsg(JmsMsgHeader routeMsg, String xmlHead,
			String xmlBody) throws ServiceException {
		if (routeMsg == null) {
			throw new ServiceException("UPAY-C-2998");
			// return "路由信息有误";
		}
		String protocol = routeMsg.getProtocol();
		if (StringUtils.isBlank(protocol)) {
			throw new ServiceException("UPAY-C-2998");
		}
		if (!protocol.toLowerCase().equals("http")
				&& !protocol.toLowerCase().equals("https")) {
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
		String reqPath = routeMsg.getReqPath() == null ? "" : routeMsg
				.getReqPath();
		logger.debug("协议类型:{},请求IP:{},请求端口号:{},请求地址:{}", new Object[] {
				protocol, reqIp, reqPort, reqPath });
		String responseText = null;
		try {
			String requestURL = protocol + "://" + reqIp + ":" + reqPort
					+ reqPath;
			logger.debug("Request URL is:{}", new Object[] { requestURL });
			// connTimeout = CoreCommon.getConnectionTimeOut();
			// revTimeout = CoreCommon.getReceiveTimeOut();
			if (StringUtils.isBlank(connTimeout)
					|| StringUtils.isBlank(revTimeout)) {
				throw new ServiceException("UPAY-C-2998");
			}
			HttpClient httpclient = this.getHttpClient(connTimeout, revTimeout);
			httpclient.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET,
					CoreConstant.MSG_ENCODING);
			HttpPost post = new HttpPost(requestURL);
			StringBody head = new StringBody(xmlHead,
					Charset.forName(CoreConstant.MSG_ENCODING));
			StringBody body = new StringBody(xmlBody,
					Charset.forName(CoreConstant.MSG_ENCODING));
			MultipartEntity entity = new MultipartEntity();
			entity.addPart(CRMConstant.XML_HEAD, head);
			entity.addPart(CRMConstant.XML_BODY, body);
			post.setEntity(entity);
			HttpResponse returnMsg = httpclient.execute(post);
			if (HttpStatus.SC_OK == returnMsg.getStatusLine().getStatusCode()) {
				HttpEntity entitys = returnMsg.getEntity();
				if (entitys != null) {
					responseText = EntityUtils.toString(entitys,
							CoreConstant.MSG_ENCODING);
				}
			}
			httpclient.getConnectionManager().shutdown();
		} catch (ConnectTimeoutException e) {
			logger.error(e.getMessage());
			log.error("省前置连接网状网超时");
			throw new ServiceException("UPAY-C-5A07");// 超时
		} catch (SocketTimeoutException e) {
			logger.error(e.getMessage());
			log.error("省前置连接网状网超时");
			throw new ServiceException("UPAY-C-5A07");
		}  catch(ConnectException e){
			logger.error(e.getMessage());
			log.error("省前置连接网状网超时");
			throw new ServiceException("UPAY-C-5A07");
		}  catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException("UPAY-C-2998");
		}
		if (StringUtils.isBlank(responseText)) {
			log.error("省前置连接网状网超时");
			throw new ServiceException("UPAY-C-5A07");
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

	// protected String decryptXmlBody(String client, String encryptText,
	// String provCode, String tradeCode) throws ServiceException {
	//
	// String decryptSwitch = CoreCommon.getDecryptSwitch();
	// String decryptText = encryptText;
	//
	// if (CoreCommon.checkIsDecrypt(decryptSwitch, provCode, tradeCode)) {
	// logger.info("......调用CRM前置解密方法......");
	// logger.info("......发起方[" + client + "]报文体密文[" + encryptText
	// + "],解密开关[" + decryptSwitch + "]......");
	// try {
	// decryptText = this.SecurityHandle.decryptPIN(encryptText);
	// } catch (Exception e) {
	// logger.info("......解密失败,失败原因：" + e.getMessage());
	// throw new ServiceException("UPAY-C-2998");
	// }
	// logger.info("......发起方[" + client + "]报文体密文[" + encryptText
	// + "],解密后明文[" + decryptText + "]......");
	// }
	// return decryptText;
	// }
	//
	// protected String encryptXmlBody(String client, String decryptText,
	// String provCode, String tradeCode) throws ServiceException {
	//
	// String encryptSwitch = CoreCommon.getEncryptSwitch();
	// String encryptText = decryptText;
	//
	// if (CoreCommon.checkIsEncrypt(encryptSwitch, provCode, tradeCode)) {
	// logger.info("......调用CRM前置加密方法......");
	// logger.info("......落地方[" + client + "]报文体明文[" + decryptText
	// + "],加密开关[" + encryptSwitch + "]......");
	// try {
	// encryptText = this.SecurityHandle.encryptPIN(decryptText);
	// } catch (Exception e) {
	// logger.info("......加密失败,失败原因：" + e.getMessage());
	// throw new ServiceException("UPAY-C-2998");
	// }
	// logger.info("......落地方[" + client + "]报文体密文[" + encryptText
	// + "],加密前明文[" + decryptText + "]......");
	// }
	//
	// return encryptText;
	// }

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

	public LogHandle getLogHandle() {
		return logHandle;
	}

	public void setLogHandle(LogHandle logHandle) {
		this.logHandle = logHandle;
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
