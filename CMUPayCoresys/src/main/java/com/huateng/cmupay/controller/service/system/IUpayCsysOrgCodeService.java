package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysOrgCode;
import com.huateng.cmupay.models.common.Order;

public interface IUpayCsysOrgCodeService {

	public void add(UpayCsysOrgCode obj);

	public void modify(UpayCsysOrgCode obj) ;

	public void del(UpayCsysOrgCode obj) ;

	public UpayCsysOrgCode findObjByKey(Long seq) ;

	public UpayCsysOrgCode findObj(Map<String, Object> params) ;

	public List<UpayCsysOrgCode> findList(Map<String, Object> params, Order order);
}
