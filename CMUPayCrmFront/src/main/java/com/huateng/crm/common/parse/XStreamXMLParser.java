package com.huateng.crm.common.parse;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.exception.ServiceException;
import com.huateng.crm.bean.message.head.BIPType;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.bean.message.head.Response;
import com.huateng.crm.bean.message.head.Routing;
import com.huateng.crm.bean.message.head.RoutingInfo;
import com.huateng.crm.bean.message.head.SNReserve;
import com.huateng.crm.bean.message.head.TransInfo;
import com.huateng.log.XmlUtil;


public class XStreamXMLParser {
	private final static Logger logger = LoggerFactory.getLogger(XStreamXMLParser.class);
	

	/**
	 * 组织报文头、报文体
	 * 
	 * @param xmlHead
	 * @param xmlBody
	 * @return
	 */
	public static InterBOSS parse(String xmlHead, String xmlBody) throws ServiceException {
		logger.debug("报文头信息如下:");
		logger.debug(xmlHead);
		logger.debug("报文体信息如下:");
		logger.debug(xmlBody);
		if (StringUtils.isBlank(xmlHead)) {
			throw new ServiceException("UPAY-C-0105");// OSN发现Message Header
														// 格式错误
		}
		if (StringUtils.isBlank(xmlBody)) {
			throw new ServiceException("UPAY-C-0106");// OSN发现Message Body格式错误
		}
		Map<String, Class<?>> mapAlias = new HashMap<String, Class<?>>();
		mapAlias.put("InterBOSS", InterBOSS.class);
		mapAlias.put("BIPType", BIPType.class);
		mapAlias.put("RoutingInfo", RoutingInfo.class);
		mapAlias.put("Routing", Routing.class);
		mapAlias.put("TransInfo", TransInfo.class);
		mapAlias.put("SNReserve", SNReserve.class);
		mapAlias.put("Response", Response.class);
		InterBOSS boss = XmlUtil.fromXml(true,xmlHead, mapAlias);
		InterBOSS body = XmlUtil.fromXml(true,xmlBody, mapAlias);
		boss.setSvcCont("<![CDATA[" + body.getSvcCont() + "]]>");
		return boss;
	}

	public static String readToXml(InterBOSS boss) throws ServiceException {
		String result = XmlUtil.toXml(boss, InterBOSS.class, true);
		return result;
	}

	
}
