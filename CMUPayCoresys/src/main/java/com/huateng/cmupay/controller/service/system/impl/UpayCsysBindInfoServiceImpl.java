/**
 * 
 */
package com.huateng.cmupay.controller.service.system.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.controller.mapper.UpayCsysBindInfoMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysTxnLogMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysTxnLogTmpMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysBindInfoService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.models.common.Order;
import com.huateng.toolbox.utils.DateUtil;

/**
 * @author cmt
 * 
 */

@Service("upayCsysBindInfoService")
public class UpayCsysBindInfoServiceImpl implements IUpayCsysBindInfoService {

	@Autowired
	private UpayCsysBindInfoMapper upayCsysBindInfoMapper;
	@Autowired
	private UpayCsysTxnLogMapper upayCsysTxnLogMapper;
	@Autowired
	private UpayCsysTxnLogTmpMapper upayCsysTxnLogTmpMapper;

	@Override
	public void add(UpayCsysBindInfo obj) throws AppBizException {
		upayCsysBindInfoMapper.insertSelective(obj);

	}

	@Override
	public void modifySelective(UpayCsysBindInfo obj) throws AppBizException {
		obj.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
		upayCsysBindInfoMapper.updateByPrimaryKeySelective(obj);
	}

	@Override
	public void modify(UpayCsysBindInfo obj) throws AppBizException {
		obj.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
		upayCsysBindInfoMapper.updateByPrimaryKey(obj);
	}
	
	

	@Override
	public void del(UpayCsysBindInfo obj) throws AppBizException {
		upayCsysBindInfoMapper.deleteByPrimaryKey(obj.getSeqId());
	}

	@Override
	public UpayCsysBindInfo findPhoneByKey(Long seq) throws AppBizException {
		return null;
	}
	
	@Override
	public UpayCsysBindInfo findObj(Map<String, Object> params)
			throws AppBizException {

		UpayCsysBindInfo upayCsysBindInfo = upayCsysBindInfoMapper
				.selectByParams(params);

		return upayCsysBindInfo;
	}

	@Override
	public String findStatus(Map<String, Object> params) throws AppBizException {
		return null;
	}

	@Override
	public List<UpayCsysBindInfo> findList(Map<String, Object> params,
			Order order) {
		String orderParam = (order == null) ? null : order.toString();
		return upayCsysBindInfoMapper.selectListByParams(params, null, null,
				orderParam);
	}

	@Override
	public void modifyTxnAndBindChange(UpayCsysBindInfo objMaster,
			UpayCsysTxnLog txnLog) throws AppBizException {
		objMaster.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysBindInfoMapper.updateByPrimaryKeySelective(objMaster);
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
	}

	@Override
	public void modifyLogAndBinfInfo(UpayCsysBindInfo objSlaver,
			UpayCsysBindInfo objMaster, UpayCsysTxnLog txnLog)
			throws AppBizException {

		if (null != objSlaver) {
			objSlaver.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
			upayCsysBindInfoMapper.updateByPrimaryKey(objSlaver);
		}
		if (null != objMaster) {
			objMaster.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
			upayCsysBindInfoMapper.updateByPrimaryKeySelective(objMaster);
		}
		if (null != txnLog) {
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
		}

	}

	@Override
	public boolean isSignUser(Map<String, Object> isSignP) {
		UpayCsysBindInfo info = upayCsysBindInfoMapper.selectByParams(isSignP);
		return null != info
				&& CommonConstant.BindStatus.Bind.getValue().equals(
						info.getStatus());
	}

	@Override
	public void modifyLogTmpAndBinfInfo(UpayCsysBindInfo obj,
			UpayCsysTxnLogTmp txnLog) throws AppBizException {

		obj.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		if (null != obj) {
			upayCsysBindInfoMapper.updateByPrimaryKey(obj);
		}
		if (null != txnLog) {
			upayCsysTxnLogTmpMapper.updateByPrimaryKeySelective(txnLog);
		}
	}

	@Override
	public void modifyLogTmpAndAddBindInfo(UpayCsysBindInfo obj,
			UpayCsysTxnLogTmp txnLog) throws AppBizException {
		obj.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		if (null != txnLog) {
			upayCsysTxnLogTmpMapper.updateByPrimaryKeySelective(txnLog);
		}
		if (null != obj) {
			upayCsysBindInfoMapper.insertSelective(obj);
		}
	}

	// 只有解约的时候才可以使用
	@Override
	public void modifySelectiveBindInfoAndLog(UpayCsysBindInfo obj,
			UpayCsysTxnLog txnLog) {
		obj.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmss());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		if (null != obj) {
			upayCsysBindInfoMapper.updateByPrimaryKeySelective(obj);
		}
		if (null != txnLog) {
			upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
		}
	}

	@Override
	public int selectSubByParams(Map<String, Object> params)
			throws AppBizException {
		return upayCsysBindInfoMapper.selectCountByParams(params);
	}

	@Override
	public int findSubCountByParams(Map<String, Object> params) {
		return upayCsysBindInfoMapper.selectCountByParams(params);
	}

	@Override
	public void modifyTxnLogAndDelBindInfo(UpayCsysBindInfo info,
			UpayCsysTxnLog txnLog) throws AppBizException {
		if (null != info) {
			upayCsysBindInfoMapper.deleteByPrimaryKey(info.getSeqId());
		}
		if (null != txnLog) {
			upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
		}
	}

	@Override
	public void modifyLogAndAddBindInfo(UpayCsysBindInfo obj,
			UpayCsysTxnLog txnLog) throws AppBizException {
		if (null != obj) {
			upayCsysBindInfoMapper.insertSelective(obj);
		}
		if (null != txnLog) {
			upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
		}
	}

	@Override
	public void modifyLogAndCleanBindInfo(UpayCsysBindInfo obj,
			UpayCsysTxnLog txnLog) throws AppBizException {
		upayCsysBindInfoMapper.updateByPrimaryKey(obj);
		upayCsysTxnLogMapper.updateByPrimaryKeySelective(txnLog);
	}

	@Override
	public void modifyClean(UpayCsysBindInfo obj) throws AppBizException {
		upayCsysBindInfoMapper.updateByPrimaryKey(obj);
		
	}

}
