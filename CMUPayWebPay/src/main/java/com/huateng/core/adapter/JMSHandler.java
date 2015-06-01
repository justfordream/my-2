package com.huateng.core.adapter;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * JMS消息发送类
 * 
 * @author Gary
 * 
 */
public class JMSHandler {
	private final Logger logger = Logger.getLogger(JMSHandler.class);
	private JmsTemplate template;
	private Destination destination;
	private Destination respDest;
	private ObjectMapper jsonMapper;
	private String queryKey;
	private String md5Key;

	public String sendMessage(final String message) {
		/*
		 * 使用单例模式获取mq序列
		 */
		final long seq = System.currentTimeMillis();
		template.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message msg = session.createTextMessage(message);
				msg.setJMSReplyTo(respDest);
				msg.setLongProperty("reqTxnSeq", seq);
				return msg;
			}
		});
		TextMessage respMsg = (TextMessage) template.receiveSelected(respDest, "reqTxnSeq=" + seq);
		String responseJson = null;
		try {
			responseJson = respMsg.getText();
		} catch (JMSException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		logger.debug(responseJson);

		return responseJson;
	}

	public JmsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public Destination getRespDest() {
		return respDest;
	}

	public void setRespDest(Destination respDest) {
		this.respDest = respDest;
	}

	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}

	public void setJsonMapper(ObjectMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
	}

	public String getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

	public String getMd5Key() {
		return md5Key;
	}

	public void setMd5Key(String md5Key) {
		this.md5Key = md5Key;
	}

}
