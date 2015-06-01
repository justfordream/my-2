package main.java.com.huateng.receive.servlet;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.com.huateng.commons.config.GN;
import main.java.com.huateng.util.FileUtil;
import main.java.com.huateng.util.TimeUtil;
import main.java.com.huateng.util.UUIDGenerator;
import main.java.com.huateng.util.XmlStringUtil;

import org.apache.log4j.Logger;

import sun.misc.Signal;

import com.caucho.hessian.client.HessianProxyFactory;
import com.huateng.bundle.PropertyBundle;
import com.huateng.remote.sign.service.TUPayRemoteService;

/**
 * @date 2014-05-21
 * @author junwords 功能：财付通通知查询
 */
public class ReceiveMsgAutoResponServletForTenQuery extends HttpServlet {
	private static final FileUtil fUtil = new FileUtil();
	private static final long serialVersionUID = -8272449870279418261L;

	private final Logger log = Logger
			.getLogger(ReceiveMsgAutoResponServletForTenQuery.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("..........财付通仿真接收请求.............");
		req.setCharacterEncoding("UTF-8");

		log.debug("..........财付通仿真获取参数............");
		Map<String, String> respParam = getAllRequestParam(req);

		resp.setContentType("text/html;charset=utf-8");

		StringBuffer pageResult = new StringBuffer();

		this.setResponseParam(respParam, pageResult);

		log.debug("财付通仿真拼裝应答报文参数：" + pageResult);
		resp.getWriter().write(pageResult.toString());
		log.debug("财付通仿真应答成功！！");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, String> getAllRequestParam(
			final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		String logString = "";
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				logString += en + "=" + value + ",";
			}
			log.debug("财付通获取请求参数为：" + logString);
		}
		return res;
	}

	/**
	 * 填充应答报文所需要的参数
	 * 
	 * @param respParam
	 *            请求报文中的参数
	 * @param pageResult
	 *            拼装的HTML格式应答报文
	 * @param valideData
	 *            要验签的数据
	 * @param encoding
	 *            编码格式
	 * @throws UnsupportedEncodingException
	 *             编码格式异常
	 * @throws MalformedURLException
	 */
	private void setResponseParam(Map<String, String> respParam,
			StringBuffer pageResult) throws MalformedURLException {

		log.debug(".........财付通仿真开始拼裝报文参数.........");

		String url = PropertyBundle.getConfig("TUpaySecurityURL");
		// 从请求报文中获取参数的键
		String keys = "sign_type,service_version,input_charset,sign,sign_key_index,retcode,retmsg,trade_state,"
				+ "trade_mode,partner,bank_type,bank_billno,total_fee,fee_type,transaction_id,out_trade_no,"
				+ "attach,time_end,transport_fee,product_fee,discount";
		// 从请求报文获取应答报文中所需参数的Map
		Map<String, String> responseMap = new HashMap<String, String>();

		StringBuffer respFilePath = new StringBuffer();
		respFilePath.append(GN.mmalltemprepfilepath).append(File.separator)
				.append("Ten").append(".xml");
		String responseString = fUtil.fileToStr(respFilePath.toString());

		TimeUtil tUtil = new TimeUtil();

		// 获取请求报文的商户号
		responseString = XmlStringUtil.relaceNodeContent("<partner>",
				"</partner>", respParam.get("partner"), responseString);
		
		String transation = (System.currentTimeMillis() + "").substring(0, 10);
		
		responseString = XmlStringUtil.relaceNodeContent("<transaction_id>",
				"</transaction_id>",
				respParam.get("partner") + tUtil.getLocalDate() + transation,
				responseString);

		responseString = XmlStringUtil.relaceNodeContent("<time_end>",
				"</time_end>", tUtil.getLocalDatetime(), responseString);
		// TODO 商户订单号
		responseString = XmlStringUtil.relaceNodeContent("<out_trade_no>",
				"</out_trade_no>", UUIDGenerator.generateUUID(), responseString);
		// 分割参数键的字符串
		String[] numStrings = keys.split("\\,");

		for (int i = 0; i < numStrings.length; i++) {
			// 从XML里读取对应的值
			String value = respParam.get(XmlStringUtil.parseNodeValueFromXml(
					"<" + numStrings[i] + ">", "</" + numStrings[i] + ">",
					responseString));
			if ("null".equals(value) || null == value) {
				value = "";
			}
			responseMap.put(numStrings[i], value);
		}
		// 验签开关
		if (PropertyBundle.getConfig("checkSignFlag").equals("open")) {
			log.debug("验签服务器地址：" + url);
			HessianProxyFactory hessianFactory = new HessianProxyFactory();
			TUPayRemoteService tUPayRemoteService = (TUPayRemoteService) hessianFactory
					.create(TUPayRemoteService.class, url);
			boolean flag = tUPayRemoteService.tenPayVerify(respParam);

			if (flag == false) {
				responseString = XmlStringUtil.relaceNodeContent("<retcode>",
						"</retcode>", "1", responseString);
				responseString = XmlStringUtil.relaceNodeContent("<retmsg>",
						"</retmsg>", "验签失败", responseString);
			}
		}
		
		// 签名开关
		if (PropertyBundle.getConfig("signFlag").equals("open")) {
			HessianProxyFactory hessianFactory = new HessianProxyFactory();
			TUPayRemoteService remoteService = (TUPayRemoteService) hessianFactory
					.create(TUPayRemoteService.class, url);
			log.debug("签名报文：" + responseMap);
			responseMap = remoteService.tenPaySign(responseMap);
			log.debug("..........签名后发送给支付前置的消息为：" + responseMap);
		}
		responseString = XmlStringUtil.relaceNodeContent("<sign>", "</sign>",
				responseMap.get("sign"), responseString);
		pageResult.append(responseString);

	}

}
