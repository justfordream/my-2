package com.huateng.file.listener;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.huateng.constant.Common;

public class RspFileSessionListener implements HttpSessionListener {

	@SuppressWarnings("unchecked")
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		HttpSession session = arg0.getSession();
		ServletContext ctx = session.getServletContext();
		Map<String, String> map = (Map<String, String>) ctx
				.getAttribute(Common.FKEY);
		System.out.println(map);
		session.setAttribute(Common.FKEY, map);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
