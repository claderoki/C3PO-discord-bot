package com.c3po.connection;

import com.c3po.database.Parameter;
import com.c3po.database.Result;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Repository {
    final private DataSource dataSource;

    public Repository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String[] getColumnLabels(ResultSet set) throws SQLException {
        ResultSetMetaData metadata = set.getMetaData();
        String[] columnLabels = new String[metadata.getColumnCount()];
        for (int i = 0; i < metadata.getColumnCount(); i++) {
            columnLabels[i] = metadata.getColumnLabel(i+1);
        }
        return columnLabels;
    }

    private PreparedStatement preparedStatement(Connection connection, String query, Parameter... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        int index = 1;
        for (Parameter parameter: params) {
            parameter.bind(statement, index++);
        }
        return statement;
    }

    protected void update(Connection connection, String query, Parameter... params) throws SQLException {
        try (PreparedStatement statement = preparedStatement(connection, query, params)) {
            statement.executeUpdate();
        }
    }

    protected void update(String query, Parameter... params) throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {
            try (PreparedStatement statement = preparedStatement(connection, query, params)) {
                statement.executeUpdate();
            }
        }
    }

    protected List<Result> query(Connection connection, String query, Parameter... params) throws SQLException {
        ArrayList<Result> results = new ArrayList<>();
        try (PreparedStatement statement = preparedStatement(connection, query, params)) {
            try (ResultSet rs = statement.executeQuery()) {
                String[] columnLabels = null;
                while (rs.next()) {
                    if (columnLabels == null) {
                        columnLabels = getColumnLabels(rs);
                    }
                    results.add(new Result(rs, columnLabels));
                }
            }
        }
        return results;
    }

    protected List<Result> query(String query, Parameter... params) throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {
            return query(connection, query, params);
        }
    }

    protected List<Result> query(String query, Collection<Parameter> params) throws SQLException {
        return query(query, params.toArray(new Parameter[0]));
    }

    protected Result getOne(String query, Parameter... params) throws SQLException {
        List<Result> results = query(query, params);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

}
