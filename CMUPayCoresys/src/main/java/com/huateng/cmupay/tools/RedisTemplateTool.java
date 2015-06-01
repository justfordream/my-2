/**
 * 
 */
package com.huateng.cmupay.tools;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.huateng.cmupay.controller.task.ThreadPoolManager;
import com.huateng.cmupay.controller.task.ValueSetThread;



public class RedisTemplateTool {
	/**
	 * redis template
	 */
	private RedisTemplate<Serializable, Serializable> template;
	/** json mapper **/
	private ObjectMapper jsonMapper;

	private ThreadPoolManager writeManager;

	private Logger log = LoggerFactory.getLogger(RedisTemplateTool.class);

	public void aSyncSet(String key, String value) {
		writeManager.addMessage(new ValueSetThread(key, value));
	}

	public String get(String key) {
		try {
			return (String) template.opsForValue().get(key);
		} catch (Exception e) {
			log.error("redis set failed,error message is {}", e.getMessage());
			// 不抛异常
		}
		return null;
	}

	/*public void aSyncSetAccount(String userid, Map<Integer, AccountVo> retMap) {
		try {
			if (MapUtils.isNotEmpty(retMap)) {
				Map<String, Map<Integer, String>> insertMap = new HashMap<String, Map<Integer, String>>(
						1);
				Map<Integer, String> jsonMap = new HashMap<Integer, String>(
						retMap.size());
				for (Map.Entry<Integer, AccountVo> e : retMap.entrySet()) {
					jsonMap.put(e.getKey(),
							jsonMapper.writeValueAsString(e.getValue()));
				}
				insertMap.put(userid, jsonMap);
				writeManager.addMessage(insertMap);
			}
		} catch (Exception e) {
			log.error("redis error ,{}", e.getMessage());
		}
	}

	public void aSyncSetAccount(Collection<AccountVo> col) {
		try {
			if (col != null && col.size() > 0) {
				Map<String, Map<Integer, String>> insertMap = new HashMap<String, Map<Integer, String>>(
						col.size());
				
				for (AccountVo acct :col) {
					Map<Integer, String> jsonMap = insertMap.get(acct.getPassport());
					if(jsonMap==null){
						jsonMap = new HashMap<Integer, String>();
						insertMap.put(acct.getPassport(), jsonMap);
					}
					jsonMap.put(acct.getAcctType(), jsonMapper.writeValueAsString(acct));
				}
				writeManager.addMessage(insertMap);
			}
		} catch (Exception e) {
			log.error("redis error ,{}", e.getMessage());
		}
	}

	public Map<Integer, AccountVo> getAccount(String userid) {
		try {
			// 先从缓存中读取
			// 若失败，并不抛出异常，而是继续通过db读取
			Map<Object, Object> cachedAcct = template.boundHashOps(userid)
					.entries();
			if (MapUtils.isNotEmpty(cachedAcct)) {
				Map<Integer, AccountVo> retMap = new HashMap<Integer, AccountVo>(
						cachedAcct.size());
				for (Entry<Object, Object> entry : cachedAcct.entrySet()) {
					String tmpJsonObj = (String) entry.getValue();

					AccountVo tmpVo = jsonMapper.readValue(tmpJsonObj,
							AccountVo.class);
					retMap.put(tmpVo.getAcctType(), tmpVo);

				}
				return retMap;
			}
		} catch (Exception e) {
			log.error("redis error ,{}", e.getMessage());
		}
		return null;
	}*/

	/**
	 * @return the template
	 */
	public RedisTemplate<Serializable, Serializable> getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(RedisTemplate<Serializable, Serializable> template) {
		this.template = template;
	}

	/**
	 * @return the jsonMapper
	 */
	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}

	/**
	 * @param jsonMapper
	 *            the jsonMapper to set
	 */
	public void setJsonMapper(ObjectMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
	}

	/**
	 * @return the writeManager
	 */
	public ThreadPoolManager getWriteManager() {
		return writeManager;
	}

	/**
	 * @param writeManager
	 *            the writeManager to set
	 */
	public void setWriteManager(ThreadPoolManager writeManager) {
		this.writeManager = writeManager;
	}

}
