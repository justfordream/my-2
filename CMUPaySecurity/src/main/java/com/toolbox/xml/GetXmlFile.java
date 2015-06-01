/**
 * 
 */
package com.toolbox.xml;

import java.io.StringReader;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;


/**
 * @author cmt
 *
 */
public class GetXmlFile {
	private String fileName = "";

	private Document document = null;

	/**
	 * @author cmt
	 * @param fileName
	 */
	public GetXmlFile(String fileName) {
		
		URL fileUrl=GetXmlFile.class.getResource(fileName);
		if (fileUrl!=null)
			this.fileName = GetXmlFile.class.getResource(fileName).toString();
		else
			this.fileName=fileName;
		
		try {
			SAXReader reader = new SAXReader();
			reader.setValidation(false);
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			this.document = reader.read(this.fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GetXmlFile(){}
	
	
	
	/**
	 * 设置XML格式的字符串
	 * @author cmt
	 * @param xmlString
	 * @return
	 */
	public Document setXmlString(String xmlString){
		StringReader sr = new StringReader(xmlString);   
		InputSource is = new InputSource(sr); 
		
		SAXReader reader = new SAXReader();
		try {
			this.document =reader.read(is);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.document;
	}
	
	public Document GetDocument() {
		return this.document;
	}

	@SuppressWarnings("unchecked")
	public List<Element> GetListByXpath(String xpath) {
		return this.document.selectNodes(xpath);
	}
}
