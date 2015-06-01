package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysDictInfo;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 * 数据字典
 */
public interface IUpayCsysDictInfoService {
	
	public void add(UpayCsysDictInfo obj) ;

	public void modify(UpayCsysDictInfo obj) ;

	public void del(UpayCsysDictInfo obj) ;

	public UpayCsysDictInfo findObjByKey(Long seq) ;

	public UpayCsysDictInfo findObj(Map<String, Object> params) ;

	public List<UpayCsysDictInfo> findList(Map<String, Object> params, Order order);

	
}