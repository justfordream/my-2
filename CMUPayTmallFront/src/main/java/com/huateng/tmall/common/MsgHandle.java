package com.huateng.tmall.common;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.tmall.bean.CustomAnnotation;
import com.huateng.tmall.bean.ParentAnnotation;


/**
 * @author cmt
 * 
 */
public class MsgHandle {

	private static final Logger log = LoggerFactory.getLogger(MsgHandle.class);

	private static String toUpperCaseFirstChar(String str) {
		if (str == null || str.length() == 1)
			return str;
		else {
			String first = (str.substring(0, 1));
			char second = str.charAt(1);
			if (!Character.isUpperCase(second)) {
				first = first.toUpperCase();
			}
			String other = str.substring(1);
			return first + other;
		}

	}

	private static String nullToEmpty(Object obj) {

		return (obj == null || "null".equals(obj)) ? "" : obj.toString();
	}

	private static String getMethodWithGet(String fieldName) {
		return "get".concat(toUpperCaseFirstChar(fieldName));
	}

	private static String getMethodWithSet(String fieldName) {
		return "set".concat(toUpperCaseFirstChar(fieldName));

	}

	/**
	 * 
	 * @方法功能说明:根据请求对象的字段的annotation,生成xml.
	 * 
	 * @param msg
	 *            总对象
	 * @return
	 */
	@SuppressWarnings("all")
	public static String marshaller(Object msg) {
		log.debug("根据请求对象的字段的annotation,生成xml:{}",new Object[]{msg.getClass().getName()});
		Document doc = null;
		String retXml = "";
		try {
			log.debug("生成xml进行中...:{}",new Object[]{msg.getClass().getCanonicalName()});
			Field[] childFields = msg.getClass().getDeclaredFields();
			doc = bean2Xml(msg, childFields);
			retXml = getXmlByElement(doc);

			log.debug("生成xml已经成功完成:{}",new Object[]{msg.getClass().getCanonicalName()});
		} catch (Exception e) {
			log.error("生成xml字符串异常:对象在引用前,请首先实例化.", e);
			//throw new Exception("U99999", e.getMessage());
		}
		return retXml;

	}

	@SuppressWarnings("unchecked")
	private static Document bean2Xml(Object msg, Field[] childFields)
			throws Exception {

		List<String> elementList = new ArrayList<String>();

		return createElement(getElementAndValue(msg, childFields, null,
				elementList));

	}

	/**
	 * 获取要生成Xml的节点路径及对应值
	 * 
	 * @return
	 */

	@SuppressWarnings({ "all" })
	private static List getElementAndValue(Object msg, Field[] childFields,
			String parentNode, List<String> elementList) throws Exception {

		List<String> temp = new ArrayList<String>();

		for (Field field : childFields) {

			CustomAnnotation custom = (CustomAnnotation) field
					.getAnnotation(CustomAnnotation.class);
			if("SerialVersionUID".equalsIgnoreCase(field.getName())){
				continue;
			}
			String method = getMethodWithGet(field.getName());
			Object valueObj = msg.getClass().getMethod(method, null)
					.invoke(msg, null);
			
			if ('0' == (custom.power())) {
				continue;
			} else if ('1' == (custom.power())
					&& (null == valueObj || "".equals(valueObj))) {
				continue;
			} else {

			}
			String custPath = custom.path();
			ParentAnnotation parent = field
					.getAnnotation(ParentAnnotation.class);
			if (custom != null && parent != null) {

				if (!"".equals(valueObj) && valueObj != null) {
					temp = getElementAndValue(valueObj, valueObj.getClass()
							.getDeclaredFields(), custPath, temp);
					if ('2' == (custom.power())
							&& (temp == null || temp.isEmpty())) {
						String lastPath = null;
						if (parentNode == null) {
							lastPath = custPath;
						} else {
							lastPath = parentNode
									+ custPath.substring(custPath.indexOf("."));
						}
						elementList.add(lastPath + "|" + "");
					} else {

						for (String str : temp) {
							elementList.add(str);
						}

					}
				} else {
					String lastPath = null;

					if (parentNode == null) {
						lastPath = custPath;
					} else {
						lastPath = parentNode
								+ custPath.substring(custPath.indexOf("."));
					}
					elementList.add(lastPath + "|" + "");
				}

			} else if (custom != null && !field.getType().isArray()) {

				String lastPath = null;
				if (parentNode == null) {
					lastPath = custPath;
				} else {
					lastPath = parentNode
							+ custPath.substring(custPath.indexOf("."));
				}
				elementList.add(lastPath + "|" + valueObj);

			}
		}

		return elementList;
	}

