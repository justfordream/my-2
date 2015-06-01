package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysOrgMapTransCode;
import com.huateng.cmupay.models.common.Order;

public interface IUpayCsysOrgMapTransCodeService {
	public void add(UpayCsysOrgMapTransCode obj);

	public void modify(UpayCsysOrgMapTransCode obj);

	public void del(UpayCsysOrgMapTransCode obj);

	public UpayCsysOrgMapTransCode findObjByKey(Long seq);

	public UpayCsysOrgMapTransCode findObj(Map<String, Object> params);
}
