package com.huateng.core.cache;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * 省网厅证书缓存
 * 
 * @author Gary
 * 
 */
@Component
public class CmuCertCache {

	private final static Map<String, Object> CMU_CERT_MAP = new HashMap<String, Object>();

	@PostConstruct
	private void init() {
		CMU_CERT_MAP.clear();
		CMU_CERT_MAP.put("200", null);
	}

	public void reLoad() {
		init();
	}

	public static Map<String, Object> getCertMap() {
		return CMU_CERT_MAP;
	}

	public static Object getCert(String homeProv) {
		return CMU_CERT_MAP.get(homeProv);
	}
}
