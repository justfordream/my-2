package com.huateng.tmall.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.huateng.log.MessageLogger;
import com.huateng.log.TMallMessageLogger;
import com.huateng.tmall.bean.head.GPay;
import com.huateng.tmall.bean.head.Header;
import com.huateng.tmall.common.TMallConstant;
import com.huateng.tmall.common.parse.TMallXMLParser;
import com.huateng.core.adaper.listener.ServiceFactory;
import com.huateng.core.base.BaseAction;
import com.huateng.core.common.CommonConstant;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.RemoteMsg;
import com.huateng.core.common.Serial;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.remoting.CrmTmallRemoting;
import com.huateng.core.util.DateUtil;
import com.huateng.core.util.TimeUtil;
import com.huateng.tmall.bean.TMallRequestMessage;
import com.huateng.tmall.service.TMallService;

/**
 * 接收银行端的http请求
 * 
 * @author leon
 * 
 */
public class TMallAction extends BaseAction {

	private static final long serialVersionUID = 8805570499616212830L;
	private Logger logger = LoggerFactory.getLogger("TMallAction");
	private TMallMessageLogger tmallLog = TMallMessageLogger.getLogger(getClass());
	private MessageLogger log = MessageLogger.getLogger(getClass());
	
	private @Value("${url}") String url;
	private @Value("${http.conn.timeout}") String iTimeout;
	private @Value("${http.rev.timeout}") String iSoTimeout;
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getiTimeout() {
		return iTimeout;
	}

	public void setiTimeout(String iTimeout) {
		this.iTimeout = iTimeout;
	}

	public String getiSoTimeout() {
		return iSoTimeout;
	}

	public void setiSoTimeout(String iSoTimeout) {
		this.iSoTimeout = iSoTimeout;
	}

