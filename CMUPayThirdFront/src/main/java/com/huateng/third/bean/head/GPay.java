package com.huateng.third.bean.head;

/**
 * 银行请求报文
 * 
 * @author Gary
 * 
 */
public class GPay {
	/**
	 * 消息报文头
	 */
	private Header Header;
	/**
	 * 消息报文体
	 */
	private String Body;
	/**
	 * 消息签名
	 */
	private Sign Sign;

	public Header getHeader() {
		return Header;
	}

	public void setHeader(Header header) {
		Header = header;
	}

	public String getBody() {
		return Body;
	}

	public void setBody(String body) {
		Body = body;
	}

	public Sign getSign() {
		return Sign;
	}

	public void setSign(Sign sign) {
		Sign = sign;
	}

}
