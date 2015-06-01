package com.huateng.core.base;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.IClientSendMessage;
import com.huateng.core.exception.ServiceException;

/**
 * 基类实现
 * 
 * @author Gary
 * 
 */
public abstract class BaseServiceImpl implements BaseService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String sourceId;
	private IClientSendMessage jmsSendUtil;

	/**
	 * 判断是否调用核心的开关
	 */
	private @Value("${INVOKE_CORE}")
	String invokeCore;

	public String getInvokeCore() {
		return invokeCore;
	}

	public void setInvokeCore(String invokeCore) {
		this.invokeCore = invokeCore;
	}

	/**
	 * 发送消息到核心
	 */
	@Override
	public void sendMsg(String destId, final Serializable objectMsg, final Map<String, String> paramMap)
			throws ServiceException {
		if (objectMsg != null) {
				if (CoreConstant.SWITCH_OPEN.equals(invokeCore.trim())) {
					// 异步发送，不需要核心响应
					jmsSendUtil.aSyncSendMsg(destId, objectMsg, paramMap);
				} 

		} else {
			logger.info("", "需要发送到核心的Object消息为NULL，没有数据！");
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}

	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public IClientSendMessage getJmsSendUtil() {
		return jmsSendUtil;
	}

	public void setJmsSendUtil(IClientSendMessage jmsSendUtil) {
		this.jmsSendUtil = jmsSendUtil;
	}

}
