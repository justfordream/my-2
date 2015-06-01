package com.huateng.utils;

import java.util.UUID;

public class UUIDGenerator {
	/**
	 * 默认构造函数
	 */
	private UUIDGenerator() {

	}

	/**
	 * 获取自动生成的UUID字符串
	 * 
	 * @return
	 */
	public static String generateUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		// 去掉"-"符号
		String temp = str.replaceAll("-", "");
		return temp;
	}

}
