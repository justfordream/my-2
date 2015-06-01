/**
 * 
 */
package com.toolbox.util;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author cmt
 *
 */
public class StringUtil {

	
	/**
	 * 已分隔符来输出传入的字符串
	 * @author cmt
	 * @param split
	 * @param obj
	 * @return
	 */
	public static String toStringSpilt(String split,String ... obj){
		if (obj==null) return "";
		
		StringBuffer sbBuffer=new StringBuffer();
		for (String s:obj){
			if (s!=null && !s.equals("")){
				if (sbBuffer.length()==0)
					sbBuffer.append(s);
				else 
					sbBuffer.append(split).append(s);
			}
		}
		
		return sbBuffer.toString();
	}
	
	
	/**
	 * 按照长度获取字符串，如果超出截取最大长度，后面加...
	 * @author cmt
	 * @param str
	 * @param len
	 * @return
	 */
	public static String maxString(String str,Integer len){
		if (str==null) return str;
		if (str.length()<=len) return str;
		return str.substring(0, len)+"...";
	}
	
//	/**
//	 * 截取一部分的日志信息
//	 * @param str
//	 * @return
//	 */
//	public static String subMessage(String str){
//		if (StringUtils.isBlank(str)){
//			return str;
//		}
//		if (str.length()>=200) {
//			return str.substring(0, 200);
//		}else{
//			return str;
//		}
//	}

	/**
	 * 判断传入参数,如果是Null或者空值，返回false，不为空返回true
	 * @author cmt
	 * @param checkAll   True:所有的为空才返回,False:只要有一个为空返回
	 * @param strings
	 * @return
	 */
	public static Boolean checkNull(Boolean checkAll,Object ...objects ){
		Boolean ret=true;
		if (objects==null) return false;
		for (Object s : objects) {
			if (s==null || s.toString().trim().equals("")){
				if (!checkAll) 
					return false;
				else {
					ret= false;
				}
			}
		}
		return ret;
	}
	
	
	
	/**
	 * 返回等长字符，如果前缀+字符串>长度，返回字符串
	 * @author cmt
	 * @param prefix 前缀
	 * @param len
	 * @param str
	 * @return
	 */
	public static String getMaxLength(String prefix,int len,String str){
		if (!checkNull(false,str) || str.length()>=len ) return str;
		
		if (prefix.length()+str.length()>len) return str;
		
		StringBuilder sb=new StringBuilder();
		sb.append(prefix);
		for (int i = 0; i < len-prefix.length()- str.length(); i++) {
			sb.append("0");
		}
		sb.append(str);
		
		return sb.toString();
	}

	
	/**
	 * 返回最长字符串，前方补足，如果传入值的长度大于，将切割字符串
	 * @author cmt
	 * @param prefix
	 * @param len
	 * @param str
	 * @return
	 */
	public static String fixLength(String prefix,int len,String str){
		if (str==null){
			str="";
		}
		
		if (str.length()>=len){
			return str.substring(str.length()-len,str.length());
		}
		
		
		StringBuilder sb=new StringBuilder();
		sb.append(prefix);
		for (int i = 0; i < len-prefix.length()- str.length(); i++) {
			sb.append(prefix);
		}
		sb.append(str);
		
		return sb.toString();
	}
	
	public  static  double getRandomA2B(double i ,double j, int point){
		if (i >= j || j <= 0||point<1||point>5) {
			return 1D;
		}
		int p = (int)(Math.pow(10,point));;
		Random ran = new Random();
		int m = (int) Math.floor((j - i) * p);
		int n = ran.nextInt(m);
		BigDecimal x = new BigDecimal(n);
		double y = x
				.divide(new BigDecimal(p), point, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		double z = i + y;
		double s = new BigDecimal(z).setScale(point, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		return s;
	}

	public static long ceil(double value) {
		return (long) Math.ceil(value);
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
				xmlContent = xmlContent.replace(contextStrOld, contextStrNew);
			}
			if (xmlContent.indexOf("<BankAcctID>") != -1) {
				String contextStrOld = xmlContent.substring(xmlContent.indexOf("<BankAcctID>"),
						xmlContent.indexOf("</BankAcctID>") + 13);
				String contextStr = xmlContent.substring(xmlContent.indexOf("<BankAcctID>")+12,
						xmlContent.indexOf("</BankAcctID>"));
				String contextStrEncry = encryStr(contextStr, "*", 4, "right");
				String contextStrNew = "<BankAcctID>" + contextStrEncry + "</BankAcctID>";
				xmlContent = xmlContent.replace(contextStrOld, contextStrNew);
			}
			if (xmlContent.indexOf("<UserID>") != -1) {
				String contextStrOld = xmlContent.substring(xmlContent.indexOf("<UserID>"),
						xmlContent.indexOf("</UserID>") + 9);
				String contextStr = xmlContent.substring(xmlContent.indexOf("<UserID>")+8,
						xmlContent.indexOf("</UserID>"));
				String contextStrEncry = encryStr(contextStr, "*", 4, "right");
				String contextStrNew = "<UserID>" + contextStrEncry + "</UserID>";
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
}
