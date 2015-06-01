/**
 * 
 */
package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;

/**
 * 
 * @author cmt
 * 第三方支付交易流水临时表
 */
public interface ITpayCsysTxnLogTmpService {

	public void add(TpayCsysTxnLog obj) ;

	public void modify(TpayCsysTxnLog obj) ;

	public void del(TpayCsysTxnLog obj) ;

	public TpayCsysTxnLog findObjByKey(Long seq) ;

	public TpayCsysTxnLog findObj(Map<String, Object> params) ;
	
	public TpayCsysTxnLog find(Map<String, Object> params) ;

	public List<TpayCsysTxnLog> findList(Map<String, Object> params, Order order);
}
