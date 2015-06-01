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
 * @author huangyansen
 *
 */
public class AuthHessianServiceExporter extends HessianServiceExporter {

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		super.handleRequest(request, response);
	}
}
