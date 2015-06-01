package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;


import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTmallTxnLog;
import com.huateng.cmupay.models.common.Order;

/**
 * 
 * @author panlg
 * 天猫交易流水(过渡方案)
 */
public interface IUpayCsysTmallTxnLogService {

	public void add(UpayCsysTmallTxnLog obj)  ;

	public void modify(UpayCsysTmallTxnLog obj)  ;
	
	public void modifyLimit(UpayCsysTmallTxnLog obj, UpayCsysBindInfo bindInfo)  ;

	public void del(UpayCsysTmallTxnLog obj)  ;

	public UpayCsysTmallTxnLog findObjByKey(Long seq)  ;

	public UpayCsysTmallTxnLog findObj(Map<String, Object> params)  ;

	public List<UpayCsysTmallTxnLog> findList(Map<String, Object> params, Order order);
}
