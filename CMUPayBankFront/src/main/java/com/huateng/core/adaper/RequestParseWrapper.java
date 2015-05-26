package com.huateng.core.adaper;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest;

public class RequestParseWrapper extends JakartaMultiPartRequest {

	@Override
	public void parse(HttpServletRequest request, String saveDir) throws IOException {
		//super.parse(request, saveDir);
	}

}
