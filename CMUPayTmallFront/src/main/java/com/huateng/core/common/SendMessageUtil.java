package com.huateng.core.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.TimeUtil;
import com.huateng.log.MessageLogger;
import com.huateng.tmall.bean.head.GPay;
import com.huateng.tmall.bean.head.Header;
import com.huateng.tmall.common.AppCode;
import com.huateng.tmall.common.parse.TMallXMLParser;
/**
 * @author cmt
 *
 */
public class SendMessageUtil implements IClientSendMessage{

	private JmsTemplate template;
	private JmsTemplate jmsTemplate4async;
	private Destination destination;
	private Destination respDest;
	private Map<String, Destination> destinationMap;
	private Map<String, Destination> respDestMap;
	private Map<String, Destination> destinationQueryMap;
	private Map<String, Destination> respDestQueryMap;
    private static int chargeCount = 0; // 用来对充值交易进行计数
    

	public synchronized static int getChargeCount() {
		return chargeCount;
	}

	public synchronized static void addChargeCount() {
		if(chargeCount < Integer.MAX_VALUE) 
			SendMessageUtil.chargeCount++;
	}
	
	public synchronized static void delChargeCount() {
		SendMessageUtil.chargeCount--;
		if(chargeCount < 0) {
			chargeCount = 0;
		}
	}

	public Map<String, Destination> getDestinationQueryMap() {
		return destinationQueryMap;
	}

	public void setDestinationQueryMap(Map<String, Destination> destinationQueryMap) {
		this.destinationQueryMap = destinationQueryMap;
	}

	public Map<String, Destination> getRespDestQueryMap() {
		return respDestQueryMap;
	}

	public void setRespDestQueryMap(Map<String, Destination> respDestQueryMap) {
		this.respDestQueryMap = respDestQueryMap;
	}

	/** 以报文中通讯流水为主键的一个线程安全hashmap */
	private static ConcurrentHashMap<String, RecvMessageBean> excuterthreadsmap = new ConcurrentHashMap<String, RecvMessageBean>();

	private static final Logger LOG = LoggerFactory.getLogger(SendMessageUtil.class);
	private MessageLogger log = MessageLogger.getLogger(getClass());
	
	/**
	 * 流量监控开关
	 */
	private @Value("${FLOW_MONITOR_SWITCH}") String flowMonitorSwitch;
	
	/**
	 * 流量监控正常上限值
	 */
	private @Value("${NORMAL_COUNT}") String normalCount;
	
	/**
	 * 流量监控告警上限值
	 */
	private @Value("${WARN_COUNT}") String warnCount;
	
	/**
	 * 流量监控应急上限值
	 */
	private @Value("${EMERGENCY_COUNT}") String emergencyCount;

