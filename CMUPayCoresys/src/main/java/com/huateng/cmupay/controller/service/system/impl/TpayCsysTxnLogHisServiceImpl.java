/**
 * 
 */
package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.his.mapper.TpayCsysTxnLogHisMapper;
import com.huateng.cmupay.controller.his.mapper.TpayCsysTxnLogHisStlMapper;
import com.huateng.cmupay.controller.his.mapper.UpayCsysTxnLogHisMapper;
import com.huateng.cmupay.controller.his.mapper.UpayCsysTxnLogHisStlMapper;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogHisService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogHisService;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;

/**
 * 
 * @author hdm
 *
 */

@Service("tpayCsysTxnLogHisService")
public class TpayCsysTxnLogHisServiceImpl implements ITpayCsysTxnLogHisService {
	@Autowired
	private TpayCsysTxnLogHisMapper tpayCsysTxnLogHisMapper;
	
	@Autowired
	private TpayCsysTxnLogHisStlMapper tpayCsysTxnLogHisStlMapper;

	@Override
	public void add(TpayCsysTxnLog obj) {
		
	}

	@Override
	public void modify(TpayCsysTxnLog obj) {
		tpayCsysTxnLogHisMapper.updateByPrimaryKeySelective(obj);
		
	}

	@Override
	public void modifyLimit(TpayCsysTxnLog obj) {
		
	}

	@Override
	public void del(TpayCsysTxnLog obj) {
		
	}

	@Override
	public TpayCsysTxnLog findObjByKey(Long seq) {
		return null;
	}

	@Override
	public Long findFeeCheck(Map<String, Object> params) {
		return null;
	}

	@Override
	public TpayCsysTxnLog findObj(Map<String, Object> params) {
		return tpayCsysTxnLogHisMapper.selectByParams(params);
	}

	@Override
	public List<TpayCsysTxnLog> findList(Map<String, Object> params,
			Order order) {
		return null;
	}

	@Override
	public void modifyLog(TpayCsysTxnLog txnLog, TpayCsysTxnLog upayLog) {
		
	}

	@Override
	public void modifyHisStl(TpayCsysTxnLog obj) {
		tpayCsysTxnLogHisStlMapper.updateByPrimaryKeySelective(obj);
		
	}

	@Override
	public TpayCsysTxnLog findHisStlObj(Map<String, Object> params) {
		return tpayCsysTxnLogHisStlMapper.selectByParams(params);
	}
	


}
