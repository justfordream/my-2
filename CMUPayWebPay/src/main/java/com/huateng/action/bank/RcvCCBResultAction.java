package com.huateng.action.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.common.CoreConstant;
import com.huateng.utils.DateUtil;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.utils.UUIDGenerator;
import com.huateng.utils.WebBackToMer;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * 建行后台结果通知处理器
 * 
 * @author Gary
 * 
 */
public class RcvCCBResultAction extends ActionSupport {

	private static final long serialVersionUID = -6722358696851962535L;
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final static String backUrl = "CMUPayWebPay/rcvCCBPage.action";
    
	@Value("${CCB.B2C.PAY.BANKFRONT_TEST}")
	private String testPayUrl;
	/**
	 * 统一入口
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String process() {
		final HttpServletRequest request = SessionRequestUtil.getRequest();
		String txType = request.getParameter("TXTYPE");
		PrintWriter writer = null;
		try {
			final String url = testPayUrl;
			if (CoreConstant.OPERATE_SIGN.equals(txType)) {
				logger.debug("##########开始向银行前置发送签约报文###########" + url);
				Thread thread=new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							sendBankfront(url, sendBankSignParmas(request));
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}
				});
				thread.start();
			} else if (CoreConstant.OPERATE_PAY.equals(txType)) {
				logger.debug("##########开始向银行前置发送支付报文###########" + url);
				Thread thread=new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							sendBankfront(url, sendBankPayParmas(request));
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}
				});
				thread.start();
			}
			Thread.sleep(6000);
			String html = assemlyRquest(request, backUrl);
			writer = SessionRequestUtil.getResponse().getWriter();
			logger.debug("#########开始向核心返回数据###########" + html);
			writer.write(html);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NONE;

	}

	/**
	 * @param url
	 * @param xmlData
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void sendBankfront(String url, String xmlData)
			throws ClientProtocolException, IOException {
		logger.debug("#########给银行前置发送报文 #########url=" + url);
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair("xmldata", xmlData));
		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams,
				"UTF-8");
		post.setEntity(uefEntity);
		logger.debug("########Request XML=" + xmlData);
		HttpResponse httpResponse = client.execute(post);
		HttpEntity entity = httpResponse.getEntity();
		String result = EntityUtils.toString(entity);
		logger.debug("############ Response msg is " + result);
	}

	/**
	 * 签约请求参数
	 */
	private String sendBankSignParmas(HttpServletRequest request) {
		String signParms = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<GPay>" + "<Header>" + "<ActivityCode>010002</ActivityCode>"
				+ "<ReqSys>0009</ReqSys>" + "<ReqChannel>50</ReqChannel>"
				+ "<ReqDate>"+DateUtil.getDateFormatYMD()+"</ReqDate>" + "<ReqTransID>"
				+ request.getParameter("ORDERID")
				+ "</ReqTransID>"
				+ "<ReqDateTime>"+DateUtil.getDateFormatYMDHMSSS()+"</ReqDateTime>"
				+ "<ActionCode>0</ActionCode>"
				+ "<RcvSys>0001</RcvSys>"
				+ "<RcvDate>"+DateUtil.getDateFormatYMD()+"</RcvDate>"
				+ "<RcvTransID>"+DateUtil.getDateFormatYMDHMSSS()+DateUtil.getDateFormatYMDHMS()+new Random().nextInt(10)+"</RcvTransID>"
				+ "<RcvDateTime>"+DateUtil.getDateFormatYMDHMSSS()+"</RcvDateTime>"
				+ "<RspCode></RspCode>"
				+ "<RspDesc></RspDesc>"
				+ "</Header>"
				+ "<Body>"
				+ "<SessionID>"+ request.getParameter("ORDERID")
				+ "</SessionID>"
				+ "<SubID>"+request.getParameter("SUBID")+"</SubID>"
				+ "<SubTime>"+DateUtil.getDateFormatYMDHMS()+"</SubTime>"
				+ "<BankAcctID>46546546543221336546</BankAcctID>"
				+ "<BankAcctType>0</BankAcctType>"
				+ "<PayType>"+request.getParameter("PayType")+"</PayType>"
				+ "<RechThreshold>"+request.getParameter("RechThreshold")+"</RechThreshold>"
				+ "<RechAmount>"+request.getParameter("RechAmount")+"</RechAmount>"
				+ "</Body>"
				+ "<Sign>"
				+ "<SignFlag>0</SignFlag>"
				+ "<CerID>625367485985443954397593847598347543534522346</CerID>"
				+ "<SignValue></SignValue>" + "</Sign>" + "</GPay> ";
		System.out.println("++++++++++++++++++++++"+signParms);
		return signParms;
	}

