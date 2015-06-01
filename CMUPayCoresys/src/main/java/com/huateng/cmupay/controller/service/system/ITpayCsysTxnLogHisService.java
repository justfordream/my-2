/**
 * 
 */
package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.TpayCsysTxnLog;

/**
 * 
 * @author hdm
 *
 */
public interface ITpayCsysTxnLogHisService {

	public void add(TpayCsysTxnLog obj)  ;

	public void modify(TpayCsysTxnLog obj)  ;
	
	// 修改TPAY_CSYS_TXN_LOG_HIS_STL表
	public void modifyHisStl(TpayCsysTxnLog obj);
	
	public void modifyLimit(TpayCsysTxnLog obj)  ;

	public void del(TpayCsysTxnLog obj)  ;

	public TpayCsysTxnLog findObjByKey(Long seq)  ;
	
	public Long findFeeCheck(Map<String, Object> params)  ;

	public TpayCsysTxnLog findObj(Map<String, Object> params)  ;
	
	// 查找UPAY_CSYS_TXN_LOG_HIS_STL表
	public TpayCsysTxnLog findHisStlObj(Map<String, Object> params);

	public List<TpayCsysTxnLog> findList(Map<String, Object> params, Order order);
	
	public void modifyLog(TpayCsysTxnLog txnLog,TpayCsysTxnLog upayLog)  ;
	

}
