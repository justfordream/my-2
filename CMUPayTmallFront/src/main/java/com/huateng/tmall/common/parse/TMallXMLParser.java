package com.huateng.tmall.common.parse;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.tmall.bean.TMallRequestMessage;
import com.huateng.tmall.bean.head.GPay;
import com.huateng.tmall.bean.head.Header;
import com.huateng.tmall.bean.head.Sign;
import com.huateng.core.base.XMLParser;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.ReflectUtil;

/**
 * 银行报文解析器
 * 
 * @author Gary
 * 
 */
public class TMallXMLParser extends XMLParser {
	 private final static Logger logger =LoggerFactory.getLogger("TMallXMLParser");

	/**
	 * 获得签名串
	 * 
	 * @param xmlContent
	 * @return
	 */
	public static String parseBankSignXml(String xmlContent) {
		StringBuffer sb = new StringBuffer();
		try {
			GPay gPay = TMallXMLParser.parseXmlContent(xmlContent);
			String header = TMallXMLParser.parseGPayHeader(gPay.getHeader());
			if (header != null) {
				sb.append(header);
			}
			String body = gPay.getBody();
			if (body != null) {
				sb.append("|");
				sb.append("<Body>" + body + "</Body>");
			}
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

		return (sb.length() == 0 ? null : sb.toString());
	}

	/**
	 * 加入签名串的报文
	 * 
	 * @param signText
	 * @param cerId
	 * @param xmlContent
	 * @return
	 */
	public static String convertBankXml(String signText, String cerId, String xmlContent) {
		String newXml = null;
		try {
			// xmlContent = xmlContent.replaceAll("<Body>",
			// "<Body><![CDATA[").replaceAll("</Body>", "]]></Body>");
			GPay gPay = TMallXMLParser.parseXmlContent(xmlContent);
			Sign sign = gPay.getSign();
			if (sign == null) {
				sign = new Sign();
			}
			if (StringUtils.isBlank(signText)) {
				sign.setSignFlag("0");
				sign.setCerID(cerId);
				sign.setSignValue("");
			} else {
				sign.setSignFlag("1");
				sign.setCerID(cerId);
				sign.setSignValue(signText);
			}
			gPay.setSign(sign);

			newXml = TMallXMLParser.parseGPay(gPay);
		} catch (Exception e) {
			logger.error("", e);
			newXml = null;
		}
		return newXml;
	}

	/**
	 * 解析报文
	 * 
	 * @param xmlContent
	 * @return
	 * @throws ServiceException
	 * @throws DocumentException
	 */
	public static GPay parseXmlContent(String xmlContent) throws Exception {
		GPay gPay = new GPay();
		try {
			xmlContent = xmlContent.replaceAll("<Body>", "<Body><![CDATA[").replaceAll("</Body>", "]]></Body>");
			Document doc = DocumentHelper.parseText(xmlContent);
			Element root = doc.getRootElement();
			TMallXMLParser.settingElement(gPay, root);
		} catch (IllegalArgumentException e) {
			logger.error("", e);
			throw new Exception(e);
		} catch (Throwable e) {
			logger.error("", e);
			throw new Exception(e);
		}
		return gPay;
	}

	/**
	 * 循环设置bean的值
	 * 
	 * @param obj
	 *            设置的目标对象
	 * @param ele
	 *            xml中对应的元素
	 * @throws Exception
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	public static void settingElement(Object obj, Element element) throws Exception {
		List<Element> rootChilds = element.elements();
		String propertyName = "";
		Field field = null;
		for (Element e : rootChilds) {
			propertyName = e.getName();
			field = ReflectUtil.getAccessibleField(obj, propertyName);
			if (field == null) {
				continue;
			}
			if (e.isTextOnly()) {
				ReflectUtil.invokeSetter(obj, propertyName, e.getTextTrim());
			} else {
				Class<?> cls = field.getType();
				Object clsObj = cls.newInstance();
				TMallXMLParser.settingElement(clsObj, e);
				ReflectUtil.invokeSetter(obj, propertyName, clsObj);
			}
		}
	}

	/**
	 * 解析对象为xml格式的字符串
	 * 
	 * @param boss
	 *            待转换的对象
	 * @return 返回转换后的xml格式字符串
	 * @throws Exception
	 * @throws InstantiationException
	 */
	public static String parseGPay(GPay gPay) {
		Element root = DocumentHelper.createElement(gPay.getClass().getSimpleName());
		Document doc = DocumentHelper.createDocument(root);
		doc.setXMLEncoding(CoreConstant.MSG_ENCODING);
		try {
			TMallXMLParser.settingXML(root, gPay);
		} catch (Exception e) {
			logger.error("", e);

		}
		String result = TMallXMLParser.formatXml(doc, CoreConstant.MSG_ENCODING, false);
		return result;
	}

	/**
	 * 解析对象为xml格式的字符串
	 * 
	 * @param boss
	 *            待转换的对象
	 * @return 返回转换后的xml格式字符串
	 * @throws Exception
	 * @throws InstantiationException
	 */
	public static String parseGPayHeader(Header header) {
		Element root = DocumentHelper.createElement(header.getClass().getSimpleName());
		Document doc = DocumentHelper.createDocument(root);
		doc.setXMLEncoding(CoreConstant.MSG_ENCODING);
		TMallXMLParser.settingXML(root, header);
		String result = TMallXMLParser.formatXml(doc.getRootElement(), CoreConstant.MSG_ENCODING, true);
		return result;
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
	public static void settingXML(Element element, Object obj) {

		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			Class<?> fieldCls = field.getType();
			Element ele = DocumentHelper.createElement(field.getName());
			Object objValue = ReflectUtil.invokeGetter(obj, field.getName());
			if (objValue == null) {
				continue;
				// objValue = fieldCls.newInstance();
			}
			if (fieldCls.equals(String.class)) {
				ele.setText(objValue.toString());
			} else {
				TMallXMLParser.settingXML(ele, objValue);
			}
			element.add(ele);
		}
	}

	/**
	 * 组装BankRequestMessage信息
	 * 
	 * @param xmlContent
	 * @return
	 */
	public static TMallRequestMessage assumlyTMallRequestMessage(String xmlContent) {
		TMallRequestMessage tmallMsg = new TMallRequestMessage();
		tmallMsg.setXmldata(xmlContent);
		return tmallMsg;
	}

	public static String getSingure(GPay gPay) throws Exception {
		String header = TMallXMLParser.parseGPayHeader(gPay.getHeader());
		String body = "<Body>" + gPay.getBody() + "</Body>";
		return header + "|" + body;
	}

	public static void main(String[] args) throws Exception {
		String demoText = testXmlData();
		demoText = demoText.replaceAll("<Body>", "<Body><![CDATA[").replaceAll("</Body>", "]]></Body>");
		// System.out.println(demoText);
		// GPay gPay = BankXMLParser.parseXmlContent(demoText);
		// System.out.println("Body......"+Base64Util.encode(gPay.getBody()+"|"+gPay.getHeader().plainText()));
		// System.out.println("Header......"+gPay.getHeader().plainText());
		// String s =
		// Base64Util.encode(gPay.getBody()+"|"+gPay.getHeader().plainText());
		// System.out.println("encode......"+s);
		// System.out.println("encode......"+Base64Util.decode(s));
		// System.out.println(BankXMLParser.parseGPayHeader(gPay.getHeader()));
		// System.out.println("<Body>"+gPay.getBody().replaceAll(" ",
		// "")+"</Body>");
		// System.out.println(BankXMLParser.parseGPay(gPay));
		// System.out.println(BankXMLParser.parseGPayHeader(gPay.getHeader()));
		// System.out.println(gPay.getBody());
		// System.out.println(BankXMLParser.getSingure(gPay));
		// System.out.println(BankXMLParser.convertBankXml(null, "123",
		// testXmlData()));
		String srcText = ccbData4();
		System.out.println("源报文......" + srcText);
		GPay gPay = TMallXMLParser.parseXmlContent(srcText);
		System.out.println("转换后报文......" + TMallXMLParser.parseGPay(gPay));
		String signText = TMallXMLParser.parseBankSignXml(srcText);
		System.out.println("签名源串......" + signText);
		System.out.println("签名串......" + gPay.getSign().getSignValue());
		String st = TMallXMLParser.convertBankXml(gPay.getSign().getSignValue(), "123", srcText);
		System.out.println("添加签名串......" + st);
		// String singure = BankXMLParser.parseBankSignXml(ccbData());
		// System.out.println(singure);
		// String signText = BankXMLParser.convertBankXml("123", "123",
		// ccbData());
		// System.out.println(signText);
		// GPay gPay = BankXMLParser.parseXmlContent(ccbData());
		// System.out.println(BankXMLParser.parseGPay(gPay));
	}

	public static String testXmlData() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<GPay>" + "	<Header>"
				+ "		<ActivityCode>ac</ActivityCode>" + "		<ReqSys>rs</ReqSys>" + "		<ReqChannel>rc</ReqChannel>"
				+ "		<ReqDate>rd</ReqDate>" + "		<ReqTransID>rti</ReqTransID>" + "		<ReqDateTime>rdi</ReqDateTime>"
				+ "		<ActionCode>ac</ActionCode>" + "		<RcvSys>rs</RcvSys>" + "		<RcvDate>rd</RcvDate>"
				+ "		<RcvTransID>rti</RcvTransID>" + "		<RcvDateTime>rdt</RcvDateTime>" + "		<RspCode>rc</RspCode>"
				+ "		<RspDesc>rd</RspDesc>" + "	</Header>" + "	<Body>" + "		<UserIDType>uit</UserIDType>"
				+ "		<UserID>ui</UserID>" + "		<Amount>a</Amount>" + "	</Body>" + "	<Sign>"
				+ "		<SignFlag>sf</SignFlag>" + "		<CerID>ci</CerID>" + "		<SignValue>sv</SignValue>" + "	</Sign>"

				+ "</GPay>";
		return result;
	}

