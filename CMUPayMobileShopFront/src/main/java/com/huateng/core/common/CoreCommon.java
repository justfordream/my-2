//package com.huateng.core.common;
//
//import java.util.ResourceBundle;
//
///**
// * 
// * @author Gary
// * 
// */
//public class CoreCommon {
//	private static ResourceBundle bundle = ResourceBundle.getBundle("jms");
//	
//	/**
//	 * 获取异步发送地址
//	 * */
//	public static String gerMarketUrl(){
//		return bundle.getString("market.url");
//	}
//	/**
//	 * 
//	 * */
//	public static int getMsgConcurrentNum() {
//		return Integer.valueOf(bundle.getString("msg.paral"));
//	}
//	/**
//	 * 获取验签开关(open:开,close:关)
//	 * 
//	 * @return
//	 */
//	public static String getCheckSwitch() {
//		return bundle.getString("check.switch");
//	}
//
//	/**
//	 * 获取签名开关(open:开,close:关)
//	 * 
//	 * @return
//	 */
//	public static String getSignSwitch() {
//		return bundle.getString("sign.switch");
//	}
//	
//	/**
//	 * 连接超时时间
//	 * 
//	 * @return
//	 */
//	public static String getConnectionTimeOut() {
//		return bundle.getString("http.conn.timeout");
//	}
//	
//	/**
//	 * 接收响应超时时间
//	 * 
//	 * @return
//	 */
//	public static String getReceiveTimeOut() {
//		return bundle.getString("http.rev.timeout");
//	}
//	
//	
//	/**
//	 * 路由信息--协议
//	 * @return
//	 */
//	public static String getTmallProtocal(){
//		return bundle.getString("TMALL_CRM_PROTOCAL");
//	}
//	
//	/**
//	 * 路由信息--ip
//	 * @return
//	 */
//	public static String getTmallIP(){
//		return bundle.getString("TMALL_CRM_IP");
//	}
//	
//	
//	/**
//	 * 路由信息--端口
//	 * @return
//	 */
//	public static String getTmallPort(){
//		return bundle.getString("TMALL_CRM_PORT");
//	}
//	
//	/**
//	 * 路由信息--请求路径
//	 * @return
//	 */
//	public static String getTmallReqPath(){
//		return bundle.getString("TMALL_CRM_REQPATH");
//	}
//	
//	/**
//	 * 路由信息--路由信息
//	 * @return
//	 */
//	public static String getTmallRouteInfo(){
//		return bundle.getString("TMALL_CRM_ROUTEINFO");
//	}
//}
