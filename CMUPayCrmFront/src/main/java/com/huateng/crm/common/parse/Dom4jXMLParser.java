package com.huateng.crm.common.parse;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.base.XMLParser;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.ReflectUtil;
import com.huateng.upay.model.TUpayKey;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.common.UUIDGenerator;

/**
 * Dom4j解析XML
 * 
 * @author Gary
 * 
 */
public class Dom4jXMLParser extends XMLParser {
	private final static Logger logger = LoggerFactory.getLogger(Dom4jXMLParser.class);
	protected static String PREFIX_CDATA = "<![CDATA[";
	protected static String SUFFIX_CDATA = "]]>";

	/**
	 * 组织报文头、报文体
	 * 
	 * @param xmlHead
	 *            报文头
	 * @param xmlBody
	 *            报文体
	 * @return
	 */
	public static InterBOSS readXML(String xmlHead, String xmlBody) throws ServiceException {
		logger.debug("报文头信息如下:");
		logger.debug(xmlHead);
		logger.debug("报文体信息如下:");
		logger.debug(xmlBody);
		if (StringUtils.isBlank(xmlHead)) {
			throw new ServiceException("UPAY-C-0105");// OSN发现Message Header格式错误
		}
		if (StringUtils.isBlank(xmlBody)) {
			throw new ServiceException("UPAY-C-0106");// OSN发现Message Body格式错误
		}
		InterBOSS boss = new InterBOSS();
		try {
			Document doc = DocumentHelper.parseText(xmlHead);
			Element root = doc.getRootElement();
			Dom4jXMLParser.settingElement(boss, root);
		} catch (DocumentException e) {
			logger.error("报文头解析失败,失败原因：" + e.getMessage());
			throw new ServiceException("UPAY-C-5A05");// 解析报文失败
		}
		InterBOSS bodyBoss = Dom4jXMLParser.parseXmlBody(xmlBody);
		boss.setSvcCont(bodyBoss.getSvcCont());
		return boss;
	}

	/**
	 * 解析整个报文
	 * 
	 * @param xmlContent
	 * @return
	 * @throws ServiceException
	 */
	public static InterBOSS parseXmlContent(String xmlContent) throws ServiceException {
		logger.debug("报文信息如下:");
		logger.debug(xmlContent);
		if (StringUtils.isBlank(xmlContent)) {
			throw new ServiceException("UPAY-C-5A06");// 系统未知错误
		}
		InterBOSS boss = new InterBOSS();
		try {
			Document doc = DocumentHelper.parseText(xmlContent);
			Element root = doc.getRootElement();
			Dom4jXMLParser.settingElement(boss, root);
		} catch (DocumentException e) {
			logger.error("报文解析失败,失败原因：" + e.getMessage());
			throw new ServiceException("UPAY-C-5A05");// 解析报文失败
		}
		return boss;
	}
	/**
	 * 组织报文头、报文体
	 * 
	 * @param xmlHead
	 *            报文头
	 * @param xmlBody
	 *            报文体
	 * @return
	 */
	public static InterBOSS readXMLHead(String xmlHead) throws ServiceException {
		logger.debug("报文头信息如下:");
		logger.debug(xmlHead);
		if (StringUtils.isBlank(xmlHead)) {
			throw new ServiceException("UPAY-C-0105");// OSN发现Message Header格式错误
		}
		InterBOSS boss = new InterBOSS();
		try {
			Document doc = DocumentHelper.parseText(xmlHead);
			Element root = doc.getRootElement();
			Dom4jXMLParser.settingElement(boss, root);
		} catch (DocumentException e) {
			logger.error("报文头解析失败,失败原因：" + e.getMessage());
			throw new ServiceException("UPAY-C-5A05");// 解析报文失败
		}
		return boss;
	}
	/**
	 * 单独解析报文体
	 * 
	 * @param xmlBody
	 * @return
	 * @throws ServiceException
	 */
	public static InterBOSS parseXmlBody(String xmlBody) throws ServiceException {
		logger.debug("报文体信息如下:");
		logger.debug(xmlBody);
		if (StringUtils.isBlank(xmlBody)) {
			throw new ServiceException("UPAY-C-0106");// OSN发现Message Body格式错误
		}
		InterBOSS boss = new InterBOSS();
		try {
			Document doc = DocumentHelper.parseText(xmlBody);
			Element root = doc.getRootElement();
			Dom4jXMLParser.settingElement(boss, root);
		} catch (DocumentException e) {
			logger.error("报文体解析失败,失败原因：" + e.getMessage());
			throw new ServiceException("UPAY-C-0106");// OSN发现Message Header
		}
		return boss;
	}

