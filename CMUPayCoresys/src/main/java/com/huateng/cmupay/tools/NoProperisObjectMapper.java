package com.huateng.cmupay.tools;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
/**
 * json属性不必一一对应的ObjectMapper.
 */
@Component("jsonMapper")
public class NoProperisObjectMapper extends ObjectMapper {
	/**
	 * Instantiates a new no properis object mapper.
	 */
	public NoProperisObjectMapper(){
		this.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}
