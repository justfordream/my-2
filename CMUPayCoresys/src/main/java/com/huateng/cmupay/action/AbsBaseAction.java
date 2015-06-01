package com.huateng.cmupay.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.cache.OrgCodeCache;
import com.huateng.cmupay.controller.cache.OrgMapTransCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.cache.RouteCache;
import com.huateng.cmupay.controller.cache.TransCodeCache;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.controller.service.system.IUpayCsysBatCutCtlService;
import com.huateng.cmupay.controller.service.system.IUpayCsysBindInfoService;
import com.huateng.cmupay.controller.service.system.IUpayCsysDictCodeService;
import com.huateng.cmupay.controller.service.system.IUpayCsysImsiLdCdService;
import com.huateng.cmupay.controller.service.system.IUpayCsysOrgCodeService;
import com.huateng.cmupay.controller.service.system.IUpayCsysOrgTransCodeService;
import com.huateng.cmupay.controller.service.system.IUpayCsysRouteInfoService;
import com.huateng.cmupay.controller.service.system.IUpayCsysSeqMapInfoService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTransCodeService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.logFormat.MessageLogger;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysOrgCode;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.tools.ValidVo;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 
 * @author cmt
 * 
 */

public abstract class AbsBaseAction<T, R> implements IBaseAction<T, R> {
	public AbsBaseAction() {
		this.testFlag = DictCodeCache.getDictCode(
				DictConst.DictId.TestFlag.getValue(),
				DictConst.CodeId.TestFlag.getValue()).getCodeValue2();
	}

	protected MessageLogger log = MessageLogger.getLogger(this.getClass());

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected String testFlag;
	protected @Value("${checkflag}")
	String checkflag;
	@Autowired
	protected IUpayCsysTxnLogService upayCsysTxnLogService;
	@Autowired
	protected IUpayCsysSeqMapInfoService upayCsysSeqMapInfoService;
	@Autowired
	protected IUpayCsysBatCutCtlService upayCsysBatCutCtlService;
	@Autowired
	protected IUpayCsysTransCodeService upayCsysTransCodeService;
	@Autowired
	protected IUpayCsysRouteInfoService upayCsysRouteInfoService;
	@Autowired
	protected IUpayCsysOrgTransCodeService upayCsysOrgTransCodeService;
	@Autowired
	protected IUpayCsysOrgCodeService upayCsysOrgCodeService;
	@Autowired
	protected IUpayCsysBindInfoService upayCsysBindInfoService;
	@Autowired
	protected IUpayCsysImsiLdCdService upayCsysImsiLdCdService;
	@Autowired
	protected IUpayCsysDictCodeService upayCsysDictCodeService;
	//新流水对象
	@Autowired 
	protected ITpayCsysTxnLogService tpayCsysTxnLogService;
	// @Autowired
	// protected IUpayCsysOrgMapTransCodeService
	// upayCsysUpayMapTransCodeService;

	/**
	 * @param obj
	 * @return
	 */
	// protected String validateModel(Object obj) {
	// if (logger.isDebugEnabled()) {
	// logger.debug("validateModel(Object) - start");
	// }
	//
	// String validRes = "";
	// if (!CommonConstant.YesOrNo.No.toString().equals(checkflag)) {
	// // StringBuilder buffer = new StringBuilder(64);
	// //
	// // Validator validator = Validation.buildDefaultValidatorFactory()
	// // .getValidator();
	// //
	// // Set<ConstraintViolation<Object>> constraintViolations = validator
	// // .validate(obj);
	// //
	// // Iterator<ConstraintViolation<Object>> iter = constraintViolations
	// // .iterator();
	// // while (iter.hasNext()) {
	// // String message = iter.next().getMessage();
	// // buffer.append(message);
	// // }
	// // return "".equals(buffer.toString())?null:buffer.toString();
	// validRes = ValidVo.getInstance().validateModel(obj);
	//
	// }
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("validateModel(Object) - end");
	// }
	// return validRes;
	// }

