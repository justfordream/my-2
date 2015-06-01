package com.huateng.bundle;

import java.util.ResourceBundle;

/**
 * 配置文件读取类
 * @author zeng.j
 *
 */
public class PropertyBundle {
	public static ResourceBundle bundle = ResourceBundle.getBundle("server");
	public static String getConfig(String key){
		return bundle.getString(key);
	}
}
