package com.huateng.cmupay.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.MessageListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.controller.cache.OrgCodeCache;
import com.huateng.cmupay.controller.cache.OrgMapTransCache;
import com.huateng.cmupay.controller.cache.RouteCache;
import com.huateng.cmupay.controller.cache.TransCodeCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysBatCutCtlService;
import com.huateng.cmupay.controller.service.system.IUpayCsysOrgCodeService;
import com.huateng.cmupay.controller.service.system.IUpayCsysOrgTransCodeService;
import com.huateng.cmupay.controller.service.system.IUpayCsysRouteInfoService;
import com.huateng.cmupay.controller.service.system.IUpayCsysSeqMapInfoService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTransCodeService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.logFormat.MessageLogger;
import com.huateng.cmupay.models.UpayCsysOrgCode;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.tools.ValidVo;
import com.huateng.toolbox.utils.StringUtil;

/**
 * @author cmt
 * @version 创建时间：2013-3-5 下午2:26:26 类说明
 */
public abstract class AbsMessageListener implements MessageListener {

	protected MessageLogger log = MessageLogger.getLogger(this.getClass());

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected JmsTemplate template;
	protected String md5key;

//	protected @Value("${testFlag}") String testFlag;
	protected @Value("${checkflag}") String checkflag;
	
	@Autowired
	protected IUpayCsysTransCodeService upayCsysTransCodeService;
	@Autowired
	protected IUpayCsysOrgTransCodeService upayCsysOrgTransCodeService;
	@Autowired
	protected IUpayCsysRouteInfoService upayCsysRouteInfoService;
	@Autowired
	protected IUpayCsysTxnLogService upayCsysTxnLogService;
	@Autowired
	protected IUpayCsysOrgCodeService upayCsysOrgCodeService;
	@Autowired
	protected IUpayCsysBatCutCtlService upayCsysBatCutCtlService;
	@Autowired
	protected IUpayCsysSeqMapInfoService upayCsysSeqMapInfoService;

	protected String validateModel(Object obj) {
	/*	logger.debug("validateModel(Object) - start");

		// 验证某一个对象
		StringBuffer buffer = new StringBuffer(64);

		Validator validator = Validation.buildDefaultValidatorFactory()
				.getValidator();

		Set<ConstraintViolation<Object>> constraintViolations = validator
				.validate(obj);

		Iterator<ConstraintViolation<Object>> iter = constraintViolations
				.iterator();
		while (iter.hasNext()) {
			String message = iter.next().getMessage();
			buffer.append(message);
		}
		String returnString = buffer.toString();
		logger.debug("validateModel(Object) - end");
		return returnString;*/
		
		String validRes = "";
		if(!CommonConstant.YesOrNo.No.toString().equals(checkflag)){
			validRes = ValidVo.getInstance().validateModel(obj);
			
		}
		return validRes;

		
		
	}

	/**
	 * 查询路由信息
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	protected UpayCsysRouteInfo findRouteInfo(Map<String, String> param){
		logger.debug("findRouteInfo(Map<String,String>) - start");
		String orgId = param.get("orgId");
		// String outerBipCode = StringUtil.toTrim(param.get("outerBipCode"));
		// String outerActivityCode = param.get("outerActivityCode");

		if ("".equals(StringUtil.toTrim(orgId))) {
			logger.debug("findRouteInfo(Map<String,String>) - end");
			return null;
		}
		UpayCsysRouteInfo routeInfo = RouteCache.getRouteInfo(orgId);
		if (routeInfo == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orgId", orgId);
			// params.put("outerBipCode", outerBipCode);
			// params.put("outerActivityCode", outerActivityCode);
			params.put("isHistory", CommonConstant.IsHistory.Normal.toString());
			params.put("status", CommonConstant.IsActive.True.toString());
			routeInfo = upayCsysRouteInfoService.findObj(params);
		}
		logger.debug("findRouteInfo(Map<String,String>) - end");
		return routeInfo;

	}

	/**
	 * 
	 * 根据外部交易码获取内部交易信息
	 * 
	 * @param reqBipCode
	 * @param reqActivityCode
	 * @return
	 * @throws Exception
	 */
	protected UpayCsysTransCode transCodeConvert(Map<String, String> param){
		logger.debug("transCodeConvert(Map<String,String>) - start");
		String reqBipCode = param.get("reqBipCode");
		String reqActivityCode = param.get("reqActivityCode");
		if ("".equals(StringUtil.toTrim(reqActivityCode)))
			return null;
		UpayCsysTransCode transCode = null;
//		if (null == reqBipCode || "".equals(reqBipCode)) {
//			transCode = TransCodeCache.getTransCode(reqActivityCode);
//		} else {
//			transCode = TransCodeCache.getTransCode(reqBipCode
//					+ reqActivityCode);
//		}
		
		transCode = TransCodeCache.getTransCode(reqBipCode,reqActivityCode);
		if (transCode == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			if (null != reqBipCode && !"".equals(reqBipCode)) {
				params.put("reqBipCode", reqBipCode);
			}
			params.put("reqActivityCode", reqActivityCode);
			transCode = upayCsysTransCodeService.findObj(params);
		}
		logger.debug("transCodeConvert(Map<String,String>) - end");
		return transCode;
	}

