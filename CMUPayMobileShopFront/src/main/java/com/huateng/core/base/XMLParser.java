package com.huateng.core.base;

import java.io.IOException;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * XML解析器基类
 * 
 * @author Gary
 * 
 */
public class XMLParser {
	

	/**
	 * 格式化XML文档
	 * 
	 * @param document
	 *            xml文档
	 * @param charset
	 *            字符串的编码
	 * @param istrans
	 *            是否对属性和元素值进行转移
	 * @return 格式化后XML字符串
	 */
	public static String formatXml(Document document, String charset, boolean istrans) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setIndent(false);
		format.setNewlines(false);
		format.setEncoding(charset);
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw, format);
		xw.setEscapeText(istrans);
		try {
			xw.write(document);
			xw.flush();
			xw.close();
		} catch (IOException e) {
			System.out.println("格式化XML文档发生异常，请检查！");
			e.printStackTrace();
		}
		return sw.toString();
	}
	/**
	 * 格式化XML文档
	 * 
	 * @param document
	 *            xml文档
	 * @param charset
	 *            字符串的编码
	 * @param istrans
	 *            是否对属性和元素值进行转移
	 * @return 格式化后XML字符串
	 */
	public static String formatXml(Element ele, String charset, boolean istrans) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setIndent(false);
		format.setNewlines(false);
		format.setEncoding(charset);
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw, format);
		xw.setEscapeText(istrans);
		try {
			xw.write(ele);
			xw.flush();
			xw.close();
		} catch (IOException e) {
			System.out.println("格式化XML文档发生异常，请检查！");
			e.printStackTrace();
		}
		return sw.toString();
	}
}
