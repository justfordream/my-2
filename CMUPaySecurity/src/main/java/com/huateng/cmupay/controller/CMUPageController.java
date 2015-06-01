//package com.huateng.cmupay.controller;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import com.huateng.cmupay.service.RemoteService;
//
//@Controller
//@RequestMapping("/CMU")
//public class CMUPageController {
//	@Autowired
//	private RemoteService CmuSecurityRemoting;
//
//	@RequestMapping(value="/Sign",method = RequestMethod.GET)
//	public String receiveSign(HttpServletRequest request) {
//		String plainText = this.signPlainText();
//		String sig = CmuSecurityRemoting.sign("111", plainText);
//		request.setAttribute("sig", sig);
//		return "/CmuSign";
//	}
//
//	@RequestMapping(value="/Pay",method = RequestMethod.GET)
//	public String receivePay(HttpServletRequest request) {
//		String plainText = this.payPlainText();
//		String sig = CmuSecurityRemoting.sign("111", plainText);
//		request.setAttribute("sig", sig);
//		return "/CmuPay";
//	}
//
//	private String signPlainText() {
//		String plainText = "SESSIONID=00000000000000000000" + "|IDTYPE=01&RIDVALUE=13823615234"
//				+ "|SERVERURL=http://localhost:8080/CMUPayWebPay/redirectResultCmu.action"
//				+ "|BACKURL=http://127.0.0.1:8080/CMUPayWebPay/CmuSign.jsp" + "|BANKID=0004"
//				+ "|SUBTIME=20130303211010" + "|TRANSACTIONID=00000000000000000000000000000001" + "|ORIGDOMAIN=0001"
//				+ "|CLIENTIP=192.168.1.1" + "|MCODE=UPAY00001";
//		return plainText;
//	}
//
//	private String payPlainText() {
//		String plainText = "MERID=0001" + "|ORDERID=001002003" + "|ORDERTIME=20130303211010&PAYED=10&CURTYPE=0"
//				+ "|IDVALUE=13825376543" + "|IDTYPE=01"
//				+ "|MERURL=http://localhost:8080/CMUPayWebPay/redirectResultCmu.action" + "|MERVAR=00000"
//				+ "|BACKURL=http://127.0.0.1:8080/CMUPayWebPay/CmuPay.jsp" + "|BANKID=0004" + "|CLIENTIP=192.168.1.1"
//				+ "|MCODE=UPAY10001";
//
//		return plainText;
//	}
//}
