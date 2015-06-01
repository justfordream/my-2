package com.huateng.cmupay.models.common.multidatasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author cmt
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContextHolder.getDataSourceType();
	}

}