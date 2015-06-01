package com.huateng.core.jms.common;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
/**
 *  json属性不必一一对应的ObjectMapper.
 * @author k.fan
 */
public class NoProperisObjectMapper extends ObjectMapper {
	/**
	 * Instantiates a new no properis object mapper.
	 */
	public NoProperisObjectMapper(){
		this.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}
