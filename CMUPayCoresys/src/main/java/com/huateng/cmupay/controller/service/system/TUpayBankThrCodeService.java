package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.TUpayBankThrCode;
import com.huateng.cmupay.models.common.Order;
/**
 * Author:oul
 * 银行代码表service接口
 * */
public interface TUpayBankThrCodeService {
	
	public void add(TUpayBankThrCode tUpayBankThrCode);
	
	public void modify(TUpayBankThrCode tUpayBankThrCode);
	
	public TUpayBankThrCode find(Map<String,Object> params);
	
	public List<TUpayBankThrCode> findList(Map<String,Object> params,Order order);
	
}
