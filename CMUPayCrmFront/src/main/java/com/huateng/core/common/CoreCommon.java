package com.huateng.core.common;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Gary
 * 
 */
public class CoreCommon {
//	private static ResourceBundle bundle = ResourceBundle.getBundle("jms");
//
//	/**
//	 * 获取加密开关(open:开,close:关)
//	 * 
//	 * @return
//	 */
//	public static String getEncryptSwitch() {
//		return bundle.getString("encrypt.switch");
//	}
//
//	/**
//	 * 获得加密开放省4位系统编码
//	 * 
//	 * @return
//	 */
//	public static String getEncryptProvince() {
//		return bundle.getString("encrypt.province");
//	}
//
//	/**
//	 * 获取解密开关(open:开,close:关)
//	 * 
//	 * @return
//	 */
//	public static String getDecryptSwitch() {
//		return bundle.getString("decrypt.switch");
//	}
//
//	/**
//	 * 获得加密开放省4位系统编码
//	 * 
//	 * @return
//	 */
//	public static String getDecryptProvince() {
//		return bundle.getString("decrypt.province");
//	}
//
//	/**
//	 * 获得加密开发接口交易码
//	 * 
//	 * @return
//	 */
//	public static String getEncryptTradeCode() {
//		return bundle.getString("encrypt.tradecode");
//	}
//
//	/**
//	 * 获得解密开发接口交易码
//	 * 
//	 * @return
//	 */
//	public static String getDecryptTradeCode() {
//		return bundle.getString("decrypt.tradecode");
//	}
//
//	/**
//	 * 判断某省是否开放加密功能
//	 * 
//	 * @param provCode
//	 *            省机构编码
//	 * @return
//	 */
//	public static boolean isEncryptPermit(String provCode) {
//		String provs = CoreCommon.getEncryptProvince();
//		if (StringUtils.isBlank(provs) || StringUtils.isBlank(provCode)) {
//			return true;
//		}
//		if (provs.indexOf(provCode) != -1) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 判断某省是否开放解密功能
//	 * 
//	 * @param provCode
//	 *            省机构编码
//	 * @return
//	 */
//	public static boolean isDecryptPermit(String provCode) {
//		String provs = CoreCommon.getDecryptProvince();
//		if (StringUtils.isBlank(provs) || StringUtils.isBlank(provCode)) {
//			return true;
//		}
//		if (provs.indexOf(provCode) != -1) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 判断某接口是否开发加密功能
//	 * 
//	 * @param tradeCode
//	 * @return
//	 */
//	public static boolean hasEncryptTradeCode(String bipCode,String tradeCode) {
//		String tradeCodes = CoreCommon.getEncryptTradeCode();
//		if (StringUtils.isBlank(tradeCodes) || StringUtils.isBlank(tradeCode) ||StringUtils.isBlank(bipCode)) {
//			return true;
//		}
//		if (tradeCodes.indexOf(bipCode+"|"+tradeCode) != -1) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 判断某接口是否开发解密功能
//	 * 
//	 * @param tradeCode
//	 * @return
//	 */
//	public static boolean hasDecryptTradeCode(String bipCode,String tradeCode) {
//		String tradeCodes = CoreCommon.getDecryptTradeCode();
//		if (StringUtils.isBlank(tradeCodes) || StringUtils.isBlank(tradeCode) ||StringUtils.isBlank(bipCode)) {
//			return true;
//		}
//		if (tradeCodes.indexOf(bipCode+"|"+tradeCode) != -1) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 加密
//	 * 
//	 * @param encryptSwitch
//	 *            加密开关
//	 * @param provCode
//	 *            省机构编码
//	 * @param tradeCode
//	 *            交易编码
//	 * @return
//	 */
//	public static boolean checkIsEncrypt(String encryptSwitch, String provCode,String bipCode, String tradeCode) {
//		boolean result = false;
//		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(encryptSwitch)) {
//			result = CoreCommon.isEncryptPermit(provCode);
//		}
//		if (result) {
//			result = CoreCommon.hasEncryptTradeCode(bipCode,tradeCode);
//		}
//		return result;
//	}
//
//	/**
//	 * 解密
//	 * 
//	 * @param decryptSwitch
//	 *            解密开关
//	 * @param provCode
//	 *            省机构编码
//	 * @param tradeCode
//	 *            交易编码
//	 * @return
//	 */
//	public static boolean checkIsDecrypt(String decryptSwitch, String provCode, String bipCode, String tradeCode) {
//		boolean result = false;
//		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(decryptSwitch)) {
//			result = CoreCommon.isDecryptPermit(provCode);
//		}
//		if (result) {
//			result = CoreCommon.hasDecryptTradeCode(bipCode,tradeCode);
//		}
//		return result;
//	}
//		
//	/**
//	 * 获得网状网地址
//	 * 
//	 * @return
//	 */
//	public static String getCenterPath() {
//		return bundle.getString("CENTER_PATH");
//	}
//	
//	/**
//	 * 连接超时时间
//	 * 
//	 * @return
//	 */
//	public static String getConnectionTimeOut() {
//		return bundle.getString("http.conn.timeout");
//	}
//	
//	/**
//	 * 接收响应超时时间
//	 * 
//	 * @return
//	 */
//	public static String getReceiveTimeOut() {
//		return bundle.getString("http.rev.timeout");
//	}
//	
//	
//	/**
//	 * 是否调用核心
//	 * */
//	public static boolean  CheckIsInvokeCore(){
//		boolean result = false;
//		String invokeCore =  bundle.getString("INVOKE_CORE");
//		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(invokeCore)) {
//			result = true;
//		}
//		return result;
//	}
	//----------------------------------华丽的分割线-------------------------------
	/**
	 * 解密
	 * 
	 * @param decryptSwitch
	 *            解密开关
	 * @param provCode
	 *            省机构编码
	 * @param tradeCode
	 *            交易编码
	 * @return
	 */
	public static boolean checkIsDecrypt(String decryptSwitch, String provCode, String bipCode, String tradeCode,String tradeCodes,String proCodes) {
		boolean result = false;
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(decryptSwitch)) {
			result = CoreCommon.isDecryptPermit(provCode,proCodes);
		}
		if (result) {
			result = CoreCommon.hasDecryptTradeCode(bipCode,tradeCode,tradeCodes);
		}
		return result;
	}
	
	/**
	 * 判断某省是否开放解密功能
	 * 
	 * @param provCode
	 *            省机构编码
	 * @return
	 */
	public static boolean isDecryptPermit(String provCode,String proCodes) {
		if (StringUtils.isBlank(proCodes) || StringUtils.isBlank(provCode)) {
			return true;
		}
		if (proCodes.indexOf(provCode) != -1) {
			return true;
		}
		return false;
	}
	

	/**
	 * 判断某接口是否开发解密功能
	 * 
	 * @param tradeCode
	 * @return
	 */
	public static boolean hasDecryptTradeCode(String bipCode,String tradeCode,String tradeCodes) {
		if (StringUtils.isBlank(tradeCodes) || StringUtils.isBlank(tradeCode) ||StringUtils.isBlank(bipCode)) {
			return true;
		}
		if (tradeCodes.indexOf(bipCode+"|"+tradeCode) != -1) {
			return true;
		}
		return false;
	}
	
	/**
	 * 加密
	 * 
	 * @param encryptSwitch
	 *            加密开关
	 * @param provCode
	 *            省机构编码
	 * @param tradeCode
	 *            交易编码
	 * @return
	 */
	public static boolean checkIsEncrypt(String encryptSwitch, String provCode,String bipCode, String tradeCode,String tradeCodes,String proCodes) {
		boolean result = false;
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(encryptSwitch)) {
			result = CoreCommon.isEncryptPermit(provCode,proCodes);
		}
		if (result) {
			result = CoreCommon.hasEncryptTradeCode(bipCode,tradeCode,tradeCodes);
		}
		return result;
	}
	
	/**
	 * 判断某省是否开放加密功能
	 * 
	 * @param provCode
	 *            省机构编码
	 * @return
	 */
	public static boolean isEncryptPermit(String provCode,String proCodes) {
		if (StringUtils.isBlank(proCodes) || StringUtils.isBlank(provCode)) {
			return true;
		}
		if (proCodes.indexOf(provCode) != -1) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断某接口是否开发加密功能
	 * 
	 * @param tradeCode
	 * @return
	 */
	public static boolean hasEncryptTradeCode(String bipCode,String tradeCode,String tradeCodes) {
		if (StringUtils.isBlank(tradeCodes) || StringUtils.isBlank(tradeCode) ||StringUtils.isBlank(bipCode)) {
			return true;
		}
		if (tradeCodes.indexOf(bipCode+"|"+tradeCode) != -1) {
			return true;
		}
		return false;
	}	
	
}
