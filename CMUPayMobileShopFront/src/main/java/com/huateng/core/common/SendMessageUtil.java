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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.TimeUtil;
import com.huateng.mmarket.bean.head.GPay;
import com.huateng.mmarket.bean.head.Header;
import com.huateng.mmarket.common.AppCode;
import com.huateng.mmarket.common.parse.TMallXMLParser;



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
	//用于异步
	private Map<String, Destination> destination4Map;
	private Map<String, Destination> respDest4Map;
	/** 以报文中通讯流水为主键的一个线程安全hashmap */
	private static ConcurrentHashMap<String, RecvMessageBean> excuterthreadsmap = new ConcurrentHashMap<String, RecvMessageBean>();

	private static final Logger LOG = LoggerFactory
			.getLogger(SendMessageUtil.class);

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
			
			//等待10秒
//			System.out.println("----------------等待10秒 -------------------");
//			Thread t=Thread.currentThread();
//			t.sleep(10000);
//			System.out.println("----------------获取消息--------------------");
//			//从队列取返回信息
//			Message msg1=template.receive();
//			System.out.println("----------------消息内容reqTxnSeq:"+msg1.getStringProperty("reqTxnSeq"));
//			if(msg1!=null){
//				LOG.debug(
//						"=============succeed to receive message from {},reqTxnSeq_returnMsg:{}",
//						respDest.toString(), reqTxnSeq + "_" + msg1);
//				return msg1;
//			} else {
//				LOG.error("=============receive message time out!reqTxnSeq:{}",reqTxnSeq);
//				//return null;
//				throw new ServiceException("UPAY-B-015A07");
//			}
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
	
	@Override
	public String sendMsg(final String reqTxnSeq, final String orgId, final String context, final String txnCd,final Long hNum ,GPay gay) throws Exception {
		
		Header header=gay.getHeader();
		String activitycode=header.getActivityCode();


		RecvMessageBean temprecv = new RecvMessageBean();
		RecvMessageBean exTempRecv = excuterthreadsmap.putIfAbsent(reqTxnSeq, temprecv);
		RecvMessageBean tempresult = null;
		//如果已存在，就打个警告
		if(exTempRecv!=null){
			LOG.error("{} key duplicated",reqTxnSeq);
		}
		boolean acqureresult = false;
		/**
		 * 充值/缴费查询,退费,支付查询 均为同步
		 * */
		if(!CommonConstant.marketCode.market01.getValue().equals(activitycode)){
			//同步
			try {
				destination = destinationMap.get(orgId);
				respDest = respDestMap.get(orgId);
				if(destination == null||respDest == null){
					destination = destinationMap.get("9999");
					respDest = respDestMap.get("9999");
				}
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
				
				//等待10秒
//				System.out.println("----------------等待10秒 -------------------");
//				Thread t=Thread.currentThread();
//				t.sleep(10000);
//				System.out.println("----------------获取消息--------------------");
//				//从队列取返回信息
//				Message msg1=template.receive();
//				System.out.println("----------------消息内容reqTxnSeq:"+msg1.getStringProperty("reqTxnSeq"));
//				if(msg1!=null){
//					LOG.debug(
//							"=============succeed to receive message from {},reqTxnSeq_returnMsg:{}",
//							respDest.toString(), reqTxnSeq + "_" + msg1);
//					return msg1;
//				} else {
//					LOG.error("=============receive message time out!reqTxnSeq:{}",reqTxnSeq);
//					//return null;
//					throw new ServiceException("UPAY-B-015A07");
//				}
				// 挂在该资源上，等待N秒内有回文
				acqureresult = temprecv.getRecvsemap().tryAcquire(
						template.getReceiveTimeout(), TimeUnit.MILLISECONDS);
				
			}finally{
				// 从hashmap里拿到收到的消息
				tempresult = excuterthreadsmap.remove(reqTxnSeq);
			}
			
			
			if (acqureresult && tempresult != null) {
				Message msg = tempresult.getRecvmsg();
				TextMessage textmessage = (TextMessage)msg;
				if (textmessage != null) {
					LOG.debug(
							"=============succeed to receive message from {},reqTxnSeq_returnMsg:{}",
							respDest.toString(), reqTxnSeq + "_" + msg);
					return textmessage.getText();
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
		}else{
			//异步
			destination = destination4Map.get("9999");
			respDest = respDest4Map.get("9999");
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
			
				LOG.info(
						"=============发送到核心的队列， 报文分别为：{},context:{}",
						destination.toString(), 
								 context);
				
			}finally{
			}
			
			return getResponseXML("", context);
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
	public String sendTextRtnMessage(final String seq, final String orgId, final String context, final String txnCd, 
			 final Long hNum ,GPay gay) throws Exception {
		LOG.info("......商城前置发送请求报文至核心{}",new Object[]{context});
		//TextMessage msg = (TextMessage) sendMsg(seq,orgId, context, txnCd, hNum);
		String resp = null;
		
		resp = sendMsg(seq,orgId,context,txnCd,hNum,gay);
		try {
			/*if(msg!=null){
				resp = msg.getText();
			}*/
			// LOG.info("......天猫前置收到核心返回的响应信息:{}",new Object[]{resp});
		} catch (Exception e) {
			LOG.error("get text msg from return message result error : {}",
					e);
			throw new ServiceException("UPAY-B-015A06");
		}
		return resp;
	}
	/**
	 * 返回一个应答报文
	 * @author ol
	 * */
	public String getResponseXML(String body, String xmlContent) throws Exception {

		GPay gPay = null;
		Header header = null;
		String resText = "";
		//gPay = TmallXMLParser.parseXmlContent(xmlContent);
		gPay = TMallXMLParser.parseXmlContent(xmlContent);
		header = gPay.getHeader();
		header.setActionCode("1");
		header.setRcvDate(TimeUtil.getSystemOfDateByFormat(TimeUtil.FORMAT_YMD));
		header.setRcvDateTime(TimeUtil.getSystemOfDateByFormat(TimeUtil.FORMAT_YMDHMSS));
		header.setRspCode(CommonConstant.market.market_010A00.getValue());
		header.setRspDesc(CommonConstant.market.market_010A00.getDesc());

		gPay.setHeader(header);

		// 应答body为空
		gPay.setBody("");
		//resText = TmallXMLParser.parseGPay(gPay);
		resText = TMallXMLParser.parseGPay(gPay);
		//resText = this.sign(client, resText);
		return resText;

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

	public Map<String, Destination> getDestination4Map() {
		return destination4Map;
	}

	public void setDestination4Map(Map<String, Destination> destination4Map) {
		this.destination4Map = destination4Map;
	}

	public Map<String, Destination> getRespDest4Map() {
		return respDest4Map;
	}

	public void setRespDest4Map(Map<String, Destination> respDest4Map) {
		this.respDest4Map = respDest4Map;
	}

}
