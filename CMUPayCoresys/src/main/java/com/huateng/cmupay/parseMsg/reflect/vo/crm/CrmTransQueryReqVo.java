package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/**
 * crm 交易结果查询请求报文体
 * 
 * @author zeng.j
 * 
 */
public class CrmTransQueryReqVo {
	@CustomAnnotation(path = "SvcCont.PayStateReq.OriReqSys")
//	@NotNull(message = "OriReqSys不能为空")
	@Pattern(regexp = "[0-9]{4}", message = "OriReqSys格式不正确")
	private String oriReqSys;
	@CustomAnnotation(path = "SvcCont.PayStateReq.OriActionDate")
	@NotNull(message = "OriActionDate不能为空")
	@Pattern(regexp = "[0-9]{8}", message = "OriActionDate格式不正确")
	private String oriActionDate;
	@CustomAnnotation(path = "SvcCont.PayStateReq.OriTransactionID")
	@NotNull(message = "OriTransactionID不能为空")
	@Pattern(regexp="10(471|100|220|531|311|351|551|210|250|571|591|898|200|771|971|270|731|791|371|891|280|230|290|851|871|931|951|991|431|240|451|999|997)(([0-9]{4}((0?[13578])|10|12)((0?[1-9])|[12][0-9]|(3[0-1]))|[0-9]{4}(02((0?[1-9])|[12][0-9]))|[0-9]{4}((0?[469])|11)((0?[1-9])|[12][0-9]|(30)))((0?[0-9]|1[0-9]|2[0-3])[0-5][0-9][0-5][0-9])([0-9]{3}))([0-9]{10})",message="OriTransactionID格式不正确")
	private String oriTransactionID;
	@CustomAnnotation(path = "SvcCont.PayStateReq.OriActivityCode")
	@Pattern(regexp = "[a-z,A-Z,0-9]{1,8}", message = "OriActivityCode参数不正确")
	private String oriActivityCode;
	public String getOriReqSys() {
		return oriReqSys;
	}
	public void setOriReqSys(String oriReqSys) {
		this.oriReqSys = oriReqSys;
	}
	public String getOriActionDate() {
		return oriActionDate;
	}
	public void setOriActionDate(String oriActionDate) {
		this.oriActionDate = oriActionDate;
	}
	public String getOriTransactionID() {
		return oriTransactionID;
	}
	public void setOriTransactionID(String oriTransactionID) {
		this.oriTransactionID = oriTransactionID;
	}
	public String getOriActivityCode() {
		return oriActivityCode;
	}
	public void setOriActivityCode(String oriActivityCode) {
		this.oriActivityCode = oriActivityCode;
	}


}
