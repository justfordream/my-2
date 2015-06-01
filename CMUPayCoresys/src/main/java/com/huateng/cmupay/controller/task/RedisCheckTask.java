package com.huateng.cmupay.controller.task;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisCheckTask {
	private Logger log = LoggerFactory.getLogger(RedisCheckTask.class);
	
	private RedisTemplate<Serializable, Serializable> template;
	public void execute(){
		log.debug("redis check task start...");
		
		try{
			template.execute(new RedisCallback<Object>() {
				
				public Object doInRedis(RedisConnection connection) {
					String ret = connection.ping();
					if(StringUtils.equalsIgnoreCase("PONG", ret)){
						//Redis2DaoAop.turnOnRedisOk();
					}else{
						log.info("redis down,change local redis flag to false...");
						//Redis2DaoAop.turnOffRedisOk();
					}
					return null;
				}
			}, true);
		}catch(Exception e){
			//Redis2DaoAop.turnOffRedisOk();
		}finally{
			log.debug("redis check task finished...");
		}
		
	}
	/**
	 * @return the template
	 */
	public RedisTemplate<Serializable, Serializable> getTemplate() {
		return template;
	}
	/**
	 * @param template the template to set
	 */
	public void setTemplate(RedisTemplate<Serializable, Serializable> template) {
		this.template = template;
	}
	
	
}