	public static String ccbData1() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>020001</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>00</ReqChannel><ReqDate>20130510</ReqDate><ReqTransID>101320130510110614000242279414</ReqTransID><ReqDateTime>20130510110614001</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0004</RcvSys></Header><Body><BankAcctID>6227001214720079044</BankAcctID><UserName>吴七一</UserName><UserIDType>00</UserIDType><UserID>310110196802016812</UserID></Body></GPay>";
		return result;
	}

	public static String ccbData2() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>020001</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>00</ReqChannel><ReqDate>20130510</ReqDate><ReqTransID>101320130510110614000242279414</ReqTransID><ReqDateTime>20130510110614001</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0004</RcvSys></Header><Body></Body></GPay>";
		return result;
	}

	public static String ccbData3() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>020001</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>00</ReqChannel><ReqDate>20130510</ReqDate><ReqTransID>101320130510110614000242279414</ReqTransID><ReqDateTime>20130510110614001</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0004</RcvSys></Header></GPay>";
		return result;
	}

	public static String ccbData4() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>020001</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>00</ReqChannel><ReqDate>20130510</ReqDate><ReqTransID>101320130510110614000242279414</ReqTransID><ReqDateTime>20130510110614001</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0004</RcvSys></Header><Body><BankAcctID>6227001214720079044</BankAcctID><UserName>吴七一</UserName><UserIDType>00</UserIDType><UserID>310110196802016812</UserID></Body><Sign><SignFlag>1</SignFlag><CerID>ci</CerID><SignValue>sv</SignValue></Sign></GPay>";
		return result;
	}

	public static String ccbData5() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>020001</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>00</ReqChannel><ReqDate>20130510</ReqDate><ReqTransID>101320130510110614000242279414</ReqTransID><ReqDateTime>20130510110614001</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0004</RcvSys></Header><Body></Body><Sign><SignFlag>1</SignFlag><CerID>ci</CerID><SignValue>sv</SignValue></Sign></GPay>";
		return result;
	}

	public static String ccbData6() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>020001</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>00</ReqChannel><ReqDate>20130510</ReqDate><ReqTransID>101320130510110614000242279414</ReqTransID><ReqDateTime>20130510110614001</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0004</RcvSys></Header><Sign><SignFlag>1</SignFlag><CerID>ci</CerID><SignValue>sv</SignValue></Sign></GPay>";
		return result;
	}
}
