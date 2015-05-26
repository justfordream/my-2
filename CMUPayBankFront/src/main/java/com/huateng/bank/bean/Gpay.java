package com.huateng.bank.bean;

import com.huateng.bank.bean.head.Header;
import com.huateng.bank.bean.head.Sign;
import com.huateng.bank.bean.body.Body;

public class Gpay {
	/**
	 * 消息报文头
	 */
	private Header Header;
	/**
	 * 消息报文体
	 */
	private Body Body;
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

	public Body getBody() {
		return Body;
	}

	public void setBody(Body body) {
		Body = body;
	}

	public Sign getSign() {
		return Sign;
	}

	public void setSign(Sign sign) {
		Sign = sign;
	}
}
