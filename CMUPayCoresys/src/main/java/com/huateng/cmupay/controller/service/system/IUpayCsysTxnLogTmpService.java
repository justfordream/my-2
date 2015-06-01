package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.models.common.Order;

public interface IUpayCsysTxnLogTmpService {

	public void add(UpayCsysTxnLogTmp obj) ;

	public void modify(UpayCsysTxnLogTmp obj) ;

	public void del(UpayCsysTxnLogTmp obj) ;

	public UpayCsysTxnLogTmp findObjByKey(Long seq) ;

	public UpayCsysTxnLogTmp findObj(Map<String, Object> params) ;
	
	public UpayCsysTxnLogTmp find(Map<String, Object> params) ;

	public List<UpayCsysTxnLogTmp> findList(Map<String, Object> params, Order order);
	
//	public void modifyTxnLogTmpAndBillPay(UpayCsysTxnLogTmp logTmp,UpayCsysBillPay bill);
	
//	public void modifyTxnLogTmpAndLimit(UpayCsysTxnLogTmp logTmp,UpayCsysBindInfo info);
}
