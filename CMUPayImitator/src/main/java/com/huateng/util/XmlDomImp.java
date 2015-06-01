package main.java.com.huateng.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlDomImp implements XmlDom {

	public void createXmlDocment(String rootName, String path) {
		Element root = new Element(rootName);
		Document doc = new Document(root);
		Format mat = Format.getCompactFormat();
		mat.setIndent("  ");
		XMLOutputter outPut = new XMLOutputter(mat);
		try {
			outPut.output(doc, new FileOutputStream(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isNodeExsitByNameByNdVal(String path, String nodename,
			String atrbtVal, String nodeVal) {
		boolean stat = false;
		try {
			SAXBuilder builer = new SAXBuilder();
			Document doc = builer.build(new File(path));
			Element root = doc.getRootElement();
			List nodeList = root.getChildren();
			Iterator it = nodeList.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				if (el.getName().equals(nodename)
						& el.getAttribute("name").getValue().equals(atrbtVal)
						& el.getText().equals(nodeVal)) {
					stat = true;
				} else {
					stat = false;
				}
			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stat;
	}

	public boolean isNodeExsitByIdByNdVal(String path, String nodename,
			String atrbtVal, String nodeVal) {
		boolean stat = false;
		try {
			SAXBuilder builer = new SAXBuilder();
			Document doc = builer.build(new File(path));
			Element root = doc.getRootElement();
			List nodeList = root.getChildren();
			Iterator it = nodeList.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				if (el.getName().equals(nodename)
						& el.getAttribute("id").getValue().equals(atrbtVal)
						& el.getText().equals(nodeVal)) {
					stat = true;
				} else {
					stat = false;
				}
				// System.out.println(el.getAttribute("name").getValue());

			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stat;
	}

	public boolean isNodeExsitByNameBysonNdVal(String path, String nodename,
			String atrbtVal, String sonNodeName, String sonAtrbtVal,
			String sonNdVal) {
		boolean stat = false;
		try {
			SAXBuilder builer = new SAXBuilder();
			Document doc = builer.build(new File(path));
			Element root = doc.getRootElement();
			List nodeList = root.getChildren();
			Iterator it = nodeList.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				List sonList = el.getChildren();
				Iterator its = sonList.iterator();
				while (its.hasNext()) {
					Element sonel = (Element) its.next();
					if (el.getName().equals(nodename)
							&& el.getAttribute("name").getValue().trim()
									.equals(atrbtVal)
							&& sonel.getName().equals(sonNodeName)
							&& sonel.getAttribute("name").getValue().trim()
									.equals(sonAtrbtVal)
							&& sonel.getText().trim().equals(sonNdVal)) {
						stat = true;
					} else {
						stat = false;
					}
				}
			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stat;
	}

	public boolean isNodeExsitByIdBysonNdVal(String path, String nodename,
			String atrbtVal, String sonNodeName, String sonAtrbtVal,
			String sonNdVal) {
		boolean stat = false;
		try {
			SAXBuilder builer = new SAXBuilder();
			Document doc = builer.build(new File(path));
			Element root = doc.getRootElement();
			List nodeList = root.getChildren();
			Iterator it = nodeList.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				List sonList = el.getChildren();
				Iterator its = sonList.iterator();
				while (its.hasNext()) {
					Element sonel = (Element) its.next();
					if (el.getName().equals(nodename)
							&& el.getAttribute("id").getValue().trim()
									.equals(atrbtVal)
							&& sonel.getName().equals(sonNodeName)
							&& sonel.getAttribute("name").getValue().trim()
									.equals(sonAtrbtVal)
							&& sonel.getText().trim().equals(sonNdVal)) {
						stat = true;
					} else {
						stat = false;
					}
				}
			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stat;
	}

	public void addNode(String path, String ndName, String sonNdNmae,
			String grandsonndName, String ndatrbtName, String ndatrbtVale,
			String ndText, String sonatrbtName, String sonatrbtVale,
			String sonndText, String grandsnatrbtName, String grandsnatrbtVale,
			String grandsnndText) {
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			Element node = new Element(ndName);
			node.setAttribute(ndatrbtName, ndatrbtVale);
			node.setText(ndText);
			Element sonnode = new Element(sonNdNmae);
			sonnode.setAttribute(sonatrbtName, sonatrbtVale);
			sonnode.setText(sonndText);
			Element grandsnnode = new Element(grandsonndName);
			grandsnnode.setAttribute(grandsnatrbtName, grandsnatrbtVale);
			grandsnnode.setText(grandsnndText);
			node.addContent(sonnode);
			sonnode.addContent(grandsnnode);
			root.addContent(node);
			Format mat = Format.getCompactFormat();
			mat.setIndent(" ");
			XMLOutputter outPut = new XMLOutputter(mat);
			outPut.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addNode(String path, String ndName, String sonNdName1,
			String sonNdName2, String sonNdName3, String sonNdName4,
			String ndNameVal, String sonndText1, String sonndText2,
			String sonndText3, String sonndText4) {
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			Element node = new Element(ndName);
//			System.out.println(ndNameVal);
			node.setAttribute("name", ndNameVal);
			Element sonnode1 = new Element(sonNdName1);
			sonnode1.setText(sonndText1);
			node.addContent(sonnode1);
			Element sonnode2 = new Element(sonNdName2);
			sonnode2.setText(sonndText2);
			node.addContent(sonnode2);
			Element sonnode3 = new Element(sonNdName3);
			sonnode3.setText(sonndText3);
			node.addContent(sonnode3);
			Element sonnode4 = new Element(sonNdName4);
			sonnode4.setText(sonndText4);
			node.addContent(sonnode4);
			root.addContent(node);
			Format mat = Format.getCompactFormat();
			mat.setIndent(" ");
			mat.setEncoding("GB2312");
			XMLOutputter outPut = new XMLOutputter(mat);
			outPut.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addNode(String path, String nodeName, String atrbtName,
			String atrbtVale, String nodeText) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			Element element = new Element(nodeName);
			element.setAttribute(atrbtName, atrbtVale);
			element.addContent(nodeText);
			Element el = new Element(nodeName);
			root.addContent(element);
			Format mat = Format.getCompactFormat();
			mat.setIndent("  ");
			XMLOutputter outPut = new XMLOutputter(mat);
			outPut.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String argsp[]) {
//		XmlDomImp dom = new XmlDomImp();
//		 boolean stat
//		 =dom.isNodeExsitByNameByNdVal("D:/wuliu/ycwl/WebRoot/xml/report.xml",
//		 "Rptname","00002", "card.jrxml");
//		 System.out.println("stat=="+stat);
//		 boolean snstat =
//		 dom.isNodeExsitByNameBysonNdVal("D:/wuliu/ycwl/WebRoot/xml/billdeal.xml",
//		 "confirmflag" , "12", "task", "12", "cardmn/cardmng.jsp");
//		 System.out.println(snstat);
//		 dom.("D:/wuliu/ycwl/WebRoot/xml/billdeal.xml", "confirmflag","12",
//		 "cardmng/cardmng.jsp");
//		 dom.removeNodeByNameByVal("D:/wuliu/ycwl/WebRoot/xml/report.xml",
//		 "Rptname", "00002", "card.jrxml");
//		 dom.addNode("","","","");
//		 dom.createXmlDocment();
//		 dom.addNode("","book","name","00003","hello");
//		 dom.removeNode("D:/wuliu/ycwl/WebRoot/xml/report.xml","Rptname","ggg.jrxml");
//		 dom.removeNode("D:/wuliu/ycwl/WebRoot/xml/report.xml", "stat");
//		 dom.removeNodeById("D:/wuliu/ycwl/WebRoot/xml/report.xml", "stat",
//		 "001");
//		 dom.removeNodeByName("D:/wuliu/ycwl/WebRoot/xml/report.xml", "stat",
//		 "0001");
//		 dom.getParentNdVal("D:/wuliu/ycwl/WebRoot/xml/report.xml","");
	}
	
	public void removeNodeByNameBysonVal(String path, String nodeName,
			String atrbtName, String sonText) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			List list = root.getChildren(nodeName);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				List sonList = el.getChildren();
				Iterator its = sonList.iterator();
				while (its.hasNext()) {
					Element sonel = (Element) its.next();
					if (el.getAttribute("name").getValue().equals(atrbtName)
							& sonel.getText().trim().equals(sonText)) {
						el.getParentElement().removeContent(el);
					}
				}
				break;
			}
			Format mat = Format.getCompactFormat();
			mat.setIndent("  ");
			XMLOutputter out = new XMLOutputter(mat);
			out.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeNodeByIdBysonVal(String path, String nodeName,
			String atrbtName, String sonText) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			List list = root.getChildren(nodeName);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				List sonList = el.getChildren();
				Iterator its = sonList.iterator();
				while (its.hasNext()) {
					Element sonel = (Element) its.next();
					if (el.getAttribute("id").getValue().equals(atrbtName)
							& sonel.getText().trim().equals(sonText)) {
						el.getParentElement().removeContent(el);
					}
				}
				break;
			}
			Format mat = Format.getCompactFormat();
			mat.setIndent("  ");
			XMLOutputter out = new XMLOutputter(mat);
			out.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeNode(String path, String nodeName) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			root.removeChildren(nodeName);
			Format mat = Format.getCompactFormat();
			mat.setIndent("  ");
			XMLOutputter out = new XMLOutputter(mat);
			out.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeNodeById(String path, String nodeName, String attribute) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			List list = root.getChildren(nodeName);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				if (el.getAttribute("id").getValue().equals(attribute)) {
					el.getParentElement().removeContent(el);
					break;
				}

			}
			Format mat = Format.getCompactFormat();
			mat.setIndent("  ");
			XMLOutputter out = new XMLOutputter(mat);
			out.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeNodeByName(String path, String nodeName, String nameValue) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			List list = root.getChildren(nodeName);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				if (el.getAttribute("name").getValue().equals(nameValue)) {
					el.getParentElement().removeContent(el);
					break;
				}

			}
			Format mat = Format.getCompactFormat();
			mat.setIndent("  ");
			XMLOutputter out = new XMLOutputter(mat);
			out.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeNodeByVal(String path, String nodeName, String value) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			List list = root.getChildren(nodeName);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				if (el.getValue().equals(value)) {
					el.getParentElement().removeContent(el);
					break;
				}

			}
			Format mat = Format.getCompactFormat();
			mat.setIndent("  ");
			XMLOutputter out = new XMLOutputter(mat);
			out.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeNodeByNmValByNdVal(String path, String nodeName,
			String atrbtVal, String value) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			List list = root.getChildren(nodeName);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				if (el.getAttribute("name").getValue().equals(atrbtVal)
						& el.getValue().equals(value)) {
					el.getParentElement().removeContent(el);
					break;
				}

			}
			Format mat = Format.getCompactFormat();
			mat.setIndent("  ");
			XMLOutputter out = new XMLOutputter(mat);
			out.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeNodeByIdValByNdVal(String path, String nodeName,
			String atrbtVal, String value) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			List list = root.getChildren(nodeName);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				if (el.getAttribute("id").getValue().equals(atrbtVal)
						& el.getValue().equals(value)) {
					el.getParentElement().removeContent(el);
					break;
				}

			}
			Format mat = Format.getCompactFormat();
			mat.setIndent("  ");
			XMLOutputter out = new XMLOutputter(mat);
			out.output(doc, new FileOutputStream(path));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getParentNdVal(String path, String nodeName) {
		SAXBuilder builder = new SAXBuilder();
		String value = "";
		try {

			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			Element el = root.getChild(nodeName);
			Element fel = el.getParentElement();
			value = fel.getValue().trim();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;

	}

	public String geSonNdVal(String path, String nodeName) {
		SAXBuilder builder = new SAXBuilder();
		String value = "";
		try {

			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			Element el = root.getChild(nodeName);
			value = el.getValue().trim();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(value);
		return value;
	}
	
	/**
	 * 和下面的方法：getgrdsonNdVal，作用是一样的，只是接收的是xml格式的字符串作为参数
	 * @param xmlDoc
	 * @param sonNdname
	 * @param idval
	 * @return
	 */
	public String getgrdsonNdVal_String(String xmlDoc, String sonNdname, String idval) {
		String value = "";
		SAXBuilder builder = new SAXBuilder();
        //创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        
		try {
			Document doc = builder.build(source);
			Element root = doc.getRootElement();
			Element el = root.getChild(sonNdname);
			value = el.getChild(idval).getValue().trim();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return value;
	}

	/**
	 * 取得xml格式流中根节点下某个节点的子节点
	 * @param msg xml格式的流
	 * @param sonNdname 根节点下的某个子节点
	 * @param idval 根节点下的子节点的某个子节点
	 * @return
	 * 如取下面xml流的BIPCode节点的值：getgrdsonNdVal("msg", "BIPType", "BIPCode")
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <InterBOSS>
	 * <Version>0100</Version>
	 * <TestFlag>0</TestFlag>
     * <BIPType>
     * <BIPCode>BIP1A151</BIPCode>
     * <ActivityCode>T1000152</ActivityCode>
     * <ActionCode>0</ActionCode>
     * </BIPType>
	 * </InterBOSS>
	 */
	public String getgrdsonNdVal(InputStream msg, String sonNdname, String idval) {
		SAXBuilder builder = new SAXBuilder();
		String value = "";
		try {

			Document doc = builder.build(msg);
			Element root = doc.getRootElement();
			Element el = root.getChild(sonNdname);
			value = el.getChild(idval).getValue().trim();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return value;
	}
	
	public String getGrdsonNdVal(String path, String sonidval, String gsonNdname) {
		SAXBuilder builder = new SAXBuilder();
		String value = "";
		try {

			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();

			List list = root.getChildren();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				if (el.getAttribute("id").getValue().trim().equals(sonidval)) {
					Element sel = el.getChild(gsonNdname);
					value = sel.getText().trim();
					break;
				}
			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(value);

		return value;
	}

	public String getgrdsonNdVal(String path, String sonNdname, String idval) {
		SAXBuilder builder = new SAXBuilder();
		String value = "";
		try {

			Document doc = builder.build(new File(path));
			Element root = doc.getRootElement();
			Element el = root.getChild(sonNdname);
			List list = el.getChildren();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element sel = (Element) it.next();
				if (sel.getAttribute("id").getValue().trim().equals(idval)) {
					value = sel.getText().trim();
					break;
				}
			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(value);

		return value;
	}

	public String getGgrdsonNdVal(InputStream path, String sonNdname, String idval, String grdsonNode) {

		SAXBuilder builder = new SAXBuilder();

		String value = "";
		try {

			Document doc = builder.build(path);

			Element root = doc.getRootElement();
			Element el = root.getChild(sonNdname);
			List list = el.getChildren();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element sel = (Element) it.next();
				if (sel.getAttribute("id").getValue().trim().equals(idval)) {
					Element gsel = sel.getChild(grdsonNode);
					value = gsel.getText().trim();
					break;
				}
			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(value);

		return value;

	}
	
	/**
	 * 修改XML字符串中第二级节点sonNdname下的子节点idval的值
	 * @param xmlDoc  	XML格式的字符串
	 * @param sonNdname	需要修改值的节点名称(从根节点算起，sonNdname属于第二级节点)
	 * @param idval     需要修改值的节点名称，属于sonNdname下的子节点
	 * @param setValue  需要设置的值
	 * @return
	 * 注意：此功能未完成，还需要保存文档的代码，建议xml格式字符串xmlDoc改成文件路径读取
	 */
	public String setGrdsonNdVal_String(String xmlDoc, String sonNdname, String idval, String setValue) {
		String value = "";
		SAXBuilder builder = new SAXBuilder();
        //创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        
		try {
			Document doc = builder.build(source);
			Element root = doc.getRootElement();
			Element el = root.getChild(sonNdname);
			el.getChild(idval).setText(setValue);
			value = el.getChild(idval).getValue().trim();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return value;
	}

}
