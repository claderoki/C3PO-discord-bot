package com.c3po.database;

import com.c3po.helpers.environment.ConfigurationLoader;
import com.c3po.helpers.environment.DatabaseCredentials;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceLoader {

    private static DataSource load() {
        DatabaseCredentials dbCredentials = ConfigurationLoader.instance().getDatabaseCredentials();
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
