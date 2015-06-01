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
public class QueryReplyMsgListenerContainer extends DefaultMessageListenerContainer {

	private String selector = "senderid='" + AppCode.INST_ID + "'";
	/**
	 * 
	 */
	public QueryReplyMsgListenerContainer() {
		super();
		//设置过滤器
		setMessageSelector(selector);
	}

}
