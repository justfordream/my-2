/**
 * 
 */
package com.huateng.core.util;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author cmt
 *
 */
public class StrUtil {

	
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
	
	public static Object toTrimNull(Object str){
		if(str ==null ||"".equals(str.toString())||"null".equalsIgnoreCase(str.toString())){
			return null;
		}
		
		return str;
		
	}
	
	public static String subString(String str, int a, int b) {
		if (str == null ) {
			return null;
		}
		if(str.length() <= b){
			return str;
		}
		return str.substring(a, b);
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
	
	public static long ceil(double value) {
		return (long) Math.ceil(value);
	}
	
}
