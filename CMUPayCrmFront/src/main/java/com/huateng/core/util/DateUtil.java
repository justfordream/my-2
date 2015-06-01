package com.huateng.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 日期工具类
 * 
 * @author Gary
 * 
 */
public class DateUtil {
	public static String FORMAT_DATE = "yyyy-MM-dd";
	private static final String userName="<UserName>**********</UserName>";
	private static final String userId="<UserID>**********</UserID>";
	private static final String bankAcctID="<BankAcctID>************</BankAcctID>";

	private static final Logger logger = LoggerFactory.getLogger("DateUtil");
	/**
	 * 获取当前日期
	 * 
	 * @return 字符串格式
	 */
	public static String getCurrentDate() {
		SimpleDateFormat df = new SimpleDateFormat(FORMAT_DATE);
		return df.format(new Date());
	}

	/**
	 * 获取当然日期
	 * 
	 * @return 日期格式
	 */
	public static Date getNowDate() {
		return new Date();
	}

	/**
	 * 获取当前日期和时间
	 * 
	 * @return
	 */
	public static String getCurrentFullDate() {
		return DateUtil.getCurrentDate() + " " + TimeUtil.getCurrentTime();
	}

	/**
	 * 转换特殊字符
	 * @param xmlContent
	 * @return
	 */
	public static String paseLog(String xmlContent){	
		String xmlStr = xmlContent;
		try {
			
			if (xmlContent.indexOf("<UserName>") != -1) {
				String contextStrOld = xmlContent.substring(xmlContent.indexOf("<UserName>"),
						xmlContent.indexOf("</UserName>") + 11);
				String needReplceStr = xmlContent.substring(xmlContent.indexOf("<UserName>")+10,
						xmlContent.indexOf("</UserName>"));
				String contextStrEncry = encryStr(needReplceStr, "*", 1, "right");
				String contextStrNew = "<UserName>" + contextStrEncry + "</UserName>";
				/*String startXml = xmlContent.substring(0,
						xmlContent.indexOf("<UserName>"));
				xmlBody.append(startXml);
				xmlBody.append(userName);
				String endXml = xmlContent.substring(xmlContent
						.indexOf("</UserName>") + 11);
				xmlBody.append(endXml);*/
				xmlContent = xmlContent.replace(contextStrOld, contextStrNew);
			}
			if (xmlContent.indexOf("<BankAcctID>") != -1) {
				String contextStrOld = xmlContent.substring(xmlContent.indexOf("<BankAcctID>"),
						xmlContent.indexOf("</BankAcctID>") + 13);
				String contextStr = xmlContent.substring(xmlContent.indexOf("<BankAcctID>")+12,
						xmlContent.indexOf("</BankAcctID>"));
				String contextStrEncry = encryStr(contextStr, "*", 4, "right");
				String contextStrNew = "<BankAcctID>" + contextStrEncry + "</BankAcctID>";
				/*String startStr = xmlContent.substring(0,
						xmlContent.indexOf("<BankAcctID>"));
				xmlBody.append(startStr);
				xmlBody.append(bankAcctID);
				String endXml = xmlContent.substring(xmlContent
						.indexOf("</BankAcctID>") + 13);
				xmlBody.append(endXml);*/
				xmlContent = xmlContent.replace(contextStrOld, contextStrNew);
			}
			if (xmlContent.indexOf("<UserID>") != -1) {
				String contextStrOld = xmlContent.substring(xmlContent.indexOf("<UserID>"),
						xmlContent.indexOf("</UserID>") + 9);
				String contextStr = xmlContent.substring(xmlContent.indexOf("<UserID>")+8,
						xmlContent.indexOf("</UserID>"));
				String contextStrEncry = encryStr(contextStr, "*", 4, "right");
				String contextStrNew = "<UserID>" + contextStrEncry + "</UserID>";
				/*String startStr = xmlContent.substring(0,
						xmlContent.indexOf("<UserID>"));
				xmlBody.append(startStr);
				xmlBody.append(userId);
				String endXml = xmlContent.substring(xmlContent
						.indexOf("</UserID>") + 9);
				xmlBody.append(endXml);*/
				xmlContent = xmlContent.replace(contextStrOld, contextStrNew);
			}
		} catch (Exception e) {
			return xmlStr;
		}
		return xmlContent;
	}
	
	/**
	 * 
	 * @param strContent 原字符串
	 * @param replceFlag 替换字符 如 “*”
	 * @param keepLength 保留长度
	 * @param keepDirection 从左、右开始保留    默认从右开始保留
	 * @return
	 * @throws Exception
	 */
	public static String encryStr(String strContent,String replceFlag,int keepLength,String keepDirection) throws Exception{
		String result = "";
		if(keepLength > strContent.length())
			return strContent;
		//保留字符
		String strKeep = "";
		if("LEFT".equalsIgnoreCase(keepDirection)){
			strKeep = strContent.substring(0,keepLength);
		}else{
			strKeep = strContent.substring(strContent.length()-keepLength,strContent.length());
		}
	
		//需替换字符串长度
		int replaceLangth = strContent.length() - keepLength;
		StringBuffer replceStrBuffer = new StringBuffer();
		for(int i=0,j=replaceLangth;i<j;i++){
			replceStrBuffer.append(replceFlag);
		}
		
		if("LEFT".equalsIgnoreCase(keepDirection)){
			result = strKeep + replceStrBuffer.toString();
		}else{
			result = replceStrBuffer.toString() + strKeep;
		}

		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(DateUtil.getCurrentDate());
	}
}
