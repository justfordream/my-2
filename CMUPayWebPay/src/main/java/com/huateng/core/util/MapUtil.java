package com.huateng.core.util;

import java.util.Map;

/**
 * Map 工具类
 * 
 * @author Gary
 * 
 */
@SuppressWarnings("all")
public class MapUtil {

	public static boolean isEmpty(Map<String,String> obj) {
		return (obj == null || obj.isEmpty()) ? true : false;
	}
}
