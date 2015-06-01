package com.huateng.core.base;

import com.huateng.core.exception.ServiceException;
import com.huateng.mmarket.bean.head.GPay;


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
	public String sendMsg(String orgId,String client,String xmlContent,GPay gay) throws ServiceException;

	/**
	 * 验签
	 * 
	 * @param xmlContent
	 * @return
	 */
	public String checkSign(String client,String plainText)throws ServiceException;
	
	public String sign(String client,String xmlContent)throws ServiceException;
	
	/**
	 * 商城验签
	 * 
	 * @param xmlContent
	 * @return
	 */
	public String mmarketCheckSign(String client,String plainText,GPay gPay)throws ServiceException;
	
	public String tmallSign(String client,String xmlContent)throws ServiceException;
}
