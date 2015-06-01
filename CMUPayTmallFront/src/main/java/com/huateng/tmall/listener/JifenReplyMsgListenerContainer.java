/**
 * 
 */
package com.huateng.tmall.listener;

import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.huateng.tmall.common.AppCode;


/**
 * @author cmt
 *
 */
public class JifenReplyMsgListenerContainer extends DefaultMessageListenerContainer {

	private String selector = "senderid='" + AppCode.INST_ID + "'";
	/**
	 * 
	 */
	public JifenReplyMsgListenerContainer() {
		super();
		//设置过滤器
		setMessageSelector(selector);
	}

}
