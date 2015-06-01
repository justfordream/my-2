package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysPayLimit;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;

/**
 * @author cmt
 *  缴费额度明细
 */
public interface IUpayCsysPayLimitService {
	
	public void add(UpayCsysPayLimit obj)  ;
	
	public boolean modifyLimitAdd(UpayCsysPayLimit obj,Long payAmt);
	public void modifyLimitDel(UpayCsysPayLimit obj,Long payAmt);

	public void modify(UpayCsysPayLimit obj)  ;

	public void del(UpayCsysPayLimit obj)  ;

	public UpayCsysPayLimit findObjByKey(Long seq)  ;

	public UpayCsysPayLimit findObj(Map<String, Object> params)  ;


	public void modifyLimit(UpayCsysTxnLog obj, UpayCsysBindInfo bindInfo);
			 ;

	public List<UpayCsysPayLimit> findList(Map<String, Object> params, Order order) ;


	public void modifyLimtsDells(UpayCsysTxnLog upayLog);

	public void modifyLimtsAdds(UpayCsysTxnLog upayLog);

	public void modifyLimtsDs(UpayCsysTxnLogHis logHis);

	public void modifyLimtsAs(UpayCsysTxnLogHis logHis);
   
}