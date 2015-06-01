/**
 * 
 */
package com.huateng.cmupay.controller.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.huateng.toolbox.xml.GetXmlFile;

/**
 * 提示信息索引类
 * 
 * @author cmt
 * 
 */
public class MessageHandler {

	private final static String XMLFILE = "/message.xml";
	
	private final static Map<String, String> CrmErrorCode = new HashMap<String, String>();
	private final static Map<String, String> CrmErrorMsg = new HashMap<String, String>();
	private final static Map<String, String> BankErrorCode = new HashMap<String, String>();
	private final static Map<String, String> BankErrorMsg = new HashMap<String, String>();
	private final static Map<String, String> WzwErrorCode = new HashMap<String, String>();
	private final static Map<String, String> WzwErrorMsg = new HashMap<String, String>();
	private final static Map<String, String> UpayErrorCode = new HashMap<String, String>();
	private final static Map<String, String> UpayErrorMsg = new HashMap<String, String>();

	static {
		GetXmlFile xmlFile = new GetXmlFile(XMLFILE);

		List<Element> crmErrorList = xmlFile
				.GetListByXpath("/Root/ErrorMsgList/CrmMsgList/Msg");

		for (Element element : crmErrorList) {
			CrmErrorCode.put(element.attributeValue("code"),
					element.attributeValue("code"));
		}

		for (Element element : crmErrorList) {
			CrmErrorMsg.put(element.attributeValue("code"),
					element.attributeValue("value"));
		}
		
		List<Element> BankErrorList = xmlFile
				.GetListByXpath("/Root/ErrorMsgList/BankMsgList/Msg");

		for (Element element : BankErrorList) {
			BankErrorCode.put(element.attributeValue("code"),
					element.attributeValue("code"));
		}

		for (Element element : BankErrorList) {
			BankErrorMsg.put(element.attributeValue("code"),
					element.attributeValue("value"));
		}
		
		List<Element> WzwErrorList = xmlFile
				.GetListByXpath("/Root/ErrorMsgList/WzwMsgList/Msg");

		for (Element element : WzwErrorList) {
			WzwErrorCode.put(element.attributeValue("code"),
					element.attributeValue("code"));
		}

		for (Element element : WzwErrorList) {
			WzwErrorMsg.put(element.attributeValue("code"),
					element.attributeValue("value"));
		}
		
		List<Element> UpayErrorList = xmlFile
				.GetListByXpath("/Root/ErrorMsgList/UpayMsgList/Msg");

		for (Element element : UpayErrorList) {
			UpayErrorCode.put(element.attributeValue("code"),
					element.attributeValue("code"));
		}

		for (Element element : UpayErrorList) {
			UpayErrorMsg.put(element.attributeValue("code"),
					element.attributeValue("value"));
		}
	}

	
	
	
	
	/**
	 * 根据wzw错误代码或者错误信息
	 * 
	 * @author cmt
	 * @param code
	 * @return
	 */
	public static String getWzwErrCode(String code) {
		if (code == null || code.equals(""))
			code = "2998";
		String msgValue = WzwErrorCode.get(code);
		if (msgValue == null) {
			code = "2998";
			msgValue = getWzwErrCode(code);
		}

		return msgValue;
	}
	
	/**
	 * 根据wzw错误代码或者错误信息
	 * 
	 * @author cmt
	 * @param code
	 * @return
	 */
	public static String getWzwErrMsg(String code) {
		if (code == null || code.equals(""))
			code = "2998";
		String msgValue = WzwErrorMsg.get(code);
		if (msgValue == null) {
			code = "2998";
			msgValue = getWzwErrMsg(code);
		}

		return msgValue;
	}

	
	
	
	/**
	 * 根据crm错误代码或者错误信息
	 * 
	 * @author cmt
	 * @param code
	 * @return
	 */
	public static String getCrmErrCode(String code) {
		if (code == null || code.equals(""))
			code = "5A06";
		String msgValue = CrmErrorCode.get(code);
		if (msgValue == null) {
			code = "5A06";
			msgValue = getCrmErrCode(code);
		}

		return msgValue;
	}
	
	/**
	 * 根据crm错误代码或者错误信息
	 * 
	 * @author cmt
	 * @param code
	 * @return
	 */
	public static String getCrmErrMsg(String code) {
		if (code == null || code.equals(""))
			code = "5A06";
		String msgValue = CrmErrorMsg.get(code);
		if (msgValue == null) {
			code = "5A06";
			msgValue = getCrmErrMsg(code);
		}

		return msgValue;
	}


	/**
	 * 根据银行错误代码或者错误信息
	 * 
	 * @author cmt
	 * @param code
	 * @return
	 */
	public static String getBankErrCode(String code) {
		if (code == null || code.equals(""))
			code = "015A06";
		String msgValue = BankErrorCode.get(code);
		if (msgValue == null) {
			code = "015A06";
			msgValue = getBankErrCode(code);
		}

		return msgValue;
	}
	
	
	/**
	 * 根据银行错误代码或者错误信息
	 * 
	 * @author cmt
	 * @param code
	 * @return
	 */
	public static String getBankErrMsg(String code) {
		if (code == null || code.equals(""))
			code = "015A06";
		String msgValue = BankErrorMsg.get(code);
		if (msgValue == null) {
			code = "015A06";
			msgValue = getBankErrMsg(code);
		}

		return msgValue;
	}

	/**
	 * 根据银行错误代码或者错误信息
	 * 
	 * @author cmt
	 * @param code
	 * @return
	 */
	public static String getUpayErrCode(String code) {
		if (code == null || code.equals(""))
			code = "U99999";
		String msgValue = UpayErrorCode.get(code);
		if (msgValue == null) {
			code = "U99999";
			msgValue = getUpayErrCode(code);
		}

		return msgValue;
	}

	/**
	 * 根据crm错误代码或者错误信息
	 * 
	 * @author cmt
	 * @param code
	 * @return
	 */
	public static String getUpayErrMsg(String code) {
		if (code == null || code.equals(""))
			code = "U99999";
		String msgValue = UpayErrorMsg.get(code);
		if (msgValue == null) {
			code = "U99999";
			msgValue = getUpayErrMsg(code);
		}

		return msgValue;
	}
	
	
	
	/**
	 * 
	 * 
	 * @author cmt
	 * @param code
	 * @return
	 */
	/*public static List<String> getErrCodeArray(String [] code) {
		List<String> nameArray = new ArrayList<String>();
		if (code == null || code.length==0)
			return nameArray;
		for(int i =0; i<code.length;i++){
			String name = ErrorCode.get(code[i]);
			nameArray.add(name);
		}
		return nameArray;
	}*/

}
