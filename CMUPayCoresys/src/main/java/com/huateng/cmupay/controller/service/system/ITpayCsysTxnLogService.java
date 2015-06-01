/**
 * 
 */
package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysBillPay;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.TpayCsysTxnLog;

/**
 * 
 * @author cmt
 * 第三方支付交易流水表
 */
public interface ITpayCsysTxnLogService {

	public void add(TpayCsysTxnLog obj)  ;

	public void modify(TpayCsysTxnLog obj)  ;
	
	public void modifyLimit(TpayCsysTxnLog obj, UpayCsysBindInfo bindInfo)  ;

	public void del(TpayCsysTxnLog obj)  ;

	public TpayCsysTxnLog findObjByKey(Long seq)  ;
	

	public TpayCsysTxnLog findObj(Map<String, Object> params)  ;
	
	public List<TpayCsysTxnLog> findList(Map<String, Object> params);
	
	public void modifyLog(TpayCsysTxnLog txnLog,TpayCsysTxnLog upayLog)  ;
	
	public void modifyTxnLogAndBillPay(TpayCsysTxnLog log,UpayCsysBillPay billPay);
	
	public void modifyTxnLogAndAddBillPay(TpayCsysTxnLog log,UpayCsysBillPay billPay);
}
