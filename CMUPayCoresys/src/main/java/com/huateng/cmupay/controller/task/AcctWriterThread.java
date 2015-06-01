package com.huateng.cmupay.controller.task;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

public class AcctWriterThread implements Runnable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6083741835756791835L;

	private RedisTemplate template;
	
	

	private String key;
	private Map fieldMap;

	//private static final int EXPIRE_SECONDS = 5400;
	/**
	 * @param template
	 * @param key
	 * @param fieldMap
	 */
	public AcctWriterThread(RedisTemplate template, String key, Map fieldMap) {
		this.template = template;
		this.key = key;
		this.fieldMap = fieldMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/****
		 * 2种方式，使用template的api做一次put和一次expire需要2次connection，使用自己写的只要1次
		 */
		template.opsForHash().putAll(key, fieldMap);
		template.expire(key, 5400, TimeUnit.SECONDS);
		
		
//		if (MapUtils.isEmpty(fieldMap)) {
//			return;
//		}
//
//		final byte[] rawKey = template.getKeySerializer().serialize(key);
//
//		final Map<byte[], byte[]> hashes = new LinkedHashMap<byte[], byte[]>(fieldMap.size());
//
//		for (Object obj:fieldMap.keySet()) {
//			String key = (String)obj;
//			byte[] hkeybyte = template.getHashKeySerializer().serialize(key);
//			String val = (String)fieldMap.get(obj);
//			byte[] hvaluebyte = template.getHashValueSerializer().serialize(val);
//			hashes.put(hkeybyte, hvaluebyte)	;
//		}
//
//		template.execute(new RedisCallback<Object>() {
//			
//			public Object doInRedis(RedisConnection connection) {
//				connection.hMSet(rawKey, hashes);
//				connection.expire(rawKey, EXPIRE_SECONDS);
//				return null;
//			}
//		}, true);

	}

	/**
	 * @return the template
	 */
	public RedisTemplate getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(RedisTemplate template) {
		this.template = template;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the fieldMap
	 */
	public Map getFieldMap() {
		return fieldMap;
	}

	/**
	 * @param fieldMap
	 *            the fieldMap to set
	 */
	public void setFieldMap(Map fieldMap) {
		this.fieldMap = fieldMap;
	}

}
