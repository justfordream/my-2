/**
 * 
 */
package com.huateng.core.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Base64加解密
 * 
 * @author cmt
 * 
 */
@SuppressWarnings("restriction")
public class Base64Util {

	private static BASE64Encoder encoder = new sun.misc.BASE64Encoder();

	private static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

	public static String ENCODING = "UTF-8";

	public Base64Util() {
	}

	/**
	 * 加密
	 * 
	 * @author cmt
	 * @param s
	 * @return
	 */

	public static String encode(String s) throws Exception {
		return encoder.encodeBuffer(s.getBytes());
	}

	/**
	 * 解密
	 * 
	 * @author cmt
	 * @param s
	 * @return
	 */
	public static String decode(String s) throws Exception {
		byte[] temp = decoder.decodeBuffer(s);
		return new String(temp);
	}
}
