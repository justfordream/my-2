/**
 * 
 */
package com.huateng.cmupay.controller.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


/**
 * 基础类MAPPER
 * @author cmt
 *
 */
public interface IBaseMapper<T> {
	
	/**
	 * 物理删除记录
	 * @author cmt
	 * @param seq
	 * @return
	 */
	int deleteByPrimaryKey(Long seq);
	
	//int deleteByPrimaryKey(@Param(value="params") Map<String,Object> params);

	/**
	 * 插入记录
	 * @author cmt
	 * @param obj
	 * @return
	 */
    int insert(T obj);

    /**
     * 插入记录(有效字段,即非空字段)
     * @author cmt
     * @param obj
     * @return
     */
    int insertSelective(T obj);

    /**
     * 根据主键 返回记录
     * @author cmt
     * @param seq
     * @return
     */
    T selectByPrimaryKey(Long seq);
    
   // T selectByPrimaryKey(@Param(value="params") Map<String,Object> params);

    /**
     * 更新记录(有效字段,即非空字段)
     * @author cmt
     * @param obj
     * @return
     */
    int updateByPrimaryKeySelective(T obj);

    /**
     * 更新记录
     * @author cmt
     * @param obj
     * @return
     */
    int updateByPrimaryKey(T obj);
    
    
    /**
     * 获取 序列号 
     * @author cmt
     * @return
     */
    Long getIdValue();
    
    /**
     * 根据 条件返回记录
     * @author cmt
     * @param params
     * @return
     */
    T selectByParams(@Param(value="params") Map<String,Object> params);
    
    /**
     * 根据 条件返回记录
     * @author 
     * @param params
     * @return
     */
    T selectByParamsOld(@Param(value="params") Map<String,Object> params);
    
    /**
     * 查询 符合条件的记录总数
     * @author cmt
     * @param params
     * @return
     */
    int selectCountByParams(@Param(value="params") Map<String,Object> params);
    
    
    /**
     * 分页查询 记录
     * @author cmt
     * @param params 查询条件
     * @param startIndex 开始游标
     * @param endIndex 结束游标
     * @param orderParam 排序参数
     * @return
     */
    List<T> selectListByParams(@Param(value="params") Map<String,Object> params,
    						   @Param(value="startIndex")Integer startIndex,
    						   @Param(value="endIndex")Integer endIndex,
    						   @Param(value="orderParam") String orderParam );
    
    
    /**
     * 查询 返回所有符合条件的列表
     * @author cmt
     * @param params
     * @param orderParam
     * @return
     */
    List<T> selectAllListByParams(@Param(value="params") Map<String,Object> params,
			   @Param(value="orderParam") String orderParam );
    
    
    
   
}
