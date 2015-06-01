/**
 * 
 */
package com.huateng.cmupay.tools;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;


public class IntegerRedisSerializer implements RedisSerializer<Integer> {

	/* (non-Javadoc)
	 * @see org.springframework.data.redis.serializer.RedisSerializer#serialize(java.lang.Object)
	 */
	public byte[] serialize(Integer t) throws SerializationException {
		
		return (t == null ? null : t.toString().getBytes());
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.redis.serializer.RedisSerializer#deserialize(byte[])
	 */
	public Integer deserialize(byte[] bytes) throws SerializationException {

		return bytes==null?null:Integer.valueOf(new String(bytes));
	}

}
