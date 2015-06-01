package main.java.com.huateng.autoinitmsg.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.huateng.bundle.PropertyBundle;
import main.java.com.huateng.commons.config.GN;
import main.java.com.huateng.util.DateUtil;
import main.java.com.huateng.util.FileUtil;
import main.java.com.huateng.util.TimeUtil;
import main.java.com.huateng.util.UUIDGenerator;
import main.java.com.huateng.util.XmlDomImp;
import main.java.com.huateng.util.IDGenerator;

/**
 * 初始化页面下拉框
 * 
 * @author panlg
 * 
 */
public class InitSelectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final FileUtil fileUtil = new FileUtil();
	XmlDomImp xmldom = new XmlDomImp();

	public InitSelectServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		int serverId = Integer.parseInt(request.getParameter("serverId"));
		String directoryName = request.getParameter("directoryName");
		String parentDir = request.getParameter("parentDir");
		String fileName = request.getParameter("fileName");
		String result = "";

		switch (serverId) {
		case 1:
			result = initBusinessSelect(GN.bankreqtempreqfilepath);
			break;
		case 2:
			result = initBankMsgSelect(GN.bankreqtempreqfilepath, directoryName);
			break;
		case 3:
			result = getBankSendMes(parentDir, fileName);
			break;
		case 5:
			result = initBusinessSelect(GN.CRMreqtempreqfilepath);
			break;
		case 6:
			result = initCRMMsgSelect(GN.CRMreqtempreqfilepath, directoryName);
			break;
		case 7:
			result = getCRMSendMes(parentDir, fileName);
			break;
		case 8:
			result = getBankAddress();
			break;
		case 9:
			result = getCRMAddress();
			break;
		case 10:
			result = initBusinessSelect(GN.mmallreqtempreqfilepath);
			break;
		case 11:
			result = getAddress("MMallAddressURL");
			break;
		case 12:
			result = initMsgSelect(GN.mmallreqtempreqfilepath,directoryName);
			break;
		case 13:
			result = getSendMes(parentDir, fileName,GN.mmallreqtempreqfilepath);
			break;
		case 14:
			result = initTMallMsgSelect(GN.tmallreqtempreqfilepath,directoryName);
			break;
		case 15:
			result = getAddress("TMallAddressURL");
			break;
		case 16:
			result = initBusinessSelect(GN.tmallreqtempreqfilepath);
			break;
		case 17:
			result = getSendTmallMes(parentDir, fileName,GN.tmallreqtempreqfilepath);
			break;
		}
			

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(result);
		out.close();
	}

	/**
	 * 页面点击业务类型下拉框触发初始化
	 * 
	 * @param tempreqfilepath
	 *            请求报文业务类型目录
	 * @return
	 */
	public static String initBusinessSelect(String bankreqtempreqfilepath) {
		int i = 0;
		StringBuffer result = new StringBuffer();
		Map<String, String> al_fileList = null;
		Set<String> set = null;

		al_fileList = fileUtil.getFileNameAndPath(bankreqtempreqfilepath);
		set = al_fileList.keySet();
		String [] sortTemp = new String[set.size()];
		
		Object [] sortTemp1 = set.toArray();
		for (int j = 0; j < set.size(); j++) {
			Integer no = Integer.parseInt(sortTemp1[j].toString().split("\\.")[0]);

			sortTemp[no-1] = sortTemp1[j].toString();
			
		}
		result.append("[");
		for (int j = 0; j < sortTemp.length; j++) {
			result.append("[");
			result.append("'" + sortTemp[j] + "'");
			result.append(",");
			result.append("'" + sortTemp[j] + "'");
			result.append("]");

			if (j < sortTemp.length) {
				result.append(",");
			}
		}
		
		result.append("]\n");

//		System.out.println("json = \n" + result.toString());
		return result.toString();
	}


	/**
	 * 初始化发送银行侧选择报文下拉框
	 * 
	 * @param directoryName
	 * @return
	 */
	public String initBankMsgSelect(String tempreqfilepath, String directoryName) {
		int i = 0;
		StringBuffer result = new StringBuffer();
		Map<String, String> al_fileList = null;
		Set<String> set = null;
//		System.out.println("directoryName ==== " + directoryName);

		al_fileList = fileUtil.getFileNameAndFile(tempreqfilepath
				+ File.separator + directoryName);
		set = al_fileList.keySet();

		result.append("[");
		for (String s : set) {
			i++;
			result.append("[");
			result.append("'" + s + "'");
			result.append(",");
			result.append("'" + s + "'");
			result.append("]");

			if (i < set.size()) {
				result.append(",");
			}
		}
		result.append("]\n");

//		System.out.println("json = \n" + result.toString());

		return result.toString();
	}
	
	/**
	 * 初始化发送银行侧选择报文下拉框
	 * 
	 * @param directoryName
	 * @return
	 */
	public String initMsgSelect(String tempreqfilepath, String directoryName) {
		int i = 0;
		StringBuffer result = new StringBuffer();
		Map<String, String> al_fileList = null;
		Set<String> set = null;
//		System.out.println("directoryName ==== " + directoryName);

		al_fileList = fileUtil.getFileNameAndFile(tempreqfilepath
				+ File.separator + directoryName);
		set = al_fileList.keySet();

		result.append("[");
		for (String s : set) {
			i++;
			result.append("[");
			result.append("'" + s + "'");
			result.append(",");
			result.append("'" + s + "'");
			result.append("]");

			if (i < set.size()) {
				result.append(",");
			}
		}
		result.append("]\n");

//		System.out.println("json = \n" + result.toString());

		return result.toString();
	}
	/**
	 * 初始化发送天猫侧选择报文下拉框
	 * 
	 * @param directoryName
	 * @return
	 */
	public String initTMallMsgSelect(String tempreqfilepath, String directoryName) {
		int i = 0;
		StringBuffer result = new StringBuffer();
		Map<String, String> al_fileList = null;
		Set<String> set = null;
//		System.out.println("directoryName ==== " + directoryName);

		al_fileList = fileUtil.getFileNameAndFile(tempreqfilepath
				+ File.separator + directoryName);
		set = al_fileList.keySet();

		result.append("[");
		for (String s : set) {
			i++;
			result.append("[");
			result.append("'" + s + "'");
			result.append(",");
			result.append("'" + s + "'");
			result.append("]");

			if (i < set.size()) {
				result.append(",");
			}
		}
		result.append("]\n");

//		System.out.println("json = \n" + result.toString());

		return result.toString();
	}
	/**
	 * 读取发送银行侧请求报文模板的内容
	 * 
	 * @param parentDir
	 *            fileName文件的上一级目录
	 * @param fileName
	 *            需要读取的文件名
	 * @return
	 */
	public String getBankSendMes(String parentDir, String fileName) {
		String mes = "";
		TimeUtil tUtil = new TimeUtil();
		StringBuffer filePath = new StringBuffer();
		filePath.append(GN.bankreqtempreqfilepath).append(File.separator).append(parentDir)
		.append(File.separator).append(fileName);
		mes = fileUtil.fileToStrUTF8(filePath.toString());
		int beginIndex = mes.indexOf("<ReqTransID>");
		int endIndex = mes.indexOf("</ReqTransID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			mes = mes.replace(mes.substring(beginIndex+12, endIndex),IDGenerator.getReqTransID(fileName));
		}
		 beginIndex = mes.indexOf("<ReqDate>");
		 endIndex = mes.indexOf("</ReqDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			mes = mes.replace(mes.substring(beginIndex+9, endIndex),tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<ReqDateTime>");
		 endIndex = mes.indexOf("</ReqDateTime>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			mes = mes.replace(mes.substring(beginIndex+13, endIndex),tUtil.getLocalDatetimeDetail().substring(0, 17));
		}
		 beginIndex = mes.indexOf("<OriReqDate>");
		 endIndex = mes.indexOf("</OriReqDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			mes = mes.replace(mes.substring(beginIndex+12, endIndex),tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<PrintDate>");
		 endIndex = mes.indexOf("</PrintDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			mes = mes.replace(mes.substring(beginIndex+11, endIndex),tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<ActionDate>");
		 endIndex = mes.indexOf("</ActionDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			mes = mes.replace(mes.substring(beginIndex+12, endIndex),tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<SubTime>");
		 endIndex = mes.indexOf("</SubTime>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			mes = mes.replace(mes.substring(beginIndex+9, endIndex),tUtil.getLocalDatetimeDetail().substring(0, 14));
		}
		beginIndex = mes.indexOf("<SubID>");
		endIndex = mes.indexOf("</SubID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改时间后，返回给页面
			mes = mes.replace(
					mes.substring(beginIndex+7, endIndex),IDGenerator.getSubIdForBank("01","000951"));
		}
		return mes;
	}
	
	/**
	 * 读取发送移动商城侧请求报文模板的内容
	 * 
	 * @param parentDir
	 *            fileName文件的上一级目录
	 * @param fileName
	 *            需要读取的文件名
	 * @return
	 */
	public String getSendMes(String parentDir, String fileName,String path) {
		String mes = "";
		TimeUtil tUtil = new TimeUtil();
		StringBuffer filePath = new StringBuffer();
		filePath.append(path).append(File.separator).append(parentDir)
		.append(File.separator).append(fileName);
		mes = fileUtil.fileToStrUTF8(filePath.toString());
		
		String str="";
		//修改移动商城认证支付请求报文
		int beginIndex=mes.indexOf("<ActivityCode>");
		int endIndex = mes.indexOf("</ActivityCode>");
		if((beginIndex > -1) && (endIndex > -1)){
			str = mes.substring(beginIndex+14, endIndex);
			if(str.equals("012063")){
				/*//修改OrderID      V32
				 beginIndex = mes.indexOf("<OrderID>");
				 endIndex = mes.indexOf("</OrderID>");
				 if ((beginIndex > -1) && (endIndex > -1)) {
					 str = "<OrderID>"+mes.substring(beginIndex+9, endIndex);
					 mes = mes.replace(str,"<OrderID>"+UUIDGenerator.randomNum(32));
				 }*/
				 //修改订单时间
				 beginIndex = mes.indexOf("<OrderTime>");
				 endIndex = mes.indexOf("</OrderTime>");
				 if ((beginIndex > -1) && (endIndex > -1)) {
					 str = "<OrderTime>"+mes.substring(beginIndex+11, endIndex);
					 mes = mes.replace(str,"<OrderTime>"+DateUtil.getDateyyyyMMddHHmmss());
				 }
				 //修改ProdID
				 beginIndex = mes.indexOf("<ProdID>");
				 endIndex = mes.indexOf("</ProdID>");
				 if ((beginIndex > -1) && (endIndex > -1)) {
					 str = "<ProdID>"+mes.substring(beginIndex+8, endIndex);
					 mes = mes.replace(str,"<ProdID>"+UUIDGenerator.randomNum(32));
				 }
				 //订单超时时间
				 beginIndex=mes.indexOf("PayTimeoutTime");
				 endIndex=mes.indexOf("PayTimeoutTime");
				 if ((beginIndex > -1) && (endIndex > -1)) {
					 str = "<PayTimeoutTime>"+mes.substring(beginIndex+8, endIndex);
					 mes = mes.replace(str,"<PayTimeoutTime>"+DateUtil.getDateyyyyMMddHHmmss()+60000);
				 }
				 
			}
			if(str.equals("012062")){
				//修改OrderTime
				 beginIndex = mes.indexOf("<OrderTime>");
				 endIndex = mes.indexOf("</OrderTime>");
				 if ((beginIndex > -1) && (endIndex > -1)) {
					 str = "<OrderTime>"+mes.substring(beginIndex+11, endIndex);
					 mes = mes.replace(str,"<OrderTime>"+DateUtil.getDateyyyyMMddHHmmss());
				 }
			}
		}
		 beginIndex = mes.indexOf("<ReqTransID>");
		 endIndex = mes.indexOf("</ReqTransID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<ReqTransID>"+mes.substring(beginIndex+12, endIndex);
			mes = mes.replace(str,"<ReqTransID>"+IDGenerator.getReqTransID(fileName));
		}
		 beginIndex = mes.indexOf("<ReqDate>");
		 endIndex = mes.indexOf("</ReqDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<ReqDate>"+mes.substring(beginIndex+9, endIndex);
			mes = mes.replace(str,"<ReqDate>"+tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<ReqDateTime>");
		 endIndex = mes.indexOf("</ReqDateTime>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<ReqDateTime>"+mes.substring(beginIndex+13, endIndex);
			mes = mes.replace(str,"<ReqDateTime>"+tUtil.getLocalDatetimeDetail().substring(0, 17));
		}
		 beginIndex = mes.indexOf("<OriReqDate>");
		 endIndex = mes.indexOf("</OriReqDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<OriReqDate>"+mes.substring(beginIndex+12, endIndex);
			mes = mes.replace(str,"<OriReqDate>"+tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<PrintDate>");
		 endIndex = mes.indexOf("</PrintDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<PrintDate>"+mes.substring(beginIndex+11, endIndex);
			mes = mes.replace(str,"<PrintDate>"+tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<ActionDate>");
		 endIndex = mes.indexOf("</ActionDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<ActionDate>"+mes.substring(beginIndex+12, endIndex);
			mes = mes.replace(str,"<ActionDate>"+tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<SubTime>");
		 endIndex = mes.indexOf("</SubTime>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<SubTime>"+mes.substring(beginIndex+9, endIndex);
			mes = mes.replace(str,"<SubTime>"+tUtil.getLocalDatetimeDetail().substring(0, 14));
		}
		beginIndex = mes.indexOf("<OrderId>");
		endIndex = mes.indexOf("</OrderId>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<OrderId>"+mes.substring(beginIndex+9, endIndex);
			mes = mes.replace(str,"<OrderId>"+UUIDGenerator.generateUUID());
		}
		 beginIndex=mes.indexOf("<ActivityCode>");
		 endIndex = mes.indexOf("</ActivityCode>");
		if((beginIndex > -1) && (endIndex > -1)){
			str = mes.substring(beginIndex+14, endIndex);
			if(str.equals("012063")){
				//修改OrderID      V32
				 beginIndex = mes.indexOf("<OrderID>");
				 endIndex = mes.indexOf("</OrderID>");
				 if ((beginIndex > -1) && (endIndex > -1)) {
					 str = "<OrderID>"+mes.substring(beginIndex+9, endIndex);
					 mes = mes.replace(str,"<OrderID>"+UUIDGenerator.randomNum(32));
				 }
			}
		}
		return mes;
	}
	/**
	 * 读取发送天猫侧请求报文模板的内容
	 * 
	 * @param parentDir
	 *            fileName文件的上一级目录
	 * @param fileName
	 *            需要读取的文件名
	 * @return
	 */
	public String getSendTmallMes(String parentDir, String fileName,String path) {
		String mes = "";
		TimeUtil tUtil = new TimeUtil();
		StringBuffer filePath = new StringBuffer();
		filePath.append(path).append(File.separator).append(parentDir)
		.append(File.separator).append(fileName);
		mes = fileUtil.fileToStrUTF8(filePath.toString());
		
		String str="";
		int beginIndex = mes.indexOf("<ReqTransID>");
		int endIndex = mes.indexOf("</ReqTransID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<ReqTransID>"+mes.substring(beginIndex+12, endIndex);
			mes = mes.replace(str,"<ReqTransID>"+IDGenerator.getReqTransID(fileName));
		}
		 beginIndex = mes.indexOf("<ReqDate>");
		 endIndex = mes.indexOf("</ReqDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<ReqDate>"+mes.substring(beginIndex+9, endIndex);
			mes = mes.replace(str,"<ReqDate>"+tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<ReqDateTime>");
		 endIndex = mes.indexOf("</ReqDateTime>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<ReqDateTime>"+mes.substring(beginIndex+13, endIndex);
			mes = mes.replace(str,"<ReqDateTime>"+tUtil.getLocalDatetimeDetail().substring(0, 17));
		}
		 beginIndex = mes.indexOf("<OrderID>");
		 endIndex = mes.indexOf("</OrderID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<OrderID>"+mes.substring(beginIndex+9, endIndex);
			mes = mes.replace(str,"<OrderID>"+tUtil.getLocalDatetimeDetail()+System.currentTimeMillis()+"00");
		}
		beginIndex = mes.indexOf("<PayTransID>");
		 endIndex = mes.indexOf("</PayTransID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<PayTransID>"+mes.substring(beginIndex+12, endIndex);
			mes = mes.replace(str,"<PayTransID>"+IDGenerator.getTransactionId());
		}
		beginIndex = mes.indexOf("<ProdShelfNO>");
		 endIndex = mes.indexOf("</ProdShelfNO>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<ProdShelfNO>"+mes.substring(beginIndex+13, endIndex);
			mes = mes.replace(str,"<ProdShelfNO>"+IDGenerator.getTransactionId());
		}
		beginIndex = mes.indexOf("<OriReqDate>");
		 endIndex = mes.indexOf("</OriReqDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<OriReqDate>"+mes.substring(beginIndex+12, endIndex);
			mes = mes.replace(str,"<OriReqDate>"+tUtil.getLocalDate());
		}
		beginIndex = mes.indexOf("<OriReqDate>");
		 endIndex = mes.indexOf("</OriReqDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<OriReqDate>"+mes.substring(beginIndex+12, endIndex);
			mes = mes.replace(str,"<OriReqDate>"+tUtil.getLocalDate());
		}
		 /*beginIndex = mes.indexOf("<PrintDate>");
		 endIndex = mes.indexOf("</PrintDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<PrintDate>"+mes.substring(beginIndex+11, endIndex);
			mes = mes.replace(str,"<PrintDate>"+tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<ActionDate>");
		 endIndex = mes.indexOf("</ActionDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<ActionDate>"+mes.substring(beginIndex+12, endIndex);
			mes = mes.replace(str,"<ActionDate>"+tUtil.getLocalDate());
		}
		 beginIndex = mes.indexOf("<SubTime>");
		 endIndex = mes.indexOf("</SubTime>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<SubTime>"+mes.substring(beginIndex+9, endIndex);
			mes = mes.replace(str,"<SubTime>"+tUtil.getLocalDatetimeDetail().substring(0, 14));
		}*/
		beginIndex = mes.indexOf("<OrderID>");
		 endIndex = mes.indexOf("</OrderID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			str = "<OrderID>"+mes.substring(beginIndex+9, endIndex);
			mes = mes.replace(str,"<OrderID>"+IDGenerator.getOrderId());
		}
		return mes;
	}
	/**
	 * 初始化发送CRM侧选择报文下拉框
	 * 
	 * @param directoryName
	 * @return
	 */
	public String initCRMMsgSelect(String tempreqfilepath, String directoryName) {
		int i = 0;
		StringBuffer result = new StringBuffer();
		Map<String, String> al_fileList = null;
		Set<String> set = null;
//		System.out.println("directoryName ==== " + directoryName);

		al_fileList = fileUtil.getFileNameAndFile(tempreqfilepath+ File.separator + directoryName);
		set = al_fileList.keySet();

		result.append("[");
		for (String s : set) {
			i++;
				result.append("[");
				result.append("'" + s + "'");
				result.append(",");
				result.append("'" + s + "'");
				result.append("]");

				if (i < set.size()) {
					result.append(",");
				}
		}
		result.append("]\n");

//		System.out.println("json = \n" + result.toString());

		return result.toString();
	}

	/**
	 * 读取发送CRM侧请求报文模板的内容
	 * 
	 * @param parentDir
	 *            fileName文件的上一级目录
	 * @param fileName
	 *            需要读取的文件名
	 * @return
	 */
	public String getCRMSendMes(String parentDir, String fileName) {
//		System.out.println("parentDir = " + parentDir + "   fileName = "+ fileName);
		String headMes = ""; // 报文空内容
		String bodyMes = ""; // 报文体内容
		String messageTotal = "";//全部报文体
		StringBuffer msg = new StringBuffer();
		TimeUtil tUtil = new TimeUtil();
		StringBuffer filePath = new StringBuffer();
		filePath.append(GN.CRMreqtempreqfilepath).append(File.separator).append(parentDir).append(File.separator).append(fileName);
		messageTotal = fileUtil.fileToStrUTF8(filePath.toString());
		
		//报文头到</InterBOSS>结束
		int headMesIndex = messageTotal.indexOf("</InterBOSS>", 1);
		//报文体从第二个<?xml开始
		int bodyMesIndex = messageTotal.indexOf("<?xml", 2);
		headMes = messageTotal.substring(0, headMesIndex+12);
		bodyMes = messageTotal.substring(bodyMesIndex,messageTotal.length());
		
		int beginIndex = headMes.indexOf("<TransIDO>");
		int endIndex = headMes.indexOf("</TransIDO>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			headMes = headMes.replace(headMes.substring(beginIndex+10, endIndex),IDGenerator.getTransIDO(fileName));
		}

		beginIndex = headMes.indexOf("<SessionID>");
		endIndex = headMes.indexOf("</SessionID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改交易流水号后，返回给页面
			headMes = headMes.replace(
					headMes.substring(beginIndex+11, endIndex),IDGenerator.getSessionID());
		}
		beginIndex = headMes.indexOf("<TransIDOTime>");
		endIndex = headMes.indexOf("</TransIDOTime>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改时间后，返回给页面
			headMes = headMes.replace(
					headMes.substring(beginIndex+14, endIndex),tUtil.getLocalDatetimeDetail().substring(0, 14));
		}
		beginIndex = headMes.indexOf("<CutOffDay>");
		endIndex = headMes.indexOf("</CutOffDay>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改时间后，返回给页面
			headMes = headMes.replace(
					headMes.substring(beginIndex+11, endIndex),tUtil.getLocalDate());
		}
		beginIndex = headMes.indexOf("<OSNTime>");
		endIndex = headMes.indexOf("</OSNTime>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改时间后，返回给页面
			headMes = headMes.replace(
					headMes.substring(beginIndex+9, endIndex),tUtil.getLocalDatetimeDetail().substring(0, 14));
		}
		beginIndex = bodyMes.indexOf("<ActionDate>");
		endIndex = bodyMes.indexOf("</ActionDate>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改时间后，返回给页面
			bodyMes = bodyMes.replace(
					bodyMes.substring(beginIndex+12, endIndex),tUtil.getLocalDate());
		}
		beginIndex = bodyMes.indexOf("<SubTime>");
		endIndex = bodyMes.indexOf("</SubTime>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改时间后，返回给页面
			bodyMes = bodyMes.replace(
					bodyMes.substring(beginIndex+9, endIndex),tUtil.getLocalDatetimeDetail().substring(0, 14));
		}
		beginIndex = bodyMes.indexOf("<TransactionID>");
		endIndex = bodyMes.indexOf("</TransactionID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改时间后，返回给页面
			int beginIndex2 = headMes.indexOf("<MsgSender>");
			int endIndex2 = headMes.indexOf("</MsgSender>");
//			String msgSender = headMes.substring(beginIndex2+11, endIndex2);
			bodyMes = bodyMes.replace(
					bodyMes.substring(beginIndex+15, endIndex),IDGenerator.getTransactionId());
		}
		beginIndex = bodyMes.indexOf("<SubID>");
		endIndex = bodyMes.indexOf("</SubID>");
		if ((beginIndex > -1) && (endIndex > -1)) {
			// 程序从模板里读出请求报文模板，修改时间后，返回给页面
			bodyMes = bodyMes.replace(
					bodyMes.substring(beginIndex+7, endIndex),IDGenerator.getSubIdForBank("01","000951"));
		}
		filePath.append(GN.CRMreqtempreqfilepath).append(File.separator).append(parentDir).append(File.separator).append(fileName);
		//报文头和报文体中间以"_|body|_"，html会处理
		msg.append(headMes).append("_|body|_").append(bodyMes);
		return msg.toString();
		
	}
	/**
	 * 从配置文件中读取银行前置地址
	 * 若有多个地址，则以“，”进行分割
	 */
	public String getBankAddress(){    //aaa
		StringBuffer result = new StringBuffer();
		String s = PropertyBundle.getConfig("BankAddressURL");
		String[] s1 = s.split(",");
		
		result.append("[");
		for (int i=0;i<s1.length;i++) {
				result.append("[");
				result.append("'" + s1[i] + "'");
				result.append(",");
				result.append("'" + s1[i] + "'");
				result.append("]");

				if (i < s1.length) {
					result.append(",");
				}
		}
		result.append("]\n");

//		System.out.println("json = \n" + result.toString());
		return result.toString();
	}
	
	/**
	 * 从配置文件中读取银行前置地址
	 */
	public String getCRMAddress(){
		StringBuffer result = new StringBuffer();
		String s = PropertyBundle.getConfig("CRMAddressURL");
		String[] s1 = s.split(",");
		
		result.append("[");
		for (int i=0;i<s1.length;i++) {
				result.append("[");
				result.append("'" + s1[i] + "'");
				result.append(",");
				result.append("'" + s1[i] + "'");
				result.append("]");

				if (i < s1.length) {
					result.append(",");
				}
		}
		result.append("]\n");

//		System.out.println("json = \n" + result.toString());
		return result.toString();
	}
	
	/**
	 * 从配置文件中读取银行前置地址
	 */
	public String getAddress(String address){
		StringBuffer result = new StringBuffer();
		String s = PropertyBundle.getConfig(address);
		String[] s1 = s.split(",");
		
		result.append("[");
		for (int i=0;i<s1.length;i++) {
				result.append("[");
				result.append("'" + s1[i] + "'");
				result.append(",");
				result.append("'" + s1[i] + "'");
				result.append("]");

				if (i < s1.length) {
					result.append(",");
				}
		}
		result.append("]\n");

//		System.out.println("json = \n" + result.toString());
		return result.toString();
	}
}
