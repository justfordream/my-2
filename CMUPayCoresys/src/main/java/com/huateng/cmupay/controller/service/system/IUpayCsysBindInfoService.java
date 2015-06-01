/**
 * 
 */
package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.models.common.Order;

/**
 * 
 * @author cmt
 * 
 */
public interface IUpayCsysBindInfoService {

	public void add(UpayCsysBindInfo obj) throws AppBizException;

	public void modify(UpayCsysBindInfo obj) throws AppBizException;
	public void modifyClean(UpayCsysBindInfo obj) throws AppBizException;

	public void modifySelective(UpayCsysBindInfo obj) throws AppBizException;

	public void del(UpayCsysBindInfo obj) throws AppBizException;

	public int findSubCountByParams(Map<String, Object> params);

	/**
	 * 查找主号码的副号码个数
	 * 
	 * @param params
	 * @return
	 * @throws AppBizException
	 */
	public int selectSubByParams(Map<String, Object> params)
			throws AppBizException;

	public UpayCsysBindInfo findPhoneByKey(Long seq) throws AppBizException;

	public String findStatus(Map<String, Object> params) throws AppBizException;

	public UpayCsysBindInfo findObj(Map<String, Object> params)
			throws AppBizException;

	public List<UpayCsysBindInfo> findList(Map<String, Object> params,
			Order order);

	/**
	 * 更新签约关系并更新交易流水
	 * 
	 * @param obj
	 * @param txnLog
	 * @throws AppBizException
	 */
	public void modifyLogAndBinfInfo(UpayCsysBindInfo obj,
			UpayCsysBindInfo odj, UpayCsysTxnLog txnLog) throws AppBizException;

	public void modifyTxnAndBindChange(UpayCsysBindInfo objMaster,
			UpayCsysTxnLog txnLog) throws AppBizException;

	public void modifySelectiveBindInfoAndLog(UpayCsysBindInfo obj,
			UpayCsysTxnLog txnLog);

	/**
	 * 更新签约关系并更新临时交易流水（网关）
	 * 
	 * @param obj
	 * @param txnLog
	 * @throws AppBizException
	 */
	public void modifyLogTmpAndBinfInfo(UpayCsysBindInfo obj,
			UpayCsysTxnLogTmp txnLog) throws AppBizException;

	/**
	 * 添加签约关系并更新交易流水（网关）
	 * 
	 * @param obj
	 * @param txnLog
	 * @throws AppBizException
	 */
	public void modifyLogTmpAndAddBindInfo(UpayCsysBindInfo obj,
			UpayCsysTxnLogTmp txnLog) throws AppBizException;

	public void modifyTxnLogAndDelBindInfo(UpayCsysBindInfo info,
			UpayCsysTxnLog txnLog) throws AppBizException;

	public boolean isSignUser(Map<String, Object> isSignP);
	/**
	 * 修改流水，添加签约数据
	 * @param obj
	 * @param info
	 * @throws AppBizException
	 */
	public void modifyLogAndAddBindInfo(UpayCsysBindInfo obj,UpayCsysTxnLog txnLog)
	throws AppBizException;
	/**
	 * 修改流水，添加签约数据
	 * @param obj
	 * @param info
	 * @throws AppBizException
	 */
	public void modifyLogAndCleanBindInfo(UpayCsysBindInfo obj,UpayCsysTxnLog txnLog)
			throws AppBizException;

}
