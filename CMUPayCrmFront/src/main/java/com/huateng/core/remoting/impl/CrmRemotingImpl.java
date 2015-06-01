package com.huateng.core.remoting.impl;

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

import com.huateng.cmupay.security.adapter.SecurityHandle;
import com.huateng.core.common.CoreCommon;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.UpayCommon;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.jms.common.JmsMsgHeader;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.remoting.CrmRemoting;
import com.huateng.core.util.DateUtil;
import com.huateng.core.util.JacksonUtils;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.bean.message.head.Response;
import com.huateng.crm.common.CRMConstant;
import com.huateng.crm.common.parse.Dom4jXMLParser;
import com.huateng.crm.logFormat.MessageLogger;
import com.huateng.log.LogHandle;

/**
 * @author cmt
 * @version 创建时间：2013-7-12 上午11:20:15 类说明
 */
public class CrmRemotingImpl implements CrmRemoting {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private MessageLogger log = MessageLogger.getLogger(getClass());
	/**
	 * 连接超时时间
	 */
	private @Value("${http.conn.timeout}") String connTimeout;
	/**
	 * 接收响应超时时间
	 */
	private @Value("${http.rev.timeout}") String revTimeout;
	/**
	 * 加密开关
	 */
	private @Value("${encrypt.switch}") String encryptSwitch;
	/**
	 * 解密开关
	 */
	private @Value("${decrypt.switch}") String decryptSwitch;
	
	/**
	 * 加密交易代码
	 */
	private @Value("${encrypt.tradecode}") String encryptTradeCodes;
	/**
	 * 解密交易代码
	 */
	private @Value("${decrypt.tradecode}") String decryptTradeCodes;
	/**
	 * 加密操作的省份代码
	 */
	private @Value("${encrypt.province}") String encryptProvinces;
	/**
	 * 解密操作的省份代码
	 */
	private @Value("${decrypt.province}") String decryptProvinces;	
	/**
	 * 是否请求网状网开关
	 */
	private @Value("${INVOKE_CRM}") String invokeCrm;

	private LogHandle logHandle;
	private SecurityHandle SecurityHandle;

