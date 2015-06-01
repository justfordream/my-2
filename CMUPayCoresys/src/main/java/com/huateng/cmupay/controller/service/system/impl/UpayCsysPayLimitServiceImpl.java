package com.huateng.cmupay.controller.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.mapper.UpayCsysPayLimitMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysTxnLogMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysPayLimitService;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysPayLimit;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;
import com.huateng.toolbox.utils.DateUtil;

/**
 * @author cmt
 * 
 */
@Service("upayCsysPayLimitService")
public class UpayCsysPayLimitServiceImpl implements IUpayCsysPayLimitService {

//	private @Value("${mouthAmt}") String mouthAmt;
	
	@Autowired
	private UpayCsysTxnLogMapper upayCsysTxnLogMapper;
	
	@Autowired
	private UpayCsysPayLimitMapper upayCsysPayLimitMapper;

	@Override
	public void add(UpayCsysPayLimit obj) {

	}

	@Override
	public boolean modifyLimitAdd(UpayCsysPayLimit obj,Long payAmt) {
		Long mouthLimit = Long.parseLong(
				DictCodeCache.getDictCode(DictConst.DictId.ChargeLimit.getValue(), DictConst.CodeId.ChargeLimit.getValue())
				.getCodeValue2());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idType", obj.getIdType());
		params.put("idValue", obj.getIdValue());
		params.put("payMonth", obj.getPayMonth());
		UpayCsysPayLimit payLimit = upayCsysPayLimitMapper
				.selectByParamsLock(params);
		if (payLimit == null) {
			payLimit = new UpayCsysPayLimit();
			payLimit.setMonthAmount(0L);
			payLimit.setPayMonth(obj.getPayMonth());
			payLimit.setIdProvince(obj.getIdProvince());
			payLimit.setIdType(obj.getIdType());
			payLimit.setIdValue(obj.getIdValue());
			payLimit.setMonthMaxAmount(obj.getMonthMaxAmount());
			payLimit.setDayMaxAmount(obj.getDayMaxAmount());
			payLimit.setDayAmount(0L);
			payLimit.setMonthAmount(payAmt);
			payLimit.setAmountCat(ExcConstant.AMOUNT_CAT);
			payLimit.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
			if(payAmt.longValue()>mouthLimit.longValue()){
				return false;
			}
			upayCsysPayLimitMapper.insertSelective(payLimit);
			return true;
		} else {

			long monthAmount = payLimit.getMonthAmount().longValue();
			long addMonthAmount = payAmt.longValue();
			if (monthAmount + addMonthAmount > mouthLimit.longValue()) {
				//upayCsysPayLimitMapper.updateByPrimaryKeySelective(payLimit);
				return false;
			} else {
				payLimit.addMonthAmount(payAmt.longValue());
				payLimit.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
				upayCsysPayLimitMapper.updateByPrimaryKeySelective(payLimit);
				return true;
			}

		}

	}

