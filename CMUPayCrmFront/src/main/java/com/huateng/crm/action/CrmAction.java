package com.huateng.crm.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.adaper.listener.ServiceFactory;
import com.huateng.core.base.BaseAction;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.RemoteMsg;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.util.DateUtil;
import com.huateng.crm.bean.CrmRequestMessage;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.bean.message.head.Response;
import com.huateng.crm.common.CRMConstant;
import com.huateng.crm.common.parse.Dom4jXMLParser;
import com.huateng.crm.logFormat.MessageLogger;
import com.huateng.crm.service.CrmService;
import com.huateng.upay.service.UpayKeyUpdateApplyService;

/**
 * CRM前置Action
 * 
 * @author Gary
 * 
 */

public class CrmAction extends BaseAction {
	private static final long serialVersionUID = -5657850081783766293L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String responseText = "";
	private MessageLogger log = MessageLogger.getLogger(getClass());

	/**
	 * 请求处理方法
	 */
	public String receive() {
		HttpServletRequest request = this.getHttpRequest();
		HttpServletResponse response = this.getHttpResponse();
		PrintWriter pw = null;
		CrmRequestMessage crmMsg = null;
		try {
			/*
			 * 设置请求公共信息(编码等)
			 */
			this.settingRequest(request);
			/*
			 * 设置响应公共信息(编码等)
			 */
			this.settingResponse(response);

			pw = response.getWriter();

			/*
			 * 获取请求中的数据，并组装成CrmRequestMessage格式
			 */
			//crmMsg = this.assemblyRequestData(request);
			crmMsg = this.assemblyReqData(request);
			/*
			 * 判断当前是否为心跳检测(当报文头报文体同时为空时判定为心跳检测)
			 */
			if(StringUtils.isBlank(crmMsg.getXmlhead()) && StringUtils.isBlank(crmMsg.getXmlbody())){
				responseText="success";
			}else if((StringUtils.isNotBlank(crmMsg.getXmlhead()) && StringUtils.isBlank(crmMsg.getXmlbody()))|| StringUtils.isBlank(crmMsg.getXmlhead()) && StringUtils.isNotBlank(crmMsg.getXmlbody()) ){
				logger.error("省前置解析网状网报文失败");
				log.error("省前置解析网状网报文失败");
				throw new ServiceException("UPAY-C-5A05");
			}else{
			/*
			 * 记录发送日志
			 */
			String client = this.printBeforeLog(crmMsg, request);
			/*
			 * 组装报文头和报文体
			 */
			CrmService crmService = (CrmService) ServiceFactory.getInstance()
					.findService("crmService");
			InterBOSS boss = crmService.assemblyXmlContent(client,
					crmMsg.getXmlhead(), crmMsg.getXmlbody());
			//logger.info("CRM前置发给核心平台组装后报文信息:[{}]",new Object[]{DateUtil.paseLog(boss.getXmlContent())});		
			/*
			 * 此处判断是否为通知更新密钥交易 是:发送密钥更新请求 否:发送报文至核心
			 */
			if (boss.getBIPType().getBIPCode().equals(CoreConstant.BIPType.UPDATE_KEY)
					&& boss.getBIPType().getActivityCode().equals(CoreConstant.BIPType.NOTICE_UPDATE_KEY)) {
				UpayKeyUpdateApplyService upayKeyUpdateApplyService 
				                   = (UpayKeyUpdateApplyService) ServiceFactory.getInstance().findService("upayKeyUpdateApplyService");
				upayKeyUpdateApplyService.applyKeyUpdate(CoreConstant.UpayKeyUpdateProposer.CENTER);
			} else {
				// 发送报文信息给核心平台
				responseText = crmService.sendMsg( boss.getSNReserve().getMsgSender(), client, boss.getXmlContent());
				log.succ("发起方流水号:{},交易代码:{},交易状态:{}", new Object[]{boss.getTransInfo().getTransIDO(),boss.getBIPType().getActivityCode(),"成功"});
			}

			/*
			 * 记录响应日志
			 */
			this.printAfterLog(responseText);
			}
			/*
			 * 返回响应报文给CRM
			 */

			pw.write(responseText);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String xmlHead = null;
			if (crmMsg != null) {
				xmlHead = crmMsg.getXmlhead();
			}
			this.writeReturnMessage(e.getMessage(), xmlHead, request, response);
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
	 * @param xmlHead
	 * @param request
	 * @param response
	 */
	private void writeReturnMessage(String errorCode, String xmlHead,
			HttpServletRequest request, HttpServletResponse response) {
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
			InterBOSS boss = null;
			Response res = null;
			try {
				bean = ErrorConfigUtil.getCrmError(errorCode);
				boss = Dom4jXMLParser.readXMLHead(xmlHead);
				res = boss.getResponse();
				if (res == null) {
					res = new Response();
				}
			} catch (Exception e1) {
				boss = new InterBOSS();
				res = new Response();
				bean = ErrorConfigUtil.getCrmError("UPAY-C-2998");
			}
			String rspCode = bean.getOuterCode();
			String rspInfo = bean.getDesc();

			res.setRspType("2");
			res.setRspDesc(rspInfo);
			res.setRspCode(rspCode);
			boss.setResponse(res);
			log.error("发起方流水号:{},错误码:{},错误描述:{}", new Object[]{boss.getTransInfo().getTransIDO(),boss.getResponse().getRspCode(),boss.getResponse().getRspDesc()});
			String resText = boss.getXmlContent();

			// CrmService crmService = (CrmService)
			// ServiceFactory.getInstance().findService("crmService");
			// RemoteMsg remote = this.getRemoteMsg(request);
			// resText = crmService.encryptXmlBody(remote.getIp(), resText);
            logger.error("<异常情况>省前置响应给网状网的信息为:[{}]",resText);
			pw = response.getWriter();
			pw.write(resText);
		} catch (Exception e1) {
			logger.error(e1.getMessage());
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}
	}
	
	
	
	
	/**
	 * 获取请求中的数据，并组装成CrmRequestMessage格式
	 * 
	 * @param request
	 * @return
	 * @throws ServiceException
	 * @throws UnsupportedEncodingException
	 */
	private CrmRequestMessage assemblyReqData(HttpServletRequest request)throws ServiceException{
		CrmRequestMessage crmMsg = new CrmRequestMessage();
		String xmlHead = "";
		String xmlBody = "";				
		try {
			xmlHead = this.getRequestParameterDate(request, CRMConstant.XML_HEAD);
			xmlBody = this.getRequestParameterDate(request, CRMConstant.XML_BODY);
			crmMsg.setXmlhead(URLDecoder.decode(xmlHead.toString(), CoreConstant.MSG_ENCODING));			
			crmMsg.setXmlbody(URLDecoder.decode(xmlBody.toString(), CoreConstant.MSG_ENCODING));		
		} catch (UnsupportedEncodingException e) {
			logger.error("",e);
			throw new ServiceException("UPAY-B-015A05");
		}
				
		return crmMsg;
	}
	
	
	/**
	 * 根据参数Key从request中获得参数值
	 * 
	 * @param request
	 * @param paramterKey
	 * @return
	 * @throws ServiceException
	 */	
	private String getRequestParameterDate(HttpServletRequest request,String paramterKey) throws ServiceException{
		StringBuffer buffer = new StringBuffer();
		//BufferedReader reader = null;
		try {
			String xmlData = request.getParameter(paramterKey);
			if (StringUtils.isBlank(xmlData)) {
				/*reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
				String show = null;
				while ((show = reader.readLine()) != null) {
					buffer.append(show);
				}*/				
			} else {
				buffer.append(xmlData);
			}

		} catch (Exception e) {
			logger.error("省前置获取网状网请求报文失败",e);
	        log.error("省前置获取网状网请求报文失败");
			throw new ServiceException("UPAY-B-015A05");
		} 
//		finally {
//			try {
//				if (reader != null) {
//					reader.close();
//				}
//			} catch (IOException e) {
//				logger.error("", e);
//			}
//		}
		
		return buffer.toString();
	}
	
	
	

	/**
	 * 获取请求中的数据，并组装成CrmRequestMessage格式
	 * 
	 * @param request
	 * @return
	 * @throws ServiceException
	 * @throws UnsupportedEncodingException
	 * @throws FileUploadException
	 */
	@SuppressWarnings("unchecked")
	private CrmRequestMessage assemblyRequestData(HttpServletRequest request)
			throws ServiceException {
		CrmRequestMessage crmMsg = new CrmRequestMessage();
		if (!ServletFileUpload.isMultipartContent(request)) {
			return crmMsg;
		}

		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			/*
			 * 设定内存可以存放文件的最大容量，单位字节，超过的部分会以临时文件的形式存放在硬盘上，这里设置成10兆
			 */
			factory.setSizeThreshold(10485760);
			File temp = new File("/upload_temp");
			if (!temp.exists()) {
				temp.mkdirs();
			}
			// 设置临时文件存放的位置
			factory.setRepository(temp);

			ServletFileUpload upload = new ServletFileUpload(factory);
			/*
			 * 设置单个文件最大容量限制，-1表示没有限制
			 */
			upload.setFileSizeMax(-1);
			/*
			 * 设置整个请求的上传容量限制，-1表示没有限制
			 */
			upload.setSizeMax(-1);

			List<FileItem> items = upload.parseRequest(request);
			String key = "";
			String value = "";
			for (FileItem item : items) {
				if (item.isFormField()) {// 普通字段
					key = item.getFieldName();
					value = item.getString(CoreConstant.MSG_ENCODING);
					if (CRMConstant.XML_HEAD.equals(key)) {// 报文头
						crmMsg.setXmlhead(URLDecoder.decode(value,
								CoreConstant.MSG_ENCODING));
					} else if (CRMConstant.XML_BODY.equals(key)) {// 报文体
						crmMsg.setXmlbody(URLDecoder.decode(value,
								CoreConstant.MSG_ENCODING));
					}
				} else {// 文件字段

				}
			}
		} catch (Exception e) {
			logger.error("",e);
			throw new ServiceException("UPAY-C-5A05");
		}
		return crmMsg;
	}

