package com.huateng.third.bean;

import com.huateng.third.bean.body.Body;
import com.huateng.third.bean.head.Header;
import com.huateng.third.bean.head.Sign;

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
