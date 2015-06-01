/**
 * 
 */
package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysBillPay;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;

/**
 * 
 * @author cmt
 * 交易流水以及历史流水
 */
public interface IUpayCsysTxnLogService {

	public void add(UpayCsysTxnLog obj)  ;

	public void modify(UpayCsysTxnLog obj)  ;
	
	public void modifyLimit(UpayCsysTxnLog obj, UpayCsysBindInfo bindInfo)  ;

	public void del(UpayCsysTxnLog obj)  ;

	public UpayCsysTxnLog findObjByKey(Long seq)  ;
	

	public UpayCsysTxnLog findObj(Map<String, Object> params)  ;
	
	public UpayCsysTxnLog findIsResend(Map<String, Object> params);

	public List<UpayCsysTxnLog> findList(Map<String, Object> params);
	
	public void modifyLog(UpayCsysTxnLog txnLog,UpayCsysTxnLog upayLog)  ;
	
	public void modifyTxnLogAndBillPay(UpayCsysTxnLog log,UpayCsysBillPay billPay);
	
	public void modifyTxnLogAndAddBillPay(UpayCsysTxnLog log,UpayCsysBillPay billPay);
	
//	public void modifyTxnLogTmpAndBillPay(UpayCsysTxnLogTmp log,UpayCsysBillPay billPay);
	
	//public Map<String,Object> findTxnLogAndBillPay(String billPay,String log,Map<String,Object> params);
	
}
