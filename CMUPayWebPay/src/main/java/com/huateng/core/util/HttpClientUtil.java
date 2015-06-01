package com.huateng.core.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.common.CoreConstant;

/**
 * 
 * @author Gary
 * 
 */
public class HttpClientUtil {

	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	/**
	 * 
	 * @param formUrl
	 *            请求的地址
	 * @param params
	 *            参数
	 * @param encoding
	 *            编码
	 * @return
	 */
	public static String formSubmit(String formUrl, Map<String, String> params,
			String encoding,String httTimeOut) {
		logger.info("httpClient 后台发送通知{}", formUrl);
		int connTimeoutInt=0;
		int soTimeoutInt=0;
		if(StringUtils.isBlank(httTimeOut)){
			connTimeoutInt=Integer.parseInt(httTimeOut);
			soTimeoutInt=Integer.parseInt(httTimeOut);
		}else{
			soTimeoutInt=Integer.parseInt(CoreConstant.SO_TIMEOUT);
			connTimeoutInt=Integer.parseInt(CoreConstant.CONNNECTION_TIMEOUT);
		}
		String responseText = StringUtils.EMPTY;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, connTimeoutInt);
		HttpConnectionParams.setSoTimeout(httpParameters, soTimeoutInt);
		/** 创建默认的httpClient实例*/
		HttpClient httpClient = new DefaultHttpClient();
		/** 创建httppost*/
		HttpPost httpPost = new HttpPost(formUrl);
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		HttpClientUtil.assemlyFormParams(params, formParams);
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formParams, encoding);
			httpPost.setEntity(uefEntity);
			long startTime = System.currentTimeMillis();
			logger.info("开始时间{}",startTime);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			long endTime = System.currentTimeMillis();
			logger.info("{},结束时间{}, 消耗时间,{}毫秒", new Object[] { formUrl,
					endTime , endTime - startTime });
			if (entity != null) {
				responseText = EntityUtils.toString(entity, encoding);
			}
			logger.info("httpClient后台通知成功{}接收的结果{}", entity, responseText);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("{}后台通知失败,失败原因###UnsupportedEncodingException{}",formUrl, e.getMessage());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			logger.error("{}后台通知失败,失败原因###ClientProtocolException{}", formUrl,e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("{}后台通知失败,失败原因###IOException{}", formUrl, e.getMessage());
		} catch (Exception e) {
			logger.error("{}后台通知失败,失败原因###Exception{}", formUrl, e.getMessage());
		} finally {
			// 关闭连接,释放资源
			httpClient.getConnectionManager().shutdown();
		}
		return responseText;
	}

	/**
	 * 转换 数据为NameValuePair格式
	 * 
	 * @param params
	 * @param formParams
	 */
	private static void assemlyFormParams(Map<String, String> params,
			List<NameValuePair> formParams) {
		if (MapUtil.isEmpty(params)) {
			return;
		}
		Set<Entry<String, String>> pSet = params.entrySet();
		BasicNameValuePair valuePair;
		for (Entry<String, String> entry : pSet) {
			valuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
			formParams.add(valuePair);
		}
	}
}
