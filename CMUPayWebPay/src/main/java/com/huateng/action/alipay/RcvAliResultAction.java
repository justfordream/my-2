/**
 * 
 */
package com.huateng.action.alipay;

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
public class RcvAliResultAction extends ActionSupport {

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

		String keys = "sign,out_trade_no,subject,payment_type,trade_no,trade_status,gmt_create,gmt_payment,gmt_close," +
				"refund_status,gmt_refund,seller_email,buyer_email,seller_id,buyer_id,price,total_fee,quantity,body," +
				"discount,is_total_fee_adjust,use_coupon,extra_common_param,out_channel_type,out_channel_amount,out_channel_inst," +
				"notify_time,notify_id,notify_type,sign_type";
		final String[] numStrings = keys.split("\\,");
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpPost httpost = new HttpPost(notify_url);
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					//循环添加参数
					for (int i = 0; i < numStrings.length; i++) {
						nvps.add(new BasicNameValuePair(numStrings[i], requestMaps.get("ht_"+numStrings[i])));
					}
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
		
		//remove Map中后台通知的参数
		for (int i = 0; i < numStrings.length; i++) {
			requestMaps.remove("ht_"+numStrings[i]);
		}
		
		thread.start();
		Thread.sleep(5000);

		String redirectHtml = WebBackToMer.getBackResult(requestMaps, return_url,
				true);
		resp.getWriter().write(redirectHtml.toString());
		return NONE;
	}

}
