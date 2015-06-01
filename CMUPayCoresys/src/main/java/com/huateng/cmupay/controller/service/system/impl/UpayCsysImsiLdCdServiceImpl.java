package com.huateng.cmupay.controller.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.mapper.UpayCsysImsiLdCdMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysImsiLdCdService;
import com.huateng.cmupay.models.UpayCsysImsiLdCd;
import com.huateng.cmupay.models.common.Order;
import com.huateng.toolbox.utils.StrUtil;

@Service("upayCsysImsiLdCdService")
public class UpayCsysImsiLdCdServiceImpl implements IUpayCsysImsiLdCdService {
	@Autowired
	private UpayCsysImsiLdCdMapper upayCsysImsiLdCdMapper;

	@Override
	public void add(UpayCsysImsiLdCd obj) {
		upayCsysImsiLdCdMapper.insert(obj);
	}

	@Override
	public void modify(UpayCsysImsiLdCd obj) {
		upayCsysImsiLdCdMapper.updateByPrimaryKeySelective(obj);
	}

	@Override
	public void del(UpayCsysImsiLdCd obj) {
		Map<String, String> unionKey = new HashMap<String, String>();
		unionKey.put("imsiAreaId", obj.getImsiAreaId());
		unionKey.put("effcTm", obj.getEffcTm());
		upayCsysImsiLdCdMapper.deleteByUnionKey(unionKey);
	}

	@Override
	public UpayCsysImsiLdCd findObj(Map<String, Object> params) {
		return upayCsysImsiLdCdMapper.selectByParams(params);
	}

	@Override
	public List<UpayCsysImsiLdCd> findList(Map<String, Object> params,
			Order order) {
		// TODO
		return upayCsysImsiLdCdMapper.selectListByParams(params, 0, 0,
				order.getOrderType());
	}

	@Override
	public String findProvinceByMobileNumber(String num) {
		if (!StringUtils.isBlank(num) && num.length() >= 7) {
			String prov = upayCsysImsiLdCdMapper
					.selectProvinceByMobileNumber(StrUtil.subString(num, 0, 7));
			return StringUtils.isBlank(prov) ? null : prov;
		} else {
			return null;
		}
	}

	@Override
	public UpayCsysImsiLdCd findObjByUnionKey(Map<String, Object> unionKey) {
		return upayCsysImsiLdCdMapper.selectByParams(unionKey);
	}

}
