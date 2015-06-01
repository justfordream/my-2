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
import com.huateng.cmupay.controller.mapper.UpayCsysPayLimitMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysTxnLogMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.models.UpayCsysBillPay;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysPayLimit;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
import com.huateng.toolbox.utils.DateUtil;

/**
 * @author cmt
 * 
 */

@Service("upayCsysTxnLogService")
public class UpayCsysTxnLogServiceImpl implements IUpayCsysTxnLogService {
	
	@Autowired
	private UpayCsysTxnLogMapper upayCsysTxnLogMapper;
//	@Autowired
//	private UpayCsysTxnLogTmpMapper upayCsysTxnLogTmpMapper;
	//@Autowired
	//private UpayCsysBillPayMapper upayCsysBillPayMapper;
	@Autowired
	private UpayCsysPayLimitMapper upayCsysPayLimitMapper;

	@Override
	public void add(UpayCsysTxnLog obj) {
		upayCsysTxnLogMapper.insertSelective(obj);
	}

	@Override
	public void modify(UpayCsysTxnLog obj) {
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		obj.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(obj);
	}

	/**
	 * 范魁 同时更新成功表和流水表
	 */
	public void modifyTxnLogAndBillPay(UpayCsysTxnLog log,
			UpayCsysBillPay billPay) {
		log.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(log);
		//upayCsysBillPayMapper.updateByPrimaryKeySelective(billPay);
	}
	
//	@Override
//	public void modifyTxnLogTmpAndBillPay(UpayCsysTxnLogTmp log,
//			UpayCsysBillPay billPay) {
//		log.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
//		upayCsysTxnLogTmpMapper.updateByPrimaryKeySelective(log);
//		//upayCsysBillPayMapper.updateByPrimaryKeySelective(billPay);		
//	}
	public void modifyTxnLogAndAddBillPay(UpayCsysTxnLog log,UpayCsysBillPay billPay){
		log.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(log);
		//upayCsysBillPayMapper.insertSelective(billPay);
	}

	public void modifyLimit(UpayCsysTxnLog obj, UpayCsysBindInfo bindInfo) {
		String idValue = null;
		String idType = null;
		
			if (CommonConstant.Mainflag.Slave.toString().equals(
					bindInfo.getMainFlag())) {
				idValue = bindInfo.getMainIdValue();
				idType=bindInfo.getMainIdType();
			}else{
				idValue = bindInfo.getIdValue();
				idType=bindInfo.getIdType();
			}
		
		String payMonth = obj.getIntTxnDate().substring(0, 6);
		obj.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(obj);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idType", idType);
		params.put("idValue", idValue);
		params.put("payMonth", payMonth);
		UpayCsysPayLimit limit =  upayCsysPayLimitMapper.selectByParamsLock(params);
		limit.setIdType(idType);
		limit.setIdValue(idValue);
		limit.setPayMonth(payMonth);
		limit.delMonthAmount(obj.getPayAmt());
		limit.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysPayLimitMapper.updateByPrimaryKeySelective(limit);
	}

	@Override
	public void del(UpayCsysTxnLog obj) {
		upayCsysTxnLogMapper.deleteByPrimaryKey(obj.getSeqId());
	}

	@Override
	public UpayCsysTxnLog findObjByKey(Long seq) {
		return upayCsysTxnLogMapper.selectByPrimaryKey(seq);
	}

	@Override
	public UpayCsysTxnLog findObj(Map<String, Object> params) {
		return upayCsysTxnLogMapper.selectByParams(params);
	}
	@Override
	public UpayCsysTxnLog findIsResend(Map<String, Object> params) {
		return upayCsysTxnLogMapper.selectForIsResend(params);
	}
	@Override
	public List<UpayCsysTxnLog> findList(Map<String, Object> params) {
		return upayCsysTxnLogMapper.selectListOrderByparams(params);
	}

	

	@Override
	public void modifyLog(UpayCsysTxnLog txnLog, UpayCsysTxnLog upayLog) {
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayLog.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(upayLog);
	}

	/**
	 * 范魁 查找流水表和成功明细表
	 */
	//TODO 值得商榷
//	@Override
//	public Map<String, Object> findTxnLogAndBillPay(String billPay, String log,
//			Map<String, Object> params) {
//		UpayCsysTxnLog txnLog = upayCsysTxnLogMapper.selectByParams(params);
//		if (txnLog == null) {
//			logger.info("txnLog查询信息为null");
//			return null;
//		}
//		params.put("seqId", txnLog.getSeqId());
//
//		UpayCsysBillPay billPayLog = upayCsysBillPayMapper
//				.selectByParamsOld(params);
//		logger.info("BillPay查询信息："+billPayLog==null?"为null":"存在");
//		params.put(log, txnLog);
//		params.put(billPay, billPayLog);
//		return params;
//	}
	
}
