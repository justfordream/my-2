package com.huateng.crm.common.parse;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.base.XMLParser;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.util.ReflectUtil;
import com.huateng.crm.bean.CrmResponseMessage;
import com.huateng.crm.bean.message.head.BIPType;
import com.huateng.crm.bean.message.head.InterBOSS;
import com.huateng.crm.bean.message.head.Routing;
import com.huateng.crm.bean.message.head.RoutingInfo;
import com.huateng.crm.bean.message.head.TransInfo;
import com.huateng.crm.common.CRMConstant;

/**
 * Crm 报文解析器
 * 
 * @author Gary
 * 
 */
public class CrmXMLParser extends XMLParser {
	private final static Logger logger = LoggerFactory.getLogger(CrmXMLParser.class);

	/**
	 * 解析报文头部
	 * 
	 * @param xmlHead
	 *            字符串形式的报文头
	 * @return 返回组装好的报文头对象
	 * @throws DocumentException
	 */
	public static InterBOSS parseHeadXML(String xmlHead) throws DocumentException {
		InterBOSS boss = new InterBOSS();
		Document doc = DocumentHelper.parseText(xmlHead);
		Element root = doc.getRootElement();
		CrmXMLParser.settingElement(boss, root);
		return boss;
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
				CrmXMLParser.settingXML(ele, objValue);
			}
			element.add(ele);
		}
	}

	/**
	 * 解析对象为xml格式的字符串
	 * 
	 * @param boss
	 *            待转换的对象
	 * @return 返回转换后的xml格式字符串
	 */
	public static String parseInterBOSS(InterBOSS boss) {
		Element root = DocumentHelper.createElement(boss.getClass().getSimpleName());
		Document doc = DocumentHelper.createDocument(root);
		doc.setXMLEncoding(CoreConstant.MSG_ENCODING);
		try {
			CrmXMLParser.settingXML(root, boss);
		} catch (InstantiationException e) {
			logger.error("", e);
		} catch (IllegalAccessException e) {
			logger.error("", e);
		}
		String result = CrmXMLParser.formatXml(doc, CoreConstant.MSG_ENCODING, false);
		return result;
	}

	/**
	 * 解析报文体
	 * 
	 * @param xmlBody
	 *            xml格式的报文体
	 * @return 返回报文体纯净内容
	 */
	public static String parseBodyXml(String xmlBody) {
		try {
			Document doc = DocumentHelper.parseText(xmlBody);
			String result = CrmXMLParser.formatXml(doc.getRootElement(), CoreConstant.MSG_ENCODING, true);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return xmlBody;
		}
	}

	/**
	 * 解析报文体
	 * 
	 * @param xmlBody
	 *            xml格式的报文体
	 * @return 返回报文体纯净内容
	 */
	public static String parseEncryptXmlBody(String xmlBody) {
		try {
			Document doc = DocumentHelper.parseText(xmlBody);
			Element root = doc.getRootElement();
			Element svcCont = root.element(CRMConstant.SVC_CONT_ELEMENT_NAME);
			//Document newDoc = DocumentHelper.parseText(svcCont.getTextTrim());
			// String result = CrmXMLParser.formatXml(newDoc.getRootElement(),
			// CoreConstant.MSG_ENCODING, true);
			return svcCont.getTextTrim();
		} catch (Exception e) {
			logger.error(e.getMessage());
			xmlBody = xmlBody.replaceAll("<?xml version='1.0' encoding='UTF-8'?>", "")
					.replaceAll("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "")
					.replaceAll("<SvcCont><![CDATA[", "<SvcCont>").replaceAll("]]></SvcCont>", "</SvcCont>");
			return xmlBody;
		}
	}

	/**
	 * 转换XMLBody并添加CDATA表达式
	 * 
	 * @param xmlBody
	 *            未转换的xmlbody
	 * @return 转换好的xmlbody内容
	 */
	public static String createXmlBodyWithCDATA(String xmlBody) {
		Element root = DocumentHelper.createElement(CRMConstant.XML_ROOT_ELEMENT_NAME);
		Document doc = DocumentHelper.createDocument(root);
		doc.setXMLEncoding(CoreConstant.MSG_ENCODING);
		Element svcCont = DocumentHelper.createElement(CRMConstant.SVC_CONT_ELEMENT_NAME);
		CDATA data = DocumentHelper.createCDATA("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlBody);
		svcCont.add(data);
		root.add(svcCont);
		String result = CrmXMLParser.formatXml(doc, CoreConstant.MSG_ENCODING, true);
		return result;
	}

	/**
	 * 组装CrmResponseMessage信息
	 * 
	 * @param xmlContent
	 * @return
	 * @throws DocumentException
	 */
	public static CrmResponseMessage assumlyCrmResponseMessage(String xmlContent) throws DocumentException {
		CrmResponseMessage crmMsg = new CrmResponseMessage();
		InterBOSS boss = CrmXMLParser.parseHeadXML(xmlContent);
		String xmlBody = CrmXMLParser.createXmlBodyWithCDATA(boss.getSvcCont());
		crmMsg.setXmlbody(xmlBody);
		boss.setSvcCont(null);
		crmMsg.setXmlhead(CrmXMLParser.parseInterBOSS(boss));
		return crmMsg;
	}
	
	
	/**
	 * 
	 *  组装密钥更新请求对象 
	 *
	 */
	public static InterBOSS assemblyUpayKeyUpdateApplyObject(){
		InterBOSS boss = new InterBOSS();
		boss.setVersion("0100");
		boss.setTestFlag("1");
		
		BIPType bipType = new BIPType();
		bipType.setActionCode("0");
		bipType.setActivityCode("T0011004");
		bipType.setBIPCode("BIP0B001");
		boss.setBIPType(bipType);
		
		RoutingInfo routingInfo = new RoutingInfo();
		routingInfo.setOrigDomain("UPSS");
		routingInfo.setRouteType("00");
		Routing routing = new Routing();
		routing.setHomeDomain("MGMT");
		routing.setRouteValue("999");
		routingInfo.setRouting(routing);
		boss.setRoutingInfo(routingInfo);
		
		TransInfo transInfo = new TransInfo();
		transInfo.setSessionID("23749525");
		transInfo.setTransIDO(String.valueOf(Calendar.getInstance().getTimeInMillis()));
		transInfo.setTransIDOTime(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));
		boss.setTransInfo(transInfo);
		
		InterBOSS scvCont = new InterBOSS();
		boss.setSvcCont(Dom4jXMLParser.parseInterBOSS(scvCont));
		
		return boss;
	}
	

	public static void main(String[] args) throws DocumentException {
		String svcCont = parseEncryptXmlBody(bodyTestData());
		// InterBOSS boss =
		// CrmXMLParser.parseHeadXML(CrmXMLParser.testXmlData());
		// // boss.setSvcCont(svcCont);
		// System.out.println(boss.getSvcCont());
		// System.out.println(CrmXMLParser.createXmlBodyWithCDATA(boss.getSvcCont()));
		// CrmResponseMessage crmMsg =
		// CrmXMLParser.assumlyCrmResponseMessage(testXmlData());
		// System.out.println(crmMsg.getXmlhead());
		// System.out.println("---------------------------");
		// System.out.println(crmMsg.getXmlbody());
		// String svcCont = parseBodyXml(crmMsg.getXmlbody());
		System.out.println(svcCont);
		String str = testXmlData();
		System.out.println(str.replaceAll("<SvcCont>", "<SvcCont><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
				.replaceAll("</SvcCont>", "]]></SvcCont>"));
		System.out.println(parseHeadXML(CrmXMLParser.bodyTestData()).getSvcCont());
	}

	public static String testErrorBodyData() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<InterBOSS>" + "<SvcCont>"
				+ "<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<AuthReq>" + "</AuthReq>]]>" + "</SvcCont>"
				+ "</InterBOSS>";
		return result;
	}

	public static String testXmlData() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<InterBOSS>" + "	<Version>0100</Version>"
				+ "	<TestFlag>1</TestFlag>" + "	<BIPType>" + "		<BIPCode>BIP1A151</BIPCode>"
				+ "		<ActivityCode>T1000152</ActivityCode>" + "		<ActionCode>0</ActionCode>" + "	</BIPType>"
				+ "	<RoutingInfo>" + "		<OrigDomain>UPSS</OrigDomain>" + "		<RouteType>01</RouteType>" + "		<Routing>"
				+ "			<HomeDomain>BOSS</HomeDomain>" + "			<RouteValue>15710345828</RouteValue>" + "		</Routing>"
				+ "	</RoutingInfo>" + "	<TransInfo>" + "		<SessionID>441992716</SessionID>"
				+ "		<TransIDO>6119022071</TransIDO>" + "		<TransIDOTime>20130304100011</TransIDOTime>"
				+ "	</TransInfo>" + "	<SNReserve>" + "		<TransIDC>2000770320130304104803083000457</TransIDC>"
				+ "		<ConvID>42852a08-9d6e-479d-8262-b1bbafe81448</ConvID>" + "		<CutOffDay>20130304</CutOffDay>"
				+ "		<OSNTime>20130304100011</OSNTime>" + "		<OSNDUNS>3111</OSNDUNS>" + "		<HSNDUNS>3110</HSNDUNS>"
				+ "		<MsgSender>3111</MsgSender>" + "		<MsgReceiver>3110</MsgReceiver>" + "		<Priority>8</Priority>"
				+ "		<ServiceLevel>1</ServiceLevel>" + "	</SNReserve>" + "	<SvcCont>" + "		<![CDATA[" + "			<AuthReq>"
				+ "				<TransactionID>7620121219140011000019</TransactionID>"
				+ "				<ActionDate>20130304</ActionDate><IDType>01</IDType>" + "				<IDValue>15710345828</IDValue>"
				+ "				<UserIDType>00</UserIDType>" + "				<UserID>13068319870712463X</UserID>"
				+ "				<UserName>JACK</UserName>" + "				<BankAcctID>621226020011952562</BankAcctID>"
				+ "				<BankAcctType>0</BankAcctType>" + "			</AuthReq>]]>" + "	</SvcCont>" + "</InterBOSS>";
		return result;
	}

	public static String headTestData() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<InterBOSS>" + "	<Version>0100</Version>"
				+ "	<TestFlag>1</TestFlag>" + "	<BIPType>" + "		<BIPCode>BIP1A151</BIPCode>"
				+ "		<ActivityCode>T1000152</ActivityCode>" + "		<ActionCode>0</ActionCode>" + "	</BIPType>"
				+ "	<RoutingInfo>" + "		<OrigDomain>UPSS</OrigDomain>" + "		<RouteType>01</RouteType>" + "		<Routing>"
				+ "			<HomeDomain>BOSS</HomeDomain>" + "			<RouteValue>15710345828</RouteValue>" + "		</Routing>"
				+ "	</RoutingInfo>" + "	<TransInfo>" + "		<SessionID>441992716</SessionID>"
				+ "		<TransIDO>6119022071</TransIDO>" + "		<TransIDOTime>20130304100011</TransIDOTime>"
				+ "	</TransInfo>" + "	<SNReserve>" + "		<TransIDC>2000770320130304104803083000457</TransIDC>"
				+ "		<ConvID>42852a08-9d6e-479d-8262-b1bbafe81448</ConvID>" + "		<CutOffDay>20130304</CutOffDay>"
				+ "		<OSNTime>20130304100011</OSNTime>" + "		<OSNDUNS>3111</OSNDUNS>" + "		<HSNDUNS>3110</HSNDUNS>"
				+ "		<MsgSender>3111</MsgSender>" + "		<MsgReceiver>3110</MsgReceiver>" + "		<Priority>8</Priority>"
				+ "		<ServiceLevel>1</ServiceLevel>" + "	</SNReserve>" + "	<SvcCont></SvcCont>" + "</InterBOSS>";
		return result;
	}

	public static String bodyTestData() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<InterBOSS>" + "	<SvcCont>"
				+ "		<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "			<AuthReq>"
				+ "				<TransactionID>7620121219140011000019</TransactionID>"
				+ "				<ActionDate>20130304</ActionDate><IDType>01</IDType>" + "				<IDValue>15710345828</IDValue>"
				+ "				<UserIDType>00</UserIDType>" + "				<UserID>13068319870712463X</UserID>"
				+ "				<UserName>JACK</UserName>" + "				<BankAcctID>621226020011952562</BankAcctID>"
				+ "				<BankAcctType>0</BankAcctType>" + "			</AuthReq>]]>" + "	</SvcCont>" + "</InterBOSS>";
		return result;
	}

}
