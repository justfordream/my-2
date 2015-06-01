package main.java.com.huateng.receive.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.com.huateng.commons.config.GN;
import main.java.com.huateng.util.FileUtil;
import main.java.com.huateng.util.TimeUtil;

/**
 * 2013-03-03
 * @author panliguan
 * 读取保存客户端发送过来的报文的记录文件
 */
public class consultReceiveMsg extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final FileUtil fileUtil = new FileUtil();
    TimeUtil tUtil = new TimeUtil();
    
    public consultReceiveMsg() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int serverID = Integer.parseInt(request.getParameter("serverID"));
		String result = "";
        request.setCharacterEncoding("UTF-8");

		switch(serverID){
			case 0:
				break;
			case 1:
				result = GetAllReceiveMsgFromBank();
				break;
			case 2:
				result = GetAllReceiveMsgFromCRM();
				break;
				
			case 3:
				result = GetAllReceiveMsgFromTmall();
				break;
			case 4:
				result = GetAllReceiveMsgFromMmall();
				break;
		}
		
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.write(result);
        out.close();
	}
	
	/**
	 *功能：查看所有银行发送的报文 
	 */
	public String GetAllReceiveMsgFromBank(){
		String mes = "";
		StringBuffer fileName = new StringBuffer();
		
		if(GN.isWinOrLinux == 1){//windows系统
			fileName.append(GN.receivefilepath).append("\\").append("bank").append("\\")
			.append(tUtil.getLocalDate()).append("\\").append(tUtil.getLocalDate()).append(".log");
		}else{//其他系统
			fileName.append(GN.receivefilepath).append("/").append("bank").append("/")
			.append(tUtil.getLocalDate()).append("/").append(tUtil.getLocalDate()).append(".log");
		}
		mes = fileUtil.fileToStr(fileName.toString());
		return mes;
	}
	
	/**
	 *功能：查看所有移动发送的报文 
	 */
	public String GetAllReceiveMsgFromCRM(){
		String mes = "";
		StringBuffer fileName = new StringBuffer();
		
		if(GN.isWinOrLinux == 1){//windows系统
			fileName.append(GN.receivefilepath).append("\\") .append("CRM").append("\\")
			.append(tUtil.getLocalDate()).append("\\").append(tUtil.getLocalDate()).append(".log");
		}else{//其他系统
			fileName.append(GN.receivefilepath).append("/").append("CRM").append("/").append(tUtil.getLocalDate())
			.append("/").append(tUtil.getLocalDate()).append(".log");
		}
		mes = fileUtil.fileToStr(fileName.toString());
		return mes;
	}
	/**
	 *功能：查看天猫发送的报文 
	 */
	public String GetAllReceiveMsgFromTmall(){
		String mes = "";
		StringBuffer fileName = new StringBuffer();
		
		if(GN.isWinOrLinux == 1){//windows系统
			fileName.append(GN.receivefilepath).append("\\") .append("Tmall").append("\\")
			.append(tUtil.getLocalDate()).append("\\").append(tUtil.getLocalDate()).append(".log");
		}else{//其他系统
			fileName.append(GN.receivefilepath).append("/").append("Tmall").append("/").append(tUtil.getLocalDate())
			.append("/").append(tUtil.getLocalDate()).append(".log");
		}
		mes = fileUtil.fileToStr(fileName.toString());
		return mes;
	}
	/**
	 *功能：查看移动商城发送的报文 
	 */
	public String GetAllReceiveMsgFromMmall(){
		String mes = "";
		StringBuffer fileName = new StringBuffer();
		
		if(GN.isWinOrLinux == 1){//windows系统
			fileName.append(GN.receivefilepath).append("\\") .append("Mmall").append("\\")
			.append(tUtil.getLocalDate()).append("\\").append(tUtil.getLocalDate()).append(".log");
		}else{//其他系统
			fileName.append(GN.receivefilepath).append("/").append("Mmall").append("/").append(tUtil.getLocalDate())
			.append("/").append(tUtil.getLocalDate()).append(".log");
		}
		mes = fileUtil.fileToStr(fileName.toString());
		return mes;
	}
}
