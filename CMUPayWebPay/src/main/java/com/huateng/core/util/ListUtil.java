package com.huateng.core.util;

import java.util.List;

/**
 * List工具类
 * 
 * @author Gary
 * 
 */
@SuppressWarnings("all")
public class ListUtil {
	/**
	 * 判断List是否为空<br>
	 * objList = null true<br>
	 * objList.size() = 0 true<br>
	 * 
	 * @param objList
	 * @return
	 */
	public static boolean isEmpty(List objList) {
		return (objList == null || objList.isEmpty()) ? true : false;
	}
}
