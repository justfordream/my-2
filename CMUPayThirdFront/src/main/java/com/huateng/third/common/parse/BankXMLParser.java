package com.huateng.third.common.parse;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.base.XMLParser;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.ReflectUtil;
import com.huateng.third.bean.BankRequestMessage;
import com.huateng.third.bean.body.Body;
import com.huateng.third.bean.head.GPay;
import com.huateng.third.bean.head.Header;
import com.huateng.third.bean.head.Sign;

/**
 * 银行报文解析器
 * 
 * @author Gary
 * 
 */
public class BankXMLParser extends XMLParser {
	 private final static Logger logger = LoggerFactory.getLogger("BankXMLParser");
    

	/**
	 * 获得签名串
	 * 
	 * @param xmlContent
	 * @return
	 */
	public static String parseBankSignXml(String xmlContent) {
		StringBuffer sb = new StringBuffer();
		try {
			GPay gPay = BankXMLParser.parseXmlContent(xmlContent);
			String header = BankXMLParser.parseGPayHeader(gPay.getHeader());
			if (header != null) {
				sb.append(header);
			}
			String body = gPay.getBody();
			if (body != null) {
				sb.append("|");
				sb.append("<Body>" + body + "</Body>");
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("",e);
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
			GPay gPay = BankXMLParser.parseXmlContent(xmlContent);
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

			newXml = BankXMLParser.parseToXml(gPay);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("",e);
			newXml = null;
		}
		return newXml;
	}
	/**
	 * 单独解析报文体
	 * 
	 * @param xmlBody
	 * @return
	 * @throws Exception 
	 */
	public static Body parseXmlBody(String xmlBody) throws Exception {

		Body boss = new Body();
		try {
			Document doc = DocumentHelper.parseText(xmlBody);
			Element root = doc.getRootElement();
			BankXMLParser.settingElement(boss, root);
		} catch (DocumentException e) {
			//e.printStackTrace();
			logger.error("",e);
		}
		return boss;
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
			BankXMLParser.settingElement(gPay, root);
		} catch (IllegalArgumentException e) {
			//e.printStackTrace();
			logger.error("",e);
			throw new Exception(e);
		} catch (Throwable e) {
			//e.printStackTrace();
			logger.error("",e);
			throw new Exception(e);
		}
		return gPay;
	}

