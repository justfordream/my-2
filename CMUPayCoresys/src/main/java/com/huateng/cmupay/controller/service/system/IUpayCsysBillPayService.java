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
 * @author cmt
 * 交易成功明细
 */
public interface IUpayCsysBillPayService  {
	
	public void add(UpayCsysBillPay obj) ;

	public void modify(UpayCsysBillPay obj) ;

	public void del(UpayCsysBillPay obj) ;

	public UpayCsysBillPay findObjByKey(Long seq) ;

	public UpayCsysBillPay findObj(Map<String, Object> params) ;
	
	/**
	 * 重写
	 * @param params
	 * @return
	 */
	public UpayCsysBillPay find(Map<String, Object> params) ;

	public List<UpayCsysBillPay> findList(Map<String, Object> params, Order order);
	
	/**
	 * @param txnLog   新纪录流水
	 * @param upayLog  原流水记录
	 * @param bill	        新的记录成功明细
	 * @param upay	        原记录成功明细
	 * @throws AppBizException
	 */
	public void modifyNote(UpayCsysTxnLog txnLog,UpayCsysTxnLog upayLog,UpayCsysBillPay bill,UpayCsysBillPay upay) throws AppBizException;
	
	public void modifyNotes(UpayCsysTxnLog txnLog,UpayCsysTxnLogHis logHis,UpayCsysBillPay bill,UpayCsysBillPayHis billHis) throws AppBizException;

	public void modifyArg(UpayCsysTxnLog txnLog, UpayCsysTxnLogHis logHis,UpayCsysBillPay bill, UpayCsysBillPayHis billHis) throws AppBizException;
   
}
	
   
