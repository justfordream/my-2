package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.service.system.IUpayCsysDictInfoService;
import com.huateng.cmupay.models.UpayCsysDictInfo;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 *
 */
@Service("upayCsysDictInfoService")
public class UpayCsysDictInfoServiceImpl implements IUpayCsysDictInfoService{
	@Override
	public void add(UpayCsysDictInfo obj)   {
	}
	@Override
	public void modify(UpayCsysDictInfo obj)   {
		
	}
	@Override
	public void del(UpayCsysDictInfo obj)   {
	}
	@Override
	public UpayCsysDictInfo findObjByKey(Long seq)   {
		return null;
	}
	@Override
	public UpayCsysDictInfo findObj(Map<String, Object> params)   {
		return null;
	}
	@Override
	public List<UpayCsysDictInfo> findList(Map<String, Object> params, Order order)
			  {
		return null;
	}

	
   
}