   /**
	* 判断是否需要加密
	* @param tanscode
	* @return
	*/
//	public static boolean isEncryptSwitch(String tanscode){
//		boolean isencrypt=false;
//		if(CoreConstant.SWITCH_OPEN.equals(CoreCommon.getEncryptSwitch())){
//			String encrptInfo=CoreCommon.getEncryptInfo();
//			String encrptInfos[]=encrptInfo.split(",");
//			for (String code : encrptInfos) {
//				if(StringUtils.equals(tanscode, code)){
//					isencrypt=true;
//				}
//			}
//		}
//		return isencrypt;
//	}
	
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
				BankXMLParser.settingElement(clsObj, e);
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
	public static String parseToXml(Object gPay) {
		Element root = DocumentHelper.createElement(gPay.getClass().getSimpleName());
		Document doc = DocumentHelper.createDocument(root);
		doc.setXMLEncoding(CoreConstant.MSG_ENCODING);
		try {
			BankXMLParser.settingXML(root, gPay);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("",e);

		}
		String result = BankXMLParser.formatXml(doc, CoreConstant.MSG_ENCODING, false);
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
		BankXMLParser.settingXML(root, header);
		String result = BankXMLParser.formatXml(doc.getRootElement(), CoreConstant.MSG_ENCODING, true);
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
				BankXMLParser.settingXML(ele, objValue);
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
	public static BankRequestMessage assumlyBankRequestMessage(String xmlContent) {
		BankRequestMessage bankMsg = new BankRequestMessage();
		bankMsg.setXmldata(xmlContent);
		return bankMsg;
	}

	public static String getSingure(GPay gPay) throws Exception {
		String header = BankXMLParser.parseGPayHeader(gPay.getHeader());
		String body = "<Body>" + gPay.getBody() + "</Body>";
		return header + "|" + body;
	}

	public static void main(String[] args) throws Exception {
		String demoText = testXmlData();
		demoText = demoText.replaceAll("<Body>", "<Body><![CDATA[").replaceAll("</Body>", "]]></Body>");
//		String srcText = ccData7();
//		System.out.println("源报文......" + srcText);
//		String gPay = new BankXMLParser().symPaseDncrypt(srcText);
		//System.out.println("转换后报文......" + gPay);
//		String signText = BankXMLParser.parseBankSignXml(srcText);
//		System.out.println("签名源串......" + signText);
//		System.out.println("签名串......" + gPay.getSign().getSignValue());
//		String st = BankXMLParser.convertBankXml(gPay.getSign().getSignValue(), "123", srcText);
//		System.out.println("添加签名串......" + st);
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
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>020001</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>00</ReqChannel><ReqDate>20130510</ReqDate><ReqTransID>101320130510110614000242279414</ReqTransID><ReqDateTime>20130510110614001</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0004</RcvSys></Header>" +
				"<Body><BankAcctID>6227001214720079044</BankAcctID>" +
				"<UserName>吴七一</UserName>" +
				"<UserIDType>00</UserIDType>" +
				"<UserID>310110196802016812</UserID>" +
				"</Body><Sign><SignFlag>1</SignFlag><CerID>ci</CerID><SignValue>sv</SignValue></Sign></GPay>";
		return result;
	}

	public static String ccbData5() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>020001</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>00</ReqChannel><ReqDate>20130510</ReqDate><ReqTransID>101320130510110614000242279414</ReqTransID><ReqDateTime>20130510110614001</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0004</RcvSys></Header><Body></Body><Sign><SignFlag>1</SignFlag><CerID>ci</CerID><SignValue>sv</SignValue></Sign></GPay>";
		return result;
	}

	public static String ccbData6() {
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay>" +
				"<Header><ActivityCode>020001</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>00</ReqChannel><ReqDate>20130510</ReqDate><ReqTransID>101320130510110614000242279414</ReqTransID><ReqDateTime>20130510110614001</ReqDateTime><ActionCode>0</ActionCode><RcvSys>0004</RcvSys></Header><Sign><SignFlag>1</SignFlag><CerID>ci</CerID><SignValue>sv</SignValue></Sign></GPay>";
		return result;
	}
	
	public static String ccData7(){
		String result="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<GPay>" +
	"<Header>" +
		"<ActivityCode>010006</ActivityCode>" +
		"<ReqSys>0009</ReqSys>" +
		"<ReqChannel>52</ReqChannel>" +
		"<ReqDate>20130913</ReqDate>" +
		"<ReqTransID>010006201309131630238492629393</ReqTransID>" +
		"<ReqDateTime>20130913163023850</ReqDateTime>" +
		"<ActionCode>0</ActionCode>" +
		"<RcvSys>0001</RcvSys>" +
		"<RcvDate></RcvDate>" +
		"<RcvTransID></RcvTransID>" +
		"<RcvDateTime></RcvDateTime>" +
		"<RspCode></RspCode>" +
		"<RspDesc></RspDesc>" +
	"</Header>" +
	"<Body>" +
		"<IDType>01</IDType>" +
		"<IDValue>13430544515</IDValue>" +
		"<UserName>张三 </UserName>" +
		"<UserIDType>00</UserIDType>" +
		"<UserID>310108198802270501</UserID>" +
	"</Body>" +
	"<Sign>" +
		"<SignFlag>0</SignFlag>" +
		"<CerID>101287345104328092378561023847</CerID>" +
		"<SignValue></SignValue>" +
	"</Sign>" +
"</GPay>";
		return result;
	}
}
