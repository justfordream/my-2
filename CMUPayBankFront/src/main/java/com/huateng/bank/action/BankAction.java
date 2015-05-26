package com.huateng.bank.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.bank.bean.BankRequestMessage;
import com.huateng.bank.bean.head.GPay;
import com.huateng.bank.bean.head.Header;
import com.huateng.bank.common.BankConstant;
import com.huateng.bank.common.parse.BankXMLParser;
import com.huateng.bank.logFormat.MessageLogger;
import com.huateng.bank.service.BankService;
import com.huateng.core.adaper.listener.ServiceFactory;
import com.huateng.core.base.BaseAction;
import com.huateng.core.common.RemoteMsg;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.util.DateUtil;


/**
 * 接收银行端的http请求
 * 
 * @author leon
 * 
 */
public class BankAction extends BaseAction {

	private static final long serialVersionUID = 8805570499616212830L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger log = MessageLogger.getLogger(getClass());


	public String receive() {
		HttpServletRequest request = this.getHttpRequest();
		HttpServletResponse response = this.getHttpResponse();
		PrintWriter pw = null;
		BankRequestMessage bankMsg = null;
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
			 * 获取请求中的数据，并组装成BankRequestMessage格式
			 */
			bankMsg = this.assemblyRequestData(request);
			/*
			 * 记录发送日志
			 */
			String client = this.printBeforeLog(bankMsg, request);
			String xmlContent = bankMsg.getXmldata();
            //logger.debug("接收到银行端发起的报文信息:{}",DateUtil.paseLog(xmlContent));
			BankService bankService = (BankService) ServiceFactory.getInstance().findService("bankService");
			/*
			 * 验签
			 */
			String responseText = bankService.checkSign(client, xmlContent);
			/*
			 * 解密文件
			 */
			//xmlContent=bankService.symPaseDncrypt(xmlContent);
			//logger.info("......发送至核心的XML......[" + DateUtil.paseLog(xmlContent) + "]");
			//logger.info("......发送至核心的XML......[{}]",new Object[]{DateUtil.paseLog(xmlContent)});
			
			/*
			 * 发送报文信息给核心平台
			 */
			responseText = bankService.sendMsg("",client, xmlContent);
			
			log.succ("银行前置成功接收核心返回的交易响应");
			//logger.info("......核心返回给银行前置响应信息......[" + responseText + "]");
			//logger.info("......核心返回给银行前置响应信息......[{}]",new Object[]{responseText});
			/*
			 * 记录响应日志
			 */
			this.printAfterLog(responseText, request);
			/*
			 * 返回响应报文给CRM
			 */
			logger.debug("响应给银行的报文长度为：" + responseText.getBytes().length);
			response.setContentLength(responseText.getBytes().length);
			pw = response.getWriter();
			pw.write(responseText);

		} catch (ServiceException e) {
			logger.error(e.getMessage());

			/*
			 * 获取请求中的数据，并组装成BankRequestMessage格式
			 */
			String xml = "";
			if (bankMsg != null) {
				xml = bankMsg.getXmldata();
			}
			this.writeReturnMessage(e.getMessage(), xml, request, response);
		} catch (Exception e) {
			logger.error("",e);

			/*
			 * 获取请求中的数据，并组装成BankRequestMessage格式
			 */
			String xml = "";
			if (bankMsg != null) {
				xml = bankMsg.getXmldata();
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
					gPay = BankXMLParser.parseXmlContent(xml);
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
			log.error("发起方交易流水号:{},错误码:{},错误描述:{}", new Object[]{header.getReqTransID(),header.getRspCode(),header.getRspDesc()});
			String resText = BankXMLParser.parseToXml(gPay);
			BankService bankService = (BankService) ServiceFactory.getInstance().findService("bankService");
			RemoteMsg remote = this.getRemoteMsg(request);
			resText = bankService.sign(remote.getIp(), resText);
            logger.error("<异常情况>银行前置返回给银行的报文信息为:[{}]",resText);
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
	private BankRequestMessage assemblyRequestData(HttpServletRequest request) throws ServiceException {

		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			String xmlData = request.getParameter(BankConstant.REQ_XML_DATA);
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
				//e.printStackTrace();
				logger.error("",e);
			}
		}
		BankRequestMessage bankMsg = new BankRequestMessage();
		bankMsg.setXmldata(sb.toString());
		return bankMsg;
	}

