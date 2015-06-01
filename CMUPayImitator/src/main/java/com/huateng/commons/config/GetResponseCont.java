package main.java.com.huateng.commons.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import com.huateng.bundle.PropertyBundle;

import main.java.com.huateng.receive.servlet.RspTemlateInit;

public class GetResponseCont {
	
	
	public static Map<String, String> getCont(Map<String, String> rspxmlmap) throws Exception{
//		取bank的返回报文的，名值对
		//bank的返回报文的绝对路径
		String url = GN.banktemprepfilepath;
		File f = new File(url);
		String str[] = f.list();
		for (int i = 0; i < str.length; i++) {
			if (str[i].endsWith(".xml")) {
//				InputStream in = RspTemlateInit.class.getResourceAsStream("/../../file/template/response/bank/"+ str[i]);
				InputStream in = new FileInputStream(url +File.separator+ str[i]);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				
				String line = "";
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\r\n");
				}
				rspxmlmap.put(str[i].substring(0, str[i].indexOf(".xml")),sb.toString());
				br.close();
				in.close();
			}
		}
		//取CRM的返回报文的，名值对
		//crm的返回报文的绝对路径
		String url2 = GN.CRMtemprepfilepath;
		File f2 = new File(url2);
		String str2[] = f2.list();
		for (int i = 0; i < str2.length; i++) {
			if (str2[i].endsWith(".xml")) {
//				InputStream in = RspTemlateInit.class.getResourceAsStream("/../../file/template/response/CRM/"+ str2[i]);
				InputStream in = new FileInputStream(url2 +File.separator+ str2[i]);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
			
				String line = "";
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\r\n");
				}
				rspxmlmap.put(str2[i].substring(0, str2[i].indexOf(".xml")),sb.toString());
				br.close();
				in.close();
			}
		}
		//取tmall的返回报文的，名值对
		//crm的返回报文的绝对路径
		String url3 = GN.tmalltemprepfilepath;
		File f3 = new File(url3);
		String str3[] = f3.list();
		for (int i = 0; i < str3.length; i++) {
			if (str2[i].endsWith(".xml")) {
//				InputStream in = RspTemlateInit.class.getResourceAsStream("/../../file/template/response/CRM/"+ str2[i]);
				InputStream in = new FileInputStream(url3 +File.separator+ str3[i]);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
			
				String line = "";
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\r\n");
				}
				rspxmlmap.put(str3[i].substring(0, str3[i].indexOf(".xml")),sb.toString());
				br.close();
				in.close();
			}
		}
		//取CRM的返回报文的，名值对
		//crm的返回报文的绝对路径
		String url4 = GN.mmalltemprepfilepath;
		File f4 = new File(url4);
		String str4[] = f4.list();
		for (int i = 0; i < str4.length; i++) {
			if (str4[i].endsWith(".xml")) {
//				InputStream in = RspTemlateInit.class.getResourceAsStream("/../../file/template/response/CRM/"+ str2[i]);
				InputStream in = new FileInputStream(url4 +File.separator+ str4[i]);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
			
				String line = "";
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\r\n");
				}
				rspxmlmap.put(str4[i].substring(0, str4[i].indexOf(".xml")),sb.toString());
				br.close();
				in.close();
			}
		}
		return rspxmlmap;
	}
}
