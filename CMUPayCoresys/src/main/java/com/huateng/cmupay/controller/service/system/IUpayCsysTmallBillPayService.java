package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTmallBillPay;
import com.huateng.cmupay.models.UpayCsysTmallTxnLog;
import com.huateng.cmupay.models.common.Order;

public interface IUpayCsysTmallBillPayService {
 
	public void add(UpayCsysTmallBillPay obj)  ;

	public void modify(UpayCsysTmallBillPay obj)  ;
	
	public void modifyAdd(UpayCsysTmallBillPay obj1, UpayCsysTmallTxnLog obj2);
	
	public void modifyLimit(UpayCsysTmallBillPay obj, UpayCsysBindInfo bindInfo)  ;

	public void del(UpayCsysTmallBillPay obj)  ;

	public UpayCsysTmallBillPay findObjByKey(Long seq)  ;

	public UpayCsysTmallBillPay findObj(Map<String, Object> params)  ;

	public List<UpayCsysTmallBillPay> findList(Map<String, Object> params, Order order);
}
