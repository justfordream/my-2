/**
 * 
 */
package com.huateng.cmupay.controller.task;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;


public class ValueSetThread implements RedisRunnable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2674070462246770652L;
	private RedisTemplate template;
	private String key;
	private String value;
	private int expireSeconds = DEFAULT_EXPIRE_SECONDS;
	//默认超时时间，1天
	private static final int DEFAULT_EXPIRE_SECONDS = 5400;
	public ValueSetThread(String key,String value){
		this.key=key;
		this.value=value;
	}
	public ValueSetThread(String key,String value,int expireSeconds){
		this.key=key;
		this.value=value;
		this.expireSeconds = expireSeconds;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		template.opsForValue().set(key, value);
		template.expire(key, expireSeconds, TimeUnit.SECONDS);
	}
	/**
	 * @return the template
	 */
	public RedisTemplate getTemplate() {
		return template;
	}
	/**
	 * @param template the template to set
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
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the expireSeconds
	 */
	public int getExpireSeconds() {
		return expireSeconds;
	}
	/**
	 * @param expireSeconds the expireSeconds to set
	 */
	public void setExpireSeconds(int expireSeconds) {
		this.expireSeconds = expireSeconds;
	}

}
