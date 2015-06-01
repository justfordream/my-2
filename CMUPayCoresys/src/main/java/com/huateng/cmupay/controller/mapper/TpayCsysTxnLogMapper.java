package com.huateng.cmupay.controller.mapper;

import com.huateng.cmupay.models.TpayCsysTxnLog;

import java.util.List;
import java.util.Map;

public interface TpayCsysTxnLogMapper extends IBaseMapper<TpayCsysTxnLog>{
	List<TpayCsysTxnLog> selectListOrderByparams(Map<String, Object> params);

}