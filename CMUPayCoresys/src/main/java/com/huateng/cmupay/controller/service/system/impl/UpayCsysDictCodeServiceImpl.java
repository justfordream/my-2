package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.mapper.UpayCsysDictCodeMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysDictCodeService;
import com.huateng.cmupay.models.UpayCsysDictCode;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 *
 */
@Service("upayCsysDictCodeService")
public class UpayCsysDictCodeServiceImpl implements IUpayCsysDictCodeService {
	@Autowired
	private UpayCsysDictCodeMapper upayCsysDictCodeMapper;

	
	
	@Override
	public void add(UpayCsysDictCode obj)   {
	}
	@Override
	public void modify(UpayCsysDictCode obj)   {
	}
	@Override
	public void del(UpayCsysDictCode obj)   {
	}
	@Override
	public UpayCsysDictCode findObjByKey(Long seq)   {
		return null;
	}
	@Override
	public UpayCsysDictCode findObj(Map<String, Object> params)   {
		return upayCsysDictCodeMapper.selectByParams(params);
	}
	@Override
	public List<UpayCsysDictCode> findList(Map<String, Object> params, Order order)
			  {
		return null;
	}



    
}