package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 * 路由信息
 */
public interface IUpayCsysRouteInfoService  {
	
	public void add(UpayCsysRouteInfo obj) ;

	public void modify(UpayCsysRouteInfo obj) ;

	public void del(UpayCsysRouteInfo obj) ;

	public UpayCsysRouteInfo findObjByKey(Long seq) ;

	public UpayCsysRouteInfo findObj(Map<String, Object> params) ;

	public List<UpayCsysRouteInfo> findList(Map<String, Object> params, Order order)
		;

	
    
}