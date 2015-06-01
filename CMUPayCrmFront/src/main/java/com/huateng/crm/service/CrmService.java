package com.huateng.crm.service;

import com.huateng.core.base.BaseService;
import com.huateng.core.exception.ServiceException;
import com.huateng.crm.bean.message.head.InterBOSS;

/**
 * CRM 处理服务类
 * 
 * @author Gary
 * 
 */
public interface CrmService extends BaseService {
	/**
	 * 组装报文头和报文体成一部分
	 * 
	 * @param xmlHead
	 *            报文头
	 * @param xmlBody
	 *            报文体
	 * @return
	 */
	public InterBOSS assemblyXmlContent(String client,String xmlHead, String xmlBody) throws ServiceException;
}
