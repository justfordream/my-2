package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.mapper.UpayCsysOrgTransCodeMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysOrgTransCodeService;
import com.huateng.cmupay.models.UpayCsysOrgTransCode;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 *
 */
@Service("upayCsysOrgTransCodeService")
public class UpayCsysOrgTransCodeServiceImpl implements IUpayCsysOrgTransCodeService{
	@Autowired
	private UpayCsysOrgTransCodeMapper upayCsysOrgTransCodeMapper;
	@Override
	public void add(UpayCsysOrgTransCode obj)  {
	}
	@Override
	public void modify(UpayCsysOrgTransCode obj)  {
	}
	@Override
	public void del(UpayCsysOrgTransCode obj)  {
	}
	@Override
	public UpayCsysOrgTransCode findObjByKey(Long seq)  {
		return null;
	}
	@Override
	public UpayCsysOrgTransCode findObj(Map<String, Object> params)  {
		
		UpayCsysOrgTransCode upayCsysOrgTransCode =	upayCsysOrgTransCodeMapper.selectByParams(params);
		return upayCsysOrgTransCode;
	}
	@Override
	public List<UpayCsysOrgTransCode> findList(Map<String, Object> params, Order order)
			{
		return null;
	}

	
   
}