	/**
	 * 
	 * @方法功能说明:解析xml,转化到响应对象msg的属性中.
	 * 
	 * @param msg
	 *            BODY对象
	 * @param xmlDoc
	 *            BODY体字符串
	 */

	public static void unmarshaller(Object msg, String xmlDoc) {
		try {
			log.debug("解析xml,转化到响应对象msg的属性中:{}",new Object[]{msg.getClass().getName()});
			if (xmlDoc == null || "null".equalsIgnoreCase(xmlDoc)
					|| "".equals(xmlDoc.trim())) {
				return;
			}
			// 加载xml,生成Document
			xmlDoc = xmlDoc.trim();
			Field[] childFields = msg.getClass().getDeclaredFields();
			xml2Bean(xmlDoc, msg, childFields);

			//log.info("解析xml数据报文成功,保存到:" + msg.getClass().getCanonicalName());
		} catch (Exception e) {
			log.error("类名:{},解析xml数据报文失败:对象在引用前,请首先实例化:{}",new Object[]{msg.getClass().getName(),e});
			//throw new AppRTException("U99999", e.getMessage());
		}
	}

	private static void xml2Bean(String xmlDoc, Object msg, Field[] childFields)
			throws Exception {

		Document doc = DocumentHelper.parseText(xmlDoc);
		if (xmlDoc.contains("<![" + ExcConstant.CDATA + "[")
				&& ExcConstant.SVC_CONT_ELEMENT_NAME.equals(doc
						.getRootElement().getName())) {
			Element root = doc.getRootElement();
			Document rootCDATA = DocumentHelper.parseText(root.getTextTrim());
			String cdataText = rootCDATA.getRootElement().asXML();
			xmlDoc = "<" + ExcConstant.SVC_CONT_ELEMENT_NAME + ">" + cdataText
					+ "</" + ExcConstant.SVC_CONT_ELEMENT_NAME + ">";
			doc = DocumentHelper.parseText(xmlDoc);
		}
		for (Field field : childFields) {
			CustomAnnotation custom = (CustomAnnotation) field
					.getAnnotation(CustomAnnotation.class);
			ParentAnnotation parent = field
					.getAnnotation(ParentAnnotation.class);
			if (custom != null && parent != null) {
				String custPath = custom.path();
				String node = custPath.substring(custPath.lastIndexOf(".") + 1);
				if (xmlDoc.contains("</" + node + ">")) {
					String bodyXml = xmlDoc.substring(
							xmlDoc.indexOf("<" + node + ">"),
							xmlDoc.indexOf("</" + node + ">"))
							+ "</" + node + ">";
					Method setMethod = msg.getClass().getMethod(
							getMethodWithSet(field.getName()), field.getType());
					setMethod.invoke(msg, bodyXml);
				}
			} else if (custom != null && !field.getType().isArray()) {
				String selectPath = "/" + custom.path().replaceAll("\\.", "/");
				Node node = doc.selectSingleNode(selectPath);
				if (node == null) {
					log.debug("未匹配到指定的节点:" + selectPath);
				} else {
					Method setMethod = msg.getClass().getMethod(
							getMethodWithSet(field.getName()), field.getType());
					if (StringUtils.isNotBlank(node.getStringValue())) {
						if (field.getType().equals(Long.class)) {
							setMethod.invoke(msg,
									Long.valueOf(node.getStringValue()));
						} else if (field.getType().equals(Integer.class)) {
							setMethod.invoke(msg,
									Integer.valueOf(node.getStringValue()));
						} else if (field.getType().equals(String.class)) {
							setMethod.invoke(msg, node.getStringValue());
						} else {
							setMethod.invoke(msg,
									String.valueOf(node.getStringValue()));
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @方法功能说明:根据path创建未创建的元素,返回路径尾部元素
	 * @param AbstractReqMsg
	 *            ,String
	 * @return void
	 */
	private static Document createElement(List<String> list) {
		if (list == null || list.isEmpty()) {
			Document document = DocumentHelper.createDocument();
			document.addElement("");
			return document;
		}
		Document document = null;
		Map<String, Element> map = new Hashtable<String, Element>();
		for (String element : list) {
			String value = "";
			String path = element.split("\\|")[0];
			if (element.split("\\|").length > 1) {
				value = nullToEmpty(element.split("\\|")[1].trim());
			}

			String[] paths = path.split("\\.");
			String[] keys = new String[paths.length];
			Element parent = null;
			Element child = null;
			for (int i = 0; i < paths.length; i++) {
				if (i == 0) {
					keys[i] = paths[i];
				} else {
					keys[i] = keys[i - 1] + paths[i];
				}
			}
			for (int i = 0; i < keys.length; i++) {
				if (map.containsKey(keys[i])) {
					if (i != keys.length - 1) {
						parent = (Element) map.get(keys[i]);
					} else {
						child = (Element) map.get(keys[i]);
					}
				} else {
					if (i == 0) {// i=0,根元素
						document = DocumentHelper.createDocument();
						child = document.addElement(paths[i]);
					} else {

						child = parent.addElement(paths[i]);// 创建子元素
					}
					if (i != keys.length - 1)
						parent = child;// 作为下个元素的父元素
					map.put(keys[i], child);// 创建的元素保存到map

				}
			}

			child.setText(value);
		}
		return document;
	}

	private static String getXmlByElement(Document document) {
		StringWriter writer = new StringWriter();
		try {
			OutputFormat format = OutputFormat.createCompactFormat();
			format.setEncoding("UTF-8");
			XMLWriter output = new XMLWriter(writer, format);
			output.write(document);
			output.close();
			writer.close();
		} catch (Exception e) {
			//throw new AppRTException("U99999", e.getMessage());
		}
		String resultStr = writer.toString();
		if (document != null)
			document.clearContent();
		return resultStr;
	}

	public static void main(String[] args) throws Exception {

		// String xmlDoc="<?xml version='1.0' encoding='UTF-8'?>"+
		// "<InterBOSS><SvcCont>"+
		// "<![CDATA[<?xml version='1.0' encoding='UTF-8'?>"+
		// "<PaymentReq><TransactionID>7620121219140011000019</TransactionID>"+
		// "<ActionDate>20130304</ActionDate><IDType>01</IDType>"+
		// "<IDValue>13611860082</IDValue>"+
		// "<Payed>80</Payed>"+
		// "<CnlTyp>01</CnlTyp>"+
		// "<SubID>1234567890123456789012</SubID>"+
		// "<PayedType>01</PayedType>"+
		// "</PaymentReq>]]>"+
		// "</SvcCont>"+
		// "</InterBOSS>";
		// Document doc = DocumentHelper.parseText(xmlDoc);
		// Element root = doc.getRootElement();
		// Element SvcCont =root.element("SvcCont");
		// root.asXML();
		// root.remove(SvcCont);
		// root.addElement("SvcCont");
		// Element SvcCont1 = root.element("SvcCont");
		// SvcCont1.addCDATA("<SubID>1234567890123456789012</SubID>");
		// System.out.print(root.asXML());

		// MsgHandle.unmarshaller(uu, msg);
		//
		// Long l2 = System.currentTimeMillis();
		// System.out.println("报文头解析耗时:" + (l2 - l1));
		// System.out.println(uu.getBody());
		// CrmCheckMsgReqVo ress = new CrmCheckMsgReqVo();
		// System.out.println(uu.getActivityCode());
		// Long l3 = System.currentTimeMillis();
		// MsgHandle.unmarshaller(ress, uu.getBody().toString());
		// Long l4 = System.currentTimeMillis();
		// System.out.println("报文体解析耗时:" + (l4 - l3));
		// System.out.println(ress.getUserName());
		// SubBindCheckResVo vo = new SubBindCheckResVo();

		// String xmlDoc="<SvcCont>"+
		// "<![CDATA[<?xml version='1.0' encoding='UTF-8'?><SubCheckRsp>  <RspCode>4A01</RspCode>  <RspInfo>99999</RspInfo></SubCheckRsp>"
		// + "]]>"+ "</SvcCont>";

		// String xmlDoc = "<SvcCont>"
		// + "<![CDATA[<?xml version='1.0' encoding='UTF-8'?>"
		// + "<PaymentRsp>" + "<IDType>01</IDType>"
		// + "<IDValue>13560017733</IDValue>"
		// + "<TransactionID>1042201304111131532001</TransactionID>"
		// + "<ActionDate>20130411</ActionDate>" + "<UserCat>1</UserCat>"
		// + "<RspCode>0000</RspCode>" + "<RspInfo>success</RspInfo>"
		// + "</PaymentRsp>" + "]]>" + "</SvcCont>";
		// unmarshaller(vo, xmlDoc);

		//
		/*BankUnbindResVo res = new BankUnbindResVo();
		res.setBankID("");
		CopyOfBankMsgVo vo = new CopyOfBankMsgVo();
		vo.setBody(res);
		vo.setActionCode("0");

		System.out.print(marshaller(vo));*/
	}

}
