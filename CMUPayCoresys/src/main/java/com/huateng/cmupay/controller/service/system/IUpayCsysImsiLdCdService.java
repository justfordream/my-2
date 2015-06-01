package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysImsiLdCd;
import com.huateng.cmupay.models.common.Order;

public interface IUpayCsysImsiLdCdService {
	public void add(UpayCsysImsiLdCd obj);

	public void modify(UpayCsysImsiLdCd obj);

	public void del(UpayCsysImsiLdCd obj);

	public UpayCsysImsiLdCd findObjByUnionKey(Map<String,Object> unionKey);
	
	public UpayCsysImsiLdCd findObj(Map<String, Object> params);

	public List<UpayCsysImsiLdCd> findList(Map<String, Object> params,
			Order order);
	public String findProvinceByMobileNumber(String num);
}
