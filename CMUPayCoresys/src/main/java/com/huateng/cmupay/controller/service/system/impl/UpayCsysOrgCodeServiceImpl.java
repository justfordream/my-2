package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.mapper.UpayCsysOrgCodeMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysOrgCodeService;
import com.huateng.cmupay.models.UpayCsysOrgCode;
import com.huateng.cmupay.models.common.Order;

/**
 * 
 * @author hdm
 *
 */
@Service("upayCsysOrgCodeService")
public class UpayCsysOrgCodeServiceImpl implements IUpayCsysOrgCodeService{
	@Autowired
	private UpayCsysOrgCodeMapper  upayCsysOrgCodeMapper;

	@Override
	public void add(UpayCsysOrgCode obj) {
		
	}

	@Override
	public void modify(UpayCsysOrgCode obj) {
		
	}

	@Override
	public void del(UpayCsysOrgCode obj) {
		
	}

	@Override
	public UpayCsysOrgCode findObjByKey(Long seq) {
		return null;
	}

	@Override
	public UpayCsysOrgCode findObj(Map<String, Object> params) {
	     UpayCsysOrgCode orgCode = upayCsysOrgCodeMapper.selectByParams(params);
		return orgCode;
	}

	@Override
	public List<UpayCsysOrgCode> findList(Map<String, Object> params,
			Order order) {
		return null;
	}

}
