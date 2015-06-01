/**
 * 
 */
package com.huateng.cmupay.controller.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.mapper.UpayCsysBillPayMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysTxnLogMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysAllTxnLogService;
import com.huateng.cmupay.models.UpayCsysAllTxnLog;
import com.huateng.cmupay.models.UpayCsysBillPay;
import com.huateng.cmupay.models.UpayCsysPayLimit;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.toolbox.utils.DateUtil;

/**
 * @author cmt
 * 
 */

@Service("upayCsysAllTxnLogService")
public class UpayCsysAllTxnLogServiceImpl implements IUpayCsysAllTxnLogService {
	
	@Autowired
	private	UpayCsysBillPayMapper upayCsysBillPayMapper;
//	@Autowired
//	private	UpayCsysPayLimitMapper upayCsysPayLimitMapper;
	@Autowired
	private	UpayCsysTxnLogMapper upayCsysTxnLogMapper;
	

	@Override
	public void add(UpayCsysAllTxnLog obj)   {
		
	}
	
	@Override
	public void modify(UpayCsysAllTxnLog obj)   {
		
	}
	@Override
	public void del(UpayCsysAllTxnLog obj)   {
		
	}
	@Override
	public UpayCsysAllTxnLog findObjByKey(Long seq)   {
		return null;
	}
	@Override
	public UpayCsysAllTxnLog findObj(Map<String, Object> params)   {
		return null;
	}
	@Override
	public List<UpayCsysAllTxnLog> findList(Map<String, Object> params, Order order)
			  {
	//	String orderParam = (order == null) ? null : order.toString();
		return null;
	
	}

	@Override
	public void addAll(UpayCsysBillPay upayCsysBillPay,
			UpayCsysPayLimit upayCsysPayLimit, UpayCsysTxnLog upayCsysTxnLog) {
		//为防止在临界点5000的时候出现问题先锁表 但是感觉不需要
		
		//如果先预先加入金额，在失败的时候在扣除感觉也不必要。
		
		//upayCsysPayLimitMapper.selectByParamsLock(new HashMap());
		Map<String, Object> params =new HashMap<String, Object>(); 
		params.put("IdValue", upayCsysPayLimit.getIdValue());
		params.put("payMonth", upayCsysPayLimit.getPayMonth());
//		UpayCsysPayLimit upayCsysPayLimitTemp =	upayCsysPayLimitMapper.selectByParams(params);//TODO 此处需要优化，这样性能比较低
//		if(upayCsysPayLimitTemp == null){
//			upayCsysPayLimitMapper.insertSelective(upayCsysPayLimit);
//		}else{
//			upayCsysPayLimit.addMonthAmount(upayCsysPayLimitTemp.getMonthAmount());
//			upayCsysPayLimitMapper.updateByPrimaryKeySelective(upayCsysPayLimit);
//		}
		
		upayCsysBillPayMapper.insertSelective(upayCsysBillPay);
		upayCsysTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(upayCsysTxnLog);
		

	}

}
