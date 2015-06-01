package com.huateng.mmarket.listener;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.common.IClientSendMessage;


/**
 * @author cmt
 *
 */
public class JmsClientRecvListener implements MessageListener {
	private Logger log = LoggerFactory.getLogger(JmsClientRecvListener.class);

	/**
	 * client templeate
	 */
	private IClientSendMessage clientrecv;

	public IClientSendMessage getClientrecv() {
		return clientrecv;
	}

	public void setClientrecv(IClientSendMessage c) {
		this.clientrecv =c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message message) {
		//TextMessage msg = (TextMessage) message;
		//Destination replyDest = null;
		try {
			clientrecv.recvmessagefromserver(message);

		} catch (Exception e) {
			log.error("",e);

		}

	}
}
