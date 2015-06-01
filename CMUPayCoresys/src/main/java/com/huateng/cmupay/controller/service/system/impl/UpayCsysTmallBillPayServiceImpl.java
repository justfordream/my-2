package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.DataSourceInstances;
import com.huateng.cmupay.controller.mapper.UpayCsysTmallBillPayMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysTmallTxnLogMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysTmallBillPayService;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTmallBillPay;
import com.huateng.cmupay.models.UpayCsysTmallTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
import com.huateng.toolbox.utils.DateUtil;

@Service("upayCsysTmallBillPayService")
public class UpayCsysTmallBillPayServiceImpl implements IUpayCsysTmallBillPayService{
	@Autowired
	private UpayCsysTmallBillPayMapper upayCsysTmallBillPayMapper;
	@Autowired
	private UpayCsysTmallTxnLogMapper upayCsysTmallTxnLogMapper;
	 
	@Override
	public void add(UpayCsysTmallBillPay obj) {
		upayCsysTmallBillPayMapper.insertSelective(obj);
	}

	@Override
	public void modify(UpayCsysTmallBillPay obj) {
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		obj.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTmallBillPayMapper.updateByPrimaryKeySelective(obj);				
	}

	@Override
	public void modifyLimit(UpayCsysTmallBillPay obj, UpayCsysBindInfo bindInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void del(UpayCsysTmallBillPay obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UpayCsysTmallBillPay findObjByKey(Long seq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpayCsysTmallBillPay findObj(Map<String, Object> params) {
		return upayCsysTmallBillPayMapper.selectByParams(params);
	}

	@Override
	public List<UpayCsysTmallBillPay> findList(Map<String, Object> params, Order order) {
		String orderParam = (order == null) ? null : order.toString();
		return upayCsysTmallBillPayMapper.selectListByParams(params, null, null, orderParam);
	}

	@Override
	public void modifyAdd(UpayCsysTmallBillPay obj1, UpayCsysTmallTxnLog obj2) {
//		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		obj2.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTmallBillPayMapper.insertSelective(obj1);
		upayCsysTmallTxnLogMapper.updateByPrimaryKeySelective(obj2);		
	}

}
