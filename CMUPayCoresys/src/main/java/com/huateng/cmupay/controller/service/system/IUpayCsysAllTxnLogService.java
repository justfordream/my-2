/**
 * 
 */
package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysAllTxnLog;
import com.huateng.cmupay.models.UpayCsysBillPay;
import com.huateng.cmupay.models.UpayCsysPayLimit;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;

/**
 * 
 * @author cmt
 * 
 */
public interface IUpayCsysAllTxnLogService {

	public void add(UpayCsysAllTxnLog obj);

	public void modify(UpayCsysAllTxnLog obj);

	public void del(UpayCsysAllTxnLog obj);

	public UpayCsysAllTxnLog findObjByKey(Long seq);

	

	public UpayCsysAllTxnLog findObj(Map<String, Object> params);

	public List<UpayCsysAllTxnLog> findList(Map<String, Object> params,
			Order order);

	void addAll(UpayCsysBillPay upayCsysBillPay,
			UpayCsysPayLimit upayCsysPayLimit, UpayCsysTxnLog upayCsysTxnLog);

}
