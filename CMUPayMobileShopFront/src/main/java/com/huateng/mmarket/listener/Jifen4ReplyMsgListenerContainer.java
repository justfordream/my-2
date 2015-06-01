package com.huateng.mmarket.listener;

import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.huateng.mmarket.common.AppCode;

/**
 * @author ol
 *
 */
public class Jifen4ReplyMsgListenerContainer extends DefaultMessageListenerContainer{
	private String selector = "senderid='" + AppCode.INST_ID + "'";
	/**
	 * 
	 */
	public Jifen4ReplyMsgListenerContainer() {
		super();
		//设置过滤器
		setMessageSelector(selector);
	}
}
