package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysOrgTransCode;
import com.huateng.cmupay.models.common.Order;

/**
 * @author cmt
 *  机构交易权限
 */
public interface IUpayCsysOrgTransCodeService {
	
	public void add(UpayCsysOrgTransCode obj)  ;

	public void modify(UpayCsysOrgTransCode obj)  ;

	public void del(UpayCsysOrgTransCode obj)  ;

	public UpayCsysOrgTransCode findObjByKey(Long seq)  ;

	public UpayCsysOrgTransCode findObj(Map<String, Object> params)  ;

	public List<UpayCsysOrgTransCode> findList(Map<String, Object> params, Order order)
			 ;


   
}