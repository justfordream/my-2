/**
 * 
 */
package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.his.mapper.UpayCsysTxnLogHisMapper;
import com.huateng.cmupay.controller.his.mapper.UpayCsysTxnLogHisStlMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogHisService;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;

/**
 * 
 * @author hdm
 *
 */

@Service("upayCsysTxnLogHisService")
public class UpayCsysTxnLogHisServiceImpl implements IUpayCsysTxnLogHisService {
	
	@Autowired
	private UpayCsysTxnLogHisMapper upayCsysTxnLogHisMapper;
	
	@Autowired
	private UpayCsysTxnLogHisStlMapper upayCsysTxnLogHisStlMapper;

	@Override
	public void add(UpayCsysTxnLogHis obj) {
		
	}

	@Override
	public void modify(UpayCsysTxnLogHis obj) {
		upayCsysTxnLogHisMapper.updateByPrimaryKeySelective(obj);
		
	}

	@Override
	public void modifyLimit(UpayCsysTxnLogHis obj) {
		
	}

	@Override
	public void del(UpayCsysTxnLogHis obj) {
		
	}

	@Override
	public UpayCsysTxnLogHis findObjByKey(Long seq) {
		return null;
	}

	@Override
	public Long findFeeCheck(Map<String, Object> params) {
		return null;
	}

	@Override
	public UpayCsysTxnLogHis findObj(Map<String, Object> params) {
		return upayCsysTxnLogHisMapper.selectByParams(params);
	}

	@Override
	public List<UpayCsysTxnLogHis> findList(Map<String, Object> params,
			Order order) {
		return null;
	}

	@Override
	public void modifyLog(UpayCsysTxnLogHis txnLog, UpayCsysTxnLogHis upayLog) {
		
	}

	@Override
	public void modifyHisStl(UpayCsysTxnLogHis obj) {
		upayCsysTxnLogHisStlMapper.updateByPrimaryKeySelective(obj);
		
	}

	@Override
	public UpayCsysTxnLogHis findHisStlObj(Map<String, Object> params) {
		return upayCsysTxnLogHisStlMapper.selectByParams(params);
	}


}
