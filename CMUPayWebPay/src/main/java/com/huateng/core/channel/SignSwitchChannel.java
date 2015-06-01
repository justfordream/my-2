package com.huateng.core.channel;

import com.huateng.bean.CmuSignRequest;

/**
 * 
 * @author Gary
 * 
 */
public interface SignSwitchChannel {
	/**
	 * 校验
	 * 
	 * @param cmuSignRequest
	 * @return
	 */
	public boolean validateLink(CmuSignRequest cmuSignRequest);
	
	public void transferLink(CmuSignRequest cmuSignRequest);
}
