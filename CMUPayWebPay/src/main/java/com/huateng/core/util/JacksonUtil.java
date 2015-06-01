package com.huateng.core.util;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.huateng.bean.CmuSignResponse;

/**
 * Json工具类
 * 
 * @author Gary
 * 
 */
public class JacksonUtil {
	private final static ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private JacksonUtil() {

	}

	/**
	 * java对象转换为json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String beanToStr(Object obj) {
		try {
			StringWriter sw = new StringWriter();
			JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
			objectMapper.writeValue(gen, obj);
			gen.close();
			return sw.toString();
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * json字符串转换为java对象
	 * 
	 * @param cls
	 * @param json
	 * @return
	 */
	public static <T> T strToBean(Class<T> cls, String json) {
		
		try {
			if(StringUtils.isBlank(json)){
				return cls.newInstance();
			}else{
				return objectMapper.readValue(json, cls);
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		CmuSignResponse res = new CmuSignResponse();
		res.setSessionID("2233333333333");
		System.out.println(JacksonUtil.beanToStr(res));
		String t = "{\"SessionID\":\"2233333333333\",\"RspCode\":null,\"RspInfo\":null,\"SubID\":null,\"SubTime\":null,\"BankAcctID\":null,\"BankAcctType\":null,\"BankID\":null,\"Sig\":null,\"TransactionID\":null,\"OrigDomain\":null,\"CLIENTIP\":null,\"ActionDate\":null,\"UserIDType\":null,\"UserID\":null,\"UserName\":null,\"PayType\":null,\"RechAmount\":null,\"RechThreshold\":null,\"UserPayType\":null,\"MCODE\":null}";
		CmuSignResponse r = JacksonUtil.strToBean(CmuSignResponse.class, t);
		System.out.println(r.getSessionID());
	}
}
