package com.huateng.upay.service.impl;

import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
import com.huateng.core.common.CoreConstant;
import com.huateng.core.exception.ServiceException;
import com.huateng.upay.model.TUpayKey;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.common.CRMConstant;
import com.huateng.crm.common.parse.CrmXMLParser;
import com.huateng.crm.common.parse.Dom4jXMLParser;
import com.huateng.upay.service.UpayKeyUpdateApplyService;
import com.huateng.upay.service.UpayKeyUpdateService;

/**
 * @author qingxue.li
 * 
 */
public class UpayKeyUpdateApplyServiceImpl implements UpayKeyUpdateApplyService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 连接超时时间
	 */
	private  @Value("${http.conn.timeout}") String connTimeout;
	/**
	 * 接收响应超时时间
	 */
	private  @Value("${http.rev.timeout}") String revTimeout;
	/**
	 * 网状网地址
	 */
	private  @Value("${CENTER_PATH}") String centerPath;
	
	private String responseText = "";
	private TUpayKey upayKey = null;
	private SecurityHandle SecurityHandle;
	private UpayKeyUpdateService upayKeyUpdateService;
	
	
	@SuppressWarnings({ "finally", "unused", "static-access" })
	@Override
	public boolean applyKeyUpdate(String proposer) {
		logger.info("申请密钥更新开始...");
		boolean res = true;
		// 组装更新密钥对象
		InterBOSS boss = CrmXMLParser.assemblyUpayKeyUpdateApplyObject();
		try {
			// 发送报文至网状网,获得响应报文
			responseText = this.sendMsg(boss.getHeader(), boss.getXmlContent());
			//responseText = this.returnUpayKeyUpdateResp();
			if(StringUtils.isBlank(responseText)){
				res = false;
				return res;
			}
				
			//responseText = this.returnUpayKeyUpdateResp();
			// 解析密钥更新请求应答报文
			upayKey = Dom4jXMLParser.parseUpayKeyUpdateResp(responseText);
			if(upayKey == null){
				res = false;
				return res;
			}
			/**
			 * 密钥更新发起方
			 */
			if(proposer.equals(CoreConstant.UpayKeyUpdateProposer.CENTER)){
				upayKey.setOperateUser("网状网");
			}else if(proposer.equals(CoreConstant.UpayKeyUpdateProposer.CONSOLE)){
				upayKey.setOperateUser("管控台");
			}
			/**
			 * 密钥更新操作时间
			 */
			upayKey.setOperateTime(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));
			/**
			 * 密钥状态
			 */
			upayKey.setStatus(CoreConstant.UpayKeyStatus.NO_UPDATE);
			//管控台持久化密钥更新信息
			res = upayKeyUpdateService.insertUpayKey(upayKey);
			
		} catch (ServiceException e1) {			
			logger.error("申请密钥更新失败！！");
			res = false;
			return res;
		}finally{
			return res;
		}
	}


	/**
	 * 根据路由信息主动发送信息到指定路由(网状网)
	 * 
	 * @param routeMsg
	 *            路由信息
	 * @param param
	 *            发送的信息
	 * @return
	 * @throws Exception
	 */
	public String sendMsg(String xmlHead, String xmlBody)
			throws ServiceException {
		// TODO Auto-generated method stub
		logger.info("------begin to sendMessage------");
		//centerPath = CoreCommon.getCenterPath();
		if(StringUtils.isBlank(centerPath)){
			throw new ServiceException("UPAY-C-2998");
		}	
		try {
			logger.debug("Request URL is: " + centerPath);
			//connTimeout = CoreCommon.getConnectionTimeOut();
			//revTimeout = CoreCommon.getReceiveTimeOut();					
			if(StringUtils.isBlank(connTimeout) || StringUtils.isBlank(revTimeout)){
				throw new ServiceException("UPAY-C-2998");
			}
			HttpClient httpclient = this.getHttpClient(connTimeout, revTimeout);
			HttpPost post = new HttpPost(centerPath);
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
	
	
	
	public static String returnUpayKeyUpdateResp(){				
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
	            "<InterBOSS>"+
				   "<Version>0100</Version>"+
	               "<TestFlag>1</TestFlag>"+
				   "<BIPType>"+
	                  "<BIPCode>BIP0B001</BIPCode>"+
				      "<ActivityCode>T0011004</ActivityCode>"+
	                  "<ActionCode>1</ActionCode>"+
				   "</BIPType>"+
	               "<RoutingInfo>"+
				      "<OrigDomain>UPSS</OrigDomain>"+
	                  "<RouteType>00</RouteType>"+
				      "<Routing>"+
	                     "<HomeDomain>MGMT</HomeDomain>"+
				         "<RouteValue>999</RouteValue>"+
	                  "</Routing>"+
				   "</RoutingInfo>"+
	               "<TransInfo>"+
				      "<SessionID>23749525</SessionID>"+
	                  "<TransIDO>123123213213213217</TransIDO>"+
				      "<TransIDOTime>20130826110102</TransIDOTime>"+
	                  "<TransIDH>000020130701160754108392</TransIDH>"+
				      "<TransIDHTime>20130701155144</TransIDHTime>"+
	                "</TransInfo>"+
				    "<SNReserve>"+
	                  "<TransIDC>9970771320130701154705963000513</TransIDC>"+
				      "<ConvID>4c0ebb14-e46c-4031-927c-1f78acb8769b</ConvID>"+
	                  "<CutOffDay>20130706</CutOffDay>"+
				      "<OSNTime>20130701154705</OSNTime>"+
	                  "<OSNDUNS>9970</OSNDUNS>"+
				      "<HSNDUNS>9990</HSNDUNS>"+
	                  "<MsgSender>0233</MsgSender>"+
				      "<MsgReceiver>9991</MsgReceiver>"+
	                  "<Priority>7</Priority>"+
				      "<ServiceLevel>0</ServiceLevel>"+
	                "</SNReserve>"+
				    "<Response>"+
	                  "<RspType>0</RspType>"+
				      "<RspCode>0000</RspCode>"+
	                  "<RspDesc>success</RspDesc>"+
				    "</Response>"+
	                "<SvcCont>"+
				      "<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
	                  "<KeyApplyRsp>"+
				         "<NewKey>1f52546506196209df4cc83e38e729cb9349cfd3bafd8094462637106a378a5937e9f64c88e3fc6b0ca21d79b8142bc19496301ebf9fda55daa7083e4c9cebe8e33999e6a8d269edcb36d5227a9319cde2f9ee9137aca3fae7d16cfdf11c1b5bcdafa1bd59391fe70db08a7efef3ff5509f8d31c33da9a8dfb9052305c44f676</NewKey>"+
	                  "</KeyApplyRsp>]]>"+
				      "</SvcCont>"+
	               "</InterBOSS>";
				      


	}	
	
	
	
	public SecurityHandle getSecurityHandle() {
		return SecurityHandle;
	}

	public void setSecurityHandle(SecurityHandle securityHandle) {
		SecurityHandle = securityHandle;
	}

	public UpayKeyUpdateService getUpayKeyUpdateService() {
		return upayKeyUpdateService;
	}

	public void setUpayKeyUpdateService(UpayKeyUpdateService upayKeyUpdateService) {
		this.upayKeyUpdateService = upayKeyUpdateService;
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


	public String getCenterPath() {
		return centerPath;
	}


	public void setCenterPath(String centerPath) {
		this.centerPath = centerPath;
	}


	public static void main(String[] args) {

		//UpayKeyUpdateApplyServiceImpl dsd = new UpayKeyUpdateApplyServiceImpl();
		//dsd.applyKeyUpdate();

	}

}
