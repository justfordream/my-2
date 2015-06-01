package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.mapper.UpayCsysRouteInfoMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysRouteInfoService;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 *
 */
@Service("upayCsysRouteInfoService")
public class UpayCsysRouteInfoServiceImpl implements IUpayCsysRouteInfoService{
	@Autowired
	private UpayCsysRouteInfoMapper  upayCsysRouteInfoMapper;
	@Override
	public void add(UpayCsysRouteInfo obj)   {
	}
	@Override
	public void modify(UpayCsysRouteInfo obj)   {
	}
	@Override
	public void del(UpayCsysRouteInfo obj)   {
	}
	@Override
	public UpayCsysRouteInfo findObjByKey(Long seq)   {
		return null;
	}

	@Override
	public UpayCsysRouteInfo findObj(Map<String, Object> params) {

		UpayCsysRouteInfo upayCsysRouteInfo = upayCsysRouteInfoMapper
				.selectByParams(params);
		return upayCsysRouteInfo;
	}
	@Override
	public List<UpayCsysRouteInfo> findList(Map<String, Object> params, Order order)
			  {
		return null;
	}

	
    
}