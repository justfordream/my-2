package com.huateng.log;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * XML工具类
 * 
 * @author cmt
 * 
 */
public class XmlUtil {

	/**
	 * java bean转换为XML字符串
	 * 
	 * @param object
	 *            要转换的bean对象
	 * @param clazz
	 *            注解class类
	 * @param isAonn
	 *            是否带注解
	 * @return 转换成功的xml字符串
	 */
	public static String toXml(Object object, Class<?> clazz, boolean isAonn) {
		XStream xstream = new XStream(new DomDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		if (isAonn) {
			xstream.processAnnotations(clazz);
		}
		String result = xstream.toXML(object);
		return result;
	}

	/**
	 * java bean转换为XML字符串
	 * 
	 * @param object
	 *            要转换的bean对象
	 * @param clazz
	 *            注解class类
	 * @param isAonn
	 *            是否带注解
	 * @return 转换成功的xml字符串
	 */
	public static void toXml(Object object, Class<?> clazz, boolean isAonn, PrintWriter pw) {
		XStream xstream = new XStream();
		xstream.setMode(XStream.NO_REFERENCES);
		if (isAonn) {
			xstream.processAnnotations(clazz);
		}
		xstream.toXML(object, pw);
	}

	/**
	 * xml字符串转换为java bean对象
	 * 
	 * @param xml
	 *            传入的xml字符串
	 * @return 转换成功的java bean对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml) {
		XStream xstream = new XStream();
		T result = (T) xstream.fromXML(xml);
		return result;
	}

	/**
	 * xml字符串转换为java bean对象
	 * 
	 * @param xml
	 *            传入的xml字符串
	 * @param mapAlias
	 *            别名集合
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

	public static void main(String[] args) {

	}

}
