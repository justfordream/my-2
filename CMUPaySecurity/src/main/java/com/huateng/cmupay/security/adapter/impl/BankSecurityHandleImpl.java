package com.huateng.cmupay.security.adapter.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import ccit.security.bssp.ex.CrypException;

import com.huateng.cmupay.cert.SecurityAPI;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.log.MessageLogger;
import com.huateng.cmupay.security.adapter.BankSecurityHandle;

/**
 * 银行端的加解密
 * 
 * @author hys
 * 
 */
public class BankSecurityHandleImpl implements BankSecurityHandle {
	private static Logger logger = LoggerFactory
			.getLogger(SecurityHandleImpl.class);
	private MessageLogger upayLog = MessageLogger.getLogger(BankSecurityHandleImpl.class);

	private String  ccbPayKey;
	private String  spdbPayKey;

	public String getCcbPayKey() {
		return ccbPayKey;
	}

	public void setCcbPayKey(String ccbPayKey) {
		this.ccbPayKey = ccbPayKey;
	}

	public String getSpdbPayKey() {
		return spdbPayKey;
	}

	public void setSpdbPayKey(String spdbPayKey) {
		this.spdbPayKey = spdbPayKey;
	}

	/**
	 * 对称加密、解密
	 * 
	 * @param type
	 *            : 0、建行；1、浦发
	 * @param key
	 *            ：密钥字节串
	 * @param bEncrypto
	 *            ：true表示加密；false表示解密
	 * @param inData
	 *            ：加密时输入为明文,解密时输入为密文（DER，Base64编码均可）
	 * @param parameter
	 *            :对称加密因子，当加密模式为CBC 时，此参数有效,此参数必须为 octet string类型
	 * @return 加密时输出为密文(DER 编码),解密时输出为明文
	 */
	@Override
	public String symDecryptPNI(int type, boolean bEncrypto, String inData) {
		logger.debug("{}处理{},原信息{}", new Object[] { type == 0 ? "建行" : "浦发",
				bEncrypto == true ? "加密" : "解密", inData });
		upayLog.debug("{}处理{},原信息{}", new Object[] { type == 0 ? "建行" : "浦发",
				bEncrypto == true ? "加密" : "解密", inData });
		String key = "";
		String resMsg = "";
		if (type == 0) {
			key = ccbPayKey;
		} else if (type == 1) {
			key = spdbPayKey;
		}
		try {
			if (bEncrypto) {
				resMsg = ebotongEncrypto(key,inData);
			} else {
				resMsg = ebotongDecrypto(key,inData);
			}
			logger.debug("{}处理{},原信息{},结果{},操作成功", new Object[] {
					type == 0 ? "建行" : "浦发", bEncrypto == true ? "加密" : "解密",inData, resMsg });
			upayLog.debug("{}处理{},原信息{},结果{},操作成功", new Object[] {
					type == 0 ? "建行" : "浦发", bEncrypto == true ? "加密" : "解密",inData, resMsg });
		} catch (CrypException e) {
			e.printStackTrace();
			logger.error("{}处理{},原信息{},失败{}", new Object[] {
					type == 0 ? "建行" : "浦发", bEncrypto == true ? "加密" : "解密",inData, e.getMessage() });
			upayLog.error("{}处理{},原信息{},失败{}", new Object[] {
					type == 0 ? "建行" : "浦发", bEncrypto == true ? "加密" : "解密",inData, e.getMessage() });
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("{}处理{},原信息{},失败{}", new Object[] {
					type == 0 ? "建行" : "浦发", bEncrypto == true ? "加密" : "解密",inData, e.getMessage() });
			upayLog.error("{}处理{},原信息{},失败{}", new Object[] {
					type == 0 ? "建行" : "浦发", bEncrypto == true ? "加密" : "解密",inData, e.getMessage() });
		}
		return resMsg;
	}

	/**
	 * 加密字符串
	 * 
	 * @throws CrypException
	 * @throws UnsupportedEncodingException
	 */
	public static String ebotongEncrypto(String key,String str)
			throws UnsupportedEncodingException, CrypException {
		String result = str;
		if (str != null && str.length() > 0) {
			BASE64Encoder base64encoder = new BASE64Encoder();
			byte encodeByte[] = SecurityAPI.symCrypto(CommonConstant.symType, key.getBytes(),true, str.getBytes(CommonConstant.ENCODEING), null);
			result = base64encoder.encode(encodeByte);
		}
		return result;
	}

	/**
	 * 解密字符串
	 * @throws IOException 
	 * @throws CrypException 
	 */
	public static String ebotongDecrypto(String key,String str) throws IOException, CrypException {
		String result = str;
		if (str != null && str.length() > 0) {
			BASE64Decoder base64decoder = new BASE64Decoder();
			byte[] encodeByte = base64decoder.decodeBuffer(str);
			byte decoder[] = SecurityAPI.symCrypto(CommonConstant.symType, key.getBytes(),false, encodeByte, null);
			result = new String(decoder, CommonConstant.ENCODEING);
		}
		return result;
	}

	public static void main(String[] args) {
		 String mes = "4iHYjhYovTzslScutWv2ApXnfUyyRW6W";
		 String resInfo = new BankSecurityHandleImpl().symDecryptPNI(0,true,mes);
		 System.out.println("加密后的支付串：" + resInfo);
		String resInfo2 = new BankSecurityHandleImpl().symDecryptPNI(0,false,mes);
		System.out.println("解密后的字符串：" + resInfo2);
		
	}

}
