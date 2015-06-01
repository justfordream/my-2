package com.huateng.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huateng.jms.JMSSender;

/**
 * 与核心通信交互接口实现
 * 
 * @author Gary
 * 
 */
@Component
public class BaseTcpImpl implements BaseTcp {

	private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * JMS发送接口
	 */
	private JMSSender jmsSender;

	/**
	 * 发送消息
	 */
	public String sendMessage(String message) {
		logger.debug("请求核心:" + message);
		return jmsSender.send(message);

	}

	public void setJmsSender(JMSSender jmsSender) {
		this.jmsSender = jmsSender;
	}

}