	/**
	 * 无返回的消息
	 * 
	 * @param txnCd
	 * @param context
	 */
	public void aSyncSendMsg(final String seq, final String orgId, final String context, final String txnCd, 
			 final Long hNum ) {
		destination = destinationMap.get(orgId);
		respDest = respDestMap.get(orgId);
		if(destination == null||respDest == null){
			destination = destinationMap.get("9999");
			respDest = respDestMap.get("9999");
		}
		jmsTemplate4async.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message msg = session.createTextMessage(context);
				msg.setStringProperty("reqTxnSeq", seq);
				//msg.setStringProperty("txnCd", txnCd.toString());
				//msg.setLongProperty("hNumber", hNum);
				msg.setBooleanProperty("isaSync", true);
				return msg;
			}
		});
	}

	/**
	 * 发送信息并获取返回信息
	 * 
	 * @param seq
	 * @param txnCd
	 * @param context
	 * @return
	 */
	public Message sendMsg1(final String reqTxnSeq, final String orgId, final String context, final String txnCd, 
			 final Long hNum ) throws Exception {
		destination = destinationMap.get(orgId);
		respDest = respDestMap.get(orgId);
		if(destination == null||respDest == null){
			destination = destinationMap.get("9999");
			respDest = respDestMap.get("9999");
		}

		RecvMessageBean temprecv = new RecvMessageBean();
		RecvMessageBean exTempRecv = excuterthreadsmap.putIfAbsent(reqTxnSeq, temprecv);
		RecvMessageBean tempresult = null;
		//如果已存在，就打个警告
		if(exTempRecv!=null){
			LOG.error("{} key duplicated",reqTxnSeq);
		}
		boolean acqureresult = false;
		try {
			template.send(destination, new MessageCreator() {
				public Message createMessage(Session session)
						throws JMSException {
					Message msg = session.createTextMessage(context);
					msg.setJMSReplyTo(respDest);
					msg.setStringProperty("senderid", AppCode.INST_ID);
					msg.setStringProperty("reqTxnSeq", reqTxnSeq);
					//msg.setStringProperty("txnCd", txnCd.toString());
					//msg.setLongProperty("hNumber", hNum);
					msg.setBooleanProperty("isaSync", false);
					return msg;
				}
			});
			LOG.info(
					"=============message sended to {},txnCd_reqTxnSeq_context:{}",
					destination.toString(), txnCd + "_" + reqTxnSeq + "_"
							+ context);

			// 挂在该资源上，等待N秒内有回文
			acqureresult = temprecv.getRecvsemap().tryAcquire(
					template.getReceiveTimeout(), TimeUnit.MILLISECONDS);
			
		}finally{
			// 从hashmap里拿到收到的消息
			tempresult = excuterthreadsmap.remove(reqTxnSeq);
			
			//流量监控
			int flowCount = excuterthreadsmap.size();
			this.tmallFlowMonitor(flowCount);
			
		}
		
		
		
		if (acqureresult && tempresult != null) {
			Message msg = tempresult.getRecvmsg();
			if (msg != null) {
				LOG.debug(
						"=============succeed to receive message from {},reqTxnSeq_returnMsg:{}",
						respDest.toString(), reqTxnSeq + "_" + msg);
				return msg;
			} else {
				LOG.error("=============receive message time out!reqTxnSeq:{}",
						reqTxnSeq);
				//return null;
				throw new ServiceException("UPAY-B-015A07");
			}
		} else {
			LOG.error("=============receive message time out!reqTxnSeq:{}",
					reqTxnSeq);
			LOG.error(" map size is [{}]", excuterthreadsmap.size());
			throw new ServiceException("UPAY-B-015A07");
			//return null;
		}
	}

	
	public String sendMsg(final String reqTxnSeq, final String orgId, final String context, final String txnCd, 
			GPay gay ) throws Exception {
		Header header = gay.getHeader();
		String activitycode = header.getActivityCode();
		
		//RecvMessageBean temprecv = new RecvMessageBean();
		//RecvMessageBean exTempRecv = excuterthreadsmap.putIfAbsent(reqTxnSeq, temprecv);
		//RecvMessageBean tempresult = null;
		//如果已存在，就打个警告
		/*if(exTempRecv!=null){
			LOG.error("{} key duplicated",reqTxnSeq);
		}*/
		//boolean acqureresult = false;
		//流量监控
		//将流量监控的方法提取到这个位置，使充值和查询都能做到监控
//		int flowCount = excuterthreadsmap.size();
//		this.tmallFlowMonitor(flowCount);
		if(CommonConstant.TmallTrans.Tmall04.getValue().equals(activitycode)){
			RecvMessageBean exTempRecv = null;
			RecvMessageBean temprecv = new RecvMessageBean();
			RecvMessageBean tempresult = null;
			boolean acqureresult = false;
			try{
			    exTempRecv = excuterthreadsmap.putIfAbsent(reqTxnSeq, temprecv);
			    if(exTempRecv!=null){
					LOG.error("{} key duplicated",reqTxnSeq);
				}
			    
				destination = destinationQueryMap.get("9999");
				respDest = respDestQueryMap.get("9999");
				
				template.send(destination, new MessageCreator() {
					public Message createMessage(Session session)
							throws JMSException {
						Message msg = session.createTextMessage(context);
						msg.setJMSReplyTo(respDest);
						msg.setStringProperty("senderid", AppCode.INST_ID);
						msg.setStringProperty("reqTxnSeq", reqTxnSeq);
						//msg.setStringProperty("txnCd", txnCd.toString());
						//msg.setLongProperty("hNumber", hNum);
						//msg.setBooleanProperty("isaSync", false);
						return msg;
					}
				});
				LOG.debug(
						"=============发送到核心的队列， 报文分别为：{},context:{}",
						destination.toString(), 
								 context);
				log.debug(
						"=============发送到核心的队列， 报文分别为：{},context:{}",
						destination.toString(), 
								 context);
				//long sendTime = System.currentTimeMillis();
		
				// 挂在该资源上，等待N秒内有回文
				acqureresult = temprecv.getRecvsemap().tryAcquire(
						template.getReceiveTimeout(), TimeUnit.MILLISECONDS);
			}finally{
				// 从hashmap里拿到收到的消息
				tempresult = excuterthreadsmap.remove(reqTxnSeq);
				//流量监控
				int flowCount = excuterthreadsmap.size() + getChargeCount();
				this.tmallFlowMonitor(flowCount);
				
			}
				if (acqureresult && tempresult != null) {
					Message msg = tempresult.getRecvmsg();
					TextMessage textmessage = (TextMessage)msg;
					if (textmessage != null) {
						LOG.debug(
								"=============成功接收的队列 {},消息流水号和消息内容为reqTxnSeq_returnMsg:{}",
								respDest.toString(), reqTxnSeq + "_" + textmessage.getText());
						log.debug(
								"=============成功接收的队列 {},消息流水号和消息内容为reqTxnSeq_returnMsg:{}",
								respDest.toString(), reqTxnSeq + "_" + textmessage.getText());
						
						return textmessage.getText();
					} else {
						LOG.error("=============接收消息超时!reqTxnSeq:{}",
								reqTxnSeq);
						log.error("=============接收消息超时!reqTxnSeq:{}",
								reqTxnSeq);
						//return null;
						throw new ServiceException("UPAY-B-015A07");
					}
				} else {
					LOG.error("=============接收消息超时!reqTxnSeq:{}",
							reqTxnSeq);
					log.error("=============接收消息超时!reqTxnSeq:{}",
							reqTxnSeq);
					LOG.error(" map size is [{}]", excuterthreadsmap.size());
					log.error(" map size is [{}]", excuterthreadsmap.size());
					throw new ServiceException("UPAY-B-015A07");
					//return null;
				}
				
				/*TextMessage respMsg = (TextMessage) this.getTemplate().receiveSelected(respDest,
						CoreConstant.RequestMsg.REQ_TXN_SEQ + "='" + reqTxnSeq + "'");	*/
			/*	long receiveTime = System.currentTimeMillis();
				long goTime = receiveTime - sendTime;
				long receiveTimeout = this.getTemplate().getReceiveTimeout();
				if (goTime >= receiveTimeout)
				{
					throw new ServiceException("UPAY-B-015A07");// 超时
				}
				if (respMsg != null)
				{
					String resMsg = respMsg.getText();
					return resMsg;
				}
				else
				{
					throw new ServiceException("UPAY-B-015A06");
				}*/
				
			
		}else{
			addChargeCount();
			destination = destinationMap.get("9999");
			respDest = respDestMap.get("9999");
/*			if(destination == null||respDest == null){
				destination = destinationMap.get("9999");
				respDest = respDestMap.get("9999");
			}*/
			try {
				template.send(destination, new MessageCreator() {
					public Message createMessage(Session session)
							throws JMSException {
						Message msg = session.createTextMessage(context);
						msg.setJMSReplyTo(respDest);
						msg.setStringProperty("senderid", AppCode.INST_ID);
						msg.setStringProperty("reqTxnSeq", reqTxnSeq);
						//msg.setStringProperty("txnCd", txnCd.toString());
						//msg.setLongProperty("hNumber", hNum);
						//msg.setBooleanProperty("isaSync", false);
						return msg;
					}
				});
			
				LOG.debug(
						"=============发送到核心的队列， 报文分别为：{},context:{}",
						destination.toString(), 
								 context);
				log.debug(
						"=============发送到核心的队列， 报文分别为：{},context:{}",
						destination.toString(), 
								 context);
			}finally{
				delChargeCount();
				int flowCount = excuterthreadsmap.size() + getChargeCount();
				this.tmallFlowMonitor(flowCount);
				
			}
			
			return getResponseXML("", context);
		}
		
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
		//resText = TmallXMLParser.parseGPay(gPay);
		resText = TMallXMLParser.parseGPay(gPay);
		//resText = this.sign(client, resText);

		return resText;

	}
	/**
	 * 发送信息并获取返回文字信息
	 * 
	 * @param seq
	 * @param txnCd
	 * @param context
	 * @return
	 */
	public String sendTextRtnMessage(final String seq, final String orgId, final String context, final String txnCd, 
			GPay gay) throws Exception {
		LOG.info("......天猫前置发送请求报文至核心{}",new Object[]{context});
		log.info("天猫前置发送请求报文至核心{}",new Object[]{context});
		//TextMessage msg = (TextMessage) sendMsg(seq,orgId, context, txnCd, hNum);
		String resp = null;
		resp = sendMsg(seq,orgId, context, txnCd, gay);
		try {
			/*if(msg!=null){
				resp = msg.getText();
			}*/
			// LOG.info("......天猫前置收到核心返回的响应信息:{}",new Object[]{resp});
			LOG.info("......天猫前置应答天猫的响应信息:{}",new Object[]{resp});
			log.info("天猫前置应答天猫的响应信息:{}",new Object[]{resp});
		} catch (Exception e) {
			LOG.error("get text msg from return message result error : {}",
					e);
			throw new ServiceException("UPAY-B-015A06");
		}
		return resp;
	}

	/**
	 * @return the template
	 */
	public JmsTemplate getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * @return the destination
	 */
	public Destination getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * @return the respDest
	 */
	public Destination getRespDest() {
		return respDest;
	}

	/**
	 * @param respDest
	 *            the respDest to set
	 */
	public void setRespDest(Destination respDest) {
		this.respDest = respDest;
	}

	public void recvmessagefromserver(Message recvmessage) {
		if (recvmessage == null) {
			LOG.info(" recvmessagefromserver error");
			return;
		}
		// 得到通讯报文中包号 

		String key;
		try {
			key = recvmessage.getStringProperty("reqTxnSeq");
			RecvMessageBean temprecv = excuterthreadsmap.get(key);

			if (temprecv != null) {
				temprecv.setRecvmsg(recvmessage);
				temprecv.getRecvsemap().release();
			} else {
				LOG.info(" map size is [{}]", excuterthreadsmap.size());
				LOG.info(" recvmessagefromserver key [{}]: is null", key);
			}
		} catch (JMSException e) {
			
			LOG.error("",e);
		}
		// 挂在资源上的消息放入，同时唤醒工作线程

	}
	
	
	public void tmallFlowMonitor(int count) throws ServiceException {
		
		if(CoreConstant.SWITCH_OPEN.equalsIgnoreCase(flowMonitorSwitch)){
			LOG.debug("......天猫前置流量监控开启......");
			try {
				if(count <= Integer.valueOf(normalCount.trim())){
					LOG.info("......当前系统的用户连接数为:{}.....",new Object[]{count});
				 }
				if(count > Integer.valueOf(normalCount.trim()) && count <= Integer.valueOf(warnCount.trim())){
					LOG.warn("......当前系统的用户连接数为:{}.....",new Object[]{count});
				}				
                if(count > Integer.valueOf(warnCount.trim()) && count <= Integer.valueOf(emergencyCount.trim())){
                	LOG.warn("......当前系统的用户连接数为:{}.....",new Object[]{count});

				}				
			} catch (Exception e) {
				throw new ServiceException("UPAY-B-015A05");
			}
		}		
	}
	
	

	/**
	 * @return the jmsTemplate4async
	 */
	public JmsTemplate getJmsTemplate4async() {
		return jmsTemplate4async;
	}

	/**
	 * @param jmsTemplate4async the jmsTemplate4async to set
	 */
	public void setJmsTemplate4async(JmsTemplate jmsTemplate4async) {
		this.jmsTemplate4async = jmsTemplate4async;
	}

	public Map<String, Destination> getDestinationMap() {
		return destinationMap;
	}

	public void setDestinationMap(Map<String, Destination> destinationMap) {
		this.destinationMap = destinationMap;
	}

	public Map<String, Destination> getRespDestMap() {
		return respDestMap;
	}

	public void setRespDestMap(Map<String, Destination> respDestMap) {
		this.respDestMap = respDestMap;
	}

	public String getFlowMonitorSwitch() {
		return flowMonitorSwitch;
	}

	public void setFlowMonitorSwitch(String flowMonitorSwitch) {
		this.flowMonitorSwitch = flowMonitorSwitch;
	}

	public String getNormalCount() {
		return normalCount;
	}

	public void setNormalCount(String normalCount) {
		this.normalCount = normalCount;
	}

	public String getWarnCount() {
		return warnCount;
	}

	public void setWarnCount(String warnCount) {
		this.warnCount = warnCount;
	}

	public String getEmergencyCount() {
		return emergencyCount;
	}

	public void setEmergencyCount(String emergencyCount) {
		this.emergencyCount = emergencyCount;
	}



}
