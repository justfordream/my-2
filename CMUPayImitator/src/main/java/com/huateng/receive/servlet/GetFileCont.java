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

import org.apache.log4j.Logger;

import main.java.com.huateng.commons.config.GetResponseCont;


/**
 * 获取模板文件中的内容
 */
public class GetFileCont extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(GetFileCont.class);
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetFileCont() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		Map<String,String> rspxmlmap = new HashMap<String,String>();
		try {
			rspxmlmap = GetResponseCont.getCont(rspxmlmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fileName = request.getParameter("fileName");
		log.debug("getFileCont报文名称："+fileName);
		if (null != fileName && !"".equals(fileName)) {
			String fileCont = rspxmlmap.get(fileName);
			log.debug("getFileCont报文内容："+fileCont);
			PrintWriter r = response.getWriter();
			r.write(fileCont);
			r.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