	/**
	 * 打印报文请求日志
	 * 
	 * @param crmMsg
	 * @param request
	 */
	private String printBeforeLog(BankRequestMessage bankMsg, HttpServletRequest request) {
		RemoteMsg remote = this.getRemoteMsg(request);
		//logger.info("[" + DateUtil.getCurrentFullDate() + "]客户端[" + remote.getIp() + "] 发送报文请求......");
		//logger.info(BankConstant.REQ_XML_DATA + " is [" + DateUtil.paseLog(bankMsg.getXmldata()) + "]");
		//logger.info("银行前置接受银行端的报文请求信息:"+BankConstant.REQ_XML_DATA + " is [{}]",new Object[]{DateUtil.paseLog(bankMsg.getXmldata())} );		
		logger.info("[{}]客户端[{}]向银行前置发送报文请求,"+BankConstant.REQ_XML_DATA+":[{}]",new Object[]{DateUtil.getCurrentFullDate(),remote.getIp(),DateUtil.paseLog(bankMsg.getXmldata())});
		return remote.getIp();
	}

	/**
	 * 打印报文响应日志
	 * 
	 * @param responseText
	 */
	private void printAfterLog(String responseText, HttpServletRequest request) {
		RemoteMsg remote = this.getRemoteMsg(request);
		//logger.info("[" + DateUtil.getCurrentFullDate() + "]银行前置返回响应给银行[" + remote.getIp() + "] ......");
		//logger.debug("The responseText is [" + responseText + "]");
		//logger.info("响应报文为[{}]",new Object[]{responseText});
		logger.info("[{}]银行前置返回响应给银行[{}],响应报文为[{}]",new Object[]{DateUtil.getCurrentFullDate(),remote.getIp(),DateUtil.paseLog(responseText)});
	}
	
	
	/**
	 * 获得返回报文FOR测试
	 */
	public String getRespText(){
		
		 return	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+"<InterBOSS>"
			+"<Version>0100</Version>"
		    +"<TestFlag>1</TestFlag>"
		    +"<BIPType>"
		      +"<BIPCode>BIP1A165</BIPCode>"
		      +"<ActivityCode>T1000158</ActivityCode>"
		      +"<ActionCode>1</ActionCode>"
		    +"</BIPType>"
		    +"<RoutingInfo>"
		      +"<OrigDomain>UPSS</OrigDomain>"
		      +"<RouteType>01</RouteType>"
		      +"<Routing>"
		        +"<HomeDomain>BOSS</HomeDomain>"
		        +"<RouteValue>13560017766</RouteValue>"
		      +"</Routing>"
		    +"</RoutingInfo>"
		    +"<TransInfo>"
		      +"<SessionID>1142201302111131530000</SessionID>"
		      +"<TransIDO>1042201334111131530000</TransIDO>"
		      +"<TransIDOTime>20130411113153</TransIDOTime>"
		      +"<TransIDH>123456789</TransIDH>"
		      +"<TransIDHTime>20130411113153</TransIDHTime>"
		    +"</TransInfo>"
		    +"<SNReserve>"
		      +"<TransIDC>1000110020440326155852979000000</TransIDC>"
		      +"<ConvID>ba62d153-d49d-4324-bfe5-8458416f59bd</ConvID>"
		      +"<CutOffDay>20130326</CutOffDay>"
		      +"<OSNTime>20130326155852</OSNTime>"
		      +"<OSNDUNS>2100</OSNDUNS>"
		      +"<HSNDUNS>2100</HSNDUNS>"
		      +"<MsgSender>2101</MsgSender>"
		      +"<MsgReceiver>0001</MsgReceiver>"
		      +"<Priority>7</Priority>"
		      +"<ServiceLevel>1</ServiceLevel>"
		    +"</SNReserve>"
		    +"<Response>"
		      +"<RspType>1</RspType>"
		      +"<RspCode>0000</RspCode>"
		      +"<RspDesc>ok</RspDesc>"
		    +"</Response>"
		    +"<SvcCont>"
		     +"<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		       +"<PaymentRsp>"
		        +"<IDType>01</IDType>"
		        +"<IDValue>13920009999</IDValue>"
		        +"<TransactionID>111222333123</TransactionID>"
		        +"<ActionDate>20130316</ActionDate>"
		        +"<UserCat>0</UserCat>"
		        +"<RspCode>0000</RspCode>"
		        +"<RspInfo>success</RspInfo>"
		       +"</PaymentRsp>]]>"
		    +"</SvcCont>"
		 +"</InterBOSS>";	
	}
	
}
