package com.huateng.channel;

import com.huateng.bean.CCBData;
import com.huateng.bean.CoreResultPayRes;
import com.huateng.bean.CoreResultSignRes;
import com.huateng.vo.MsgData;

/**
 * 建行切换通道
 * 
 * @author Gary
 * 
 */
public interface CCBSwitchChannel {
	/**
	 * 校验签约信息
	 * 
	 * @param reqData
	 */
	public void validateSignLink(CCBData ccbData);

	/**
	 * 发送签约信息
	 * 
	 * @param reqData
	 */
	public MsgData transferSignLink(CoreResultSignRes coreRsp);

	/**
	 * 校验签约信息
	 * 
	 * @param reqData
	 */
	public void validatePayLink(CCBData ccbData);

	/**
	 * 发送缴费信息
	 * 
	 * @param reqData
	 */
	public MsgData transferPayLink(CoreResultPayRes coreRsp);
	
    /**
     * 移动商城缴费
     * @param coreRsp
     * @return
     */
	public MsgData transferShopPayLink(CoreResultPayRes coreRsp);
	
}
