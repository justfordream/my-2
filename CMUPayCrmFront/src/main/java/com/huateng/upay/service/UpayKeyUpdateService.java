package com.huateng.upay.service;

import com.huateng.upay.model.TUpayKey;

/**
 * @author qingxue.li
 * 
 */
public interface UpayKeyUpdateService {
	/**
	 * 入库密钥更新记录
	 * 
	 * @param upayKey
	 *            密钥更新记录
	 * 
	 */
	public boolean insertUpayKey(TUpayKey upayKey);

	/**
	 * 更新密钥信息
	 * 
	 * @param upayKey
	 *            密钥更新记录
	 */
	public void updateUpayKey(TUpayKey key);

	public void test(String str);

}
