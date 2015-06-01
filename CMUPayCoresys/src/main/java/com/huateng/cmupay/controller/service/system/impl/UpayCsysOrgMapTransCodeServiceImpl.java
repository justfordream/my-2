package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.controller.mapper.UpayCsysOrgMapTransCodeMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysOrgMapTransCodeService;
import com.huateng.cmupay.models.UpayCsysOrgMapTransCode;
import com.huateng.cmupay.models.common.Order;

public class UpayCsysOrgMapTransCodeServiceImpl implements
		IUpayCsysOrgMapTransCodeService {
	private UpayCsysOrgMapTransCodeMapper upayCsysOrgMapTransCodeMapper;
	@Override
	public void add(UpayCsysOrgMapTransCode obj) {
	}

	@Override
	public void modify(UpayCsysOrgMapTransCode obj) {
	}

	@Override
	public void del(UpayCsysOrgMapTransCode obj) {
	}

	@Override
	public UpayCsysOrgMapTransCode findObjByKey(Long seq) {
		return null;
	}

	@Override
	public UpayCsysOrgMapTransCode findObj(Map<String, Object> params) {
		return upayCsysOrgMapTransCodeMapper.selectByParams(params);
	}
}
