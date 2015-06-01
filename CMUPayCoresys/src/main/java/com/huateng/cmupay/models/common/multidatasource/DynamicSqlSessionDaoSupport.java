package com.huateng.cmupay.models.common.multidatasource;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

/** 
 * @author cmt  
 * @version 创建时间：2013-4-10 下午8:09:24 
 * 类说明 
 */

/**
 * 动态切换SqlSessionFactory的SqlSessionDaoSupport
 * 
 * @see org.mybatis.spring.support.SqlSessionDaoSupport
 */
public class DynamicSqlSessionDaoSupport extends DaoSupport {

	private Map<Object, SqlSessionFactory> targetSqlSessionFactorys;

	private SqlSessionFactory defaultTargetSqlSessionFactory;

	private SqlSession sqlSession;

	private boolean externalSqlSession;

	@Autowired(required=false)
	public final void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		if (!this.externalSqlSession) {
			this.sqlSession = new SqlSessionTemplate(sqlSessionFactory);
		}
	}

	@Autowired(required=false)
	public final void setSqlSessionTemplate(
			SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSession = sqlSessionTemplate;
		this.externalSqlSession = true;
	}

	/**
	 * Users should use this method to get a SqlSession to call its statement
	 * methods This is SqlSession is managed by spring. Users should not
	 * commit/rollback/close it because it will be automatically done.
	 * 
	 * @return Spring managed thread safe SqlSession
	 */
	public final SqlSession getSqlSession() {
		SqlSessionFactory targetSqlSessionFactory = targetSqlSessionFactorys
				.get(ContextHolder.getContext());
		if (targetSqlSessionFactory != null) {
			setSqlSessionFactory(targetSqlSessionFactory);
		} else if (defaultTargetSqlSessionFactory != null) {
			setSqlSessionFactory(defaultTargetSqlSessionFactory);
		}
		return this.sqlSession;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void checkDaoConfig() {
		Assert.notNull(this.sqlSession,
				"Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required");
	}

	public Map<Object, SqlSessionFactory> getTargetSqlSessionFactorys() {
		return targetSqlSessionFactorys;
	}

	/**
	 * Specify the map of target SqlSessionFactory, with the lookup key as key.
	 * 
	 * @param targetSqlSessionFactorys
	 */
	public void setTargetSqlSessionFactorys(
			Map<Object, SqlSessionFactory> targetSqlSessionFactorys) {
		this.targetSqlSessionFactorys = targetSqlSessionFactorys;
	}

	public SqlSessionFactory getDefaultTargetSqlSessionFactory() {
		return defaultTargetSqlSessionFactory;
	}

	/**
	 * Specify the default target SqlSessionFactory, if any.
	 * 
	 * @param defaultTargetSqlSessionFactory
	 */
	public void setDefaultTargetSqlSessionFactory(
			SqlSessionFactory defaultTargetSqlSessionFactory) {
		this.defaultTargetSqlSessionFactory = defaultTargetSqlSessionFactory;
	}

}
