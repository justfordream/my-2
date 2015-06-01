package com.huateng.crm.bean.message.head;

/**
 * 路由信息
 * 
 * @author Gary
 * 
 */
public class Routing {
	/**
	 * 归属方应用域代码(参见应用域编码表)
	 */
	private String HomeDomain;
	/**
	 * 路由关键值(路由类型对应的关键值，参见路由类型说明)
	 */
	private String RouteValue;

	public String getHomeDomain() {
		return HomeDomain;
	}

	public void setHomeDomain(String homeDomain) {
		HomeDomain = homeDomain;
	}

	public String getRouteValue() {
		return RouteValue;
	}

	public void setRouteValue(String routeValue) {
		RouteValue = routeValue;
	}

}
