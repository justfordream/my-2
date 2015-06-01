package main.java.com.huateng.receive.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.com.huateng.util.DateUtil;
import main.java.com.huateng.util.IDGenerator;
import main.java.com.huateng.util.UUIDGenerator;

import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.huateng.bundle.PropertyBundle;
import com.huateng.remote.sign.service.TUPayRemoteService;

/**
 * 2014年5月21日10
 * 处理移动商城认证支付
 */
public class ReceiveMsgMMshopForTpayRZZF extends HttpServlet {
	private final Logger log = Logger.getLogger(ReceiveMsgMMshopForTpayRZZF.class);
	private static final long serialVersionUID = 1L;
       
    public ReceiveMsgMMshopForTpayRZZF() {
        super();
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("开始接受请求报文参数");
		response.setCharacterEncoding("UTF-8");
		//获取原请求报文所有参数
		Map<String, String> respParam = getAllRequestParam(request);
		StringBuffer pageResult = new StringBuffer();
		
		//给返回报文赋值
		log.info("给返回报文赋值");
		setResponseParam(respParam,pageResult);
		String result = pageResult.substring(0,pageResult.length()-1);
		log.debug("银联仿真拼裝应答报文参数：" + result);
		log.info("返回报文赋值完毕!");
		response.getWriter().write(result);
	}
	/**
	 * 获取请求参数中所有的信息
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
			}
		}
		return res;
	}
	//报文参数赋值
	  private void setResponseParam(Map<String, String> respParam,StringBuffer pageResult) throws UnsupportedEncodingException, MalformedURLException {
			 String url = PropertyBundle.getConfig("TUpaySecurityURL");     
		    //从请求报文中获取参数的键
				String keys ="version,certId,signature,encoding,txnType,txnSubType,bizType,accessType,merId,orderId,txnTime,accNo,currencyCode,reqReserved,reserved,txnAmt," ;
				//从请求报文获取应答报文中所需参数的Map
				Map<String, String> responseMap = new HashMap<String, String>();
				//从请求报文中获取参数值
				if (null != respParam && !respParam.isEmpty()) {
					String [] numStrings = keys.split("\\,");
					for (int i = 0; i < numStrings.length; i++) {
						String value = respParam.get(numStrings[i]);
						responseMap.put(numStrings[i], value);
					}
				}
			//从请求报文中获取参数值
			if (null != responseMap && !responseMap.isEmpty()) {
				 /*
				 *应答码
				 */
				responseMap.put("respCode",PropertyBundle.getConfig("responseCode"));       
				/*
				 * 应答描述
				 */
				responseMap.put("respMsg", PropertyBundle.getConfig("responseMsg"));    
				/*
				 * 查询流水号
				 */
				responseMap.put("queryId", IDGenerator.getSessionID()+"0");      
				/*
				 * 响应时间
				 */
				responseMap.put("respTime", DateUtil.getDateyyyyMMddHHmmss());  
				/*
				 * 清算金额 1---12位整数
				 */
				responseMap.put("settleAmt", UUIDGenerator.randomNum(2));     
				/*
				 * 清算币种
				 */
				responseMap.put("settleCurrencyCode", "156");   
				/*
				 * 系统跟踪号  N6 
				 */
				responseMap.put("traceNo", UUIDGenerator.randomNum(6));    
				/*
				 * 交易传输时间 格式 YYYYMMDDhhmmss
				 */
				responseMap.put("traceTime", DateUtil.getDateyyyyMMddHHmmss());      
			}
			
			// 验签开关
			if (PropertyBundle.getConfig("checkSignFlag").equals("open")) {
				log.debug("验签服务器地址：" + url);
				HessianProxyFactory hessianFactory = new HessianProxyFactory();
				TUPayRemoteService tUPayRemoteService = (TUPayRemoteService) hessianFactory.create(TUPayRemoteService.class, url);
				boolean flag = tUPayRemoteService.verify(respParam.get("certId"),
						respParam);

				if (flag == false) {
					// 响应码
					responseMap.put("respCode", "11");
					// 响应信息
					responseMap.put("respMsg", "验证签名失败");
				}
			}
			// 签名开关
			if (PropertyBundle.getConfig("signFlag").equals("open")) {
				HessianProxyFactory hessianFactory = new HessianProxyFactory();
				TUPayRemoteService remoteService = (TUPayRemoteService) hessianFactory.create(TUPayRemoteService.class, url);
				log.debug("签名报文：" + responseMap);
				responseMap = remoteService.sign(respParam.get("certId"),
						responseMap);
				log.debug("..........签名后发送给支付前置的消息为：" + responseMap);

			}
			
			Iterator<Entry<String, String>> it = responseMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				pageResult.append(key + "=" + value + "&");
			}
		}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
    }

}
