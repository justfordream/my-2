/**
 * 
 */
package com.toolbox.base64;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Base64加解密
 * 
 * @author cmt
 * 
 */
public class Base64 {

	private static BASE64Encoder encoder = new sun.misc.BASE64Encoder();

	private static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

	public Base64() {
	}

	/**
	 * 加密
	 * @author cmt
	 * @param s
	 * @return
	 */
	
	public static String encode(String s) throws Exception{
		return URLEncoder.encode(encoder.encode(s.getBytes()),"UTF-8");
	}

	/**
	 * 解密
	 * @author cmt
	 * @param s
	 * @return
	 */
	public static String decode(String s) throws Exception{
		s=URLDecoder.decode(s,"UTF-8");
		try {
			byte[] temp = decoder.decodeBuffer(s);
			return new String(temp);
		} catch (IOException ioe) {
			// handler
		}
		return s;
	}
}
