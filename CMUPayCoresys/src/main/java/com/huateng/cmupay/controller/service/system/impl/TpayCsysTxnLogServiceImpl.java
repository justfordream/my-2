/**
 * 
 */
package com.huateng.cmupay.controller.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DataSourceInstances;
import com.huateng.cmupay.controller.mapper.TpayCsysTxnLogMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysPayLimitMapper;
import com.huateng.cmupay.controller.mapper.TpayCsysTxnLogMapper;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.models.UpayCsysBillPay;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysPayLimit;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
import com.huateng.toolbox.utils.DateUtil;

/**
 * @author Manzhizhen
 * 
 */

@Service("tpayCsysTxnLogService")
public class TpayCsysTxnLogServiceImpl implements ITpayCsysTxnLogService {

	@Autowired
	private TpayCsysTxnLogMapper tpayCsysTxnLogMapper;

	@Autowired
	private UpayCsysPayLimitMapper upayCsysPayLimitMapper;

	@Override
	public void add(TpayCsysTxnLog obj) {
		tpayCsysTxnLogMapper.insertSelective(obj);
	}

	@Override
	public void modify(TpayCsysTxnLog obj) {
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		obj.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogMapper.updateByPrimaryKeySelective(obj);
	}

	/**
	 * 范魁 同时更新成功表和流水表
	 */
	public void modifyTxnLogAndBillPay(TpayCsysTxnLog log,
			UpayCsysBillPay billPay) {
		log.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogMapper.updateByPrimaryKeySelective(log);
	}

	public void modifyTxnLogAndAddBillPay(TpayCsysTxnLog log,
			UpayCsysBillPay billPay) {
		log.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogMapper.updateByPrimaryKeySelective(log);
	}

	public void modifyLimit(TpayCsysTxnLog obj, UpayCsysBindInfo bindInfo) {
		String idValue = null;
		String idType = null;

		if (CommonConstant.Mainflag.Slave.toString().equals(
				bindInfo.getMainFlag())) {
			idValue = bindInfo.getMainIdValue();
			idType = bindInfo.getMainIdType();
		} else {
			idValue = bindInfo.getIdValue();
			idType = bindInfo.getIdType();
		}

		String payMonth = obj.getIntTxnDate().substring(0, 6);
		obj.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogMapper.updateByPrimaryKeySelective(obj);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idType", idType);
		params.put("idValue", idValue);
		params.put("payMonth", payMonth);
		UpayCsysPayLimit limit = upayCsysPayLimitMapper
				.selectByParamsLock(params);
		limit.setIdType(idType);
		limit.setIdValue(idValue);
		limit.setPayMonth(payMonth);
		limit.delMonthAmount(obj.getPayAmt());
		limit.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysPayLimitMapper.updateByPrimaryKeySelective(limit);
	}

	@Override
	public void del(TpayCsysTxnLog obj) {
		tpayCsysTxnLogMapper.deleteByPrimaryKey(obj.getSeqId());
	}

	@Override
	public TpayCsysTxnLog findObjByKey(Long seq) {
		return tpayCsysTxnLogMapper.selectByPrimaryKey(seq);
	}

	@Override
	public TpayCsysTxnLog findObj(Map<String, Object> params) {
		return tpayCsysTxnLogMapper.selectByParams(params);
	}

	@Override
	public List<TpayCsysTxnLog> findList(Map<String, Object> params) {
		return tpayCsysTxnLogMapper.selectListOrderByparams(params);
	}

	@Override
	public void modifyLog(TpayCsysTxnLog txnLog, TpayCsysTxnLog upayLog) {
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
		tpayCsysTxnLogMapper.updateByPrimaryKeySelective(upayLog);
	}
}
