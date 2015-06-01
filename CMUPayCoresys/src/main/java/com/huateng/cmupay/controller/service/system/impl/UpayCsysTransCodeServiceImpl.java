package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.mapper.UpayCsysTransCodeMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysTransCodeService;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 *
 */
@Service("upayCsysTransCodeService")
public class UpayCsysTransCodeServiceImpl implements IUpayCsysTransCodeService{
	@Autowired
	private UpayCsysTransCodeMapper upayCsysTransCodeMapper;
	
	@Override
	public void add(UpayCsysTransCode obj)   {
	}
	@Override
	public void modify(UpayCsysTransCode obj)   {
	}
	@Override
	public void del(UpayCsysTransCode obj)   {
	}
	@Override
	public UpayCsysTransCode findObjByKey(Long seq)   {
		return null;
	}
	@Override
	public UpayCsysTransCode findObj(Map<String, Object> params)   {
		return upayCsysTransCodeMapper.selectByParams(params);
	}
	@Override
	public List<UpayCsysTransCode> findList(Map<String, Object> params, Order order)
			  {
		return null;
	}

	
}