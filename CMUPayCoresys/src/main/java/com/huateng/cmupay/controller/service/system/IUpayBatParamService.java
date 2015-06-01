/**
 * 
 */
package com.huateng.cmupay.controller.service.system;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.common.Order;
import com.huateng.cmupay.models.his.UpayBatParam;

/**
 * 
 * @author hdm
 *
 */
public interface IUpayBatParamService {

	public UpayBatParam findObj(Map<String, Object> params)  ;

	public List<UpayBatParam> findList(Map<String, Object> params, Order order);

}
