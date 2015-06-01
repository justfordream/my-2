package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogTmpService;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.controller.mapper.TpayCsysTxnLogTmpMapper;


@Service("tpayCsysTxnLogTmpService")
public class TpayCsysTxnLogTmpServiceImpl implements ITpayCsysTxnLogTmpService{
	@Autowired
	private TpayCsysTxnLogTmpMapper tpayCsysTxnLogTmpMapper;
	
	@Override
	public void add(TpayCsysTxnLog obj) {
		tpayCsysTxnLogTmpMapper.insertSelective(obj);
	}

	@Override
	public void modify(TpayCsysTxnLog obj) {
		tpayCsysTxnLogTmpMapper.updateByPrimaryKeySelective(obj);
	}

	@Override
	public void del(TpayCsysTxnLog obj) {
	}

	@Override
	public TpayCsysTxnLog findObjByKey(Long seq) {
		return null;
	}

	@Override
	public TpayCsysTxnLog findObj(Map<String, Object> params) {
		return tpayCsysTxnLogTmpMapper.selectByParams(params);
	}

	@Override
	public TpayCsysTxnLog find(Map<String, Object> params) {
		return tpayCsysTxnLogTmpMapper.selectByParams(params);
	}

	@Override
	public List<TpayCsysTxnLog> findList(Map<String, Object> params,
			Order order) {
		return null;
	}


}
