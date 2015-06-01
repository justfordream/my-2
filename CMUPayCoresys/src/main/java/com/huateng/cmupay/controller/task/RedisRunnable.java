/**
 * 
 */
package com.huateng.cmupay.controller.task;

import org.springframework.data.redis.core.RedisTemplate;


public interface RedisRunnable extends Runnable {
	RedisTemplate getTemplate();
	/**
	 * @param template the template to set
	 */
	void setTemplate(RedisTemplate template);
}