	/**
	 * 校验机构状态
	 * 
	 * @param param
	 * @return
	 */
	protected boolean orgStatusCheck(Map<String, String> param) {
		logger.debug("orgStatusCheck(Map<String,String>) - start");
		String orgId = param.get("orgId");
		if (null == param || null == orgId || "".equals(orgId)) {
			logger.debug("orgStatusCheck(Map<String,String>) - end");
			return false;
		}
		UpayCsysOrgCode orgCode = OrgCodeCache.getOrgCode(orgId);
		if (null == orgCode) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.putAll(param);
			orgCode = upayCsysOrgCodeService.findObj(params);
		}
		boolean returnboolean = null != orgCode;
		logger.debug("orgStatusCheck(Map<String,String>) - end");
		return returnboolean;
	}


	/**
	 * 
	 * @param reqOrg
	 *            发起方机构
	 * @param rcvOrg
	 *            接受方机构
	 * @return
	 */
	protected List<UpayCsysOrgCode> orgsOn(String reqOrg, String rcvOrg) {
		List<String> orgs = new ArrayList<String>();
		orgs.add(reqOrg);
		orgs.add(rcvOrg);
		return OrgCodeCache.getOrgCodeIn(orgs);
	}

	/**
	 * 
	 * @param orgs
	 *            机构编码集合
	 * @return true if 机构编码集合中的所有机构状态都正常 else false
	 */
	protected boolean orgsOn(List<String> orgs) {
		List<UpayCsysOrgCode> orgList = OrgCodeCache.getOrgCodeIn(orgs);
		return orgList != null && orgList.size() > 0
				&& orgList.size() == orgs.size();
	}

	/**
	 * 
	 * @param reqOrg
	 *            发起方机构
	 * @param rcvOrg
	 *            接受方机构
	 * @return true if 发起方机构and接受方机构 状态正常 else false
	 */
	protected boolean o2oOrgsOn(String reqOrg, String rcvOrg) {
		if (StringUtils.isBlank(reqOrg) || StringUtils.isBlank(rcvOrg)) {
			return false;
		}
		List<UpayCsysOrgCode> orgList = orgsOn(reqOrg, rcvOrg);
		return orgList != null && orgList.size() > 0 && orgList.size() == 2;
	}

	/**
	 * @param orgl
	 * @return
	 */
	protected String offOrg(List<String> orgl) {
		List<UpayCsysOrgCode> orgList = OrgCodeCache.getOrgCodeIn(orgl);

		for (UpayCsysOrgCode org : orgList) {
			if (org.getStatus()
					.equals(CommonConstant.IsActive.False.getValue())
					|| org.getIsHistory().equals(
							CommonConstant.IsHistory.History.getValue())) {
				return org.getOrgId();
			}
		}

		return null;
	}
	
	protected abstract BaseMsgVo convertRtnMsgVo(BaseMsgVo vo);

	public void setMd5key(String md5key) {
		this.md5key = md5key;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

}