    @SuppressWarnings("all")
	protected String validateModel(Object obj, String... key) {
		if (logger.isDebugEnabled()) {
			logger.debug("validateModel(Object) - start");
		}
		String validRes = "";
		if (!CommonConstant.YesOrNo.No.toString().equals(checkflag)) {
			validRes = ValidVo.getInstance().validateModel(obj,key);
//			if (null == validRes || "".equals(validRes)) {
//				try {
//					Field[] childFields = obj.getClass().getDeclaredFields();
//					for (Field field : childFields) {
//						CascadeAnnotation custom = (CascadeAnnotation) field
//								.getAnnotation(CascadeAnnotation.class);
//						if (custom != null) {
//							String cascadeMethod = custom.cascadeMethod();
//							String method = custom.method();
//							String constraintClazzPath = custom
//									.constraintClazzPath();
//							Class<?> clazz = obj.getClass();
//							Object valueObj1 = clazz.getMethod(cascadeMethod,
//									null).invoke(obj, null);
//							Object valueObj2 = clazz.getMethod(method, null)
//									.invoke(obj, null);
//							Class<?> c = Class.forName(constraintClazzPath);
//							IValidator i = (IValidator) c.newInstance();
//							validRes = i.isValid(valueObj1, valueObj2);
//						}
//					}
//
//				} catch (Exception e) {
//					logger.error("", e);
//					// validRes = "级联验证失败";
//					validRes = "";
//				}
//			}

		}

		if (logger.isDebugEnabled()) {
			logger.debug("validateModel(Object) - end");
		}
		return validRes;
	}

	/**
	 * 閺屻儴顕楃捄顖滄暠娣団剝浼?
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	protected UpayCsysRouteInfo findRouteInfo(Map<String, String> param) {
		if (logger.isDebugEnabled()) {
			logger.debug("findRouteInfo(Map<String,String>) - start");
		}

		String orgId = param.get("orgId");
		// String outerBipCode = StringUtil.toTrim(param.get("outerBipCode"));
		// String outerActivityCode = param.get("outerActivityCode");

		if ("".equals(StringUtil.toTrim(orgId))) {
			if (logger.isDebugEnabled()) {
				logger.debug("findRouteInfo(Map<String,String>) - end");
			}
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

		if (logger.isDebugEnabled()) {
			logger.debug("findRouteInfo(Map<String,String>) - end");
		}
		return routeInfo;
	}

	protected UpayCsysTransCode transCodeConvert(Map<String, String> param) {
		if (logger.isDebugEnabled()) {
			logger.debug("transCodeConvert(Map<String,String>) - start");
		}

		String reqBipCode = param.get("reqBipCode");
		String reqActivityCode = param.get("reqActivityCode");
		if ("".equals(StringUtil.toTrim(reqBipCode))
				|| "".equals(StringUtil.toTrim(reqActivityCode)))
			return null;
		UpayCsysTransCode transCode = TransCodeCache.getTransCode(reqBipCode,
				reqActivityCode);
		if (transCode == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			if (null != reqBipCode && !"".equals(reqBipCode)) {
				params.put("reqBipCode", reqBipCode);
			}
			params.put("reqActivityCode", reqActivityCode);
			params.put("isHistory", CommonConstant.IsHistory.Normal.toString());
			transCode = upayCsysTransCodeService.findObj(params);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("transCodeConvert(Map<String,String>) - end");
		}
		return transCode;
	}

	// protected UpayCsysTransCode transCodeConver(Map<String, String> param){
	// if (logger.isDebugEnabled()) {
	// logger.debug("transCodeConver(Map<String,String>) - start");
	// }
	//
	// String reqActivityCode = param.get("reqActivityCode");
	// if ("".equals(StringUtil.toTrim(reqActivityCode)))
	// return null;
	// UpayCsysTransCode transCode =
	// TransCodeCache.getTransCode(reqActivityCode);
	// if (transCode == null) {
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("reqActivityCode", reqActivityCode);
	// params.put("isHistory", CommonConstant.IsHistory.Normal.toString());
	// transCode = upayCsysTransCodeService.findObj(params);
	// }
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("transCodeConver(Map<String,String>) - end");
	// }
	// return transCode;
	// }
	// /**
	// *
	// * 閺嶈宓佹禍銈嗘娴狅絿鐖滈崪灞藉弿鐏炩偓娴溿倖妲楅惍浣稿灲閺傤厽妲搁崥锔芥箒娴溿倖妲楅弶鍐
	// *
	// * @param reqBipCode
	// * @param reqActivityCode
	// * @return
	// * @throws Exception
	// */
	// protected boolean orgTransCodeCheck(Map<String, String> param){
	// if (logger.isDebugEnabled()) {
	// logger.debug("orgTransCodeCheck(Map<String,String>) - start");
	// }
	//
	// String orgId = param.get("orgId");
	// String transCode = param.get("transCode");
	// if ("".equals(StringUtil.toTrim(orgId))
	// || "".equals(StringUtil.toTrim(transCode)))
	// return false;
	// //
	// // UpayCsysOrgTransCode orgTransCode = OrgTransCodeCache
	// // .getOrgTransCode(orgId + transCode);
	// // if (orgTransCode == null) {
	// // Map<String, Object> params = new HashMap<String, Object>();
	// // params.put("orgId", orgId);
	// // params.put("transCode", transCode);
	// // params.put("status", CommonConstant.IsActive.True.toString());
	// // params.put("isHistory", CommonConstant.IsHistory.Normal.toString());
	// // orgTransCode = upayCsysOrgTransCodeService.findObj(params);
	// // }
	// // if (orgTransCode == null) {
	// // return false;
	// // }
	//
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("orgId", orgId);
	// params.put("transCode", transCode);
	// params.put("status", CommonConstant.IsActive.True.toString());
	// params.put("isHistory", CommonConstant.IsHistory.Normal.toString());
	// UpayCsysOrgTransCode orgTransCode =
	// upayCsysOrgTransCodeService.findObj(params);
	//
	// if (orgTransCode == null) {
	// logger.debug("orgTransCodeCheck(Map<String,String>) - end");
	// return false;
	// }
	//
	// return true;
	// }
	//

