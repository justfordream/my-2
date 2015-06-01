package com.huateng.core.remoting;

import java.util.Map;



public interface TUpayRemoting {
	public Map<String, String> sendMsg(String header, Map<String, String> paramsMap);
}
