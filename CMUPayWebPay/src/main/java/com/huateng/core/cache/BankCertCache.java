package com.huateng.core.cache;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * 
 * @author Gary
 * 
 */
public class BankCertCache {
	private final static Map<String, Object> BANK_CERT_MAP = new HashMap<String, Object>();

	@PostConstruct
	private void init() {
		BANK_CERT_MAP.clear();
		BANK_CERT_MAP.put("0004", null);
		BANK_CERT_MAP.put("0005", null);
	}

	public void reLoad() {
		init();
	}

	public static Map<String, Object> getCertMap() {
		return BANK_CERT_MAP;
	}

	public static Object getCert(String bankId) {
		return BANK_CERT_MAP.get(bankId);
	}
}
