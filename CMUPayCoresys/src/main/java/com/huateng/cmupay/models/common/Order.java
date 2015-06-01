/**
 * 
 */
package com.huateng.cmupay.models.common;

import com.huateng.cmupay.controller.cache.ColumnCache;



/**
 * 排序类
 * @author cmt
 *
 */
public class Order implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String column;
	
	private String orderType;
	
	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	private Order(String column, String orderType){
		this.column=column;
		this.orderType=orderType;
	}
	
	public static Order desc(String column){
		Order order=new Order(column, "desc");
		return order;
	}
	
	public static Order asc(String column){
		Order order=new Order(column, "asc");
		return order;
	}
	
	public String toString(){
		
		String orderColumn= ColumnCache.getColumnByProperty(this.column);
		if (orderColumn==null) return null;
		
		StringBuffer sb=new StringBuffer();
		sb.append(orderColumn)
		  .append(" ")
		  .append(this.orderType);
		
		return sb.toString();
	}
}
