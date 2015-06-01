package com.huateng.service;

import com.huateng.bean.TenData;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;

public interface TenPayService {

	public MsgData shopTenPayNotice(boolean isPage, TenData tenData)
			throws AppException;

}