	/**
	 * @param phoneNum
	 * @return
	 */
	protected String formatPhoneNo(String phoneNum) {
		if (logger.isDebugEnabled()) {
			logger.debug("formatPhoneNo(String) - start");
		}

		String returnString = StrUtil.subString(phoneNum,
				phoneNum.length() - 11, phoneNum.length());
		if (logger.isDebugEnabled()) {
			logger.debug("formatPhoneNo(String) - end");
		}
		return returnString;
	}

	/**
	 * @param param
	 * @return
	 */
	protected boolean orgStatusCheck(String orgId) {
		if (logger.isDebugEnabled()) {
			logger.debug("orgStatusCheck(String) - start");
		}

		if (null == orgId || "".equals(orgId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("orgStatusCheck(String) - end");
			}
			return false;
		}
		UpayCsysOrgCode orgCode = OrgCodeCache.getOrgCode(orgId);
		if (null == orgCode) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orgId", orgId);
			params.put("status", CommonConstant.IsActive.True.getValue());
			params.put("isHistory", CommonConstant.IsHistory.Normal.getValue());
			orgCode = upayCsysOrgCodeService.findObj(params);
		}
		boolean returnboolean = null != orgCode
				&& null != orgCode.getStatus()
				&& CommonConstant.IsActive.True.getValue().equals(
						orgCode.getStatus());
		if (logger.isDebugEnabled()) {
			logger.debug("orgStatusCheck(String) - end");
		}
		return returnboolean;
	}

	/**
	 * @param reqTransId
	 * @param reqDomain
	 * @return
	 */
	protected boolean isRepeatTrans(String reqTransId, String reqDomain) {
		if (logger.isDebugEnabled()) {
			logger.debug("isRepeatTrans(String, String) - start");
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reqOprId", reqTransId);
		params.put("reqDomain", reqDomain);
		UpayCsysTxnLog txnLog = upayCsysTxnLogService.findObj(params);
		boolean returnboolean = txnLog != null;
		if (logger.isDebugEnabled()) {
			logger.debug("isRepeatTrans(String, String) - end");
		}
		return returnboolean;
	}

//	/**
//	 * @param <T>
//	 */
//	protected String findProvinceByMobileNumber(String str) {
//		if (logger.isDebugEnabled()) {
//			logger.debug("findProvinceByMobileNumber(String str) - start");
//		}
//		if ("".equals(StringUtil.toTrim(str)))
//			return null;
//		String reqProvince = ProvAreaCache.getProvAreaByPrimary(str);
//		if (logger.isDebugEnabled()) {
//			logger.debug("findProvinceByMobileNumber(String str) - end");
//		}
//		
//		if("".equals(reqProvince) || "null".equalsIgnoreCase(reqProvince)){
//			reqProvince = null;
//		}
//		
//		return reqProvince;
//	}
	
	/**
	 * @param <T>
	 */
	protected ProvincePhoneNum findProvinceByMobileNumber(String str) {
		if (logger.isDebugEnabled()) {
			logger.debug("findProvinceByMobileNumber(String str) - start");
		}
		if ("".equals(StringUtil.toTrim(str)))
			return null;
		ProvincePhoneNum reqProvince = ProvAreaCache.getProvAreaByPrimary(str);
		if (logger.isDebugEnabled()) {
			logger.debug("findProvinceByMobileNumber(String str) - end");
		}

		if (reqProvince == null || "".equals(reqProvince.getProvinceCode())
				|| "null".equalsIgnoreCase(reqProvince.getProvinceCode())) {
			reqProvince.setProvinceCode(null);
		}

		return reqProvince;
	}


//	/**
//	 * @param reqOrg
//	 * @param rcvOrg
//	 * @param innerTransCode
//	 * @return
//	 */
//	protected boolean isTransRoutePermit(String reqOrg, String rcvOrg,
//			String innerTransCode) {
//		if (StringUtils.isBlank(rcvOrg)) {
//			return false;
//		}
//		UpayCsysOrgTransCode orgTransCode = OrgTransCodeCache
//				.getOrgTransCode(reqOrg.trim() ,innerTransCode.trim());
//		if (null == orgTransCode) {
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("orgId", reqOrg);
//			params.put("transCode", innerTransCode);
//			params.put("status", CommonConstant.IsActive.True.getValue());
//			params.put("isHistory", CommonConstant.IsHistory.Normal.getValue());
//			upayCsysOrgTransCodeService.findObj(params);
//		}
//		if (null == orgTransCode
//				|| !CommonConstant.IsActive.True.getValue().equals(
//						orgTransCode.getStatus())
//				|| !CommonConstant.IsHistory.Normal.getValue().equals(
//						orgTransCode.getIsHistory())) {
//			return false;
//		}
//
//		String permitOrgs = orgTransCode.getReserved1();
//		if (StringUtils.isBlank(permitOrgs)) {
//			return false;
//		}
//		String[] permitOrgArr = permitOrgs.split(",");
//		if (permitOrgArr.length == 0) {
//			return false;
//		}
//		List<String> pos = new ArrayList<String>();
//		for (String s : permitOrgArr) {
//			pos.add(s.trim());
//		}
//		if (pos.contains(rcvOrg)) {
//			return true;
//		}
//		return false;
//	}

	protected boolean isO2OTransOn(String reqOrg, String rcvOrg,
			String transCode) {
		return isO2OTransOn(reqOrg, rcvOrg, null, transCode);
	}

	/**
	 * 校验机构交易权限
	 * 
	 * @param reqOrg
	 *            发起方
	 * @param rcvOrg
	 *            接收方
	 * @param transCode
	 *            内部交易代码
	 * @return
	 */
	protected boolean isO2OTransOn(String reqOrg, String rcvOrg,
			String thridOrg, String transCode) {
		logger.debug(
				"start check org trans,reqOrg:{},rcvOrg:{},thridOrg{},transCode:{}",
				new Object[] { reqOrg, rcvOrg, thridOrg, transCode });

		if (StringUtils.isBlank(reqOrg) || StringUtils.isBlank(rcvOrg)
				|| StringUtils.isBlank(transCode)) {
			return false;
		}

		List<String> orgList = new ArrayList<String>();
		if (!StringUtils.isBlank(reqOrg)) {
			orgList.add(reqOrg);
		}
		if (!StringUtils.isBlank(rcvOrg)) {
			orgList.add(rcvOrg);
		}
		if (!StringUtils.isBlank(thridOrg)) {
			orgList.add(thridOrg);
		}
		String offOrg = this.offOrg(orgList);
		if (null != offOrg) {
			return false;
		}

		String o2o = OrgMapTransCache.getOrgMapTransCode(reqOrg, rcvOrg);
		if (o2o == null) {
			return false;
		}
		String transListStr = o2o.trim();
		if (StringUtils.isBlank(transListStr)) {
			return false;
		}
		String transArr[] = transListStr
				.split(CommonConstant.SpeSymbol.COMMA_MARK.getValue());
		if (transArr.length == 0) {
			return false;
		}
		List<String> transListTrim = new ArrayList<String>();
		for (String s : transArr) {
			transListTrim.add(s.trim());
		}
		if (transListTrim.contains(transCode)) {
			return true;
		}
		return false;
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

		if (orgList == null ||orgList.size()==0 || orgList.isEmpty()) {
			return null;
		}
		for (UpayCsysOrgCode org : orgList) {
			if(org == null ){
				continue;
			}
			if (org.getStatus()
					.equals(CommonConstant.IsActive.False.getValue())
					|| org.getIsHistory().equals(
							CommonConstant.IsHistory.History.getValue())) {
				return org.getOrgId();
			}
		}

		return null;
	}
	
//	/**
//	 * 根据响应码获取响应类型
//	 * @param rspCode crm二级响应码
//	 * @return 响应类型
//	 */
//	protected String parseRspType(String rspCode){
//		String rspType = CommonConstant.CrmRspType.SysErr.getValue();
//		if(StringUtils.isBlank(rspCode)){
//			return CommonConstant.CrmRspType.SysErr.getValue();
//		}
//		if(rspCode.equals(RspCodeConstant.Crm.CRM_0000.getValue())){
//			return CommonConstant.CrmRspType.Success.getValue();
//		}
//		
//		rspType = rspCode.substring(0,1);
//		if(!CommonConstant.CrmRspType.errList().contains(rspType)){
//			return CommonConstant.CrmRspType.SysErr.getValue();
//		}else{
//			return rspType;
//		}
//	}
	
//	/**
//	 * 验证机构的状态及机构交易权限，校验顺序：发起方就够，外部机构，接收方机构，机构交易权限
//	 * @param reqOrg 发起方机构代码
//	 * @param rcvOrg 接收方机构代码
//	 * @param thridOrg 外部机构代码
//	 * @param transCode 内部交易代码
//	 * @return  reqOrg or rcvOrg or thridOrg,if any org check failed
//	 * 			transCode if org trans_code check failed
//	 */
//	protected String offOrgTrans(String reqOrg, String rcvOrg,
//			String thridOrg, String transCode) {
//		logger.debug(
//				"start check org trans,reqOrg:{},rcvOrg:{},thridOrg{},transCode:{}",
//				new Object[] { reqOrg, rcvOrg, thridOrg, transCode });
//		List<String> orgList = new ArrayList<String>();
//		if (!StringUtils.isBlank(reqOrg)) {
//			orgList.add(reqOrg);
//		}
//		if (!StringUtils.isBlank(rcvOrg)) {
//			orgList.add(rcvOrg);
//		}
//		if (!StringUtils.isBlank(thridOrg)) {
//			orgList.add(thridOrg);
//		}
//		String offOrg = this.offOrg(orgList);
//		if(!StringUtils.isBlank(offOrg)||"null".equalsIgnoreCase(offOrg)){
//			logger.warn("机构:{}交易:{}权限关闭",offOrg,transCode);
//			log.warn("机构:{}交易:{}权限关闭",offOrg,transCode);
//			return new StringBuffer(offOrg).append(",").append(transCode).toString();
//		}
//
//		String o2o = OrgMapTransCache.getOrgMapTransCode(reqOrg, rcvOrg);
//		if (o2o == null || "".equals(o2o) ||"null".equalsIgnoreCase(o2o)) {
//			logger.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
//			log.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
//			return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
//		}
//		String transListStr = o2o.trim();
//		if (StringUtils.isBlank(transListStr)) {
//			logger.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
//			log.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
//			return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
//		}
//		String transArr[] = transListStr
//				.split(CommonConstant.SpeSymbol.COMMA_MARK.getValue());
//		if (transArr.length == 0) {
//			logger.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
//			log.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
//			return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
//		}
//		List<String> transListTrim = new ArrayList<String>();
//		for (String s : transArr) {
//			transListTrim.add(s.trim());
//		}
//		if (!transListTrim.contains(transCode)) {
//			logger.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
//			log.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
//			return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
//		}
//		return null;
//	}
	
	/**
	 * 验证机构的状态及机构交易权限，校验顺序：发起方机构，外部机构，接收方机构，机构交易权限
	 * 
	 * @param reqOrg
	 *            发起方机构代码
	 * @param rcvOrg
	 *            接收方机构代码
	 * @param thridOrg
	 *            外部机构代码
	 * @param transCode
	 *            内部交易代码
	 * @param type
	 * 			 手机号码类型
	 * @return reqOrg or rcvOrg or thridOrg,if any org check failed transCode if
	 *         org trans_code check failed
	 */
	protected String offOrgTrans(String reqOrg, String rcvOrg, String thridOrg,
			String transCode, int type) {
		logger.debug(
				"start check org trans,reqOrg:{},rcvOrg:{},thridOrg{},transCode:{}",
				new Object[] { reqOrg, rcvOrg, thridOrg, transCode });
		
		// 手机号码类型既不是移动也不是联通电信
		if(type == CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType()) {
			logger.error("机构:{},{}交易:{}该手机号码不存在", new Object[] { reqOrg, rcvOrg,
					transCode });
			log.error("机构:{},{}交易:{}该手机号码不存在", new Object[] { reqOrg, rcvOrg,
					transCode });
			return new StringBuffer(reqOrg).append(",").append(rcvOrg)
					.append(",").append(transCode).toString();
		}
		
		List<String> orgList = new ArrayList<String>();
		if (!StringUtils.isBlank(reqOrg)) {
			orgList.add(reqOrg);
		}
		if (!StringUtils.isBlank(rcvOrg)) {
			orgList.add(rcvOrg);
		}
		if (!StringUtils.isBlank(thridOrg)) {
			orgList.add(thridOrg);
		}
		String offOrg = this.offOrg(orgList);
		if (!StringUtils.isBlank(offOrg) || "null".equalsIgnoreCase(offOrg)) {
			logger.warn("机构:{}交易:{}权限关闭", offOrg, transCode);
			log.warn("机构:{}交易:{}权限关闭", offOrg, transCode);
			return new StringBuffer(offOrg).append(",").append(transCode)
					.toString();
		}

		String o2o = null;
		if (type == CommonConstant.PhoneNumType.UNICOM_TELECOM
						.getType()) {
			// 作为联通电信号码检测
			o2o = OrgMapTransCache.getOrgMapTransCodeForUnicomTelecom(reqOrg,
					rcvOrg);
			if (o2o == null || "".equals(o2o) || "null".equalsIgnoreCase(o2o)) {
				logger.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				log.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				return new StringBuffer(reqOrg).append(",").append(rcvOrg)
						.append(",").append(transCode).toString();
			}

			String transListStr = o2o.trim();
			if (StringUtils.isBlank(transListStr)) {
				logger.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				log.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				return new StringBuffer(reqOrg).append(",").append(rcvOrg)
						.append(",").append(transCode).toString();
			}
			String transArr[] = transListStr
					.split(CommonConstant.SpeSymbol.COMMA_MARK.getValue());
			if (transArr.length == 0) {
				logger.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				log.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				return new StringBuffer(reqOrg).append(",").append(rcvOrg)
						.append(",").append(transCode).toString();
			}
			List<String> transListTrim = new ArrayList<String>();
			for (String s : transArr) {
				transListTrim.add(s.trim());
			}
			if (!transListTrim.contains(transCode)) {
				logger.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				log.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				return new StringBuffer(reqOrg).append(",").append(rcvOrg)
						.append(",").append(transCode).toString();
			}
			
		} else  {
			// 作为移动号码检测
			o2o = OrgMapTransCache.getOrgMapTransCode(reqOrg, rcvOrg);
			if (o2o == null || "".equals(o2o) || "null".equalsIgnoreCase(o2o)) {
				logger.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				log.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				return new StringBuffer(reqOrg).append(",").append(rcvOrg)
						.append(",").append(transCode).toString();
			}

			String transListStr = o2o.trim();
			if (StringUtils.isBlank(transListStr)) {
				logger.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				log.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				return new StringBuffer(reqOrg).append(",").append(rcvOrg)
						.append(",").append(transCode).toString();
			}
			String transArr[] = transListStr
					.split(CommonConstant.SpeSymbol.COMMA_MARK.getValue());
			if (transArr.length == 0) {
				logger.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				log.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				return new StringBuffer(reqOrg).append(",").append(rcvOrg)
						.append(",").append(transCode).toString();
			}
			List<String> transListTrim = new ArrayList<String>();
			for (String s : transArr) {
				transListTrim.add(s.trim());
			}
			if (!transListTrim.contains(transCode)) {
				logger.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				log.warn("机构:{},{}交易:{}权限关闭", new Object[] { reqOrg, rcvOrg,
						transCode });
				return new StringBuffer(reqOrg).append(",").append(rcvOrg)
						.append(",").append(transCode).toString();
			}
		}

		return null;
	}
	
	/**
	 * 验证机构的状态及机构交易权限，校验顺序：发起方就够，接收方机构，机构交易权限
	 * @param reqOrg 发起方机构代码
	 * @param rcvOrg 接收方机构代码
	 * @param transCode 内部交易代码
	 * @param type 手机号码类型
	 * @return  reqOrg or rcvOrg,if any org check failed
	 * 			transCode if org trans_code check failed
	 */
	protected String offOrgTrans(String reqOrg, String rcvOrg, String transCode, int type) {
//		return offOrgTrans(reqOrg,null,rcvOrg,transCode);
//		return offOrgTrans(reqOrg,rcvOrg,null,transCode);
		return offOrgTrans(reqOrg, rcvOrg, null, transCode, type);
	}
}
