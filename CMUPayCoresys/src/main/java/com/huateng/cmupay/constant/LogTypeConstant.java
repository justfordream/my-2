/**
 * 
 */
package com.huateng.cmupay.constant;

/**
 * 日志类型常量
 * @author cmt
 *
 */
public enum LogTypeConstant {
	

	
	
		/**
	 * 日志
	 */
	LogSystemClear("logSystem.delete","日志-清空"),
	BatCutCtl("batCut.find","日切-日期")
	;
	
	String name;
	String value;
	
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}

	LogTypeConstant(String name,String value){
		this.name=name;
		this.value=value;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public static String getValue(String name){
		LogTypeConstant [] types=LogTypeConstant.values();
		for (LogTypeConstant logTypeConstant : types) {
			if (logTypeConstant.name.equals(name))
				return logTypeConstant.value;
		}
		return "";
	}
}
