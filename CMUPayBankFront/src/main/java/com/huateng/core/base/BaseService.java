package com.huateng.core.base;

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
	 * 
	 * @param xmlContent
	 * @return
	 */
	public String sendMsg(String orgId,String client,String xmlContent) throws ServiceException;

	/**
	 * 验签
	 * 
	 * @param xmlContent
	 * @return
	 */
	public String checkSign(String client,String plainText)throws ServiceException;
	
	public String sign(String client,String xmlContent)throws ServiceException;
}
