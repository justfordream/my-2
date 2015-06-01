package com.huateng.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author cmt
 * 
 */
public  class JacksonUtils {
	private static final Logger LOG = LoggerFactory
			.getLogger(JacksonUtils.class);
	
	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
	//MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
		MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}


	private static final JsonFactory JSONFACTORY = new JsonFactory();
	

	/**
	 * 把json参数转换成对应Vo
	 * 
	 * @param param
	 * @param T
	 * @return
	 */
	public static <T> T json2Bean(String json, Class<T> clazz) {
		
		T rtn =null;
		try {
			
			rtn = 	MAPPER.readValue(json, clazz);
		} catch (JsonParseException e) {
			LOG.error(e.getMessage());
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return rtn;
	}

	
	
	/**
	 * 转换vo 为 json
	 */
	public static String bean2Json(Object o) {
		StringWriter sw = new StringWriter();
		JsonGenerator jsonGenerator = null;

		try {
			jsonGenerator = JSONFACTORY.createJsonGenerator(sw);
			MAPPER.writeValue(jsonGenerator, o);
			return sw.toString();

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;

		} finally {
			if (jsonGenerator != null) {
				try {
					jsonGenerator.close();
				} catch (Exception e) {
					LOG.error(e.getMessage());

				}
			}
		}
	}
	
	

	/**
	 * 转换Json String 为 HashMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> json2Map(String json,
			boolean collToString) {
		try {
			Map<String, Object> map = MAPPER.readValue(json, HashMap.class);
			if (collToString) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getValue() instanceof Collection
							|| entry.getValue() instanceof Map) {
						entry.setValue(bean2Json(entry.getValue()));
					}
				}
			}
			return map;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("serial")
	public static class jsonParseException extends Exception {
		public jsonParseException(String message) {
			super(message);
		}
	}

	/**
	 * List 转换成json
	 * 
	 * @param list
	 * @return
	 */
	public static String list2Json(List<Map<String, String>> list) {
		JsonGenerator jsonGenerator = null;
		StringWriter sw = new StringWriter();
		try {
			jsonGenerator = JSONFACTORY.createJsonGenerator(sw);
			new ObjectMapper().writeValue(jsonGenerator, list);
			jsonGenerator.flush();
			return sw.toString();
		} catch (Exception e) {
			return null;
		} finally {
			if (jsonGenerator != null) {
				try {
					jsonGenerator.flush();
					jsonGenerator.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * json 转List
	 * 
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> json2List(String json) {
		try {
			if (json != null && !"".equals(json.trim())) {
				JsonParser jsonParse = JSONFACTORY
						.createJsonParser(new StringReader(json));

				ArrayList<Map<String, String>> arrayList = (ArrayList<Map<String, String>>) new ObjectMapper()
						.readValue(jsonParse, ArrayList.class);
				return arrayList;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	
	// 测试方法
	public static void main(String[] args) {
		User user = new User();
		user.setId(1);
		user.setUsername("张小三");
		user.setAddress("海南");
		user.setPhone("010");

		User user2 = new User();
		user2.setId(2);
		user2.setUsername("李小璐");
		user2.setAddress("天津");
		user2.setPhone("8888888");

		List<User> list = new ArrayList<User>();
		list.add(user);
		list.add(user2);

		 String str = bean2Json(user);
		 System.out.println(str);

		 String json =
		 "{\"address\":\"拾金路\",\"id\":1,\"username\":\"张小宁\",\"phone\":123456789}";
		 User u = (User)json2Bean(json,User.class);
		 System.out.println(u);
		
	}
	
	
	static class   User {
		 
		 private int id;
		 private String username;
		 private String address;
		 private String phone;
		 public int getId() {
		  return id;
		 }
		 public void setId(int id) {
		  this.id = id;
		 }
		 public String getUsername() {
		  return username;
		 }
		 public void setUsername(String username) {
		  this.username = username;
		 }
		 public String getAddress() {
		  return address;
		 }
		 public void setAddress(String address) {
		  this.address = address;
		 }
		 public String getPhone() {
		  return phone;
		 }
		 public void setPhone(String phone) {
		  this.phone = phone;
		 }
		 
		 @Override
		 public String toString() {
		  return "id: " + id + " username: " + username + " address: " + address + " phone: " + phone;
		 }
		}
	
}
