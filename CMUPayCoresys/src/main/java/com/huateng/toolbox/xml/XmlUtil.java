package com.huateng.toolbox.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.dom4j.Element;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 * XML工具类
 * @author cmt
 *
 */
public class XmlUtil {
	
	//private static final MessageLogger logger = MessageLogger.getLogger(XmlUtil.class);
	
	/**
	 * java bean转换为XML字符串
	 * @param object 要转换的bean对象
	 * @param clazz 注解class类
	 * @param isAonn  是否带注解
	 * @return 转换成功的xml字符串
	 */
	public static String toXml(Object object, Class<?> clazz, boolean isAonn) {
		XStream xstream = new XStream(new DomDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.autodetectAnnotations(true);//去掉不用的属性
		if (isAonn) {
			xstream.processAnnotations(clazz);
		}
		String result = xstream.toXML(object);
		return result;
	}
	/**
	 * xml字符串转换为java bean对象
	 * @param xml 传入的xml字符串
	 * @return 转换成功的java bean对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml) {
		XStream xstream = new XStream();
		T result = (T) xstream.fromXML(xml);
		return result;
	}
	
	
	/*public static <T>T toBean(String xmlStr,Class<T> cls){
		XStream xstream=new XStream(new DomDriver());
		xstream.processAnnotations(cls);
		T obj=(T)xstream.fromXML(xmlStr);
		return obj;
	}*/
	
	
	/**
	 * xml字符串转换为java bean对象
	 * @param xml 传入的xml字符串
	 * @param mapAlias 别名集合
	 * @return 转换成功的java bean对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, Map<String, Class<?>> mapAlias) {
		XStream xstream = new XStream();
		Set<String> keys = mapAlias.keySet();
		for (String key : keys) {
			xstream.alias(key, mapAlias.get(key));
		}
		T result = (T) xstream.fromXML(xml);
		return result;
	}
	
	
	public static String domParser(String is) {
		long i0= System.currentTimeMillis();
		GetXmlFile  xmlFile = new GetXmlFile();
		xmlFile.setXmlString(is);
		Element eleResponseCode = (Element) xmlFile.GetElementByXpath("//MESSAGE/ActivityCode");
		String activityCode =eleResponseCode.getText().trim();
		long i1= System.currentTimeMillis();
		System.out.println(i1-i0);
		return activityCode;

	}

	public static String xmlParser(String is) {
		long i0= System.currentTimeMillis();
		InputStream inStream = new ByteArrayInputStream(is.getBytes());
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		String os = "";
		try {
			XMLStreamReader reader = xmlFactory
					.createXMLStreamReader(new InputStreamReader(inStream));

			while (reader.hasNext()) {

				int point = reader.next();
				if (point == XMLStreamConstants.START_ELEMENT) {
					if ("ActivityCode".equalsIgnoreCase(reader.getLocalName())) {
						os = reader.getElementText();
						break;
					}
				}

				if (point == XMLStreamConstants.END_DOCUMENT) {
					return "ActivityCode is null";

				}

			}

		} catch (XMLStreamException e) {
			//logger.error("解析xml失败", e);
			os = "解析xml失败:" + e.getMessage();
		} finally {
			try {
				inStream.close();
			} catch (IOException e) {
				 //logger.error("解析xml,关闭流失败", e);
				os = "解析xml,关闭流失败:" + e.getMessage();
			}
		}
		long i1= System.currentTimeMillis();
		System.out.println(i1-i0);
		return os;
	}

	@SuppressWarnings("unchecked")
	public static <T>T toBean(String xmlStr,Class<T> cls){
		XStream xstream=new XStream(new DomDriver());
		xstream.processAnnotations(cls);
		T obj=(T)xstream.fromXML(xmlStr);
		return obj;
	}
	
	public static void main(String[] args) {
		
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><MESSAGE><MCODE>101710</MCODE><ActivityCode>01274433494671</ActivityCode><DATE>20100521</DATE><TIME>171814</TIME><MERID>888001900010004</MERID><MOBILEID>15201347914</MOBILEID><SIGN>VjyY3hVh97DvAvOSUoIBmtBeUdtp8SmvQtCSwF/LhWYkTGPVW8eZHGBz68u/jqbgrKKaWs7s/HCe8229cZnuCD6kbI80aCnBe1lxKGDCSz6Whc6KU0pm5DKRcoq8nWMgtXDy3haRqQ5z3lgRyKHYAUcTFqUak0tIX9Z+AAiv9XQ=</SIGN></MESSAGE>";
		domParser(str);
		xmlParser(str);
		
	}
	

}
