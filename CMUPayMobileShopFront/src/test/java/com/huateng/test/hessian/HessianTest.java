//package com.huateng.test.hessian;
//
//import java.net.MalformedURLException;
//
//import com.caucho.hessian.client.HessianProxyFactory;
//import com.huateng.core.remoting.BankRemoting;
//
//public class HessianTest {
//
//	public static void main(String[] args) throws MalformedURLException,
//			ClassNotFoundException {
//		// TODO Auto-generated method stub
//
//		String url = "http://localhost:8086/CMUPayBankFront/remote/BankRemoting";
//		HessianProxyFactory factory = new HessianProxyFactory();
//		BankRemoting bankRemoting = (com.huateng.core.remoting.BankRemoting) factory
//				.create(url);
//		bankRemoting.sendMsg("", "");
//
//	}
//
//}
