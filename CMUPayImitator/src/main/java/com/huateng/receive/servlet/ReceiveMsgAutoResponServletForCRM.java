package main.java.com.huateng.receive.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.huateng.bundle.PropertyBundle;
import com.huateng.security.adapter.SecurityHandle;

import main.java.com.huateng.commons.config.GN;
import main.java.com.huateng.util.DateUtil;
import main.java.com.huateng.util.EnDecryptUtil;
import main.java.com.huateng.util.FileUtil;
import main.java.com.huateng.util.StringUtils;
import main.java.com.huateng.util.TimeUtil;
import main.java.com.huateng.util.UUIDGenerator;
import main.java.com.huateng.util.XmlDomImp;
import main.java.com.huateng.util.IDGenerator;
import main.java.com.huateng.util.XmlStringUtil;

/**
 * @date 2013-03-02
 * @author panlg 模拟省RCM 功能：作为服务端，接收发送过来的信息，并把接收到的报文保存如文件
 *         然后解析报文的业务码和交易流水，并根据此业务码和交易流水，查找出相应的应答报文，并自动返回
 *         模拟省份接收消息
 */
public class ReceiveMsgAutoResponServletForCRM extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final FileUtil fUtil = new FileUtil();
	private static Map<String, String> rspFileContent = new HashMap<String, String>();
	private final Logger log = Logger.getLogger(ReceiveMsgAutoResponServletForCRM.class);
	private static final XmlDomImp xmldom = new XmlDomImp();
	private static final TimeUtil tUtil = new TimeUtil();
	public ReceiveMsgAutoResponServletForCRM() {
		super();
	}

	/**
	 * 取得系统绝对路径
	 * 
	 * @param request
	 * @return
	 */
	public String sysPath(HttpServletRequest request) {
		return request.getSession().getServletContext().getRealPath("/");
	}
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		StringBuffer pathName = new StringBuffer();
		StringBuffer respFilePath = new StringBuffer();
		StringBuffer respFileName = new StringBuffer();
		StringBuffer fileName = new StringBuffer();
		StringBuffer receiveFileLog = new StringBuffer();
		
		TimeUtil tUtil = new TimeUtil();
		String transIDO = "";// 请求报文中的报文头TransIDO
		String transIDOTime = "";// 请求报文中的报文头TransIDOTime
		String transactionID = ""; // 请求报文体中的流水号TransactionID
		String sessionID = "";// 秋秋报文头中的SessionID
		String transIDHTime = DateUtil.getDateyyyyMMddHHmmss();// 落地方(仿真)时间，取系统时间
		String transIDH = IDGenerator.genTransIDH();// 落地方(仿真)交易流水
		String provCode = "";
		String transCode = "";
		String actionDate = tUtil.getLocalDate();
		String subID = "";
		String routeValue = "";
		String routeType = "";
		//String origDomain = "";
		
		pathName.append(GN.receivefilepath).append(File.separator).append("CRM").append(File.separator)
		.append(tUtil.getLocalDate()); // 获取当天日期作为文件路径名，格式：YYYYMMDD
		fileName.append(pathName).append(File.separator).append(tUtil.getLocalDatetime()).append(".txt"); // 获取时间戳作为文件名，一个文件保存一条报文记录，放在YYYYMMDD目录下
		receiveFileLog.append(pathName).append(File.separator).append(tUtil.getLocalDate()).append(".log"); // 每天创建一个YYYYMMDD.log文件，用来保存当然左右接收到的报文记录

		fUtil.createDir(pathName.toString());
		fUtil.createFile(receiveFileLog.toString());

		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		Map<String, String> reqMsgMap = new HashMap<String, String>();
		for (Object object : items) {
			FileItem fileItem = (FileItem) object;
			if (fileItem.isFormField()) {
				reqMsgMap.put(fileItem.getFieldName(),
						fileItem.getString("utf-8"));// 如果你页面编码是utf-8的
				if (fileItem.getFieldName().equals("xmlhead")) {
					respFileName.append(xmldom.getgrdsonNdVal(fileItem.getInputStream(), "BIPType", "BIPCode"))
					.append("_").append(xmldom.getgrdsonNdVal(fileItem.getInputStream(),"BIPType", "ActivityCode"))
					.append(".xml");
				}
			}
		}
		String reqHead = reqMsgMap.get("xmlhead");
		String reqBody = reqMsgMap.get("xmlbody");
		String decodeFlag = PropertyBundle.getConfig("decodeFlag");
		transIDO = XmlStringUtil.parseNodeValueFromXml("<TransIDO>",
				"</TransIDO>", reqHead);
		log.debug("请求流水号:" + transIDO);
		transIDOTime = XmlStringUtil.parseNodeValueFromXml("<TransIDOTime>",
				"</TransIDOTime>", reqHead);
		log.debug("请求时间:" + transIDOTime);
		sessionID = XmlStringUtil.parseNodeValueFromXml("<SessionID>",
				"</SessionID>", reqHead);
		log.debug("请求会话ID:" + sessionID);
		provCode = XmlStringUtil.parseNodeValueFromXml("<MsgSender>",
				"</MsgSender>", reqHead);
		log.debug("请求省份:" + provCode);
		transCode = XmlStringUtil.parseNodeValueFromXml("<ActivityCode>",
				"</ActivityCode>", reqHead);
		log.debug("请求机构代码:" + transCode);
		routeValue = XmlStringUtil.parseNodeValueFromXml("<RouteValue>",
				"</RouteValue>", reqHead);
		log.debug("请求routeValue:" + routeValue);
		routeType = XmlStringUtil.parseNodeValueFromXml("<RouteType>",
				"</RouteType>", reqHead);
		log.debug("请求routeType:" + routeType);
		
		subID = XmlStringUtil.parseNodeValueFromXml("<SubID>",
				"</SubID>", reqBody);
		
		log.debug("请求subID:" + subID);

		String pin = XmlStringUtil.parseNodeValueFromXml("<![CDATA[","]]>",reqBody);
		log.debug("接收到的处理前报文CDATA体："+pin);
        if(reqHead.isEmpty()==false&&reqBody.isEmpty()==false&&EnDecryptUtil.isDecrypt(reqHead)==true&&"open".equals(decodeFlag)){
					try {
						String url = PropertyBundle.getConfig("CRMSecurityURL");
		                HessianProxyFactory hessianFactory = new HessianProxyFactory(); 
		                SecurityHandle securityHandle = (SecurityHandle) hessianFactory.create(SecurityHandle.class, url); 
		                pin = securityHandle.decryptPIN(pin);
		                log.debug("接收到的解密后报文CDATA体："+pin);
					} catch (Exception e) {
						log.debug("解密省报文异常");
				}
			}
		
		transactionID = XmlStringUtil.parseNodeValueFromXml("<TransactionID>","</TransactionID>", pin);
		log.debug("请求操作流水号:" + transactionID);

		respFilePath.append(GN.CRMtemprepfilepath).append(File.separator).append(respFileName);
		String responseMsg = ""; // 应答报文内容

		String StressTest = PropertyBundle.getConfig("StressTest");
		if("true".equals(StressTest)){
			/*
			 * 加上同步，用于压力测试
			 * 非压力测试，不能加，修改返回报文后，时时读取
			 */
			synchronized (rspFileContent)
			{
				if (rspFileContent.containsKey(respFilePath.toString()))
				{
					responseMsg = rspFileContent.get(respFilePath.toString());
				}
				else
				{
					responseMsg = fUtil.fileToStrUTF8(respFilePath.toString());
					rspFileContent.put(respFilePath.toString(), responseMsg);
				}
			}
		}else{
			//正常情况下使用
			responseMsg = fUtil.fileToStrUTF8(respFilePath.toString());
		}
		
		//去掉格式
		responseMsg = StringUtils.replaceBlank(responseMsg);
		responseMsg = responseMsg.replaceAll("xmlversion","xml version");
		responseMsg = responseMsg.replaceAll("encoding=", " encoding=");
		
		// 原报文头中的信息
		responseMsg = XmlStringUtil.relaceNodeContent("<TransIDO>",
				"</TransIDO>", transIDO, responseMsg);
		responseMsg = XmlStringUtil.relaceNodeContent("<TransIDOTime>",
				"</TransIDOTime>", transIDOTime, responseMsg);
		responseMsg = XmlStringUtil.relaceNodeContent("<SessionID>",
				"</SessionID>", sessionID, responseMsg);
		responseMsg = XmlStringUtil.relaceNodeContent("<TransactionID>",
				"</TransactionID>", transactionID, responseMsg);
		// 仿真生成的信息
		responseMsg = XmlStringUtil.relaceNodeContent("<TransIDH>",
				"</TransIDH>", transIDH, responseMsg);
		responseMsg = XmlStringUtil.relaceNodeContent("<TransIDHTime>",
				"</TransIDHTime>", transIDHTime, responseMsg);
		responseMsg = XmlStringUtil.relaceNodeContent("<ActionDate>",
				"</ActionDate>", actionDate, responseMsg);
		responseMsg = XmlStringUtil.relaceNodeContent("<SubID>",
				"</SubID>", subID, responseMsg);
		responseMsg = XmlStringUtil.relaceNodeContent("<RouteValue>",
				"</RouteValue>", routeValue, responseMsg);
		responseMsg = XmlStringUtil.relaceNodeContent("<RouteType>",
				"</RouteType>", routeType, responseMsg);
		responseMsg = XmlStringUtil.relaceNodeContent("<SettleDate>",
				"</SettleDate>", DateUtil.getDateyyyyMMdd(), responseMsg);
		log.debug("仿真返回拼装报文:"+responseMsg);
		//若文件名为BIP1A153_T1000155.xml(银行预签约返回报文)，则第二个sessionid也需要替换
		if("BIP1A153_T1000155.xml".equals(respFileName.toString())){
			responseMsg = XmlStringUtil.relaceNodeContentAfter("<SessionID>",
					"</SessionID>", "<![CDATA[", UUIDGenerator.randomNum(20), responseMsg);
		}
		
		String bs = "<![CDATA[";
		String be = "]]>";
		String rspbody = XmlStringUtil.parseNodeValueFromXml(bs, be,responseMsg);
		String encodeFlag = PropertyBundle.getConfig("encodeFlag");
		try {
		 if(responseMsg.isEmpty()==false&&EnDecryptUtil.isEncrypt(responseMsg)==true&&"open".equals(encodeFlag)){
						try {
							String url = PropertyBundle.getConfig("CRMSecurityURL");
			                HessianProxyFactory hessianFactory = new HessianProxyFactory(); 
			                SecurityHandle securityHandle = (SecurityHandle) hessianFactory.create(SecurityHandle.class, url); 
			                log.debug("返回报文加密前:"+rspbody);
			                rspbody = securityHandle.encryptPIN(rspbody);
			                log.debug("返回报文加密后:"+rspbody);
						} catch (Exception e) {
							log.debug("加密省报文异常");
						}
					}
			responseMsg = XmlStringUtil.relaceNodeContent(bs, be, rspbody,responseMsg);
			
		} catch (Exception e) {
			log.debug("加密报文体异常");
		}
		log.debug("仿真发送的报文："+responseMsg);
		fUtil.strToFile(reqMsgMap.get("xmlhead").toString(), fileName.toString(), true);
		fUtil.strToFile(reqMsgMap.get("xmlbody").toString(), fileName.toString(), true);
		StringBuffer msgheader = null;
		msgheader = new StringBuffer();
		msgheader.append("\n\n").append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n")
		.append("报文接收时间：").append(tUtil.getLocalDatetime_1()).append("\n")
		.append("报文保存地址：").append(fileName).append("\n")
		.append("报文发起方的IP和端口：").append(request.getRemoteAddr()).append(":").append(request.getRemotePort())
		.append("\n").append("接收到的报文内容如下：\n");
		fUtil.strToFile(msgheader.toString(), receiveFileLog.toString(), true);
		fUtil.strToFile(reqMsgMap.get("xmlhead").toString(), receiveFileLog.toString(),true);
		fUtil.strToFile(reqMsgMap.get("xmlbody").toString(), receiveFileLog.toString(),true);
		msgheader = new StringBuffer();
		msgheader.append("\n应答报文文件名：").append(respFilePath).append("，报文内容如下：\n");
		fUtil.strToFile(msgheader.toString(), receiveFileLog.toString(), true);
		fUtil.strToFile(responseMsg.toString(), receiveFileLog.toString(), true);
		msgheader = new StringBuffer();
		msgheader.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
		fUtil.strToFile(msgheader.toString(), receiveFileLog.toString(), true);

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(responseMsg);
		out.close();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	private String getRemoteIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