	GPay gPayemergency = null;
    String xmlContenteme = null;
	public String receive() {
		HttpServletRequest request = this.getHttpRequest();
		 
		TMallRequestMessage tmallMsg = null;
		boolean emergency ;
		GPay gPay = null;
	    TMallService tmallService = null;
	    String responseText = null;
	    PrintWriter pw = null;
	    HttpServletResponse response = null;
	    response = this.getHttpResponse();

		try {
			/*
			 * 设置请求公共信息(编码等)
			 */
			this.settingRequest(request);
			/*
			 * 设置响应公共信息(编码等)
			 */
			this.settingResponse(response);
			/*
			 * 获取请求中的数据，并组装成TMallRequestMessage格式
			 */
			tmallMsg = this.assemblyRequestData(request);
			
			/*
			 * 任务添加至线程池
			 */
			//UpayThreadPoolUtil.putReqestQueue(tmallMsg);
			 tmallService = (TMallService) ServiceFactory.getInstance().findService("tmallService");						
			/*
			 * 记录发送日志
			 */
			String client = this.printBeforeLog(tmallMsg, request);
			
			/*
			 * 流量监控 
			 */
			//tmallService.tmallFlowMonitor();				
			/*
			 * 解析请求报文
			 */
			String xmlContent = tmallMsg.getXmldata();
			gPay = tmallService.assemblyGPayMessage(xmlContent);
			/*
			 * #验签#
			 */			
			 responseText = tmallService.tmallCheckSign(client, xmlContent,gPay);			
			/*
			 * 判断是否为应急方案
			 */
			emergency = tmallService.checkIsEmergency();	
			gPayemergency = gPay ;
			xmlContenteme = xmlContent;
		//	emergency = true;  
			if(emergency){
				String activeCode = gPayemergency.getHeader().getActivityCode();
				if(CoreConstant.tmall_recharge.equals(activeCode)){
					new Thread(new Runnable(){
						
						public  void run(){
							
							StringBuilder tradeMessage = null;
							String responseTexteme = null;
							tradeMessage = new StringBuilder();
							String transIDHTime = DateUtil.getDateyyyyMMddHHmmssSSS();
							String txnSeq = null;
							try {
								TMallService tmallServiceeme = (TMallService) ServiceFactory.getInstance().findService("tmallService");	
								CrmTmallRemoting crmTmallRemoting = (CrmTmallRemoting) ServiceFactory.getInstance().findService("crmTmallRemoting");
							    txnSeq=Serial.genSerialNoss(CommonConstant.Sequence.IntSeq.toString());
								List<NameValuePair> formParams = new ArrayList<NameValuePair>();
								UrlEncodedFormEntity uefEntity;
								HttpPost post;
								HttpClient httpClient;
								HttpResponse response =null;
								
								/*
								 * 组装省充值请求     
								 * 
								 * 
								 *    报文给移动前置
								 */
								Map resultReqMap = null;
								resultReqMap = tmallServiceeme.assemblyCrmXml(gPayemergency,txnSeq,transIDHTime);	
								String headerJson = (String) resultReqMap.get("headerJson");
								String[] xmlMsg = (String[]) resultReqMap.get("xmlMsg");				
								tradeMessage.append(resultReqMap.get("tradeReqMessage"));
								
								logger.info("......<天猫应急方案>发送请求至省前置,报文头信息为[{}],报文体信息[{}]",new Object[]{xmlMsg[0],xmlMsg[1]});	
								/*
								 * 发送省充值请求报文至省前置
								 */
								responseTexteme = crmTmallRemoting.sendMsg(headerJson, xmlMsg);
								String transRevIDHTime = DateUtil.getDateyyyyMMddHHmmssSSS();
								logger.info("......<天猫应急方案>省前置返回给天猫前置响应信息为:[{}]",new Object[]{responseTexteme});
								/*
								 * 解析省前置的响应报文
								 * 组装为天猫充值响应报文
								 */
								Map resultResMap = null;
								resultResMap = tmallServiceeme.assemblyTMallResXml(gPayemergency,responseTexteme,txnSeq,transRevIDHTime);
								responseTexteme = (String) resultResMap.get("resXml");
								tradeMessage.append(resultResMap.get("tradeResMessage"));	
								/*
								 * 记录追踪日志信息<天猫应急方案>
								 */
								if(tradeMessage != null){
									tmallLog.recordMessage(tradeMessage.toString());
								}	
								//签名	
									responseTexteme = tmallServiceeme.sign(responseTexteme);
								log.succ("发起方交易流水号:{},交易状态:{}", new Object[]{gPayemergency.getHeader().getReqTransID(),"成功"});
								/*
								 * 记录响应日志
								 */
								logger.info("统一支付的响应报文为[{}]",new Object[]{responseTexteme});

								formParams.add(new BasicNameValuePair("xmldata", responseTexteme));
								uefEntity = new UrlEncodedFormEntity(formParams,CoreConstant.MSG_ENCODING);
								post = new HttpPost(url);
								post.setEntity(uefEntity);
								//如果发送请求失败，重试3次
								for (int i = 0; i < CoreConstant.TRY_TIMES; i++)
								{
									try
									{
										logger.warn("第  {} 次发送",i + 1);
										logger.info("发送给浙商的报文为："+responseTexteme);
										HttpParams params = new BasicHttpParams();
										params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,Integer.valueOf(iTimeout).intValue());
										params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, Integer.valueOf(iSoTimeout).intValue());
										httpClient = new DefaultHttpClient(params);
										response = httpClient.execute(post);
										log.debug("http response : " + response.getStatusLine().getStatusCode());
										logger.info("http response : " + response.getStatusLine().getStatusCode());
										if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode())
										{
											HttpEntity entitys = response.getEntity();
											String responseTextemeins = null;
											if (entitys != null)
											{
												responseTextemeins = EntityUtils.toString(entitys, CoreConstant.MSG_ENCODING);
												responseTextemeins = URLDecoder.decode(responseTextemeins,  CoreConstant.MSG_ENCODING);
												if(null == responseTextemeins || "".equals(responseTextemeins)){
													log.info("第{}次,运营商返回给天猫前置的消息内容为空进行重发",i+1);
													continue;
												}else{
													log.info("运营商返回给天猫前置的消息内容："+responseTextemeins);
													logger.info("运营商返回给天猫前置的消息内容："+responseTextemeins);
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
											log.warn("重发第 {}次 ", i + 1);
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
								
								
								
								
							} catch (Exception e) {
								log.error("天猫前置收到消息后解析报错{}",responseTexteme);
								logger.error("天猫前置收到消息后解析报错{}",responseTexteme);
							}
						}
					}).start();
					String responseTexts = null;
					responseTexts = getResponseXML("",xmlContent);
					/*
					 * 记录响应日志
					 */
					logger.info("天猫应急处理成功,第一次返回应答报文为[{}]",new Object[]{responseTexts});
					/*
					 * 返回响应报文
					 */
					logger.debug("应答给天猫的报文长度为：" + responseTexts.getBytes().length);
					response.setContentLength(responseTexts.getBytes().length);
					pw = response.getWriter();
					pw.write(responseTexts);
				}else{
					String responseTexts = null;
					responseTexts ="系统繁忙";
					log.error("天猫应急查询没有权限{}",xmlContent);
					logger.error("天猫应急查询没有权限{}",xmlContent);
					response.setContentLength(responseTexts.getBytes().length);
					pw = response.getWriter();
					pw.write(responseTexts);
				}
				
				
			}else{				
				/*
				 * 发送报文信息给核心平台
				 */
				responseText = tmallService.sendMsg("",client, xmlContent,gPay);
				log.succ("发起方交易流水号:{},交易状态:{}", new Object[]{gPay.getHeader().getReqTransID(),"成功"});
				/*
				 * 记录响应日志
				 */
				this.printAfterLog(responseText, request);
				/*
				 * 返回响应报文
				 */
				logger.debug("响应给天猫的报文长度为：" + responseText.getBytes().length);
				response.setContentLength(responseText.getBytes().length);
				pw = response.getWriter();
				pw.write(responseText);
			}	

		} catch (Exception e) {
			logger.error("",e);
			/*
			 * 获取请求中的数据，并组装成TMallRequestMessage格式
			 */
			String xml = "";
			if (tmallMsg != null) {
				xml = tmallMsg.getXmldata();
			}
			this.writeReturnMessage(e.getMessage(), xml, request, response);
		} finally {
					
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}
		return NONE;
	}

	/**
	 * 异常返回报文
	 * 
	 * @param errorCode
	 * @param xml
	 * @param request
	 * @param response
	 */
	private void writeReturnMessage(String errorCode, String xml, HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			/*
			 * 设置请求公共信息(编码等)
			 */
			this.settingRequest(request);
			/*
			 * 设置响应公共信息(编码等)
			 */
			this.settingResponse(response);
			ErrorBean bean = null;
			GPay gPay = null;
			Header header = null;
			if(StringUtils.isNotBlank(xml)){
				try {
					bean = ErrorConfigUtil.getBankError(errorCode);
					gPay = TMallXMLParser.parseXmlContent(xml);
					header = gPay.getHeader();
					if(header == null){
						header = new Header();
					}
				} catch (Exception e1) {
					gPay = new GPay();
					header = new Header();
					bean = ErrorConfigUtil.getBankError("UPAY-B-015A06");
				}
			}else{
				gPay = new GPay();
				header = new Header();
				bean = ErrorConfigUtil.getBankError("UPAY-B-015A05");
			}
			
			String rspCode = bean.getOuterCode();
			String rspDesc = bean.getDesc();

			/*
			 * 应答/错误代码
			 */
			header.setRspCode(rspCode);
			/*
			 * 应答/错误描述
			 */
			header.setRspDesc(rspDesc);
			/*
			 * 交易动作代码
			 */
			header.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			/*
			 * 接收方交易日期
			 */
			header.setRcvDate(DateUtil.getDateyyyyMMdd());
			/*
			 * 接收方交易流水号
			 */
			header.setRcvTransID(Serial.genSerialNoss(CommonConstant.Sequence.IntSeq.toString()));
			/*
			 * 接收方时间戳
			 */
			header.setRcvDateTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			
			gPay.setHeader(header);
			
			//there is no body in resText 
			gPay.setBody("");			
			log.error("发起方交易流水号:{},错误码:{},错误描述:{}", new Object[]{header.getReqTransID(),header.getRspCode(),header.getRspDesc()});
			String resText = TMallXMLParser.parseGPay(gPay);			
			TMallService tmallService = (TMallService) ServiceFactory.getInstance().findService("tmallService");
			RemoteMsg remote = this.getRemoteMsg(request);
			resText = tmallService.sign(remote.getIp(), resText);
            logger.error("<异常情况>天猫前置响应给天猫的响应信息为:[{}]",resText);
			pw = response.getWriter();
			pw.write(resText);
		} catch (Exception e1) {
			logger.error("",e1);
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	private TMallRequestMessage assemblyRequestData(HttpServletRequest request) throws ServiceException {

		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			String xmlData = request.getParameter(TMallConstant.REQ_XML_DATA);
				if (StringUtils.isBlank(xmlData)) {
				reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
				String show = null;
				while ((show = reader.readLine()) != null) {
					sb.append(show);
				}
			} else {
				sb.append(xmlData);
			}

		} catch (IOException e) {
			logger.error("",e);
			throw new ServiceException("UPAY-B-015A05");
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		TMallRequestMessage tmallMsg = new TMallRequestMessage();
		tmallMsg.setXmldata(sb.toString());
		return tmallMsg;
	}

	/**
	 * 打印报文请求日志
	 * 
	 * @param crmMsg
	 * @param request
	 */
	private String printBeforeLog(TMallRequestMessage tmallMsg, HttpServletRequest request) {
		RemoteMsg remote = this.getRemoteMsg(request);
		//logger.info("[" + DateUtil.getCurrentFullDate() + "]客户端[" + remote.getIp() + "] 发送报文请求......");
		//logger.debug(TMallConstant.REQ_XML_DATA + " is [" + tmallMsg.getXmldata() + "]");
		//logger.info(TMallConstant.REQ_XML_DATA + " is [{}]" ,new Object[]{tmallMsg.getXmldata()});		
		logger.info("[{}]客户端[{}]向天猫前置发送报文请求,"+TMallConstant.REQ_XML_DATA+"is[{}]",new Object[]{DateUtil.getCurrentFullDate(),remote.getIp(),tmallMsg.getXmldata()});
		
		return remote.getIp();
	}

	/**
	 * 打印报文响应日志
	 * 
	 * @param responseText
	 */
	private void printAfterLog(String responseText, HttpServletRequest request) {
		RemoteMsg remote = this.getRemoteMsg(request);
		//logger.info("[" + DateUtil.getCurrentFullDate() + "]核心返回给天猫[" + remote.getIp() + "] ......");
		//logger.debug("The responseText is [" + responseText + "]");
		//logger.info("返回给天猫的响应报文为: [{}]",new Object[]{responseText});
		logger.info("[{}]签名以后返回给天猫[{}]的响应报文为[{}]",new Object[]{DateUtil.getCurrentFullDate(),remote.getIp(),responseText});
	}
	
	private String getResponseXML(String client, final String xmlContent) throws Exception
	{

		GPay gPay = null;
		Header header = null;
		String resText = "";
		//gPay = TmallXMLParser.parseXmlContent(xmlContent);
		gPay = TMallXMLParser.parseXmlContent(xmlContent);
		header = gPay.getHeader();
		header.setActionCode(CoreConstant.ACTION_CODE_RESPONSE);
		header.setRcvDate(TimeUtil.getSystemOfDateByFormat(TimeUtil.FORMAT_YMD));
		header.setRcvDateTime(TimeUtil.getSystemOfDateByFormat(TimeUtil.FORMAT_YMDHMSS));
		header.setRspCode(CoreConstant.RSP_SUCCESS_CODE);
		header.setRspDesc(CoreConstant.RSP_SUCCESS_DESC);
		header.setRcvTransID("");
		gPay.setHeader(header);
		// 应答body为空
		gPay.setBody("");
		resText = TMallXMLParser.parseGPay(gPay);
		TMallService tmallService = null;
		tmallService = (TMallService) ServiceFactory.getInstance().findService("tmallService");	
		resText = tmallService.sign(client, resText);
		return resText;
	}
	

}
