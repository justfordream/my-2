//package com.huateng.test.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import junit.framework.TestCase;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.huateng.bank.common.BankConstant;
//import com.huateng.core.common.CoreConstant;
//
///**
// * 银行前置测试
// * 
// * @author Gary
// * 
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({ "classpath:applicationContext.xml" })
//public class BankActionTest extends TestCase {
//	private Logger logger = LoggerFactory.getLogger(getClass());
//
//	@Test
//	public void testHttp() throws Exception {
//		String url = "http://127.0.0.1:8080/CMUPayBankFront/bankRevMsg.action";
//		final String xmlData = "";
//		HttpClient client = new DefaultHttpClient();
//		HttpPost post = new HttpPost(url);
//		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
//		formParams.add(new BasicNameValuePair("xmldata",xmlData));
//		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, CoreConstant.MSG_ENCODING);
//		post.setEntity(uefEntity);
//		logger.debug("......Request msg......");
//		logger.debug(BankConstant.REQ_XML_DATA + "=" + xmlData);
//		HttpResponse httpResponse = client.execute(post);
//		HttpEntity entity = httpResponse.getEntity();
//		String result = EntityUtils.toString(entity);
//		logger.debug("......Response msg is " + result);
//
//	}
//	public  String testXmlData() {
//		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//				"<GPay>" +
//				"	<Header>" +
//				"		<ActivityCode>ac</ActivityCode>" +
//				"		<ReqSys>rs</ReqSys>" +
//				"		<ReqChannel>rc</ReqChannel>" +
//				"		<ReqDate>rd</ReqDate>" +
//				"		<ReqTransID>rti</ReqTransID>" +
//				"		<ReqDateTime>rdi</ReqDateTime>" +
//				"		<ActionCode>ac</ActionCode>" +
//				"		<RcvSys>rs</RcvSys>" +
//				"		<RcvDate>rd</RcvDate>" +
//				"		<RcvTransID>rti</RcvTransID>" +
//				"		<RcvDateTime>rdt</RcvDateTime>" +
//				"		<RspCode>rc</RspCode>" +
//				"		<RspDesc>rd</RspDesc>" +
//				"	</Header>" +
//				"	<Body>" +
//				"		<UserIDType>uit</UserIDType>" +
//				"		<UserID>ui</UserID>" +
//				"		<Amount>a</Amount>" +
//				"	</Body>" +
//				"	<Sign>" +
//				"		<SignFlag>sf</SignFlag>" +
//				"		<CerID>ci</CerID>" +
//				"		<SignValue>sv</SignValue>" +
//				"	</Sign>" +
//				"</GPay>";
//		return result;
//	}
//}
