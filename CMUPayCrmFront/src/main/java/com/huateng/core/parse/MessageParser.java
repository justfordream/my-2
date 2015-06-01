package com.huateng.core.parse;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.huateng.crm.common.CRMConstant;

/**
 * 报文解析
 * 
 * @author Gary
 * 
 */
public class MessageParser implements XMLParser {
	private Map<String, String> requestData;
	

	public MessageParser() {
		super();
	}

	public MessageParser(Map<String, String> requestData) {
		super();
		this.requestData = requestData;
	}

	/**
	 * 构造請求報文信息
	 * 
	 * @return
	 */
	@Override
	public String createMessage() {

		return this.getXmlHead();
	}

	@Override
	public String getXmlHead() {
		String head = this.requestData.get(CRMConstant.XML_HEAD);
		if (StringUtils.isBlank(head)) {
			head = "";
		}
		return head;
	}

	@Override
	public String getXmlBody() {
		String body = this.requestData.get(CRMConstant.XML_BODY);
		if (StringUtils.isBlank(body)) {
			body = "";
		}
		return body;
	}

	@Override
	public String getAttachfile() {
		String attachfile = this.requestData.get(CRMConstant.ATTACH_FILE);
		if (StringUtils.isBlank(attachfile)) {
			attachfile = "";
		}
		return attachfile;
	}

}