	/**
	 * 支付请求参数
	 */
	private String sendBankPayParmas(HttpServletRequest request) {
		String signParms = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<GPay>" + "<Header>" + "<ActivityCode>010014</ActivityCode>"
				+ "<ReqSys>0009</ReqSys>" + "<ReqChannel>51</ReqChannel>"
				+ "<ReqDate>"+DateUtil.getDateFormatYMD()+"</ReqDate>" + "<ReqTransID>"
				+ request.getParameter("ORDERID")
				+ "</ReqTransID>"
				+ "<ReqDateTime>"+DateUtil.getDateFormatYMDHMSSS()+"</ReqDateTime>"
				+ "<ActionCode>0</ActionCode>"
				+ "<RcvSys>0001</RcvSys>"
				//+ "<RcvDate>"+DateUtil.getDateFormatYMD()+"</RcvDate>"
				//+ "<RcvTransID>"+DateUtil.getDateFormatYMDHMSSS()+DateUtil.getDateFormatYMDHMS()+new Random().nextInt(10)+"</RcvTransID>"
				//+ "<RcvDateTime>"+DateUtil.getDateFormatYMDHMSSS()+"</RcvDateTime>"
				+ "<RspCode></RspCode>"
				+ "<RspDesc></RspDesc>"
				+ "</Header>"
				+ "<Body>"
				+ "<OrderID>"
				+ request.getParameter("ORDERID")
				+ "</OrderID>"
				+ "<MerVAR>13600091200</MerVAR>"
				+ "<RspCode>020A00</RspCode>"
				+ "<RspInfo>020A00</RspInfo>"
				+ "</Body>"
				+ "<Sign>"
				+ "<SignFlag>0</SignFlag>"
				+ "<CerID>625367485985443954397593847598347543534522346</CerID>"
				+ "<SignValue></SignValue>" + "</Sign>" + "</GPay>";
		return signParms;
	}

	public String assemlyRquest(HttpServletRequest request, String url) {

		String posId = request.getParameter("POSID");
		String branchId = request.getParameter("BRANCHID");
		String orderId = request.getParameter("ORDERID");
		String subId = request.getParameter("SUBID");
		String payment = request.getParameter("PAYMENT");
		String curCode = request.getParameter("CURCODE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String success = request.getParameter("SUCCESS");
		String beggingNo = request.getParameter("BEGGINGNO");
		String txType = request.getParameter("TXTYPE");
		String accNo = request.getParameter("ACCNO");
		String referer = request.getParameter("REFERER");
		String clientIp = request.getParameter("CLIENTIP");
		String sign = request.getParameter("SIGN");
		logger.debug("......接收建行结果通知，请求参数......[POSID:" + posId + ",BRANCHID:"
				+ branchId + ",ORDERID:" + orderId + ",SUBID:" + subId
				+ ",PAYMENT:" + payment + ",CURCODE:" + curCode + ",REMARK1:"
				+ remark1 + ",REMARK2:" + remark1 + ",SUCCESS:" + success
				+ ",BEGGINGNO:" + beggingNo + ",TXTYPE:" + txType + ",ACCNO:"
				+ accNo + ",REFERER:" + referer + ",CLIENTIP:" + clientIp
				+ ",SIGN:" + sign + "]");

		Map<String, String> params = new HashMap<String, String>();
		params.put("POSID", posId);
		params.put("BRANCHID", branchId);
		params.put("ORDERID", orderId);
		params.put("SUBID", subId);
		params.put("PAYMENT", payment);
		params.put("CURCODE", curCode);
		params.put("REMARK1", remark1);
		params.put("REMARK2", remark2);
		params.put("SUCCESS", success);
		params.put("BEGGINGNO", beggingNo);
		params.put("TXTYPE", txType);
		params.put("ACCNO", accNo);
		params.put("REFERER", referer);
		params.put("CLIENTIP", clientIp);
		params.put("SIGN", sign);
		String toHtml = WebBackToMer.getBackResult(params, url, false);
		return toHtml;
	}

}
