package com.huateng.cmupay.cert;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {

    
	
//	
//	private static ResourceBundle bundle = ResourceBundle.getBundle("env");
//	
//	/**
//	 * 建设银行的签名的密钥
//	 * @return
//	 */
//	public static String getCCBPAYKEY(){
//		return bundle.getString("CCB_B2C_PAY_KEY");
//	}
//	/**
//	 * 建设银行加密的密钥
//	 * @return
//	 */
//	public static String getCCBKEY(){
//		return bundle.getString("CCB_PAY_KEY");
//	}
//	/**
//	 * 浦发银行的加密密钥
//	 * @return
//	 */
//	public static String getSPDBKEY(){
//		return bundle.getString("SPDB_PAY_KEY");
//	}
	public static byte[] readBytesFromFile(String fileName) throws IOException {
		FileInputStream fileInputStream;
		fileInputStream = new FileInputStream(fileName);
		int total = fileInputStream.available();
		byte[] buffer = new byte[total];
		fileInputStream.read(buffer);
		fileInputStream.close();
		return buffer;
	}

	public static String getFileHash(String fileName) {
		byte buffer[];
		try {
			buffer = readBytesFromFile(fileName);
			return sha1(buffer);
//			return new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String BytesToHexString(byte[] bytes) {
		byte tb;
		char low;
		char high;
		char tmpChar;
		StringBuffer str = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			tb = bytes[i];
			tmpChar = (char) ((tb >>> 4) & 0x000f);
			if (tmpChar >= 10) {
				high = (char) (('a' + tmpChar) - 10);
			} else {
				high = (char) ('0' + tmpChar);
			}
			str.append(high);
			tmpChar = (char) (tb & 0x000f);
			if (tmpChar >= 10) {
				low = (char) (('a' + tmpChar) - 10);
			} else {
				low = (char) ('0' + tmpChar);
			}
			str.append(low);
		}
		return str.toString();
	}

	public static String sha1(byte[] original) {
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"System doesn't support SHA1 algorithm.");
		}
		msgDigest.update(original);

		return BytesToHexString(msgDigest.digest());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "D:/PBILL20130604100001.CMCC-nosign.0004";
		String hexHashValue = FileUtil.getFileHash(fileName);

		System.out.println("Hex=" + hexHashValue);
	}

}
