package com.huateng.core.base;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.IClientSendMessage;
import com.huateng.core.common.Serial;
import com.huateng.core.exception.ServiceException;
import com.huateng.mmarket.bean.head.GPay;

/**
 * 基类实现
 * 
 * @author Gary
 * 
 */
public abstract class BaseServiceImpl implements BaseService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String sourceId;
	private IClientSendMessage jmsSendUtil;
	
	/**
	 *判断是否调用核心的开关
	 */
	private @Value("${INVOKE_CORE}") String invokeCore;
	

	public String getInvokeCore() {
		return invokeCore;
	}

	public void setInvokeCore(String invokeCore) {
		this.invokeCore = invokeCore;
	}

	/**
	 * 发送消息
	 */
	public String sendMsg(String orgId, String client, final String xmlContent,GPay gay)
			throws ServiceException {
		if (StringUtils.isNotBlank(xmlContent)) {
			//final String reqTxnSeq = Serial.genSerialNo(sourceId);
			//System.out.println("请求报文:"+xmlContent);
			logger.info("请求报文:"+xmlContent);
			final String reqTxnSeq = Serial.generateSerialNo(sourceId);
			String respMsg = null;
			try {	
				if(CoreConstant.SWITCH_OPEN.equals(invokeCore.trim())){
					respMsg = jmsSendUtil.sendTextRtnMessage(reqTxnSeq,
							orgId, xmlContent, "", null,gay);
				}else{
					respMsg = this.getRespText();
				}
				System.out.println("respMsg: "+respMsg);
				
				if (respMsg != null) {
					respMsg = this.sign(client, respMsg);
					System.out.println("respMsg is not null : "+respMsg);
					return respMsg;
				} else {
					throw new ServiceException("UPAY-B-015A06");
				}
			} catch (Exception e) {
				logger.error("", e);
				throw new ServiceException(e.getMessage());
			}
		} else {
			// GPay gPay = new GPay();
			// Header header = new Header();
			// header.setRspCode("015A05");
			// header.setRspDesc("解析报文失败");
			// gPay.setHeader(header);
			// return BankXMLParser.parseGPay(gPay);
			System.out.println("======================xmlContent为空，发送失败!");
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}

	}
	
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public IClientSendMessage getJmsSendUtil() {
		return jmsSendUtil;
	}

	public void setJmsSendUtil(IClientSendMessage jmsSendUtil) {
		this.jmsSendUtil = jmsSendUtil;
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
		       +"<PaymentRsp>"
		        +"<IDType>01</IDType>"
		        +"<IDValue>13920009999</IDValue>"
		        +"<TransactionID>111222333123</TransactionID>"
		        +"<ActionDate>20130316</ActionDate>"
		        +"<UserCat>0</UserCat>"
		        +"<RspCode>0000</RspCode>"
		        +"<RspInfo>success</RspInfo>"
		       +"</PaymentRsp>"
		    +"</SvcCont>"
		 +"</InterBOSS>";	
	}

}
