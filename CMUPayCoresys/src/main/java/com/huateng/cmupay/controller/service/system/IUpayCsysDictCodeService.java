package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysDictCode;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 * 数据字典明细
 */
public interface IUpayCsysDictCodeService {
	
	public void add(UpayCsysDictCode obj)  ;

	public void modify(UpayCsysDictCode obj)  ;

	public void del(UpayCsysDictCode obj)  ;

	public UpayCsysDictCode findObjByKey(Long seq)  ;

	public UpayCsysDictCode findObj(Map<String, Object> params)  ;

	public List<UpayCsysDictCode> findList(Map<String, Object> params, Order order)
			 ;

	
    
}