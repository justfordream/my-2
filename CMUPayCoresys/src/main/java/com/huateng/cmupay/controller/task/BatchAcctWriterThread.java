/**
 * 
 */
package com.huateng.cmupay.controller.task;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;


public class BatchAcctWriterThread implements Runnable {

	private RedisTemplate template;
	
	private Map<String,Map<Integer,String>> map;
	private static final int EXPIRE_SECONDS = 5400;
	public BatchAcctWriterThread(RedisTemplate template,Map<String,Map<Integer,String>> map){
		this.template=template;
		this.map=map;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		
		if (MapUtils.isEmpty(map)) {
			return;
		}
		final Map<byte[],Map<byte[],byte[]>> byteMap = new HashMap<byte[],Map<byte[],byte[]>>(map.size());
		for(Map.Entry<String,Map<Integer,String>> entry:map.entrySet()){
			final byte[] rawKey = template.getStringSerializer().serialize(entry.getKey());
			Map<Integer,String> oriMap = entry.getValue();
			final Map<byte[], byte[]> hashes = new LinkedHashMap<byte[], byte[]>(oriMap.size());

			for (Entry<Integer,String> e:oriMap.entrySet()) {
				Integer key =e.getKey();
				byte[] hkeybyte = template.getHashKeySerializer().serialize(key);
				String val =e.getValue();
				byte[] hvaluebyte = template.getHashValueSerializer().serialize(val);
				hashes.put(hkeybyte, hvaluebyte)	;
			}
			byteMap.put(rawKey, hashes);
		}
		


		template.execute(new RedisCallback<Object>() {
			
			public Object doInRedis(RedisConnection connection) {
				//批处理模式，打开pipeline
				connection.openPipeline();
				for(Map.Entry<byte[],Map<byte[],byte[]>> entry:byteMap.entrySet()){
					connection.hMSet(entry.getKey(), entry.getValue());
					connection.expire(entry.getKey(), EXPIRE_SECONDS);
				}
				connection.closePipeline();
				return null;
			}
		}, true);

	}

}
