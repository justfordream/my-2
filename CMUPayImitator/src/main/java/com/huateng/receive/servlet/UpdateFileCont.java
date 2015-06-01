package main.java.com.huateng.receive.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

import main.java.com.huateng.commons.config.GN;
import main.java.com.huateng.commons.config.GetResponseCont;

import com.huateng.bundle.PropertyBundle;
import com.huateng.constant.Common;

/**
 * Servlet implementation class UpdateFileCont
 */
public class UpdateFileCont extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(UpdateFileCont.class);
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateFileCont() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		String fileName = request.getParameter("fileName");
		String fileCont = request.getParameter("fileCont");
		if (null != fileName && !"".equals(fileName) && null != fileCont
				&& !"".equals(fileCont)) {
			Map<String,String> rspxmlmap = new HashMap<String,String>();
			try {
				rspxmlmap = GetResponseCont.getCont(rspxmlmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			rspxmlmap.put(fileName, fileCont);
			log.debug("修改后文件内容："+fileCont);
			if(fileName.startsWith("BIP")){
				String urlCrm = GN.CRMtemprepfilepath;
				BufferedWriter  bw = new BufferedWriter(new FileWriter(urlCrm+File.separator+fileName+".xml"));
				log.debug("修改crm文件的路径："+ urlCrm+File.separator+fileName+".xml");
				bw.write(fileCont);
				 bw.close();
			}else if(fileName.equals("022051")){
				String urlmmall = GN.mmalltemprepfilepath; 
				BufferedWriter  bw = new BufferedWriter(new FileWriter(urlmmall+File.separator+fileName+".xml"));
				log.debug("修改mmall文件的路径："+ urlmmall+File.separator+fileName+".xml");
				bw.write(fileCont);
				 bw.close();
			}else if(fileName.equals("022003")){
				String urltmall = GN.tmalltemprepfilepath; 
				BufferedWriter  bw = new BufferedWriter(new FileWriter(urltmall+File.separator+fileName+".xml"));
				log.debug("修改tmall文件的路径："+ urltmall+File.separator+fileName+".xml");
				bw.write(fileCont);
				 bw.close();
			}else{
				String urlBank = GN.banktemprepfilepath; 
				BufferedWriter  bw = new BufferedWriter(new FileWriter(urlBank+File.separator+fileName+".xml"));
				log.debug("修改bank文件的路径："+ urlBank+File.separator+fileName+".xml");
				bw.write(fileCont);
				 bw.close();
			}
			PrintWriter p = response.getWriter();
			p.write(rspxmlmap.get(fileName));
			p.close();
		}
	}

}