	/**
	 * 解析InterBOSS
	 * 
	 * @param boss
	 * @return xml字符串
	 */
	public static String parseInterBOSS(InterBOSS boss) {
		Element root = DocumentHelper.createElement(boss.getClass().getSimpleName());
		Document doc = DocumentHelper.createDocument(root);
		doc.setXMLEncoding(CoreConstant.MSG_ENCODING);
		try {
			Dom4jXMLParser.settingXML(root, boss);
		} catch (InstantiationException e) {
			logger.error("", e);
		} catch (IllegalAccessException e) {
			logger.error("", e);
		}
		String result = Dom4jXMLParser.formatXml(doc, CoreConstant.MSG_ENCODING, false);
		return result;
	}

	/**
	 * 循环设置bean的值
	 * 
	 * @param obj
	 *            设置的目标对象
	 * @param ele
	 *            xml中对应的元素
	 */
	@SuppressWarnings("unchecked")
	public static void settingElement(Object obj, Element element) {
		try {
			List<Element> rootChilds = element.elements();
			String propertyName = "";
			Field field = null;
			for (Element e : rootChilds) {
				propertyName = e.getName();
				if (e.isTextOnly()) {
					ReflectUtil.invokeSetter(obj, propertyName, e.getTextTrim());
				} else if (Dom4jXMLParser.isCDATA(e)) {
					ReflectUtil.invokeSetter(obj, propertyName, e.getTextTrim());
				} else {
					field = ReflectUtil.getAccessibleField(obj, propertyName);
					if (field != null) {
						Class<?> cls = field.getType();
						Object clsObj = cls.newInstance();
						CrmXMLParser.settingElement(clsObj, e);
						ReflectUtil.invokeSetter(obj, propertyName, clsObj);
					}
				}
			}
		} catch (InstantiationException e) {
			logger.error("", e);
		} catch (IllegalAccessException e) {
			logger.error("", e);
		}
	}

	/**
	 * 循环设置bean设置xml的值
	 * 
	 * @param element
	 *            设置值的xml
	 * @param obj
	 *            设置的目标对象
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static void settingXML(Element element, Object obj) throws InstantiationException, IllegalAccessException {

		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			Class<?> fieldCls = field.getType();
			Element ele = DocumentHelper.createElement(field.getName());
			Object objValue = ReflectUtil.invokeGetter(obj, field.getName());
			if (objValue == null) {
				continue;
			}
			if (fieldCls.equals(String.class)) {
				ele.setText(objValue.toString());
			} else {
				Dom4jXMLParser.settingXML(ele, objValue);
			}
			element.add(ele);
		}
	}

	/**
	 * 判断是否为CDATA节点
	 * 
	 * @param element
	 * @return
	 */
	private static boolean isCDATA(Node node) {
		if (!node.hasContent()) {
			return false;
		}
		Iterator<?> iterator = ((Branch) node).content().iterator();
		while (iterator.hasNext()) {
			Node e = (Node) iterator.next();
			if (Node.CDATA_SECTION_NODE == e.getNodeType()) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 判断报文值是否为空
	 * @param xmlString
	 * @return
	 * @throws DocumentException 
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public static boolean isNull(String xmlString) throws DocumentException{
		boolean result = true;
		if(StringUtils.isBlank(xmlString)){
			
		}else{
			  Document dom=DocumentHelper.parseText(xmlString);
			  Element root=dom.getRootElement();
			  
			  List<Element> elements = root.elements();
			  for(Element el : elements){
				  if(StringUtils.isNotBlank(el.getText())){
					  result = false;
					  break;
				  }				  
			  }			  
		}		
		return result;
	}
	
	
	/**
	 * 
	 * 解析密钥更新请求应答报文
	 * @throws ServiceException 
	 * 
	 */
	public static TUpayKey parseUpayKeyUpdateResp(String responseText) throws ServiceException {
		TUpayKey upayKey = new TUpayKey();
		Document doc = null;
		InterBOSS boss = parseXmlContent(responseText);
		upayKey.setObjId(UUIDGenerator.generateUUID());
		upayKey.setTransIDO(boss.getTransInfo().getTransIDO());
		upayKey.setTransIDOTime(boss.getTransInfo().getTransIDOTime());
		
		String svcContent = boss.getSvcCont();
		System.out.println("svcContent: "+svcContent);
		if(StringUtils.isNotBlank(svcContent)){			
			try {
				doc = DocumentHelper.parseText(svcContent);
				Element root = doc.getRootElement();
				Node newKeyNode = root.selectSingleNode("NewKey");
				if(newKeyNode != null){
					String newKey = newKeyNode.getText();
					upayKey.setNewKey(newKey);
					System.out.println("newKey: "+upayKey.getNewKey());
					logger.info("更新密钥，最新密钥为:"+upayKey.getNewKey());
				}else{
					logger.error("更新密钥为空！！");
				}
			} catch (DocumentException e) {
				logger.error("报文体解析失败,失败原因：" + e.getMessage());
			}
			
		}else{
			logger.error("报文体为空！！");
		}		
		return upayKey;
		
	}
}
