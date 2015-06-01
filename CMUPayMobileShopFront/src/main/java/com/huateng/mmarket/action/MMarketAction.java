package com.huateng.mmarket.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;

import com.huateng.log.MessageLogger;
import com.huateng.mmarket.bean.head.GPay;
import com.huateng.mmarket.bean.head.Header;
import com.huateng.mmarket.common.MMarketConstant;
import com.huateng.mmarket.common.parse.TMallXMLParser;
import com.huateng.core.adaper.listener.ServiceFactory;
import com.huateng.core.base.BaseAction;
import com.huateng.core.common.CommonConstant;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.RemoteMsg;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.util.DateUtil;
import com.huateng.core.util.StrUtil;
import com.huateng.mmarket.bean.MobileMarketRequestMessage;
import com.huateng.mmarket.service.MMarketService;

/**
 * 接收商城的http请求
 * 
 * @author ol
 * 
 */
public class MMarketAction extends BaseAction {

	private static final long serialVersionUID = 8805570499616212830L;
	private Logger logger = LoggerFactory.getLogger("MMarketAction");
	private MessageLogger log =MessageLogger.getLogger("MMarketAction");
	@Autowired
	private MMarketService mmarketService;
	
	public String receive() {
		HttpServletRequest request = this.getHttpRequest();
		HttpServletResponse response = this.getHttpResponse();
		PrintWriter pw = null;
		MobileMarketRequestMessage mMarketMsg = null;
		GPay gPay = null;
		String responseText=null;
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
			mMarketMsg = this.assemblyRequestData(request);
			/*
			 * 记录发送日志
			 */
			String client = this.printBeforeLog(mMarketMsg, request);
			String xmlContent = mMarketMsg.getXmldata();
			if(xmlContent.startsWith(CoreConstant.MSG_CODE)){
				xmlContent=xmlContent.substring(8);
			}
			/*
			 * 解析请求报文
			 */
			gPay = mmarketService.assemblyGPayMessage(xmlContent);
			/*
			 * #验签#
			 */			
			responseText = mmarketService.mmarketCheckSign(client, xmlContent,gPay);
			/*
			 * 发送报文信息给核心平台
			 */
			responseText = mmarketService.sendMsg("",client, xmlContent,gPay);
			
			/*
			 * 记录响应日志
			 */
			this.printAfterLog(responseText, request);
			logger.debug("响应给商城的报文长度为：" + responseText.getBytes().length);
			response.setContentLength(responseText.getBytes().length);
			pw = response.getWriter();
			pw.write(responseText);
		} catch (Exception e) {
			log.error("[商城前置交易异常!请求时间：[{}]发起方流水号:[{}]，" +
					"发起方机构：[{}]，交易代码：[{}]",new Object[]{
					StrUtil.parseNodeValueFromXml("<ReqDateTime>", "</ReqDateTime>", responseText),
					StrUtil.parseNodeValueFromXml("<ReqTransID>", "</ReqTransID>", responseText),
					StrUtil.parseNodeValueFromXml("<ReqSys>", "</ReqSys>", responseText),
					StrUtil.parseNodeValueFromXml("<ActivityCode>", "</ActivityCode>", responseText)});
			logger.error("",e);
			pw.write("解析报文失败!");
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
	@SuppressWarnings("unused")
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
			header.setRspCode(rspCode);
			header.setRspDesc(rspDesc);
			gPay.setHeader(header);
			gPay.setBody("");
			String resText = TMallXMLParser.parseGPay(gPay);			
			MMarketService mmarketService = (MMarketService) ServiceFactory.getInstance().findService("tmallService");
			RemoteMsg remote = this.getRemoteMsg(request);
			resText = mmarketService.sign(remote.getIp(), resText);

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
	private MobileMarketRequestMessage assemblyRequestData(HttpServletRequest request) throws ServiceException {

		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			String xmlData = request.getParameter(MMarketConstant.REQ_XML_DATA);
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
				e.printStackTrace();
			}
		}
		MobileMarketRequestMessage mMarketMsg = new MobileMarketRequestMessage();
		mMarketMsg.setXmldata(sb.toString());
		return mMarketMsg;
	}
	
	/**
	 * 打印报文请求日志
	 * 
	 * @param crmMsg
	 * @param request
	 */
	private String printBeforeLog(MobileMarketRequestMessage mMarketMsg, HttpServletRequest request) {
		RemoteMsg remote = this.getRemoteMsg(request);
		logger.info("[仿真/移动商城 ->移动商城前置]请求报文:[{}]" ,new Object[]{mMarketMsg.getXmldata()});
		return remote.getIp();
	}

	/**
	 * 打印报文响应日志
	 * 
	 * @param responseText
	 */
	private void printAfterLog(String responseText, HttpServletRequest request) {
		logger.info("[移动商城前置->仿真/移动商城]应答报文:[{}]",responseText);
	}
}
