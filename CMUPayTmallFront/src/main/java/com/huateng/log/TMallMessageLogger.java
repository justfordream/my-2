package com.huateng.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.common.CommonConstant;
import com.huateng.core.util.StrUtil;
import com.huateng.tmall.bean.crm.CrmChargeReqVo;
import com.huateng.tmall.bean.crm.CrmMsgVo;

/**
 * 系统追踪日志
 * 
 * @author qingxue.li
 * 
 */
public class TMallMessageLogger {
	private final Logger logger = LoggerFactory.getLogger(getClass());;

	public static TMallMessageLogger getLogger() {
		return new TMallMessageLogger();
	}

	public static TMallMessageLogger getLogger(final Class<?> clazz) {
		return new TMallMessageLogger(clazz.getName());
	}

	public static TMallMessageLogger getLogger(final String clazzname) {
		return new TMallMessageLogger(clazzname);
	}

	private TMallMessageLogger(final String clazzname) {
		this.setClazzname(clazzname);
		//logger = LoggerFactory.getLogger(getClass());
	}

	private TMallMessageLogger() {
		this.setClazzname("TMallMessageLogger");
		//logger = LoggerFactory.getLogger(getClass());
	}
	
	/*
	 *天猫应急方案
	 *追踪日志 <请求>
	 */

