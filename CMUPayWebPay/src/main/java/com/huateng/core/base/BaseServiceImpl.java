package com.huateng.core.base;

import com.huateng.core.adapter.JMSHandler;

/**
 * 基类实现
 * 
 * @author Gary
 * 
 */
public class BaseServiceImpl implements BaseService {
	private JMSHandler jmsHandler;

	public String sendJsonMsg(String message) {
		return this.jmsHandler.sendMessage(message);
	}

}