	@SuppressWarnings("finally")
	@Override
	public String sendMsg(String header, String... args) {
		String resp2CoreText = "";
		String responseText = "";
		try {
			if (StringUtils.isNotBlank(args[0])) {
				String xmlContent = args[0].replaceAll("<SvcCont>",
						"<SvcCont><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>").replaceAll("</SvcCont>",
						"]]></SvcCont>");
				InterBOSS reqBoss = Dom4jXMLParser.parseXmlContent(xmlContent);	
				logger.info("接受核心请求,报文头:{},报文内容:{}",new Object[]{reqBoss.getHeader(),DateUtil.paseLog(args[0])});
				//获取路由信息			
				JmsMsgHeader routeMsg = JacksonUtils.json2Bean(header, JmsMsgHeader.class);		
				logger.debug("......根据路由信息向CRM:[{}]发送信息......",new Object[]{routeMsg.getReqIp()});
				
				log.debug("交易代码：{}，落地方交易流水号：{}",
						new Object[]{reqBoss.getBIPType().getActivityCode(),reqBoss.getTransInfo().getTransIDO()});
				/*
				 * 加密报文体
				 */
				String decryptBodyText = reqBoss.getSvcCont();
				String encryptBodyText = this.encryptXmlBody(routeMsg.getReqIp(), decryptBodyText, "",(reqBoss.getBIPType() == null ? "" : reqBoss.getBIPType().getBIPCode()),(reqBoss.getBIPType() == null ? "" : reqBoss.getBIPType().getActivityCode()));
				reqBoss.setSvcCont(encryptBodyText);
				//发送消息到网状网
				logger.info("请求CRM,报文头:{},报文内容:{}",new Object[]{reqBoss.getHeader(),DateUtil.paseLog(reqBoss.getBody())});
				logger.debug("请求CRM,报文头:{},报文内容:{}",new Object[]{reqBoss.getHeader(),reqBoss.getBody()});
				log.info("省前置向网状网发送请求报文");
				
				if(CoreConstant.SWITCH_CLOSE.equalsIgnoreCase(invokeCrm.trim())){
					responseText = this.getRespText();
				}else{
					responseText = this.sendMsg(routeMsg, reqBoss.getHeader(), reqBoss.getBody());
				}
								
				logger.info("接收到CRM:[{}]响应信息:{}",new Object[]{routeMsg.getReqIp(),DateUtil.paseLog(responseText)});
				log.info("省前置接收网状网返回的响应报文");
				
				/*
				 * 解密报文体
				 */
				InterBOSS rspBoss = Dom4jXMLParser.parseXmlContent(responseText);
				String resEncryptBodyText = rspBoss.getSvcCont();
				String resDecryptBodyText = this.decryptXmlBody(routeMsg.getReqIp(), resEncryptBodyText,rspBoss.getSNReserve().getMsgReceiver(),rspBoss.getBIPType().getBIPCode(),rspBoss.getBIPType().getActivityCode());
				rspBoss.setSvcCont(resDecryptBodyText);				
				/*
				 * 返回信息
				 */
				logger.info("响应给核心信息，CRM:[{}],信息如下:{}",new Object[]{routeMsg.getReqIp(),DateUtil.paseLog(rspBoss.getXmlContent())});
				
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
						resp2CoreText = rspBoss.getXmlContent();
					}						
				}else{
					resp2CoreText = rspBoss.getXmlContent();
				}
				/*
				 * 返回信息
				 * */
				logger.debug("----------------resp2CoreText:{}",resp2CoreText);
			} else {
				throw new ServiceException("UPAY-C-2998");
			}
		} catch (ServiceException e) {
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
			log.error("发起方流水号:{},错误码:{},错误描述:{}", new Object[]{boss.getTransInfo().getTransIDO(),boss.getResponse().getRspCode(),boss.getResponse().getRspDesc()});			
			try {
				resp2CoreText = boss.getXmlContent();
			} catch (ServiceException e1) {
				logger.error("", e1);
			}			
		}finally{
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
		logger.debug("协议类型:{},请求IP:{},请求端口号:{},请求地址:{}",new Object[]{protocol,reqIp,reqPort,reqPath});
		String responseText = null;
		try {
			String requestURL = protocol + "://" + reqIp + ":" + reqPort
					+ reqPath;
			logger.debug("Request URL is:{}",new Object[]{requestURL});
			//connTimeout = CoreCommon.getConnectionTimeOut();
			//revTimeout = CoreCommon.getReceiveTimeOut();
			if(StringUtils.isBlank(connTimeout) || StringUtils.isBlank(revTimeout)){
				throw new ServiceException("UPAY-C-2998");
			}
			HttpClient httpclient = this.getHttpClient(connTimeout, revTimeout);
			httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, CoreConstant.MSG_ENCODING);
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
			throw new ServiceException("UPAY-C-5A07");// 超时未收到响应
		} catch (SocketTimeoutException e) {
			logger.error(e.getMessage());
			throw new ServiceException("UPAY-C-5A07");// 超时未收到响应
		} catch (Exception e) {
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

	protected String decryptXmlBody(String client, String encryptText,
			String provCode, String bipCode,String tradeCode) throws ServiceException {

		//String decryptSwitch = CoreCommon.getDecryptSwitch();
		String decryptText = encryptText;

		//if (CoreCommon.checkIsDecrypt(decryptSwitch, provCode,bipCode, tradeCode)) {
		if (CoreCommon.checkIsDecrypt(decryptSwitch, provCode,bipCode, tradeCode,decryptTradeCodes,decryptProvinces)) {
			logger.debug("......调用CRM前置解密方法......");
			//logger.info("......发起方:[{}]报文体密文:[{}],解密开关:[{}]......",new Object[]{client,encryptText,decryptSwitch});
			logger.debug("......发起方:[{}],解密开关:[{}]......",new Object[]{client,decryptSwitch});
			try {
				decryptText = this.SecurityHandle.decryptPIN(encryptText);
			} catch (Exception e) {
				logger.debug("......解密失败,失败原因:{}",new Object[]{e.getMessage()});
				throw new ServiceException("UPAY-C-2998");
			}
			//logger.info("......发起方:[{}]报文体密文:[{}],解密后明文:[{}]......",new Object[]{client,encryptText,DateUtil.paseLog(decryptText)});
			logger.debug("......解密成功......");
		}
		return decryptText;
	}

	protected String encryptXmlBody(String client, String decryptText,
			String provCode, String bipCode,String tradeCode) throws ServiceException {

		//String encryptSwitch = CoreCommon.getEncryptSwitch();
		String encryptText = decryptText;

		//if (CoreCommon.checkIsEncrypt(encryptSwitch, provCode, bipCode,tradeCode)) {
		if (CoreCommon.checkIsEncrypt(encryptSwitch, provCode, bipCode,tradeCode,encryptTradeCodes,encryptProvinces)) {
			logger.debug("......调用CRM前置加密方法......");
			//logger.info("......落地方:[{}],报文体明文:[{}],加密开关:[{}]......",new Object[]{client,DateUtil.paseLog(decryptText),encryptSwitch});
			logger.debug("......落地方:[{}],加密开关:[{}]......",new Object[]{client,encryptSwitch});
			try {
				encryptText = this.SecurityHandle.encryptPIN(decryptText);
			} catch (Exception e) {
				logger.debug("......加密失败,失败原因：" + e.getMessage());
				throw new ServiceException("UPAY-C-2998");
			}
			//logger.info("......落地方:[{}]报文体密文:[{}],加密前明文:[{}]......",new Object[]{client,encryptText,DateUtil.paseLog(decryptText)});
			logger.debug("......加密成功......");
		}

		return encryptText;
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
	
	
	/**
	 * 获得返回报文FOR测试
	 */
	public String getRespText(){
	return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
+"<InterBOSS>"
	+"<Version>0100</Version>"
	+"<TestFlag>1</TestFlag>"
	+"<BIPType>"
		+"<BIPCode>BIP1A160</BIPCode>"
		+"<ActivityCode>T1000158</ActivityCode>"
		+"<ActionCode>1</ActionCode>"
	+"</BIPType>"
	+"<RoutingInfo>"
		+"<OrigDomain>UPSS</OrigDomain>"
		+"<RouteType>01</RouteType>"
		+"<Routing>"
			+"<HomeDomain>BOSS</HomeDomain>"
			+"<RouteValue>13920009999</RouteValue>"
		+"</Routing>"
	+"</RoutingInfo>"
	+"<TransInfo>"
		+"<SessionID>1234567890</SessionID>"
		+"<TransIDO>2221s2233dsd4d56d7890</TransIDO>"
		+"<TransIDOTime>20130410100235</TransIDOTime>"
		+"<TransIDH>12312312312312</TransIDH>"
		+"<TransIDHTime>20130410171135</TransIDHTime>"
	+"</TransInfo>"
	+"<SNReserve>"
		+"<TransIDC>111111</TransIDC>"
		+"<ConvID>11111</ConvID>"
		+"<CutOffDay>20120410</CutOffDay>"
		+"<OSNTime>20130410103035</OSNTime>"
		+"<OSNDUNS>2100</OSNDUNS>"
		+"<HSNDUNS>0099</HSNDUNS>"
		+"<MsgSender>2101</MsgSender>"
		+"<MsgReceiver>0001</MsgReceiver>"
		+"<Priority></Priority>"
		+"<ServiceLevel></ServiceLevel>"
	+"</SNReserve>"
	+"<Response>"
		+"<RspType>0</RspType>"
		+"<RspCode>0000</RspCode>"
		+"<RspDesc>success</RspDesc>"
	+"</Response>"
	+"<SvcCont>"
+"<PaymentRsp>"
			+"<IDType>01</IDType>"
			+"<IDValue>13920009999</IDValue>"
			+"<TransactionID>111222333123</TransactionID>"
			+"<ActionDate>20130316</ActionDate>"
			+"<UserCat>0</UserCat>"
			+"<RspCode>0000</RspCode>"
			+"<RspInfo>充值成功</RspInfo>"
+"</PaymentRsp>"
	+"</SvcCont>"
+"</InterBOSS>";
	}
	

	public LogHandle getLogHandle() {
		return logHandle;
	}

	public void setLogHandle(LogHandle logHandle) {
		this.logHandle = logHandle;
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

	public String getEncryptSwitch() {
		return encryptSwitch;
	}

	public void setEncryptSwitch(String encryptSwitch) {
		this.encryptSwitch = encryptSwitch;
	}

	public String getDecryptSwitch() {
		return decryptSwitch;
	}

	public void setDecryptSwitch(String decryptSwitch) {
		this.decryptSwitch = decryptSwitch;
	}

	public String getEncryptTradeCodes() {
		return encryptTradeCodes;
	}

	public void setEncryptTradeCodes(String encryptTradeCodes) {
		this.encryptTradeCodes = encryptTradeCodes;
	}

	public String getDecryptTradeCodes() {
		return decryptTradeCodes;
	}

	public void setDecryptTradeCodes(String decryptTradeCodes) {
		this.decryptTradeCodes = decryptTradeCodes;
	}

	public String getEncryptProvinces() {
		return encryptProvinces;
	}

	public void setEncryptProvinces(String encryptProvinces) {
		this.encryptProvinces = encryptProvinces;
	}

	public String getDecryptProvinces() {
		return decryptProvinces;
	}

	public void setDecryptProvinces(String decryptProvinces) {
		this.decryptProvinces = decryptProvinces;
	}

	public String getInvokeCrm() {
		return invokeCrm;
	}

	public void setInvokeCrm(String invokeCrm) {
		this.invokeCrm = invokeCrm;
	}
		
}