	public void recordMessage(CrmMsgVo crmMsgVo,CrmChargeReqVo  crmChargeReqVo){
		StringBuilder content = new StringBuilder();
		
		if(crmMsgVo != null){
			//流水号？？
//			content.append("seq_id:");
//			content.append("|");
			//内部交易流水号	
			content.append("int_txn_seq:"+crmMsgVo.getTransIDO());
			content.append("|");
			//内部交易代码
			content.append("int_trans_code:"+crmMsgVo.getActivityCode());
			content.append("|");
			//内部交易日期
			content.append("int_txn_date:"+StrUtil.subString(crmMsgVo.getTransIDHTime(),0,8));
			content.append("|");
			//内部交易时间
			content.append("int_txn_time:"+crmMsgVo.getTransIDHTime());
			content.append("|");
			//对账日期
			content.append("settle_date:");
			content.append("|");
			//发起方交易方式
			content.append("pay_mode:");
			content.append("|");
			//业务大类:缴费类交易
			content.append("buss_type:"+CommonConstant.BussType.OnlineChargeBus);
			content.append("|");
			//业务渠道##要加一个业务渠道吗
			content.append("buss_chl:"+CommonConstant.BussChl.tmall);
			content.append("|");			
			//天猫交易代码
			content.append("tmall_activity_code:"+crmMsgVo.getActivityCode());
			content.append("|");
			//天猫方机构代码
			content.append("tmall_org_id:"+crmChargeReqVo.getBankID());
			content.append("|");			
			//天猫方路由信息
			content.append("tmall_route_info:");
			content.append("|");	
			//天猫请求流水号
			content.append("tmall_trans_id:"+crmMsgVo.getTransIDO());
			content.append("|");			
			//天猫请求的日期
			content.append("tmall_trans_dt:"+StrUtil.subString(crmMsgVo.getTransIDHTime(),0,8));
			content.append("|");	
			//天猫请求的时间
			content.append("tmall_trans_tm:"+crmMsgVo.getTransIDHTime());
			content.append("|");
			//天猫落地流水号
			content.append("tmall_transh_id:");
			content.append("|");			
			//天猫落地的日期
			content.append("tmall_transh_dt:");
			content.append("|");			
			//天猫落地的时间
			content.append("tmall_transh_tm:");
			content.append("|");			
		    //天猫渠道标识
			content.append("tmall_cnl_type:");
			content.append("|");
            //省业务代码
			content.append("crm_bip_code:"+crmMsgVo.getBIPCode());
			content.append("|");			
			//省交易代码	
			content.append("crm_activity_code:"+crmMsgVo.getActivityCode());
			content.append("|");			
            //省机构代码	
			content.append("crm_org_id:");
			content.append("|");
		    //省路由类型	crm_route_type	
			content.append("crm_route_type:");
			content.append("|");
            //省路由信息	crm_route_val	
			content.append("crm_route_val:");
			content.append("|");
		    //省路由表路由编号	crm_route_info	
			content.append("crm_route_info:");
			content.append("|");
  		    //省业务流水号	crm_session_id	
			content.append("crm_session_id:"+crmMsgVo.getSessionID());
			content.append("|");
   		    //省请求交易流水号	crm_trans_id	
			content.append("crm_trans_id:"+crmMsgVo.getTransIDO());
			content.append("|");
   		    //省请求交易日期	crm_trans_dt	
			content.append("crm_trans_dt:");
			content.append("|");
   		    //省请求交易时间	crm_trans_tm	
			content.append("crm_trans_tm:");
			content.append("|");
   		    //省落地交易流水号	crm_transh_id	
			content.append("crm_transh_id:");
			content.append("|");
   		    //省落地交易日期	crm_transh_dt	
			content.append("crm_transh_dt:");
			content.append("|");
   		    //省落地交易时间	crm_transh_tm	
			content.append("crm_transh_tm:");
			content.append("|");
   		    //省操作流水号	crm_opr_id	
			content.append("crm_opr_id:");
			content.append("|");
   		    //省操作请求日期	crm_opr_dt	
			content.append("crm_opr_dt:");
			content.append("|");
   		    //省操作请求时间	crm_opr_tm	
			content.append("crm_opr_tm:");
			content.append("|");
   		    //省渠道标识	crm_cnl_type	
			content.append("crm_cnl_type:");
			content.append("|");
   		    //省交易开始时间	crm_start_tm	
			content.append("crm_start_tm:");
			content.append("|");
   		    //省交易结束时间	crm_end_tm	
			content.append("crm_end_tm:");
			content.append("|");
//   		    //sn交易流水号	sn_trans_idc	
//			content.append("sn_trans_idc:");
//			content.append("|");
//   		    //sn处理标识	sn_conv_id	
//			content.append("sn_conv_id:");
//			content.append("|");
//   		    //sn日切点	sn_cut_off_day	
//			content.append("sn_cut_off_day:");
//			content.append("|");
//   		    //sn处理时间	sn_osn_tm	
//			content.append("sn_osn_tm:");
//			content.append("|");
//   		    //sn发起方交换节点代码	sn_osnduns	
//			content.append("sn_osnduns:");
//			content.append("|");
//   		    //sn接收方交换节点代码	sn_hsnduns	
//			content.append("sn_hsnduns:");
//			content.append("|");
//   		    //sn发起方机构编码	sn_orgi_org_id	
//			content.append("sn_orgi_org_id:");
//			content.append("|");
//   		    //sn接受方机构编码	sn_home_org_id	
//			content.append("sn_home_org_id:");
//			content.append("|");
   		    //用户号码归属地	id_province	
			content.append("id_province:");
			content.append("|");
   		    //用户号码标识类型	id_type	
			content.append("id_type:"+crmChargeReqVo.getIDType());
			content.append("|");
   		    //用户号码	id_value	
			content.append("id_value:"+crmChargeReqVo.getIDValue());
			content.append("|");
   		    //用户类型	user_cat	
			content.append("user_cat:");
			content.append("|");
   		    //应缴费金额	need_pay_amt	
			content.append("need_pay_amt:");
			content.append("|");
   		    //实际缴费金额	pay_amt	
			content.append("pay_amt:");
			content.append("|");
   		    //缴费类型	payed_type	
			content.append("payed_type:"+crmChargeReqVo.getPayedType());
			content.append("|");
   		    //天猫应答代码	tmall_rsp_code	
			content.append("tmall_rsp_code:");
			content.append("|");
			//天猫应答描述
			content.append("tmall_rsp_desc:");
			content.append("|");
			//省应答类型
			content.append("crm_rsp_type:");
			content.append("|");
			//省应答代码
			content.append("crm_rsp_code:");
			content.append("|");
			//省二级应答码
			content.append("crm_sub_rsp_code:");
			content.append("|");
			//省应答描述
			content.append("crm_rsp_desc:");
			content.append("|");
			//省二级应答描述
			content.append("crm_sub_rsp_desc:");
			content.append("|");
			//操作系统标识（原系统标示）
			content.append("ori_org_id:");
			content.append("|");
			//操作流水号（原交易流水号）
			content.append("ori_opr_trans_id:");
			content.append("|");
			//操作请求日期（原交易请求日期）
			content.append("ori_trans_date:");
			content.append("|");
			//返销标示
			content.append("back_flag:");
			content.append("|");
			//退款标示
			content.append("refund_flag:");
			content.append("|");
			//冲正标示
			content.append("reverse_flag:");
			content.append("|");
			//是否已经对账
			content.append("reconciliation_flag:");
			content.append("|");
			//状态
			content.append("status:");
			content.append("|");
			//最后修改操作员
			content.append("last_upd_oprid:");
			content.append("|");
			//最后修改时间
			content.append("last_upd_time:");
			content.append("|");
			//原流水号
			content.append("l_seq_id:");
			content.append("|");
			//保留域1
			content.append("reserved1:");
			content.append("|");
			//保留域2
			content.append("reserved2:");
			content.append("|");
			//保留域3
			content.append("reserved3:");
			content.append("|");

		}		
		logger.info(content.toString());
	}
	
	
		
	public void recordMessage(String xml){
		logger.info(xml);
	}
	
	private String clazzname = "";

	public String getClazzname() {
		return clazzname;
	}

	public void setClazzname(String clazzname) {
		this.clazzname = clazzname;
	}

}
