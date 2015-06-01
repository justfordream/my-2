package com.huateng.mmarket.service;

import java.util.Map;

import com.huateng.core.base.BaseService;
import com.huateng.core.exception.ServiceException;
import com.huateng.mmarket.bean.head.GPay;

/**
 * Bank 处理服务类
 * 
 * @author Gary
 * 
 */
public interface MMarketService extends BaseService {
	/**
	 * 组装crm报文信息
	 * 
	 * @param xmlContent
	 * @return
	 * @throws ServiceException 
	 */
	
//	public Map assemblyCrmXml(GPay gPay,String txnSeq,String transIdHTime) throws ServiceException;

	public GPay assemblyGPayMessage(String xmlContent) throws ServiceException;

	/**
	 * 组装天猫充值相应报文
	 * 
	 * @param crmResXml
	 * @return
	 * @throws ServiceException 
	 */
//	public Map assemblyTMallResXml(GPay gpay,String crmResXml,String txnSeq,String transIdHTime) throws ServiceException;
	
	/**
	 * 判断当前是否为应急方案
	 * 
	 * @return
	 */
	public boolean checkIsEmergency();

}
