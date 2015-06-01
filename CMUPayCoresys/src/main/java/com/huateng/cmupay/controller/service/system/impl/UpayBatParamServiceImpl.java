/**
 * 
 */
package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.his.mapper.UpayBatParamMapper;
import com.huateng.cmupay.controller.service.system.IUpayBatParamService;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.his.UpayBatParam;

/**
 * 
 * @author hdm
 *
 */

@Service("upayBatParamService")
public class UpayBatParamServiceImpl implements IUpayBatParamService {
	
	@Autowired
	private UpayBatParamMapper upayBatParamMapper;
	

	@Override
	public UpayBatParam findObj(Map<String, Object> params) {
		return upayBatParamMapper.selectByParams(params);
	}

	@Override
	public List<UpayBatParam> findList(Map<String, Object> params, Order order) {
		return null;
	}



}
