package com.huateng.cmupay.controller.mapper;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysTxnLog;

public interface UpayCsysTxnLogMapper extends IBaseMapper<UpayCsysTxnLog>{

	List<UpayCsysTxnLog> selectListOrderByparams(Map<String, Object> params);

	UpayCsysTxnLog selectForIsResend(Map<String, Object> params);
    
}