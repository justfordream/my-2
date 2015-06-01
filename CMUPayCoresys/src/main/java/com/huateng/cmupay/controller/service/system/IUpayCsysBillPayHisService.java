package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.models.UpayCsysBillPay;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.his.UpayCsysBillPayHis;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;

/**
 * 
 * @author hdm
 *
 */
public interface IUpayCsysBillPayHisService  {
	
	public void add(UpayCsysBillPayHis obj) ;

	public void modify(UpayCsysBillPayHis obj) ;

	public void del(UpayCsysBillPayHis obj) ;

	public UpayCsysBillPayHis findObjByKey(Long seq) ;

	public UpayCsysBillPayHis findObj(Map<String, Object> params) ;
	
	public UpayCsysBillPayHis find(Map<String, Object> params) ;

	public List<UpayCsysBillPayHis> findList(Map<String, Object> params, Order order);
	
	/**
	 * 
	 * @param txnLog
	 * @param logHis
	 * @param bill
	 * @param billHis
	 * @throws AppBizException
	 */
	public void modifyNotes(UpayCsysTxnLog txnLog,UpayCsysTxnLogHis logHis,UpayCsysBillPay bill,UpayCsysBillPayHis billHis) throws AppBizException;
	
	public void modifyNote(UpayCsysTxnLogHis logHis,UpayCsysBillPayHis billHis) throws AppBizException;
	
	
}
	
   
