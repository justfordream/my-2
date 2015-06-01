package com.huateng.cmupay.controller.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogTmpService;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysPayLimit;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.controller.mapper.UpayCsysPayLimitMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysTxnLogTmpMapper;


@Service("upayCsysTxnLogTmpService")
public class UpayCsysTxnLogTmpServiceImpl implements IUpayCsysTxnLogTmpService{
	@Autowired
	private UpayCsysTxnLogTmpMapper upayCsysTxnLogTmpMapper;
//	@Autowired
//	private UpayCsysBillPayMapper upayCsysBillPayMapper;
	@Autowired
	private UpayCsysPayLimitMapper upayCsysPayLimitMapper;
	@Override
	public void add(UpayCsysTxnLogTmp obj) {
		upayCsysTxnLogTmpMapper.insertSelective(obj);
	}

	@Override
	public void modify(UpayCsysTxnLogTmp obj) {
		upayCsysTxnLogTmpMapper.updateByPrimaryKeySelective(obj);
	}

	@Override
	public void del(UpayCsysTxnLogTmp obj) {
	}

	@Override
	public UpayCsysTxnLogTmp findObjByKey(Long seq) {
		return null;
	}

	@Override
	public UpayCsysTxnLogTmp findObj(Map<String, Object> params) {
		return upayCsysTxnLogTmpMapper.selectByParams(params);
	}

	@Override
	public UpayCsysTxnLogTmp find(Map<String, Object> params) {
		return upayCsysTxnLogTmpMapper.selectByParams(params);
	}

	@Override
	public List<UpayCsysTxnLogTmp> findList(Map<String, Object> params,
			Order order) {
		return null;
	}

//	@Override
//	public void modifyTxnLogTmpAndBillPay(UpayCsysTxnLogTmp logTmp,
//			UpayCsysBillPay bill) {
//		upayCsysTxnLogTmpMapper.updateByPrimaryKeySelective(logTmp);
//		upayCsysBillPayMapper.updateByPrimaryKeySelective(bill);			
//	}

//	@Override
//	public void modifyTxnLogTmpAndLimit(UpayCsysTxnLogTmp logTmp,
//			UpayCsysBindInfo bindInfo) {
//			String idValue = null;
//			if (null == bindInfo || null == bindInfo.getIdValue()
//					|| "".equals(bindInfo.getIdValue())) {
//				idValue = logTmp.getIdValue();
//			}else{
//				if (CommonConstant.Mainflag.Slave.toString().equals(
//						bindInfo.getMainFlag())) {
//					idValue = bindInfo.getMainIdValue();
//				}else{
//					idValue = bindInfo.getIdValue();
//				}
//			}
//
//			String payMonth = logTmp.getIntTxnDate().substring(0, 6);
//			upayCsysTxnLogTmpMapper.updateByPrimaryKeySelective(logTmp);
//
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("idValue", idValue);
//			params.put("payMonth", payMonth);
//			UpayCsysPayLimit limit = upayCsysPayLimitMapper
//					.selectByParamsLock(params);
//			limit.setIdValue(idValue);
//			limit.setPayMonth(payMonth);
//			limit.delMonthAmount(logTmp.getPayAmt());
//			upayCsysPayLimitMapper.updateByPrimaryKeySelective(limit);
//		
//	}

}
