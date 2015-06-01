/**
 * 
 */
package com.huateng.cmupay.serviceExporter;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.caucho.HessianServiceExporter;




/**
 * 
 * 
 * 
 * @author cmt
 * 
 */
public class AuthHessianServiceExporter extends HessianServiceExporter {

	protected final  Logger logger = LoggerFactory.getLogger(AuthHessianServiceExporter.class);
	
	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String auth = request.getHeader("authorization");
		System.out.print("auth:"+auth);
		try{
//			 BufferedReader  buffer = request.getReader();
//			 String str  = buffer.toString();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("未知错误",e);
			return;
		}

		super.handleRequest(request, response);
	}
}
