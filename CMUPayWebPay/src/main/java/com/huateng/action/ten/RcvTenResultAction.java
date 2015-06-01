/**
 * 
 */
package com.huateng.action.ten;

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
public class RcvTenResultAction extends ActionSupport {

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

		final String return_url = requestMaps.get("return_url");
		final String notify_url = requestMaps.get("notify_url");
		requestMaps.remove("submit");
		requestMaps.remove("return_url");
		requestMaps.remove("notify_url");

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpPost httpost = new HttpPost(notify_url);
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("sign_type", requestMaps.get("sign_type")));
					nvps.add(new BasicNameValuePair("service_version", requestMaps.get("service_version")));
					nvps.add(new BasicNameValuePair("input_charset", requestMaps.get("input_charset")));
					nvps.add(new BasicNameValuePair("sign", requestMaps.get("sign")));
					nvps.add(new BasicNameValuePair("sign_key_index", requestMaps.get("sign_key_index")));
					nvps.add(new BasicNameValuePair("trade_mode", requestMaps.get("trade_mode")));
					nvps.add(new BasicNameValuePair("trade_state", requestMaps.get("trade_state")));
					nvps.add(new BasicNameValuePair("pay_info", requestMaps.get("pay_info")));
					nvps.add(new BasicNameValuePair("partner", requestMaps.get("partner")));
					nvps.add(new BasicNameValuePair("bank_type", requestMaps.get("bank_type")));
					nvps.add(new BasicNameValuePair("bank_billno", requestMaps.get("bank_billno")));
					nvps.add(new BasicNameValuePair("total_fee", requestMaps.get("total_fee")));
					nvps.add(new BasicNameValuePair("fee_type", requestMaps.get("fee_type")));
					nvps.add(new BasicNameValuePair("notify_id", requestMaps.get("notify_id")));
					nvps.add(new BasicNameValuePair("transaction_id", requestMaps.get("transaction_id")));
					nvps.add(new BasicNameValuePair("out_trade_no", requestMaps.get("out_trade_no")));
					nvps.add(new BasicNameValuePair("attach", requestMaps.get("attach")));
					nvps.add(new BasicNameValuePair("time_end", requestMaps.get("time_end")));
					nvps.add(new BasicNameValuePair("transport_fee", requestMaps.get("transport_fee")));
					nvps.add(new BasicNameValuePair("product_fee", requestMaps.get("product_fee")));
					nvps.add(new BasicNameValuePair("discount", requestMaps.get("discount")));
					nvps.add(new BasicNameValuePair("buyer_alias", requestMaps.get("buyer_alias")));
					
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

		String redirectHtml = WebBackToMer.getBackResult(requestMaps, return_url,
				true);
		resp.getWriter().write(redirectHtml.toString());
		return NONE;
	}

}
