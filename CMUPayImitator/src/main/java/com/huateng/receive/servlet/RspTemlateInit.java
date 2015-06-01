package main.java.com.huateng.receive.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.huateng.bundle.PropertyBundle;


import main.java.com.huateng.commons.config.GN;
import main.java.com.huateng.commons.config.GetResponseCont;
/**
 * 初始化下拉框
 */
public class RspTemlateInit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     */
    public RspTemlateInit() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		Map<String,String> rspxmlmap = new HashMap<String,String>();
		try {
			GetResponseCont.getCont(rspxmlmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(String s:rspxmlmap.keySet()){
			sb.append("\"").append(s).append("\"").append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("]");
		PrintWriter r = response.getWriter();
		r.write(sb.toString());
		r.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
