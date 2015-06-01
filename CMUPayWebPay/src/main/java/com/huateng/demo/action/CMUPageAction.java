package com.huateng.demo.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.huateng.cmupay.service.RemoteService;
import com.huateng.utils.SessionRequestUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 仿真程序
 * 
 * @author Gary
 * 
 */
public class CMUPageAction extends ActionSupport {

	private static final long serialVersionUID = 2901766583845530134L;
	@Autowired
	private RemoteService cmuSecurityRemoting;
	public String receiveSign() {
		try {
			String plainText = this.signPlainText();
			String sig = cmuSecurityRemoting.sign("111", plainText);
			HttpServletRequest request = SessionRequestUtil.getRequest();
			request.setAttribute("sig", sig);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return "sign";
	}

	public String receivePay() {
		try {
			String plainText = this.payPlainText();
			String sig = cmuSecurityRemoting.sign("111", plainText);
			HttpServletRequest request = SessionRequestUtil.getRequest();
			request.setAttribute("sig", sig);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return "pay";
	}

	private String signPlainText() {
		String plainText = "SESSIONID=00000000000000000000" + "|IDTYPE=01&RIDVALUE=13823615234"
				+ "|SERVERURL=http://localhost:8080/CMUPayWebPay/redirectResultCmu.action"
				+ "|BACKURL=http://127.0.0.1:8080/CMUPayWebPay/CmuSign.jsp" + "|BANKID=0004"
				+ "|SUBTIME=20130303211010" + "|TRANSACTIONID=00000000000000000000000000000001" + "|ORIGDOMAIN=0001"
				+ "|CLIENTIP=192.168.1.1" + "|MCODE=UPAY00001";
		return plainText;
	}

	private String payPlainText() {
		String plainText = "MERID=0001" +
				"|ORDERID=001002003" +
				"|ORDERTIME=20130303211010&PAYED=10&CURTYPE=0" +
				"|IDVALUE=13825376543" +
				"|IDTYPE=01" +
				"|MERURL=http://localhost:8080/CMUPayWebPay/redirectResultCmu.action" +
				"|MERVAR=00000" +
				"|BACKURL=http://127.0.0.1:8080/CMUPayWebPay/CmuPay.jsp" +
				"|BANKID=0004" +
				"|CLIENTIP=192.168.1.1" +
				"|MCODE=UPAY10001";
		
		return plainText;
	}
}
