package com.c3po.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Repository {
    final private DataSource dataSource;

    public Repository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void query(String query) throws SQLException {
        Connection conn = this.dataSource.getConnection();
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String a = "";
        }

        rs.close();
        statement.close();
        conn.close();
    }
}
