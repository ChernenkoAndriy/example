package com.springdb.example.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType context = DataSourceContextHolder.getBranchContext();
        if (context == DataSourceType.REPLICA) {
            try (Connection ignored = getResolvedDataSources().get(DataSourceType.REPLICA).getConnection()) {
                return DataSourceType.REPLICA;
            } catch (SQLException e) {
                return DataSourceType.PRIMARY;
            }
        }
        return context;
    }
}