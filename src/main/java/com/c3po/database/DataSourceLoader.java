package com.c3po.database;

import com.c3po.helper.environment.Configuration;
import com.c3po.helper.environment.DatabaseCredentials;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceLoader {

    private static DataSource load() {
        DatabaseCredentials dbCredentials = Configuration.instance().getDatabaseCredentials();
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://"+ dbCredentials.getHost() +":"+dbCredentials.getPort()+"/" + dbCredentials.getDatabase());
        ds.setUsername(dbCredentials.getUsername());
        ds.setPassword(dbCredentials.getPassword());
        return ds;
    }

    private static DataSource INSTANCE;
    public static DataSource instance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

}