	@Override
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
		UpayCsysPayLimit limit =upayCsysPayLimitMapper.selectByParamsLock(params);
		limit.setIdType(idType);
		limit.setIdValue(idValue);
		limit.setPayMonth(payMonth);
		limit.delMonthAmount(obj.getPayAmt());
		limit.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysPayLimitMapper.updateByPrimaryKeySelective(limit);
	}
	
	
	@Override
	public void modifyLimitDel(UpayCsysPayLimit obj,Long payAmt) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idType", obj.getIdType());
		params.put("idValue", obj.getIdValue());
		params.put("payMonth", obj.getPayMonth());
	    UpayCsysPayLimit payLimit = upayCsysPayLimitMapper
				.selectByParamsLock(params);
	    payLimit.setIdType(obj.getIdType());
	    payLimit.setIdValue(obj.getIdValue());
	    payLimit.setPayMonth(obj.getPayMonth());
		payLimit.delMonthAmount(payAmt.longValue());
		payLimit.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
		upayCsysPayLimitMapper.updateByPrimaryKeySelective(payLimit);
		
	}

	/**
	 * Crm冲正,减去限额表中冲正的金额 
	 * 2013-06-18 hdm
	 */
	@Override
	public void modifyLimtsDells(UpayCsysTxnLog upayLog) {
		String idValue = null;
		String idType = null;
		if (CommonConstant.Mainflag.Slave.toString().equals(upayLog.getMainFlag())) {//副号码时,主号码必须填写
			idValue = upayLog.getMainIdValue();
			idType = upayLog.getMainIdType();
		}else{
			idValue = upayLog.getIdValue();
			idType = upayLog.getIdType();
		}
		String payMonth = upayLog.getReqTransDt().substring(0,6);//应该是此缴费的月份,不一定是退费的日期
		Long payAmt  = upayLog.getPayAmt();//减去的金额
		
		Map<String, Object> pars = new HashMap<String, Object>();
		pars.put("idType", idType);
		pars.put("idValue", idValue);
		pars.put("payMonth", payMonth);
		UpayCsysPayLimit lims = upayCsysPayLimitMapper.selectByParamsLock(pars);//锁表
		lims.setIdType(idType);
		lims.setIdValue(idValue);
		lims.setPayMonth(payMonth);
		lims.delMonthAmount(payAmt);
		lims.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysPayLimitMapper.updateByPrimaryKeySelective(lims);
	}

	/**
	 * Crm向Bank冲正失败,将先减去得限额表中的金额回退 
	 * 2013-06-18 hdm
	 */
	@Override
	public void modifyLimtsAdds(UpayCsysTxnLog upayLog) {
		String idValue = null;
		String idType = null;
		if (CommonConstant.Mainflag.Slave.toString().equals(upayLog.getMainFlag())) {//副号码时,主号码必须填写
			idValue = upayLog.getMainIdValue();
			idType = upayLog.getMainIdType();
		}else{
			idValue = upayLog.getIdValue();
			idType = upayLog.getIdType();
		}
		String payMonth = upayLog.getReqTransDt().substring(0,6);//
		Long payAmt  = upayLog.getPayAmt();//回退的金额
		
		Map<String, Object> pars = new HashMap<String, Object>();
		pars.put("idType", idType);
		pars.put("idValue", idValue);
		pars.put("payMonth", payMonth);
		UpayCsysPayLimit lim = upayCsysPayLimitMapper.selectByParamsLock(pars);
		lim.setIdType(idType);
		lim.setIdValue(idValue);
		lim.setPayMonth(payMonth);
		lim.addMonthAmount(payAmt);
		lim.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysPayLimitMapper.updateByPrimaryKeySelective(lim);
	}

	/**
	 * Crm退费,减去限额表中冲正的金额 
	 * 2013-06-25 hdm
	 */
	@Override
	public void modifyLimtsDs(UpayCsysTxnLogHis logHis) {
		
		String idValue = null;
		String idType = null;
		if (CommonConstant.Mainflag.Slave.toString().equals(logHis.getMainFlag())) {//副号码时,主号码必须填写
			idValue = logHis.getMainIdValue();
			idType = logHis.getMainIdType();
		}else{
			idValue = logHis.getIdValue();
			idType = logHis.getIdType();
		}
		String payMonth = logHis.getReqTransDt().substring(0,6);//缴费的月份,不该是退费的月份
//		String payMonth = logHis.getIntTxnDate().substring(0,6);//日切时间,取前六位,表示那个月
		Long payAmt  = logHis.getPayAmt();//减去的金额
		
		Map<String, Object> pars = new HashMap<String, Object>();
		pars.put("idType", idType);
		pars.put("idValue", idValue);
		pars.put("payMonth", payMonth);
		UpayCsysPayLimit lims = upayCsysPayLimitMapper.selectByParamsLock(pars);//锁表
		lims.setIdType(idType);
		lims.setIdValue(idValue);
		lims.setPayMonth(payMonth);
		lims.delMonthAmount(payAmt);
		lims.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysPayLimitMapper.updateByPrimaryKeySelective(lims);
		
	}

	/**
	 * Crm向Bank冲正失败,将先减去得限额表中的金额回退 
	 * 2013-06-25 hdm
	 */
	@Override
	public void modifyLimtsAs(UpayCsysTxnLogHis logHis) {
		
		String idValue = null;
		String idType = null;
		if (CommonConstant.Mainflag.Slave.toString().equals(logHis.getMainFlag())) {//副号码时,主号码必须填写
			idValue = logHis.getMainIdValue();
			idType = logHis.getMainIdType();
		}else{
			idValue = logHis.getIdValue();
			idType = logHis.getIdType();
		}
		String payMonth = logHis.getReqTransDt().substring(0,6);//日切时间,取前六位,表示那个月
//		String payMonth = logHis.getIntTxnDate().substring(0,6);//日切时间,取前六位,表示那个月
		Long payAmt  = logHis.getPayAmt();//回退的金额
		
		Map<String, Object> pars = new HashMap<String, Object>();
		pars.put("idType", idType);
		pars.put("idValue", idValue);
		pars.put("payMonth", payMonth);
		UpayCsysPayLimit lim = upayCsysPayLimitMapper.selectByParamsLock(pars);
		lim.setIdType(idType);
		lim.setIdValue(idValue);
		lim.setPayMonth(payMonth);
		lim.addMonthAmount(payAmt);
		lim.setLastUpdTime( DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysPayLimitMapper.updateByPrimaryKeySelective(lim);
		
	}
	
	
	@Override
	public void modify(UpayCsysPayLimit obj) {

	}

	@Override
	public void del(UpayCsysPayLimit obj) {
	}

	@Override
	public UpayCsysPayLimit findObjByKey(Long seq) {
		return null;
	}

	@Override
	public UpayCsysPayLimit findObj(Map<String, Object> params) {

		UpayCsysPayLimit upayCsysPayLimit = upayCsysPayLimitMapper
				.selectByParams(params);
		return upayCsysPayLimit;
	}

	@Override
	public List<UpayCsysPayLimit> findList(Map<String, Object> params,
			Order order) {
		return null;
	}

	
}