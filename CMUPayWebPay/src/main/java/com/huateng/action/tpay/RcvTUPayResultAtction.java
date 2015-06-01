/**
 * 
 */
package com.huateng.action.tpay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.filter.HttpStringFilter;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.utils.WebBackToMer;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Administrator
 * 
 */
public class RcvTUPayResultAtction extends ActionSupport {

	@Value("${SPECIAL_FILETER_LIST}")
	private String fileterStr;

	public String receive() throws Exception {

		HttpServletRequest req = SessionRequestUtil.getRequest();
		HttpServletResponse resp = SessionRequestUtil.getResponse();
		
		req.setCharacterEncoding("UTF-8");
		resp.setHeader("Cache-Control", "no-cache, must-revalidate");
		resp.setHeader("Pragma", "no-cache");
		resp.setContentType("text/html;charset=utf-8");
		
		final Map<String, String> requestMaps = HttpStringFilter.filterRequestParams(
				req, fileterStr);

		final String frontUrl = requestMaps.get("frontUrl");
		final String backUrl = requestMaps.get("backUrl");
		requestMaps.remove("submit");
		requestMaps.remove("frontUrl");
		requestMaps.remove("backUrl");

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpPost httpost = new HttpPost(backUrl);
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("version", requestMaps.get("version")));
					nvps.add(new BasicNameValuePair("encoding", requestMaps.get("encoding")));
					nvps.add(new BasicNameValuePair("certId", requestMaps.get("certId")));
					nvps.add(new BasicNameValuePair("signature", requestMaps.get("signature")));
					nvps.add(new BasicNameValuePair("txnType", requestMaps.get("txnType")));
					nvps.add(new BasicNameValuePair("txnSubType", requestMaps.get("txnSubType")));
					nvps.add(new BasicNameValuePair("bizType", requestMaps.get("bizType")));
					nvps.add(new BasicNameValuePair("accessType", requestMaps.get("accessType")));
					nvps.add(new BasicNameValuePair("merId", requestMaps.get("merId")));
					nvps.add(new BasicNameValuePair("orderId", requestMaps.get("orderId")));
					nvps.add(new BasicNameValuePair("txnTime", requestMaps.get("txnTime")));
					nvps.add(new BasicNameValuePair("accNo", requestMaps.get("accNo")));
					nvps.add(new BasicNameValuePair("txnAmt", requestMaps.get("txnAmt")));
					nvps.add(new BasicNameValuePair("currencyCode", requestMaps.get("currencyCode")));
					nvps.add(new BasicNameValuePair("reqReserved", requestMaps.get("reqReserved")));
					nvps.add(new BasicNameValuePair("reserved", requestMaps.get("reserved")));
					nvps.add(new BasicNameValuePair("queryId", requestMaps.get("queryId")));
					nvps.add(new BasicNameValuePair("respCode", requestMaps.get("respCode")));
					nvps.add(new BasicNameValuePair("respMsg", requestMaps.get("respMsg")));
					nvps.add(new BasicNameValuePair("respTime", requestMaps.get("respTime")));
					nvps.add(new BasicNameValuePair("settleAmt", requestMaps.get("settleAmt")));
					nvps.add(new BasicNameValuePair("settleCurrencyCode", requestMaps.get("settleCurrencyCode")));
					nvps.add(new BasicNameValuePair("settleDate", requestMaps.get("settleDate")));
					nvps.add(new BasicNameValuePair("traceNo", requestMaps.get("traceNo")));
					nvps.add(new BasicNameValuePair("traceTime", requestMaps.get("traceTime")));
					nvps.add(new BasicNameValuePair("exchangeDate", requestMaps.get("exchangeDate")));
					nvps.add(new BasicNameValuePair("exchangeRate", requestMaps.get("exchangeRate")));
					nvps.add(new BasicNameValuePair("payCardType", requestMaps.get("payCardType")));
					nvps.add(new BasicNameValuePair("payType", requestMaps.get("payType")));
					nvps.add(new BasicNameValuePair("issuerIdentifyMode", requestMaps.get("issuerIdentifyMode")));

					
					httpost.setEntity(new UrlEncodedFormEntity(nvps,
							Consts.UTF_8));
					httpost.setHeader("Content-Type",
							"application/x-www-form-urlencoded; charset=UTF-8");
					HttpResponse response = httpclient.execute(httpost);
		            int code = response.getStatusLine().getStatusCode();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		Thread.sleep(5000);

		String redirectHtml = WebBackToMer.getBackResult(requestMaps, frontUrl,
				true);
		resp.getWriter().write(redirectHtml.toString());
		return NONE;
	}

}
