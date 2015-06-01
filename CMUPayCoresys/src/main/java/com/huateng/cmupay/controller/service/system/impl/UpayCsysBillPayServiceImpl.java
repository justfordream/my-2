package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.DataSourceInstances;
import com.huateng.cmupay.controller.mapper.UpayCsysBillPayHisMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysBillPayMapper;
import com.huateng.cmupay.controller.his.mapper.UpayCsysTxnLogHisMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysTxnLogMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysBillPayService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.models.UpayCsysBillPay;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
import com.huateng.cmupay.models.his.UpayCsysBillPayHis;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;

/**
 * @author cmt
 *
 */
@Service("upayCsysBillPayService")
public class UpayCsysBillPayServiceImpl implements IUpayCsysBillPayService  {
	@Autowired
	private UpayCsysBillPayMapper upayCsysBillPayMapper;
	@Autowired
	private UpayCsysBillPayHisMapper upayCsysBillPayHisMapper;
	@Autowired
	private UpayCsysTxnLogMapper upayCsysTxnLogMapper;
	@Autowired
	private UpayCsysTxnLogHisMapper upayCsysTxnLogHisMapper;
	@Override
	public void add(UpayCsysBillPay obj)   {
		upayCsysBillPayMapper.insertSelective(obj);
	}
	@Override
	public void modify(UpayCsysBillPay obj)   {
	}
	@Override
	public void del(UpayCsysBillPay obj)   {
	}
	@Override
	public UpayCsysBillPay findObjByKey(Long seq)   {
		return null;
	}
	/**
	 * fan_kui  根据给定条件查找发票打印状态，如需做其他查找，请重写该方法
	 */
	@Override
	public UpayCsysBillPay findObj(Map<String, Object> params)   {		
		return upayCsysBillPayMapper.selectByParams(params);
	}
	@Override
	public List<UpayCsysBillPay> findList(Map<String, Object> params, Order order)
			  {
		return null;
	}
   
	//返回值问题
	/**
	 * txnLog   新纪录流水
	 * upayLog  原流水记录
	 * bill	            新成功明细
	 * upay	            原成功明细
	 */
	@Override
	public void modifyNote(UpayCsysTxnLog txnLog,UpayCsysTxnLog upayLog,UpayCsysBillPay bill,UpayCsysBillPay upay)throws AppBizException {
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(upayLog);
		upayCsysBillPayMapper.insertSelective(bill);//insert insertSelective有啥区别
		upayCsysBillPayMapper.updateByPrimaryKeySelective(upay);
	}
	@Override
	public UpayCsysBillPay find(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return upayCsysBillPayMapper.selectByParamsOld(params);
	}
	@Override
	public void modifyNotes(UpayCsysTxnLog txnLog, UpayCsysTxnLogHis logHis,
			UpayCsysBillPay bill, UpayCsysBillPayHis billHis)
			throws AppBizException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void modifyArg(UpayCsysTxnLog txnLog, UpayCsysTxnLogHis logHis,UpayCsysBillPay bill, UpayCsysBillPayHis billHis) {
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
		upayCsysBillPayMapper.insertSelective(bill);
		
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_HIS);
		upayCsysBillPayHisMapper.updateByPrimaryKeySelective(billHis);
		DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_HIS);
		upayCsysTxnLogHisMapper.updateByPrimaryKeySelective(logHis);
		
	}

}