package com.huateng.log;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * XML工具类
 * 
 * @author cmt
 * 
 */
public class XmlUtil {
	protected static String PREFIX_CDATA = "<![CDATA[";
	protected static String SUFFIX_CDATA = "]]>";

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
		XStream xstream = XmlUtil.initXStream(true);
		//xstream.setMode(XStream.NO_REFERENCES);
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
	public static <T> T fromXml(boolean isAddCDATA, String xml, Map<String, Class<?>> mapAlias) {
		XStream xstream = XmlUtil.initXStream(isAddCDATA);
		Set<String> keys = mapAlias.keySet();
		for (String key : keys) {
			xstream.alias(key, mapAlias.get(key));
		}
		T result = (T) xstream.fromXML(xml);
		return result;
	}

	public static XStream initXStream(boolean isAddCDATA) {
		XStream xstream = null;
		if (isAddCDATA) {
			xstream = new XStream(new XppDriver() {
				public HierarchicalStreamWriter createWriter(Writer out) {
					return new PrettyPrintWriter(out) {
						protected void writeText(QuickWriter writer, String text) {
							if (text.startsWith(PREFIX_CDATA) && text.endsWith(SUFFIX_CDATA)) {
								writer.write(text);
							} else {
								super.writeText(writer, text);
							}
						}
					};
				};
			});
		} else {
			xstream = new XStream();
		}
		return xstream;
	}

	public static void main(String[] args) {

	}

}
