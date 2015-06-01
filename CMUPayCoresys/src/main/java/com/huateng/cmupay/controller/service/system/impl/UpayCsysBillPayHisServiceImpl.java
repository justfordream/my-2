package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.mapper.UpayCsysBillPayHisMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysBillPayMapper;
import com.huateng.cmupay.controller.his.mapper.UpayCsysTxnLogHisMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysTxnLogMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysBillPayHisService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.models.UpayCsysBillPay;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.his.UpayCsysBillPayHis;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;


/**
 * 
 * @author hdm
 *
 */
@Service("upayCsysBillPayHisService")
public class UpayCsysBillPayHisServiceImpl implements IUpayCsysBillPayHisService{
	
	@Autowired
	private UpayCsysBillPayHisMapper upayCsysBillPayHisMapper;
	@Autowired
	private UpayCsysTxnLogHisMapper upayCsysTxnLogHisMapper;
	@Autowired
	private UpayCsysBillPayMapper upayCsysBillPayMapper;
	@Autowired
	private UpayCsysTxnLogMapper upayCsysTxnLogMapper;

	@Override
	public void add(UpayCsysBillPayHis obj) {
		
	}

	@Override
	public void modify(UpayCsysBillPayHis obj) {
		
	}

	@Override
	public void del(UpayCsysBillPayHis obj) {
		
	}

	@Override
	public UpayCsysBillPayHis findObjByKey(Long seq) {
		return null;
	}

	@Override
	public UpayCsysBillPayHis findObj(Map<String, Object> params) {
		return upayCsysBillPayHisMapper.selectByParamsOld(params);
	}

	@Override
	public UpayCsysBillPayHis find(Map<String, Object> params) {
		return null;
	}

	@Override
	public List<UpayCsysBillPayHis> findList(Map<String, Object> params,Order order) {
		return null;
	}

	@Override
	public void modifyNotes(UpayCsysTxnLog txnLog, UpayCsysTxnLogHis logHis,UpayCsysBillPay bill, UpayCsysBillPayHis billHis) throws AppBizException {
		
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
		upayCsysBillPayMapper.insertSelective(bill);
		
		upayCsysBillPayHisMapper.updateByPrimaryKeySelective(billHis);
		upayCsysTxnLogHisMapper.updateByPrimaryKeySelective(logHis);
		
	}

	@Override
	public void modifyNote(UpayCsysTxnLogHis logHis, UpayCsysBillPayHis billHis)throws AppBizException {

		upayCsysTxnLogHisMapper.updateByPrimaryKeySelective(logHis);
		upayCsysBillPayHisMapper.updateByPrimaryKeySelective(billHis);
		
	}
}
