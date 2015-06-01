package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.DataSourceInstances;
import com.huateng.cmupay.controller.mapper.UpayCsysTmallTxnLogMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysTmallTxnLogService;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTmallTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
import com.huateng.toolbox.utils.DateUtil;

@Service("upayCsysTmallTxnLogService")
public class UpayCsysTmallTxnLogServiceImpl implements IUpayCsysTmallTxnLogService{
	@Autowired
	private UpayCsysTmallTxnLogMapper upayCsysTmallTxnLogMapper;
	
	@Override
	public void add(UpayCsysTmallTxnLog obj) {
		upayCsysTmallTxnLogMapper.insertSelective(obj);
	}

	@Override
	public void modify(UpayCsysTmallTxnLog obj) {
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		obj.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTmallTxnLogMapper.updateByPrimaryKeySelective(obj);		
	}

	@Override
	public void modifyLimit(UpayCsysTmallTxnLog obj, UpayCsysBindInfo bindInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void del(UpayCsysTmallTxnLog obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UpayCsysTmallTxnLog findObjByKey(Long seq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpayCsysTmallTxnLog findObj(Map<String, Object> params) {
		return upayCsysTmallTxnLogMapper.selectByParams(params);
	}

	@Override
	public List<UpayCsysTmallTxnLog> findList(Map<String, Object> params, Order order) {
		String orderParam = (order == null) ? null : order.toString();
		return upayCsysTmallTxnLogMapper.selectListByParams(params, null, null, orderParam);
	}

}
