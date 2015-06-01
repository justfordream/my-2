package com.huateng.core.base;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.IClientSendMessage;
import com.huateng.core.common.Serial;
import com.huateng.core.exception.ServiceException;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.common.parse.Dom4jXMLParser;
/**
 * 基类实现
 * 
 * @author Gary
 * 
 */
public abstract class BaseServiceImpl implements BaseService {
	private Logger logger = LoggerFactory.getLogger(getClass());
//	private JmsTemplate template;
//	private Destination destination;
//	private Destination respDest;
//	
//	private Map<String, Destination> destinationMap;
//	private Map<String, Destination> respDestMap;
	
	private String sourceId;
	private IClientSendMessage jmsSendUtil;  
	/**
	 * 是否调用核心的开关
	 */
	private @Value("${INVOKE_CORE}") String invokeCore;
	
	public String getInvokeCore() {
		return invokeCore;
	}

	public void setInvokeCore(String invokeCore) {
		this.invokeCore = invokeCore;
	}

	
//	public Map<String, Destination> getDestinationMap() {
//		return destinationMap;
//	}
//
//	public void setDestinationMap(Map<String, Destination> destinationMap) {
//		this.destinationMap = destinationMap;
//	}
//
//	public Map<String, Destination> getRespDestMap() {
//		return respDestMap;
//	}
//
//	public void setRespDestMap(Map<String, Destination> respDestMap) {
//		this.respDestMap = respDestMap;
//	}

	/**
	 * 发送消息
	 */
	@Override
	public String sendMsg(String orgId,String client, final String xmlContent) throws ServiceException {
		if (StringUtils.isNotBlank(xmlContent)) {
//			destination = destinationMap.get(orgId);
//			respDest = respDestMap.get(orgId);
//			if(destination == null||respDest == null){
//				destination = destinationMap.get("9999");
//				respDest = respDestMap.get("9999");
//			}
			
			//final String reqTxnSeq = Serial.genSerialNo(sourceId);
			final String reqTxnSeq = Serial.generateSerialNo(sourceId);
			String respMsg = null;
			
			try {
//				template.send(destination, new MessageCreator() {
//					public Message createMessage(Session session) throws JMSException {
//						Message msg = session.createTextMessage(xmlContent);
//						msg.setJMSReplyTo(respDest);
//						msg.setStringProperty(CoreConstant.RequestMsg.REQ_TXN_SEQ, reqTxnSeq);
//						//msg.getJMSMessageID();
//						return msg;
//					}
//				});
//				//JMSRedelivered 
//				
//				long sendTime = System.currentTimeMillis();
//				//template.browseSelected(respDest, CoreConstant.RequestMsg.REQ_TXN_SEQ + "='" + reqTxnSeq + "'",null);
//				TextMessage respMsg = (TextMessage) template.receiveSelected(respDest,
//						CoreConstant.RequestMsg.REQ_TXN_SEQ + "='" + reqTxnSeq + "'");
//				//respMsg.acknowledge();
//				long receiveTime = System.currentTimeMillis();
//				long goTime = receiveTime - sendTime;
//				long receiveTimeout = this.template.getReceiveTimeout();
//				if (goTime >= receiveTimeout) {
//					throw new ServiceException("UPAY-C-5A07");//超时
//				}
//				
//			
//				
//				
//				if (respMsg != null) {
//					String resMsg = respMsg.getText();
//					resMsg = resMsg.replaceAll("<SvcCont>",
//							"<SvcCont><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>").replaceAll("</SvcCont>",
//							"]]></SvcCont>");
//					InterBOSS boss = Dom4jXMLParser.parseXmlContent(resMsg);
//					String decryptBodyText = boss.getSvcCont();
//					String encryptBodyText = this.encryptXmlBody(client, decryptBodyText,(boss.getSNReserve() == null?"":boss.getSNReserve().getMsgSender()),(boss.getBIPType() == null?"":boss.getBIPType().getActivityCode()));
//					boss.setSvcCont(encryptBodyText);
////					if(StringUtils.isNotBlank(resMsg)){
////						resMsg = "<![CDATA["+resMsg+"]]>";
////						boss.setSvcCont(resMsg);
////					}
//					String resText = boss.getXmlContent();
//					return resText;
//				} else {
//					throw new ServiceException("UPAY-C-2998");
//				}

				if(CoreConstant.SWITCH_OPEN.equals(invokeCore.trim())){
					 respMsg = jmsSendUtil.sendTextRtnMessage(reqTxnSeq, orgId, xmlContent, "",null);
				}else{
					 respMsg = this.getRespText();
				}
								
				if (respMsg != null) {
					respMsg = respMsg
							.replaceAll("<SvcCont>",
									"<SvcCont><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
							.replaceAll("</SvcCont>", "]]></SvcCont>");					
					InterBOSS boss = Dom4jXMLParser.parseXmlContent(respMsg);
					String decryptBodyText = boss.getSvcCont();
					String encryptBodyText = this.encryptXmlBody(client,
							decryptBodyText, (boss.getSNReserve() == null ? ""
									: boss.getSNReserve().getMsgSender()),
									(boss.getBIPType() == null ? "" : boss.getBIPType()
											.getBIPCode()),
							(boss.getBIPType() == null ? "" : boss.getBIPType()
									.getActivityCode()));
					boss.setSvcCont(encryptBodyText);

					String resText = boss.getXmlContent();
					return resText;
				} else {
					throw new ServiceException("UPAY-C-2998");
				}
				
			} catch (Exception e) {
				logger.error("",e);
				throw new ServiceException(e.getMessage());
			}
		} else {
			throw new ServiceException("UPAY-C-5A05");// 解析报文失败
		}
	}

//	public JmsTemplate getTemplate() {
//		return template;
//	}
//
//	public void setTemplate(JmsTemplate template) {
//		this.template = template;
//	}
//
//	public Destination getDestination() {
//		return destination;
//	}
//
//	public void setDestination(Destination destination) {
//		this.destination = destination;
//	}
//
//	public Destination getRespDest() {
//		return respDest;
//	}
//
//	public void setRespDest(Destination respDest) {
//		this.respDest = respDest;
//	}

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

}
