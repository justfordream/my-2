package com.huateng.cmupay.controller.mapper;

import com.huateng.cmupay.models.TpayCsysTxnLog;

public interface TpayCsysTxnLogTmpMapper extends IBaseMapper<TpayCsysTxnLog>{
    int insert(TpayCsysTxnLog record);

    int insertSelective(TpayCsysTxnLog record);
}