	/**
	 * 打印报文请求日志
	 * 
	 * @param crmMsg
	 * @param request
	 */
	private String printBeforeLog(CrmRequestMessage crmMsg,
			HttpServletRequest request) {
		RemoteMsg remote = this.getRemoteMsg(request);
		//logger.info("[{}]客户端:{}发送报文请求......",new Object[]{DateUtil.getCurrentFullDate(),remote.getIp()});
		//logger.info( "接受CRM端请求报文头和报文体信息:xmlhead is:[{}],\r\n     xmlbody is:[{}]",new Object[]{crmMsg.getXmlhead().toString(),DateUtil.paseLog(crmMsg.getXmlbody()).toString()});
		//logger.info(CRMConstant.XML_BODY + " is[{}]",new Object[]{DateUtil.paseLog(crmMsg.getXmlbody())});
		logger.info("[{}]客户端:[{}]发送报文请求,报文头和报文体信息分别为:xmlhead is:[{}],\r\nxmlbody is:[{}]",new Object[]{DateUtil.getCurrentFullDate(),remote.getIp(),crmMsg.getXmlhead().toString(),DateUtil.paseLog(crmMsg.getXmlbody()).toString()});
		logger.debug("[{}]客户端:[{}]发送报文请求,报文头和报文体信息分别为:xmlhead is:[{}],\r\nxmlbody is:[{}]",new Object[]{DateUtil.getCurrentFullDate(),remote.getIp(),crmMsg.getXmlhead().toString(),crmMsg.getXmlbody()});
		return remote.getIp();
	}

	/**
	 * 打印报文响应日志
	 * 
	 * @param responseText
	 */
	private void printAfterLog(String responseText) {
		logger.info("返回给CRM端响应的信息 xmldata is:[{}]",new Object []{DateUtil.paseLog(responseText)});
	}
}
