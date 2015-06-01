package com.huateng.core.base;

import java.io.Serializable;
import java.util.Map;

import com.huateng.core.exception.ServiceException;


/**
 * 基类接口
 * 
 * @author Gary
 * 
 */
public interface BaseService {
	/**
	 * 报文内容
	 * @param destId 目标队列在配置文件（applicationContext-tupay-jms.xml）中的编号，不填则默认
	 * @param objectMsg
	 * @return
	 */
	public void sendMsg(String destId, Serializable objectMsg, final Map<String, String> paramMap) throws ServiceException;

	/**
	 * 验签
	 * 
	 * @param paramsMaps
	 * @return
	 */
	public Map<String, String> checkSign(String client, Map<String, String> paramsMaps)throws ServiceException;
	
	/**
	 * 签名
	 * @param client
	 * @param paramsMaps
	 * @return
	 * @throws ServiceException
	 */
	public Map<String, String> sign(String client, Map<String, String> paramsMaps)throws ServiceException;
}
