package main.java.com.huateng.sendservlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.com.huateng.util.EnDecryptUtil;
import main.java.com.huateng.util.StringUtils;
import main.java.com.huateng.util.XmlStringUtil;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.huateng.bundle.PropertyBundle;
import com.huateng.remote.sign.service.RemoteService;
import com.huateng.security.adapter.BankSecurityHandle;
import com.huateng.security.adapter.SecurityHandle;


/**
 * panliguan
 * 主动请求其它接口的参数流写入(POST方式)
 * 输入：http地址端口等
 */
public class httpPostMsgServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(httpPostMsgServlet.class);
	
    public httpPostMsgServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String address = request.getParameter("address");
        String sendMsg = request.getParameter("sendMsg");
        //去掉格式
        sendMsg = StringUtils.replaceBlank(sendMsg);
        sendMsg = sendMsg.replaceAll("xmlversion","xml version");
        sendMsg = sendMsg.replaceAll("encoding=", " encoding=");
    	String signFlag = XmlStringUtil.parseNodeValueFromXml("<SignFlag>", "</SignFlag>", sendMsg);
    	String signFlagProperty = PropertyBundle.getConfig("signFlag");
        if("1".equals(signFlag)&&"open".equals(signFlagProperty)){
        	int headerIndex = sendMsg.indexOf("<Header>");
        	int headerLast =sendMsg.indexOf("</Header>");
        	String header = sendMsg.substring(headerIndex, headerLast+9);
			String body = XmlStringUtil.parseNodeValueFromXml("<Body>", "</Body>",sendMsg);
			String key = XmlStringUtil.parseNodeValueFromXml("<ReqSys>", "</ReqSys>", sendMsg);
			StringBuffer sb = new StringBuffer();
			sb.append(header).append("|").append("<Body>" + body + "</Body>");
			String url = PropertyBundle.getConfig("BankSecurityURL");
            HessianProxyFactory hessianFactory = new HessianProxyFactory(); 
            RemoteService remoteService = (RemoteService) hessianFactory.create(RemoteService.class, url); 
			log.debug("签名报文："+sb.toString());
            String signReturn = remoteService.sign(key,sb.toString());
			log.debug("。。。。。。。。签名为："+signReturn);
			sendMsg = XmlStringUtil.relaceNodeContent("<SignValue>","</SignValue>", signReturn, sendMsg);
			log.debug("..........签名后发送给crm的消息为："+sendMsg);
        } 
        String bankEncodeFlag = PropertyBundle.getConfig("bankEncodeFlag");
        String bankEncodeActivityCode = PropertyBundle.getConfig("bankEncodeActivityCode");
        String activityCode = XmlStringUtil.parseNodeValueFromXml("<ActivityCode>", "</ActivityCode>", sendMsg);
        if("open".equals(bankEncodeFlag)&&bankEncodeActivityCode.contains(activityCode)==true){
        	log.debug(".........银行端用户身份验证加密");
        	String userName = XmlStringUtil.parseNodeValueFromXml("<UserName>", "</UserName>", sendMsg);
        	String userId =  XmlStringUtil.parseNodeValueFromXml("<UserID>", "</UserID>", sendMsg);
        	String url = PropertyBundle.getConfig("BankEncryURL");
            HessianProxyFactory hessianFactory = new HessianProxyFactory(); 
            BankSecurityHandle remoteService = (BankSecurityHandle) hessianFactory.create(BankSecurityHandle.class, url); 
        	sendMsg = XmlStringUtil.relaceNodeContent("<UserName>","</UserName>", remoteService.symDecryptPNI(0, true, userName), sendMsg);
        	sendMsg = XmlStringUtil.relaceNodeContent("<UserID>","</UserID>", remoteService.symDecryptPNI(0, true, userId), sendMsg);
        	
        }
        StringBuffer result = new StringBuffer();
		long timeStart = 0;
		long timeEnd = 0;
		
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String responseDirect = PropertyBundle.getConfig("responseDirect");
        if("2".equals(responseDirect)){
        	result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><GPay><Header><ActivityCode>010001</ActivityCode><ReqSys>0009</ReqSys><ReqChannel>54</ReqChannel><ReqDate>20130804</ReqDate><ReqTransID>b522f401169d447ea6d3a0eb653923</ReqTransID><ReqDateTime>20130804164400913</ReqDateTime><ActionCode>1</ActionCode><RcvSys>0001</RcvSys><RcvDate>20130802</RcvDate><RcvTransID>101020130804164240000118907052</RcvTransID><RcvDateTime>20130804164240506</RcvDateTime><RspCode>020A00</RspCode><RspDesc>直接返回</RspDesc></Header><Body><SessionID>01308041644028417770</SessionID><UserCat>0</UserCat><AutoPayable>0</AutoPayable><DefRechThreshold>1000</DefRechThreshold><MaxRechThreshold>5000</MaxRechThreshold><DefRechAmount>10000</DefRechAmount><MaxRechAmount>50000</MaxRechAmount><HomeProv>200</HomeProv></Body></GPay>");
        	res.setContentType("text/xml;charset=UTF-8");
    		res.getWriter().print(result);
    		res.getWriter().close();
        }else{
            try {
            	timeStart =  System.currentTimeMillis();
                HttpPost httpost = new HttpPost(address);
                List <NameValuePair> nvps = new ArrayList <NameValuePair>();
                nvps.add(new BasicNameValuePair("xmldata", sendMsg));
                httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
                //设置超时时间（建链超时和响应超时）
                httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
                httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
                //接收响应消息
                HttpResponse response = httpclient.execute(httpost);
                HttpEntity entity = response.getEntity();
                timeEnd = System.currentTimeMillis();
                int code = response.getStatusLine().getStatusCode();
    	        result.append("用时：" ).append((timeEnd - timeStart)).append("毫秒...\n"); 
                
    	        if (code == 200){
    	        	result.append(EntityUtils.toString(entity, "UTF-8"));
    	        }else{
    	        	result.append("服务器连接请求失败,错误代码 ：").append(code).append("\n")
    	        	.append("失败原因如下：\n").append(EntityUtils.toString(entity, "UTF-8"));
    	        }
                
            }catch(ConnectTimeoutException e){
				log.debug("..........url连接超时异常："+e);
				result.append("url连接超时777777");
			}catch(HttpHostConnectException e){
				log.debug("..........连接失败异常："+e);
				result.append("连接服务器失败！");
			}catch(SocketTimeoutException e){
				log.debug("..........连接失败异常："+e);
				result.append("读取超时777777");
			}catch(Exception e){
				log.debug("........异常"+e);
    			result.append("用时：").append((timeEnd - timeStart)).append("毫秒...\n")
            	.append("服务器连接请求失败：").append(e.getMessage());
    		}finally {
                httpclient.getConnectionManager().shutdown();
            }
            log.debug(result);
    		res.setContentType("text/xml;charset=UTF-8");
    		res.getWriter().print(result);
    		res.getWriter().close();
        }

	}

}
