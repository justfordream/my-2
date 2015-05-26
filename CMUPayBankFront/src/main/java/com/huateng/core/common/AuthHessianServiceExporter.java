/**
 * 
 */
package com.huateng.core.common;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.caucho.HessianServiceExporter;




/**
 * 
 * 
 * 
 * @author cmt
 * 
 */
public class AuthHessianServiceExporter extends HessianServiceExporter {

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
//		String auth = request.getHeader("authorization");
//		System.out.print("auth:"+auth);
//		try{
//			 BufferedReader  buffer = request.getReader();
//			 String str  = buffer.toString();
//		}catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}

		super.handleRequest(request, response);
	}
}
