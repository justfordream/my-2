package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 * 内部交易代码
 */
public interface IUpayCsysTransCodeService  {
	
	public void add(UpayCsysTransCode obj) ;

	public void modify(UpayCsysTransCode obj) ;

	public void del(UpayCsysTransCode obj) ;

	public UpayCsysTransCode findObjByKey(Long seq) ;

	public UpayCsysTransCode findObj(Map<String, Object> params) ;

	public List<UpayCsysTransCode> findList(Map<String, Object> params, Order order)
			;

	
   
}