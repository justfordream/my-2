package main.java.com.huateng.receive.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.huateng.bundle.PropertyBundle;
import com.huateng.remote.sign.service.RemoteService;
import com.huateng.security.adapter.BankSecurityHandle;

import main.java.com.huateng.commons.config.GN;
import main.java.com.huateng.util.DateUtil;
import main.java.com.huateng.util.FileUtil;
import main.java.com.huateng.util.StringUtils;
import main.java.com.huateng.util.TimeUtil;
import main.java.com.huateng.util.UUIDGenerator;
import main.java.com.huateng.util.XmlDomImp;
import main.java.com.huateng.util.IDGenerator;
import main.java.com.huateng.util.XmlStringUtil;

/**
 * @date 2013-03-02
 * @author Administrator 功能：作为服务端，接收移动商城发送过来的信息，并保存文件
 */
public class ReceiveMsgAutoResponServletForMobileShop extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final FileUtil fUtil = new FileUtil();
	private static Map<String, String> rspFileContent = new HashMap<String, String>();
	private final Logger log = Logger.getLogger(ReceiveMsgAutoResponServletForMobileShop.class);
	private static final TimeUtil tUtil = new TimeUtil();
	private static final XmlDomImp xmldom = new XmlDomImp();
	public ReceiveMsgAutoResponServletForMobileShop() {
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

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("UTF-8");
			String reqMsg = "";
			StringBuffer pathName = new StringBuffer();
			StringBuffer fileName = new StringBuffer();
			StringBuffer respFilePath = new StringBuffer();
			String respFileName = "";
			String reqTransID = ""; // 取交易流水号
			StringBuffer receiveFileLog = new StringBuffer();
			// 获取当天日期作为文件路径名，格式：YYYYMMDD
			pathName.append(GN.receivefilepath).append(File.separator).append("Mmall").append(File.separator).append(tUtil.getLocalDate());
			// 获取时间戳作为文件名，一个文件保存一条报文记录，放在YYYYMMDD目录下
			fileName.append(pathName).append(File.separator).append(tUtil.getLocalDatetime()).append(".txt");
			// 每天创建一个YYYYMMDD.log文件，用来保存当然左右接收到的报文记录
			receiveFileLog.append(pathName).append(File.separator).append(tUtil.getLocalDate()).append(".log");

			fUtil.createDir(pathName.toString());
			fUtil.createFile(receiveFileLog.toString());

			reqMsg = request.getParameter("xmldata");
			log.debug("..........仿真接收报文："+reqMsg);
			respFileName = "022051";
//				xmldom.getgrdsonNdVal_String(reqMsg, "Header","ActivityCode");
			System.out.println("respFileName????????????????????????????????:"+respFileName);
			//取移动商城或者商户代码
			String reqkey = XmlStringUtil.parseNodeValueFromXml("<ReqSys>", "</ReqSys>", reqMsg);
			String rcvkey = XmlStringUtil.parseNodeValueFromXml("<RcvSys>", "</RcvSys>", reqMsg);
			String SignFlag =  XmlStringUtil.parseNodeValueFromXml("<SignFlag>", "</SignFlag>", reqMsg);
			log.debug("仿真验签开关:" + SignFlag);
			String SignValue = XmlStringUtil.parseNodeValueFromXml("<SignValue>", "</SignValue>",reqMsg);
			log.debug("仿真签名:" + SignValue);
			String header = XmlStringUtil.parseNodeValueFromXml("<Header>", "</Header>",reqMsg);
			String body = XmlStringUtil.parseNodeValueFromXml("<Body>", "</Body>",reqMsg);
			//取header和body去进行签名
			StringBuffer plainText = new StringBuffer();
			plainText.append("<Header>").append(header).append("</Header>|<Body>").append(body).append("</Body>");
			log.debug("仿真签名原文："+plainText);
			respFilePath.append(GN.mmalltemprepfilepath).append(File.separator).append(respFileName).append(".xml");
			
			String responseMsg = "";
			
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
			
			log.debug("仿真移动商城侧返回报文路径"+respFilePath);
			log.debug("仿真移动商城侧返回报文"+responseMsg);
			//去掉格式
			responseMsg = StringUtils.replaceBlank(responseMsg);
			responseMsg = responseMsg.replaceAll("xmlversion","xml version");
			responseMsg = responseMsg.replaceAll("encoding=", " encoding=");
			
			String checkSignFlagProperty = PropertyBundle.getConfig("checkSignFlag");
			//报文验签开关为，进行验签
			if("1".equals(SignFlag)&&"open".equals(checkSignFlagProperty)){
				String url = PropertyBundle.getConfig("BankSecurityURL");
				log.debug("验签服务器地址："+url);
			    HessianProxyFactory hessianFactory = new HessianProxyFactory(); 
			    RemoteService remoteService = (RemoteService) hessianFactory.create(RemoteService.class, url); 
				boolean flag = remoteService.verify(rcvkey,plainText.toString(),SignValue);
				if(flag == false){
					log.debug("仿真接收报文后验签失败！");
					responseMsg = XmlStringUtil.relaceNodeContent("<RspCode>",
							"</RspCode>", "024A06", responseMsg);
					responseMsg = XmlStringUtil.relaceNodeContent("<RspDesc>",
							"</RspDesc>", "签名验证失败", responseMsg);
				}
			}

			reqTransID = xmldom.getgrdsonNdVal_String(reqMsg, "Header","ReqTransID");
			String reqDateTime = XmlStringUtil.parseNodeValueFromXml(
					"<ReqDateTime>", "</ReqDateTime>", reqMsg);
			String reqDate = "".equals(reqDateTime) ? DateUtil.getDateyyyyMMdd(): reqDateTime.substring(0, 8);
			String rcvDate = DateUtil.getDateyyyyMMdd();
			String rcvDateTime = DateUtil.getDateyyyyMMddHHmmssSSS();
			String rcvTransID = IDGenerator.genRcvTransId();
			//取签名开关
			String signFlag = XmlStringUtil.parseNodeValueFromXml("<SignFlag>", "</SignFlag>", reqMsg);
			responseMsg = XmlStringUtil.relaceNodeContent("<ReqTransID>",
					"</ReqTransID>", reqTransID, responseMsg);
			responseMsg = XmlStringUtil.relaceNodeContent("<ReqDate>",
					"</ReqDate>", reqDate, responseMsg);
			responseMsg = XmlStringUtil.relaceNodeContent("<ReqDateTime>",
					"</ReqDateTime>", reqDateTime, responseMsg);
			responseMsg = XmlStringUtil.relaceNodeContent("<RcvDate>",
					"</RcvDate>", rcvDate, responseMsg);
			responseMsg = XmlStringUtil.relaceNodeContent("<RcvDateTime>",
					"</RcvDateTime>", rcvDateTime, responseMsg);
			responseMsg = XmlStringUtil.relaceNodeContent("<RcvTransID>",
					"</RcvTransID>", rcvTransID, responseMsg);
			responseMsg = XmlStringUtil.relaceNodeContent("<SignFlag>",
					"</SignFlag>", signFlag, responseMsg);
			responseMsg = XmlStringUtil.relaceNodeContent("<SubTime>",
					"</SubTime>", DateUtil.getDateyyyyMMddHHmmss(), responseMsg);
			/**
			 * @time 20131114
			 * */
//		responseMsg = XmlStringUtil.relaceNodeContent("<SubID>",
//				"</SubID>", IDGenerator.getSubIdForBank(reqkey), responseMsg);
			String reqChannel = XmlStringUtil.parseNodeValueFromXml("<ReqChannel>", "</ReqChannel>", reqMsg);
			responseMsg = XmlStringUtil.relaceNodeContent("<SubID>",
					"</SubID>", IDGenerator.getSubIdForBank(rcvkey+reqChannel), responseMsg);
			log.debug("修改返回报文的SubID");
			String key = XmlStringUtil.parseNodeValueFromXml("<RcvSys>", "</RcvSys>", responseMsg);
			
			String signFlagProperty = PropertyBundle.getConfig("signFlag");
			if("1".equals(signFlag)&&"open".equals(signFlagProperty)){
				String header2 = XmlStringUtil.parseNodeValueFromXml("<Header>", "</Header>",responseMsg);
				String body2 = XmlStringUtil.parseNodeValueFromXml("<Body>", "</Body>",responseMsg);
				StringBuffer plainText2 = new StringBuffer();
				plainText2 = plainText2.append("<Header>").append(header2).append("</Header>|<Body>").append(body2).append("</Body>");

				log.debug(".......仿真签名的字符串："+plainText2.toString());
				String url = PropertyBundle.getConfig("CRMSecurityURL");
			    HessianProxyFactory hessianFactory = new HessianProxyFactory(); 
			    RemoteService remoteService = (RemoteService) hessianFactory.create(RemoteService.class, url); 
				String signReturn = remoteService.sign(key,plainText2.toString());
				responseMsg = XmlStringUtil.relaceNodeContent("<SignValue>",
						"</SignValue>", signReturn, responseMsg);
			}
			String bankEncodeFlag = PropertyBundle.getConfig("bankEncodeFlag");
			String bankEncodeActivityCode = PropertyBundle.getConfig("bankEncodeActivityCode");
			String activityCode = XmlStringUtil.parseNodeValueFromXml("<ActivityCode>", "</ActivityCode>", responseMsg);
			if("open".equals(bankEncodeFlag)&&bankEncodeActivityCode.contains(activityCode)==true){
				log.debug(".........移动商城端加密，报文为"+activityCode);
				String userName = XmlStringUtil.parseNodeValueFromXml("<UserName>", "</UserName>", responseMsg);
				String userId =  XmlStringUtil.parseNodeValueFromXml("<UserID>", "</UserID>", responseMsg);
				String bankAcctID =  XmlStringUtil.parseNodeValueFromXml("<BankAcctID>", "</BankAcctID>", responseMsg);
				String url = PropertyBundle.getConfig("BankEncryURL");
			    HessianProxyFactory hessianFactory = new HessianProxyFactory(); 
			    BankSecurityHandle remoteService = (BankSecurityHandle) hessianFactory.create(BankSecurityHandle.class, url); 
			    responseMsg = XmlStringUtil.relaceNodeContent("<UserName>","</UserName>", remoteService.symDecryptPNI(0, true, userName), responseMsg);
			    responseMsg = XmlStringUtil.relaceNodeContent("<UserID>","</UserID>", remoteService.symDecryptPNI(0, true, userId), responseMsg);
			    responseMsg = XmlStringUtil.relaceNodeContent("<BankAcctID>","</BankAcctID>", remoteService.symDecryptPNI(0, true, bankAcctID), responseMsg);
			}
			log.debug("仿真处理后发送报文："+responseMsg);
			fUtil.strToFile(reqMsg, fileName.toString(), true);
			StringBuffer msgheader = null;
			msgheader = new StringBuffer();
			msgheader.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n")
			.append("报文接收时间：").append(tUtil.getLocalDatetime_1()).append("\n")
			.append("报文保存地址：").append(fileName).append("\n")
			.append("报文发起方的IP和端口：").append(request.getRemoteAddr()).append(":")
			.append(request.getRemotePort()).append("\n")
			.append("接收到的报文内容如下：\n");
			
			fUtil.strToFile(msgheader.toString(), receiveFileLog.toString(), true);
			fUtil.strToFile(reqMsg, receiveFileLog.toString(), true);
			msgheader = new StringBuffer();
			msgheader.append("\n应答报文文件名：").append(respFilePath).append("，报文内容如下：\n");
			fUtil.strToFile(msgheader.toString(), receiveFileLog.toString(), true);
			fUtil.strToFile(responseMsg, receiveFileLog.toString(), true);
			msgheader = new StringBuffer();
			msgheader.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
			fUtil.strToFile(msgheader.toString(), receiveFileLog.toString(), true);
			
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(responseMsg);
			out.close();
		} catch (Exception e) {
			log.error("移动商城仿真异常：", e);
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	public static void main(String[] args) {
	}

}
