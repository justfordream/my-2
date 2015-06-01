package com.huateng.service;

import com.huateng.bean.AliData;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;

public interface AliPayService {

	public MsgData shopAliPayNotice(boolean isPage, AliData aliData)
			throws AppException;

}
