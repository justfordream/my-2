package com.huateng.service;

import com.huateng.bean.ShopData;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;

public interface RcvPayShopOpenService {
	
	public MsgData sendPayShopOpenRequest(ShopData cmuData) throws AppException;
}
