package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.h2.mapper.TUpayBankThrCodeh2Mapper;
import com.huateng.cmupay.controller.mapper.TUpayBankThrCodeMapper;
import com.huateng.cmupay.controller.service.system.TUpayBankThrCodeService;
import com.huateng.cmupay.models.TUpayBankThrCode;
import com.huateng.cmupay.models.common.Order;
/**
 * Author:oul
 * 银行代码表操作service接口实现类
 * */
@Service("tupayBankThrCodeService")
public class TUpayBankThrCodeServiceImpl implements TUpayBankThrCodeService{
	@Autowired
	TUpayBankThrCodeMapper tupayBankThrCodeMapper;

	@Override
	public void add(TUpayBankThrCode tUpayBankThrCode) {
		tupayBankThrCodeMapper.insertSelective(tUpayBankThrCode);
	}

	@Override
	public void modify(TUpayBankThrCode tUpayBankThrCode) {
		tupayBankThrCodeMapper.updateByPrimaryKeySelective(tUpayBankThrCode);
	}

	@Override
	public TUpayBankThrCode find(Map<String, Object> params) {
		return tupayBankThrCodeMapper.selectByParams(params);
	}

	@Override
	public List<TUpayBankThrCode> findList(Map<String, Object> params,
			Order order) {
		return tupayBankThrCodeMapper.selectAllListByParams(params,null);
	}

}
