package com.huateng.service;

import com.huateng.bean.CCBData;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;

/**
 * 建行业务处理接口
 * 
 * @author Gary
 * 
 */
public interface CCBService {

	public MsgData signNotice(boolean isPage,CCBData ccbData) throws AppException;

	public MsgData payNotice(boolean isPage,CCBData ccbData)throws AppException;
}
