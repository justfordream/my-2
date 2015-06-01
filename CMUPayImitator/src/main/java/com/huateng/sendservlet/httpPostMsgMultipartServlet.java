package main.java.com.huateng.sendservlet;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.com.huateng.receive.servlet.UpdateFileCont;
import main.java.com.huateng.util.EnDecryptUtil;
import main.java.com.huateng.util.XmlStringUtil;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.http.HttpEntity;   
import org.apache.http.HttpResponse;   
import org.apache.http.HttpStatus;   
import org.apache.http.client.ClientProtocolException;   
import org.apache.http.client.HttpClient;   
import org.apache.http.client.methods.HttpPost;   
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.MultipartEntity;   
import org.apache.http.entity.mime.content.FileBody;   
import org.apache.http.entity.mime.content.StringBody;   
import org.apache.http.impl.client.DefaultHttpClient;   
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.huateng.bundle.PropertyBundle;
import com.huateng.security.adapter.SecurityHandle;

/**
 * Servlet implementation class httpPostMsgMultipartServlet
 * 模拟省发消息
 */
public class httpPostMsgMultipartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(httpPostMsgMultipartServlet.class);
	
    public httpPostMsgMultipartServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String address = request.getParameter("address");
        String msgHeader = request.getParameter("xmlhead");
        String msgBody = request.getParameter("xmlbody");
        String encodeFlag = PropertyBundle.getConfig("encodeFlag");
        String url = PropertyBundle.getConfig("CRMSecurityURL");
        HessianProxyFactory hessianFactory = new HessianProxyFactory();
        boolean encrySign = false; 
        SecurityHandle securityHandle = (SecurityHandle) hessianFactory.create(SecurityHandle.class, url); 
        if(msgHeader.isEmpty()==false&&msgBody.isEmpty()==false&&EnDecryptUtil.isEncrypt(msgHeader)==true&&"open".equals(encodeFlag)){
			encrySign = true;
        	String encrySource =  XmlStringUtil.parseNodeValueFromXml("<![CDATA[","]]>", msgBody);
			log.debug("...........crm加密前明文:" + encrySource);
            String rspbody = securityHandle.encryptPIN(encrySource);
            log.debug("...........crm加密后密文："+rspbody);
            msgBody = XmlStringUtil.relaceNodeContent("<![CDATA[","]]>", rspbody, msgBody);
        	log.debug("..........发送给crm前置的消息为："+msgBody);
        } 
        
        StringBuffer result = new StringBuffer();
		long timeStart = 0;
		long timeEnd = 0;
		String responseDirect = PropertyBundle.getConfig("responseDirect");
		if("2".equals(responseDirect)){
			result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><InterBOSS><Version>0100</Version><TestFlag>0</TestFlag><BIPType><BIPCode>BIP1A151</BIPCode><ActivityCode>T1000153</ActivityCode><ActionCode>1</ActionCode></BIPType><RoutingInfo><OrigDomain>BOSS</OrigDomain><RouteType>00</RouteType><Routing><HomeDomain>UPSS</HomeDomain><RouteValue>997</RouteValue></Routing></RoutingInfo><TransInfo><SessionID>2013080416470944990b</SessionID><TransIDO>20130804164709449961da9e85ca44</TransIDO><TransIDOTime>20130804164709</TransIDOTime></TransInfo><Response><RspType>0</RspType><RspCode>0000</RspCode><RspDesc>交易成功</RspDesc></Response><SvcCont><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReversalRsp><RspCode>0000</RspCode><RspInfo>直接返回</RspInfo><TransactionID>013080416470945127595519682690</TransactionID><ActionDate>20130804</ActionDate><SubID>2013080416471786738a5a</SubID><SettleDate>20130804</SettleDate></ReversalRsp>]]></SvcCont></InterBOSS>");
			response.setContentType("text/xml;charset=UTF-8");
			response.getWriter().print(result.toString());
			response.getWriter().close();
		}else{
			try{
				timeStart =  System.currentTimeMillis();
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(address);
				StringBody xmlhead = new StringBody(msgHeader.toString(), Charset.forName("UTF-8"));  
				StringBody xmlbody = new StringBody(msgBody, Charset.forName("UTF-8"));  
				MultipartEntity entity = new MultipartEntity();
				entity.addPart("xmlhead", xmlhead);
				entity.addPart("xmlbody", xmlbody);
				post.setEntity(entity);   
				 //设置超时时间
                httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
                httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
				HttpResponse returnMsg = httpclient.execute(post);   
				timeEnd = System.currentTimeMillis();
				result.append("用时：").append((timeEnd - timeStart)).append("毫秒...\n"); 
				if(HttpStatus.SC_OK == returnMsg.getStatusLine().getStatusCode()){     
				    HttpEntity entitys = returnMsg.getEntity();   
				    if (entity != null) {  
				    	String returnSource = EntityUtils.toString(entitys, "UTF-8");
				    	//若发送前有加密，则在此处进行解密
				    	if(encrySign==true){
				    		//截取加密数据
				    		String encrySource =  XmlStringUtil.parseNodeValueFromXml("<![CDATA[","]]>", returnSource);
				    		log.debug(".......返回报文中的加密数据"+encrySource);
				    		//调用解密数据
				    		String encryMessage = securityHandle.decryptPIN(encrySource);
					    	//替换加密报文
				    		if(encryMessage == null||"0".equals(encryMessage)){
				    			returnSource = XmlStringUtil.relaceNodeContent("<![CDATA[","]]>", encrySource, returnSource);
				    			log.debug("......解密为空，返回报文："+returnSource);
				    		}else{
				    			returnSource = XmlStringUtil.relaceNodeContent("<![CDATA[","]]>", encryMessage, returnSource);
				    			log.debug("......解密后，返回报文："+returnSource);
				    		}
				    	}
				    	result.append(returnSource);   
				    }   
				}else{
		        	result.append("连接失败，失败原因：服务器连接请求失败,错误代码 ").append(returnMsg.getStatusLine().getStatusCode());
		        	
				}
				httpclient.getConnectionManager().shutdown();  
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
			}
			log.debug(result.toString());
			response.setContentType("text/xml;charset=UTF-8");
			response.getWriter().print(result.toString());
			response.getWriter().close();
		}
		
	}


}
