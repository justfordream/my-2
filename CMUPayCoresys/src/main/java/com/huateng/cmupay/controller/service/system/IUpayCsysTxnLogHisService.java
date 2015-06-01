/**
 * 
 */
package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;

/**
 * 
 * @author hdm
 *
 */
public interface IUpayCsysTxnLogHisService {

	public void add(UpayCsysTxnLogHis obj)  ;

	public void modify(UpayCsysTxnLogHis obj)  ;
	
	// 修改UPAY_CSYS_TXN_LOG_HIS_STL表
	public void modifyHisStl(UpayCsysTxnLogHis obj);
	
	public void modifyLimit(UpayCsysTxnLogHis obj)  ;

	public void del(UpayCsysTxnLogHis obj)  ;

	public UpayCsysTxnLogHis findObjByKey(Long seq)  ;
	
	public Long findFeeCheck(Map<String, Object> params)  ;

	public UpayCsysTxnLogHis findObj(Map<String, Object> params)  ;
	
	// 查找UPAY_CSYS_TXN_LOG_HIS_STL表
	public UpayCsysTxnLogHis findHisStlObj(Map<String, Object> params);

	public List<UpayCsysTxnLogHis> findList(Map<String, Object> params, Order order);
	
	public void modifyLog(UpayCsysTxnLogHis txnLog,UpayCsysTxnLogHis upayLog)  ;
	

}
