package com.huateng.core.common;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.DateUtil;
import com.huateng.third.common.AppCode;
import com.huateng.third.logFormat.MessageLogger;



/**
 * 发送消息的实现类
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
	
	/** 以报文中通讯流水为主键的一个线程安全hashmap */
	private static ConcurrentHashMap<String, RecvMessageBean> excuterthreadsmap = new ConcurrentHashMap<String, RecvMessageBean>();

	private static final Logger LOG = LoggerFactory
			.getLogger(SendMessageUtil.class);
	private MessageLogger log = MessageLogger.getLogger(getClass());
	
	/**
	 * 无返回的消息
	 * 
	 * @param destId 目标队列在配置文件（applicationContext-tupay-jms.xml）中的编号，不填则默认
	 * @param objectMsg
	 */
	@Override
	public void aSyncSendMsg(String destId, final Serializable objectMsg, final Map<String, String> paramMap) {
		destination = destinationMap.get(destId);
		// 如果找不到，则选择默认地址
		if(destination == null){
			destination = destinationMap.get("9999");
		}
		
		jmsTemplate4async.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage msg = session.createObjectMessage(objectMsg);
				
				msg.setBooleanProperty("isaSync", true);
				// 设置其他参数（如第三方支付机构号）
				if(MapUtils.isNotEmpty(paramMap)) {
					Set<String> keySet = paramMap.keySet();
					for(String key : keySet) {
						msg.setStringProperty(key, paramMap.get(key));
					}
				}
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
	public Message sendMsg(final String reqTxnSeq, final String orgId, final Map<String, String> paramsMap, final String txnCd, 
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
					ObjectMessage msg = session.createObjectMessage((Serializable) paramsMap);
					
					msg.setJMSReplyTo(respDest);
					msg.setStringProperty("senderid", AppCode.INST_ID);
					msg.setStringProperty("reqTxnSeq", reqTxnSeq);
					//msg.setStringProperty("txnCd", txnCd.toString());
					//msg.setLongProperty("hNumber", hNum);
					//for deploy only 3.17
					msg.setBooleanProperty("isaSync", false);
					return msg;
				}
			});
			LOG.debug(
					"=============message sended to {},txnCd_reqTxnSeq_context:{}",
					destination.toString(), txnCd + "_" + reqTxnSeq + "_"
							+ DateUtil.printMapLog(paramsMap));

			// 挂在该资源上，等待N秒内有回文
			acqureresult = temprecv.getRecvsemap().tryAcquire(
					template.getReceiveTimeout(), TimeUnit.MILLISECONDS);
			
		}finally{
			// 从hashmap里拿到收到的消息
			tempresult = excuterthreadsmap.remove(reqTxnSeq);
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

	/**
	 * 发送信息并获取返回文字信息
	 * 
	 * @param seq
	 * @param txnCd
	 * @param context
	 * @return
	 */
	public String sendTextRtnMessage(final String seq, final String orgId, final Map<String, String> paramsMap, final String txnCd, 
			 final Long hNum ) throws Exception {
		LOG.debug("银行前置发给核心平台组装后的报文信息:[{}]",new Object[]{paramsMap});
		log.info("银行前置发往核心请求报文");
		TextMessage msg = (TextMessage) sendMsg(seq,orgId, paramsMap, txnCd, hNum);
		String resp = null;
		try {
			if(msg!=null){
				resp = msg.getText();
			}
			log.info("银行前置接收核心的响应报文");
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
		/* 得到通讯报文中包号 */

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
}
