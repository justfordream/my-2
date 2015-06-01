package com.huateng.crm.bean.message.head;

/**
 * 交易路由信息
 * 
 * @author Gary
 * 
 */
public class RoutingInfo {
	/**
	 * 发起方应用域代码(参见应用域编码表)
	 */
	private String OrigDomain;
	/**
	 * 路由类型(参见路由类型编码，如按手机号码路由等)
	 */
	private String RouteType;
	/**
	 * 路由信息
	 */
	private Routing Routing;
	public String getOrigDomain() {
		return OrigDomain;
	}
	public void setOrigDomain(String origDomain) {
		OrigDomain = origDomain;
	}
	public String getRouteType() {
		return RouteType;
	}
	public void setRouteType(String routeType) {
		RouteType = routeType;
	}
	public Routing getRouting() {
		return Routing;
	}
	public void setRouting(Routing routing) {
		Routing = routing;
	}

}
