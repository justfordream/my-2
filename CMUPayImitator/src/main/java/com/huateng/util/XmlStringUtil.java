package main.java.com.huateng.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * xml报文工具类
 * 
 * @author zeng.j
 * 
 */
public class XmlStringUtil {
	/**
	 * 替换xml中在某个字符串之后节点的值，只适合替换报文中只有一个指定名字的节点
	 * 
	 * @param nodeStart
	 *            节点开始标签 eg :<TransactionID>
	 * @param nodeEnd
	 *            节点结束标签 eg :</TransactionID>
	 * @param relacement
	 *            节点替换的内容
	 * @param src
	 *            原字符串
	 * @return
	 */
	public static String relaceNodeContentAfter(String nodeStart, String nodeEnd,String afterStart,
			String relacement, String src) {
		int nodeStartLength = nodeStart.length();
		int startAfter = src.indexOf(afterStart);
		int start = src.indexOf(nodeStart,startAfter);
		int end = src.indexOf(nodeEnd,startAfter);

		if(start>-1 && end>-1){
			String segStart = src.substring(0, start + nodeStartLength);
			String segEnd = src.substring(end, src.length());
			return segStart + relacement + segEnd;
		}
		return src;
	}
	/**
	 * 替换xml中节点的值，只适合替换报文中只有一个指定名字的节点
	 * 
	 * @param nodeStart
	 *            节点开始标签 eg :<TransactionID>
	 * @param nodeEnd
	 *            节点结束标签 eg :</TransactionID>
	 * @param relacement
	 *            节点替换的内容
	 * @param src
	 *            原字符串
	 * @return
	 */
	public static String relaceNodeContent(String nodeStart, String nodeEnd,
			String relacement, String src) {
		int nodeStartLength = nodeStart.length();
		int start = src.indexOf(nodeStart);
		int end = src.indexOf(nodeEnd);

		if(start>-1 && end>-1){
			String segStart = src.substring(0, start + nodeStartLength);
			String segEnd = src.substring(end, src.length());
			return segStart + relacement + segEnd;
		}
		return src;
	}
	/**
	 * 获取xml文本中节点的值
	 * 
	 * @param nodeStart
	 *            节点开始标签 eg :<TransactionID>
	 * @param nodeEnd
	 *            节点结束标签 eg :</TransactionID>
	 * @param src
	 *            原字符串
	 * @return
	 */
	public static String parseNodeValueFromXml(String nodeStart,
			String nodeEnd, String src) {
		int nodeStartLength = nodeStart.length();
		int start = src.indexOf(nodeStart);
		int end = src.indexOf(nodeEnd);
		if(start>-1&&end>-1){
			String nodeVal = src.substring(start + nodeStartLength, end);
			return nodeVal;
		}
		return "";
	}

	/**
	 * 获取xml中xpath节点的值
	 * 
	 * @param xml
	 * @param encoding
	 * @param xpath
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws DocumentException
	 */
	public static String getNodeValFromXml(String xml, String encoding,
			String xpath) throws UnsupportedEncodingException,
			DocumentException {
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new ByteArrayInputStream(xml
				.getBytes(encoding)));
		return document.selectSingleNode(xpath).getText();
	}

	/**
	 * 获取xml中xpath节点的值
	 * 
	 * @param in
	 * @param xpath
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws DocumentException
	 */
	public static String getNodeValFromStream(InputStream in, String xpath)
			throws UnsupportedEncodingException, DocumentException {
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(in);
		return document.selectSingleNode(xpath).getText();
	}
}
