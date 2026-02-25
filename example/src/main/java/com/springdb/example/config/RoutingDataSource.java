package com.springdb.example.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        Object key = DataSourceContextHolder.getBranchContext();
        return (key != null) ? key : DataSourceType.PRIMARY;
    }
}