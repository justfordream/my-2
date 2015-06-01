package com.huateng.core.channel;

import com.huateng.bean.CmuPayRequest;

/**
 * 
 * @author Gary
 * 
 */
public interface PaySwitchChannel {
	/**
	 * 
	 * @param cmuPayRequest
	 * @return
	 */
	public boolean validateLink(CmuPayRequest cmuPayRequest);
}
