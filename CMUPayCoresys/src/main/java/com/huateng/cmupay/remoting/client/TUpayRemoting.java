package com.huateng.cmupay.remoting.client;

import java.util.Map;



public interface TUpayRemoting {
	public Map<String, String> sendMsg(String header, Map<String, String> paramsMap);
}
