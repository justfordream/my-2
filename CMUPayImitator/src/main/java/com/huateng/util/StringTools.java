package main.java.com.huateng.util;

import java.lang.reflect.Field;

public class StringTools {
	
	//长度不足，末尾添加空�?
	public String addSpace(String string, int length){
		String space = "";
		
		for(int i = 0; i < (length - string.length()); i++){
			space += " ";
		}
		
		return space;
	}
	
}
